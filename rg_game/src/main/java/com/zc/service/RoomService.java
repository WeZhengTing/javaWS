package com.zc.service;

import com.jfinal.kit.Ret;
import com.zc.model.Room;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface RoomService {
    Ret createRoom(Room room , HttpServletRequest req);
    List<Room> getRooms(int page,int limit);

   Ret enterRoom(String roomId,String pwd, HttpServletRequest req);

    long getRoomCount(String roomName, String roomStatus);
    List<Room> getRooms(int page,int limit,String roomName, String roomStatus);

    Ret outRoom(HttpServletRequest request);

    Ret getRoomInfo(HttpServletRequest request);

    Ret ready(HttpServletRequest request);
}
