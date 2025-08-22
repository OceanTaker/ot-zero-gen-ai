package com.oceantaker.otzerogenai.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 服务创建工厂
 */
@Configuration
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    /**
     * 创建 AI  服务
     *
     * @return 创建的AI代码生成服务实例
     * 只需要写接口，不需要写http请求，不用写如何与ai对话
     */
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        // 使用代理，为接口生成了实现类，与ai交互
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }

}