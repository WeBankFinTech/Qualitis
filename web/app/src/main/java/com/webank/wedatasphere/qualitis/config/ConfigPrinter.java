/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.config;

import com.webank.wedatasphere.qualitis.exception.UnSupportConfigFileSuffixException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
@Component
public class ConfigPrinter {

    @Autowired
    private Environment environment;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPrinter.class);
    private static final String PROPERTIES_FILE_EXTENSION = "properties";
    private static final String YAML_FILE_EXTENSION = "yml";

    private List<String> propertiesFiles = new ArrayList<>();
    private String[] fileExtension = new String[] { PROPERTIES_FILE_EXTENSION, YAML_FILE_EXTENSION };

    private void addPropertiesFile() {
        // Add related config file
        List<String> profiles = new ArrayList<>();
        profiles.add("");
        profiles.addAll(Arrays.asList(environment.getActiveProfiles()));
        for (String profile : profiles) {
            for (String fileExt : fileExtension) {
                String name;
                if ("".equals(profile)) {
                    name = "classpath:/application." + fileExt;
                } else {
                    name = "classpath:/application-" + profile + "." + fileExt;
                }
                propertiesFiles.add(name);
            }
        }
    }

    @PostConstruct
    public void printConfig() throws UnSupportConfigFileSuffixException {
        LOGGER.info("Start to print config");
        addPropertiesFile();
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        LOGGER.info("Prepared to print config in file: {}", propertiesFiles);

        for (String fileName : propertiesFiles) {
            LOGGER.info("======================== Config in {} ========================", fileName);
            PropertySourceLoader propertySourceLoader = getPropertySourceLoader(fileName);
            Resource resource = resourceLoader.getResource(fileName);
            try {
                List<PropertySource<?>> propertySources = propertySourceLoader.load(fileName, resource);
                for (PropertySource p : propertySources) {
                    Map<String, Object> map = (Map<String, Object>) p.getSource();
                    for (String key : map.keySet()) {
                        LOGGER.info("Name: [{}]=[{}]", key, map.get(key));
                    }
                }
            } catch (IOException e) {
                LOGGER.info("Failed to open file: {}, caused by: {}, it does not matter", fileName, e.getMessage());
            }
            LOGGER.info("=======================================================================================\n");
        }
        LOGGER.info("Succeed to print all configs");
    }

    private PropertySourceLoader getPropertySourceLoader(String fileName) throws UnSupportConfigFileSuffixException {
        if (fileName.contains(PROPERTIES_FILE_EXTENSION)) {
            return new PropertiesPropertySourceLoader();
        } else if (fileName.contains(YAML_FILE_EXTENSION)) {
            return new YamlPropertySourceLoader();
        }
        LOGGER.error("Failed to recognize file: {}", fileName);
        throw new UnSupportConfigFileSuffixException();
    }

}
