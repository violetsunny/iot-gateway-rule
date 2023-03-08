package top.iot.gateway.rule.engine.condition.supports;

import java.util.Map;

/**
 * @author zhouhao
 * @since 1.0.0
 */
public interface ScriptEvaluator {
    Object evaluate(String lang, String script, Map<String, Object> context) throws Exception;

}
