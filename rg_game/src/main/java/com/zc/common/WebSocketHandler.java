package com.zc.common;

import com.jfinal.handler.Handler;
import com.jfinal.kit.StrKit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;
/**
 * @ClassName WebSocketHandler
 * @Description TODO
 * @Author Yszzz
 * @Date 2022/11/1 21:59
 * @Version 1.0
 */
public class WebSocketHandler extends Handler {

    private Pattern filterUrlRegxPattern;

    public WebSocketHandler(String filterUrlRegx) {
        if (StrKit.isBlank(filterUrlRegx))
            throw new IllegalArgumentException("The para filterUrlRegx can not be blank.");
        filterUrlRegxPattern = Pattern.compile(filterUrlRegx);
    }
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (filterUrlRegxPattern.matcher(target).find())
            return ;
        else
            next.handle(target, request, response, isHandled);

    }

}