package com.oceantaker.otzerogenai.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.oceantaker.otzerogenai.model.dto.chathistory.ChatHistoryQueryRequest;
import com.oceantaker.otzerogenai.model.entity.ChatHistory;
import com.oceantaker.otzerogenai.model.entity.User;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author <a href="https://github.com/OceanTaker">程序员OceanTaker</a>
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加对话历史消息
     *
     * @param appId       应用id
     * @param message     消息内容
     * @param messageType 消息类型
     * @param userId      用户id
     * @return 是否添加成功
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 删除对话历史消息
     *
     * @param appId 应用id
     * @return 是否删除成功
     */
    boolean deleteByAppId(Long appId);

    /**
     * 分页查询某 APP 下的对话历史消息
     *
     * @param appId           应用id
     * @param pageSize        每页大小
     * @param lastCreateTime  最后创建时间
     * @param loginUser       登录用户
     * @return 对话历史消息列表
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);

    /**
     * 加载对话历史消息到内存
     *
     * @param appId           应用id
     * @param chatMemory      对话历史消息内存
     * @param maxCount        最大加载条数
     * @return 加载成功的数量
     */
    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);

    /**
     * 获取查询条件
     *
     * @param chatHistoryQueryRequest 查询条件
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);
}
