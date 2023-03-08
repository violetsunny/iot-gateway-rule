package top.iot.gateway.rule.engine.cluster.scope;

import top.iot.gateway.core.cluster.ClusterManager;
import top.iot.gateway.rule.engine.api.scope.FlowScope;
import top.iot.gateway.rule.engine.api.scope.GlobalScope;

public class ClusterGlobalScope extends ClusterPersistenceScope implements GlobalScope {

    public ClusterGlobalScope(ClusterManager clusterManager) {
        super("scope:global", clusterManager);
    }

    @Override
    public FlowScope flow(String id) {
        return new ClusterFlowScope("scope:flow:" + id, clusterManager);
    }

}
