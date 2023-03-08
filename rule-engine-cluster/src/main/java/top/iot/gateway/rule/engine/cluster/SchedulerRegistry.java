package top.iot.gateway.rule.engine.cluster;

import top.iot.gateway.rule.engine.api.scheduler.Scheduler;
import reactor.core.publisher.Flux;

import java.util.List;

public interface SchedulerRegistry {


    List<Scheduler> getLocalSchedulers();

    /**
     * 获取全部Scheduler
     *
     * @return scheduler流
     */
    Flux<Scheduler> getSchedulers();

    /**
     * 监听Scheduler加入注册中心事件
     *
     * @return scheduler流
     */
    Flux<Scheduler> handleSchedulerJoin();

    /**
     * 监听Scheduler掉线事件
     *
     * @return scheduler流
     */
    Flux<Scheduler> handleSchedulerLeave();

    /**
     * 注册Scheduler到注册中心
     *
     * @param scheduler scheduler
     */
    void register(Scheduler scheduler);

}
