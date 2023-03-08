package top.iot.gateway.rule.engine.api.task;

import lombok.AllArgsConstructor;
import top.iot.gateway.rule.engine.api.RuleData;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor(staticName = "of")
public class CompositeOutput implements Output {

    private final List<Output> outputs;

    @Override
    public Mono<Boolean> write(Publisher<RuleData> dataStream) {
        return Flux.from(dataStream)
                .flatMap(data -> Flux.fromIterable(outputs)
                        .flatMap(out -> out.write(data)))
                .reduce(Boolean::equals)
                ;
    }

    @Override
    public Mono<Void> write(String nodeId, Publisher<RuleData> dataStream) {
        return Flux.from(dataStream)
                .flatMap(data -> Flux.fromIterable(outputs)
                        .flatMap(out -> out.write(nodeId, Mono.just(data))))
                .then();
    }
}
