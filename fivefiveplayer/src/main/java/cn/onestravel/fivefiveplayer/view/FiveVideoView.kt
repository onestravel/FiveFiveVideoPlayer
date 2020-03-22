package cn.onestravel.fivefiveplayer.view

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import cn.onestravel.fivefiveplayer.FivePlayer
import cn.onestravel.fivefiveplayer.FiveVideoGestureListener
import cn.onestravel.fivefiveplayer.R
import cn.onestravel.fivefiveplayer.VideoDisplayTypeDef
import cn.onestravel.fivefiveplayer.interf.*
import cn.onestravel.fivefiveplayer.utils.VideoUtils
import kotlin.math.abs


/**
 * @author onestravel
 * @createTime 2020-03-20
 * @description TODO
 */
typealias OnDoubleClickListener = (View) -> Unit

class FiveVideoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    PlayerInterface,
    PlayerCallBack {
    private var mGestureViewType: Int = 0
    private var mVisibleResume: Boolean = false;
    private val mTextureView: VideoTextureView by lazy { VideoTextureView(context) }
    private val mPlayIv: ImageView by lazy { ImageView(context) }
    private val mThumbIv: ImageView by lazy { ImageView(context) }
    private val mLoadingBar: ProgressBar by lazy { ProgressBar(context) }
    private val mGestureChangeContainer: FrameLayout by lazy { FrameLayout(context) }
    private val mPlayer: FivePlayer by lazy { FivePlayer() }
    private var onPreparedListener: OnPreparedListener? = null
    var onProgressListener: OnProgressListener? = null
    var onCompleteListener: OnCompleteListener? = null
    var onErrorListener: OnErrorListener? = null
    private var onDoubleClickListener: OnDoubleClickListener? = null
    private var onClickListener: OnClickListener? = null
    private var mDoubleClickPlay: Boolean = true
    private var mClickPlay: Boolean = false
    private var mPlayerCallBack: PlayerCallBack? = null
    private val mTouchEventCountThread: TouchEventCountThread by lazy { TouchEventCountThread() }
    private val mVideoGestureListener: FiveVideoGestureListener by lazy {
        FiveVideoGestureListener(
            context,
            this
        )
    }
    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(
            context,
            mVideoGestureListener
        )
    }

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

        val lpGestureChange = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lpGestureChange.gravity = Gravity.CENTER
        mGestureChangeContainer.layoutParams = lpGestureChange
        mGestureChangeContainer.visibility = View.GONE
        addView(mGestureChangeContainer)

        mPlayer.attachTextureView(mTextureView)
        mPlayer.setPlayerCallBack(this)
//        super.setOnClickListener({})
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
    override fun setVideoDisplayType(@VideoDisplayTypeDef type: Int) {
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

    fun showGestureChangeView(type: Int, view: View) {
        if (mGestureViewType != type) {
            mGestureChangeContainer.removeAllViews()
            mGestureChangeContainer.addView(view)
            mGestureChangeContainer.visibility = View.VISIBLE
        }
    }

    fun hideGestureChangeView() {
        mGestureChangeContainer.removeAllViews()
        mGestureChangeContainer.visibility = View.GONE
        mGestureViewType = 0
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
        mPlayerCallBack?.let {
            it.onPrepared()
        }
        onPreparedListener?.let {
            it.invoke(this)
        }
    }

    override fun onStart(first: Boolean) {
        mPlayIv.visibility = View.GONE
        mThumbIv.visibility = View.GONE
        mLoadingBar.visibility = View.GONE
        mPlayerCallBack?.let {
            it.onStart(first)
        }
    }

    override fun onStopped() {
        mPlayIv.visibility = View.VISIBLE
        mPlayerCallBack?.let {
            it.onStopped()
        }
    }

    override fun onPaused() {
        mPlayIv.visibility = View.VISIBLE
        mPlayerCallBack?.let {
            it.onPaused()
        }
    }

    override fun onResume() {
        mLoadingBar.visibility = View.GONE
        mPlayIv.visibility = View.GONE
        mPlayerCallBack?.let {
            it.onResume()
        }
    }

    override fun onSeekTo(position: Long) {
        mPlayerCallBack?.let {
            it.onSeekTo(position)
        }
    }

    override fun onProgressChanged(total: Long, progress: Long) {
        mPlayIv.visibility = View.GONE
        mLoadingBar.visibility = View.GONE
        mPlayerCallBack?.let {
            it.onProgressChanged(total, progress)
        }
        onProgressListener?.let {
            it.invoke(progress, total)
        }
    }

    override fun onCompletion() {
        mPlayIv.visibility = View.VISIBLE
        mPlayerCallBack?.let {
            it.onCompletion()
        }
        onCompleteListener?.let {
            it.invoke()
        }
    }


    override fun onError(e: Exception) {
        mPlayerCallBack?.let {
            it.onError(e)
        }
        onErrorListener?.let {
            it.invoke(e)
        }
    }

    override fun onVideoSizeChanged(width: Int, height: Int) {
        mPlayerCallBack?.let {
            it.onVideoSizeChanged(width, height)
        }
    }

    override fun onSetSurfaceTexture(surface: SurfaceTexture) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            mTextureView.surfaceTexture = surface
        }
    }


    fun setOnPreparedListener(onPreparedListener: OnPreparedListener) {
        this.onPreparedListener = onPreparedListener
    }


    fun setOnDoubleClickListener(onDoubleClickListener: OnDoubleClickListener) {
        this.onDoubleClickListener = onDoubleClickListener
    }


    fun setPlayerCallback(playerCallBack: PlayerCallBack) {
        this.mPlayerCallBack = playerCallBack
    }

    override fun setOnClickListener(l: OnClickListener?) {
        this.onClickListener = l
    }


    private var downX: Float = 0f
    private var downY: Float = 0f
    private var moveX: Float = 0f
    private var moveY: Float = 0f

    /**
     * 触摸事件处理
     * @param v
     * @param event
     * @return
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (0 == mTouchEventCountThread.touchCount) { // 第一次按下时,开始统计
                    if (mDoubleClickPlay) {
                        postDelayed(mTouchEventCountThread, 500)
                    } else {
                        post(mTouchEventCountThread)
                        return super.onTouchEvent(event)
                    }
                }
                downX = event.x//float DownX
                downY = event.y//float DownY
                moveX = 0f
                moveY = 0f
            }
            MotionEvent.ACTION_UP -> {
                //判断是否继续传递信号
                if ((moveX < 20 && moveY < 20)) {
                    mTouchEventCountThread.touchCount++
                    if (mTouchEventCountThread.touchCount == 2) {
                        removeCallbacks(mTouchEventCountThread)
                        post(mTouchEventCountThread)
                    }
                    // 一次点击事件要有按下和抬起, 有抬起必有按下, 所以只需要在ACTION_UP中处理

                    // 如果是长按操作, 则Handler的消息,不能将touchCount置0, 需要特殊处理
                    if (mTouchEventCountThread.isLongClick) {
                        mTouchEventCountThread.touchCount = 0
                        mTouchEventCountThread.isLongClick = false
                    }
                }else{
                    hideGestureChangeView()
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mVideoGestureListener.mGestureMotion == FiveVideoGestureListener.GESTURE_MOTION_PROGRESS) {
//                        mListener.onSeek(mGestureProgress * 1000);
                    }
//                    mGestureMotion = 0;// 手指离开屏幕后，重置调节音量或进度的标志
//                    gesture_volume_layout.setVisibility(View.GONE);
//                    gesture_bright_layout.setVisibility(View.GONE);
//                    gesture_progress_layout.setVisibility(View.GONE);
                }
            }
            MotionEvent.ACTION_MOVE -> {
                moveX += abs(event.x - downX);//X轴距离
                moveY += abs(event.y - downY);//y轴距离

            }
            MotionEvent.ACTION_CANCEL -> {
            }
            else -> {
            }
        }
        return gestureDetector.onTouchEvent(event);
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

    private fun onClick(v: View?) {
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

    private fun onLongClick() {

    }

    inner class TouchEventCountThread : Runnable {
        var touchCount = 0
        var isLongClick = false
        override fun run() {
            when (touchCount) {
                0 -> { // long click
                    isLongClick = true
                    onLongClick()
                    touchCount = 0
                    isLongClick = false
                }
                2 -> {
                    onDoubleClick()
                    touchCount = 0
                    isLongClick = false
                }
                else -> {
                    onClick(this@FiveVideoView)
                    touchCount = 0
                    isLongClick = false
                }
            }
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