package io.jboot.components.limiter;

public enum LimitScope {

    /**
     * 整个集群限次，多实例共享
     */
    CLUSTER,

    /**
     * 每个实例单独限次
     */
    NODE;

}
