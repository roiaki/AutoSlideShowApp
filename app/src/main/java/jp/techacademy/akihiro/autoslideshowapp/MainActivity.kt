package jp.techacademy.akihiro.autoslideshowapp

import android.Manifest

import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    lateinit var cursor: Cursor
    private var mTimer: Timer? = null
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
                Log.d("ANDROID", "許可されている")
            } else {
                Log.d("ANDROID", "許可されていない")
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }

        // 1つのActivityで複数のボタンにリスナーを登録して似たような処理行う場合はActivityで実装する方法を選び、
        // 各ボタンで行う処理が明らかに違う場合などは直接記述する
        forward_button.setOnClickListener {
            moveToNextIamge()
        }

        back_button.setOnClickListener {
            returnImage()
        }

        play_stop_button.setOnClickListener {
            stopAndGo()
        }
    }

    // 権限確認
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                    Log.d("ANDROID", "許可された")
                } else {
                    Log.d("ANDROID", "許可されなかった")
                }
        }
    }

    // 起動時に画像を表示する
    private fun getContentsInfo() {
        play_stop_button.text = "再生"

        // 画像の情報を取得する
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )!!

        // cursor!!.moveToFirst() 取得した結果に対するカーソルを先頭に移動させる 「!! not-nullアサーション演算子」
        if (cursor!!.moveToFirst()) {

            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            Log.d("ANDROID", "URI : " + imageUri.toString())
            var pos = cursor.position.toString()
            Log.d("ANDROID", pos)
            imageView.setImageURI(imageUri)
        }
    }

    // 次の画像を表示する
    private fun moveToNextIamge() {
        if (cursor.isLast) {
            cursor.moveToFirst()
        } else {
            cursor.moveToNext()
        }
        // indexからIDを取得し、そのIDから画像のURIを取得する
        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
        // 画像のIDを取得
        val id = cursor.getLong(fieldIndex)

        val imageUri =
            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

        imageView.setImageURI(imageUri)
        var strId = id.toString()
        Log.d("ANDROID", strId)
    }

    // 前の画像を表示する
    private fun returnImage() {
        if (cursor.isFirst) {
            cursor.moveToLast()
        } else {
            cursor.moveToPrevious()
        }
        // indexからIDを取得し、そのIDから画像のURIを取得する
        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
        // 画像のIDを取得
        val id = cursor.getLong(fieldIndex)

        val imageUri =
            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

        imageView.setImageURI(imageUri)
        var ids = id.toString()
        Log.d("ANDROID", ids)
    }

    // スライドショー起動と停止
    private fun stopAndGo() {
        if (mTimer == null) {
            play_stop_button.text = "停止"
            // タイマーの作成
            mTimer = Timer()
            // タイマーの始動
            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    mHandler.post {
                        moveToNextIamge()
                    }
                }
            }, 2000, 2000) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定

            forward_button.isEnabled = false
            back_button.isEnabled = false

        } else {
            Log.d("a", mTimer.toString())
            if (mTimer != null) {
                play_stop_button.text = "再生"
                mTimer!!.cancel()
                mTimer = null
            }
            forward_button.isEnabled = true
            back_button.isEnabled = true
            Log.d("a", mTimer.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Android", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Android", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Android", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Android", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        cursor!!.close()
        Log.d("Android", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Android", "onRestart")
    }
}