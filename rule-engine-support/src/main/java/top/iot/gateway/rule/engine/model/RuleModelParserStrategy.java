package top.iot.gateway.rule.engine.model;

import top.iot.gateway.rule.engine.api.model.RuleModel;

/**
 * 模型解析器策略
 *
 * @author zhouhao
 * @since 1.0.0
 */
public interface RuleModelParserStrategy {

    String getFormat();

    RuleModel parse(String modelDefineString);
}
