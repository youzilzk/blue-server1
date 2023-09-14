package com.youzi.blue.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {
    private static final Config instance = new Config();
    private static final String regex1 = "^-(.+)=(.+)$";
    private static final String regex2 = "^--(.+)=(.+)$";

    public static Config getInstance() {
        return instance;
    }

    private Map<String, String> data = new HashMap<>();

    private Properties cfg = new Properties();

    public void init(String[] args) {
        InputStream is = Config.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            cfg.load(is);
            for (String name : cfg.stringPropertyNames()) {
                data.put(name, cfg.getProperty(name));
            }
            is.close();
            //参数覆盖
            if (args != null && args.length > 0) {
                for (String arg : args) {
                    if (Pattern.matches(regex1, arg)) {
                        Pattern pattern = Pattern.compile(regex1);
                        Matcher m = pattern.matcher(arg);
                        if (m.find()) {
                            String key = m.group(1);
                            String value = m.group(2);
                            data.put(key, value);
                        } else {
                            System.out.println("NO MATCH");
                        }
                    }
                    if (Pattern.matches(regex2, arg)) {
                        Pattern pattern = Pattern.compile(regex2);
                        Matcher m = pattern.matcher(arg);
                        if (m.find()) {
                            String key = m.group(1);
                            String value = m.group(2);
                            data.put(key, value);
                        } else {
                            System.out.println("NO MATCH");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean hasKey(String key) {
        return data.containsKey(key);
    }

    public String getProperty(String name) {
        return (String) data.get(name);
    }

    public String getStringValue(String key) {
        return this.getProperty(key);
    }

    public String getStringValue(String key, String defaultValue) {
        String value = this.getStringValue(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public int getIntValue(String key, int defaultValue) {
        return LangUtil.parseInt(data.get(key), defaultValue);
    }

    public int getIntValue(String key) {
        return LangUtil.parseInt(data.get(key));
    }

    public double getDoubleValue(String key, Double defaultValue) {
        return LangUtil.parseDouble(data.get(key), defaultValue);
    }

    public double getDoubleValue(String key) {
        return LangUtil.parseDouble(data.get(key));
    }

    public double getLongValue(String key, Long defaultValue) {
        return LangUtil.parseLong(this.getProperty(key), defaultValue);
    }

    public double getLongValue(String key) {
        return LangUtil.parseLong(this.getProperty(key));
    }

    public Boolean getBooleanValue(String key, Boolean defaultValue) {
        return LangUtil.parseBoolean(this.getProperty(key), defaultValue);
    }

    public Boolean getBooleanValue(String key) {
        return LangUtil.parseBoolean(this.getProperty(key));
    }


}
