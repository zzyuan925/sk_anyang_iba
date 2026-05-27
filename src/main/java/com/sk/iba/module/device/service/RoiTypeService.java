package com.sk.iba.module.device.service;

import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.device.dto.RoiTypeCreateDTO;
import com.sk.iba.module.device.dto.RoiTypeQueryDTO;
import com.sk.iba.module.device.dto.RoiTypeUpdateDTO;
import com.sk.iba.module.device.vo.RoiTypeOptionVO;
import com.sk.iba.module.device.vo.RoiTypeVO;

import java.util.List;

/**
 * @author zzy
 */
public interface RoiTypeService {

    /**
     * 分页查询ROI类型
     */
    PageResult<RoiTypeVO> pageRoiTypes(RoiTypeQueryDTO queryDTO);

    /**
     * 查询ROI类型详情
     */
    RoiTypeVO getRoiTypeById(Long id);

    /**
     * 新增ROI类型
     */
    Long createRoiType(RoiTypeCreateDTO createDTO);

    /**
     * 修改ROI类型
     */
    void updateRoiType(RoiTypeUpdateDTO updateDTO);

    /**
     * 删除ROI类型
     */
    void deleteRoiType(Long id);

    /**
     * 查询启用ROI类型下拉选项
     */
    List<RoiTypeOptionVO> listEnabledRoiTypeOptions();
}