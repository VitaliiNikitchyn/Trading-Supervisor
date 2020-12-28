package com.tradingsupervisor.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.media.MediaActionSound
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tradingsupervisor.R
import com.tradingsupervisor.data.entity.Photo
import com.tradingsupervisor.data.entity.Shop
import com.tradingsupervisor.ui.customview.AutoFitTextureView
import com.tradingsupervisor.viewmodel.PhotoViewModel
import java.util.*
import kotlin.math.max

class CameraFragment : Fragment() {
    companion object {
        const val TAG = "CameraFragment"

        //Check state orientation of output image
        private val ORIENTATIONS = SparseIntArray()
        private const val REQUEST_PERMISSION_CAMERA = 200
        fun newInstance(): CameraFragment {
            return CameraFragment()
        }

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }

    private lateinit var textureView: AutoFitTextureView
    private lateinit var cameraId: String
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var previewSize: Size? = null
    private var imageReader: ImageReader? = null
    private var sensorOrientation = 0
    private var shopID: Long? = null
    private lateinit var photoViewModel: PhotoViewModel
    private lateinit var photoCount: TextView

    //private boolean isFlashSupported;
    private var backgroundHandler: Handler? = null
    private var backgroundThread: HandlerThread? = null
    private val textureListener: SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            setUpCameraOutput(width, height)
            configureTransform(width, height)
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            //configureTransform(width, height); //not needed, activity's orientation is fixed
        }

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
    }

    private val stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            //stateCallback called in background thread
            shopID = activity!!.intent.getLongExtra(Shop.SHOP_ID, -99)
            if (shopID == -99L) activity!!.finish()
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraDevice.close()
        }

        override fun onError(cameraDevice: CameraDevice, i: Int) {
            cameraDevice.close()
        }
    }

    //This code executed in backgroundThread
    private val imageReaderListener = OnImageAvailableListener { imageReader ->
        val image = imageReader.acquireLatestImage()
        val fileName = image.timestamp.toString() + "_" + shopID + ".jpeg"
        val buffer = image.planes[0].buffer
        val photo = Photo()
        photo.filename = fileName
        photo.creationTime = Date()
        photo.shopID = shopID
        photoViewModel.addPhoto(photo, buffer) //sync is ok here
        image.close()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        textureView = view.findViewById(R.id.textureView) //(AutoFitTextureView)
        textureView.surfaceTextureListener = textureListener
        view.findViewById<View>(R.id.btnCapture).setOnClickListener { takePicture() }
        view.findViewById<View>(R.id.btn_show_preview).setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            var fragment = fragmentManager.findFragmentByTag(PhotoMultiplePreviewFragment.TAG)
            if (fragment == null) fragment = PhotoMultiplePreviewFragment.newInstance()
            fragmentManager.beginTransaction()
                    .replace(R.id.photo_activity_container, fragment!!, PhotoMultiplePreviewFragment.TAG)
                    .addToBackStack(null)
                    .commit()
        }
        view.findViewById<View>(R.id.btn_close).setOnClickListener {
            requireActivity().onBackPressed()
            //getActivity().getSupportFragmentManager().popBackStack();
        }
        photoCount = view.findViewById(R.id.photoCountTv)
        return view
    }

    //configures only preview, not final jpeg img file
    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        val rotation = requireContext().display?.rotation!!
        //val rotation = activity!!.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0F, 0F, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0F, 0F, previewSize!!.height.toFloat(), previewSize!!.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale = max(
                    viewHeight.toFloat() / previewSize!!.height,
                    viewWidth.toFloat() / previewSize!!.width)
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        textureView.setTransform(matrix)
    }

    private fun takePicture() {
        if (null == cameraDevice) return
        try {
            val captureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(imageReader!!.surface)
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
            captureBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE,
                    CameraMetadata.NOISE_REDUCTION_MODE_HIGH_QUALITY)
            captureBuilder.set(CaptureRequest.JPEG_QUALITY, 100.toByte())
            val rotation = requireContext().display?.rotation!!
            //val rotation = requireActivity().windowManager.defaultDisplay.rotation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation))
            captureSession!!.capture(captureBuilder.build(), null, null)
            val sound = MediaActionSound()
            sound.play(MediaActionSound.SHUTTER_CLICK)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private fun getOrientation(rotation: Int): Int {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS[rotation] + sensorOrientation + 270) % 360
    }

    private fun createCameraPreview() {
        try {
            val texture = textureView.surfaceTexture!!
            texture.setDefaultBufferSize(previewSize!!.width, previewSize!!.height)
            val surface = Surface(texture)
            val previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(surface)
            cameraDevice!!.createCaptureSession(listOf(surface, imageReader!!.surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                            if (cameraDevice == null) return
                            captureSession = cameraCaptureSession
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                            //setAutoFlash(mPreviewRequestBuilder);
                            try {
                                cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(),
                                        null, backgroundHandler)
                            } catch (e: CameraAccessException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {}
                    }, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun setUpCameraOutput(width: Int, height: Int) {
        try {
            val manager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
            for (camera_id in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(camera_id)
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_BACK) {
                    val map = characteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                    val outputSizes = map.getOutputSizes(ImageFormat.JPEG) //sorted from biggest to small
                    //Arrays.sort(outputSizes, new CompareSizesByArea());
                    /*
                    Size largest = Collections.min(
                            Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                            new CompareSizesByArea());*/

                    //Size minimal = outputSizes[1];  //minimal + 1
                    //imageReader = ImageReader.newInstance(minimal.getWidth(), minimal.getHeight(),ImageFormat.JPEG, 1);
                    imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 1)
                    imageReader!!.setOnImageAvailableListener(imageReaderListener, backgroundHandler)
                    sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!

                    /* try without this
                    // Find out if we need to swap dimension to get the preview size relative to sensor
                    // coordinate.
                    int displayRotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                    //noinspection ConstantConditions
                    sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    boolean swappedDimensions = false;
                    switch (displayRotation) {
                        case Surface.ROTATION_0:
                        case Surface.ROTATION_180:
                            if (sensorOrientation == 90 || sensorOrientation == 270) {
                                swappedDimensions = true;
                            }
                            break;
                        case Surface.ROTATION_90:
                        case Surface.ROTATION_270:
                            if (sensorOrientation == 0 || sensorOrientation == 180) {
                                swappedDimensions = true;
                            }
                            break;
                    }*/
//                    val displaySize = Point()
//                    //activity!!.windowManager.defaultDisplay.getSize(displaySize)
//                    val maxPreviewWidth = displaySize.x
//                    val maxPreviewHeight = displaySize.y

                    /* try without this
                    if (swappedDimensions) {
                        rotatedPreviewWidth = height;
                        rotatedPreviewHeight = width;
                        maxPreviewWidth = displaySize.y;
                        maxPreviewHeight = displaySize.x;
                    }

                    if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                        maxPreviewWidth = MAX_PREVIEW_WIDTH;
                    }
                    if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                        maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                    }*/

                    // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                    // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                    // garbage capture data
                    val bounds = requireActivity().windowManager.currentWindowMetrics.bounds
                    previewSize = chooseOptimalSize(
                            map.getOutputSizes(SurfaceTexture::class.java),
                            width, height,
                            maxWidth = bounds.width(),
                            maxHeight = bounds.height(),
                            outputSizes[0] /*largest*/)

                    //orientation is only portrait
                    textureView.setAspectRatio(previewSize!!.height, previewSize!!.width)

                    /* We fit the aspect ratio of TextureView to the size of preview we picked.
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
                    } else {
                        textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
                    }*/


                    //Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    //isFlashSupported = available == null ? false : available;
                    cameraId = camera_id
                    break
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace();
            Toast.makeText(activity, "setup camera outputs error", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
    }

    private fun openCamera() {
        val mActivity = requireActivity()
        val permissionCamera = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
        if (permissionCamera == PackageManager.PERMISSION_GRANTED) {
            try {
                val manager = mActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                manager.openCamera(cameraId!!, stateCallback, backgroundHandler)
            } catch (e: CameraAccessException) {
                Toast.makeText(activity, "Camera open Error", Toast.LENGTH_SHORT).show()
                e.printStackTrace();
                mActivity.finish()
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
        }
    }

    private fun closeCamera() {
        if (null != captureSession) {
            captureSession!!.close()
            captureSession = null
        }
        if (null != cameraDevice) {
            cameraDevice!!.close()
            cameraDevice = null
        }
        if (null != imageReader) {
            imageReader!!.close()
            imageReader = null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBackgroundThread()
                if (textureView.isAvailable) {
                    setUpCameraOutput(textureView.width, textureView.height)
                    configureTransform(textureView.width, textureView.height)
                    openCamera()
                } else textureView.surfaceTextureListener = textureListener
            } else {
                Toast.makeText(activity, "You can't use camera without permissions",
                        Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        photoViewModel = ViewModelProvider(requireActivity()).get(PhotoViewModel::class.java)
        photoViewModel.photoCount.observe(viewLifecycleOwner) { number ->
            if (number == null) return@observe
            photoCount.text = number.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (textureView.isAvailable) {
            setUpCameraOutput(textureView.width, textureView.height)
            configureTransform(textureView.width, textureView.height)
            openCamera()
        } else textureView.surfaceTextureListener = textureListener
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun stopBackgroundThread() {
        backgroundThread!!.quitSafely()
        try {
            backgroundThread!!.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("Preview session")
        backgroundThread!!.start()
        backgroundHandler = Handler(backgroundThread!!.looper)

        /*
        imageReaderThread = new HandlerThread("Image reader thread");
        imageReaderThread.start();
        imageReaderHandler = new Handler(imageReaderThread.getLooper());*/
    }

    /**
     * Given `choices` of `Size`s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     * class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal `Size`, or an arbitrary one if none were big enough
     */
    private fun chooseOptimalSize(
            choices: Array<Size>, textureViewWidth: Int, textureViewHeight: Int,
            maxWidth: Int, maxHeight: Int, aspectRatio: Size): Size {

        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough: MutableList<Size> = ArrayList()
        // Collect the supported resolutions that are smaller than the preview Surface
        val notBigEnough: MutableList<Size> = ArrayList()
        val w = aspectRatio.width
        val h = aspectRatio.height
        for (option in choices) {
            if (option.width <= maxWidth && option.height <= maxHeight && option.height == option.width * h / w) {
                if (option.width >= textureViewWidth &&
                        option.height >= textureViewHeight) {
                    bigEnough.add(option)
                } else {
                    notBigEnough.add(option)
                }
            }
        }
        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        return when {
            bigEnough.size > 0 -> Collections.min(bigEnough, CompareSizesByArea())
            notBigEnough.size > 0 -> Collections.max(notBigEnough, CompareSizesByArea())
            else -> choices[0]
        }
    }

    internal class CompareSizesByArea : Comparator<Size> {
        override fun compare(lhs: Size, rhs: Size): Int {
            return java.lang.Long.signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)
        }
    }
}