package com.graffitiboard.activities;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by antiy2015 on 2017/7/24.
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity implements View.OnClickListener {
    private int PERMISSION_REQUEST_CODE = 88;
    private PermissionCallback mPermissionCallback;
    public interface PermissionCallback{
        void hasPermission();
        void noPermission();
    }

    /**
     * Android M运行时权限请求封装
     * @param permissionDes 权限描述
     * @param permissionCallback 请求权限回调
     * @param permissions 请求的权限（数组类型），直接从Manifest中读取相应的值，比如Manifest.permission.WRITE_CONTACTS
     */
    public void performCodeWithPermission(@NonNull String permissionDes, PermissionCallback permissionCallback, @NonNull String... permissions){
        if(permissions == null || permissions.length == 0)return;
        this.mPermissionCallback = permissionCallback;
        if((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || checkPermissionGranted(permissions)){
            if(mPermissionCallback !=null){
                mPermissionCallback.hasPermission();
                mPermissionCallback = null;
            }
        }else{
            //permission has not been granted.
            requestPermission(permissionDes, PERMISSION_REQUEST_CODE,permissions);
        }

    }
    private boolean checkPermissionGranted(String[] permissions){
        boolean flag = true;
        for(String p:permissions){
            if(ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED){
                flag = false;
                break;
            }
        }
        return flag;
    }
    private void requestPermission(String permissionDes,final int requestCode,final String[] permissions){
        if(shouldShowRequestPermissionRationale(permissions)){
            //如果用户之前拒绝过此权限，再提示一次准备授权相关权限
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(permissionDes)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(BaseAppCompatActivity.this, permissions, requestCode);
                        }
                    }).show();

        }else{
            ActivityCompat.requestPermissions(BaseAppCompatActivity.this, permissions, requestCode);
        }
    }
    private boolean shouldShowRequestPermissionRationale(String[] permissions){
        boolean flag = false;
        for(String p:permissions){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,p)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(verifyPermissions(grantResults)){
                if(mPermissionCallback !=null) {
                    mPermissionCallback.hasPermission();
                    mPermissionCallback = null;
                }
            }else{
                showToast("暂无权限执行相关操作！");
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                String pkg = "com.android.settings";
                String cls = "com.android.settings.applications.InstalledAppDetails";
                intent.setComponent(new ComponentName(pkg, cls));
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                if(mPermissionCallback !=null) {
                    mPermissionCallback.noPermission();
                    mPermissionCallback = null;
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
    public boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void showToast(String msg){
        Toast.makeText(BaseAppCompatActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
