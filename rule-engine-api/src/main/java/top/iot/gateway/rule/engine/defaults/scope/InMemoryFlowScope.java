package top.iot.gateway.rule.engine.defaults.scope;

import top.iot.gateway.rule.engine.api.scope.ContextScope;
import top.iot.gateway.rule.engine.api.scope.FlowScope;
import top.iot.gateway.rule.engine.api.scope.NodeScope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryFlowScope extends InMemoryPersistenceScope implements FlowScope {

    private final Map<String, NodeScope> nodeScopeMap = new ConcurrentHashMap<>();
    private final Map<String, ContextScope> contextScopeMap = new ConcurrentHashMap<>();


    @Override
    public NodeScope node(String id) {
        return nodeScopeMap.computeIfAbsent(id, k -> new InMemoryNodeScope());
    }

    @Override
    public ContextScope context(String id) {
        return contextScopeMap.computeIfAbsent(id, k -> new InMemoryContextScope());
    }
}
