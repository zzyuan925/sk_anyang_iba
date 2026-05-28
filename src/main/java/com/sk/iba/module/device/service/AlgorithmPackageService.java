package com.sk.iba.module.device.service;

import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.device.dto.AlgorithmPackageCreateDTO;
import com.sk.iba.module.device.dto.AlgorithmPackageQueryDTO;
import com.sk.iba.module.device.dto.AlgorithmPackageUpdateDTO;
import com.sk.iba.module.device.vo.AlgorithmPackageVO;

/**
 * 算法包 Service
 *
 * @author zzy
 */
public interface AlgorithmPackageService {

    /**
     * 分页查询算法包
     */
    PageResult<AlgorithmPackageVO> pageAlgorithmPackages(AlgorithmPackageQueryDTO queryDTO);

    /**
     * 查询算法包详情
     */
    AlgorithmPackageVO getAlgorithmPackageById(Long id);

    /**
     * 上传算法包
     */
    Long createAlgorithmPackage(AlgorithmPackageCreateDTO createDTO);

    /**
     * 修改算法包
     */
    void updateAlgorithmPackage(AlgorithmPackageUpdateDTO updateDTO);

    /**
     * 删除算法包
     */
    void deleteAlgorithmPackage(Long id);
}