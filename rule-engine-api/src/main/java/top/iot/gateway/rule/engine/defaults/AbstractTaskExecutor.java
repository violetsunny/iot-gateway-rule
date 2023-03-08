package top.iot.gateway.rule.engine.defaults;

import io.opentelemetry.api.common.AttributeKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.iot.gateway.core.trace.MonoTracer;
import top.iot.gateway.rule.engine.api.RuleData;
import top.iot.gateway.rule.engine.api.task.ExecutableTaskExecutor;
import top.iot.gateway.rule.engine.api.task.ExecutionContext;
import top.iot.gateway.rule.engine.api.task.Task;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.function.BiConsumer;

@Slf4j
public abstract class AbstractTaskExecutor implements ExecutableTaskExecutor {
    protected final static AttributeKey<String> executor_name = AttributeKey.stringKey("name");

    @Getter
    protected ExecutionContext context;

    @Getter
    protected volatile Task.State state = Task.State.shutdown;

    protected volatile Disposable disposable;
    private final MonoTracer<Object> tracer;

    private BiConsumer<Task.State, Task.State> stateListener = (from, to) -> {
        AbstractTaskExecutor.log.debug("task [{}] state changed from {} to {}.",
                                       context.getJob(),
                                       from,
                                       to);
    };

    public AbstractTaskExecutor(ExecutionContext context) {
        this.context = context;
        this.tracer = MonoTracer
                .create("/rule-runtime/" + context.getJob().getExecutor() +
                                "/" + context.getInstanceId() +
                                "/" + context.getJob().getNodeId(),
                        span -> span.setAttribute(executor_name, this.getName()));
    }

    @SuppressWarnings("all")
    protected <T> MonoTracer<T> tracer() {
        return (MonoTracer) tracer;
    }

    @Override
    public abstract String getName();

    protected abstract Disposable doStart();

    protected void changeState(Task.State state) {
        if (this.state == state) {
            return;
        }
        stateListener.accept(this.state, this.state = state);
    }

    @Override
    public synchronized void start() {
        if (disposable != null && !disposable.isDisposed()) {
            changeState(Task.State.running);
            return;
        }
        disposable = doStart();
        changeState(Task.State.running);
    }

    @Override
    public void reload() {

    }

    @Override
    public void pause() {
        changeState(Task.State.paused);
    }

    @Override
    public synchronized void shutdown() {
        changeState(Task.State.shutdown);
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public void onStateChanged(BiConsumer<Task.State, Task.State> listener) {
        this.stateListener = this.stateListener.andThen(listener);
    }

    @Override
    public void validate() {

    }

    @Override
    public Mono<Void> execute(RuleData ruleData) {
        return context
                .getOutput()
                .write(ruleData)
                .as(tracer())
                .then()
                ;
    }
}
