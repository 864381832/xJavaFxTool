package com.xwintop.xJavaFxTool.common;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.net.URLCodec;

/**
 * bs.util.common.ExCodeC.
 * CodeC for apache common codec.
 *
 * @author Baishui2004
 * @version 1.1
 */
public final class ExCodec {

	/**
	 * private constructor.
	 */
	private ExCodec() {
	}

	/**
	 * default encoding.
	 */
	public static final String ENCODING_DEFAULT = "UTF-8";

	/**
	 * Encode string to hex.
	 *
	 * @param string String
	 * @return <code>String</code> hex string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encodeHex(String string) throws UnsupportedEncodingException {
		return encodeHex(string, ENCODING_DEFAULT);
	}

	/**
	 * Encode string to hex.
	 *
	 * @param string String
	 * @param charSet CharSet
	 * @return <code>String</code> hex string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encodeHex(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		return Hex.encodeHexString(string.getBytes(charSet));
	}

	/**
	 * Decode hex to string.
	 *
	 * @param string String
	 * @return <code>String</code> string
	 * @throws DecoderException if decode error occurs
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String decodeHex(String string) throws DecoderException, UnsupportedEncodingException {
		return decodeHex(string, ENCODING_DEFAULT);
	}

	/**
	 * Decode hex to string.
	 *
	 * @param string String
	 * @param charSet CharSet
	 * @return <code>String</code> string
	 * @throws DecoderException if decode error occurs
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String decodeHex(String string, String charSet) throws UnsupportedEncodingException, DecoderException {
		if (string == null) {
			return null;
		}
		return new String(Hex.decodeHex(string.toCharArray()), charSet);
	}

	/**
	 * Encode string to ascii.
	 *
	 * @param string String
	 * @return <code>String</code> ascii string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encodeAscii(String string) throws UnsupportedEncodingException {
		return encodeAscii(string, ENCODING_DEFAULT);
	}

	/**
	 * Encode string to ascii.
	 *
	 * @param string String
	 * @param charSet CharSet
	 * @return <code>String</code> ascii string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encodeAscii(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		return BinaryCodec.toAsciiString(string.getBytes(charSet));
	}

	/**
	 * Decode ascii to string.
	 *
	 * @param string String
	 * @return <code>String</code> string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String decodeAscii(String string) throws UnsupportedEncodingException {
		return decodeAscii(string, ENCODING_DEFAULT);
	}

	/**
	 * Decode ascii to string.
	 *
	 * @param string String
	 * @param charSet CharSet
	 * @return <code>String</code> string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String decodeAscii(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		return new String(BinaryCodec.fromAscii(string.toCharArray()), charSet);
	}

	/**
	 * Encode string for URL.
	 *
	 * @param string String
	 * @return <code>String</code> url string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encodeURL(String string) throws UnsupportedEncodingException {
		return encodeURL(string, ENCODING_DEFAULT);
	}

	/**
	 * Encode string for URL.
	 *
	 * @param string String
	 * @param charSet CharSet
	 * @return <code>String</code> url string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encodeURL(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		return new String(URLCodec.encodeUrl(null, string.getBytes(charSet)), charSet);
	}

	/**
	 * Decode URL.
	 *
	 * @param string String
	 * @return <code>String</code> string
	 * @throws DecoderException if decode error occurs
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String decodeURL(String string) throws DecoderException, UnsupportedEncodingException {
		return decodeURL(string, ENCODING_DEFAULT);
	}

	/**
	 * Decode URL.
	 *
	 * @param string String
	 * @param charSet CharSet
	 * @return <code>String</code> string
	 * @throws DecoderException if decode error occurs
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String decodeURL(String string, String charSet) throws DecoderException, UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		return new String(URLCodec.decodeUrl(string.getBytes(charSet)), charSet);
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex string.
	 *
	 * @param string String
	 * @return <code>String</code> MD5 digest as a hex string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encryptMd5(String string) throws UnsupportedEncodingException {
		return encryptMd5(string, ENCODING_DEFAULT);
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex string.
	 *
	 * @param string String
	 * @param charSet CharSet
	 * @return <code>String</code> MD5 digest as a hex string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encryptMd5(String string, String charSet) throws UnsupportedEncodingException {
		return DigestUtils.md5Hex(string.getBytes(charSet));
	}

	/**
	 * Calculates the SHA-1 digest and returns the value as a hex string.
	 *
	 * @param string String
	 * @return <code>String</code> SHA-1 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encryptSha(String string) throws UnsupportedEncodingException {
		return encryptSha(string, ENCODING_DEFAULT);
	}

	/**
	 * Calculates the SHA-1 digest and returns the value as a hex string.
	 *
	 * @param string String
	 * @param charSet CharSet
	 * @return <code>String</code> SHA-1 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encryptSha(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		return DigestUtils.sha1Hex(string.getBytes(charSet));
	}

	/**
	 * Calculates the SHA-256 digest and returns the value as a hex string.
	 *
	 * @param string String
	 * @return <code>String</code> SHA-256 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encryptSha256(String string) throws UnsupportedEncodingException {
		return encryptSha256(string, ENCODING_DEFAULT);
	}

	/**
	 * Calculates the SHA-256 digest and returns the value as a hex string.
	 *
	 * @param string String
	 * @param charSet CharSet
	 * @return <code>String</code> SHA-256 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encryptSha256(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		return DigestUtils.sha256Hex(string.getBytes(charSet));
	}

	/**
	 * Calculates the SHA-384 digest and returns the value as a hex string.
	 *
	 * @param string String
	 * @return <code>String</code> SHA-384 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encryptSha384(String string) throws UnsupportedEncodingException {
		return encryptSha384(string, ENCODING_DEFAULT);
	}

	/**
	 * Calculates the SHA-384 digest and returns the value as a hex string.
	 *
	 * @param string String
	 * @param charSet CharSet
	 * @return <code>String</code> SHA-384 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encryptSha384(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		return DigestUtils.sha384Hex(string.getBytes(charSet));
	}

	/**
	 * Calculates the SHA-512 digest and returns the value as a hex string.
	 *
	 * @param string String
	 * @return <code>String</code> SHA-512 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encryptSha512(String string) throws UnsupportedEncodingException {
		return encryptSha512(string, ENCODING_DEFAULT);
	}

	/**
	 * Calculates the SHA-512 digest and returns the value as a hex string.
	 *
	 * @param string String
	 * @param charSet CharSet
	 * @return <code>String</code> SHA-512 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encryptSha512(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		return DigestUtils.sha512Hex(string.getBytes(charSet));
	}

	/**
	 * Encode string for Base32.
	 *
	 * @param string String
	 * @return <code>String</code> Base32 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encodeBase32(String string) throws UnsupportedEncodingException {
		return encodeBase32(string, ENCODING_DEFAULT);
	}

	/**
	 * Encode string for Base32.
	 *
	 * @param string  String
	 * @param charSet CharSet
	 * @return <code>String</code> Base32 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encodeBase32(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		Base32 base32 = new Base32();
		return new String(base32.encode(string.getBytes(charSet)));
	}

	/**
	 * Decode Base32.
	 *
	 * @param string String
	 * @return <code>String</code> string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String decodeBase32(String string) throws UnsupportedEncodingException {
		return decodeBase32(string, ENCODING_DEFAULT);
	}

	/**
	 * Decode Base32.
	 *
	 * @param string  String
	 * @param charSet CharSet
	 * @return <code>String</code> string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String decodeBase32(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		Base32 base32 = new Base32();
		return new String(base32.decode(string.getBytes(charSet)), charSet);
	}

	/**
	 * Encode string for Base64.
	 *
	 * @param string String
	 * @return <code>String</code> Base64 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encodeBase64(String string) throws UnsupportedEncodingException {
		return encodeBase64(string, ENCODING_DEFAULT);
	}

	/**
	 * Encode string for Base64.
	 *
	 * @param string  String
	 * @param charSet CharSet
	 * @return <code>String</code> Base64 string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String encodeBase64(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		return new String(Base64.encodeBase64(string.getBytes(charSet)));
	}

	/**
	 * Decode Base64.
	 *
	 * @param string String
	 * @return <code>String</code> string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String decodeBase64(String string) throws UnsupportedEncodingException {
		return decodeBase64(string, ENCODING_DEFAULT);
	}

	/**
	 * Decode Base64.
	 *
	 * @param string  String
	 * @param charSet CharSet
	 * @return <code>String</code> string
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	public static String decodeBase64(String string, String charSet) throws UnsupportedEncodingException {
		if (string == null) {
			return null;
		}
		return new String(Base64.decodeBase64(string.getBytes(charSet)), charSet);
	}
}