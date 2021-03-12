package com.yiban.yibanplugin.helper;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//import com.blankj.utilcode.util.EncryptUtils;
//import com.yiban.app.jni.JNIHelper;

public class EncryptionHelper {

    private static final String key = "yibanEncryPlugin";

    public static String encryptPassword(String password) {
//        String publicKey = JNIHelper.getEncodePwdPublicKey();
//        byte[] bytes = EncryptUtils.encryptRSA2Base64(password.getBytes(), base64Decode(publicKey.getBytes()), 1024, "RSA/None/PKCS1Padding");
//        return new String(bytes);
        return "";
    }

    private static byte[] base64Decode(final byte[] input) {
        return Base64.decode(input, Base64.NO_WRAP);
    }


    public static String decodeConfigString(String config) {
//        String privateKey = JNIHelper.getEncodeConfigPrivateKey();
//        byte[] bytes = EncryptUtils.decryptBase64RSA(config.getBytes(), base64Decode(privateKey.getBytes()), 1024 * 4, "RSA/None/PKCS1Padding");
//        return bytes == null ? "" : new String(bytes);
        return "";
    }

    public static String decryptEncodeString(String data) {
        try {
            byte[] bytes = base64Decode(data.getBytes(StandardCharsets.UTF_8));
            return new String(decrypt(bytes, key.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static byte[] decrypt(byte[] bytes, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Key k = new SecretKeySpec(key, "AES");
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, k, paramSpec);
        return cipher.doFinal(bytes);
    }
}
