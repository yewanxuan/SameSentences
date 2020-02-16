/***************************************************************************
 *
 * Copyright (c) 2019 Baidu.com, Inc. All Rights Reserved
 * ConfigUtil.class 2019/11/13 19:15:43 yewanxuan
 *
 **************************************************************************/

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class Config {
    private static AtomicBoolean  propertyLoaded = new AtomicBoolean(false);
    private static Properties clientProperties;
    private static String FILENAME = "main.properties";
    private static String propFilePath = System.getProperty("user.dir") +
            File.separator + "conf" + File.separator;
    private static HashMap<String, String> configMap= new HashMap<>();

    private static  Properties loadProperties(String filename) {
        File clientPropertyFile = new File(propFilePath + filename);
        Properties clientProperties = new Properties();
        InputStream clientPropertyInputStream;
        try {
            clientPropertyInputStream = new FileInputStream(clientPropertyFile);
            clientProperties.load(clientPropertyInputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return clientProperties;
    }

    public static String get(String key) throws IllegalArgumentException {
        if (propertyLoaded.get() == false) {
            clientProperties = loadProperties(FILENAME);
            propertyLoaded.set(true);
        }
        String value = clientProperties.getProperty(key);
        if (value == null) {
            value = configMap.get(key);
            if (value == null ) {
                String errMsg = "ConfigUtil getkey Failed: " + key + ".Please Check Manually";
                throw new IllegalArgumentException(errMsg);
            }
        }
        return value;
    }

}