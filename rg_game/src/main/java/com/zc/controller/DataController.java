package com.zc.controller;

import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.kit.Ret;
import com.zc.model.Data;
import com.zc.service.DataService;
import com.zc.service.impl.DataServiceImpl;

import java.util.List;

@Path("/data")
public class DataController extends Controller {
    DataService ds = new DataServiceImpl();

    public void getDataByKey() {
        String key = getPara("key");
        List<Data> list = ds.getDataByKey(key);
        if (list == null || list.size() == 0) {
            renderJson(Ret.fail("关键字不存在！请检查！"));
        } else {
            renderJson(Ret.ok("data", list));
        }
    }
}
