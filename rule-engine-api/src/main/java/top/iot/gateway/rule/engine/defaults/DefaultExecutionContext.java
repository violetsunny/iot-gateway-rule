package top.iot.gateway.rule.engine.defaults;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.iot.gateway.core.event.EventBus;
import top.iot.gateway.rule.engine.api.Slf4jLogger;
import top.iot.gateway.rule.engine.api.scheduler.ScheduleJob;
import top.iot.gateway.rule.engine.api.scope.GlobalScope;
import top.iot.gateway.rule.engine.api.task.CompositeOutput;
import top.iot.gateway.rule.engine.api.task.ConditionEvaluator;

import java.util.stream.Collectors;

@Getter
@Slf4j
public class DefaultExecutionContext extends AbstractExecutionContext {

    public DefaultExecutionContext(String workerId,
                                   ScheduleJob scheduleJob,
                                   EventBus eventBus,
                                   ConditionEvaluator evaluator,
                                   GlobalScope scope) {
        super(workerId, scheduleJob,
              eventBus,
              new Slf4jLogger("rule.engine." + scheduleJob.getInstanceId() + "." + scheduleJob.getNodeId()),
              job -> new EventBusInput(job.getInstanceId(), job.getNodeId(), eventBus),
              job -> new EventBusOutput(job.getInstanceId(), eventBus, job.getOutputs(), evaluator),
              job -> job.getEventOutputs()
                        .stream()
                        .map(event -> new EventBusEventOutput(job.getInstanceId(), eventBus, event.getType(), event.getSource()))
                        .collect(Collectors.groupingBy(EventBusEventOutput::getEvent, Collectors.collectingAndThen(Collectors.toList(), CompositeOutput::of))),
              scope
        );
    }

}
