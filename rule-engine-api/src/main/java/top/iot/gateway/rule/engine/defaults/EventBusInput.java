package top.iot.gateway.rule.engine.defaults;

import lombok.AllArgsConstructor;
import top.iot.gateway.core.event.EventBus;
import top.iot.gateway.core.event.Subscription;
import top.iot.gateway.rule.engine.api.RuleConstants;
import top.iot.gateway.rule.engine.api.RuleData;
import top.iot.gateway.rule.engine.api.task.Input;
import reactor.core.publisher.Flux;

@AllArgsConstructor
public class EventBusInput implements Input {

    private final String instanceId;

    private final String nodeId;

    private final EventBus bus;

    @Override
    public Flux<RuleData> accept() {

        return bus.subscribe(Subscription.of("rule-engine:" + nodeId, RuleConstants.Topics.input(instanceId, nodeId),Subscription.Feature.values()), RuleData.class);
    }

}
