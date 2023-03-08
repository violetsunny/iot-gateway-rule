package top.iot.gateway.rule.engine.defaults;

import lombok.extern.slf4j.Slf4j;
import top.iot.gateway.core.event.EventBus;
import top.iot.gateway.rule.engine.api.RuleData;
import top.iot.gateway.rule.engine.api.scheduler.ScheduleJob;
import top.iot.gateway.rule.engine.api.task.ConditionEvaluator;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class EventBusOutput extends AbstractOutput {

    private final EventBus eventBus;

    public EventBusOutput(String instanceId,
                          EventBus eventBus,
                          List<ScheduleJob.Output> outputs,
                          ConditionEvaluator evaluator) {
        super(instanceId, outputs, evaluator);
        this.eventBus = eventBus;
    }


    @Override
    protected Mono<Boolean> doWrite(String address, Publisher<RuleData> data) {
        return eventBus.publish(address, data).thenReturn(true);
    }

    @Override
    protected Mono<Boolean> doWrite(String address, RuleData data) {
        return eventBus.publish(address, data).thenReturn(true);
    }


}
