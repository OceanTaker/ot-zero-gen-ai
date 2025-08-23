package com.oceantaker.otzerogenai.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.oceantaker.otzerogenai.exception.BusinessException;
import com.oceantaker.otzerogenai.exception.ErrorCode;
import com.oceantaker.otzerogenai.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 抽象代码文件保存器 - 模板方法模式
 *
 * @param <T>
 */
public abstract class CodeFileSaverTemplate<T> {

    /**
     * 文件保存的根目录
     */
    // System.getProperty("user.dir")获取当前Java进程的工作目录（即启动JVM时的路径）
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 模板方法：保存代码的标准流程
     *
     * @param result 代码结果对象
     * @param appId  应用 ID
     * @return 保存的目录
     */
    public final File saveCode(T result, Long appId) {
        // 1. 验证输入
        validateInput(result, appId);
        // 2. 构建唯一目录
        String baseDirPath = buildUniqueDir(appId);
        // 3. 保存文件（具体实现交给子类）
        saveFiles(result, baseDirPath);
        // 4. 返回文件目录对象
        return new File(baseDirPath);
    }


    /**
     * 保存单个文件 子类保存不同文件类型的方法会调用这个方法，所以此方法可以protected
     * @param dirPath 文件目录
     * @param filename 文件名
     * @param content 文件内容
     * 父类提供的通用方法，不允许子类覆写
     */
    protected final void writeToFile(String dirPath, String filename, String content) {
        // 拼出完整文件路径
        // File.separator : 不同的操作系统下都兼容的分隔符 “/”
        String filePath = dirPath + File.separator + filename;
        // 使用枚举类防止拼错
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }



    protected void validateInput(T result, Long appId) {
        if(result == null || appId == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存代码失败，请检查输入参数");
        }
    }


    /**
     * 构建文件唯一路径：tmp/code_output/biztype_雪花 ID
     *
     * @param appId 应用 ID
     * @return
     */
    protected String buildUniqueDir(Long appId) {
        if(appId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        }
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 保存文件的具体实现（由子类实现）
     *
     * @param result      代码结果对象
     * @param baseDirPath 基础目录路径
     */
    protected abstract void saveFiles(T result, String baseDirPath);


    /**
     * 获取代码类型（由子类实现）
     *
     * @return 代码生成类型
     */
    protected abstract CodeGenTypeEnum getCodeType();


}