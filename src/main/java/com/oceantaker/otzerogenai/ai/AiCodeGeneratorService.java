package com.oceantaker.otzerogenai.ai;

import com.oceantaker.otzerogenai.ai.model.HtmlCodeResult;
import com.oceantaker.otzerogenai.ai.model.MultiFileCodeResult;
import com.oceantaker.otzerogenai.controller.HealthController;
import dev.langchain4j.service.SystemMessage;
import org.springframework.http.HttpLogging;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {

    /**
     * 生成 HTML 代码
     *
     * @param userMessage 用户消息
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    // 只需要把返回值从String改成对应的对象名HtmlCodeResult，框架会自动转化
    HtmlCodeResult generateHtmlCode(String userMessage);

    /**
     * 生成多文件代码
     *
     * @param userMessage 用户消息
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMessage);

    /**
     * 生成 HTML 代码 (流式）
     *
     * @param userMessage 用户消息
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    // 只需要把返回值从String改成对应的对象名HtmlCodeResult，框架会自动转化
    Flux<String> generateHtmlCodeStream(String userMessage);

    /**
     * 生成多文件代码 (流式）
     *
     * @param userMessage 用户消息
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMultiFileCodeStream(String userMessage);
}
