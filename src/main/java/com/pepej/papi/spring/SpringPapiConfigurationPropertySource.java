package com.pepej.papi.spring;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.springframework.core.env.PropertySource;

public class SpringPapiConfigurationPropertySource extends PropertySource<FileConfiguration> {

    public SpringPapiConfigurationPropertySource(String name, YamlConfiguration source) {
        super(name, source);
    }

    @Override
    public Object getProperty(String name) {
        return source.get(name);
    }
}
