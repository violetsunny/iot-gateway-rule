package top.iot.gateway.rule.engine.cluster.worker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.iot.gateway.core.cluster.ClusterManager;
import top.iot.gateway.rule.engine.api.RuleConstants;
import top.iot.gateway.rule.engine.api.RuleData;
import top.iot.gateway.rule.engine.api.task.Output;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class QueueEventOutput implements Output {
    private final String instanceId;

    private final ClusterManager clusterManager;

    @Getter
    private final String event;

    private final String sourceNode;

    @Override
    public Mono<Boolean> write(Publisher<RuleData> dataStream) {
        return clusterManager.<RuleData>getQueue(createTopic(sourceNode)).add(dataStream);
    }

    @Override
    public Mono<Void> write(String nodeId, Publisher<RuleData> data) {
        return clusterManager.<RuleData>getQueue(createTopic(nodeId)).add(data).then();
    }

    private String createTopic(String node) {
        return RuleConstants.Topics.input(instanceId, node);
    }
}
