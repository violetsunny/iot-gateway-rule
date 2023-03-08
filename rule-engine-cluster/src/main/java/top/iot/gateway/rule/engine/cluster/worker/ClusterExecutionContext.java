package top.iot.gateway.rule.engine.cluster.worker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.iot.gateway.core.cluster.ClusterManager;
import top.iot.gateway.core.event.EventBus;
import top.iot.gateway.rule.engine.api.Slf4jLogger;
import top.iot.gateway.rule.engine.api.scheduler.ScheduleJob;
import top.iot.gateway.rule.engine.api.task.CompositeOutput;
import top.iot.gateway.rule.engine.api.task.ConditionEvaluator;
import top.iot.gateway.rule.engine.cluster.scope.ClusterGlobalScope;
import top.iot.gateway.rule.engine.defaults.AbstractExecutionContext;

import java.util.stream.Collectors;

@Getter
@Slf4j
public class ClusterExecutionContext extends AbstractExecutionContext {

    public ClusterExecutionContext(String workerId,
                                   ScheduleJob scheduleJob,
                                   EventBus eventBus,
                                   ClusterManager clusterManager,
                                   ConditionEvaluator evaluator) {
        super(workerId,
              scheduleJob,
              eventBus,
              new Slf4jLogger("rule.engine." + scheduleJob.getInstanceId() + "." + scheduleJob.getNodeId()),
              job -> new QueueInput(job.getInstanceId(), job.getNodeId(), clusterManager),
              job -> new QueueOutput(job.getInstanceId(), clusterManager, job.getOutputs(), evaluator),
              job -> job.getEventOutputs()
                        .stream()
                        .map(event -> new QueueEventOutput(job.getInstanceId(), clusterManager, event.getType(), event.getSource()))
                        .collect(Collectors.groupingBy(QueueEventOutput::getEvent, Collectors.collectingAndThen(Collectors.toList(), CompositeOutput::of))),
              new ClusterGlobalScope(clusterManager)
        );
    }

    public ClusterExecutionContext(String workerId,
                                   ScheduleJob scheduleJob,
                                   EventBus eventBus,
                                   RuleIOManager manager) {
        super(workerId,
              scheduleJob,
              eventBus,
              new Slf4jLogger("rule.engine." + scheduleJob.getInstanceId() + "." + scheduleJob.getNodeId()),
              manager::createInput,
              manager::createOutput,
              manager::createEvent,
              manager.createScope()
        );
    }

}
