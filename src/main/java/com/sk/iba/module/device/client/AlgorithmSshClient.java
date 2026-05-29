package com.sk.iba.module.device.client;

import com.jcraft.jsch.*;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.module.device.entity.AlgorithmServer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Properties;

/**
 * 算法服务器 SSH 客户端
 *
 * @author zzy
 */
@Component
public class AlgorithmSshClient {

    /**
     * 上传并解压算法包
     */
    public void uploadAndUnzip(AlgorithmServer server, String localPackagePath, String deployPath) {
        if (!StringUtils.hasText(localPackagePath)) {
            throw new BusinessException("算法包本地路径不能为空");
        }
        if (!StringUtils.hasText(deployPath)) {
            throw new BusinessException("算法包部署路径不能为空");
        }

        File localFile = new File(localPackagePath);
        if (!localFile.exists() || !localFile.isFile()) {
            throw new BusinessException("算法包文件不存在：" + localPackagePath);
        }

        Session session = null;
        ChannelSftp sftp = null;

        try {
            session = createSession(server);

            executeCommand(session, "mkdir -p " + quote(deployPath));

            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();

            String remoteFilePath = deployPath + "/" + localFile.getName();
            sftp.put(localPackagePath, remoteFilePath);

            executeCommand(session, "unzip -o " + quote(remoteFilePath) + " -d " + quote(deployPath));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("算法包部署失败：" + e.getMessage());
        } finally {
            if (sftp != null) {
                sftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    private Session createSession(AlgorithmServer server) {
        try {
            if (!StringUtils.hasText(server.getIp())) {
                throw new BusinessException("算法服务器IP不能为空");
            }
            if (!StringUtils.hasText(server.getAccount())) {
                throw new BusinessException("算法服务器账号不能为空");
            }
            if (!StringUtils.hasText(server.getPassword())) {
                throw new BusinessException("算法服务器密码不能为空");
            }

            int port = server.getPort() == null ? 22 : server.getPort();

            JSch jSch = new JSch();
            Session session = jSch.getSession(server.getAccount(), server.getIp(), port);
            session.setPassword(server.getPassword());

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect(10000);
            return session;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("连接算法服务器失败：" + e.getMessage());
        }
    }

    private void executeCommand(Session session, String command) {
        ChannelExec channel = null;

        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            ByteArrayOutputStream errStream = new ByteArrayOutputStream();
            channel.setErrStream(errStream);

            channel.connect();

            while (!channel.isClosed()) {
                Thread.sleep(100);
            }

            int exitStatus = channel.getExitStatus();
            if (exitStatus != 0) {
                String error = errStream.toString();
                throw new BusinessException("执行远程命令失败：" + command + "，错误：" + error);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("执行远程命令失败：" + e.getMessage());
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    private String quote(String value) {
        return "'" + value.replace("'", "'\\''") + "'";
    }
}