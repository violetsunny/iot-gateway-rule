package top.iot.gateway.rule.engine.cluster.worker;

import lombok.AllArgsConstructor;
import top.iot.gateway.core.cluster.ClusterManager;
import top.iot.gateway.rule.engine.api.scheduler.ScheduleJob;
import top.iot.gateway.rule.engine.api.scope.GlobalScope;
import top.iot.gateway.rule.engine.api.task.CompositeOutput;
import top.iot.gateway.rule.engine.api.task.ConditionEvaluator;
import top.iot.gateway.rule.engine.api.task.Input;
import top.iot.gateway.rule.engine.api.task.Output;
import top.iot.gateway.rule.engine.cluster.scope.ClusterGlobalScope;

import java.util.Map;
import java.util.stream.Collectors;


@AllArgsConstructor
public class ClusterRuleIOManager implements RuleIOManager {

    private final ClusterManager clusterManager;

    private final ConditionEvaluator evaluator;

    @Override
    public Input createInput(ScheduleJob job) {
        return new QueueInput(job.getInstanceId(),job.getNodeId(),clusterManager);
    }

    @Override
    public Output createOutput(ScheduleJob job) {
        return new QueueOutput(job.getInstanceId(),clusterManager,job.getOutputs(),evaluator);
    }

    @Override
    public Map<String, Output> createEvent(ScheduleJob job) {
        return job.getEventOutputs()
                  .stream()
                  .map(event -> new QueueEventOutput(job.getInstanceId(), clusterManager, event.getType(), event.getSource()))
                  .collect(Collectors.groupingBy(QueueEventOutput::getEvent, Collectors.collectingAndThen(Collectors.toList(), CompositeOutput::of)));
    }

    @Override
    public GlobalScope createScope() {
        return new ClusterGlobalScope(clusterManager);
    }
}
