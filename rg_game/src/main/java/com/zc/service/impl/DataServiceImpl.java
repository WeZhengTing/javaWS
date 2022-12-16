package com.zc.service.impl;

import com.zc.model.Data;
import com.zc.service.DataService;

import java.util.List;

public class DataServiceImpl implements DataService {
    @Override
    public List<Data> getDataByKey(String key) {
        List<Data> list=Data.dao.find("SELECT * FROM `zc_data` where `key`=?",key);
        return list;
    }
}
