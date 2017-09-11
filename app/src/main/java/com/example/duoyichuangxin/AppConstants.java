package com.example.duoyichuangxin;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by zzg on 2017/9/9.
 */

public class AppConstants {

    // 通过设置间隔时间和距离可以控制速度和图标移动的距离
    public static final int TIME_INTERVAL = 80;

    // line1
    public static final LatLng[] latlngs = new LatLng[]{
            new LatLng(22.616865, 114.032641),
            new LatLng(22.619834, 114.037995),
            new LatLng(22.615681, 114.040906),
            new LatLng(22.612645, 114.035552),
            new LatLng(22.616857, 114.032677)
    };

    // line2
    public static final LatLng[] latlngs1 = new LatLng[]{
            // 114.03795,22.619867
            new LatLng(22.619834, 114.037995),
            new LatLng(22.615681, 114.040906),
            new LatLng(22.612645, 114.035552),
            new LatLng(22.616857, 114.032677),
            new LatLng(22.619867, 114.03795)
    };

    // line3
    public static final LatLng[] latlngs2 = new LatLng[]{
            // 114.040753,22.615722
            new LatLng(22.615681, 114.040906),
            new LatLng(22.612645, 114.035552),
            new LatLng(22.616857, 114.032677),
            new LatLng(22.619834, 114.037995),
            new LatLng(22.615772, 114.040753)
    };

    // line4
    public static final LatLng[] latlngs3 = new LatLng[]{
            new LatLng(22.612645, 114.035552),
            new LatLng(22.616865, 114.032641),
            new LatLng(22.619834, 114.037995),
            new LatLng(22.615681, 114.040906),
            new LatLng(22.612695, 114.035606)
    };
}
