package com.yiban.plugin.extension;

import org.gradle.api.Project;

public class EncodeExt {


    public Project mProject;
    public String encode_path;
    public String decode_path;
    public String encode_file_name;
    public String decode_file_name;


    public void setProject(Project project) {
        this.mProject = project;
    }

    public String getEncodePath() {
        return encode_path;
    }

    public String getDecodePath() {
        return decode_path;
    }

    public String getEncodeFileName() {
        if (encode_file_name == null || "".equals(encode_file_name)) {
            encode_file_name = "encode.json";
        } else {
            if (!encode_file_name.contains(".")) {
                encode_file_name += ".json";
            }
        }
        return encode_file_name;
    }

    public String getDecodeFileName() {
        if (decode_file_name == null || "".equals(decode_file_name)) {
            decode_file_name = "decode.json";
        } else {
            if (!decode_file_name.contains(".")) {
                decode_file_name += ".json";
            }
        }
        return decode_file_name;
    }
}
