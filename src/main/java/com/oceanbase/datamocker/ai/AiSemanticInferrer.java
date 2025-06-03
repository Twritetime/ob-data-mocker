package com.oceanbase.datamocker.ai;

import com.oceanbase.datamocker.config.FieldConfig;
import com.oceanbase.datamocker.config.MockerConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于AI的语义推断器
 * 使用预训练的NLP模型推断字段语义
 * 注意：当前版本仅为演示，实际上使用规则推断
 */
@Slf4j
public class AiSemanticInferrer implements FieldSemanticInferrer {
    
    private final MockerConfig.AiModelConfig modelConfig;
    private final RuleBasedSemanticInferrer fallbackInferrer;
    
    public AiSemanticInferrer(MockerConfig.AiModelConfig modelConfig) {
        this.modelConfig = modelConfig;
        this.fallbackInferrer = new RuleBasedSemanticInferrer();
    }
    
    @Override
    public SemanticType inferSemanticType(String fieldName, String sqlType) {
        // 当前版本直接使用规则推断
        log.info("AI inference is not implemented yet, using rule-based inference for field: {}", fieldName);
        return fallbackInferrer.inferSemanticType(fieldName, sqlType);
    }
    
    @Override
    public FieldConfig generateFieldConfig(String fieldName, String sqlType, SemanticType semanticType) {
        // 对于配置生成，我们仍然使用规则推断器的逻辑
        return fallbackInferrer.generateFieldConfig(fieldName, sqlType, semanticType);
    }
    
    @Override
    public void initialize() {
        log.info("Initializing AI semantic inferrer (demo version)");
        fallbackInferrer.initialize();
    }
    
    @Override
    public void close() {
        log.info("Closing AI semantic inferrer");
        fallbackInferrer.close();
    }
} 