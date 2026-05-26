package com.sk.iba.module.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.enums.StatusEnum;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.device.dto.CameraCreateDTO;
import com.sk.iba.module.device.dto.CameraQueryDTO;
import com.sk.iba.module.device.dto.CameraUpdateDTO;
import com.sk.iba.module.device.entity.Camera;
import com.sk.iba.module.device.mapper.CameraMapper;
import com.sk.iba.module.device.service.CameraService;
import com.sk.iba.module.device.vo.CameraOptionVO;
import com.sk.iba.module.device.vo.CameraVO;
import com.sk.iba.module.system.entity.SysRegion;
import com.sk.iba.module.system.entity.SysUserRegion;
import com.sk.iba.module.system.mapper.SysRegionMapper;
import com.sk.iba.module.system.mapper.SysUserRegionMapper;
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
public class CameraServiceImpl implements CameraService {

    private static final Integer SOURCE_TYPE_RTSP = 1;

    private static final Integer SOURCE_TYPE_LOCAL_FILE = 2;

    private final CameraMapper cameraMapper;

    private final SysRegionMapper sysRegionMapper;

    private final SysUserRegionMapper sysUserRegionMapper;

    @Override
    public PageResult<CameraVO> pageCameras(CameraQueryDTO queryDTO) {
        Page<Camera> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Camera> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.hasText(queryDTO.getCameraName()), Camera::getCameraName, queryDTO.getCameraName())
                .like(StringUtils.hasText(queryDTO.getCameraCode()), Camera::getCameraCode, queryDTO.getCameraCode())
                .eq(queryDTO.getSourceType() != null, Camera::getSourceType, queryDTO.getSourceType())
                .eq(queryDTO.getStatus() != null, Camera::getStatus, queryDTO.getStatus());

        Set<Long> queryRegionIds = getQueryRegionIds(queryDTO.getRegionId());

        if (queryRegionIds != null) {
            if (queryRegionIds.isEmpty()) {
                return PageResult.of(List.of(), 0L, queryDTO.getPageNum(), queryDTO.getPageSize());
            }

            wrapper.in(Camera::getRegionId, queryRegionIds);
        }

        wrapper.orderByDesc(Camera::getCreateTime);

        Page<Camera> cameraPage = cameraMapper.selectPage(page, wrapper);
        IPage<CameraVO> voPage = cameraPage.convert(this::toVO);
        return PageResult.of(voPage);
    }

    @Override
    public CameraVO getCameraById(Long id) {
        Camera camera = cameraMapper.selectById(id);
        if (camera == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "摄像头不存在");
        }

        checkCameraDataPermission(camera);
        return toVO(camera);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCamera(CameraCreateDTO createDTO) {
        String cameraName = createDTO.getCameraName().trim();
        String cameraCode = createDTO.getCameraCode().trim();
        String sourceUrl = createDTO.getSourceUrl().trim();

        checkSourceType(createDTO.getSourceType());
        checkRegionValidAndPermission(createDTO.getRegionId());

        Long count = cameraMapper.selectCount(new LambdaQueryWrapper<Camera>()
                .eq(Camera::getCameraCode, cameraCode));

        if (count > 0) {
            throw new BusinessException("摄像头编码已存在");
        }

        if (createDTO.getStatus() == null) {
            createDTO.setStatus(StatusEnum.ENABLED.getCode());
        }
        if (!StatusEnum.isValid(createDTO.getStatus())) {
            throw new BusinessException("摄像头状态不合法");
        }

        Camera camera = new Camera();
        BeanUtils.copyProperties(createDTO, camera);
        camera.setCameraName(cameraName);
        camera.setCameraCode(cameraCode);
        camera.setSourceUrl(sourceUrl);

        if (StringUtils.hasText(camera.getIp())) {
            camera.setIp(camera.getIp().trim());
        }
        if (StringUtils.hasText(camera.getUsername())) {
            camera.setUsername(camera.getUsername().trim());
        }

        cameraMapper.insert(camera);
        return camera.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCamera(CameraUpdateDTO updateDTO) {
        Camera oldCamera = cameraMapper.selectById(updateDTO.getId());
        if (oldCamera == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "摄像头不存在");
        }

        checkCameraDataPermission(oldCamera);

        if (updateDTO.getCameraName() != null && !StringUtils.hasText(updateDTO.getCameraName())) {
            throw new BusinessException("摄像头名称不能为空");
        }

        if (updateDTO.getCameraCode() != null) {
            String cameraCode = updateDTO.getCameraCode().trim();

            Long count = cameraMapper.selectCount(new LambdaQueryWrapper<Camera>()
                    .eq(Camera::getCameraCode, cameraCode)
                    .ne(Camera::getId, updateDTO.getId()));

            if (count > 0) {
                throw new BusinessException("摄像头编码已存在");
            }

            updateDTO.setCameraCode(cameraCode);
        }

        if (updateDTO.getRegionId() != null) {
            checkRegionValidAndPermission(updateDTO.getRegionId());
        }

        if (updateDTO.getSourceType() != null) {
            checkSourceType(updateDTO.getSourceType());
        }

        if (updateDTO.getSourceUrl() != null && !StringUtils.hasText(updateDTO.getSourceUrl())) {
            throw new BusinessException("视频源地址不能为空");
        }

        if (updateDTO.getStatus() != null && !StatusEnum.isValid(updateDTO.getStatus())) {
            throw new BusinessException("摄像头状态不合法");
        }

        Camera camera = new Camera();
        BeanUtils.copyProperties(updateDTO, camera);

        if (StringUtils.hasText(camera.getCameraName())) {
            camera.setCameraName(camera.getCameraName().trim());
        }
        if (StringUtils.hasText(camera.getSourceUrl())) {
            camera.setSourceUrl(camera.getSourceUrl().trim());
        }
        if (StringUtils.hasText(camera.getIp())) {
            camera.setIp(camera.getIp().trim());
        }
        if (StringUtils.hasText(camera.getUsername())) {
            camera.setUsername(camera.getUsername().trim());
        }

        cameraMapper.updateById(camera);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCamera(Long id) {
        Camera camera = cameraMapper.selectById(id);
        if (camera == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "摄像头不存在");
        }

        checkCameraDataPermission(camera);

        // 后面接算法、告警后，这里可以加限制：
        // 1. 摄像头已绑定算法，不能删除
        // 2. 摄像头存在告警记录，不能删除，或者只允许逻辑删除

        cameraMapper.deleteById(id);
    }

    @Override
    public List<CameraOptionVO> listEnabledCameraOptions() {
        LambdaQueryWrapper<Camera> wrapper = new LambdaQueryWrapper<Camera>()
                .eq(Camera::getStatus, StatusEnum.ENABLED.getCode())
                .orderByDesc(Camera::getCreateTime);

        Set<Long> visibleRegionIds = getVisibleRegionIds();
        if (!SecurityUtils.isSuperAdmin()) {
            if (visibleRegionIds.isEmpty()) {
                return List.of();
            }

            wrapper.in(Camera::getRegionId, visibleRegionIds);
        }

        List<Camera> cameras = cameraMapper.selectList(wrapper);
        return cameras.stream().map(this::toOptionVO).toList();
    }

    private Set<Long> getQueryRegionIds(Long queryRegionId) {
        if (SecurityUtils.isSuperAdmin()) {
            if (queryRegionId == null) {
                return null;
            }

            return getRegionAndChildIds(queryRegionId);
        }

        Set<Long> visibleRegionIds = getVisibleRegionIds();
        if (visibleRegionIds.isEmpty()) {
            return Collections.emptySet();
        }

        if (queryRegionId == null) {
            return visibleRegionIds;
        }

        Set<Long> queryRegionIds = getRegionAndChildIds(queryRegionId);

        return queryRegionIds.stream()
                .filter(visibleRegionIds::contains)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Long> getVisibleRegionIds() {
        if (SecurityUtils.isSuperAdmin()) {
            return Collections.emptySet();
        }

        Long currentUserId = SecurityUtils.getUserId();

        List<SysRegion> allRegions = sysRegionMapper.selectList(new LambdaQueryWrapper<>());
        if (allRegions.isEmpty()) {
            return Collections.emptySet();
        }

        List<SysUserRegion> userRegions = sysUserRegionMapper.selectList(
                new LambdaQueryWrapper<SysUserRegion>()
                        .eq(SysUserRegion::getUserId, currentUserId)
        );

        Set<Long> visibleRegionIds = userRegions.stream()
                .map(SysUserRegion::getRegionId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (SysRegion region : allRegions) {
            if (currentUserId.equals(region.getCreateBy())) {
                visibleRegionIds.add(region.getId());
            }
        }

        Set<Long> baseRegionIds = new HashSet<>(visibleRegionIds);
        for (Long regionId : baseRegionIds) {
            addChildRegionIds(regionId, allRegions, visibleRegionIds);
        }

        return visibleRegionIds;
    }

    private Set<Long> getRegionAndChildIds(Long regionId) {
        SysRegion region = sysRegionMapper.selectById(regionId);
        if (region == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "区域不存在");
        }

        List<SysRegion> allRegions = sysRegionMapper.selectList(new LambdaQueryWrapper<>());

        Set<Long> regionIds = new LinkedHashSet<>();
        regionIds.add(regionId);
        addChildRegionIds(regionId, allRegions, regionIds);

        return regionIds;
    }

    private void addChildRegionIds(Long parentId,
                                   List<SysRegion> regions,
                                   Set<Long> regionIds) {
        for (SysRegion region : regions) {
            if (!parentId.equals(region.getParentId())) {
                continue;
            }

            regionIds.add(region.getId());
            addChildRegionIds(region.getId(), regions, regionIds);
        }
    }

    private void checkRegionValidAndPermission(Long regionId) {
        SysRegion region = sysRegionMapper.selectById(regionId);
        if (region == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "区域不存在");
        }

        if (!StatusEnum.isEnabled(region.getStatus())) {
            throw new BusinessException("不能选择已禁用区域");
        }

        if (SecurityUtils.isSuperAdmin()) {
            return;
        }

        Set<Long> visibleRegionIds = getVisibleRegionIds();
        if (!visibleRegionIds.contains(regionId)) {
            throw new BusinessException("无权选择该区域");
        }
    }

    private void checkCameraDataPermission(Camera camera) {
        if (SecurityUtils.isSuperAdmin()) {
            return;
        }

        Set<Long> visibleRegionIds = getVisibleRegionIds();
        if (!visibleRegionIds.contains(camera.getRegionId())) {
            throw new BusinessException("无权操作该摄像头");
        }
    }

    private void checkSourceType(Integer sourceType) {
        if (!SOURCE_TYPE_RTSP.equals(sourceType) && !SOURCE_TYPE_LOCAL_FILE.equals(sourceType)) {
            throw new BusinessException("视频源类型不合法");
        }
    }

    private CameraVO toVO(Camera camera) {
        CameraVO vo = new CameraVO();
        BeanUtils.copyProperties(camera, vo);
        return vo;
    }

    private CameraOptionVO toOptionVO(Camera camera) {
        CameraOptionVO vo = new CameraOptionVO();
        BeanUtils.copyProperties(camera, vo);
        return vo;
    }
}