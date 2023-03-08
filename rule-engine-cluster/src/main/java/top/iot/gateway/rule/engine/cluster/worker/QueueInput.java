package top.iot.gateway.rule.engine.cluster.worker;

import lombok.AllArgsConstructor;
import top.iot.gateway.core.cluster.ClusterManager;
import top.iot.gateway.core.cluster.ClusterQueue;
import top.iot.gateway.rule.engine.api.RuleConstants;
import top.iot.gateway.rule.engine.api.RuleData;
import top.iot.gateway.rule.engine.api.task.Input;
import reactor.core.publisher.Flux;

@AllArgsConstructor
public class QueueInput implements Input {

    private final String instanceId;

    private final String nodeId;

    private final ClusterManager clusterManager;

    @Override
    public Flux<RuleData> accept() {
        ClusterQueue<RuleData> input = clusterManager.getQueue(RuleConstants.Topics.input(instanceId, nodeId));


        return input.subscribe();
    }

}
