package com.sk.iba.module.system.service;

import com.sk.iba.module.system.dto.RegionCreateDTO;
import com.sk.iba.module.system.dto.RegionQueryDTO;
import com.sk.iba.module.system.dto.RegionUpdateDTO;
import com.sk.iba.module.system.vo.RegionOptionVO;
import com.sk.iba.module.system.vo.RegionTreeVO;
import com.sk.iba.module.system.vo.RegionVO;

import java.util.List;

/**
 * @author zzy
 */
public interface SysRegionService {

    /**
     * 树形查询区域
     */
    List<RegionTreeVO> treeRegions(RegionQueryDTO queryDTO);

    /**
     * 查询区域详情
     */
    RegionVO getRegionById(Long id);

    /**
     * 新增区域
     */
    Long createRegion(RegionCreateDTO createDTO);

    /**
     * 修改区域
     */
    void updateRegion(RegionUpdateDTO updateDTO);

    /**
     * 删除区域
     */
    void deleteRegion(Long id);

    /**
     * 查询启用区域下拉选项
     */
    List<RegionOptionVO> listEnabledRegionOptions();
}