package com.numan.runningtracker.db_.typeconverters_

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverters
import java.io.ByteArrayOutputStream

class Converters {

    /*
    * Image will be save as byteArray in room
    * Form of ByteArray: 101110101010
    * Image will be converted from bitmap to byte array
    * */
    @TypeConverters
    fun fromBitmap(bmp: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverters
    fun toBitmap(byteArray: ByteArray): Bitmap {

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}