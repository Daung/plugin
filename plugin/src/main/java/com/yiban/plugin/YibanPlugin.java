package com.yiban.plugin;

import com.yiban.plugin.extension.EncodeExt;
import com.yiban.plugin.task.DecodeTask;
import com.yiban.plugin.task.EncodeTask;
import com.yiban.plugin.task.MakeClassTask;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class YibanPlugin implements Plugin<Project> {
    public static final String group = "encode";
    public static final String encode_name = "yiban_plugin_encode";
    public static final String decode_name = "yiban_plugin_decode";
    public static final String helper_class = "yiban_plugin_helper_class";

    @Override
    public void apply(Project project) {

        EncodeExt ext = project.getExtensions().create(group, EncodeExt.class);
        ext.setProject(project);
        project.getTasks().create(encode_name, EncodeTask.class, project, ext);
        project.getTasks().create(decode_name, DecodeTask.class, project, ext);
        project.getTasks().create(helper_class, MakeClassTask.class, project, ext);
    }
}