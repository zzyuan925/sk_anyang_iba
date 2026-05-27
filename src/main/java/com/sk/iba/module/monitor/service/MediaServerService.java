package com.sk.iba.module.monitor.service;

import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.monitor.dto.MediaServerCreateDTO;
import com.sk.iba.module.monitor.dto.MediaServerQueryDTO;
import com.sk.iba.module.monitor.dto.MediaServerUpdateDTO;
import com.sk.iba.module.monitor.vo.MediaServerVO;

/**
 * 流媒体服务器 Service
 *
 * @author zzy
 */
public interface MediaServerService {

    /**
     * 分页查询流媒体服务器
     */
    PageResult<MediaServerVO> pageMediaServers(MediaServerQueryDTO queryDTO);

    /**
     * 查询流媒体服务器详情
     */
    MediaServerVO getMediaServerById(Long id);

    /**
     * 新增流媒体服务器
     */
    Long createMediaServer(MediaServerCreateDTO createDTO);

    /**
     * 修改流媒体服务器
     */
    void updateMediaServer(MediaServerUpdateDTO updateDTO);

    /**
     * 删除流媒体服务器
     */
    void deleteMediaServer(Long id);
}