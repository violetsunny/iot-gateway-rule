package top.iot.gateway.rule.engine.cluster.scope;

import top.iot.gateway.core.cluster.ClusterManager;
import top.iot.gateway.rule.engine.api.scope.NodeScope;

class ClusterNodeScope extends ClusterPersistenceScope implements NodeScope {
    public ClusterNodeScope(String id, ClusterManager clusterManager) {
        super(id, clusterManager);
    }
}
