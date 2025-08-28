package com.oceantaker.otzerogenai.service;

/**
 * 截图服务
 * @author Oceantaker
 */

public interface ScreenshotService {

    /**
     * 通用的截图服务，可以得到访问地址
     * @param webUrl 网页地址
     * @return 访问地址
     */
    String generateAndUploadScreenshot(String webUrl);
}
