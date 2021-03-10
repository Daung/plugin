package com.yiban.plugin.task;

import com.yiban.plugin.YibanPlugin;
import com.yiban.plugin.extension.EncodeExt;
import com.yiban.plugin.helper.EncryptionHelper;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Locale;

import javax.inject.Inject;

public class DecodeTask extends DefaultTask {

    protected final Project mProject;
    protected final EncodeExt mExt;

    @Inject
    public DecodeTask(Project project, EncodeExt ext) {
        this.mProject = project;
        this.mExt = ext;
        setGroup(YibanPlugin.group);
        setDescription("AES 解密Task");
    }

    @TaskAction
    public void taskAction() {
        boolean checkFile = checkFile();
        if (!checkFile) {
            return;
        }
        boolean encodeConfigFile = findEncodeConfigFile();
        if (!encodeConfigFile) {
            return;
        }

        String fileContent = readFileContent();
        if (fileContent != null && !"".equals(fileContent)) {
            writeFile(fileContent);
        }

    }

    private void writeFile(String fileContent) {
        System.out.println("开始写入解密文件 content = " + fileContent);
        try {
            fileContent = fileContent.replaceAll("\u0000", "");
            File encodeFile = new File(mExt.getDecodePath(), mExt.getDecodeFileName());
            if (!encodeFile.exists()) {
                encodeFile.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(encodeFile));
            out.write(fileContent);
            out.close();
            System.out.println("文件创建成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("结束写入解密文件 ");
    }

    private String readFileContent() {
        File encodeFile = new File(mExt.getEncodePath(), mExt.getEncodeFileName());
        System.out.println("解密配置文件的路径 " + encodeFile.getAbsolutePath());
        String content = getFileContent(encodeFile);
        if (content == null || "".equals(content)) {
            System.out.println("解密配置 文件内存为空 加密结束");
            return "";
        }

        System.out.println("开始检测文件内容格式");

        try {
            JSONObject object = new JSONObject(content);
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = object.optString(key);
                System.out.println("解密之前 key =" + key + " value = " + value);
                value = EncryptionHelper.decodeFromBase64String(value);
                System.out.println("解密之后 key =" + key + " value = " + value);
                object.put(key, value);
            }
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("encode.json 文件格式有误 错误原因 " + e.getMessage());
        }
        return "";
    }

    private static String getFileContent(File targetFile) {
        try {
            InputStream inputStream = new FileInputStream(targetFile);
            if (inputStream == null) {
                System.out.println("inputStream 为空");
                return "";
            }

            Reader reader = new InputStreamReader(inputStream);
            StringBuilder stringBuffer = new StringBuilder();
            char[] b = new char[1024];

            while (reader.read(b) != -1) {
                stringBuffer.append(b);
            }
            inputStream.close();
            reader.close();
            return stringBuffer.toString();
        } catch (Exception e) {
            System.out.println("读取加密文件异常: " + e.getMessage());
        }
        return "";
    }

    private boolean findEncodeConfigFile() {

        File encodeFile = new File(mExt.getEncodePath(), mExt.getEncodeFileName());
        boolean createSuccess = false;
        try {
            if (!encodeFile.exists()) {
                System.out.println(String.format(Locale.CHINESE, "%s 加密配置文件不存在, 开始创建", mExt.getEncodeFileName()));
                createSuccess = encodeFile.createNewFile();
            } else {
                System.out.println(String.format(Locale.CHINESE, "找到%s加密配置文件", mExt.getEncodeFileName()));
                createSuccess = true;
            }
        } catch (IOException e) {
            System.out.println(String.format(Locale.CHINESE, "%s 加密配置文件不存在, 创建失败", mExt.getEncodeFileName()));
        }
        return createSuccess;
    }

    private boolean checkFile() {
        String dir = mExt.getEncodePath();
        File dirFile = new File(dir);
        boolean createDirSuccess;
        if (!dirFile.exists()) {
            System.out.println(String.format(Locale.CHINESE, "%s 不存在，开始创建", dir));
            createDirSuccess = dirFile.mkdirs();
        } else {
            System.out.println(String.format(Locale.CHINESE, "%s 路径已经存在", dir));
            createDirSuccess = true;
        }
        if (!createDirSuccess) {
            System.out.println(String.format(Locale.CHINESE, "%s 不存在，创建路径失败", dir));
        }
        return createDirSuccess;
    }
}
