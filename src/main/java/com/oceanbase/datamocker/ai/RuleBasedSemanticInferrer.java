package com.oceanbase.datamocker.ai;

import com.oceanbase.datamocker.config.FieldConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 基于规则的语义推断器
 * 通过预定义的规则和模式匹配来推断字段语义
 */
@Slf4j
public class RuleBasedSemanticInferrer implements FieldSemanticInferrer {
    
    private static final Map<Pattern, SemanticType> NAME_PATTERNS = new HashMap<>();
    private static final Map<String, SemanticType> SQL_TYPE_MAPPINGS = new HashMap<>();
    
    static {
        // 初始化名称模式
        NAME_PATTERNS.put(Pattern.compile("(?i).*name.*"), SemanticType.NAME);
        NAME_PATTERNS.put(Pattern.compile("(?i).*first.*name.*"), SemanticType.NAME);
        NAME_PATTERNS.put(Pattern.compile("(?i).*last.*name.*"), SemanticType.NAME);
        NAME_PATTERNS.put(Pattern.compile("(?i).*full.*name.*"), SemanticType.NAME);
        NAME_PATTERNS.put(Pattern.compile("(?i).*user.*name.*"), SemanticType.USERNAME);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*email.*"), SemanticType.EMAIL);
        NAME_PATTERNS.put(Pattern.compile("(?i).*mail.*"), SemanticType.EMAIL);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*address.*"), SemanticType.ADDRESS);
        NAME_PATTERNS.put(Pattern.compile("(?i).*addr.*"), SemanticType.ADDRESS);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*phone.*"), SemanticType.PHONE);
        NAME_PATTERNS.put(Pattern.compile("(?i).*mobile.*"), SemanticType.PHONE);
        NAME_PATTERNS.put(Pattern.compile("(?i).*tel.*"), SemanticType.PHONE);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*age.*"), SemanticType.AGE);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*gender.*"), SemanticType.GENDER);
        NAME_PATTERNS.put(Pattern.compile("(?i).*sex.*"), SemanticType.GENDER);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*birth.*date.*"), SemanticType.DATE);
        NAME_PATTERNS.put(Pattern.compile("(?i).*birthday.*"), SemanticType.DATE);
        NAME_PATTERNS.put(Pattern.compile("(?i).*dob.*"), SemanticType.DATE);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*create.*time.*"), SemanticType.DATETIME);
        NAME_PATTERNS.put(Pattern.compile("(?i).*update.*time.*"), SemanticType.DATETIME);
        NAME_PATTERNS.put(Pattern.compile("(?i).*modify.*time.*"), SemanticType.DATETIME);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*amount.*"), SemanticType.AMOUNT);
        NAME_PATTERNS.put(Pattern.compile("(?i).*price.*"), SemanticType.AMOUNT);
        NAME_PATTERNS.put(Pattern.compile("(?i).*fee.*"), SemanticType.AMOUNT);
        NAME_PATTERNS.put(Pattern.compile("(?i).*cost.*"), SemanticType.AMOUNT);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*percent.*"), SemanticType.PERCENTAGE);
        NAME_PATTERNS.put(Pattern.compile("(?i).*ratio.*"), SemanticType.PERCENTAGE);
        NAME_PATTERNS.put(Pattern.compile("(?i).*rate.*"), SemanticType.PERCENTAGE);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*company.*"), SemanticType.COMPANY);
        NAME_PATTERNS.put(Pattern.compile("(?i).*corp.*"), SemanticType.COMPANY);
        NAME_PATTERNS.put(Pattern.compile("(?i).*enterprise.*"), SemanticType.COMPANY);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*job.*"), SemanticType.JOB_TITLE);
        NAME_PATTERNS.put(Pattern.compile("(?i).*position.*"), SemanticType.JOB_TITLE);
        NAME_PATTERNS.put(Pattern.compile("(?i).*title.*"), SemanticType.JOB_TITLE);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*country.*"), SemanticType.COUNTRY);
        NAME_PATTERNS.put(Pattern.compile("(?i).*nation.*"), SemanticType.COUNTRY);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*city.*"), SemanticType.CITY);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*province.*"), SemanticType.PROVINCE_STATE);
        NAME_PATTERNS.put(Pattern.compile("(?i).*state.*"), SemanticType.PROVINCE_STATE);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*zip.*"), SemanticType.POSTAL_CODE);
        NAME_PATTERNS.put(Pattern.compile("(?i).*postal.*code.*"), SemanticType.POSTAL_CODE);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*ip.*"), SemanticType.IP_ADDRESS);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*url.*"), SemanticType.URL);
        NAME_PATTERNS.put(Pattern.compile("(?i).*link.*"), SemanticType.URL);
        NAME_PATTERNS.put(Pattern.compile("(?i).*website.*"), SemanticType.URL);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*color.*"), SemanticType.COLOR);
        NAME_PATTERNS.put(Pattern.compile("(?i).*colour.*"), SemanticType.COLOR);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*password.*"), SemanticType.PASSWORD);
        NAME_PATTERNS.put(Pattern.compile("(?i).*pwd.*"), SemanticType.PASSWORD);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*id.*"), SemanticType.IDENTIFIER);
        NAME_PATTERNS.put(Pattern.compile("(?i).*code.*"), SemanticType.IDENTIFIER);
        
        NAME_PATTERNS.put(Pattern.compile("(?i).*desc.*"), SemanticType.DESCRIPTION);
        NAME_PATTERNS.put(Pattern.compile("(?i).*description.*"), SemanticType.DESCRIPTION);
        NAME_PATTERNS.put(Pattern.compile("(?i).*remark.*"), SemanticType.DESCRIPTION);
        NAME_PATTERNS.put(Pattern.compile("(?i).*comment.*"), SemanticType.DESCRIPTION);
        
        // 初始化SQL类型映射
        SQL_TYPE_MAPPINGS.put("DATE", SemanticType.DATE);
        SQL_TYPE_MAPPINGS.put("TIME", SemanticType.TIME);
        SQL_TYPE_MAPPINGS.put("DATETIME", SemanticType.DATETIME);
        SQL_TYPE_MAPPINGS.put("TIMESTAMP", SemanticType.DATETIME);
    }
    
    @Override
    public SemanticType inferSemanticType(String fieldName, String sqlType) {
        if (fieldName == null || fieldName.isEmpty()) {
            return SemanticType.UNKNOWN;
        }
        
        // 首先根据字段名进行匹配
        for (Map.Entry<Pattern, SemanticType> entry : NAME_PATTERNS.entrySet()) {
            if (entry.getKey().matcher(fieldName).matches()) {
                log.debug("Field '{}' matched pattern for semantic type: {}", fieldName, entry.getValue());
                return entry.getValue();
            }
        }
        
        // 如果字段名没有匹配，尝试根据SQL类型进行匹配
        if (sqlType != null && !sqlType.isEmpty()) {
            SemanticType typeFromSql = SQL_TYPE_MAPPINGS.get(sqlType.toUpperCase());
            if (typeFromSql != null) {
                log.debug("Field '{}' inferred semantic type from SQL type {}: {}", fieldName, sqlType, typeFromSql);
                return typeFromSql;
            }
        }
        
        // 如果都没有匹配，返回未知类型
        log.debug("Field '{}' with SQL type {} has unknown semantic type", fieldName, sqlType);
        return SemanticType.UNKNOWN;
    }
    
    @Override
    public FieldConfig generateFieldConfig(String fieldName, String sqlType, SemanticType semanticType) {
        FieldConfig config = new FieldConfig();
        config.setType(sqlType);
        
        // 根据语义类型设置适当的配置
        switch (semanticType) {
            case NAME:
                configureNameField(config);
                break;
            case EMAIL:
                configureEmailField(config);
                break;
            case PHONE:
                configurePhoneField(config);
                break;
            case ADDRESS:
                configureAddressField(config);
                break;
            case AGE:
                configureAgeField(config);
                break;
            case GENDER:
                configureGenderField(config);
                break;
            case DATE:
                configureDateField(config);
                break;
            case DATETIME:
                configureDateTimeField(config);
                break;
            case AMOUNT:
                configureAmountField(config);
                break;
            case PERCENTAGE:
                configurePercentageField(config);
                break;
            default:
                // 对于其他类型，保持默认配置
                break;
        }
        
        return config;
    }
    
    private void configureNameField(FieldConfig config) {
        config.setType("STRING");
        config.setMinLength(2);
        config.setMaxLength(50);
    }
    
    private void configureEmailField(FieldConfig config) {
        config.setType("STRING");
        config.setPattern("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }
    
    private void configurePhoneField(FieldConfig config) {
        config.setType("STRING");
        // 简单的电话号码模式
        config.setPattern("^\\d{11}$");
    }
    
    private void configureAddressField(FieldConfig config) {
        config.setType("STRING");
        config.setMinLength(10);
        config.setMaxLength(200);
    }
    
    private void configureAgeField(FieldConfig config) {
        config.setType("INT");
        config.setMin("0");
        config.setMax("120");
    }
    
    private void configureGenderField(FieldConfig config) {
        config.setType("STRING");
        config.setEnumValues(new String[]{"男", "女", "未知"});
    }
    
    private void configureDateField(FieldConfig config) {
        config.setType("DATE");
        config.setMin("1900-01-01");
        config.setMax("2030-12-31");
    }
    
    private void configureDateTimeField(FieldConfig config) {
        config.setType("DATETIME");
        config.setMin("1900-01-01T00:00:00");
        config.setMax("2030-12-31T23:59:59");
    }
    
    private void configureAmountField(FieldConfig config) {
        config.setType("DECIMAL");
        config.setMin("0");
        config.setMax("1000000");
    }
    
    private void configurePercentageField(FieldConfig config) {
        config.setType("DECIMAL");
        config.setMin("0");
        config.setMax("100");
    }
    
    @Override
    public void initialize() {
        // 规则推断器不需要特殊初始化
        log.info("Rule-based semantic inferrer initialized");
    }
    
    @Override
    public void close() {
        // 规则推断器不需要特殊清理
        log.info("Rule-based semantic inferrer closed");
    }
} 