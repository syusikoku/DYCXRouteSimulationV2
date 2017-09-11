package com.example.duoyichuangxin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.duoyichuangxin.callback.PermissionListener;
import com.example.duoyichuangxin.utils.UIUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.example.duoyichuangxin.task.MathUtil.getAngle;


/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, PermissionListener {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private PermissionListener mPermissionListener;
    private Handler mHandler;
    private Polyline mPolyline;
    private Marker mMoveMarker;

    private List<Future<Boolean>> mFutureList;
    private ExecutorService mThreadPool;
    private Future<Boolean> mFuture;

    private Callable task = null;
    private LatLng[] latlngs = null;
    private AirLineExecutor mAirLineExecutor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        reqPermission();
        initView(savedInstanceState);
        initData();
        addListener();
    }

    private void initData() {
        mAirLineExecutor = new AirLineExecutor(this, mMoveMarker, mHandler, mMapView);
    }

    private void reqPermission() {
        // 操作有问题需要修改
        requestRuntimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, this);
    }

    private void addListener() {
        findViewById(R.id.btn_cat_screen).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_action_left).setOnClickListener(this);
        findViewById(R.id.btn_action_up).setOnClickListener(this);
        findViewById(R.id.btn_action_right).setOnClickListener(this);
        findViewById(R.id.btn_action_down).setOnClickListener(this);
    }

    private void initView(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.onCreate(this, savedInstanceState);
        mBaiduMap = mMapView.getMap();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(new LatLng(22.616598, 114.03769));
        builder.zoom(17.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mHandler = new Handler(Looper.getMainLooper());
        drawPolyLine();
        // moveLooper();
        mMapView.showZoomControls(true);
    }

    private void drawPolyLine() {
        List<LatLng> polylines = new ArrayList<>();
        for (int index = 0; index < AppConstants.latlngs.length; index++) {
            polylines.add(AppConstants.latlngs[index]);
        }

        polylines.add(AppConstants.latlngs[0]);
        PolylineOptions polylineOptions = new PolylineOptions().points(polylines).width(10).color(Color.RED);

        mPolyline = (Polyline) mBaiduMap.addOverlay(polylineOptions);
        OverlayOptions markerOptions;
        markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)).position(polylines.get(0))
                .rotate((float) getAngle(mPolyline, 0));
        mMoveMarker = (Marker) mBaiduMap.addOverlay(markerOptions);

    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        mBaiduMap.clear();
    }

    @Override
    public void onClick(View v) {
        mAirLineExecutor.reset();
        switch (v.getId()) {
            case R.id.btn_cat_screen:
                // 截图，在SnapshotReadyCallback中保存图片到 sd 卡
                mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
                    public void onSnapshotReady(Bitmap snapshot) {
                        mAirLineExecutor.reset();
                        File file = new File("/mnt/sdcard/test.png");
                        FileOutputStream out;
                        try {
                            out = new FileOutputStream(file);
                            if (snapshot.compress(
                                    Bitmap.CompressFormat.PNG, 100, out)) {
                                out.flush();
                                out.close();
                            }
                            Toast.makeText(MainActivity.this,
                                    "屏幕截图成功，图片存在: " + file.toString(),
                                    Toast.LENGTH_SHORT).show();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Toast.makeText(MainActivity.this, "正在截取屏幕图片...",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_start:
                break;
            case R.id.btn_stop:
                latlngs = new LatLng[0];
                break;
            case R.id.btn_action_left:
                // 从左边开始
                // 设置为新的坐标,开始点
                latlngs = AppConstants.latlngs;
                break;
            case R.id.btn_action_up:
                latlngs = AppConstants.latlngs1;
                break;
            case R.id.btn_action_right:
                latlngs = AppConstants.latlngs2;
                break;
            case R.id.btn_action_down:
                latlngs = AppConstants.latlngs3;
                break;
        }
        if (latlngs != null && latlngs.length > 0) {
            mAirLineExecutor.startAction(latlngs);
        }
    }


    /**
     * 申请运行时权限
     */
    public void requestRuntimePermission(String[] permissions, PermissionListener permissionListener) {
        mPermissionListener = permissionListener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            permissionListener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        mPermissionListener.onGranted();
                    } else {
                        mPermissionListener.onDenied(deniedPermissions);
                    }
                }
                break;
        }
    }

    @Override
    public void onGranted() {

    }

    @Override
    public void onDenied(List<String> deniedPermissions) {
        UIUtils.showToast(getString(R.string.write_storage_permission_deny));
    }
}
