package com.oceanbase.datamocker.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 模拟数据生成器配置类
 */
@Data
@Slf4j
public class MockerConfig {
    
    /**
     * 是否启用AI语义推断
     */
    private boolean enableAiInference = true;
    
    /**
     * 生成数据的默认行数
     */
    private int defaultRowCount = 100;
    
    /**
     * 字段配置映射，key为字段名，value为字段配置
     */
    private Map<String, FieldConfig> fieldConfigs = new HashMap<>();
    
    /**
     * 排除生成的字段列表
     */
    private String[] excludeFields = new String[0];
    
    /**
     * AI模型配置
     */
    private AiModelConfig aiModelConfig = new AiModelConfig();
    
    /**
     * 从YAML文件加载配置
     *
     * @param configFile 配置文件
     * @return 配置对象
     */
    public static MockerConfig fromYaml(File configFile) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.readValue(configFile, MockerConfig.class);
        } catch (IOException e) {
            log.error("Failed to load config from yaml file: {}", configFile.getAbsolutePath(), e);
            return new MockerConfig();
        }
    }
    
    /**
     * 从JSON文件加载配置
     *
     * @param configFile 配置文件
     * @return 配置对象
     */
    public static MockerConfig fromJson(File configFile) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.readValue(configFile, MockerConfig.class);
        } catch (IOException e) {
            log.error("Failed to load config from json file: {}", configFile.getAbsolutePath(), e);
            return new MockerConfig();
        }
    }
    
    /**
     * AI模型配置
     */
    @Data
    public static class AiModelConfig {
        /**
         * 模型类型，默认为BERT
         */
        private String modelType = "BERT";
        
        /**
         * 模型路径
         */
        private String modelPath = "";
        
        /**
         * 是否使用预训练模型
         */
        private boolean usePretrainedModel = true;
        
        /**
         * 模型推理超时时间（毫秒）
         */
        private long inferenceTimeout = 5000;
    }
} 