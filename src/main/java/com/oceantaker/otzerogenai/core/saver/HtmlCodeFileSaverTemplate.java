package com.oceantaker.otzerogenai.core.saver;

import cn.hutool.core.util.StrUtil;
import com.oceantaker.otzerogenai.ai.model.HtmlCodeResult;
import com.oceantaker.otzerogenai.exception.BusinessException;
import com.oceantaker.otzerogenai.exception.ErrorCode;
import com.oceantaker.otzerogenai.model.enums.CodeGenTypeEnum;

/**
 * HTML代码保存器
 *
 * @author oceantaker
 */
public class HtmlCodeFileSaverTemplate extends  CodeFileSaverTemplate<HtmlCodeResult>{

    @Override
    public CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());

    }

    // 重写父类的校验参数方法
    @Override
    protected void validateInput(HtmlCodeResult result) {
        // 父类的 参数为空校验
        super.validateInput(result);
        // HTML 代码不能为空
        if(StrUtil.isBlank(result.getHtmlCode())){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 代码不能为空");
        }
    }
}
