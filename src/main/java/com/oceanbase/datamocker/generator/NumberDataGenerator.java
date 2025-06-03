package com.oceanbase.datamocker.generator;

import com.oceanbase.datamocker.config.FieldConfig;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * 数值类型数据生成器
 */
@Slf4j
public class NumberDataGenerator extends AbstractDataGenerator {
    
    private static final List<String> SUPPORTED_TYPES = Arrays.asList(
            "INT", "INTEGER", "SMALLINT", "TINYINT", "BIGINT", 
            "FLOAT", "DOUBLE", "DECIMAL", "NUMBER", "NUMERIC"
    );
    
    private static final double DEFAULT_MIN = 0;
    private static final double DEFAULT_MAX = 1000;
    
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
            String value = fieldConfig.getEnumValues()[index];
            return parseNumber(value, type);
        }
        
        double min = DEFAULT_MIN;
        double max = DEFAULT_MAX;
        
        // 解析最小值和最大值
        if (fieldConfig.getMin() != null && !fieldConfig.getMin().isEmpty()) {
            try {
                min = Double.parseDouble(fieldConfig.getMin());
            } catch (NumberFormatException e) {
                log.warn("Invalid min value: {}, using default", fieldConfig.getMin());
            }
        }
        
        if (fieldConfig.getMax() != null && !fieldConfig.getMax().isEmpty()) {
            try {
                max = Double.parseDouble(fieldConfig.getMax());
            } catch (NumberFormatException e) {
                log.warn("Invalid max value: {}, using default", fieldConfig.getMax());
            }
        }
        
        // 确保最小值小于最大值
        if (min >= max) {
            max = min + DEFAULT_MAX;
        }
        
        // 根据分布类型生成数值
        double value;
        switch (fieldConfig.getDistributionType()) {
            case NORMAL:
                // 正态分布，使用均值和标准差
                double mean = (min + max) / 2;
                double stdDev = (max - min) / 6; // 约95%的值在均值±3倍标准差范围内
                value = random.nextGaussian() * stdDev + mean;
                // 确保值在范围内
                value = Math.max(min, Math.min(max, value));
                break;
            case EXPONENTIAL:
                // 指数分布
                double lambda = 1.0 / ((max - min) / 5); // 设置合适的lambda值
                value = min + (-Math.log(1 - random.nextDouble()) / lambda);
                // 确保值在范围内
                value = Math.min(max, value);
                break;
            case UNIFORM:
            default:
                // 均匀分布
                value = min + (max - min) * random.nextDouble();
                break;
        }
        
        // 根据字段类型返回适当的数值类型
        return formatNumberByType(value, type);
    }
    
    /**
     * 根据字段类型格式化数值
     *
     * @param value 数值
     * @param type 字段类型
     * @return 格式化后的数值对象
     */
    private Object formatNumberByType(double value, String type) {
        switch (type) {
            case "INT":
            case "INTEGER":
                return (int) Math.round(value);
            case "SMALLINT":
                return (short) Math.round(value);
            case "TINYINT":
                return (byte) Math.round(value);
            case "BIGINT":
                return Math.round(value);
            case "FLOAT":
                return (float) value;
            case "DOUBLE":
                return value;
            case "DECIMAL":
            case "NUMBER":
            case "NUMERIC":
                // 对于DECIMAL类型，保留2位小数
                return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
            default:
                return value;
        }
    }
    
    /**
     * 将字符串解析为数值
     *
     * @param value 字符串值
     * @param type 字段类型
     * @return 解析后的数值对象
     */
    private Object parseNumber(String value, String type) {
        try {
            switch (type) {
                case "INT":
                case "INTEGER":
                    return Integer.parseInt(value);
                case "SMALLINT":
                    return Short.parseShort(value);
                case "TINYINT":
                    return Byte.parseByte(value);
                case "BIGINT":
                    return Long.parseLong(value);
                case "FLOAT":
                    return Float.parseFloat(value);
                case "DOUBLE":
                    return Double.parseDouble(value);
                case "DECIMAL":
                case "NUMBER":
                case "NUMERIC":
                    return new BigDecimal(value);
                default:
                    return Double.parseDouble(value);
            }
        } catch (NumberFormatException e) {
            log.warn("Failed to parse number: {}, using default", value);
            return formatNumberByType(DEFAULT_MIN, type);
        }
    }
} 