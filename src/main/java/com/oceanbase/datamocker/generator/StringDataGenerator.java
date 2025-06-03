package com.oceanbase.datamocker.generator;

import com.oceanbase.datamocker.config.FieldConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 字符串类型数据生成器
 */
@Slf4j
public class StringDataGenerator extends AbstractDataGenerator {
    
    private static final String TYPE = "STRING";
    private static final String[] CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".split("");
    private static final int DEFAULT_MIN_LENGTH = 5;
    private static final int DEFAULT_MAX_LENGTH = 20;
    
    @Override
    public boolean supports(String fieldType) {
        return TYPE.equalsIgnoreCase(fieldType) || "VARCHAR".equalsIgnoreCase(fieldType) 
                || "CHAR".equalsIgnoreCase(fieldType) || "TEXT".equalsIgnoreCase(fieldType);
    }
    
    @Override
    protected Object doGenerate(String fieldName, FieldConfig fieldConfig) {
        // 如果有枚举值，从枚举值中随机选择
        if (fieldConfig.getEnumValues() != null && fieldConfig.getEnumValues().length > 0) {
            int index = random.nextInt(fieldConfig.getEnumValues().length);
            return fieldConfig.getEnumValues()[index];
        }
        
        // 如果有正则表达式模式，使用正则表达式生成
        if (fieldConfig.getPattern() != null && !fieldConfig.getPattern().isEmpty()) {
            try {
                // 这里简化处理，实际应该使用专门的正则表达式数据生成库
                // 例如 Xeger 或 Generex
                log.warn("Regex pattern generation is not fully implemented yet, using random string instead");
                return generateRandomString(fieldConfig);
            } catch (Exception e) {
                log.error("Failed to generate string with pattern: {}", fieldConfig.getPattern(), e);
                return generateRandomString(fieldConfig);
            }
        }
        
        // 默认生成随机字符串
        return generateRandomString(fieldConfig);
    }
    
    /**
     * 生成随机字符串
     *
     * @param fieldConfig 字段配置
     * @return 随机字符串
     */
    private String generateRandomString(FieldConfig fieldConfig) {
        int minLength = fieldConfig.getMinLength() != null ? fieldConfig.getMinLength() : DEFAULT_MIN_LENGTH;
        int maxLength = fieldConfig.getMaxLength() != null ? fieldConfig.getMaxLength() : DEFAULT_MAX_LENGTH;
        
        if (minLength < 0) {
            minLength = DEFAULT_MIN_LENGTH;
        }
        
        if (maxLength < minLength) {
            maxLength = minLength + DEFAULT_MAX_LENGTH;
        }
        
        int length = minLength;
        if (maxLength > minLength) {
            length = minLength + random.nextInt(maxLength - minLength + 1);
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS[random.nextInt(CHARS.length)]);
        }
        
        return sb.toString();
    }
} 