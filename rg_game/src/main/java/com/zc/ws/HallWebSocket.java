package com.zc.ws;

import com.alibaba.fastjson.JSONObject;
import com.zc.controller.front.UserController;
import com.zc.model.User;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName HallWebSocket
 * @Description TODO
 * @Author Yszzz
 * @Date 2022/11/1 21:35
 * @Version 1.0
 */
@ServerEndpoint("/hallWs/{token}")
public class HallWebSocket {
    Session session;
    String token;
    static Map<String,HallWebSocket> map = new HashMap<String,HallWebSocket>();
    User user;

    //1.open 2.close 3.message 4.error
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token){
        this.session = session;
        this.token = token;
        user = UserController.tokenMap.get(token);
        if (user == null){
            // 提示未找到
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("method", "error");
            jsonObject.put("msg", "未登录！");
            session.getAsyncRemote().sendText(jsonObject.toString());
            return;
        }
        map.put(token, this);
        System.out.println(user.getUsername() + "进入房间");
    }
    @OnClose
    public void onClose(){
        if (map.get(token) == null){
            return;
        }
        map.remove(token);
        System.out.println(user.getUsername() + "退出房间!");
    }
    @OnMessage
    public void onMessage(String msg){
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (!map.get(key).token.equals(token)){
                map.get(key).session.getAsyncRemote().sendText(msg);
            }
        }
    }

    public static void sendMsg(String token,String msg){
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (!map.get(key).token.equals(token)){
                map.get(key).session.getAsyncRemote().sendText(msg);
            }
        }
    }

}