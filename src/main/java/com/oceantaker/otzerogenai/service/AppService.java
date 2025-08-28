package com.oceantaker.otzerogenai.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.oceantaker.otzerogenai.model.dto.app.AppAddRequest;
import com.oceantaker.otzerogenai.model.dto.app.AppQueryRequest;
import com.oceantaker.otzerogenai.model.entity.App;
import com.oceantaker.otzerogenai.model.entity.User;
import com.oceantaker.otzerogenai.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author <a href="https://github.com/OceanTaker">程序员OceanTaker</a>
 */
public interface AppService extends IService<App> {

    /**
     * 通过对话生成应用代码
     *
     * @param appId 应用 ID
     * @param userMessage 用户提示词 因为该方法会复用在其他地方，所以还是传入这个参数，方便后续扩展
     * @param loginUser 登录用户
     * @return
     */
    Flux<String> chatToGenCode(Long appId, String userMessage, User loginUser);

    /**
     * 创建应用
     *
     * @param appAddRequest 应用添加请求
     * @param loginUser 登录用户
     * @return 应用 ID
     */
    Long createApp(AppAddRequest appAddRequest, User loginUser);

    /**
     * 应用部署
     *
     * @param appId 应用 ID
     * @param loginUser 登录用户
     * @return 可访问的部署地址
     */
    String deployApp(Long appId, User loginUser);

    /**
     * 异步生成应用截图并更新封面
     *
     * @param appId  应用ID
     * @param appUrl 应用访问URL
     */
    void generateAppScreenshotAsync(Long appId, String appUrl);

    /**
     * 获取应用封装类
     *
     * @param app
     * @return
     */
    AppVO getAppVO(App app);

    /**
     * 获取应用列表封装类
     *
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 构造应用查询条件
     *
     * @param appQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

}
