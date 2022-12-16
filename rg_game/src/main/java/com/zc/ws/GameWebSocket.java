package com.zc.ws;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.zc.controller.front.RoomConteoller;
import com.zc.controller.front.UserController;
import com.zc.model.Player;
import com.zc.model.Room;
import com.zc.model.User;
import com.zc.service.impl.RoomServiceImpl;
import com.zc.util.Util;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;

/**
 * @ClassName HallWebSocket
 * @Description TODO
 * @Author Yszzz
 * @Date 2022/11/1 21:35
 * @Version 1.0
 */
@ServerEndpoint("/gameWs/{token}")
public class GameWebSocket {
    Session session;
    String token;
    static Map<String, GameWebSocket> map = new HashMap<String, GameWebSocket>();
    User user;

    //1.open 2.close 3.message 4.error
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        this.session = session;
        this.token = token;
        user = UserController.tokenMap.get(token);
        if (user == null) {
            // 提示未找到
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("method", "error");
            jsonObject.put("msg", "未登录！");
            session.getAsyncRemote().sendText(jsonObject.toString());
            return;
        }
        map.put(token, this);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", "otherEnterRoom");
        jsonObject.put("username", user.getUsername());
        sendMsg(token, jsonObject.toString());
        System.out.println(user.getUsername() + "进入房间");
    }

    @OnClose
    public void onClose() {
        if (map.get(token) == null) {
            return;
        }
        map.remove(token);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", "otherOutRoom");
        jsonObject.put("username", user.getUsername());
        sendMsg(token, jsonObject.toString());
        outRoom();
        System.out.println(user.getUsername() + "退出房间!");
    }

    private void outRoom() {

        if (token == null) return;
        User user = UserController.tokenMap.get(token);
        if (user == null) return;
        Player player = RoomConteoller.playerMap.get(user.getId());

        if (RoomConteoller.playerRoom.get(user.getId()) == null) {
            return;
        }
        Room thisRoom = RoomConteoller.playerRoom.get(user.getId());
        if (!RoomConteoller.rooms.contains(thisRoom)) {
            return;
        }
        //        3.用户退出房间是否影响游戏进行
        if (player.getRole() == RoomServiceImpl.blackRole.getValueId() || player.getRole() == RoomServiceImpl.whileRole.getValueId()) {
            //退出之前把游戏关闭掉
            //发送游戏关闭ws
            int thisRole = player.getRole();
            //继承给房间里其他人
            for (Player p : thisRoom.getPlayers()) {

                if (p.getRole() != RoomServiceImpl.blackRole.getValueId() && p.getRole() != RoomServiceImpl.whileRole.getValueId()) {

                    p.setRole(thisRole);//角色继承给其他人
                    break;
                }
            }
        }
        RoomConteoller.playerRoom.remove(player.getId());
        for (Player p : thisRoom.getPlayers()) {
            if (p.getId() == player.getId()) {
                thisRoom.getPlayers().remove(p);
                break;
            }
        }
        RoomConteoller.playerMap.remove(player.getId());
        JSONObject msg = new JSONObject();
        msg.put("method", "freshTable");
        HallWebSocket.sendMsg(token, msg.toString());

        if (thisRoom.getPlayers().size() == 0) {
            RoomConteoller.rooms.remove(thisRoom);
            return;
        }
        if (thisRoom.getRoomOwner().equals(player)) {
            //玩家就是房主的时候
            Random random = new Random();
            Player p = thisRoom.getPlayers().get(random.nextInt(thisRoom.getPlayers().size()));
            thisRoom.setRoomOwner(p);//新房主
        }
    }

    @OnMessage
    public void onMessage(String msg) {
        JSONObject obj = JSONObject.parseObject(msg);
        String method = (String) obj.get("method");
        if ("sendChat".equals(method)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("method", method);
            jsonObject.put("msg", obj.get("msg"));
            jsonObject.put("username", user.getUsername());
            sendMsg(token, jsonObject.toString());

        }else if ("down".equals(method)){
            sendMsg(token,msg);
        }
    }

    /**
     * 发送消息给房间里的所有人(除了自己)
     * @param token
     * @param msg
     */
    public static void sendMsg(String token, String msg) {
        Set<String> keys = map.keySet();
        Room thisRoom = RoomConteoller.playerRoom.get(map.get(token).user.getId());
        List<Player> players = thisRoom.getPlayers();
        for (String key : keys) {
            if (!map.get(key).token.equals(token)) {
//               找到当前房间，搜索房间里的人是否存在于这个map里token
                Integer thisId = map.get(key).user.getId();//当前这个人是否在房间
                for (Player p : players) {
                    if (p.getId().equals(thisId)) {
                        //在房间内
                        map.get(key).session.getAsyncRemote().sendText(msg);
                    }
                }
            }
        }
    }

    /**
     * 发送消息给房间内的所有人
     * @param token
     * @param msg
     */
    public static void sendMsgAll(String token, String msg) {
        Set<String> keys = map.keySet();
        Room thisRoom = RoomConteoller.playerRoom.get(map.get(token).user.getId());
        List<Player> players = thisRoom.getPlayers();
        for (String key : keys) {
//               找到当前房间，搜索房间里的人是否存在于这个map里token
            Integer thisId = map.get(key).user.getId();//当前这个人是否在房间
            for (Player p : players) {
                if (p.getId().equals(thisId)) {
                    //在房间内
                    map.get(key).session.getAsyncRemote().sendText(msg);
                }
            }
        }
    }
}