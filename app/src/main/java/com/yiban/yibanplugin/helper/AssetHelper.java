package com.yiban.yibanplugin.helper;

import android.content.res.AssetManager;

import com.yiban.application.YibanApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//import com.yiban.app.application.YibanApplication;

public class AssetHelper {

    public static String getStringFormAsset(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getStreamFromAsset(fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    inputStream));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStream.close();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static InputStream getStreamFromAsset(String fileName) throws IOException {
        AssetManager manager = YibanApplication.getInstance().getAssets();
        return manager.open(fileName);
    }
}
