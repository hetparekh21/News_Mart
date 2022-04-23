package com.example.newsmart.Data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.sql.Date;

public class Converters {

    @TypeConverter
    public static Bitmap byteTobit(byte[] array) {

        if (array != null) {

            Bitmap.Config configBmp = Bitmap.Config.valueOf("JPEG");
            Bitmap bitmap_tmp = Bitmap.createBitmap(300, 250, configBmp);
            ByteBuffer buffer = ByteBuffer.wrap(array);
            bitmap_tmp.copyPixelsFromBuffer(buffer);

            return bitmap_tmp ;

        }

//        if (array != null) {
//
////            final BitmapFactory.Options options = new BitmapFactory.Options();
////            options.inSampleSize = calculateInSampleSize(options, 300, 250);
////            options.inMutable = true;
////            options.inJustDecodeBounds = true ;
//            Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
//            return bitmap;
//
//        }

        return null;
    }

    @TypeConverter
    public static byte[] bitTobyte(Bitmap bitmap) {

//        if (bitmap != null) {
//
//            ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
//            bitmap.copyPixelsToBuffer(byteBuffer);
//            byteBuffer.rewind();
//            return byteBuffer.array();
//
//        }


//        if (bitmap != null) {
//
//            int size = bitmap.getRowBytes() * bitmap.getHeight();
//            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
//            bitmap.copyPixelsToBuffer(byteBuffer);
//            byte[] byteArray = byteBuffer.array();
//            return  byteArray ;
//
//        }

//        if (bitmap != null) {
//
//            ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
//            bitmap.copyPixelsToBuffer(byteBuffer);
//            byteBuffer.rewind();
//            return byteBuffer.array();
//
//        }

        if (bitmap != null) {

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            return output.toByteArray();

        }

        return null;

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
