package cn.onestravel.fivefiveplayer.kernel

import android.view.Surface
import android.view.TextureView
import cn.onestravel.fivefiveplayer.MediaDataSource


/**
 * @author onestravel
 * @createTime 2020-03-19
 * @description 媒体播放内核API接口
*/
interface MediaKernelInterface : TextureView.SurfaceTextureListener{
    /**
     * 开始播放视频
     */
    fun prepare(dataSource: MediaDataSource)

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
     * 设置Surface
     */
    fun setSurface(surface: Surface)
}