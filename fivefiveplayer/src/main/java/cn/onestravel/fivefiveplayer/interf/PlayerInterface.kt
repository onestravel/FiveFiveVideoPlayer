package cn.onestravel.fivefiveplayer.interf

import java.lang.Exception

/**
 * 视频播放器开放API
 * Created by onestravel on 2020/3/19
 */
typealias OnPreparedListener = (PlayerInterface) -> Unit

interface PlayerInterface {
    companion object {
        /**
         * 播放异常
         */
        const val STATE_ERROR: Int = -1

        /**
         * 未设置视频资源
         */
        const val STATE_IDLE: Int = 0

        /**
         * 正在准备状态
         */
        const val STATE_PREPARING: Int = 1

        /**
         * 视频资源准备完成
         */
        const val STATE_PREPARED: Int = 2

        /**
         * 视频正在播放状态
         */
        const val STATE_BUFFERING_PLAYING: Int = 3

        /**
         * 视频暂停播放状态
         */
        const val STATE_BUFFERING_PAUSED: Int = 4

        /**
         * 视频正在播放状态
         */
        const val STATE_PLAYING: Int = 5


        /**
         * 视频暂停播放状态
         */
        const val STATE_PAUSED: Int = 6

        /**
         * 视频停止播放状态
         */
        const val STATE_STOP: Int = 7

        /**
         * 视频播放完成状态
         */
        const val STATE_COMPLETE: Int = 8


        /**
         * 视频自适应
         */
        const val VIDEO_DISPLAY_TYPE_ADAPTER = 0

        /**
         * 原版视频
         */
        const val VIDEO_DISPLAY_TYPE_ORIGINAL = 1

        /**
         * 视频某一个边达到最大宽/高度
         */
        const val VIDEO_DISPLAY_TYPE_FIT_CENTER = 2

        /**
         * 填充满裁切视频
         */
        const val VIDEO_DISPLAY_TYPE_CENTER_CROP = 3

    }

    /**
     * 设置资源
     */
    fun setDataSource(url: String)

    /**
     * 开始播放视频
     */
    fun start()

    /**
     * 从指定位置开始播放视频
     */
    fun start(position: Long)

    /**
     * 暂停播放视频
     */
    fun pause()

    /**
     * 停止播放视频
     */
    fun stop()

    /**
     * 继续播放视频
     */
    fun resume()

    /**
     * 视频定位到指定位置
     */
    fun seekTo(position: Long)

    /**
     * 重置播放器
     */
    fun reset()

    /**
     * 释放播放器
     */
    fun release()


    /**
     * 获取视频总时长
     */
    fun getDuration(): Long

    /**
     * 获取视频当前位置时长
     */
    fun getCurrentPosition(): Long

    /**
     * 获取视频当前是否正在播放
     */
    fun isPlaying(): Boolean

    /**
     * 设置音量
     */
    fun setVolume(leftVolume: Float, rightVolume: Float)

    /**
     * 设置倍速
     */
    fun setSpeed(speed: Float)

    /**
     * 设置旋转角度
     */
    fun setVideoRotation(rotation: Float)

}