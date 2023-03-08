package top.iot.gateway.rule.engine.condition;

import top.iot.gateway.rule.engine.api.RuleData;
import top.iot.gateway.rule.engine.api.model.Condition;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * @author zhouhao
 * @since 1.0.0
 */
public interface ConditionEvaluatorStrategy {

    String getType();

    boolean evaluate(Condition condition, RuleData context);

    /**
     * prepare
     * @param condition prepare
     * @return prepare
     */
    default Function<RuleData, Mono<Boolean>> prepare(Condition condition) {
        return (data) -> Mono.just(evaluate(condition, data));
    }
}
