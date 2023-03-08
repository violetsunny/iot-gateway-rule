package top.iot.gateway.rule.engine.cluster;

import lombok.Getter;
import lombok.Setter;
import top.iot.gateway.rule.engine.api.model.RuleModel;

/**
 * 规则实例信息
 *
 * @author zhouhao
 * @since 1.1.7
 */
@Getter
@Setter
public class RuleInstance {
    /**
     * 实例ID
     */
    private String id;

    /**
     * 模型
     */
    private RuleModel model;
}
