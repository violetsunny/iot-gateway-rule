package top.iot.gateway.rule.engine.defaults;

import lombok.AllArgsConstructor;
import top.iot.gateway.rule.engine.api.RuleEngine;
import top.iot.gateway.rule.engine.api.scheduler.Scheduler;
import top.iot.gateway.rule.engine.api.task.Task;
import top.iot.gateway.rule.engine.api.model.RuleModel;
import top.iot.gateway.rule.engine.api.worker.Worker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@AllArgsConstructor
public class DefaultRuleEngine implements RuleEngine {

    //调度器
    private final Scheduler scheduler;

    public Flux<Task> startRule(String instanceId,
                                RuleModel model) {
        return Flux.fromIterable(new ScheduleJobCompiler(instanceId, model).compile())
                .flatMap(scheduler::schedule)
                .collectList()
                .flatMapIterable(Function.identity())
                .flatMap(task -> task.start().thenReturn(task));
    }

    public Flux<Task> getTasks(String instanceId) {
        return scheduler.getSchedulingTask(instanceId);
    }

    public Mono<Void> shutdown(String instanceId) {
        return scheduler.shutdown(instanceId);
    }

    @Override
    public Flux<Worker> getWorkers() {
        return scheduler.getWorkers();
    }
}
