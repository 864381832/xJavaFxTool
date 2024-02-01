package com.xwintop.xJavaFxTool.utils;

import cn.hutool.json.JSONUtil;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;

/**
 * @ClassName: NcmDump
 * @Description: 网易云音乐转换工具
 * @author: xufeng
 * @date: 2019/8/10 0010 0:06
 */

public class NcmDump {
    private static final byte[] CORE_KEY = {0x68, 0x7A, 0x48, 0x52, 0x41, 0x6D, 0x73, 0x6F, 0x35, 0x6B, 0x49, 0x6E, 0x62, 0x61, 0x78, 0x57};
    private static final byte[] MODIFY_KEY = {0x23, 0x31, 0x34, 0x6C, 0x6A, 0x6B, 0x5F, 0x21, 0x5C, 0x5D, 0x26, 0x30, 0x55, 0x3C, 0x27, 0x28};

    /**
     * dump ncm file to mp3
     *
     * @param file    input file (*.ncm)
     * @param outPath output path (folder/directory)
     * @return true = success or false = fail
     */
    public static boolean dump(File file, File outPath) {
        NcmFile ncm = new NcmFile(file, outPath);
        if (dumpData(ncm)) {
            fixID3(ncm);
            return true;
        }
        return false;
    }

    public static void fixID3(NcmFile ncm) {
        try {
            AudioFile f = AudioFileIO.read(ncm.outFile());
            Tag tag = f.getTag();
            tag.setField(FieldKey.ALBUM, ncm.id3.album);
            tag.setField(FieldKey.TITLE, ncm.id3.musicName);
            tag.setField(FieldKey.ARTIST, ncm.id3.artistString());
            if (ncm.albumImage != null) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(ncm.albumImage));
                MetadataBlockDataPicture coverArt = new MetadataBlockDataPicture(ncm.albumImage, 0, //
                        ncm.albumImageMimeType(), //
                        "", //
                        image.getWidth(), //
                        image.getHeight(), //
                        image.getColorModel().hasAlpha() ? 32 : 24, //
                        0);
                Artwork artwork = ArtworkFactory.createArtworkFromMetadataBlockDataPicture(coverArt);
                tag.setField(tag.createField(artwork));
            }
            AudioFileIO.write(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean dumpData(NcmFile ncm) {
        FileInputStream fncm = null;
        FileOutputStream fout = null;
        try {
            fncm = new FileInputStream(ncm.fncm);
            byte[] b = new byte[1024];
            fncm.read(b, 0, 8);
            if (!"CTENFDAM".equals(new String(b, 0, 8))) {
                return false;
            }
            fncm.skip(2);
            //
            fncm.read(b, 0, 4);
            int len = b2i(b);
            if (len <= 0) {
                return false;// broken file
            }
            byte[] keyData = new byte[len];
            fncm.read(keyData);
            for (int i = 0; i < keyData.length; i++) {
                keyData[i] ^= 0x64;
            }
            // ID3 -------------------------------------------
            fncm.read(b, 0, 4);
            ID3Data id3 = readID3(fncm, b2i(b));
            ncm.setID3(id3);
            // skip crc32(4b) & unused chars(5b) ------------------
            fncm.skip(9);
            // albumImage -------------------------------------------
            fncm.read(b, 0, 4);
            int imgSize = b2i(b);
            if (imgSize > 0) {
                byte[] img_data = new byte[imgSize];
                fncm.read(img_data);
                ncm.setAlbumImage(img_data);
            }
            // mp3 data -------------------------------------------
            byte[] rawKeyData = aes128EcbDecrypt(keyData, CORE_KEY);
            int[] box = buildKeyBox(rawKeyData);
//			print(box);
            byte[] buffer = new byte[0x4000];
            File ftmp = ncm.tmpFile();
            fout = new FileOutputStream(ftmp);
            boolean first = true;
            while (true) {
                int n = fncm.read(buffer);
                if (n < 0) {
                    break;
                }
                for (int i = 0; i < n; i++) {
                    int j = (i + 1) & 0xff;
                    int k = (box[j] + j) & 0xff;
                    k = (box[j] + box[k]) & 0xff;
                    byte key = (byte) (box[k] & 0xff);
                    buffer[i] ^= key;
                }
                if (first) {
                    ncm.setFormat(getFormat(ncm, buffer));
                    first = false;
                }
                fout.write(buffer, 0, n);
            }
            fout.flush();
            fout.close();
            fout = null;
            fncm.close();
            fncm = null;
            return ftmp.renameTo(ncm.outFile());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fncm != null) {
                try {
                    fncm.close();
                } catch (Exception e2) {
                }
            }
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e2) {
                }
            }
        }
    }

    private static ID3Data readID3(FileInputStream fncm, int n) {
        if (n > 0) {
            try {
                byte[] modifyData = new byte[n];
                fncm.read(modifyData);
                for (int i = 0; i < modifyData.length; i++) {
                    modifyData[i] ^= 0x63;
                }
                // offset header
                byte[] tmp = new byte[modifyData.length - 22];
                System.arraycopy(modifyData, 22, tmp, 0, tmp.length);
                // escape `163 key(Don't modify):`
                byte[] data = Base64.getDecoder().decode(tmp);
                byte[] dedata = aes128EcbDecrypt(data, MODIFY_KEY);
                // escape `music:`
                String json = new String(dedata, 6, dedata.length - 6).trim();
                return JSONUtil.toBean(json, ID3Data.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getFormat(NcmFile ncm, byte[] b) {
        if (b[0] == 0x49 && b[1] == 0x44 && b[2] == 0x33) {
            return "mp3";
        }
        return "flac";
    }

    private static int b2i(byte[] b) {
        int i = 0;
        i |= b[0] & 0xff;
        i |= (b[1] & 0xff) << 8;
        i |= (b[2] & 0xff) << 16;
        i |= (b[3] & 0xff) << 24;
        return i;
    }

    private static byte[] aes128EcbDecrypt(byte[] src, byte[] key) throws Exception {
        int l = src.length;
        int x = l % 16;
        byte[] content = src;
        if (x != 0) {
            content = new byte[l + 16 - x];
            System.arraycopy(src, 0, content, 0, l);
        }
        SecretKeySpec sks = new SecretKeySpec(key, "AES");// 转换为AES专用密钥
        Cipher cipher = Cipher.getInstance("AES_128/ECB/NoPadding");// 实例化
        cipher.init(Cipher.DECRYPT_MODE, sks);// 使用密钥初始化，设置为解密模式
        return cipher.doFinal(content);// 执行操作
    }

    private static int[] buildKeyBox(byte[] key) {
        int key_len = key.length - 17 - key[key.length - 1];
        byte[] tmp = new byte[key.length - 17];
        System.arraycopy(key, 17, tmp, 0, tmp.length);
        key = tmp;
        int[] box = new int[256];
        for (int i = 0; i < 256; ++i) {
            box[i] = (byte) i;
        }

        int last_byte = 0;
        int key_offset = 0;

        for (int i = 0; i < 256; ++i) {
            int swap = box[i];
            int c = (swap + last_byte + key[key_offset++]) & 0xff;
            if (key_offset >= key_len) {
                key_offset = 0;
            }
            box[i] = box[c];
            box[c] = swap;
            last_byte = c;
        }
        return box;
    }
}

class ID3Data {
    public String album;
    public String musicName;
    public String[][] artist;
    public String format;

    public boolean isMP3() {
        return "mp3".equals(format);
    }

    public String artistString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < artist.length; i++) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(artist[i][0]);
        }
        return sb.toString();
    }
}

class NcmFile {
    public final File fncm;
    private final File outPath;
    private final String fileNameWithoutExt;
    private String format;
    public ID3Data id3;
    public byte[] albumImage;

    public NcmFile(File fncm, File outPath) {
        this.fncm = fncm;
        this.outPath = outPath;
        String fname = fncm.getName();
        int i = fname.lastIndexOf('.');
        fileNameWithoutExt = fname.substring(0, i);
    }

    public void setID3(ID3Data id3) {
        this.id3 = id3;
    }

    public void setAlbumImage(byte[] albumImage) {
        this.albumImage = albumImage;
    }

    public String albumImageMimeType() {
        final byte[] mPNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};// PNG file header
        int l = mPNG.length;
        if (albumImage.length > l) {
            for (int i = 0; i < l; i++) {
                if (albumImage[i] != mPNG[i]) {
                    return "image/jpg";
                }
            }
        }
        return "image/png";
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public File tmpFile() {
        return new File(outPath, fileNameWithoutExt + ".tmp");
    }

    public File outFile() {
        if (id3 != null && id3.format != null) {
            return new File(outPath, fileNameWithoutExt + "." + id3.format);
        }
        return new File(outPath, fileNameWithoutExt + "." + format);
    }

}

