package jp.techacademy.keita.doi.autoslideshowapp

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private var mUriList = arrayListOf<Uri>()
    private var currentViewImageUri = Uri.EMPTY

    fun getStartImageUri(context: Context): Uri {
        return if (currentViewImageUri == Uri.EMPTY) {
            getContentsInfo(context)
            currentViewImageUri = mUriList[0]
            mUriList[0]
        } else {
            getForwardImageUri()
        }
    }

    fun getForwardImageUri(): Uri {
        var index = mUriList.binarySearch(currentViewImageUri)
        if (index == mUriList.lastIndex) index = 0  else index++
        currentViewImageUri = mUriList[index]
        return currentViewImageUri
    }

    fun getBackwardImageUri(): Uri {
        var index = mUriList.binarySearch(currentViewImageUri)
        if (index == 0) index = mUriList.lastIndex  else index--
        currentViewImageUri = mUriList[index]
        return currentViewImageUri
    }

    private fun getContentsInfo(context: Context) {
        // 画像の情報を取得する
        val resolver = context.contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

        if (cursor!!.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                Log.d("ANDROID", "ID: $id URI : $imageUri")
                mUriList.add(imageUri)
            } while (cursor.moveToNext())
        }
        cursor.close()
    }
}