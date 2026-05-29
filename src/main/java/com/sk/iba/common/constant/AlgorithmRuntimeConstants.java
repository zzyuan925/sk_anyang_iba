package com.sk.iba.common.constant;

/**
 * 算法运行常量
 *
 * @author zzy
 */
public class AlgorithmRuntimeConstants {

    private AlgorithmRuntimeConstants() {
    }

    /**
     * 算法服务接口端口
     */
    public static final int ALGORITHM_API_PORT = 8900;

    /**
     * 未运行
     */
    public static final int RUN_STATUS_STOPPED = 0;

    /**
     * 运行中
     */
    public static final int RUN_STATUS_RUNNING = 1;

    /**
     * 运行异常
     */
    public static final int RUN_STATUS_ERROR = 2;
}