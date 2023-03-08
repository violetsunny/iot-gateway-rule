package top.iot.gateway.rule.engine.cluster.worker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.iot.gateway.rule.engine.api.scheduler.ScheduleJob;
import top.iot.gateway.rule.engine.api.task.Task;
import top.iot.gateway.rule.engine.api.worker.Worker;
import top.iot.gateway.rule.engine.cluster.scheduler.SchedulerRpcService;
import top.iot.gateway.rule.engine.cluster.task.RemoteTask;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
public class RemoteWorker implements Worker {

    @Getter
    private final String id;

    @Getter
    private final String name;

    private final SchedulerRpcService rpcService;

    @Override
    public Mono<Task> createTask(String schedulerId, ScheduleJob job) {
        return rpcService
                .createTask(SchedulerRpcService.CreateTaskRequest.of(id,job))
                .map(response -> new RemoteTask(
                        response.getId(),
                        response.getName(),
                        id,
                        schedulerId,
                        rpcService,
                        job
                ));
    }

    @Override
    public Mono<List<String>> getSupportExecutors() {
        return rpcService
                .getSupportExecutors(id);
    }

    @Override
    public Mono<State> getState() {
        return rpcService
                .getWorkerState(id);
    }
}
