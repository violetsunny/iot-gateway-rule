package top.iot.gateway.rule.engine.cluster.scope;

import lombok.AllArgsConstructor;
import top.iot.gateway.core.cluster.ClusterCounter;
import top.iot.gateway.rule.engine.api.scope.ScopeCounter;
import reactor.core.publisher.Mono;

@AllArgsConstructor
class ClusterScopeCounter implements ScopeCounter {

    private final ClusterCounter counter;

    @Override
    public Mono<Double> inc(double n) {
        return counter.increment(n);
    }

    @Override
    public Mono<Double> dec(double n) {
        return counter.decrement(n);
    }

    @Override
    public Mono<Double> get() {
        return counter.get();
    }

    @Override
    public Mono<Double> set(double value) {
        return counter.set(value);
    }

    @Override
    public Mono<Double> setAndGet(double value) {
        return counter.setAndGet(value);
    }

    @Override
    public Mono<Double> getAndSet(double value) {
        return counter.getAndSet(value);
    }
}
