package cn.onestravel.fivefiveplayer.impl

import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import cn.onestravel.fivefiveplayer.R
import cn.onestravel.fivefiveplayer.utils.LogHelper
import cn.onestravel.fivefiveplayer.utils.VideoUtils
import cn.onestravel.fivefiveplayer.view.CircleProgressBar
import cn.onestravel.fivefiveplayer.FiveVideoView
import java.lang.Exception


/**
 * @author onestravel
 * @createTime 2020-03-19
 * @description 视频播放器手势控制监听器
 */
class FiveVideoGestureListener(val mContext: Context, val fiveVideoView: FiveVideoView) :
    GestureDetector.OnGestureListener {
    private val TAG = javaClass.simpleName
    private val mGestureVolumeLayout: View by lazy {
        LayoutInflater.from(mContext).inflate(R.layout.five_layout_gesture_volume, null)
    }
    private val mGestureVolumeIconView: ImageView by lazy {
        mGestureVolumeLayout.findViewById<ImageView>(
            R.id.five_view_volume_icon
        )
    }
    private val mGestureVolumeTextView: TextView by lazy {
        mGestureVolumeLayout.findViewById<TextView>(
            R.id.five_view_volume_text
        )
    }
    private val mGestureVolumeProgressView: CircleProgressBar by lazy {
        mGestureVolumeLayout.findViewById<CircleProgressBar>(
            R.id.five_view_volume_progress
        )
    }
    private val mGestureBrightLayout: View by lazy {
        LayoutInflater.from(mContext).inflate(R.layout.five_layout_gesture_bright, null)
    }
    private val mGestureBrightIconView: ImageView by lazy {
        mGestureBrightLayout.findViewById<ImageView>(
            R.id.five_view_bright_icon
        )
    }
    private val mGestureBrightTextView: TextView by lazy {
        mGestureBrightLayout.findViewById<TextView>(
            R.id.five_view_bright_text
        )
    }
    private val mGestureBrightProgressView: CircleProgressBar by lazy {
        mGestureBrightLayout.findViewById<CircleProgressBar>(
            R.id.five_view_bright_progress
        )
    }
    private val mGestureProgressLayout: View by lazy {
        LayoutInflater.from(mContext).inflate(R.layout.five_layout_gesture_progress, null)
    }
    private val mGestureProgressView: ProgressBar by lazy {
        mGestureProgressLayout.findViewById<ProgressBar>(
            R.id.five_view_progress_view
        )
    }
    private val mGestureProgressTextView: TextView by lazy {
        mGestureProgressLayout.findViewById<TextView>(
            R.id.five_view_progress_text
        )
    }
    var mGestureMotion = 0 // 1调节进度, 2调节音量, 3调节亮度

    companion object {
        val GESTURE_MOTION_PROGRESS = 1
        val GESTURE_MOTION_VOLUME = 2
        val GESTURE_MOTION_BRIGHT = 3
    }

    private val GESTURE_PROGRESS_STEP = 3 * 1000 // 设定进度滑动时的步长，避免每次滑动都改变，导致改变过快 单位s

    private val GESTURE_BRIGHTNESS_STEP = 3
    private var ensureGestureMotion = false // 每次触摸屏幕后，第一次scroll的标志

    private val mAudiomanager: AudioManager by lazy { (mContext.getSystemService(Context.AUDIO_SERVICE)) as AudioManager }
    private var mGestureProgress: Long = 0 //s

    private var playerWidth = 0
    private var playerHeight: Int = 0
    private val maxVolume: Int by lazy { mAudiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }// 获取系统最大音量
    private var currentVolume: Float = 0f
    private var mBrightness: Int = -1 // 亮度

    init {
        currentVolume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() // 获取当前音量

    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        fiveVideoView.hideGestureChangeView()
        return false;
    }

    override fun onDown(e: MotionEvent?): Boolean {
        playerWidth = fiveVideoView.width
        playerHeight = fiveVideoView.height
        currentVolume =
            mAudiomanager!!.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() // 获取当前值
        ensureGestureMotion = true;// 设定是触摸屏幕后第一次scroll的标志
        return true;
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        val mOldX = e1!!.x
        val mOldY = e1.y
        val y = e2!!.rawY.toInt()
        if (ensureGestureMotion) { // 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
            // 横向的距离变化大则调整进度，纵向的变化大则调整音量
            if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                mGestureProgressLayout!!.visibility = View.VISIBLE
                mGestureVolumeLayout!!.visibility = View.GONE
                mGestureBrightLayout!!.visibility = View.GONE
                mGestureMotion =
                    GESTURE_MOTION_PROGRESS
                mGestureProgress = fiveVideoView.getCurrentPosition()
            } else {
                if (mOldX > playerWidth * 3.0 / 5) { // 音量
                    mGestureVolumeLayout!!.visibility = View.VISIBLE
                    mGestureBrightLayout!!.visibility = View.GONE
                    mGestureProgressLayout!!.visibility = View.GONE
                    mGestureMotion =
                        GESTURE_MOTION_VOLUME
                } else if (mOldX < playerWidth * 2.0 / 5) { // 亮度
                    mGestureBrightLayout!!.visibility = View.VISIBLE
                    mGestureVolumeLayout!!.visibility = View.GONE
                    mGestureProgressLayout!!.visibility = View.GONE
                    mGestureMotion =
                        GESTURE_MOTION_BRIGHT
                    try {
                        mBrightness = (VideoUtils.getAppBrightness(mContext) * 255).toInt()
                    } catch (e: Settings.SettingNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        // 如果每次触摸屏幕后第一次scroll是调节进度，那之后的scroll事件都处理音量进度，直到离开屏幕执行下一次操作
        if (mGestureMotion == GESTURE_MOTION_PROGRESS) {
            // distanceX=lastScrollPositionX-currentScrollPositionX，因此为正时是快进
            if (Math.abs(distanceX) > Math.abs(distanceY)) { // 横向移动大于纵向移动
                val duration: Long = fiveVideoView.getDuration()
                if (distanceX >= 1) { // 快退，用步长控制改变速度，可微调
                    if (mGestureProgress > GESTURE_PROGRESS_STEP) { // 避免为负
                        mGestureProgress -= GESTURE_PROGRESS_STEP // scroll方法执行一次快退3秒
                    } else {
                        mGestureProgress = 0
                    }
                } else if (distanceX <= -1) { // 快进
                    if (mGestureProgress < duration - GESTURE_PROGRESS_STEP) { // 避免超过总时长
                        mGestureProgress += GESTURE_PROGRESS_STEP // scroll执行一次快进3秒
                    } else {
                        mGestureProgress = duration - 10
                    }
                }
                if (mGestureProgress < 0) {
                    mGestureProgress = 0
                }
                var progress = (mGestureProgress.toFloat() / duration * 100).toInt()
                if (progress > 99) {
                    progress = 99
                }
                val timeText: String =
                    VideoUtils.formatTime(mGestureProgress)
                        .toString() + "/" + VideoUtils.formatTime(duration)
                mGestureProgressTextView!!.text = timeText
                mGestureProgressView.progress = progress
                fiveVideoView.showGestureChangeView(
                    GESTURE_MOTION_PROGRESS,
                    mGestureProgressLayout
                )
//                LogHelper.i(TAG, "progress= $progress%")
            }
        } else if (mGestureMotion == GESTURE_MOTION_VOLUME) {
//
            if (Math.abs(distanceY) > Math.abs(distanceX)) { // 纵向移动大于横向移动
                if (distanceY >= 1) { // 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
                    if (currentVolume < maxVolume) { // 为避免调节过快，distanceY应大于一个设定值
                        currentVolume += 0.1f
                    }
                } else if (distanceY <= -1) { // 音量调小
                    if (currentVolume > 0) {
                        currentVolume -= 0.1f
                    }

                }
                if (currentVolume > maxVolume) {
                    currentVolume = maxVolume.toFloat()
                }
                if (currentVolume < 0) {
                    currentVolume = 0f
                }
                val percentage = currentVolume.toFloat() * 100 / maxVolume
                if (currentVolume == 0f) { // 静音，设定静音独有的图片
                    mGestureVolumeIconView.setImageResource(R.mipmap.five_icon_no_volume)
                    mGestureVolumeTextView!!.text =
                        mContext.getString(R.string.five_string_volume_mute)
                } else {
                    mGestureVolumeIconView.setImageResource(R.mipmap.five_icon_volume)
                    mGestureVolumeTextView!!.text = mContext.getString(R.string.five_string_volume)
                }
                mGestureVolumeProgressView.setProgress(percentage)
                LogHelper.i(TAG, "volume= $percentage%")
                mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume.toInt(), 0)
                fiveVideoView.showGestureChangeView(GESTURE_MOTION_VOLUME, mGestureVolumeLayout)
            }
        } else if (mGestureMotion == GESTURE_MOTION_BRIGHT) {
//            ivGestureBright!!.setImageResource(R.drawable.player_bright)
            try {
                mBrightness = (VideoUtils.getAppBrightness(mContext) * 255).toInt()
                if (distanceY >= 1) {
                    mBrightness += GESTURE_BRIGHTNESS_STEP
                } else if (distanceY <= -1) {
                    mBrightness -= GESTURE_BRIGHTNESS_STEP
                }
                if (mBrightness < 5) mBrightness = 5
                if (mBrightness > 255) mBrightness = 255
//                LogHelper.i(TAG, "brightness $mBrightness")
                setAppLight(mBrightness)
                val percent = mBrightness.toFloat() * 100 / 255
                mGestureBrightProgressView.setProgress(percent)
                fiveVideoView.showGestureChangeView(GESTURE_MOTION_BRIGHT, mGestureBrightLayout)
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }
        }

        ensureGestureMotion = false // 第一次scroll执行完成，修改标志

        return false

    }


    /**
     * 设置 Activity 亮度
     */
    private fun setAppLight(brightness: Int) {
        try {
            VideoUtils.setAppBrightness(mContext, brightness.toFloat() / 255)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    fun onSeekCompletion() {
        fiveVideoView.seekTo(mGestureProgress)
    }

    fun onActionUp() {
        mGestureMotion = 0;
    }

}