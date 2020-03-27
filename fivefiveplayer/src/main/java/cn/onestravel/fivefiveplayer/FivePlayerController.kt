package cn.onestravel.fivefiveplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import cn.onestravel.fivefiveplayer.interf.*
import cn.onestravel.fivefiveplayer.utils.VideoUtils
import cn.onestravel.fivefiveplayer.view.SelectorPopView

/**
 * @author onestravel
 * @createTime 2020-03-21
 * @description 播放器控制器
 */
open class FivePlayerController(val context: Context) : ControllerInterface {
    private var mPlayerState: Int = PlayerInterface.PLAYER_STATE_NORMAL
    protected val rootView: FrameLayout by lazy { FrameLayout(context) }
    private var normalControllerView: View? = null
    private var fullControllerView: View? = null
    protected var startView: ImageView? = null
    protected var progressTextView: TextView? = null
    protected var durationTextView: TextView? = null
    protected var progressSeekBar: SeekBar? = null
    protected var speedBtnView: TextView? = null
    protected var definitionBtnView: TextView? = null
    protected var fullScreenBtnView: ImageView? = null
    private var touchSeekBar: Boolean = false
    private var mMax: Long = 0
    private var mProgress: Long = 0
    private var mPercent: Float = 0f
    private var mDuration: Long = 0
    private var mRotation: Float = 0f
    private var mSpeed: Float = 1f
    private var mDefinitionType: DefinitionType = DefinitionType.SD
    private var mActionCallback: ControllerActionCallback? = null
    private val mDefinitionTypeSelectorPop: SelectorPopView by lazy { SelectorPopView(context) }
    override fun getControllerView(): View {
        normalControllerView =
            LayoutInflater.from(context).inflate(R.layout.five_layout_video_controller, null)
        fullControllerView =
            LayoutInflater.from(context)
                .inflate(R.layout.five_layout_video_controller_full_screen, null)
        normalControllerView?.let {
            changeControllerView(it)
        }
        return rootView
    }

    private fun changeControllerView(view: View) {
        startView = view.findViewById(R.id.five_view_start_view)
        progressTextView = view.findViewById(R.id.five_view_progress_text)
        progressSeekBar = view.findViewById(R.id.five_view_progress_seek_bar)
        durationTextView = view.findViewById(R.id.five_view_duration_text)
        fullScreenBtnView = view.findViewById(R.id.five_view_full_screen_view)
        speedBtnView = view.findViewById(R.id.five_view_speed_text)
        definitionBtnView = view.findViewById(R.id.five_view_definition_text)
        startView?.let {
            it.setOnClickListener(this::onClick)
        }
        fullScreenBtnView?.let {
            it.setOnClickListener(this::onClick)
        }
        speedBtnView?.let {
            it.setOnClickListener(this::onClick)
        }
        definitionBtnView?.let {
            it.setOnClickListener(this::onClick)
        }
        progressSeekBar?.let { it ->
            it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mActionCallback?.let { callback ->
                            callback.onActionSeekTo((seekBar?.progress ?: 0).toLong())
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    touchSeekBar = true
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    touchSeekBar = false

                }

            })
        }
        rootView.removeAllViews()
        rootView.addView(view)
    }

    override fun setMaxProgress(max: Long) {
        this.mMax = max
        progressSeekBar?.let {
            it.max = mMax.toInt()
        }
    }

    override fun setBufferingProgress(percent: Float) {
        this.mPercent = percent
        progressSeekBar?.let {
            it.secondaryProgress = (percent * mMax).toInt()
        }
    }

    override fun setProgress(position: Long, duration: Long) {
        this.mProgress = position
        this.mDuration = duration
        if (!touchSeekBar) {
            progressSeekBar?.let {
                it.progress = position.toInt()
            }
            progressTextView?.let {
                it.text = VideoUtils.formatTime(position)
            }
            durationTextView?.let {
                it.text = VideoUtils.formatTime(duration)
            }
        }
    }

    override fun setRotation(rotation: Float) {
        this.mRotation = rotation
        if (mRotation == 90f || mRotation == 270f) {
            fullControllerView?.let {
                changeControllerView(it)
            }
        } else {
            normalControllerView?.let {
                changeControllerView(it)
            }
        }
    }

    override fun onStart() {
        startView?.let {
            val tag = it.getTag(R.id.five_view_start_view) ?: 0
            if (tag == 0) {
                setPauseIcon()
                it.setTag(R.id.five_view_start_view, 1)
            }
        }
    }

    /**
     * 暂停播放
     */
    private fun setPauseIcon() {
        startView?.let {
            val imgRes = if (mPlayerState == PlayerInterface.PLAYER_STATE_FULL_SCREEN) {
                R.mipmap.five_icon_pause
            } else {
                R.mipmap.five_icon_circle_pause
            }
            it.setImageResource(imgRes)
        }
    }

    override fun onPause() {

        startView?.let {
            val tag = it.getTag(R.id.five_view_start_view) ?: 0
            if (tag == 1) {
                setPlayIcon()
                it.setTag(R.id.five_view_start_view, 0)
            }
        }
    }

    override fun onChangePlayerState(playerState: Int) {
        this.mPlayerState = playerState
        val platTag = startView?.getTag(R.id.five_view_start_view) ?: 0
        if (mPlayerState == PlayerInterface.PLAYER_STATE_FULL_SCREEN) {
            fullControllerView?.let {
                changeControllerView(it)
            }
        } else {
            normalControllerView?.let {
                changeControllerView(it)
            }
        }
        if (platTag == 1) {//正在播放
            setPauseIcon()
        } else {//暂停播放
            setPlayIcon()
        }
        startView?.setTag(R.id.five_view_start_view, platTag)
        setMaxProgress(mMax)
        setProgress(mProgress, mDuration)
    }

    override fun onSelectorSelected(obj: Any, selectData: String) {
        if (obj == "speed") {
            speedBtnView?.let {
                it.text = selectData
            }
            when (selectData) {
                context.getString(R.string.five_string_speed_0_75) -> {
                    mActionCallback?.let {
                        it.onActionChangeSpeed(0.75f)
                    }
                }
                context.getString(R.string.five_string_speed_1_25) -> {
                    mActionCallback?.let {
                        it.onActionChangeSpeed(1.25f)
                    }
                }
                context.getString(R.string.five_string_speed_1_5) -> {
                    mActionCallback?.let {
                        it.onActionChangeSpeed(1.5f)
                    }
                }
                context.getString(R.string.five_string_speed_2_0) -> {
                    mActionCallback?.let {
                        it.onActionChangeSpeed(2.0f)
                    }
                }
                context.getString(R.string.five_string_speed_1_0) -> {
                    mActionCallback?.let {
                        it.onActionChangeSpeed(1.0f)
                    }
                }
            }
        }
    }

    /**
     * 正在播放
     */
    private fun setPlayIcon() {
        startView?.let {
            val imgRes = if (mPlayerState == PlayerInterface.PLAYER_STATE_FULL_SCREEN) {
                R.mipmap.five_icon_play
            } else {
                R.mipmap.five_icon_circle_play
            }
            it.setImageResource(imgRes)
        }
    }

    override fun setActionCallBack(actionCallback: ControllerActionCallback) {
        this.mActionCallback = actionCallback
    }


    private fun onClick(view: View) {
        mActionCallback?.let { callback ->
            if (view.id == R.id.five_view_start_view) {
                startView?.let {
                    val tag = it.getTag(R.id.five_view_start_view) ?: 0
                    if (tag == 0) {
                        callback.onActionPlay()
                    } else {
                        callback.onActionPause()
                    }
                }
            } else if (view.id == R.id.five_view_full_screen_view) {
                fullScreenBtnView?.let {
                    if (mPlayerState == PlayerInterface.PLAYER_STATE_FULL_SCREEN) {
                        callback.onActionSetPlayerState(PlayerInterface.PLAYER_STATE_NORMAL)
                    } else {
                        callback.onActionSetPlayerState(PlayerInterface.PLAYER_STATE_FULL_SCREEN)
                    }
                }
            } else {

            }
        }
        if (view.id == R.id.five_view_speed_text) {
            val speedArr = context.resources.getStringArray(R.array.five_string_speed_arr)
            mActionCallback?.let {
                it.onActionShowSelector("speed", speedArr, speedBtnView?.text.toString())
            }
        }
    }
}