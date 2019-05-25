package com.xwintop.xJavaFxTool.services.assistTools;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;
import com.xwintop.xJavaFxTool.controller.assistTools.TextToSpeechToolController;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import com.xwintop.xcore.commons.CacheUtil;
import com.xwintop.xcore.util.javafx.FileChooserUtil;
import com.xwintop.xcore.util.javafx.TooltipUtil;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * @ClassName: TextToSpeechToolService
 * @Description: 语音转换工具
 * @author: xufeng
 * @date: 2018/2/6 15:53
 */

@Getter
@Setter
@Slf4j
public class TextToSpeechToolService {
    private TextToSpeechToolController textToSpeechToolController;
    //设置APPID/AK/SK
    public static final String APP_ID = "10690878";
    public static final String API_KEY = "y5VI7DG4YNb1G600X8FpxsN2";
    public static final String SECRET_KEY = "4RoIZEBGePYU4R4capF4OEYZ3HL3nCSU";
    private AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
    private MediaPlayer mediaPlayer = null;

    private String mp3Cache = ConfigureUtil.getConfigurePath("output.mp3");

    public void playAction() throws Exception {
        TtsResponse res = getTtsResponse();
        byte[] data = res.getData(); //生成的音频数据
        if (data != null) {
            Util.writeBytesToFileSystem(data, mp3Cache);
//            audioStream = new AudioStream(new ByteArrayInputStream(data));
//            AudioPlayer.player.start(audioStream);//用静态成员player.start播放音乐
            //AudioPlayer.player.stop(as);//关闭音乐播放
            Media media = new Media(new File(mp3Cache).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnEndOfMedia(()->{
                textToSpeechToolController.getPlayButton().setText("播放");
            });
            mediaPlayer.play();
        }
        JSONObject res1 = res.getResult();
        if (res1 != null) {
            log.info(res1.toString(2));
        }
    }

    public void stopPlayAction() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void saveAudioAction() throws Exception {
        TtsResponse res = getTtsResponse();
        byte[] data = res.getData(); //生成的音频数据
        if (data != null) {
            File saveFile = FileChooserUtil.chooseSaveFile("output.mp3", new FileChooser.ExtensionFilter("mp3", "*.mp3"));
            if (saveFile == null) {
                TooltipUtil.showToast("未选择文件！！！");
                return;
            }
            Util.writeBytesToFileSystem(data, saveFile.getAbsolutePath());
        }
    }

    private TtsResponse getTtsResponse() {
        // 设置可选参数
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("spd", Integer.toString((int) textToSpeechToolController.getSpdSlider().getValue()));
        options.put("vol", Integer.toString((int) textToSpeechToolController.getVolSlider().getValue()));
        String per = textToSpeechToolController.getPerToggleGroup().getSelectedToggle().getUserData().toString();
        options.put("per", per);
//        System.out.println(options.toString());
        log.info("转换参数："+options.toString());
        String textString = textToSpeechToolController.getTextTextArea().getText();
        TtsResponse res = CacheUtil.getInstance().get(options.toString() + textString, TtsResponse.class, s -> {
            return client.synthesis(textString, "zh", 1, options);
        });
        return res;
    }

    public TextToSpeechToolService(TextToSpeechToolController textToSpeechToolController) {
        this.textToSpeechToolController = textToSpeechToolController;
    }
}