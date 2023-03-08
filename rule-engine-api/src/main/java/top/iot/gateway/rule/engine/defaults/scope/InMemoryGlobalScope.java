package top.iot.gateway.rule.engine.defaults.scope;

import top.iot.gateway.rule.engine.api.scope.FlowScope;
import top.iot.gateway.rule.engine.api.scope.GlobalScope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGlobalScope extends InMemoryPersistenceScope implements GlobalScope {

    private final Map<String, FlowScope> flowScopeMap = new ConcurrentHashMap<>();

    @Override
    public FlowScope flow(String id) {
        return flowScopeMap.computeIfAbsent(id, ignore -> new InMemoryFlowScope());
    }
}
