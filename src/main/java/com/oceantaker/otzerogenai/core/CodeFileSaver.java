package com.oceantaker.otzerogenai.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.oceantaker.otzerogenai.ai.model.HtmlCodeResult;
import com.oceantaker.otzerogenai.ai.model.MultiFileCodeResult;
import com.oceantaker.otzerogenai.constant.AppConstant;
import com.oceantaker.otzerogenai.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 文件保存器
 */
@Deprecated // 荒废的文件保存器
public class CodeFileSaver {

    /**
     * 文件保存的根目录
     */
    // System.getProperty("user.dir")获取当前Java进程的工作目录（即启动JVM时的路径）
    private static final String FILE_SAVE_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;

    /**
     * 保存html 网页代码
     *
     * @param htmlCodeResult
     * @return
     */
    public static File saveHtmlCodeResult(HtmlCodeResult htmlCodeResult) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(baseDirPath, "index.html", htmlCodeResult.getHtmlCode());
        return new File(baseDirPath);
    }


    /**
     * 保存多文件代码
     *
     * @param multiFileCodeResult
     * @return
     */
    public static File saveMultiFileCodeResult(MultiFileCodeResult multiFileCodeResult) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToFile(baseDirPath, "index.html", multiFileCodeResult.getHtmlCode());
        writeToFile(baseDirPath, "style.css", multiFileCodeResult.getCssCode());
        writeToFile(baseDirPath, "script.js", multiFileCodeResult.getJsCode());
        return new File(baseDirPath);
    }

    /**
     * 构建文件唯一路径：tmp/code_output/biztype_雪花 ID
     * @param bizType 代码生成类型
     * @return
     */
    private static String buildUniqueDir(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 保存单个文件 上面两个保存代码的方法会调用这个方法，所以此方法可以私有
     * @param dirPath
     * @param filename
     * @param content
     */
    private static void writeToFile(String dirPath, String filename, String content) {
        // 拼出完整文件路径
        // File.separator : 不同的操作系统下都兼容的分隔符 “/”
        String filePath = dirPath + File.separator + filename;
        // 使用枚举类防止拼错
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }




}
