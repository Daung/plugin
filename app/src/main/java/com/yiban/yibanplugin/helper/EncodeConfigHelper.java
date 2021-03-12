package com.yiban.yibanplugin.helper;

import android.text.TextUtils;

import com.yiban.yibanplugin.bean.EncodeConfigBean;

public class EncodeConfigHelper {

    private static EncodeConfigHelper helper;

    private final EncodeConfigBean configBean;

    private final EncodeConfigBean cacheBean;

    public static EncodeConfigHelper getHelper() {
        if (helper == null) {
            synchronized (EncodeConfigHelper.class) {
                if (helper == null) {
                    helper = new EncodeConfigHelper();
                }
            }
        }
         return helper;
    }
    private EncodeConfigHelper() {
        configBean = GsonHelper.fromJson(AssetHelper.getStringFormAsset("encode.json"), EncodeConfigBean.class);
        cacheBean = new EncodeConfigBean();
    }

    public String getName() {
        if (TextUtils.isEmpty(cacheBean.getName())) {
            String name = EncryptionHelper.decryptEncodeString(configBean.getName());
            cacheBean.setName(name);
        }
        return cacheBean.getName();
    }

    public String getAge() {
        if (TextUtils.isEmpty(cacheBean.getAge())) {
            String name = EncryptionHelper.decryptEncodeString(configBean.getAge());
            cacheBean.setAge(name);
        }
        return cacheBean.getAge();
    }

}
