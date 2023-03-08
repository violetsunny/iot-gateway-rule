package top.iot.gateway.rule.engine.condition;

import top.iot.gateway.core.utils.Reactors;
import top.iot.gateway.rule.engine.api.task.ConditionEvaluator;
import top.iot.gateway.rule.engine.api.RuleData;
import top.iot.gateway.rule.engine.api.model.Condition;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author zhouhao
 * @since 1.0.0
 */
public class DefaultConditionEvaluator implements ConditionEvaluator {

    private final Map<String, ConditionEvaluatorStrategy> allStrategy = new HashMap<>();

    @Override
    public boolean evaluate(Condition condition, RuleData context) {
        if (condition == null || StringUtils.isEmpty(condition)) {
            return true;
        }
        return Optional.ofNullable(allStrategy.get(condition.getType()))
                       .map(strategy -> strategy.evaluate(condition, context))
                       .orElseThrow(() -> new UnsupportedOperationException("不支持的条件类型:" + condition.getType()));
    }

    @Override
    public Function<RuleData, Mono<Boolean>> prepare(Condition condition) {
        if (condition == null || StringUtils.isEmpty(condition.getConfiguration())) {
            return ignore -> Reactors.ALWAYS_TRUE;
        }
        return Optional
                .ofNullable(allStrategy.get(condition.getType()))
                .map(strategy -> strategy.prepare(condition))
                .orElseThrow(() -> new UnsupportedOperationException("不支持的条件类型:" + condition.getType()));
    }

    public void register(ConditionEvaluatorStrategy strategy) {
        allStrategy.put(strategy.getType(), strategy);
    }
}
