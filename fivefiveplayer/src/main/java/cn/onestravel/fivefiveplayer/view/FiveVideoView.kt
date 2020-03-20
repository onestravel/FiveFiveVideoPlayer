package cn.onestravel.fivefiveplayer.view

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import cn.onestravel.fivefiveplayer.FivePlayer
import cn.onestravel.fivefiveplayer.R
import cn.onestravel.fivefiveplayer.VideoDisplayTypeDef
import cn.onestravel.fivefiveplayer.interf.OnPreparedListener
import cn.onestravel.fivefiveplayer.interf.PlayerCallBack
import cn.onestravel.fivefiveplayer.interf.PlayerInterface
import cn.onestravel.fivefiveplayer.utils.VideoUtils


/**
 * Created by onestravel on 2020/3/20
 */
typealias OnDoubleClickListener = (View) -> Unit

class FiveVideoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    PlayerInterface,
    PlayerCallBack, View.OnClickListener {
    private var mVisibleResume: Boolean = false;
    private val mTextureView: VideoTextureView by lazy { VideoTextureView(context) }
    private val mPlayIv: ImageView by lazy { ImageView(context) }
    private val mThumbIv: ImageView by lazy { ImageView(context) }
    private val mLoadingBar: ProgressBar by lazy { ProgressBar(context) }
    private val mPlayer: FivePlayer by lazy { FivePlayer() }
    private var onPreparedListener: OnPreparedListener? = null
    private var onDoubleClickListener: OnDoubleClickListener? = null
    private var onClickListener: OnClickListener? = null
    private var mDoubleClickPlay: Boolean = true
    private var mClickPlay: Boolean = false


    init {
        val lpTextureView = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lpTextureView.gravity = Gravity.CENTER
        mTextureView.layoutParams = lpTextureView
        addView(mTextureView)

        val lpThumb = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        lpThumb.gravity = Gravity.CENTER
        mThumbIv.layoutParams = lpThumb
        mThumbIv.scaleType = ImageView.ScaleType.FIT_CENTER
        addView(mThumbIv)


        val lpIvPlay = LayoutParams(VideoUtils.dp2px(context, 30f), VideoUtils.dp2px(context, 30f))
        lpIvPlay.gravity = Gravity.CENTER
        mPlayIv.layoutParams = lpIvPlay
        mPlayIv.setImageResource(R.drawable.drawable_five_icon_play)
        mPlayIv.visibility = View.GONE
        addView(mPlayIv)


        val lpLoading = LayoutParams(VideoUtils.dp2px(context, 30f), VideoUtils.dp2px(context, 30f))
        lpLoading.gravity = Gravity.CENTER
        mLoadingBar.layoutParams = lpLoading
        val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resources.getDrawable(R.drawable.drawable_five_progress_bar, null)
        } else {
            resources.getDrawable(R.drawable.drawable_five_progress_bar)
        }
        mLoadingBar.indeterminateDrawable = drawable

        addView(mLoadingBar)

        mPlayer.attachTextureView(mTextureView)
        mPlayer.setPlayerCallBack(this)
        super.setOnClickListener(this)
    }

    override fun setDataSource(url: String) {
        mPlayer.setDataSource(url)
    }

    /**
     * 设置视频显示类型
     * @param type { #PlayerInterface#VIDEO_DISPLAY_TYPE_ADAPTER,
     *                  PlayerInterface#VIDEO_DISPLAY_TYPE_ORIGINAL,
     *                  PlayerInterface#VIDEO_DISPLAY_TYPE_FIT_CENTER,
     *                  PlayerInterface#VIDEO_DISPLAY_TYPE_CENTER_CROP
     *                  }
     */
    fun setVideoDisplayType(@VideoDisplayTypeDef type: Int) {
        mTextureView.videoDisplayType = type
    }

    /**
     * 设置单击播放/暂停
     */
    fun setClickPlay(clickPlay: Boolean) {
        this.mClickPlay = clickPlay
        if (mClickPlay) {
            mDoubleClickPlay = false
        }
    }


    /**
     * 设置双击播放/暂停
     */
    fun setDoubleClickPlay(doubleClickPlay: Boolean) {
        this.mDoubleClickPlay = doubleClickPlay
        if (mDoubleClickPlay) {
            mClickPlay = false
        }
    }

    override fun start() {
        mPlayer.start()
        mLoadingBar.visibility = View.VISIBLE
    }

    override fun start(position: Long) {
        mPlayer.start(position)
    }

    override fun pause() {
        mPlayer.pause()
    }

    override fun stop() {
        mPlayer.stop()
    }

    override fun resume() {
        mPlayer.resume()
    }

    override fun seekTo(position: Long) {
        mPlayer.seekTo(position)
    }

    override fun reset() {
        mPlayIv.visibility = View.VISIBLE
        mPlayer.reset()
    }

    override fun release() {
        mPlayer.release()
    }


    override fun getDuration(): Long {
        return mPlayer.getDuration()
    }

    override fun getCurrentPosition(): Long {
        return mPlayer.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return mPlayer.isPlaying()
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        mPlayer.setVolume(leftVolume, rightVolume)
    }

    override fun setSpeed(speed: Float) {
        mPlayer.setSpeed(speed)
    }

    override fun setVideoRotation(rotation: Float) {
        mTextureView.rotation = rotation
        mPlayIv.rotation = rotation
    }

    override fun onPrepared() {
        mLoadingBar.visibility = View.GONE
        mPlayIv.visibility = View.VISIBLE
        onPreparedListener?.let {
            it.invoke(this)
        }
    }

    override fun onStart() {
        mPlayIv.visibility = View.GONE
        mThumbIv.visibility = View.GONE
        mLoadingBar.visibility = View.GONE
    }

    override fun onStopped() {
        mPlayIv.visibility = View.VISIBLE
    }

    override fun onPaused() {
        mPlayIv.visibility = View.VISIBLE
    }

    override fun onResume() {
        mLoadingBar.visibility = View.GONE
        mPlayIv.visibility = View.GONE
    }

    override fun onSeekTo(position: Long) {
    }

    override fun onProgressChanged(total: Long, progress: Long) {
        mPlayIv.visibility = View.GONE
        mLoadingBar.visibility = View.GONE
    }

    override fun onCompletion() {
        mPlayIv.visibility = View.VISIBLE
    }


    override fun onError(e: Exception) {

    }

    override fun onVideoSizeChanged(width: Int, height: Int) {

    }

    override fun onSetSurfaceTexture(surface: SurfaceTexture) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mTextureView.surfaceTexture = surface
        }
    }


    fun setOnPreparedListener(onPreparedListener: OnPreparedListener) {
        this.onPreparedListener = onPreparedListener
    }

    fun setOnDoubleClickListener(onDoubleClickListener: OnDoubleClickListener) {
        this.onDoubleClickListener = onDoubleClickListener
    }

    override fun setOnClickListener(l: OnClickListener?) {
        this.onClickListener = l
    }


    private var firstClick: Long = 0
    private var count: Int = 0

    /**
     * 触摸事件处理
     * @param v
     * @param event
     * @return
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (MotionEvent.ACTION_DOWN == event.action) { //按下
            count++
            if (1 == count) {
                firstClick = System.currentTimeMillis() //记录第一次点击时间
            } else if (2 == count) {
                val secondClick = System.currentTimeMillis() //记录第二次点击时间
                if (secondClick - firstClick < 500) { //判断二次点击时间间隔是否在设定的间隔时间之内
                    onDoubleClick()
                    count = 0
                    firstClick = 0
                } else {
                    firstClick = secondClick
                    count = 1
                }
            }
        }
        return true
    }

    private fun onDoubleClick() {
        if (mDoubleClickPlay) {
            if (isPlaying()) {
                pause()
            } else {
                resume()
            }
        }
        onDoubleClickListener?.let {
            it.invoke(this)
        }
    }

    override fun onClick(v: View?) {
        if (mClickPlay) {
            if (isPlaying()) {
                pause()
            } else {
                resume()
            }
        }
        onClickListener?.let {
            it.onClick(this)
        }
    }


    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        handleVisibilityChange(visibility, getVisibility())
    }


    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        handleVisibilityChange(windowVisibility, visibility)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        handleVisibilityChange(windowVisibility, visibility)
    }

    /**
     * 处理视图可见性
     */
    private fun handleVisibilityChange(windowVisibility: Int, visibility: Int) {
        if (windowVisibility == View.VISIBLE && (visibility == View.VISIBLE) and hasWindowFocus()) {
            if (mVisibleResume) {
                resume()
                mVisibleResume = false
            }
        } else {
            if (isPlaying()) {
                mVisibleResume = true
                pause()
            }
        }
    }
}