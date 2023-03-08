package top.iot.gateway.rule.engine.cluster.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.iot.gateway.rule.engine.api.RuleData;
import top.iot.gateway.rule.engine.api.scheduler.ScheduleJob;
import top.iot.gateway.rule.engine.api.task.Task;
import top.iot.gateway.rule.engine.api.task.TaskSnapshot;
import top.iot.gateway.rule.engine.cluster.scheduler.SchedulerRpcService;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class RemoteTask implements Task {
    @Getter
    private final String id;

    @Getter
    private final String name;

    @Getter
    private final String workerId;

    @Getter
    private final String schedulerId;

    @Getter
    private final SchedulerRpcService rpcService;

    @Getter
    private ScheduleJob job;

    @Override
    public Mono<Void> setJob(ScheduleJob job) {
        this.job = job;
        return rpcService
                .setTaskJob(SchedulerRpcService.TaskJobRequest.of(id, job));
    }

    private Mono<Void> operation(SchedulerRpcService.TaskOperation operation) {
        return rpcService
                .taskOperation(SchedulerRpcService.OperateTaskRequest.of(id, operation));
    }

    @Override
    public Mono<Void> reload() {
        return operation(SchedulerRpcService.TaskOperation.RELOAD);
    }

    @Override
    public Mono<Void> start() {
        return operation(SchedulerRpcService.TaskOperation.START);
    }

    @Override
    public Mono<Void> pause() {
        return operation(SchedulerRpcService.TaskOperation.PAUSE);
    }

    @Override
    public Mono<Void> shutdown() {
        return operation(SchedulerRpcService.TaskOperation.SHUTDOWN);
    }

    @Override
    public Mono<Void> execute(RuleData data) {
        return rpcService.executeTask(SchedulerRpcService.ExecuteTaskRequest.of(id, data)).then();
    }

    @Override
    public Mono<State> getState() {
        return rpcService
                .getTaskState(id);
    }

    @Override
    public Mono<Void> debug(boolean debug) {
        return operation(debug ? SchedulerRpcService.TaskOperation.ENABLE_DEBUG : SchedulerRpcService.TaskOperation.DISABLE_DEBUG);
    }

    @Override
    public Mono<Long> getLastStateTime() {
        return rpcService.getLastStateTime(id);
    }

    @Override
    public Mono<Long> getStartTime() {
        return rpcService.getStartTime(id);
    }

    @Override
    public Mono<TaskSnapshot> dump() {
        return rpcService.dumpTask(id);
    }
}
