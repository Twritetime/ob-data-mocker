package com.oceanbase.datamocker.generator;

import com.oceanbase.datamocker.config.FieldConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 抽象数据生成器基类
 */
@Slf4j
public abstract class AbstractDataGenerator implements DataGenerator {
    
    protected final Random random = new Random();
    
    /**
     * 检查是否应该生成null值
     *
     * @param fieldConfig 字段配置
     * @return 是否生成null值
     */
    protected boolean shouldGenerateNull(FieldConfig fieldConfig) {
        if (!fieldConfig.isAllowNull() || fieldConfig.getNullRate() <= 0) {
            return false;
        }
        return random.nextDouble() < fieldConfig.getNullRate();
    }
    
    /**
     * 生成一个数据值，处理null值的情况
     *
     * @param fieldName 字段名
     * @param fieldConfig 字段配置
     * @return 生成的数据值
     */
    @Override
    public Object generate(String fieldName, FieldConfig fieldConfig) {
        if (shouldGenerateNull(fieldConfig)) {
            return null;
        }
        return doGenerate(fieldName, fieldConfig);
    }
    
    /**
     * 实际生成数据的方法，由子类实现
     *
     * @param fieldName 字段名
     * @param fieldConfig 字段配置
     * @return 生成的数据值
     */
    protected abstract Object doGenerate(String fieldName, FieldConfig fieldConfig);
} 