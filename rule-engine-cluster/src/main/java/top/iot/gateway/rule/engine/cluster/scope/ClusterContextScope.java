package top.iot.gateway.rule.engine.cluster.scope;

import top.iot.gateway.core.cluster.ClusterCache;
import top.iot.gateway.core.cluster.ClusterManager;
import top.iot.gateway.rule.engine.api.scope.ContextScope;

class ClusterContextScope extends ClusterPersistenceScope implements ContextScope {
    public ClusterContextScope(String id, ClusterManager clusterManager) {
        super(id, clusterManager);
    }

    @Override
    protected ClusterCache<String, Object> getCache() {
        return clusterManager.createCache(getKey());
    }
}
