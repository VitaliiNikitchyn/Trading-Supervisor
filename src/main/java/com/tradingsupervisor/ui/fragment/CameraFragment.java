package com.tradingsupervisor.ui.fragment;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tradingsupervisor.R;
import com.tradingsupervisor.viewmodel.PhotoPreviewViewModel;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


public class CameraFragment extends Fragment {

    //Check state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static{
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }

    private TextureView textureView;
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;

    private Size previewImageSize;
    private ImageReader imageReader;

    private static final int REQUEST_PERMISSION = 200;
    //private boolean isFlashSupported;

    private Handler previewSessionHandler;
    private HandlerThread previewSessionThread;
    private Handler imageReaderHandler;
    private HandlerThread imageReaderThread;

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            setUpCameraOutput();
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) { }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) { }
    };

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    ImageReader.OnImageAvailableListener imageReaderListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {

            PhotoPreviewViewModel previewViewModel = ViewModelProviders.of(getActivity())
                    .get(PhotoPreviewViewModel.class);

            Image image = imageReader.acquireLatestImage();
            previewViewModel.addPhoto(image);
            image.close();

            //Toast.makeText(getActivity(), "onImageAvailable", Toast.LENGTH_SHORT).show();
        }
    };


    public CameraFragment() {}

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textureView = view.findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(textureListener);
        view.findViewById(R.id.btnCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        final TextView timerView = view.findViewById(R.id.textView_timer);
        final CountDownTimer timer = new CountDownTimer(16000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerView.setText(Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                getActivity().finish();
            }
        };
        timer.start();


        view.findViewById(R.id.btn_show_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.photo_activity_container, PhotoPreviewFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void takePicture() {
        if (null == cameraDevice)
            return;
        try{
            final CaptureRequest.Builder captureBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            captureBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE,
                    CameraMetadata.NOISE_REDUCTION_MODE_HIGH_QUALITY);

            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            captureSession.capture(captureBuilder.build(), null, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert  texture != null;
            texture.setDefaultBufferSize(previewImageSize.getWidth(), previewImageSize.getHeight());
            Surface surface = new Surface(texture);
            final CaptureRequest.Builder previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            if(cameraDevice == null)
                                return;
                            CameraFragment.this. captureSession = cameraCaptureSession;
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                            //setAutoFlash(mPreviewRequestBuilder);
                            try {
                                cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(),null, previewSessionHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCameraOutput() {
        try {
            CameraManager manager = (CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE);
            for (String camera_id: manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(camera_id);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_BACK) {

                    StreamConfigurationMap map = characteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    assert map != null;

                    Size largest = Collections.min(
                            Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                            new CompareSizesByArea());
                    imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                            ImageFormat.JPEG, 1);
                    imageReader.setOnImageAvailableListener(imageReaderListener, imageReaderHandler);

                    previewImageSize = Collections.max(
                            Arrays.asList(map.getOutputSizes(SurfaceTexture.class)),
                            new CompareSizesByArea());

                    //Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    //isFlashSupported = available == null ? false : available;

                    cameraId = camera_id;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        int permissionCamera = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if(permissionCamera == PackageManager.PERMISSION_GRANTED) {
            try {
                CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
                manager.openCamera(cameraId, stateCallback, previewSessionHandler);
            } catch (CameraAccessException e) {
                Toast.makeText(getActivity(),"Camera open Error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }
    }

    private void closeCamera() {
        if (null != captureSession) {
            captureSession.close();
            captureSession = null;
        }
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getActivity(), "You can't use camera without permissions",
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        if(textureView.isAvailable()) {
            setUpCameraOutput();
            openCamera();
        }
        else textureView.setSurfaceTextureListener(textureListener);
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void stopBackgroundThread() {
        previewSessionThread.quitSafely();
        imageReaderThread.quitSafely();
        try {
            previewSessionThread.join();
            imageReaderThread.join();
            previewSessionThread = null;
            imageReaderThread = null;
            previewSessionHandler = null;
            imageReaderHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        previewSessionThread = new HandlerThread("Preview session background");
        previewSessionThread.start();
        previewSessionHandler = new Handler(previewSessionThread.getLooper());

        imageReaderThread = new HandlerThread("Image reader thread");
        imageReaderThread.start();
        imageReaderHandler = new Handler(imageReaderThread.getLooper());
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}
