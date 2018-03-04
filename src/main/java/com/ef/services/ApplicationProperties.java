package com.ef.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private Properties properties = new Properties();

    public ApplicationProperties() throws IOException {
        InputStream in = this.getClass()
                .getClassLoader()
                .getResourceAsStream
                        ("application.properties");
        properties.load(in);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
