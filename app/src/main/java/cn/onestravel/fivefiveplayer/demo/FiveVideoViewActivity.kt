package cn.onestravel.fivefiveplayer.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.onestravel.fivefiveplayer.interf.PlayerInterface
import cn.onestravel.fivefiveplayer.kernel.ExoPlayerKernel
import cn.onestravel.fivefiveplayer.kernel.IJKPlayerKernel
import kotlinx.android.synthetic.main.activity_five_video_view.*

/**
 * Created by onestravel on 2020/3/20
 */
open class FiveVideoViewActivity : AppCompatActivity() {
    //    val path: String = (getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath
//        ?: "") + "/Video/OctoMaker_4760_Sand Balls.mp4";
    val path: String = "http://vfx.mtime.cn/Video/2017/03/31/mp4/170331093811717750.mp4"

    //    val path: String = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
    private var rotation: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_five_video_view)

        initData()
    }

    private fun initData() {
        fiveVideoView.setMediaKernelClass(ExoPlayerKernel::class.java)
        fiveVideoView.setDataSource(path)
        fiveVideoView.setOnPreparedListener {
            it.start()
            fiveVideoView.setVideoDisplayType(PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER)
        }
        fiveVideoView.setOnClickListener { }
    }
}