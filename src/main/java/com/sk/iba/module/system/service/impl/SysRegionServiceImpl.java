package com.sk.iba.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sk.iba.common.constant.SystemConstants;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.enums.StatusEnum;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.module.device.entity.Camera;
import com.sk.iba.module.device.mapper.CameraMapper;
import com.sk.iba.module.system.dto.RegionCreateDTO;
import com.sk.iba.module.system.dto.RegionQueryDTO;
import com.sk.iba.module.system.dto.RegionUpdateDTO;
import com.sk.iba.module.system.entity.SysRegion;
import com.sk.iba.module.system.entity.SysUserRegion;
import com.sk.iba.module.system.mapper.SysRegionMapper;
import com.sk.iba.module.system.mapper.SysUserRegionMapper;
import com.sk.iba.module.system.service.SysRegionService;
import com.sk.iba.module.system.vo.RegionOptionVO;
import com.sk.iba.module.system.vo.RegionTreeVO;
import com.sk.iba.module.system.vo.RegionVO;
import com.sk.iba.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class SysRegionServiceImpl implements SysRegionService {

    private final SysRegionMapper sysRegionMapper;

    private final SysUserRegionMapper sysUserRegionMapper;

    private final CameraMapper cameraMapper;

    @Override
    public List<RegionTreeVO> treeRegions(RegionQueryDTO queryDTO) {
        List<SysRegion> regions = sysRegionMapper.selectList(
                new LambdaQueryWrapper<SysRegion>()
                        .orderByAsc(SysRegion::getParentId)
                        .orderByAsc(SysRegion::getSort)
                        .orderByAsc(SysRegion::getId)
        );

        if (regions.isEmpty()) {
            return List.of();
        }

        Map<Long, SysRegion> regionMap = regions.stream()
                .collect(Collectors.toMap(SysRegion::getId, region -> region));

        Set<Long> visibleIds = filterVisibleRegionIds(regions, regionMap);
        Set<Long> finalVisibleIds = filterRegionQueryIds(regions, regionMap, queryDTO, visibleIds);

        List<RegionTreeVO> voList = regions.stream()
                .filter(region -> finalVisibleIds.contains(region.getId()))
                .map(this::toTreeVO)
                .toList();

        Map<Long, RegionTreeVO> voMap = voList.stream()
                .collect(Collectors.toMap(RegionTreeVO::getId, vo -> vo));

        List<RegionTreeVO> treeList = new ArrayList<>();

        for (RegionTreeVO vo : voList) {
            Long parentId = vo.getParentId();

            if (parentId == null || SystemConstants.ROOT_PARENT_ID.equals(parentId) || !voMap.containsKey(parentId)) {
                treeList.add(vo);
                continue;
            }

            RegionTreeVO parent = voMap.get(parentId);
            parent.getChildren().add(vo);
        }

        return treeList;
    }

    @Override
    public RegionVO getRegionById(Long id) {
        SysRegion region = sysRegionMapper.selectById(id);
        if (region == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "区域不存在");
        }

        checkRegionDataPermission(region);
        return toVO(region);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRegion(RegionCreateDTO createDTO) {
        String regionName = createDTO.getRegionName().trim();
        String regionCode = createDTO.getRegionCode().trim();
        Long parentId = createDTO.getParentId() == null
                ? SystemConstants.ROOT_PARENT_ID
                : createDTO.getParentId();

        if (!SystemConstants.ROOT_PARENT_ID.equals(parentId)) {
            SysRegion parent = sysRegionMapper.selectById(parentId);
            if (parent == null) {
                throw new BusinessException("父级区域不存在");
            }
            checkRegionDataPermission(parent);
        }

        Long count = sysRegionMapper.selectCount(new LambdaQueryWrapper<SysRegion>()
                .eq(SysRegion::getRegionCode, regionCode));

        if (count > 0) {
            throw new BusinessException("区域编码已存在");
        }

        SysRegion region = new SysRegion();
        BeanUtils.copyProperties(createDTO, region);
        region.setParentId(parentId);
        region.setRegionName(regionName);
        region.setRegionCode(regionCode);

        if (region.getSort() == null) {
            region.setSort(0);
        }
        if (region.getStatus() == null) {
            region.setStatus(StatusEnum.ENABLED.getCode());
        }
        if (!StatusEnum.isValid(region.getStatus())) {
            throw new BusinessException("区域状态不合法");
        }

        sysRegionMapper.insert(region);
        return region.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRegion(RegionUpdateDTO updateDTO) {
        SysRegion oldRegion = sysRegionMapper.selectById(updateDTO.getId());
        if (oldRegion == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "区域不存在");
        }

        checkRegionDataPermission(oldRegion);

        if (updateDTO.getRegionName() != null && !StringUtils.hasText(updateDTO.getRegionName())) {
            throw new BusinessException("区域名称不能为空");
        }

        if (updateDTO.getRegionCode() != null) {
            String regionCode = updateDTO.getRegionCode().trim();

            Long count = sysRegionMapper.selectCount(new LambdaQueryWrapper<SysRegion>()
                    .eq(SysRegion::getRegionCode, regionCode)
                    .ne(SysRegion::getId, updateDTO.getId()));

            if (count > 0) {
                throw new BusinessException("区域编码已存在");
            }

            updateDTO.setRegionCode(regionCode);
        }

        Long parentId = updateDTO.getParentId();
        if (parentId != null) {
            if (parentId.equals(updateDTO.getId())) {
                throw new BusinessException("父级区域不能选择自己");
            }

            if (!SystemConstants.ROOT_PARENT_ID.equals(parentId)) {
                SysRegion parent = sysRegionMapper.selectById(parentId);
                if (parent == null) {
                    throw new BusinessException("父级区域不存在");
                }

                checkRegionDataPermission(parent);

                if (isChildRegion(updateDTO.getId(), parentId)) {
                    throw new BusinessException("父级区域不能选择自己的子区域");
                }
            }
        }

        if (updateDTO.getStatus() != null && !StatusEnum.isValid(updateDTO.getStatus())) {
            throw new BusinessException("区域状态不合法");
        }

        SysRegion region = new SysRegion();
        BeanUtils.copyProperties(updateDTO, region);

        if (StringUtils.hasText(region.getRegionName())) {
            region.setRegionName(region.getRegionName().trim());
        }

        sysRegionMapper.updateById(region);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRegion(Long id) {
        SysRegion region = sysRegionMapper.selectById(id);
        if (region == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "区域不存在");
        }

        checkRegionDataPermission(region);

        Long childCount = sysRegionMapper.selectCount(new LambdaQueryWrapper<SysRegion>()
                .eq(SysRegion::getParentId, id));

        if (childCount > 0) {
            throw new BusinessException("该区域存在子区域，不能删除");
        }

        Long userCount = sysUserRegionMapper.selectCount(new LambdaQueryWrapper<SysUserRegion>()
                .eq(SysUserRegion::getRegionId, id));

        if (userCount > 0) {
            throw new BusinessException("该区域已分配给用户，不能删除");
        }

        Long cameraCount = cameraMapper.selectCount(new LambdaQueryWrapper<Camera>()
                .eq(Camera::getRegionId, id));

        if (cameraCount > 0) {
            throw new BusinessException("该区域下存在摄像头，不能删除");
        }

        sysRegionMapper.deleteById(id);
    }

    @Override
    public List<RegionOptionVO> listEnabledRegionOptions() {
        List<SysRegion> regions = sysRegionMapper.selectList(
                new LambdaQueryWrapper<SysRegion>()
                        .eq(SysRegion::getStatus, StatusEnum.ENABLED.getCode())
                        .orderByAsc(SysRegion::getParentId)
                        .orderByAsc(SysRegion::getSort)
                        .orderByAsc(SysRegion::getId)
        );

        if (regions.isEmpty()) {
            return List.of();
        }

        Map<Long, SysRegion> regionMap = regions.stream()
                .collect(Collectors.toMap(SysRegion::getId, region -> region));

        Set<Long> visibleIds = filterVisibleRegionIds(regions, regionMap);

        return regions.stream()
                .filter(region -> visibleIds.contains(region.getId()))
                .map(this::toOptionVO)
                .toList();
    }

    private boolean isChildRegion(Long regionId, Long targetParentId) {
        List<SysRegion> regions = sysRegionMapper.selectList(new LambdaQueryWrapper<>());
        Map<Long, SysRegion> regionMap = regions.stream()
                .collect(Collectors.toMap(SysRegion::getId, region -> region));

        Long parentId = targetParentId;

        while (parentId != null && !SystemConstants.ROOT_PARENT_ID.equals(parentId)) {
            if (regionId.equals(parentId)) {
                return true;
            }

            SysRegion parent = regionMap.get(parentId);
            if (parent == null) {
                return false;
            }

            parentId = parent.getParentId();
        }

        return false;
    }

    private Set<Long> filterVisibleRegionIds(List<SysRegion> regions, Map<Long, SysRegion> regionMap) {
        if (SecurityUtils.isSuperAdmin()) {
            return regions.stream()
                    .map(SysRegion::getId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        Long currentUserId = SecurityUtils.getUserId();

        List<SysUserRegion> userRegions = sysUserRegionMapper.selectList(
                new LambdaQueryWrapper<SysUserRegion>()
                        .eq(SysUserRegion::getUserId, currentUserId)
        );

        Set<Long> visibleIds = userRegions.stream()
                .map(SysUserRegion::getRegionId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (SysRegion region : regions) {
            if (currentUserId.equals(region.getCreateBy())) {
                visibleIds.add(region.getId());
            }
        }

        Set<Long> baseIds = new HashSet<>(visibleIds);
        for (Long regionId : baseIds) {
            addChildRegionIds(regionId, regions, visibleIds);
        }

        Set<Long> needAddParentIds = new HashSet<>(visibleIds);
        for (Long regionId : needAddParentIds) {
            SysRegion region = regionMap.get(regionId);
            if (region != null) {
                addParentRegionIds(region, regionMap, visibleIds);
            }
        }

        return visibleIds;
    }

    private Set<Long> filterRegionQueryIds(List<SysRegion> regions,
                                           Map<Long, SysRegion> regionMap,
                                           RegionQueryDTO queryDTO,
                                           Set<Long> visibleIds) {
        if (!hasRegionQueryCondition(queryDTO)) {
            return visibleIds;
        }

        Set<Long> finalVisibleIds = new LinkedHashSet<>();

        for (SysRegion region : regions) {
            if (!visibleIds.contains(region.getId())) {
                continue;
            }

            if (!matchRegionQuery(region, queryDTO)) {
                continue;
            }

            finalVisibleIds.add(region.getId());
            addParentRegionIds(region, regionMap, finalVisibleIds);
            addChildRegionIds(region.getId(), regions, finalVisibleIds);
        }

        finalVisibleIds.removeIf(id -> !visibleIds.contains(id));
        return finalVisibleIds;
    }

    private boolean hasRegionQueryCondition(RegionQueryDTO queryDTO) {
        if (queryDTO == null) {
            return false;
        }

        return StringUtils.hasText(queryDTO.getRegionName())
                || StringUtils.hasText(queryDTO.getRegionCode())
                || queryDTO.getStatus() != null;
    }

    private boolean matchRegionQuery(SysRegion region, RegionQueryDTO queryDTO) {
        if (queryDTO == null) {
            return true;
        }

        if (StringUtils.hasText(queryDTO.getRegionName())
                && !region.getRegionName().contains(queryDTO.getRegionName())) {
            return false;
        }

        if (StringUtils.hasText(queryDTO.getRegionCode())
                && !region.getRegionCode().contains(queryDTO.getRegionCode())) {
            return false;
        }

        if (queryDTO.getStatus() != null
                && !queryDTO.getStatus().equals(region.getStatus())) {
            return false;
        }

        return true;
    }

    private void addParentRegionIds(SysRegion region,
                                    Map<Long, SysRegion> regionMap,
                                    Set<Long> visibleIds) {
        Long parentId = region.getParentId();

        while (parentId != null && !SystemConstants.ROOT_PARENT_ID.equals(parentId)) {
            SysRegion parent = regionMap.get(parentId);
            if (parent == null) {
                break;
            }

            visibleIds.add(parent.getId());
            parentId = parent.getParentId();
        }
    }

    private void addChildRegionIds(Long parentId,
                                   List<SysRegion> regions,
                                   Set<Long> visibleIds) {
        for (SysRegion region : regions) {
            if (!parentId.equals(region.getParentId())) {
                continue;
            }

            visibleIds.add(region.getId());
            addChildRegionIds(region.getId(), regions, visibleIds);
        }
    }

    private void checkRegionDataPermission(SysRegion region) {
        if (SecurityUtils.isSuperAdmin()) {
            return;
        }

        Long currentUserId = SecurityUtils.getUserId();

        if (currentUserId.equals(region.getCreateBy())) {
            return;
        }

        Long count = sysUserRegionMapper.selectCount(new LambdaQueryWrapper<SysUserRegion>()
                .eq(SysUserRegion::getUserId, currentUserId)
                .eq(SysUserRegion::getRegionId, region.getId()));

        if (count <= 0) {
            throw new BusinessException("无权操作该区域");
        }
    }

    private RegionVO toVO(SysRegion region) {
        RegionVO vo = new RegionVO();
        BeanUtils.copyProperties(region, vo);
        return vo;
    }

    private RegionTreeVO toTreeVO(SysRegion region) {
        RegionTreeVO vo = new RegionTreeVO();
        BeanUtils.copyProperties(region, vo);
        return vo;
    }

    private RegionOptionVO toOptionVO(SysRegion region) {
        RegionOptionVO vo = new RegionOptionVO();
        BeanUtils.copyProperties(region, vo);
        return vo;
    }
}