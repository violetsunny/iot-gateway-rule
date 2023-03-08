package top.iot.gateway.rule.engine.cluster.scope;

import top.iot.gateway.core.cluster.ClusterManager;
import top.iot.gateway.rule.engine.api.scope.ContextScope;
import top.iot.gateway.rule.engine.api.scope.FlowScope;
import top.iot.gateway.rule.engine.api.scope.NodeScope;

class ClusterFlowScope extends ClusterPersistenceScope implements FlowScope {
    public ClusterFlowScope(String id, ClusterManager clusterManager) {
        super(id, clusterManager);
    }
    @Override
    public NodeScope node(String id) {
        return new ClusterNodeScope(this.id + ":node:" + id, clusterManager);
    }

    @Override
    public ContextScope context(String id) {
        return new ClusterContextScope(this.id + ":context:" + id, clusterManager);
    }
}
