package com.example.duoyichuangxin;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by zzg on 2017/9/11.
 */

public interface IExecutor {
    void stop();

    void reset();

    void startAction(LatLng[] latlngs);
}
