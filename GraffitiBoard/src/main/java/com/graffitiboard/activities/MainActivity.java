package com.graffitiboard.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.graffitiboard.view.GraffitiBoard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends BaseAppCompatActivity implements
        GraffitiBoard.UndoRedoCallback, Handler.Callback{

    private View mUndoView;
    private View mRedoView;
    private View mPenView;
    private View mEraserView;
    private View mClearView;
    private AppCompatSeekBar mSeekBar;
    private GraffitiBoard mGraffitiBoard;
    private ProgressDialog mSaveProgressDlg;
    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;
    private Handler mHandler;

    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0x00000001;

    public static final int MOUNT_UNMOUNT_FILESYSTEMS_REQUEST_CODE = 0x00000002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSeekBar = (AppCompatSeekBar)findViewById(R.id.seek_bar) ;
        mSeekBar.setOnSeekBarChangeListener(changeListener);

        mGraffitiBoard = (GraffitiBoard) findViewById(R.id.palette);
        mGraffitiBoard.setUndoRedoCallback(this);

        mUndoView = findViewById(R.id.undo);
        mRedoView = findViewById(R.id.redo);
        mPenView = findViewById(R.id.pen);
        mPenView.setSelected(true);
        mEraserView = findViewById(R.id.eraser);
        mClearView = findViewById(R.id.clear);

        mUndoView.setOnClickListener(this);
        mRedoView.setOnClickListener(this);
        mPenView.setOnClickListener(this);
        mEraserView.setOnClickListener(this);
        mClearView.setOnClickListener(this);

        mUndoView.setEnabled(false);
        mRedoView.setEnabled(false);
        mHandler = new Handler(this);
    }

    @Override
    public void onUndoRedoStatusChanged() {
        mUndoView.setEnabled(mGraffitiBoard.isRevoke());
        mRedoView.setEnabled(mGraffitiBoard.isRecovery());
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case MSG_SAVE_FAILED:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this,"保存失败",Toast.LENGTH_SHORT).show();
                break;
            case MSG_SAVE_SUCCESS:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this,"画板已保存",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_SAVE_FAILED);
        mHandler.removeMessages(MSG_SAVE_SUCCESS);
    }

    private void initSaveProgressDlg(){
        mSaveProgressDlg = new ProgressDialog(this);
        mSaveProgressDlg.setMessage("正在保存,请稍候...");
        mSaveProgressDlg.setCancelable(false);
    }

    private static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    private static String saveImage(Bitmap bmp, int quality) {
        if (bmp == null) {
            return null;
        }
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            return null;
        }
        File appDir = new File(Environment.getExternalStorageDirectory(), "graffiti");
        if (!(appDir.exists() && appDir.isDirectory())){
            if (!(appDir.mkdirs())){
                return null;
            }
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                performCodeWithPermission("GraffitiBoard需要申请权限",new BaseAppCompatActivity.PermissionCallback() {
                    @Override
                    public void hasPermission() {
                        if(mSaveProgressDlg==null){
                            initSaveProgressDlg();
                        }
                        mSaveProgressDlg.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bm = mGraffitiBoard.buildBitmap();
                                String savedFile = saveImage(bm, 100);
                                if (savedFile != null) {
                                    scanFile(MainActivity.this, savedFile);
                                    mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                                }else{
                                    mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                                }
                            }
                        }).start();
                    }
                    @Override
                    public void noPermission() {
                    }
                }, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
        }
        return true;
    }

    SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mGraffitiBoard.setGraffitiSize(seekBar.getProgress());
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.undo:
                mGraffitiBoard.revoke();
                break;
            case R.id.redo:
                mGraffitiBoard.recovery();
                break;
            case R.id.pen:
                v.setSelected(true);
                mEraserView.setSelected(false);
                break;
            case R.id.eraser:
                v.setSelected(true);
                mPenView.setSelected(false);
                break;
            case R.id.clear:
                mGraffitiBoard.doClear();
                break;
        }
    }
}
