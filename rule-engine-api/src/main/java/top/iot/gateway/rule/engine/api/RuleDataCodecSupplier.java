package top.iot.gateway.rule.engine.api;

public interface RuleDataCodecSupplier{

    boolean isSupport(Class type);

    RuleDataCodec getCodec();


    default int getOrder(){
        return 0;
    }

}
