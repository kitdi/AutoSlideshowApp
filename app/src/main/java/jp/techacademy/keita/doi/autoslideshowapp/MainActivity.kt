package jp.techacademy.keita.doi.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                changeButtonStatus(mTimer == null)
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            changeButtonStatus(mTimer == null)
        }

        play_pause_button.setOnClickListener {
            if (mTimer == null) {
                changeButtonStatus(false)
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mHandler.post {
                            imageView.setImageURI(viewModel.getStartImageUri(applicationContext))
                        }
                    }
                }, 2000, 2000) // 最初に始動させるまで100ミリ秒、ループの間隔を100ミリ秒 に設定
            } else {
                changeButtonStatus(true)
                mTimer!!.cancel()
                mTimer = null
            }
        }

        forward_button.setOnClickListener {
            imageView.setImageURI(viewModel.getForwardImageUri())
        }

        backward_button.setOnClickListener {
            imageView.setImageURI(viewModel.getBackwardImageUri())
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    changeButtonStatus(mTimer == null)
                }
        }
    }

    private fun changeButtonStatus (status: Boolean) {
        if (status) {
            play_pause_button.text = "再生"
            play_pause_button.isEnabled = true
            forward_button.isEnabled = true
            backward_button.isEnabled = true
            stop_icon.visibility = ImageView.VISIBLE
        } else {
            play_pause_button.text = "停止"
            play_pause_button.isEnabled = true
            forward_button.isEnabled = false
            backward_button.isEnabled = false
            stop_icon.visibility = ImageView.INVISIBLE
        }
    }
}