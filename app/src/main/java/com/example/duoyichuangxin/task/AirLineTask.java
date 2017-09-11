package com.example.duoyichuangxin.task;

import android.os.Handler;
import android.util.Log;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.example.duoyichuangxin.AppConstants;

import java.util.concurrent.Callable;

/**
 * Created by zzg on 2017/9/10.
 */

public class AirLineTask implements Callable<Boolean> {

    private final Marker mMoveMarker;
    private final Handler mHandler;
    private final MapView mMapView;
    private final LatLng[] mLatLngs;

    public AirLineTask(Marker marker, Handler handler, MapView mapView,LatLng[] latLngs) {
        this.mMoveMarker = marker;
        this.mHandler = handler;
        this.mMapView = mapView;
        this.mLatLngs = latLngs;
    }

    @Override
    public Boolean call() throws Exception {
        boolean hasStop = Thread.currentThread().isInterrupted();
        //while (!hasStop) {
            Log.e("Test", "AirLineTask hasStop = " + hasStop);

            if (Thread.currentThread().isInterrupted()) { // 任务被取消
                Log.e("Test", "AirLineTask 任务取消了1");
                mHandler.removeCallbacksAndMessages(null);
                return false;
            }

            for (int i = 0; i < mLatLngs.length - 1; i++) {
                final LatLng startPoint = mLatLngs[i];
                final LatLng endPoint = mLatLngs[i + 1];

                if (Thread.currentThread().isInterrupted()) { // 任务被取消
                    Log.e("Test", "AirLineTask 任务取消了2");
                    mHandler.removeCallbacksAndMessages(null);
                    return false;
                }

                mMoveMarker
                        .setPosition(startPoint);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // refresh marker's rotate
                        if (mMapView == null) {
                            return;
                        }
                        mMoveMarker.setRotate((float) MathUtil.getAngle(startPoint,
                                endPoint));
                    }
                });
                double slope = MathUtil.getSlope(startPoint, endPoint);
                // 是不是正向的标示
                boolean isReverse = (startPoint.latitude > endPoint.latitude);

                double intercept = MathUtil.getInterception(slope, startPoint);

                double xMoveDistance = isReverse ? MathUtil.getXMoveDistance(slope) :
                        -1 * MathUtil.getXMoveDistance(slope);

                for (double j = startPoint.latitude; !((j > endPoint.latitude) ^ isReverse);
                     j = j - xMoveDistance) {

                    LatLng latLng = null;
                    if (slope == Double.MAX_VALUE) {
                        latLng = new LatLng(j, startPoint.longitude);
                    } else {
                        latLng = new LatLng(j, (j - intercept) / slope);
                    }

                    final LatLng finalLatLng = latLng;

                    if (Thread.currentThread().isInterrupted()) { // 任务被取消
                        Log.e("Test", "AirLineTask 任务取消了3");
                        mHandler.removeCallbacksAndMessages(null);
                        return false;
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mMapView == null) {
                                return;
                            }
                            mMoveMarker.setPosition(finalLatLng);
                        }
                    });
                    try {
                        Thread.sleep(AppConstants.TIME_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e("Test", "AirLineTask 任务取消了4");
                        Log.e("Test", "startPoint = " + startPoint
                                + " , endPoint = " + endPoint);
                        Log.e("Test", "finalLatLng x = " + finalLatLng.latitude
                                + " , finalLatLng y = " + finalLatLng.longitude);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mMapView == null) {
                                    return;
                                }
                                LatLng tmpLatLng = new LatLng(endPoint.latitude, endPoint.longitude);
                                mMoveMarker.setPosition(tmpLatLng);
                            }
                        });

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 移动到最近的点
                                mHandler.removeCallbacksAndMessages(null);
                            }
                        }, 200);
                        return false;
                    }
                }

            }
            Log.e("Test", "AirLineTask do task......");
        // }
        return true;
    }
}
