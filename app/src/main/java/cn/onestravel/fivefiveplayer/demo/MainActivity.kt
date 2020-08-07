package cn.onestravel.fivefiveplayer.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.onestravel.fivefiveplayer.FivePlayer
import cn.onestravel.fivefiveplayer.FiveVideoPlayer
import cn.onestravel.fivefiveplayer.FiveVideoPlayerActivity
import cn.onestravel.fivefiveplayer.kernel.ExoPlayerKernel
import cn.onestravel.fivefiveplayer.kernel.IJKPlayerKernel
import kotlinx.android.synthetic.main.activity_five_video_view.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //    val path: String = (getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath
//        ?: "") + "/Video/OctoMaker_4760_Sand Balls.mp4";
    val path: String = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"

    //    val path: String = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4"
//    val path: String = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        FivePlayer.mediaKernelClass = IJKPlayerKernel::class.java
//        FivePlayer.mediaKernelClass = ExoPlayerKernel::class.java
        btnFiveVideoView.setOnClickListener {
            val intent = Intent(this@MainActivity, FiveVideoViewActivity::class.java)
            startActivity(intent)
        }
        btnFiveVideoPlayer.setOnClickListener {
            FiveVideoPlayerActivity.start(this, path, "玩具总动员")
        }
        btnVideoList.setOnClickListener {
            val intent = Intent(this@MainActivity, ListVideoActivity::class.java)
            startActivity(intent)
        }
        btnTikTokList.setOnClickListener {
            val intent = Intent(this@MainActivity, TikTokListActivity::class.java)
            startActivity(intent)
        }

    }
}
