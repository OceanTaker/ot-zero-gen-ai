package com.oceantaker.otzerogenai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.oceantaker.otzerogenai.core.AiGeneratorFacade;
import com.oceantaker.otzerogenai.exception.BusinessException;
import com.oceantaker.otzerogenai.exception.ErrorCode;
import com.oceantaker.otzerogenai.exception.ThrowUtils;
import com.oceantaker.otzerogenai.mapper.AppMapper;
import com.oceantaker.otzerogenai.model.dto.app.AppQueryRequest;
import com.oceantaker.otzerogenai.model.entity.App;
import com.oceantaker.otzerogenai.model.entity.User;
import com.oceantaker.otzerogenai.model.enums.CodeGenTypeEnum;
import com.oceantaker.otzerogenai.model.vo.AppVO;
import com.oceantaker.otzerogenai.model.vo.UserVO;
import com.oceantaker.otzerogenai.service.AppService;
import com.oceantaker.otzerogenai.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/OceanTaker">程序员OceanTaker</a>
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{


    @Resource
    // 这里调用userService而不是userMapper，因为最好一个服务去调用另一个服务
    private UserService userService;

    @Resource
    private AiGeneratorFacade aiGeneratorFacade;

    @Override
    public Flux<String> chatToGenCode(Long appId, String userMessage, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 错误");
        ThrowUtils.throwIf(StrUtil.isBlank(userMessage), ErrorCode.PARAMS_ERROR, "提示词不能为空");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 权限校验
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限操作");
        // 4. 获取应用生成代码类型
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType); // 转成生成代码类型枚举类
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        // 5. 调用 AI 生成代码 调用门面
        return aiGeneratorFacade.generateAndSaveCodeStream(userMessage, codeGenTypeEnum, appId);
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO); // app -> appVO，还差一个userVO
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        /*
          从app获得userId，如果不为空，就获取用户信息，赋给userVO，再赋给appVO
         */
        return appVO;
    }


    @Override
    public List<AppVO> getAppVOList(List<App> appList) {

        // HuTool 工具类提供的集合判空方法（优于原生 appList == null || appList.isEmpty()）
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId) // app => app.getUserId() 简化
                .collect(Collectors.toSet()); //放入集合聚合
        // listByIds：按照用户id列表查询用户信息
        // toMap(key, value) 直接指定key和value
        // 将 List<User>转换为 Map<Long, UserVO>，以用户 ID 为键，提升后续查找效率（O(1) 复杂度）
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        // 流式组装 AppVO 列表
        /*
        遍历app列表，给每个app：
        首先把app变成封装类 app->appVO
        然后从map中 根据用户id 取出来 用户封装类的信息
        再把封装后的用户信息返回出去，得到一个新的列表
         */
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }


    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }



}
