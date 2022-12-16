package com.zc.service;

import com.zc.model.Data;

import java.util.List;

public interface DataService {
    List<Data> getDataByKey(String key);
}
