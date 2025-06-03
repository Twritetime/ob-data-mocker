package com.oceanbase.datamocker.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 字段配置类，用于定义单个字段的生成规则
 */
@Data
public class FieldConfig {
    
    /**
     * 字段生成类型
     */
    private String type;
    
    /**
     * 是否允许为空
     */
    private boolean allowNull = false;
    
    /**
     * 空值比例，取值范围0-1
     */
    private double nullRate = 0.0;
    
    /**
     * 最小值（适用于数值、日期类型）
     */
    private String min;
    
    /**
     * 最大值（适用于数值、日期类型）
     */
    private String max;
    
    /**
     * 字符串最小长度
     */
    private Integer minLength;
    
    /**
     * 字符串最大长度
     */
    private Integer maxLength;
    
    /**
     * 正则表达式模式（用于生成符合特定格式的数据）
     */
    private String pattern;
    
    /**
     * 枚举值列表（用于从固定集合中随机选择）
     */
    private String[] enumValues;
    
    /**
     * 数据分布类型（如NORMAL、UNIFORM等）
     */
    private DistributionType distributionType = DistributionType.UNIFORM;
    
    /**
     * 分布参数（如正态分布的均值、标准差等）
     */
    private Map<String, Object> distributionParams = new HashMap<>();
    
    /**
     * 自定义生成器类名
     */
    private String customGeneratorClass;
    
    /**
     * 自定义生成器参数
     */
    private Map<String, Object> customGeneratorParams = new HashMap<>();
    
    /**
     * 数据分布类型枚举
     */
    public enum DistributionType {
        /**
         * 均匀分布
         */
        UNIFORM,
        
        /**
         * 正态分布
         */
        NORMAL,
        
        /**
         * 指数分布
         */
        EXPONENTIAL,
        
        /**
         * 泊松分布
         */
        POISSON,
        
        /**
         * 自定义分布
         */
        CUSTOM
    }
} 