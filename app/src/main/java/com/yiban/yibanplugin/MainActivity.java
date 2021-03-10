package com.yiban.yibanplugin;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    private static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";

    private static final String key = "yibanEncryPlugin";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                AssetManager manager = getResources().getAssets();
                String name = "encode.json";
                try {
                    InputStream inputStream = manager.open(name);
                    BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bf.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                    bf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String fileContent = stringBuilder.toString();
                Log.d(TAG, "onClick: content = " + fileContent);
                try {
                    JSONObject object = new JSONObject(fileContent);
                    Iterator<String> keys = object.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = object.optString(key);
                        System.out.println("解密之前 key =" + key + " value = " + value);
                        value = decodeFromBase64String(value);
                        System.out.println("解密之后 key =" + key + " value = " + value);
                        object.put(key, value);
                    }
                    Log.d(TAG, "onClick: result = " + object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static String decodeFromBase64String(String data) {
        try {
            byte[] bytes = android.util.Base64.decode(data, android.util.Base64.NO_WRAP);
//            byte[] bytes = Base64.getDecoder().decode(data);
            return new String(decrypt(bytes, key.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] decrypt(byte[] bytes, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
        Key k = new SecretKeySpec(key, KEY_ALGORITHM);
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, k, paramSpec);
        return cipher.doFinal(bytes);
    }
}