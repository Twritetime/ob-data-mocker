package com.oceanbase.datamocker.ai;

import com.oceanbase.datamocker.config.MockerConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 语义推断器工厂类
 */
@Slf4j
public class SemanticInferrerFactory {
    
    /**
     * 创建语义推断器
     *
     * @param config 配置
     * @return 语义推断器
     */
    public static FieldSemanticInferrer createInferrer(MockerConfig config) {
        if (config == null) {
            log.warn("No config provided, using rule-based inferrer");
            return createRuleBasedInferrer();
        }
        
        if (!config.isEnableAiInference()) {
            log.info("AI inference disabled by config, using rule-based inferrer");
            return createRuleBasedInferrer();
        }
        
        try {
            log.info("Creating AI-based semantic inferrer");
            FieldSemanticInferrer inferrer = new AiSemanticInferrer(config.getAiModelConfig());
            inferrer.initialize();
            return inferrer;
        } catch (Exception e) {
            log.error("Failed to create AI-based inferrer: {}", e.getMessage(), e);
            log.warn("Falling back to rule-based inferrer");
            return createRuleBasedInferrer();
        }
    }
    
    /**
     * 创建基于规则的语义推断器
     *
     * @return 基于规则的语义推断器
     */
    public static FieldSemanticInferrer createRuleBasedInferrer() {
        log.info("Creating rule-based semantic inferrer");
        FieldSemanticInferrer inferrer = new RuleBasedSemanticInferrer();
        inferrer.initialize();
        return inferrer;
    }
} 