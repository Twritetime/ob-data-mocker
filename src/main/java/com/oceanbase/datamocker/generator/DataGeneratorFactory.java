package com.oceanbase.datamocker.generator;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 数据生成器工厂类
 */
@Slf4j
public class DataGeneratorFactory {
    
    private static final List<DataGenerator> generators = new ArrayList<>();
    
    static {
        // 注册内置生成器
        registerGenerator(new StringDataGenerator());
        registerGenerator(new NumberDataGenerator());
        registerGenerator(new DateTimeDataGenerator());
        
        // 加载通过SPI机制注册的生成器
        ServiceLoader<DataGenerator> serviceLoader = ServiceLoader.load(DataGenerator.class);
        for (DataGenerator generator : serviceLoader) {
            registerGenerator(generator);
        }
    }
    
    /**
     * 注册数据生成器
     *
     * @param generator 数据生成器
     */
    public static void registerGenerator(DataGenerator generator) {
        generators.add(generator);
        log.debug("Registered data generator: {}", generator.getClass().getName());
    }
    
    /**
     * 获取支持指定字段类型的生成器
     *
     * @param fieldType 字段类型
     * @return 数据生成器，如果没有找到合适的生成器，返回默认的字符串生成器
     */
    public static DataGenerator getGenerator(String fieldType) {
        for (DataGenerator generator : generators) {
            if (generator.supports(fieldType)) {
                return generator;
            }
        }
        
        // 如果没有找到合适的生成器，返回默认的字符串生成器
        log.warn("No suitable generator found for field type: {}, using default string generator", fieldType);
        return new StringDataGenerator();
    }
} 