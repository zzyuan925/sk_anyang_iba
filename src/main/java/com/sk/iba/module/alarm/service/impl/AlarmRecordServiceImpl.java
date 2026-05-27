package com.sk.iba.module.alarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sk.iba.common.constant.AlarmConstants;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.alarm.dto.AlarmFalseAlarmDTO;
import com.sk.iba.module.alarm.dto.AlarmRecordQueryDTO;
import com.sk.iba.module.alarm.entity.AlarmRecord;
import com.sk.iba.module.alarm.mapper.AlarmRecordMapper;
import com.sk.iba.module.alarm.service.AlarmRecordService;
import com.sk.iba.module.alarm.vo.AlarmRecordVO;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 告警记录 Service 实现
 *
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class AlarmRecordServiceImpl implements AlarmRecordService {

    private final AlarmRecordMapper alarmRecordMapper;

    private final SysRegionMapper sysRegionMapper;

    private final SysUserRegionMapper sysUserRegionMapper;

    @Override
    public PageResult<AlarmRecordVO> pageAlarmRecords(AlarmRecordQueryDTO queryDTO) {
        Page<AlarmRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<AlarmRecord> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(queryDTO.getCameraId() != null, AlarmRecord::getCameraId, queryDTO.getCameraId())
                .eq(queryDTO.getFunctionId() != null, AlarmRecord::getFunctionId, queryDTO.getFunctionId())
                .eq(queryDTO.getIsFalseAlarm() != null, AlarmRecord::getIsFalseAlarm, queryDTO.getIsFalseAlarm())
                .like(StringUtils.hasText(queryDTO.getCameraName()), AlarmRecord::getCameraName, queryDTO.getCameraName())
                .like(StringUtils.hasText(queryDTO.getCameraCode()), AlarmRecord::getCameraCode, queryDTO.getCameraCode())
                .ge(queryDTO.getStartTime() != null, AlarmRecord::getAlarmTime, queryDTO.getStartTime())
                .le(queryDTO.getEndTime() != null, AlarmRecord::getAlarmTime, queryDTO.getEndTime());

        Set<Long> queryRegionIds = getQueryRegionIds(queryDTO.getRegionId());

        if (queryRegionIds != null) {
            if (queryRegionIds.isEmpty()) {
                return PageResult.of(List.of(), 0L, queryDTO.getPageNum(), queryDTO.getPageSize());
            }

            wrapper.in(AlarmRecord::getRegionId, queryRegionIds);
        }

        wrapper.orderByDesc(AlarmRecord::getAlarmTime);

        Page<AlarmRecord> alarmPage = alarmRecordMapper.selectPage(page, wrapper);
        IPage<AlarmRecordVO> voPage = alarmPage.convert(this::toVO);
        return PageResult.of(voPage);
    }

    @Override
    public AlarmRecordVO getAlarmRecordById(Long id) {
        AlarmRecord alarmRecord = getAlarmRecord(id);
        checkAlarmDataPermission(alarmRecord);
        return toVO(alarmRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlarmRecord(Long id) {
        AlarmRecord alarmRecord = getAlarmRecord(id);
        checkAlarmDataPermission(alarmRecord);
        alarmRecordMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markFalseAlarm(Long id, AlarmFalseAlarmDTO falseAlarmDTO) {
        AlarmRecord oldAlarmRecord = getAlarmRecord(id);
        checkAlarmDataPermission(oldAlarmRecord);

        String remark = null;
        if (falseAlarmDTO != null && StringUtils.hasText(falseAlarmDTO.getFalseAlarmRemark())) {
            remark = falseAlarmDTO.getFalseAlarmRemark().trim();
        }

        alarmRecordMapper.update(null,
                new LambdaUpdateWrapper<AlarmRecord>()
                        .eq(AlarmRecord::getId, id)
                        .set(AlarmRecord::getIsFalseAlarm, AlarmConstants.FALSE_ALARM_YES)
                        .set(AlarmRecord::getFalseAlarmBy, SecurityUtils.getUserId())
                        .set(AlarmRecord::getFalseAlarmTime, LocalDateTime.now())
                        .set(AlarmRecord::getFalseAlarmRemark, remark)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelFalseAlarm(Long id) {
        AlarmRecord oldAlarmRecord = getAlarmRecord(id);
        checkAlarmDataPermission(oldAlarmRecord);

        alarmRecordMapper.update(null,
                new LambdaUpdateWrapper<AlarmRecord>()
                        .eq(AlarmRecord::getId, id)
                        .set(AlarmRecord::getIsFalseAlarm, AlarmConstants.FALSE_ALARM_NO)
                        .set(AlarmRecord::getFalseAlarmBy, SecurityUtils.getUserId())
                        .set(AlarmRecord::getFalseAlarmTime, LocalDateTime.now())
                        .set(AlarmRecord::getFalseAlarmRemark, null)
        );
    }

    private AlarmRecord getAlarmRecord(Long id) {
        AlarmRecord alarmRecord = alarmRecordMapper.selectById(id);
        if (alarmRecord == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "告警记录不存在");
        }
        return alarmRecord;
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

    private void checkAlarmDataPermission(AlarmRecord alarmRecord) {
        if (SecurityUtils.isSuperAdmin()) {
            return;
        }

        Set<Long> visibleRegionIds = getVisibleRegionIds();
        if (!visibleRegionIds.contains(alarmRecord.getRegionId())) {
            throw new BusinessException("无权操作该告警记录");
        }
    }

    private AlarmRecordVO toVO(AlarmRecord alarmRecord) {
        AlarmRecordVO vo = new AlarmRecordVO();
        BeanUtils.copyProperties(alarmRecord, vo);
        return vo;
    }
}