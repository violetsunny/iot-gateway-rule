package top.iot.gateway.rule.engine.api.scheduler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 调度器选择器,根据任务,从多个调度器中选择调度器来执行此任务
 *
 * @author zhouhao
 * @since 1.1
 */
public interface SchedulerSelector {

    SchedulerSelector selectAll = (scheduler, job) -> scheduler;

    /**
     * 选择调度器,可通过实现此方法来进行任务负载均衡,或者根据任务指定的调度规则来选择不同的调度器
     *
     * @param schedulers 所有可用调度器
     * @param job        任务信息
     * @return 执行此任务的调度器
     */
    Flux<Scheduler> select(Flux<Scheduler> schedulers, ScheduleJob job);

    /**
     * 测试调度器是否可以调度此任务
     *
     * @param scheduler 调度器
     * @param job       任务
     * @return 是否可以调度
     * @since 1.1.7
     */
    default Mono<Boolean> test(Scheduler scheduler, ScheduleJob job) {
        return this
                .select(Flux.just(scheduler), job)
                .hasElements();
    }
}
