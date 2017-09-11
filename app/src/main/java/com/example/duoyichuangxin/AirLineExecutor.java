package com.example.duoyichuangxin;

import android.app.Activity;
import android.os.Handler;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.example.duoyichuangxin.task.AirLineTask;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by zzg on 2017/9/11.
 */

public class AirLineExecutor implements IExecutor {

    private final WeakReference<Activity> mActivityWeakReference;
    private final Marker mMoveMarker;
    private final Handler mHandler;
    private final MapView mMapView;
    private final ExecutorService mThreadPool;
    private final List<Future<Boolean>> mFutureList;
    private AirLineTask task;
    private Future<Boolean> mFuture;

    public AirLineExecutor(Activity activity, Marker marker, Handler handler, MapView mapView) {
        mActivityWeakReference = new WeakReference<>(activity);
        this.mMoveMarker = marker;
        this.mHandler = handler;
        this.mMapView = mapView;
        mThreadPool = Executors.newSingleThreadExecutor();
        // 只能有一个
        mFutureList = new LinkedList<Future<Boolean>>();
    }

    @Override
    public void stop() {

    }

    @Override
    public void reset() {
        if (mFutureList.size() > 0) {
            Future<Boolean> task = mFutureList.get(0);
            mFutureList.clear();
            cancelTask(task, 200);
        }
    }

    @Override
    public void startAction(LatLng[] latlngs) {
        task = new AirLineTask(mMoveMarker, mHandler, mMapView, latlngs);
        mFuture = mThreadPool.submit(task);
        mFutureList.add(mFuture);
    }

    private static void cancelTask(final Future<?> future, final int delay) {
        Runnable cancellation = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    future.cancel(true); // 取消与 future 关联的正在运行的任务
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        };
        new Thread(cancellation).start();
    }
}
