package com.yiban.yibanplugin.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class GsonHelper {
    private static Gson mGson;

    private static void checkCreateGson() {
        if(mGson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(int.class, new IntTypeAdapter());
            builder.registerTypeAdapter(Integer.class, new IntTypeAdapter());
            mGson = builder.create();
        }
    }

    public static String toJson(Object src) {
        checkCreateGson();
        return mGson.toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        checkCreateGson();
        return mGson.toJson(src, typeOfSrc);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        checkCreateGson();
        return mGson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        checkCreateGson();
        return mGson.fromJson(json, typeOfT);
    }

    @Deprecated
    public static String toString(Object aClass) {
        checkCreateGson();
        return mGson.toJson(aClass);
    }


    public static String jsonString(Object object) {
        if (object == null) {
            return "";
        }
       return mGson.toJson(object);
    }

    public static class IntTypeAdapter extends TypeAdapter<Number> {
        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }

        @Override
        public Number read(JsonReader in) throws IOException {
            if(in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                String result = in.nextString();
                if("".equals(result)) {
                    return null;
                }
                return Integer.parseInt(result);
            }catch(NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }
    }
}
