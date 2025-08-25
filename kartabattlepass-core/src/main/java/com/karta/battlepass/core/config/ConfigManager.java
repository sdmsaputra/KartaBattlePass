package com.karta.battlepass.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final File dataFolder;
    private final ClassLoader resourceClassLoader;
    private final ObjectMapper objectMapper;

    public ConfigManager(@NotNull File dataFolder, @NotNull ClassLoader resourceClassLoader) {
        this.dataFolder = dataFolder;
        this.resourceClassLoader = resourceClassLoader;
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public <T> T loadConfig(@NotNull String fileName, @NotNull Class<T> configClass) throws IOException {
        File file = new File(dataFolder, fileName);
        saveDefault(fileName, file);
        return objectMapper.readValue(file, configClass);
    }

    public <T> Map<String, T> loadConfigsFromDirectory(@NotNull String dirName, @NotNull Class<T> configClass) {
        File dir = new File(dataFolder, dirName);
        if (!dir.exists()) {
            dir.mkdirs();
            // We could copy a default directory here if we had one
        }

        Map<String, T> configs = new HashMap<>();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));
        if (files == null) {
            return configs;
        }

        for (File file : files) {
            try {
                configs.put(file.getName(), objectMapper.readValue(file, configClass));
            } catch (IOException e) {
                // Log this error properly in a real implementation
                e.printStackTrace();
            }
        }
        return configs;
    }

    private void saveDefault(@NotNull String resourceName, @NotNull File destination) throws IOException {
        if (!destination.exists()) {
            try (InputStream inputStream = resourceClassLoader.getResourceAsStream(resourceName)) {
                if (inputStream != null) {
                    Files.copy(inputStream, destination.toPath());
                }
            }
        }
    }
}
