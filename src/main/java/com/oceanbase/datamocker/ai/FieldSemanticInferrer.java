package com.oceanbase.datamocker.ai;

import com.oceanbase.datamocker.config.FieldConfig;

/**
 * 字段语义推断接口
 */
public interface FieldSemanticInferrer {
    
    /**
     * 推断字段的语义类型
     *
     * @param fieldName 字段名
     * @param sqlType SQL类型
     * @return 推断的语义类型
     */
    SemanticType inferSemanticType(String fieldName, String sqlType);
    
    /**
     * 根据语义类型生成字段配置
     *
     * @param fieldName 字段名
     * @param sqlType SQL类型
     * @param semanticType 语义类型
     * @return 字段配置
     */
    FieldConfig generateFieldConfig(String fieldName, String sqlType, SemanticType semanticType);
    
    /**
     * 初始化推断器
     */
    void initialize();
    
    /**
     * 关闭推断器，释放资源
     */
    void close();
} 