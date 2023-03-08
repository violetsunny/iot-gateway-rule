package top.iot.gateway.rule.engine.cluster.worker;

import top.iot.gateway.rule.engine.api.scheduler.ScheduleJob;
import top.iot.gateway.rule.engine.api.scope.GlobalScope;
import top.iot.gateway.rule.engine.api.task.Input;
import top.iot.gateway.rule.engine.api.task.Output;

import java.util.Map;

public interface RuleIOManager {

    Input createInput(ScheduleJob job);

    Output createOutput(ScheduleJob job);

    GlobalScope createScope();

    Map<String, Output> createEvent(ScheduleJob job);

}
