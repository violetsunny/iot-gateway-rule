package top.iot.gateway.rule.engine.cluster.worker;

import lombok.Getter;
import top.iot.gateway.core.cluster.ClusterManager;
import top.iot.gateway.core.event.EventBus;
import top.iot.gateway.rule.engine.api.scheduler.ScheduleJob;
import top.iot.gateway.rule.engine.api.task.ConditionEvaluator;
import top.iot.gateway.rule.engine.api.task.Task;
import top.iot.gateway.rule.engine.api.task.TaskExecutorProvider;
import top.iot.gateway.rule.engine.api.worker.Worker;
import top.iot.gateway.rule.engine.defaults.DefaultTask;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterWorker implements Worker {

    private final Map<String, TaskExecutorProvider> executors = new ConcurrentHashMap<>();

    @Getter
    private final String id;

    @Getter
    private final String name;

    private final EventBus eventBus;

    private final RuleIOManager ruleIOManager;

    public ClusterWorker(String id,
                         String name,
                         EventBus eventBus,
                         ClusterManager clusterManager,
                         ConditionEvaluator evaluator) {
        this(id, name, eventBus, new ClusterRuleIOManager(clusterManager, evaluator));
    }

    public ClusterWorker(String id,
                         String name,
                         EventBus eventBus,
                         RuleIOManager ioManager) {
        this.id = id;
        this.name = name;
        this.eventBus = eventBus;
        this.ruleIOManager = ioManager;
    }

    @Override
    public Mono<Task> createTask(String schedulerId, ScheduleJob job) {
        return Mono
                .justOrEmpty(executors.get(job.getExecutor()))
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("unsupported executor:" + job.getExecutor())))
                .flatMap(provider -> {
                    ClusterExecutionContext context = createContext(job);
                    return provider
                            .createTask(context)
                            .map(executor -> new DefaultTask(schedulerId, this.getId(), context, executor));
                });
    }

    protected ClusterExecutionContext createContext(ScheduleJob job) {
        return new ClusterExecutionContext(getId(), job, eventBus, ruleIOManager);
    }

    @Override
    public Mono<List<String>> getSupportExecutors() {

        return Mono.just(new ArrayList<>(executors.keySet()));
    }

    @Override
    public Mono<State> getState() {
        return Mono.just(State.working);
    }

    public void addExecutor(TaskExecutorProvider provider) {
        executors.put(provider.getExecutor(), provider);
    }
}
