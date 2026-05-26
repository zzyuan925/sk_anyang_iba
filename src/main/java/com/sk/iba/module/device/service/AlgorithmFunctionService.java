package com.sk.iba.module.device.service;

import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.device.dto.FunctionCreateDTO;
import com.sk.iba.module.device.dto.FunctionQueryDTO;
import com.sk.iba.module.device.dto.FunctionUpdateDTO;
import com.sk.iba.module.device.vo.FunctionOptionVO;
import com.sk.iba.module.device.vo.FunctionVO;

import java.util.List;

/**
 * @author zzy
 */
public interface AlgorithmFunctionService {

    /**
     * 分页查询算法功能
     */
    PageResult<FunctionVO> pageFunctions(FunctionQueryDTO queryDTO);

    /**
     * 查询算法功能详情
     */
    FunctionVO getFunctionById(Long id);

    /**
     * 新增算法功能
     */
    Long createFunction(FunctionCreateDTO createDTO);

    /**
     * 修改算法功能
     */
    void updateFunction(FunctionUpdateDTO updateDTO);

    /**
     * 删除算法功能
     */
    void deleteFunction(Long id);

    /**
     * 查询启用算法功能下拉选项
     */
    List<FunctionOptionVO> listEnabledFunctionOptions();
}