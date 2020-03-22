package cn.onestravel.fivefiveplayer.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.onestravel.fivefiveplayer.interf.PlayerInterface
import kotlinx.android.synthetic.main.activity_video_player.*

/**
 * Created by onestravel on 2020/3/20
 */
open class FiveVideoPlayerActivity : AppCompatActivity() {
    //    val path: String = (getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath
//        ?: "") + "/Video/OctoMaker_4760_Sand Balls.mp4";
    val path: String = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
//    val path: String = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4"
//    val path: String = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
    private var rotation: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        initData()
    }

    private fun initData() {
        fiveVideoPlayer.setDataSource(path)
        fiveVideoPlayer.setOnPreparedListener {
            it.start()
//            it.setSpeed(1.5f)
//            it.setVideoRotation(90f)
            it.setVideoDisplayType(PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER)
        }
        fiveVideoPlayer.setOnClickListener { }
    }
}