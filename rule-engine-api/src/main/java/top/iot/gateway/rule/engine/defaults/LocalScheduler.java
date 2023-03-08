package top.iot.gateway.rule.engine.defaults;

import lombok.Getter;
import lombok.Setter;
import top.iot.gateway.rule.engine.api.scheduler.ScheduleJob;
import top.iot.gateway.rule.engine.api.scheduler.Scheduler;
import top.iot.gateway.rule.engine.api.task.Task;
import top.iot.gateway.rule.engine.api.worker.Worker;
import top.iot.gateway.rule.engine.api.worker.WorkerSelector;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class LocalScheduler implements Scheduler {

    @Getter
    private final String id;

    @Setter
    private WorkerSelector workerSelector = defaultSelector;

    final static WorkerSelector defaultSelector = (workers1, rule) -> workers1.take(1);

    private final Map<String/*workerId*/, Worker> workers = new ConcurrentHashMap<>();

    private final Map<String/*规则实例ID*/, Map<String/*nodeId*/, List<Task>>> executors = new ConcurrentHashMap<>();

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    public LocalScheduler(String id) {
        this.id = id;
    }

    @Override
    public Flux<Worker> getWorkers() {
        return Flux.fromIterable(workers.values());
    }

    @Override
    public Mono<Worker> getWorker(String workerId) {

        return Mono.justOrEmpty(workers.get(workerId));
    }

    @Override
    public Mono<Boolean> canSchedule(ScheduleJob job) {
        return findWorker(job.getExecutor(), job)
                .hasElements();
    }

    protected Flux<Worker> findWorker(String executor, ScheduleJob schedulingRule) {
        return workerSelector
                .select(Flux.fromIterable(workers.values())
                            .filterWhen(exe -> exe
                                    .getSupportExecutors()
                                    .map(list -> list.contains(executor))
                                    .defaultIfEmpty(false)), schedulingRule);
    }

    @Override
    public Flux<Task> schedule(ScheduleJob job) {

        return Flux
                .fromIterable(getExecutor(job.getInstanceId(), job.getNodeId()))
                .flatMap(task -> {
                    //停止旧任务
                    removeTask(task);
                    return task.shutdown();
                })
                .thenMany(createExecutor(job));
    }

    @Override
    public Mono<Void> shutdown(String instanceId) {
        return getSchedulingTask(instanceId)
                .doOnNext(task -> tasks.remove(task.getId()))
                .concatMapDelayError(Task::shutdown)
                .doAfterTerminate(() -> clearExecutor(instanceId))
                .then();
    }

    @Override
    public Mono<Void> shutdownTask(String taskId) {
        Task task = removeTask(taskId);
        if (null != task) {
            return task.shutdown().then();
        }
        return Mono.empty();
    }

    private Flux<Task> createExecutor(ScheduleJob job) {
        return findWorker(job.getExecutor(), job)
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("unsupported executor:" + job.getExecutor())))
                .flatMap(worker -> worker.createTask(id, job))
                .doOnNext(this::addTask);
    }

    private void addTask(Task task) {
        tasks.put(task.getId(), task);
        getExecutor(task.getJob().getInstanceId(), task.getJob().getNodeId()).add(task);
    }

    private Task removeTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            removeTask(task);
        }
        return task;
    }

    private void removeTask(Task task) {
        tasks.remove(task.getId());
        getExecutor(task.getJob().getInstanceId(), task.getJob().getNodeId()).remove(task);
    }

    @Override
    public Flux<Task> getSchedulingTask(String instanceId) {
        return Flux.fromIterable(getExecutor(instanceId).values())
                   .flatMapIterable(Function.identity());
    }

    @Override
    public Mono<Task> getTask(String taskId) {
        return Mono.justOrEmpty(tasks.get(taskId));
    }

    @Override
    public Flux<Task> getSchedulingTasks() {
        return Flux.fromIterable(executors.values())
                   .flatMapIterable(Map::values)
                   .flatMapIterable(Function.identity());
    }

    @Override
    public Mono<Long> totalTask() {
        return getSchedulingTasks().count();
    }

    private List<Task> getExecutor(String instanceId, String nodeId) {
        return getExecutor(instanceId).computeIfAbsent(nodeId, ignore -> new CopyOnWriteArrayList<>());
    }

    private void clearExecutor(String instanceId) {
        executors.remove(instanceId);
    }

    private Map<String, List<Task>> getExecutor(String instanceId) {
        return executors.computeIfAbsent(instanceId, ignore -> new ConcurrentHashMap<>());
    }

    public void addWorker(Worker worker) {
        this.workers.put(worker.getId(), worker);
    }
}
