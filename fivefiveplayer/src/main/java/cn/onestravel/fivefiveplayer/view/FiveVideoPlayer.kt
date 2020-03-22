package cn.onestravel.fivefiveplayer.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import cn.onestravel.fivefiveplayer.FivePlayerController
import cn.onestravel.fivefiveplayer.R
import cn.onestravel.fivefiveplayer.interf.*
import cn.onestravel.fivefiveplayer.utils.VideoUtils


/**
 * @author onestravel
 * @createTime 2020-03-21
 * @description TODO
 */
class FiveVideoPlayer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), PlayerInterface,
    PlayerCallBack, ControllerActionCallback {
    private var mFiveBackView: ImageView? = null
    private var mFiveTitleView: TextView? = null
    private var mPlayerState: Int = PlayerInterface.PLAYER_STATE_NORMAL
    private val mViewContainer: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.five_layout_video_player, null)
    }
    private val mSelectorView: SelectorPopView by lazy { SelectorPopView(context) }
    private var mOnPreparedListener: OnPreparedListener? = null
    private var mBottomControllerView: FrameLayout? = null
    private var mRightSelectorView: FrameLayout? = null
    private var mFiveVideoView: FiveVideoView? = null
    private var mController: ControllerInterface = FivePlayerController(context)

    init {
        mBottomControllerView =
            mViewContainer.findViewById<FrameLayout>(R.id.five_layout_bottom_controller)
        mRightSelectorView =
            mViewContainer.findViewById<FrameLayout>(R.id.five_layout_right_selector)

        mFiveVideoView = mViewContainer.findViewById(R.id.five_view_video_view)
        setMediaController(mController)
        mFiveVideoView?.let {
            it.setPlayerCallback(this)
            it.setOnClickListener {
                mRightSelectorView?.let {
                    if (it.visibility === View.VISIBLE) {
                        hideSelectorView()
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
                }
            }
        }
        setOnClickListener {
            mRightSelectorView?.let {
                if (it.visibility === View.VISIBLE) {
                    hideSelectorView()
                }
            }
        }
        addView(mViewContainer)
    }

    override fun setDataSource(url: String) {
        mFiveVideoView?.let {
            it.setDataSource(url)
        }
    }

    fun setOnPreparedListener(onPreparedListener: OnPreparedListener) {
        this.mOnPreparedListener = onPreparedListener
    }

    fun setMediaController(controller: ControllerInterface) {
        this.mController = controller
        mController?.let {
            it.setActionCallBack(this)
        }
        mBottomControllerView?.let {
            it.removeAllViews()
            it.addView(mController?.getControllerView())
        }
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
        mFiveVideoView?.let {
            it.release()
        }
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
        mController.setMaxProgress(getDuration())
        mOnPreparedListener?.let {
            it.invoke(this)
        }
    }

    override fun onStart(first: Boolean) {
        mController.onStart()
    }

    override fun onStopped() {
    }

    override fun onPaused() {
        mController.onPause()
    }

    override fun onResume() {
    }

    override fun onSeekTo(position: Long) {
    }

    override fun onProgressChanged(total: Long, progress: Long) {
        mController.setProgress(progress, total)
    }

    override fun onCompletion() {
    }

    override fun onError(e: Exception) {
    }

    override fun onVideoSizeChanged(width: Int, height: Int) {
    }

    override fun onSetSurfaceTexture(surface: SurfaceTexture) {

    }

    override fun onActionPlay() {
        resume()
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
            playerState === PlayerInterface.PLAYER_STATE_FULL_SCREEN -> {
                enterFullScreen()
            }
            playerState === PlayerInterface.PLAYER_STATE_TINY_WINDOW -> {
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
            VideoUtils.hideActionBar(context)
            VideoUtils.hideBottomUIMenu(context)
            VideoUtils.scanForActivity(context)?.let {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                removeView(mViewContainer)
                val params = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                it.findViewById<ViewGroup>(android.R.id.content)?.addView(mViewContainer, params)
                mPlayerState = PlayerInterface.PLAYER_STATE_FULL_SCREEN
                mController.onChangePlayerState(mPlayerState)
                return true
            }
        }
        return false
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun _exitFullScreen(): Boolean {
        if (mPlayerState === PlayerInterface.PLAYER_STATE_FULL_SCREEN) {
            VideoUtils.showActionBar(context)
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
                mController.onChangePlayerState(mPlayerState)
                return true
            }
        }
        return false
    }


    private fun _enterTinyWindow(): Boolean {
        if (mPlayerState === PlayerInterface.PLAYER_STATE_TINY_WINDOW) return false
        removeView(mViewContainer)
        VideoUtils.scanForActivity(context)?.let {
            val contentView = it.findViewById(android.R.id.content) as ViewGroup

            // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
            val params = LayoutParams(
                (VideoUtils.getScreenWidth(context) * 0.6f) as Int,
                (VideoUtils.getScreenWidth(context) * 0.6f * 9f / 16f) as Int
            )
            params.gravity = Gravity.BOTTOM or Gravity.END
            params.rightMargin = VideoUtils.dp2px(context, 8f)
            params.bottomMargin = VideoUtils.dp2px(context, 8f)
            contentView.addView(mViewContainer, params)
            mPlayerState = PlayerInterface.PLAYER_STATE_TINY_WINDOW
            mController.onChangePlayerState(mPlayerState)
            return true
        }
        return false
    }

    private fun _exitTinyWindow(): Boolean {
        if (mPlayerState === PlayerInterface.PLAYER_STATE_TINY_WINDOW) {
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
                mController.onChangePlayerState(mPlayerState)
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

    private fun showSelectorView() {
        mRightSelectorView?.let {
            it.visibility = View.VISIBLE
        }
    }

    private fun hideSelectorView() {
        mRightSelectorView?.let {
            it.visibility = View.GONE
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

}