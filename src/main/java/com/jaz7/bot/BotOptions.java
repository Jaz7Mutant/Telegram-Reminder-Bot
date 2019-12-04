package com.jaz7.bot;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class BotOptions {
    public static Map<String, String> botOptions;
    public static Map<String, String> botAnswers;

    public BotOptions() {
        Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
        try {
            List<Map<String, String>> mapList = new GsonBuilder().create().fromJson(
                    new FileReader("src/main/resources/BotResources.json"), type);
            botOptions = mapList.get(0);
            botAnswers = mapList.get(1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}