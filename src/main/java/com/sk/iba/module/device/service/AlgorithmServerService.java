package com.sk.iba.module.device.service;

import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.device.dto.AlgorithmServerCreateDTO;
import com.sk.iba.module.device.dto.AlgorithmServerQueryDTO;
import com.sk.iba.module.device.dto.AlgorithmServerUpdateDTO;
import com.sk.iba.module.device.vo.AlgorithmServerOptionVO;
import com.sk.iba.module.device.vo.AlgorithmServerVO;

import java.util.List;

/**
 * @author zzy
 */
public interface AlgorithmServerService {

    /**
     * 分页查询算法服务器
     */
    PageResult<AlgorithmServerVO> pageServers(AlgorithmServerQueryDTO queryDTO);

    /**
     * 查询算法服务器详情
     */
    AlgorithmServerVO getServerById(Long id);

    /**
     * 新增算法服务器
     */
    Long createServer(AlgorithmServerCreateDTO createDTO);

    /**
     * 修改算法服务器
     */
    void updateServer(AlgorithmServerUpdateDTO updateDTO);

    /**
     * 删除算法服务器
     */
    void deleteServer(Long id);

    /**
     * 查询启用算法服务器下拉选项
     */
    List<AlgorithmServerOptionVO> listEnabledServerOptions();
}