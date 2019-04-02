package com.kross.taxi_passenger.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException


object BitmapHelper {

    fun getDownSampledBitmap(context: Context, targetWidth: Int, targetHeight: Int, uri: Uri? = null, photoPath: String? = null): Bitmap? {
        var outDimens: BitmapFactory.Options? = null
        var width = 0
        var height = 0
        uri?.let {
            outDimens = getBitmapDimensions(context, it, null)
            width = outDimens!!.outWidth
            height = outDimens!!.outHeight
        }

        photoPath?.let {
            outDimens = getBitmapDimensions(context, null, it)
            width = outDimens!!.outWidth
            height = outDimens!!.outHeight
        }

        val sampleSize = calculateSampleSize(width, height, targetWidth, targetHeight)

        return rotateBitmap(downSampleBitmap(context, uri, photoPath, sampleSize)!!, photoPath, context)
    }

    @Throws(FileNotFoundException::class, IOException::class)
    private fun getBitmapDimensions(context: Context, uri: Uri?, photoPath: String?): BitmapFactory.Options {
        val outDimens = BitmapFactory.Options()
        outDimens.inJustDecodeBounds = true // the decoder will return null (no bitmap)

        uri?.let {
            val inputStream = context.getContentResolver().openInputStream(uri)
            // if Options requested only the size will be returned
            inputStream.use { BitmapFactory.decodeStream(inputStream, null, outDimens) }
        }

        photoPath?.let {
            val input = FileInputStream(File(photoPath))
            input.use { BitmapFactory.decodeStream(input, null, outDimens) }
        }

        return outDimens
    }

    private fun rotateBitmap(bmp: Bitmap, path: String?, context: Context): Bitmap {
        var rotatedBitmap = bmp
        if (path != null) {
            val ei: ExifInterface?
            try {
                ei = ExifInterface(File(path).path)
                val dfd = context.contentResolver.openInputStream(Uri.fromFile(File(path)))
                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                Log.d("AAAAAAA", " $orientation")
                rotatedBitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(bmp, 90)
                    ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(bmp, 180)
                    ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(bmp, 270)
                    ExifInterface.ORIENTATION_UNDEFINED -> {
                        TransformationUtils.rotateImage(bmp, 270)
                    }
                    ExifInterface.ORIENTATION_NORMAL -> bmp
                    else -> bmp
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return rotatedBitmap
    }

    private fun calculateSampleSize(width: Int, height: Int, targetWidth: Int, targetHeight: Int): Int {
        var inSampleSize = 1

        if (height > targetHeight || width > targetWidth) {

            val heightRatio = Math.round(height.toFloat() / targetHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / targetWidth.toFloat())

            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    @Throws(FileNotFoundException::class, IOException::class)
    private fun downSampleBitmap(context: Context, uri: Uri?, photoPath: String?, sampleSize: Int): Bitmap? {
        var resizedBitmap: Bitmap? = null
        val outBitmap = BitmapFactory.Options()
        outBitmap.inJustDecodeBounds = false // the decoder will return a bitmap
        outBitmap.inSampleSize = sampleSize


        uri?.let {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream.use {
                resizedBitmap = BitmapFactory.decodeStream(inputStream, null, outBitmap)
            }
        }

        photoPath?.let {
            val input = FileInputStream(File(photoPath))
            input.use {
                resizedBitmap = BitmapFactory.decodeStream(input, null, outBitmap)
            }
        }

        return resizedBitmap
    }

}