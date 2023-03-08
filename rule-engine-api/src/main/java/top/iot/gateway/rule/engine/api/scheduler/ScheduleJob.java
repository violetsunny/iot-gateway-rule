package top.iot.gateway.rule.engine.api.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;
import top.iot.gateway.rule.engine.api.model.Condition;
import top.iot.gateway.rule.engine.api.model.RuleLink;
import top.iot.gateway.rule.engine.api.model.RuleModel;
import top.iot.gateway.rule.engine.api.model.RuleNodeModel;
import top.iot.gateway.rule.engine.api.task.TaskExecutorProvider;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.*;

/**
 * 调度任务,在规则发布时,会将规则节点{@link RuleNodeModel}转为任务,发送给对应的调度器{@link Scheduler}进行调度执行
 *
 * @author zhouhao
 * @since 1.0
 */
@Getter
@Setter
public class ScheduleJob implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 规则实例ID
     */
    @Nonnull
    private String instanceId;

    /**
     * 规则ID
     *
     * @see RuleNodeModel#getRuleId()
     */
    @Nonnull
    private String ruleId;

    /**
     * 节点ID
     *
     * @see RuleNodeModel#getId()
     */
    @Nonnull
    private String nodeId;

    @Nonnull
    private String modelType;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 执行器
     *
     * @see TaskExecutorProvider#getExecutor()
     * @see RuleNodeModel#getExecutor()
     */
    @Nonnull
    private String executor;

    /**
     * 执行器配置信息
     *
     * @see RuleNodeModel#getConfiguration()
     */
    private Map<String, Object> configuration;

    /**
     * 规则配置信息
     *
     * @see RuleModel#getConfiguration()
     */
    private Map<String, Object> ruleConfiguration;

    /**
     * 输入节点
     *
     * @see RuleNodeModel#getId()
     * @see RuleNodeModel#getInputs()
     */
    private List<String> inputs = new ArrayList<>();

    /**
     * 监听事件输入
     */
    private List<Event> events = new ArrayList<>();

    /**
     * 监听事件输出
     */
    private List<Event> eventOutputs = new ArrayList<>();

    /**
     * 输出节点
     */
    private List<Output> outputs = new ArrayList<>();

    /**
     * 上下文
     */
    private Map<String, Object> context = new HashMap<>();

    /**
     * 调度规则
     */
    private SchedulingRule schedulingRule;

    public Optional<Object> getRuleConfiguration(String key) {
        if (MapUtils.isEmpty(ruleConfiguration)) {
            return Optional.empty();
        }
        return Optional.ofNullable(ruleConfiguration.get(key));
    }

    public Optional<Object> getConfiguration(String key) {
        if (MapUtils.isEmpty(configuration)) {
            return Optional.empty();
        }
        return Optional.ofNullable(configuration.get(key));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Event implements Serializable {

        /**
         * 事件类型
         *
         * @see RuleLink#getType()
         */
        @Nonnull
        private String type;

        /**
         * 事件源
         *
         * @see RuleNodeModel#getId()
         * @see RuleLink#getSource()
         */
        @Nonnull
        private String source;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output implements Serializable {
        /**
         * 输出节点
         *
         * @see RuleNodeModel#getId()
         */
        @Nonnull
        private String output;

        /**
         * 输出条件,满足条件才输出
         */
        private Condition condition;
    }

    @Override
    public String toString() {
        return instanceId + ":" + nodeId + "(" + executor + ")";
    }
}
