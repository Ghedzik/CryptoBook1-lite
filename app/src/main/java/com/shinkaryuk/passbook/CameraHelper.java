package com.shinkaryuk.passbook;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

/**
 * Created by shinkaryuk on 06.01.2018.
 */

public class CameraHelper {
    private CameraManager mCameraManager = null;
    private String mCameraID = null;
    private CameraDevice mCameraDevice = null;
    private Context mContext;
    private TextureView mTextureView = null;

    public CameraHelper(@NonNull CameraManager cameraManager, @NonNull String cameraID, Context c) {
        mCameraManager = cameraManager;
        mCameraID = cameraID;
        mContext = c;
    }

    public void viewFormatSize(int formatSize) {
        // Получения характеристик камеры
        CameraCharacteristics cc = null;
        try {
            cc = mCameraManager.getCameraCharacteristics(mCameraID);
            // Получения списка выходного формата, который поддерживает камера
            StreamConfigurationMap configurationMap = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            // Получения списка разрешений которые поддерживаются для формата jpeg
            Size[] sizesJPEG = configurationMap.getOutputSizes(ImageFormat.JPEG);
            if (sizesJPEG != null) {
                for (Size item : sizesJPEG) {
                    Log.i("LOG", "w:" + item.getWidth() + " h:" + item.getHeight());
                }
            } else {
                Log.e("LOG", "camera with id: " + mCameraID + " don`t support format: " + formatSize);
            }
        } catch (CameraAccessException e) {
            Log.e("eLOG", e.getMessage());             //e.printStackTrace();
        }
    }

    public boolean isOpen() {
        if (null == mCameraDevice)
            return false;
         else
            return true;

    }

    public void openCamera() {
        try {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mCameraManager.openCamera(mCameraID, mCameraCallback, null);

        } catch (CameraAccessException e) {
            Log.e("LOG",e.getMessage());         //e.printStackTrace();
        }
    }

    public void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            createCameraPreviewSession();
            Log.i("LOG", "Open camera  with id:"+mCameraDevice.getId());
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            mCameraDevice.close();
            Log.i("LOG", "disconnect camera with id:"+mCameraDevice.getId());
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.i("LOG", "error! camera id:"+camera.getId()+" error:"+error);
        }
    };

    public void setTextureView(TextureView tv){
        mTextureView = tv;
    }

    private void createCameraPreviewSession() {
        SurfaceTexture texture = mTextureView.getSurfaceTexture();//mTextureView.getSurfaceTexture();
        assert texture != null;
        texture.setDefaultBufferSize(1920,1080);
        Surface surface = new Surface(texture);

        try {
            final CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);

            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    CameraCaptureSession mSession = session;
                    try {
                        mSession.setRepeatingRequest(builder.build(),null,null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }}, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
