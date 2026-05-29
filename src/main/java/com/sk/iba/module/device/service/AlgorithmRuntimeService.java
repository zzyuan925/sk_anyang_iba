package com.sk.iba.module.device.service;

import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.device.dto.AlgorithmRuntimeDeployDTO;
import com.sk.iba.module.device.dto.AlgorithmRuntimeQueryDTO;
import com.sk.iba.module.device.vo.AlgorithmRuntimeVO;

/**
 * 算法运行 Service
 *
 * @author zzy
 */
public interface AlgorithmRuntimeService {

    /**
     * 分页查询算法运行列表
     */
    PageResult<AlgorithmRuntimeVO> pageAlgorithmRuntimes(AlgorithmRuntimeQueryDTO queryDTO);

    /**
     * 部署算法包
     */
    Long deployAlgorithmPackage(AlgorithmRuntimeDeployDTO deployDTO);

    /**
     * 启动算法
     */
    void startAlgorithm(Long id);

    /**
     * 停止算法
     */
    void stopAlgorithm(Long id);
}