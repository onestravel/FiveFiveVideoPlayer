package cn.onestravel.fivefiveplayer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import cn.onestravel.fivefiveplayer.interf.PlayerInterface
import cn.onestravel.fivefiveplayer.utils.VideoUtils
import kotlinx.android.synthetic.main.five_activity_video_player.*
import java.lang.Exception

/**
 * @author onestravel
 * @createTime 2020-03-22
 * @description 视频播放器
 */
open class FiveVideoPlayerActivity : AppCompatActivity() {
    private var isPlaying: Boolean = false
    private var mediaDataSource: MediaDataSource? = null
    protected open var autoStart: Boolean = false

    companion object {
        const val VIDEO_PATH: String = "videoPath"
        const val VIDEO_TITLE: String = "videoTitle"
        const val VIDEO_LOOPING: String = "videoLooping"

        fun start(context: Context, url: String) {
            start(context, url, "")
        }

        fun start(context: Context, url: String, title: String) {
            start(context, url, title, false)
        }

        fun start(context: Context, url: String, title: String, looping: Boolean) {
            val intent = Intent(context, FiveVideoPlayerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(VIDEO_PATH, url)
            intent.putExtra(VIDEO_TITLE, title)
            intent.putExtra(VIDEO_LOOPING, looping)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.five_activity_video_player)
        try {
            VideoUtils.setWindowStatusBarColor(this, R.color.five_color_bg_color_black)
            VideoUtils.hideActionBar(this)
            VideoUtils.setNavigationBarColor(this, R.color.five_color_bg_color_black)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val path = intent.getStringExtra(VIDEO_PATH)
        var title = intent.getStringExtra(VIDEO_TITLE)
        val looping = intent.getBooleanExtra(VIDEO_LOOPING, false)
        var uri = intent.data
        if (TextUtils.isEmpty(path) && uri == null) {
            finish()
            return
        }
        if (uri == null) {
            uri = Uri.parse(path)
        } else {
            autoStart = true
            title = uri.path?.substring((uri.path?.lastIndexOf("/") ?: -1) + 1) ?: ""
        }
        mediaDataSource = MediaDataSource(title, uri, looping)
        initData()
    }

    private fun initData() {
//        val path = intent.getStringExtra(VIDEO_PATH)
//        val title = intent.getStringExtra(VIDEO_TITLE)
//        val looping = intent.getBooleanExtra(VIDEO_LOOPING, false)
//        var uri = intent.data
//        if (TextUtils.isEmpty(path) && uri == null) {
//            finish()
//            return
//        }
//        if (uri == null) {
//            uri = Uri.parse(path);
//        }
//        mediaDataSource = MediaDataSource(title, uri, looping)
        fiveVideoPlayer.setOnBackPressListener { finish() }
        fiveVideoPlayer.setOnPreparedListener {
            if(autoStart){
                it.start()
            }
            it.setVideoDisplayType(PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER)
        }
        mediaDataSource?.let {
            fiveVideoPlayer.setDataSource(it)
        }

    }

    override fun onResume() {
        super.onResume()
        if (isPlaying) {
            fiveVideoPlayer?.let {
                it.resume()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fiveVideoPlayer?.let {
            isPlaying = it.isPlaying();
            it.pause()
        }
    }

    override fun onDestroy() {
        try {
            fiveVideoPlayer?.let {
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}