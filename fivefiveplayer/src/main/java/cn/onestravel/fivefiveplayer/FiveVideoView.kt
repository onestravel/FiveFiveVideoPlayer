package cn.onestravel.fivefiveplayer

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
import cn.onestravel.fivefiveplayer.impl.FivePlayerImpl
import cn.onestravel.fivefiveplayer.impl.FiveVideoGestureListener
import cn.onestravel.fivefiveplayer.impl.VideoDisplayTypeDef
import cn.onestravel.fivefiveplayer.interf.*
import cn.onestravel.fivefiveplayer.kernel.MediaKernelApi
import cn.onestravel.fivefiveplayer.utils.VideoUtils
import cn.onestravel.fivefiveplayer.view.VideoTextureView
import com.bumptech.glide.Glide
import kotlin.math.abs


/**
 * @author onestravel
 * @createTime 2020-03-20
 * @description TODO
 */
typealias OnDoubleClickListener = (View) -> Unit

open class FiveVideoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    PlayerInterface,
    PlayerCallBack {
    private var mGestureViewType: Int = 0
    private var mVisibleResume: Boolean = false;
    private val mTextureView: VideoTextureView by lazy {
        VideoTextureView(
            context
        )
    }
    private val mGestureChangeContainer: FrameLayout by lazy { FrameLayout(context) }
    private val mPlayIv: ImageView by lazy { ImageView(context) }
    private val mThumbIv: ImageView by lazy { ImageView(context) }
    private val mLoadingBar: ProgressBar by lazy { ProgressBar(context) }
    private val mPlayer: FivePlayerImpl by lazy { FivePlayerImpl(context) }
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

    /**
     * 设置是否允许双击播放/暂停
     */
    var doubleClickPlay: Boolean = true
        set(value) {
            field = value
            if (value) {
                clickPlay = false
            }
        }

    /**
     * 设置是否允许单击播放/暂停
     */
    var clickPlay: Boolean = false
        set(value) {
            field = value
            if (value) {
                doubleClickPlay = false
            }
        }

    var gestureControlEnable = false
    var showPlayIconEnable = true
    var showLoadingViewEnable = true
    private var onClickListener: OnClickListener? = null
    private var onDoubleClickListener: OnDoubleClickListener? = null
    private var onPreparedListener: OnPreparedListener? = null
    private var onProgressListener: OnProgressListener? = null
    private var onCompleteListener: OnCompleteListener? = null
    private var onErrorListener: OnErrorListener? = null
    private var mPlayerCallBack: PlayerCallBack? = null


    fun setPlayerCallback(playerCallBack: PlayerCallBack) {
        this.mPlayerCallBack = playerCallBack
    }

    /**
     * 设置点击监听事件
     */
    override fun setOnClickListener(l: OnClickListener?) {
        this.onClickListener = l
    }

    /**
     * 设置准备完成监听事件
     */
    override fun setOnPreparedListener(onPreparedListener: OnPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    /**
     * 设置播放进度监听事件
     */
    override fun setOnProgressListener(onProgressListener: OnProgressListener) {
        this.onProgressListener = onProgressListener
    }

    /**
     * 设置播放完成监听事件
     */
    override fun setOnCompleteListener(onCompleteListener: OnCompleteListener) {
        this.onCompleteListener = onCompleteListener
    }

    /**
     * 设置播放异常监听事件
     */
    override fun setOnErrorListener(onErrorListener: OnErrorListener) {
        this.onErrorListener = onErrorListener
    }

    /**
     * 设置双击监听事件
     */
    fun setOnDoubleClickListener(onDoubleClickListener: OnDoubleClickListener) {
        this.onDoubleClickListener = onDoubleClickListener
    }

    /**
     * 设置播放器媒体内核
     */
    override fun setMediaKernelClass(clazz: Class<out MediaKernelApi>) {
        mPlayer.setMediaKernel(clazz)
        reset()
    }

    init {
        val lpTextureView = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
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
        mPlayIv?.let {
            it.setOnClickListener {
                if (isPlaying()) {
                    pause()
                } else {
                    resume()
                }
                onClickListener?.let { listener ->
                    listener.onClick(this)
                }
            }
        }
        hidePlayIcon()
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

        mPlayer.setPlayerCallBack(this)
//        super.setOnClickListener({})
        FivePlayer.registerPlayer(this)
    }

    override fun setPreviewImg(url: String) {
        Glide.with(mThumbIv)
            .asBitmap()
            .load(url)
            .override(2, 2)
            .centerCrop()
            .into(mThumbIv)

    }

    override fun setDataSource(url: String) {
        mPlayer.setDataSource(url)
        mPlayer.attachTextureView(mTextureView)
        hideLoadingView()
        hidePlayIcon()
    }


    override fun setDataSource(dataSource: MediaDataSource) {
        mPlayer.setDataSource(dataSource)
        mPlayer.attachTextureView(mTextureView)
        hideLoadingView()
        hidePlayIcon()
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
     * 显示手势控制改变的view
     * @param type value in {FiveVideoGestureListener.GESTURE_MOTION_PROGRESS ,
     *                       FiveVideoGestureListener.GESTURE_MOTION_VOLUME,
     *                       FiveVideoGestureListener.GESTURE_MOTION_BRIGHT}
     * @param view 改变播放器 音量/亮度/进度 的View
     */
    fun showGestureChangeView(type: Int, view: View) {
        if (mGestureViewType != type) {
            mGestureChangeContainer.removeAllViews()
            mGestureChangeContainer.addView(view)
            mGestureChangeContainer.visibility = View.VISIBLE
        }
    }

    /**
     * 隐藏手势控制改变的View
     */
    fun hideGestureChangeView() {
        mGestureChangeContainer.removeAllViews()
        mGestureChangeContainer.visibility = View.GONE
        mGestureViewType = 0
    }

    override fun start() {
        mPlayer.start()
        hidePlayIcon()
        showLoadingView()
    }

    override fun start(position: Long) {
        mPlayer.start(position)
        hidePlayIcon()
        showLoadingView()
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
        showPlayIcon()
        mPlayer.reset()
    }

    override fun release() {
        mPlayer.release()
        FivePlayer.unRegisterPlayer(this)
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

    override fun isPaused(): Boolean {
        return mPlayer.isPaused()
    }

    override fun isCompletion(): Boolean {
        return mPlayer.isCompletion()
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
        hideLoadingView()
        showPlayIcon()
        mPlayerCallBack?.let {
            it.onPrepared()
        }
        onPreparedListener?.let {
            it.invoke(this)
        }
    }

    override fun onStart(first: Boolean) {
        hidePlayIcon()
        mThumbIv.visibility = View.GONE
        hideLoadingView()
        mPlayerCallBack?.let {
            it.onStart(first)
        }
    }


    override fun onStopped() {
        showPlayIcon()
        mPlayerCallBack?.let {
            it.onStopped()
        }
    }


    override fun onPaused() {
        showPlayIcon()
        mPlayerCallBack?.let {
            it.onPaused()
        }
    }

    override fun onResume() {
        hideLoadingView()
        hidePlayIcon()
        mPlayerCallBack?.let {
            it.onResume()
        }
    }

    override fun onSeekTo(position: Long) {
        mPlayerCallBack?.let {
            it.onSeekTo(position)
        }
    }

    override fun onBufferingPaused() {
        showPlayIcon()
        mThumbIv.visibility = View.GONE
        showLoadingView()
        mPlayerCallBack?.let {
            it.onBufferingPaused()
        }
    }

    override fun onBufferingPlaying() {
        hidePlayIcon()
        mThumbIv.visibility = View.GONE
        showLoadingView()
        mPlayerCallBack?.let {
            it.onBufferingPlaying()
        }
    }

    override fun onPlaying() {
        hidePlayIcon()
        mThumbIv.visibility = View.GONE
        hideLoadingView()
        mPlayerCallBack?.let {
            it.onPlaying()
        }
    }

    override fun onProgressChanged(total: Long, progress: Long) {
        hidePlayIcon()
        hideLoadingView()
        mPlayerCallBack?.let {
            it.onProgressChanged(total, progress)
        }
        onProgressListener?.let {
            it.invoke(progress, total)
        }
    }

    override fun onCompletion() {
        showPlayIcon()
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
            mTextureView.surfaceTexture = surface
        }
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
                    postDelayed(mTouchEventCountThread, 350)
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
                    if (!doubleClickPlay || mTouchEventCountThread.touchCount == 2) {
                        removeCallbacks(mTouchEventCountThread)
                        post(mTouchEventCountThread)
                    }
                    // 一次点击事件要有按下和抬起, 有抬起必有按下, 所以只需要在ACTION_UP中处理

                    // 如果是长按操作, 则Handler的消息,不能将touchCount置0, 需要特殊处理
                    if (mTouchEventCountThread.isLongClick) {
                        mTouchEventCountThread.touchCount = 0
                        mTouchEventCountThread.isLongClick = false
                    }
                } else {
                    if (mVideoGestureListener.mGestureMotion == FiveVideoGestureListener.GESTURE_MOTION_PROGRESS) {
                        mVideoGestureListener.onSeekCompletion()
                    }
                    hideGestureChangeView()
                    mVideoGestureListener.onActionUp()
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
        return if (gestureControlEnable) {
            gestureDetector.onTouchEvent(event);
        } else {
            true
        }
    }

    private fun onDoubleClick() {
        if (doubleClickPlay) {
            when {
                isPlaying() -> {
                    pause()
                }
                isPaused() -> {
                    resume()
                }
                isCompletion() -> {
                    seekTo(0)
                    start()
                }
                else -> {
                    start()
                }
            }
        }
        onDoubleClickListener?.let {
            it.invoke(this)
        }
    }

    private fun onClick(v: View?) {
        if (clickPlay) {
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


    private fun showPlayIcon() {
        if (showPlayIconEnable) {
            mPlayIv.visibility = View.VISIBLE
        }
    }

    private fun hidePlayIcon() {
        mPlayIv.visibility = View.GONE
    }


    private fun showLoadingView() {
        if (showLoadingViewEnable) {
            mLoadingBar.visibility = View.VISIBLE
        }
    }

    private fun hideLoadingView() {
        mLoadingBar.visibility = View.GONE
    }
}