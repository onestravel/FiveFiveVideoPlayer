package cn.onestravel.fivefiveplayer.interf

import android.graphics.SurfaceTexture
import java.lang.Exception

/**
 * @author onestravel
 * @createTime 2020-03-20
 * @description 播放器回调接口
 */
interface PlayerCallBack {
    /**
     * 视频准备完成回调
     */
    fun onPrepared()

    /**
     * 视频开始播放回调
     */
    fun onStart(first: Boolean)

    /**
     * 视频停止播放回调
     */
    fun onStopped()

    /**
     * 视频暂停播放回调
     */
    fun onPaused()

    /**
     * 视频继续播放回调
     */
    fun onResume()

    /**
     * 视频定位到指定位置回调
     */
    fun onSeekTo(position: Long)

    /**
     * 缓存中，视频播放状态为暂停
     */
    fun onBufferingPaused()

    /**
     * 缓存中，视频播放状态为正在播放
     */
    fun onBufferingPlaying()

    /**
     * 视频正在播放
     */
    fun onPlaying()


    /**
     * 视频播放进度回调
     */
    fun onProgressChanged(total: Long, progress: Long)

    /**
     * 视频播放完成回调
     */
    fun onCompletion()


    /**
     * 视频播放异常回调
     */
    fun onError(e: Exception)

    /**
     * 视频大小改变回调
     */
    fun onVideoSizeChanged(width: Int, height: Int)

    /**
     * 设置SurfaceTexture 回调
     */
    fun onSetSurfaceTexture(surface: SurfaceTexture)

}