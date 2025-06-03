package com.oceanbase.datamocker.generator;

import com.oceanbase.datamocker.config.FieldConfig;

/**
 * 数据生成器接口
 */
public interface DataGenerator {
    
    /**
     * 生成一个数据值
     *
     * @param fieldName 字段名
     * @param fieldConfig 字段配置
     * @return 生成的数据值
     */
    Object generate(String fieldName, FieldConfig fieldConfig);
    
    /**
     * 检查是否支持指定的字段类型
     *
     * @param fieldType 字段类型
     * @return 是否支持
     */
    boolean supports(String fieldType);
} 