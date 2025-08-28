package com.oceantaker.otzerogenai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.oceantaker.otzerogenai.exception.BusinessException;
import com.oceantaker.otzerogenai.exception.ErrorCode;
import com.oceantaker.otzerogenai.exception.ThrowUtils;
import com.oceantaker.otzerogenai.service.ProjectDownloadService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

@Service
@Slf4j
public class ProjectDownloadServiceImpl implements ProjectDownloadService {

    /**
     * 需要过滤的文件和目录名称
     */
    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules",
            ".git",
            "dist",
            "build",
            ".DS_Store",
            ".env",
            "target",
            ".mvn",
            ".idea",
            ".vscode"
    );

    /**
     * 需要过滤的文件扩展名
     */
    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            ".log",
            ".tmp",
            ".cache"
    );

    @Override
    public void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response) {
        // 基础校验
        ThrowUtils.throwIf(StrUtil.isBlank(projectPath), ErrorCode.PARAMS_ERROR, "项目路径不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(downloadFileName), ErrorCode.PARAMS_ERROR, "下载文件名不能为空");
        File projectDir = new File(projectPath);
        ThrowUtils.throwIf(!projectDir.exists(), ErrorCode.PARAMS_ERROR, "项目路径不存在");
        ThrowUtils.throwIf(!projectDir.isDirectory(), ErrorCode.PARAMS_ERROR, "项目路径不是目录");
        log.info("开始打包项目：{} -> {}.zip", projectPath, downloadFileName);
        // 设置 HTTP 响应头 样板代码
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition",
                String.format("attachment; filename=\"%s.zip\"", downloadFileName));
        // 定义文件过滤器
        FileFilter fileFilter = file -> isPathAllowed(projectDir.toPath(), file.toPath());
        // 打包压缩
        try {
            ZipUtil.zip(response.getOutputStream(), StandardCharsets.UTF_8, false, fileFilter, projectDir);
            log.info("项目打包完成：{} -> {}.zip", projectPath, downloadFileName);
        } catch (IOException e) {
            log.error("项目打包下载失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "项目打包下载失败");
        }


    }

    /**
     * 校验路径是否包含在压缩包中
     * @param projectRoot 项目根目录
     * @param fullPath 完整路径
     * @return true 表示允许下载
     */
    private boolean isPathAllowed(Path projectRoot, Path fullPath){
        // 获取相对路径
        Path relativePath = projectRoot.relativize(fullPath);
        // 遍历路径的每一部分检查是否满足要求
        for (Path part : relativePath){
            String partName = part.toString();
            // 检擦是否在忽略名单中
            if (IGNORED_NAMES.contains(partName)){
                return false;
            }
            // 检测是否以要忽略的扩展名结尾
            if (IGNORED_EXTENSIONS.stream().anyMatch(extension -> partName.toLowerCase().endsWith(extension))){
                return false;
            }

        }
        return true;
    }
}
