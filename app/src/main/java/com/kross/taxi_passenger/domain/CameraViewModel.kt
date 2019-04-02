package com.kross.taxi_passenger.domain

import android.app.Application
import android.content.Context
import android.graphics.*
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.Resolution
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import io.fotoapparat.selector.front
import io.fotoapparat.view.CameraView
import java.io.File



class CameraViewModel(application: Application) : BaseViewModel(application) {

    fun initBackCamera(cameraView: CameraView, context: Context, viewAspect: Float): Fotoapparat {
        return Fotoapparat(
                context = context,
                view = cameraView,
                scaleType = ScaleType.CenterCrop,
                cameraConfiguration = CameraConfiguration(pictureResolution = {
                    sortedByDescending(Resolution::area)
                            .filter { it.height <= 780 }
                            .firstOrNull {
                                it.flipDimensions().aspectRatio <= viewAspect
                            }
                            ?: maxBy(Resolution::aspectRatio)
                }),
                lensPosition = back())
    }

    fun initFrontCamera(cameraView: CameraView, context: Context, viewAspect: Float): Fotoapparat {
        return Fotoapparat(
                context = context,
                view = cameraView,
                scaleType = ScaleType.CenterCrop,
                cameraConfiguration = CameraConfiguration(pictureResolution = {
                    sortedByDescending(Resolution::area)
                            .filter { it.height <= 1200 }
                            .firstOrNull { it.flipDimensions().aspectRatio <= viewAspect }
                            ?: maxBy(Resolution::aspectRatio)
                }),
                lensPosition = front())
    }

    fun transform(width: Int, src: Bitmap, degrees: Float, frameWidthRaw: Int, frameHeightRaw: Int, isCircle: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(360f - degrees)

        val heightProportion = frameWidthRaw / width.toDouble()
        val frameHeight = (src.height * heightProportion).toInt()
        val frameWidth = (frameHeight * frameHeightRaw.toDouble() / frameWidthRaw).toInt()

        val centerX = src.width / 2
        val centerY = src.height / 2

//        if (!isCircle) {
//            matrix.postScale(1f, -1f, centerX.toFloat(), centerY.toFloat())
//            matrix.postScale(-1f, 1f, centerX.toFloat(), centerY.toFloat())
//        }

        return Bitmap.createBitmap(
                src,
                centerX - frameWidth / 2,
                centerY - frameHeight / 2,
                frameWidth,
                frameHeight,
                matrix,
                true
        )
    }

    fun createCircularBitmap(srcBitmap: Bitmap): Bitmap {
        val squareBitmapWidth = Math.min(srcBitmap.width, srcBitmap.height)

        // Initialize a new instance of Bitmap
        val dstBitmap = Bitmap.createBitmap(
                squareBitmapWidth, // Width
                squareBitmapWidth, // Height
                Bitmap.Config.ARGB_8888 // Config
        )
        val canvas = Canvas(dstBitmap)

        // Initialize a new Paint instance
        val paint = Paint()
        paint.isAntiAlias = true
        val rect = Rect(0, 0, squareBitmapWidth, squareBitmapWidth)
        val rectF = RectF(rect)
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        // Calculate the left and top of copied bitmap
        val left = ((squareBitmapWidth - srcBitmap.width) / 2).toFloat()
        val top = ((squareBitmapWidth - srcBitmap.height) / 2).toFloat()

        canvas.drawBitmap(srcBitmap, left, top, paint)

        srcBitmap.recycle()

        return dstBitmap
    }

    fun prepareFile(context: Context, requestCode: Int?): File {
        val dir = File(context.filesDir, "images")
        dir.mkdirs()
        return File(dir, "photo$requestCode.jpg")
    }

}