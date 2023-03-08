package top.iot.gateway.rule.engine.cluster.scheduler;

import io.scalecube.services.annotations.Service;
import io.scalecube.services.annotations.ServiceMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.iot.gateway.rule.engine.api.RuleData;
import top.iot.gateway.rule.engine.api.scheduler.ScheduleJob;
import top.iot.gateway.rule.engine.api.task.Task;
import top.iot.gateway.rule.engine.api.task.TaskSnapshot;
import top.iot.gateway.rule.engine.api.worker.Worker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;

@Service
public interface SchedulerRpcService {

    @ServiceMethod
    Mono<String >getId();

    @ServiceMethod
    Flux<WorkerInfo> getWorkers();

    @ServiceMethod
    Mono<WorkerInfo> getWorker(String id);

    @ServiceMethod
    Flux<TaskInfo> schedule(ScheduleJob job);

    @ServiceMethod
    Mono<Void> shutdown(String instanceId);

    @ServiceMethod
    Flux<TaskInfo> getSchedulingTask(String instanceId);

    @ServiceMethod
    Mono<TaskInfo> getTask(String taskId);

    @ServiceMethod
    Flux<TaskInfo> getSchedulingTasks();

    @ServiceMethod
    Mono<Long> totalTask();

    @ServiceMethod
    Mono<Boolean> canSchedule(ScheduleJob job);

    @ServiceMethod
    Mono<Void> executeTask(ExecuteTaskRequest request);

    @ServiceMethod
    Mono<Task.State> getTaskState(String taskId);

    @ServiceMethod
    Mono<Void> taskOperation(OperateTaskRequest request);

    @ServiceMethod
    Mono<Void> setTaskJob(TaskJobRequest request);

    @ServiceMethod
    Mono<Long> getLastStateTime(String taskId);

    @ServiceMethod
    Mono<Long> getStartTime(String taskId);

    @ServiceMethod
    Mono<TaskInfo> createTask(CreateTaskRequest request);

    @ServiceMethod
    Mono<List<String>> getSupportExecutors(String workerId);

    @ServiceMethod
    Mono<Worker.State> getWorkerState(String workerId);

    @ServiceMethod
    Mono<Boolean> isAlive();

    @ServiceMethod
    Mono<TaskSnapshot> dumpTask(String taskId);

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    class WorkerInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private String id;

        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    class TaskInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private String id;

        private String name;

        private String workerId;

        private ScheduleJob job;

    }

    enum TaskOperation {
        START,
        PAUSE,
        RELOAD,
        SHUTDOWN,
        ENABLE_DEBUG,
        DISABLE_DEBUG
    }

    @Getter
    @Setter
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    class CreateTaskRequest implements Serializable {
        private static final long serialVersionUID = 1L;

        private String workerId;
        private ScheduleJob job;
    }

    @Getter
    @Setter
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    class TaskJobRequest implements Serializable {
        private static final long serialVersionUID = 1L;

        private String taskId;
        private ScheduleJob job;
    }

    @Getter
    @Setter
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    class ExecuteTaskRequest implements Serializable {
        private static final long serialVersionUID = 1L;

        private String taskId;
        private RuleData data;
    }

    @Getter
    @Setter
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    class OperateTaskRequest implements Serializable {
        private static final long serialVersionUID = 1L;

        private String taskId;
        private TaskOperation operation;
    }
}
