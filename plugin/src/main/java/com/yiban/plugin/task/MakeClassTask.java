package com.yiban.plugin.task;

import com.android.build.gradle.AppExtension;
import com.yiban.plugin.YibanPlugin;
import com.yiban.plugin.extension.EncodeExt;
import com.yiban.plugin.helper.EncryptionHelper;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class MakeClassTask extends DefaultTask {

    private static final String JAVA_BEAN_FILE_NAME = "EncodeConfigBean";
    private static final String JAVA_HELPER_FILE_NAME = "EncodeConfigHelper";
    private final Project mProject;
    private final EncodeExt mExt;

    AppExtension mAppExtension;

    @Inject
    public MakeClassTask(Project project, EncodeExt ext) {
        this.mProject = project;
        this.mExt = ext;
        setGroup(YibanPlugin.group);
        setDescription("根据 encode 或者 decode 配置文件自动生成类");
        mAppExtension = (AppExtension) project.getExtensions().findByName("android");
    }

    @TaskAction
    public void AutoGenerateClass() {

        //初始化路径
//        initDir();

        //读取文件
        boolean encodeFileExist = checkEncodeFileExist();
        String encodeFileName = mExt.getEncodeFileName();
        String decodeFileName = mExt.getDecodeFileName();
        String fileContent;
        if (encodeFileExist) {
            fileContent = getFileContent(new File(mExt.getEncodePath(), encodeFileName));
            System.out.println(encodeFileName + " 配置文件存在");
        } else {
            System.out.println(encodeFileName + " 配置文件不存在，尝试读取decode.json文件");
            boolean decodeFileExist = checkDecodeFileExist();

            if (decodeFileExist) {
                System.out.println(decodeFileName + " 配置文件存在");
                fileContent = getFileContent(new File(mExt.getDecodePath(), decodeFileName));
            } else {
                System.out.println(decodeFileName + " 配置文件不存在,自动生成类结束");
                return;
            }
        }

        if (fileContent == null || "".equals(fileContent)) {
            System.out.println("配置文件内容为空,自动生成类结束");
            return;
        }


        checkContentValid(fileContent);

    }

    private void checkContentValid(String fileContent) {
        try {
            JSONObject configObj = new JSONObject(fileContent);
            parseFileContent(configObj);
        } catch (JSONException e) {
            System.out.println("内容格式异常 " + e.getLocalizedMessage());
        }
    }

    private void parseFileContent(JSONObject configObj) {
        Iterator<String> keys = configObj.keys();
        if (!keys.hasNext()) {
            System.out.println("空的JSON 对象, 自动生成类结束");
            return;
        }
        //检测Javabean 目录
        initJavaBeanDir();
        //首先生成类
        List<String> _keys = iterator2List(keys);
        generateJavaBean(_keys);

        generateJavaHelper(_keys);
    }

    private void generateJavaHelper(List<String> keys) {
        try {
            String lastDir = "helper";
            File dirBean = getJavaBeanDirFile(lastDir);
            if (!dirBean.exists()) {
                dirBean.mkdirs();
            }
            File javaBeanFile = new File(dirBean, JAVA_HELPER_FILE_NAME.concat(".java"));
            if (!javaBeanFile.exists()) {
                javaBeanFile.createNewFile();
            }

            String applicationId = mAppExtension.getDefaultConfig().getApplicationId();


            StringBuilder javaHelperBean = new StringBuilder();
            //包名路径
            javaHelperBean.append("package ")
                    .append(applicationId)
                    .append(".")
                    .append(lastDir)
                    .append(";")
                    .append("\n\n");

            javaHelperBean.append("import ")
                    .append("android.text.TextUtils;")
                    .append("\n\n");


            javaHelperBean.append("import ")
                    .append(applicationId)
                    .append(".")
                    .append("bean.")
                    .append(JAVA_BEAN_FILE_NAME)
                    .append(";")
                    .append("\n\n");


            //javabean累的框架

            javaHelperBean.append("public class ")
                    .append(JAVA_HELPER_FILE_NAME)
                    .append(" {")
                    .append("\n\n");


            //拼接字段
            String emptySpace = "    ";

            //生成字段
            javaHelperBean.append(emptySpace)
                    .append("private static ")
                    .append(JAVA_HELPER_FILE_NAME)
                    .append(" helper;")
                    .append("\n\n");

            javaHelperBean.append(emptySpace)
                    .append("private final ")
                    .append(JAVA_BEAN_FILE_NAME)
                    .append(" configBean;")
                    .append("\n\n");

            javaHelperBean.append(emptySpace)
                    .append("private final ")
                    .append(JAVA_BEAN_FILE_NAME)
                    .append(" cacheBean;")
                    .append("\n\n");


            //生成单例构造方法
            javaHelperBean.append(emptySpace)
                    .append("public static ")
                    .append(JAVA_HELPER_FILE_NAME)
                    .append(" getHelper() {")
                    .append("\n")
                    .append(emptySpace)
                    .append(emptySpace)
                    .append("if (helper == null) {")
                    .append("\n")
                    .append(emptySpace)
                    .append(emptySpace)
                    .append(emptySpace)
                    .append("synchronized (EncodeConfigHelper.class) {")
                    .append("\n")
                    .append(emptySpace)
                    .append(emptySpace)
                    .append(emptySpace)
                    .append(emptySpace)
                    .append("if (helper == null) {")
                    .append("\n")
                    .append(emptySpace)
                    .append(emptySpace)
                    .append(emptySpace)
                    .append(emptySpace)
                    .append(emptySpace)
                    .append("helper = new ")
                    .append(JAVA_HELPER_FILE_NAME)
                    .append("();")
                    .append("\n")
                    .append(emptySpace)
                    .append(emptySpace)
                    .append(emptySpace)
                    .append(emptySpace)
                    .append("}")
                    .append("\n")
                    .append(emptySpace)
                    .append(emptySpace)
                    .append(emptySpace)
                    .append("}")
                    .append("\n")
                    .append(emptySpace)
                    .append(emptySpace)
                    .append("}")
                    .append("\n")
                    .append(emptySpace)
                    .append(emptySpace)
                    .append(" return helper;")
                    .append("\n")
                    .append(emptySpace)
                    .append("}")
                    .append("\n");


            //私有的构造方法
            javaHelperBean.append(emptySpace)
                    .append("private ")
                    .append(JAVA_HELPER_FILE_NAME)
                    .append("() {")
                    .append("\n")
                    .append(emptySpace)
                    .append(emptySpace)
                    .append("configBean = GsonHelper.fromJson(AssetHelper.getStringFormAsset(")
                    .append("\"")
                    .append(mExt.getEncodeFileName())
                    .append("\"")
                    .append("), ")
                    .append(JAVA_BEAN_FILE_NAME)
                    .append(".class);")
                    .append("\n")
                    .append(emptySpace)
                    .append(emptySpace)
                    .append("cacheBean = new ")
                    .append(JAVA_BEAN_FILE_NAME)
                    .append("();")
                    .append("\n")
                    .append(emptySpace)
                    .append("}")
                    .append("\n\n");


            //生成get方法
            for (String key : keys) {
                String firstLetterUpper = key.substring(0, 1).toUpperCase().concat(key.substring(1));
                String getName = "get".concat(firstLetterUpper);
                String setName = "set".concat(firstLetterUpper);
                //get方法
                javaHelperBean.append(emptySpace)
                        .append("public String ")
                        .append(getName)
                        .append("() {")
                        .append("\n")
                        .append(emptySpace)
                        .append(emptySpace)
                        .append("if (TextUtils.isEmpty(cacheBean.")
                        .append(getName)
                        .append("())) {")
                        .append("\n")
                        .append(emptySpace)
                        .append(emptySpace)
                        .append(emptySpace)
                        .append("String name = EncryptionHelper.decryptEncodeString(configBean.")
                        .append(getName)
                        .append("());")
                        .append("\n")
                        .append(emptySpace)
                        .append(emptySpace)
                        .append(emptySpace)
                        .append("cacheBean.")
                        .append(setName)
                        .append("(name);")
                        .append("\n")
                        .append(emptySpace)
                        .append(emptySpace)
                        .append("}")
                        .append("\n")
                        .append(emptySpace)
                        .append(emptySpace)
                        .append("return cacheBean.")
                        .append(getName)
                        .append("();")
                        .append("\n")
                        .append(emptySpace)
                        .append("}")
                        .append("\n\n");
            }

            javaHelperBean.append("}")
                    .append("\n");
            String content = javaHelperBean.toString();
            System.out.println("===========================================");
            System.out.println(content);
            System.out.println("===========================================");

            writeFile(content, javaBeanFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initJavaBeanDir() {
        String applicationId = mAppExtension.getDefaultConfig().getApplicationId();
        String beanDir = mProject.getRootDir() + "/app/src/main/java/" + applicationId.replaceAll("\\.", "/") + "/bean";
        File dirBean = new File(beanDir);
        if (dirBean.exists()) {
            System.out.println(beanDir + " 已经存在");
        } else {
            boolean mkdirs = dirBean.mkdirs();
            if (mkdirs) {
                System.out.println(beanDir + " 创建成功");
            } else {
                System.out.println(beanDir + " 创建失败");
            }
        }
    }


    private List<String> iterator2List(Iterator<String> keys) {
        List<String> _keys = new ArrayList<>();
        while (keys.hasNext()) {
            String key = keys.next();
            _keys.add(key);
        }
        return _keys;
    }

    private void generateJavaBean(List<String> _keys) {
        try {
            String lastDir = "bean";
            File dirBean = getJavaBeanDirFile(lastDir);
            if (!dirBean.exists()) {
                dirBean.mkdirs();
            }
            File javaBeanFile = new File(dirBean, JAVA_BEAN_FILE_NAME.concat(".java"));
            if (!javaBeanFile.exists()) {
                javaBeanFile.createNewFile();
            }

            String applicationId = mAppExtension.getDefaultConfig().getApplicationId();


            StringBuilder javaBean = new StringBuilder();
            //包名路径
            javaBean.append("package ")
                    .append(applicationId)
                    .append(".")
                    .append(lastDir)
                    .append(";")
                    .append("\n\n");


            //javabean累的框架

            javaBean.append("public class ")
                    .append(JAVA_BEAN_FILE_NAME)
                    .append(" {")
                    .append("\n\n");


            //拼接字段
            String emptySpace = "    ";

            //生成字段
            for (String key : _keys) {
                javaBean.append(emptySpace)
                        .append("private String ")
                        .append(key)
                        .append(";")
                        .append("\n");
            }

            javaBean.append("\n\n");

            //生成字段的get 方法和set 方法
            for (String key : _keys) {
                String firstLetterUpper = key.substring(0, 1).toUpperCase().concat(key.substring(1));
                String setName = "set".concat(firstLetterUpper);
                String getName = "get".concat(firstLetterUpper);

                //get方法
                javaBean.append(emptySpace)
                        .append("public String ")
                        .append(getName)
                        .append("() {")
                        .append("\n")
                        .append(emptySpace)
                        .append(emptySpace)
                        .append(" return ")
                        .append(key)
                        .append(";")
                        .append("\n")
                        .append(emptySpace)
                        .append("}")
                        .append("\n\n");

                //set方法
                javaBean.append(emptySpace)
                        .append("public void ")
                        .append(setName)
                        .append("(String ")
                        .append(key)
                        .append(") {")
                        .append("\n")
                        .append(emptySpace)
                        .append(emptySpace)
                        .append("this.")
                        .append(key)
                        .append(" = ")
                        .append(key)
                        .append(";")
                        .append("\n")
                        .append(emptySpace)
                        .append("}")
                        .append("\n\n");
            }

            javaBean.append("}")
                    .append("\n");
            String content = javaBean.toString();
            System.out.println("===========================================");
            System.out.println(content);
            System.out.println("===========================================");

            writeFile(content, javaBeanFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void writeFile(String fileContent, File javaBeanFile) {
        try {
            if (!javaBeanFile.exists()) {
                javaBeanFile.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(javaBeanFile));
            out.write(fileContent);
            out.close();
            System.out.println("文件创建成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("结束写入加密文件 ");
    }

    @NotNull
    private File getJavaBeanDirFile(String lastDir) {
        String applicationId = mAppExtension.getDefaultConfig().getApplicationId();
        String beanDir = mProject.getRootDir() + "/app/src/main/java/" + applicationId.replaceAll("\\.", "/") + "/" + lastDir;
        return new File(beanDir);
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

    private boolean checkEncodeFileExist() {
        return new File(mExt.getEncodePath()).exists();
    }

    private boolean checkDecodeFileExist() {
        return new File(mExt.getDecodePath()).exists();
    }

    private void initDir() {
        String classPath = mExt.getAutoGenerateClassPath();
        File dir = new File(classPath);
        if (dir.exists()) {
            System.out.println("检测自动生成类路径是存在 path = " + classPath);
        } else {
            System.out.println("检测自动生成类路径是不存在 path = " + classPath + " 开始创建路径");
            if (dir.mkdirs()) {
                System.out.println("检测自动生成类路径path = " + classPath + " 创建成功");
            } else {
                System.out.println("检测自动生成类路径path = " + classPath + " 创建失败");
            }
        }
    }
}
