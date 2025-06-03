package com.oceanbase.datamocker;

import com.oceanbase.datamocker.ai.FieldSemanticInferrer;
import com.oceanbase.datamocker.ai.SemanticInferrerFactory;
import com.oceanbase.datamocker.config.FieldConfig;
import com.oceanbase.datamocker.config.MockerConfig;
import com.oceanbase.datamocker.generator.DataGenerator;
import com.oceanbase.datamocker.generator.DataGeneratorFactory;
import com.oceanbase.datamocker.generator.SemanticDataGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

/**
 * 数据模拟生成器主类
 */
@Slf4j
public class DataMocker {
    
    private final MockerConfig config;
    private final FieldSemanticInferrer semanticInferrer;
    private final SemanticDataGenerator semanticDataGenerator;
    
    /**
     * 构造函数
     *
     * @param config 配置
     */
    public DataMocker(MockerConfig config) {
        this.config = config;
        this.semanticInferrer = SemanticInferrerFactory.createInferrer(config);
        this.semanticDataGenerator = new SemanticDataGenerator(semanticInferrer);
    }
    
    /**
     * 生成模拟数据
     *
     * @param tableName 表名
     * @param fieldNames 字段名列表
     * @param fieldTypes 字段类型列表
     * @param rowCount 生成的行数
     * @return 生成的数据，每行是一个Map，key为字段名，value为生成的值
     */
    public List<Map<String, Object>> generateData(String tableName, List<String> fieldNames, List<String> fieldTypes, int rowCount) {
        if (fieldNames.size() != fieldTypes.size()) {
            throw new IllegalArgumentException("Field names and types must have the same size");
        }
        
        if (rowCount <= 0) {
            rowCount = config.getDefaultRowCount();
        }
        
        log.info("Generating {} rows of data for table: {}", rowCount, tableName);
        
        List<Map<String, Object>> result = new ArrayList<>(rowCount);
        Set<String> excludeFields = new HashSet<>(Arrays.asList(config.getExcludeFields()));
        
        // 生成每一行数据
        for (int i = 0; i < rowCount; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            
            // 生成每个字段的数据
            for (int j = 0; j < fieldNames.size(); j++) {
                String fieldName = fieldNames.get(j);
                String fieldType = fieldTypes.get(j);
                
                // 跳过排除的字段
                if (excludeFields.contains(fieldName)) {
                    continue;
                }
                
                // 获取字段配置
                FieldConfig fieldConfig = getFieldConfig(fieldName, fieldType);
                
                // 生成字段值
                Object value = generateFieldValue(fieldName, fieldConfig);
                
                row.put(fieldName, value);
            }
            
            result.add(row);
        }
        
        return result;
    }
    
    /**
     * 获取字段配置
     *
     * @param fieldName 字段名
     * @param fieldType 字段类型
     * @return 字段配置
     */
    private FieldConfig getFieldConfig(String fieldName, String fieldType) {
        // 如果配置中有该字段的配置，使用配置中的
        FieldConfig fieldConfig = config.getFieldConfigs().get(fieldName);
        if (fieldConfig != null) {
            // 确保类型已设置
            if (fieldConfig.getType() == null || fieldConfig.getType().isEmpty()) {
                fieldConfig.setType(fieldType);
            }
            return fieldConfig;
        }
        
        // 否则创建一个新的配置
        fieldConfig = new FieldConfig();
        fieldConfig.setType(fieldType);
        
        return fieldConfig;
    }
    
    /**
     * 生成字段值
     *
     * @param fieldName 字段名
     * @param fieldConfig 字段配置
     * @return 生成的值
     */
    private Object generateFieldValue(String fieldName, FieldConfig fieldConfig) {
        // 使用语义数据生成器生成数据
        return semanticDataGenerator.generate(fieldName, fieldConfig);
    }
    
    /**
     * 从配置文件创建数据模拟器
     *
     * @param configFile 配置文件
     * @return 数据模拟器
     */
    public static DataMocker fromConfigFile(File configFile) {
        MockerConfig config;
        
        if (configFile.getName().endsWith(".yaml") || configFile.getName().endsWith(".yml")) {
            config = MockerConfig.fromYaml(configFile);
        } else if (configFile.getName().endsWith(".json")) {
            config = MockerConfig.fromJson(configFile);
        } else {
            log.warn("Unsupported config file format: {}, using default config", configFile.getName());
            config = new MockerConfig();
        }
        
        return new DataMocker(config);
    }
    
    /**
     * 使用默认配置创建数据模拟器
     *
     * @return 数据模拟器
     */
    public static DataMocker createDefault() {
        return new DataMocker(new MockerConfig());
    }
    
    /**
     * 关闭数据模拟器，释放资源
     */
    public void close() {
        if (semanticInferrer != null) {
            semanticInferrer.close();
        }
    }
} 