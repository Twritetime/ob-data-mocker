package com.oceanbase.datamocker.generator;

import com.oceanbase.datamocker.config.FieldConfig;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * 日期时间类型数据生成器
 */
@Slf4j
public class DateTimeDataGenerator extends AbstractDataGenerator {
    
    private static final List<String> SUPPORTED_TYPES = Arrays.asList(
            "DATE", "TIME", "DATETIME", "TIMESTAMP"
    );
    
    private static final String DEFAULT_MIN_DATE = "1970-01-01";
    private static final String DEFAULT_MAX_DATE = "2030-12-31";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Override
    public boolean supports(String fieldType) {
        return SUPPORTED_TYPES.contains(fieldType.toUpperCase());
    }
    
    @Override
    protected Object doGenerate(String fieldName, FieldConfig fieldConfig) {
        String type = fieldConfig.getType().toUpperCase();
        
        // 如果有枚举值，从枚举值中随机选择
        if (fieldConfig.getEnumValues() != null && fieldConfig.getEnumValues().length > 0) {
            int index = random.nextInt(fieldConfig.getEnumValues().length);
            return fieldConfig.getEnumValues()[index];
        }
        
        // 根据类型生成不同的日期时间
        switch (type) {
            case "DATE":
                return generateDate(fieldConfig);
            case "TIME":
                return generateTime(fieldConfig);
            case "DATETIME":
            case "TIMESTAMP":
                return generateDateTime(fieldConfig);
            default:
                return generateDateTime(fieldConfig);
        }
    }
    
    /**
     * 生成日期
     *
     * @param fieldConfig 字段配置
     * @return 日期字符串
     */
    private String generateDate(FieldConfig fieldConfig) {
        LocalDate minDate = parseDate(fieldConfig.getMin(), DEFAULT_MIN_DATE);
        LocalDate maxDate = parseDate(fieldConfig.getMax(), DEFAULT_MAX_DATE);
        
        // 确保最小日期在最大日期之前
        if (minDate.isAfter(maxDate)) {
            maxDate = minDate.plusYears(10);
        }
        
        // 计算日期范围内的天数
        long minEpochDay = minDate.toEpochDay();
        long maxEpochDay = maxDate.toEpochDay();
        long randomDay = minEpochDay + (long) (random.nextDouble() * (maxEpochDay - minEpochDay));
        
        // 生成随机日期
        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
        return randomDate.format(DATE_FORMATTER);
    }
    
    /**
     * 生成时间
     *
     * @param fieldConfig 字段配置
     * @return 时间字符串
     */
    private String generateTime(FieldConfig fieldConfig) {
        // 生成0-23小时
        int hour = random.nextInt(24);
        // 生成0-59分钟
        int minute = random.nextInt(60);
        // 生成0-59秒
        int second = random.nextInt(60);
        
        LocalTime time = LocalTime.of(hour, minute, second);
        return time.format(TIME_FORMATTER);
    }
    
    /**
     * 生成日期时间
     *
     * @param fieldConfig 字段配置
     * @return 日期时间字符串
     */
    private String generateDateTime(FieldConfig fieldConfig) {
        LocalDate date = LocalDate.parse(generateDate(fieldConfig), DATE_FORMATTER);
        LocalTime time = LocalTime.parse(generateTime(fieldConfig), TIME_FORMATTER);
        
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * 解析日期字符串
     *
     * @param dateStr 日期字符串
     * @param defaultDateStr 默认日期字符串
     * @return 解析后的LocalDate对象
     */
    private LocalDate parseDate(String dateStr, String defaultDateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return LocalDate.parse(defaultDateStr, DATE_FORMATTER);
        }
        
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("Invalid date format: {}, using default", dateStr);
            return LocalDate.parse(defaultDateStr, DATE_FORMATTER);
        }
    }
} 