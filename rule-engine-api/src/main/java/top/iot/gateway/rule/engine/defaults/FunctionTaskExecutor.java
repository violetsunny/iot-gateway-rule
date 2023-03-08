package top.iot.gateway.rule.engine.defaults;

import lombok.Getter;
import top.iot.gateway.core.trace.TraceHolder;
import top.iot.gateway.rule.engine.api.RuleConstants;
import top.iot.gateway.rule.engine.api.RuleData;
import top.iot.gateway.rule.engine.api.task.ExecutionContext;
import top.iot.gateway.rule.engine.api.task.Task;
import top.iot.gateway.rule.engine.api.task.TaskExecutor;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public abstract class FunctionTaskExecutor extends AbstractTaskExecutor implements TaskExecutor {

    @Getter
    private final String name;

    public FunctionTaskExecutor(String name, ExecutionContext context) {
        super(context);
        this.name = name;
    }

    protected abstract Publisher<RuleData> apply(RuleData input);

    private Mono<Void> doApply(RuleData input) {
        return context
                .getOutput()
                .write(Flux.from(this.apply(input))
                           .flatMap(output -> context
                                   .fireEvent(RuleConstants.Event.result, output)
                                   .thenReturn(output)))
                .then(context.fireEvent(RuleConstants.Event.complete, input))
                .as(tracer())
                .onErrorResume(error -> context.onError(error, input))
                .contextWrite(TraceHolder.readToContext(Context.empty(), input.getHeaders()))
                .then();
    }

    @Override
    public final Mono<Void> execute(RuleData ruleData) {
        return doApply(ruleData);
    }

    @Override
    protected Disposable doStart() {
        return context
                .getInput()
                .accept()
                .filter(data -> state == Task.State.running)
                // FIXME: 2021/9/3 背压支持？
                .flatMap(this::doApply, Integer.MAX_VALUE)
                .onErrorResume(error -> context.onError(error, null))
                .subscribe()
                ;
    }


}
