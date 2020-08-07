package cn.onestravel.fivefiveplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.*
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.onestravel.fivefiveplayer.interf.*
import cn.onestravel.fivefiveplayer.kernel.MediaKernelApi
import cn.onestravel.fivefiveplayer.utils.AnimationFromType
import cn.onestravel.fivefiveplayer.utils.AnimationUtils
import cn.onestravel.fivefiveplayer.utils.VideoUtils
import cn.onestravel.fivefiveplayer.view.SelectorPopView


/**
 * @author onestravel
 * @createTime 2020-03-21
 * @description TODO
 */
open class FiveVideoPlayer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), PlayerInterface,
    PlayerCallBack, ControllerActionCallback {
    private val HIDE_VIEW_SECOMDS: Long = 4 * 1000
    private var mFiveBackView: ImageView? = null
    private var mFiveTitleView: TextView? = null
    private var mPlayerState: Int = PlayerInterface.PLAYER_STATE_NORMAL
    private val mViewContainer: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.five_layout_video_player, null)
    }
    private val mSelectorView: SelectorPopView by lazy {
        SelectorPopView(
            context
        )
    }
    private val hideViewRunnable: Runnable by lazy { Runnable { hideTopActionBarView();hideControllerView() } }
    private var mBottomControllerView: FrameLayout? = null
    private var mTopActionBarView: RelativeLayout?
    private var mRightSelectorView: FrameLayout? = null
    private var mFiveVideoView: FiveVideoView? = null
    private var mController: ControllerInterface? = FivePlayerController(context)
    private var onPreparedListener: OnPreparedListener? = null
    private var onProgressListener: OnProgressListener? = null
    private var onCompleteListener: OnCompleteListener? = null
    private var onErrorListener: OnErrorListener? = null
    private var onBackPressListener: OnBackPressListener? = null
    var hideViewEnable: Boolean = true
    var gestureControlEnable: Boolean = true
        set(value) {
            field = value
            mFiveVideoView?.let {
                it.gestureControlEnable = value
            }
        }

    init {
        mBottomControllerView =
            mViewContainer.findViewById<FrameLayout>(R.id.five_layout_bottom_controller)
        mTopActionBarView =
            mViewContainer.findViewById<RelativeLayout>(R.id.five_layout_top_action_bar)
        mRightSelectorView =
            mViewContainer.findViewById<FrameLayout>(R.id.five_layout_right_selector)

        mFiveVideoView = mViewContainer.findViewById(R.id.five_view_video_view)
        mController?.let {
            setMediaController(it)
        }
        mFiveVideoView?.let {
            gestureControlEnable = true
            it.setPlayerCallback(this)
            it.setOnClickListener {
                mRightSelectorView?.let { selectorView ->
                    if (selectorView.visibility == View.VISIBLE) {
                        hideSelectorView()
                    }
                }
                if (mRightSelectorView == null || mRightSelectorView!!.visibility != View.VISIBLE) {
                    mTopActionBarView?.let { view ->
                        if (view.visibility == View.VISIBLE) {
                            hideTopActionBarView()
                            hideControllerView()
                        } else {
                            showTopActionBarView()
                            showControllerView()
                            hideViewDelay()
                        }
                    }
                }
            }
        }
        mFiveBackView = mViewContainer.findViewById(R.id.five_view_back)
        mFiveTitleView = mViewContainer.findViewById(R.id.five_view_title_tv)
        mFiveBackView?.let {
            it.setOnClickListener {
                if (mPlayerState == PlayerInterface.PLAYER_STATE_FULL_SCREEN) {
                    _exitFullScreen()
                } else {
                    onBackPressListener?.let { callback ->
                        callback.invoke()
                    }
                }
            }
        }
        setOnClickListener {
            mRightSelectorView?.let {
                if (it.visibility == View.VISIBLE) {
                    hideSelectorView()
                }
            }
        }
        addView(mViewContainer)
        FivePlayer.registerPlayer(this)
    }

    override fun setPreviewImg(url: String) {
        mFiveVideoView?.let {
            it.setPreviewImg(url)
        }
    }

    override fun setDataSource(url: String) {
        mFiveVideoView?.let {
            it.setDataSource(url)
        }
    }

    fun setDataSource(url: String, title: String) {
        mFiveVideoView?.let {
            it.setDataSource(url)
        }
        mFiveTitleView?.let {
            it.text = title
        }
    }

    override fun setDataSource(dataSource: MediaDataSource) {
        mFiveVideoView?.let {
            it.setDataSource(dataSource)
        }
        mFiveTitleView?.let {
            it.text = dataSource?.title ?: ""
        }

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
     * 设置播放器内核
     */
    override fun setMediaKernelClass(clazz: Class<out MediaKernelApi>) {
        mFiveVideoView?.let {
            it.setMediaKernelClass(clazz)
        }
    }

    /**
     * 设置视频播放控制器
     */
    fun setMediaController(controller: ControllerInterface?) {
        this.mController = controller
        mController?.let {
            it.setActionCallBack(this)
        }
        mBottomControllerView?.let {
            it.removeAllViews()
            it.addView(mController?.getControllerView())
        }
    }

    fun setOnBackPressListener(backPressListener: OnBackPressListener) {
        this.onBackPressListener = backPressListener;
    }

    override fun start() {
        mFiveVideoView?.let {
            it.start()
        }

    }

    override fun start(position: Long) {
        mFiveVideoView?.let {
            it.start(position)
        }
    }

    override fun pause() {
        mFiveVideoView?.let {
            it.pause()
        }
    }

    override fun stop() {
        mFiveVideoView?.let {
            it.stop()
        }
    }

    override fun resume() {
        mFiveVideoView?.let {
            it.resume()
        }
    }

    override fun seekTo(position: Long) {
        mFiveVideoView?.let {
            it.seekTo(position)
        }
    }

    override fun reset() {
        mFiveVideoView?.let {
            it.reset()
        }
    }

    override fun release() {
        mBottomControllerView?.let {
            it.removeAllViews()
        }
        mRightSelectorView?.let {
            it.removeAllViews()
        }
        mFiveVideoView?.let {
            it.release()
        }
        FivePlayer.unRegisterPlayer(this)
    }

    override fun getDuration(): Long {
        return mFiveVideoView?.getDuration() ?: 0
    }

    override fun getCurrentPosition(): Long {
        return mFiveVideoView?.getCurrentPosition() ?: 0
    }

    override fun isPlaying(): Boolean {
        return mFiveVideoView?.isPlaying() ?: false
    }

    override fun isPaused(): Boolean {
        return mFiveVideoView?.isPaused() ?: false
    }

    override fun isCompletion(): Boolean {
        return mFiveVideoView?.isCompletion() ?: false
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        mFiveVideoView?.let {
            it.setVolume(leftVolume, rightVolume)
        }
    }

    override fun setSpeed(speed: Float) {
        mFiveVideoView?.let {
            it.setSpeed(speed)
        }

    }

    override fun setVideoRotation(rotation: Float) {
//        mFiveVideoView?.let {
//            it.setVideoRotation(rotation)
//        }
        this.rotation = rotation
    }

    override fun setVideoDisplayType(displayType: Int) {
        mFiveVideoView?.let {
            it.setVideoDisplayType(displayType)
        }
    }

    override fun onPrepared() {
        mController?.setMaxProgress(getDuration())
        onPreparedListener?.let {
            it.invoke(this)
        }
    }

    override fun onStart(first: Boolean) {
        mController?.onStart()
        if (isPlaying()) {
            hideViewDelay()
        }
    }

    override fun onStopped() {
        mController?.onPause()
    }

    override fun onPaused() {
        mController?.onPause()
        showTopActionBarView()
        showControllerView()
    }

    override fun onResume() {
        mController?.onStart()
        if (isPlaying()) {
            hideViewDelay()
        }
    }

    override fun onSeekTo(position: Long) {
        mController?.setProgress(position, getDuration())
        if (isPlaying()) {
            hideViewDelay()
        }
    }

    override fun onBufferingPaused() {

    }

    override fun onBufferingPlaying() {

    }

    override fun onPlaying() {
        mController?.onStart()
        hideViewDelay()
    }

    private fun hideViewDelay() {
        removeCallbacks(hideViewRunnable)
        postDelayed(hideViewRunnable, HIDE_VIEW_SECOMDS)
    }

    override fun onProgressChanged(total: Long, progress: Long) {
        mController?.setProgress(progress, total)
        onProgressListener?.let {
            it.invoke(progress, total)
        }
    }

    override fun onCompletion() {
        mController?.onPause()
        showTopActionBarView()
        showControllerView()
        onCompleteListener?.let {
            it.invoke()
        }
    }

    override fun onError(e: Exception) {
        showTopActionBarView()
        showControllerView()
        onErrorListener?.let {
            it.invoke(e)
        }
    }

    override fun onVideoSizeChanged(width: Int, height: Int) {
    }

    override fun onSetSurfaceTexture(surface: SurfaceTexture) {

    }

    override fun onActionPlay() {
        if (isPaused()) {
            resume()
        } else {
            start()
        }
    }

    override fun onActionPause() {
        pause()
    }

    override fun onActionSeekTo(position: Long) {
        seekTo(position)
    }

    override fun onActionChangeSpeed(speed: Float) {
        setSpeed(speed)
    }

    override fun onActionSetPlayerState(playerState: Int) {
        when {
            playerState == PlayerInterface.PLAYER_STATE_FULL_SCREEN -> {
                enterFullScreen()
            }
            playerState == PlayerInterface.PLAYER_STATE_TINY_WINDOW -> {
                enterTinyWindow()
            }
            else -> {
                exitFullScreenOrTinyWindow()
            }
        }
    }

    /**
     * 进入全屏播放
     */
    fun enterFullScreen(): Boolean {
        return _enterFullScreen()
    }

    /**
     * 进入小窗口播放
     */
    fun enterTinyWindow(): Boolean {
        return _enterTinyWindow()
    }


    /**
     * 退出全屏播放货退出小窗口播放
     */
    fun exitFullScreenOrTinyWindow(): Boolean {
        _exitFullScreen()
        _exitTinyWindow()
        return true
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun _enterFullScreen(): Boolean {
        if (mPlayerState == PlayerInterface.PLAYER_STATE_NORMAL) {
            // 隐藏ActionBar、状态栏，并横屏
            VideoUtils.hideActionBarAndStatusBar(context)
            VideoUtils.hideBottomUIMenu(context)
            VideoUtils.scanForActivity(context)?.let {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                removeView(mViewContainer)
                val params = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                it.findViewById<ViewGroup>(android.R.id.content)?.addView(mViewContainer, params)
                mPlayerState = PlayerInterface.PLAYER_STATE_FULL_SCREEN
                mController?.onChangePlayerState(mPlayerState)
                return true
            }
        }
        return false
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun _exitFullScreen(): Boolean {
        if (mPlayerState == PlayerInterface.PLAYER_STATE_FULL_SCREEN) {
            VideoUtils.showActionBarAndStatusBar(context)
            VideoUtils.showBottomUIMenu(context)
            VideoUtils.scanForActivity(context)?.let {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val contentView: ViewGroup = it.findViewById(android.R.id.content)
                contentView.removeView(mViewContainer)
                val params = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                this.removeView(mViewContainer)
                this.addView(mViewContainer, params)
                mPlayerState = PlayerInterface.PLAYER_STATE_NORMAL
                mController?.onChangePlayerState(mPlayerState)
                return true
            }
        }
        return false
    }


    private fun _enterTinyWindow(): Boolean {
        if (mPlayerState == PlayerInterface.PLAYER_STATE_TINY_WINDOW) return false
        removeView(mViewContainer)
        VideoUtils.scanForActivity(context)?.let {
            val contentView = it.findViewById(android.R.id.content) as ViewGroup

            // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
            val params = LayoutParams(
                (VideoUtils.getScreenWidth(context) * 0.6f).toInt(),
                (VideoUtils.getScreenWidth(context) * 0.6f * 9f / 16f).toInt()
            )
            params.gravity = Gravity.BOTTOM or Gravity.END
            params.rightMargin = VideoUtils.dp2px(context, 8f)
            params.bottomMargin = VideoUtils.dp2px(context, 8f)
            contentView.addView(mViewContainer, params)
            mPlayerState = PlayerInterface.PLAYER_STATE_TINY_WINDOW
            mController?.onChangePlayerState(mPlayerState)
            return true
        }
        return false
    }

    private fun _exitTinyWindow(): Boolean {
        if (mPlayerState == PlayerInterface.PLAYER_STATE_TINY_WINDOW) {
            VideoUtils.scanForActivity(context)?.let {
                val contentView = it
                    .findViewById(android.R.id.content) as ViewGroup
                contentView.removeView(mViewContainer)
                val params = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                this.addView(mViewContainer, params)
                mPlayerState = PlayerInterface.PLAYER_STATE_TINY_WINDOW
                mController?.onChangePlayerState(mPlayerState)
                return true
            }
        }
        return false
    }

    override fun onActionChangeDefinition(definitionType: DefinitionType) {

    }

    override fun onActionShowSelector(obj: Any, datas: Array<String>, selectedData: String) {
        mRightSelectorView?.let {
            mSelectorView.setData(datas, selectedData)
            mSelectorView.setonSelectedCallBack { position, selectedData ->
                mController?.let { controller ->
                    controller.onSelectorSelected(obj, selectedData)
                }
                hideSelectorView()
            }
            it.removeAllViews()
            it.addView(mSelectorView)
            showSelectorView()
        }
    }

    /**
     * 显示顶部标题栏
     */
    private fun showTopActionBarView() {
        mTopActionBarView?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
                val anim = AnimationUtils.makeInAnimation(context, AnimationFromType.TOP)
                anim.setAnimationListener(object : SimpleAnimationListener() {
                    override fun onAnimationEnd(animation: Animation?) {
                        it.clearAnimation()
                    }
                })
                it.animation = anim
                anim.start()
            }
        }
    }

    /**
     * 隐藏顶部标题栏
     */
    private fun hideTopActionBarView() {
        if (!hideViewEnable) {
            return
        }
        mTopActionBarView?.let {
            if (it.visibility == View.VISIBLE) {
                val anim = AnimationUtils.makeOutAnimation(context, AnimationFromType.TOP)
                anim.setAnimationListener(object : SimpleAnimationListener() {
                    override fun onAnimationEnd(animation: Animation?) {
                        it.clearAnimation()
                        it.visibility = View.GONE
                    }
                })
                it.animation = anim
                anim.start()
            }
        }
    }

    /**
     * 显示控制器View
     */
    private fun showControllerView() {
        mBottomControllerView?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
                val anim = AnimationUtils.makeInAnimation(context, AnimationFromType.BOTTOM)
                anim.setAnimationListener(object : SimpleAnimationListener() {
                    override fun onAnimationEnd(animation: Animation?) {
                        it.clearAnimation()
                        it.visibility = View.VISIBLE
                    }
                })
                it.animation = anim
                anim.start()
            }
        }
    }

    /**
     * 隐藏控制器View
     */
    private fun hideControllerView() {
        if (!hideViewEnable) {
            return
        }
        mBottomControllerView?.let {
            if (it.visibility == View.VISIBLE) {
                val anim = AnimationUtils.makeOutAnimation(context, AnimationFromType.BOTTOM)
                anim.setAnimationListener(object : SimpleAnimationListener() {
                    override fun onAnimationEnd(animation: Animation?) {
                        it.clearAnimation()
                        it.visibility = View.GONE
                    }
                })
                it.animation = anim
                anim.start()
            }
        }
    }

    /**
     * 显示选择器view
     */
    private fun showSelectorView() {
        mRightSelectorView?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
                val anim = AnimationUtils.makeInAnimation(context, AnimationFromType.RIGHT)
                anim.setAnimationListener(object : SimpleAnimationListener() {
                    override fun onAnimationEnd(animation: Animation?) {
                        it.clearAnimation()
                        it.visibility = View.VISIBLE
                    }
                })
                it.animation = anim
                anim.start()
            }
        }
    }

    /**
     * 隐藏选择器view
     */
    private fun hideSelectorView() {
        mRightSelectorView?.let {
            if (it.visibility == View.VISIBLE) {
                val anim = AnimationUtils.makeOutAnimation(context, AnimationFromType.RIGHT)
                anim.setAnimationListener(object : SimpleAnimationListener() {
                    override fun onAnimationEnd(animation: Animation?) {
                        it.clearAnimation()
                        it.visibility = View.GONE
                    }
                })
                it.animation = anim
                it.animation.start()
                it.postDelayed({
                    it.clearAnimation()
                    it.visibility = View.GONE
                }, 200)
            }
        }
    }


    override fun setRotation(rotation: Float) {
        if (rotation != getRotation()) {
            super.setRotation(rotation)
            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (width > 0 && height > 0) {
            if (rotation == 90f || rotation == 270f) {
                super.onMeasure(heightMeasureSpec, widthMeasureSpec)
                return
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mPlayerState != PlayerInterface.PLAYER_STATE_NORMAL) {
            exitFullScreenOrTinyWindow()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}