package cn.onestravel.fivefiveplayer.impl

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import cn.onestravel.fivefiveplayer.FivePlayer
import cn.onestravel.fivefiveplayer.MediaDataSource
import cn.onestravel.fivefiveplayer.interf.PlayerCallBack
import cn.onestravel.fivefiveplayer.interf.PlayerInterface
import cn.onestravel.fivefiveplayer.kernel.MediaKernelApi
import cn.onestravel.fivefiveplayer.kernel.MediaKernelInterface
import cn.onestravel.fivefiveplayer.kernel.MediaPlayerKernel
import cn.onestravel.fivefiveplayer.utils.LogHelper
import cn.onestravel.fivefiveplayer.utils.VideoUtils
import cn.onestravel.fivefiveplayer.view.VideoTextureView
import java.lang.reflect.Constructor

/**
 * @author onestravel
 * @createTime 2020-03-19
 * @description 媒体内核处理类
 */
class FivePlayerImpl(val context: Context) {
    private val TAG: String = javaClass.simpleName
    private var mDataSource: MediaDataSource? = null
    private var mMediaKernel: MediaKernelInterface = newInstance(FivePlayer.mediaKernelClass)
    private var mState: Int = PlayerInterface.STATE_IDLE
    private var mPlayerCallBack: PlayerCallBack? = null
    private var mTextureView: VideoTextureView? = null
    private var continuePlayFromPerPosition = false

    fun attachTextureView(textureView: VideoTextureView) {
        this.mTextureView = textureView
        mTextureView?.let {
            it.surfaceTextureListener = mMediaKernel
            it.isOpaque = false
            it.isActivated = true
        }

    }


    /**
     * 设置回调监听
     */
    fun setPlayerCallBack(playerCallBack: PlayerCallBack) {
        this.mPlayerCallBack = playerCallBack
    }

    /**
     * 获取当前状态
     */
    fun getState(): Int {
        return mState;
    }

    /**
     * 设置播放器媒体内核
     */
    fun setMediaKernel(clazz: Class<out MediaKernelApi>) {
        this.mMediaKernel = newInstance(clazz)
    }

    /**
     * 根据传入的内核类初始化一个内核对象
     */
    private fun newInstance(clazz: Class<out MediaKernelApi>): MediaKernelApi {
        try {
            val cls = Class.forName(clazz.name)
            //构造有一个参数的构造函数；（参数类型）
            val constructor: Constructor<*> = cls.getDeclaredConstructor(FivePlayerImpl::class.java)
            //根据构造函数，传入值生成实例
            return constructor.newInstance(this) as MediaKernelApi
        } catch (e: ClassNotFoundException) {
            LogHelper.e(TAG, " ${clazz.simpleName} class not found", e)
        } catch (e: Exception) {
            LogHelper.e(TAG, "init ${clazz.simpleName} object error", e)
        }
        return MediaPlayerKernel(this)
    }

    /**
     * 设置媒体源
     */
    fun setDataSource(url: String) {
        val uri = Uri.parse(url)
        uri?.let {
            mDataSource =
                MediaDataSource(it)
            mMediaKernel.release()
            mMediaKernel.prepare(this.mDataSource!!)
            mState = PlayerInterface.STATE_PREPARING
        }
    }

    /**
     * 设置媒体源
     */
    fun setDataSource(dataSource: MediaDataSource) {
        dataSource?.let {
            mDataSource = it
            mMediaKernel.release()
            mMediaKernel.prepare(it)
            mState = PlayerInterface.STATE_PREPARING
        }
    }

    /**
     * 开始播放
     */
    fun start() {
        if (mState == PlayerInterface.STATE_PREPARED ||
            mState == PlayerInterface.STATE_PAUSED ||
            mState == PlayerInterface.STATE_COMPLETE
        ) {
            val position = VideoUtils.getSavedPlayPosition(context, mDataSource?.uri)
            if (position > 20) {
                seekTo(position - 20)
            }
            mMediaKernel.start()
            onPlaying()
        }
    }

    /**
     * 从指定位置开始播放
     */
    fun start(position: Long) {
        if (mState == PlayerInterface.STATE_PREPARED ||
            mState == PlayerInterface.STATE_PAUSED ||
            mState == PlayerInterface.STATE_COMPLETE
        ) {
            mMediaKernel.start(position)
            onPlaying()
        }
    }

    /**
     * 暂停播放
     */
    fun pause() {
        if (mState == PlayerInterface.STATE_PLAYING ||
            mState == PlayerInterface.STATE_BUFFERING_PLAYING
        ) {
            VideoUtils.savePlayPosition(context, mDataSource?.uri, getCurrentPosition())
            mMediaKernel.pause()
            onPaused()
        }
    }

    /**
     * 停止播放
     */
    fun stop() {
        if (mState == PlayerInterface.STATE_PLAYING ||
            mState == PlayerInterface.STATE_BUFFERING_PLAYING
        ) {
            VideoUtils.savePlayPosition(context, mDataSource?.uri, getCurrentPosition())
            mMediaKernel.stop()
            onStopped()
        }
    }

    /**
     * 继续播放
     */
    fun resume() {
        if (mState == PlayerInterface.STATE_PAUSED ||
            mState == PlayerInterface.STATE_BUFFERING_PAUSED
        ) {
            val position = VideoUtils.getSavedPlayPosition(context, mDataSource?.uri)
            if (position > 0) {
                seekTo(position)
            }
            mMediaKernel.resume()
            onResume()
            onPlaying()
        }
    }

    /**
     * 定位视频
     */
    fun seekTo(position: Long) {
        try {
            if (mState != PlayerInterface.STATE_IDLE &&
                mState != PlayerInterface.STATE_PREPARING &&
                mState != PlayerInterface.STATE_ERROR
            ) {
                mMediaKernel.seekTo(position)
            }
        } catch (e: java.lang.Exception) {
            LogHelper.e(TAG, "Five player seekTo error:", e)
        }

    }


    /**
     * 获取视频总时长
     */
    fun getDuration(): Long {
        try {
            if (mState != PlayerInterface.STATE_IDLE &&
                mState != PlayerInterface.STATE_PREPARING &&
                mState != PlayerInterface.STATE_ERROR
            ) {
                return mMediaKernel.getDuration()
            }
        } catch (e: java.lang.Exception) {
            LogHelper.e(TAG, "Five player getDuration error:", e)
        }
        return 0
    }

    /**
     * 获取视频当前位置
     */
    fun getCurrentPosition(): Long {
        try {
            if (mState != PlayerInterface.STATE_IDLE &&
                mState != PlayerInterface.STATE_PREPARING &&
                mState != PlayerInterface.STATE_ERROR
            ) {
                return mMediaKernel.getCurrentPosition()
            }
        } catch (e: java.lang.Exception) {
            LogHelper.e(TAG, "Five player getDuration error:", e)
        }
        return 0
    }

    /**
     * 获取视频是否正在播放
     */
    fun isPlaying(): Boolean {
        var isPlaying = false
        try {
            if (mState != PlayerInterface.STATE_IDLE &&
                mState != PlayerInterface.STATE_PREPARING &&
                mState != PlayerInterface.STATE_ERROR
            ) {
                isPlaying = mMediaKernel.isPlaying()
            }
        } catch (e: java.lang.Exception) {
            LogHelper.e(TAG, "Five player isPlaying error:", e)
        }
        return isPlaying && mState == PlayerInterface.STATE_PLAYING
    }


    /**
     * 获取视频是否正在暂停
     */
    fun isPaused(): Boolean {
        return mState == PlayerInterface.STATE_PAUSED
    }

    /**
     * 获取视频是否播放完成
     */
    fun isCompletion(): Boolean {
        return mState == PlayerInterface.STATE_COMPLETE
    }

    /**
     * 设置播放器音量
     */
    fun setVolume(leftVolume: Float, rightVolume: Float) {
        try {
            mMediaKernel.setVolume(leftVolume, rightVolume)
        } catch (e: java.lang.Exception) {
            LogHelper.e(TAG, "Five player change volume error:", e)
        }
    }

    /**
     * 设置播放器倍速
     */
    fun setSpeed(speed: Float) {
        try {
            if (mState != PlayerInterface.STATE_IDLE && mState != PlayerInterface.STATE_PREPARING
                && mState != PlayerInterface.STATE_ERROR
            ) {
                mMediaKernel.setSpeed(speed)
            }
        } catch (e: java.lang.Exception) {
            LogHelper.e(TAG, "Five player change speed error:", e)
        }
    }

    /**
     * 设置播放器SurfaceTexture
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun setSurfaceTexture(surface: SurfaceTexture) {
        LogHelper.e("==================", "setSurfaceTexture")
        mPlayerCallBack?.let { it.onSetSurfaceTexture(surface) }
    }

    /**
     * 播放器重置
     */
    fun reset() {
        try {
            mState = PlayerInterface.STATE_IDLE
            mProgressHandler.removeCallbacks(mProgressTicker)
            VideoUtils.savePlayPosition(context, mDataSource?.uri, 0)
            mMediaKernel.stop()
            mMediaKernel.reset()
        } catch (e: java.lang.Exception) {
            LogHelper.e(TAG, "Five player reset error:", e)
        }
    }

    /**
     * 播放器释放
     */
    fun release() {
        try {
            mState = PlayerInterface.STATE_IDLE
            mProgressHandler.removeCallbacks(mProgressTicker)
            VideoUtils.savePlayPosition(context, mDataSource?.uri, 0)
            mMediaKernel.stop()
            mMediaKernel.release()
        } catch (e: java.lang.Exception) {
            LogHelper.e(TAG, "Five player release error:", e)
        }
    }

    /**
     * 播放器准备完成回调
     */
    fun onPrepared() {
        mState = PlayerInterface.STATE_PREPARED
        val continuePlay = continuePlayFromPerPosition
        start()
        if (!continuePlay) {
            pause()
        }
        mPlayerCallBack?.let {
            it.onPrepared()
        }
    }

    /**
     * 开始渲染图像
     */
    fun onStartRender() {
        if (mState == PlayerInterface.STATE_PREPARED) {
            mState = PlayerInterface.STATE_PLAYING
        }
        mPlayerCallBack?.let {
            it.onStart(true)
        }
        onPlaying()
    }

    /**
     * 停止播放回调
     */
    fun onStopped() {
        continuePlayFromPerPosition = false
        mState = PlayerInterface.STATE_STOP
        mProgressHandler.removeCallbacks(mProgressTicker)
        mPlayerCallBack?.let {
            it.onStopped()
        }
    }

    /**
     * 暂停播放回调
     */
    fun onPaused() {
        continuePlayFromPerPosition = false
        mState = PlayerInterface.STATE_PAUSED
        mProgressHandler.removeCallbacks(mProgressTicker)
        mPlayerCallBack?.let {
            it.onPaused()
        }
    }

    private fun onResume() {
        continuePlayFromPerPosition = true
        mPlayerCallBack?.let {
            it.onResume()
        }
    }


    /**
     * 缓冲播放
     */
    fun onBufferingPlaying() {
        mState = PlayerInterface.STATE_BUFFERING_PLAYING
        mPlayerCallBack?.let {
            it.onBufferingPlaying()
        }
    }

    /**
     * 暂时不播放，以缓冲更多的数据
     */
    fun onBufferingPaused() {
        mState = PlayerInterface.STATE_BUFFERING_PAUSED
        mProgressHandler.removeCallbacks(mProgressTicker)
        mPlayerCallBack?.let {
            it.onBufferingPaused()
        }
    }

    /**
     * 正在播放
     */
    fun onPlaying() {
        mState = PlayerInterface.STATE_PLAYING
        continuePlayFromPerPosition = true
        mProgressHandler.post(mProgressTicker)
        mPlayerCallBack?.let {
            it.onPlaying()
        }

    }

    /**
     * 视频跳转到指定位置成功回调
     */
    fun onSeekTo(position: Long) {
        mPlayerCallBack?.let {
            it.onSeekTo(position)
        }
    }

    /**
     * 视频跳转到指定位置成功回调
     */
    fun onSeekComplete() {
        onSeekTo(getCurrentPosition())
    }

    /**
     * 缓冲更新回调
     */
    fun onBufferingUpdate(percent: Int) {
        mPlayerCallBack?.let {
        }
    }

    /**
     * 视频播放进度更新回调
     */
    fun onProgressChanged(total: Long, progress: Long) {
        mPlayerCallBack?.let {
            it.onProgressChanged(total, progress)
        }
    }

    /**
     * 视频播放完成回调
     */
    fun onCompletion() {
        onProgressChanged(getDuration(), getCurrentPosition())
        continuePlayFromPerPosition = false
        mState = PlayerInterface.STATE_COMPLETE
        mPlayerCallBack?.let {
            it.onCompletion()
        }
        mProgressHandler.removeCallbacks(mProgressTicker)
        VideoUtils.savePlayPosition(context, mDataSource?.uri, 0)
    }

    /**
     * 视频播放异常回调
     */
    fun onError(e: Exception) {
        continuePlayFromPerPosition = false
        mState = PlayerInterface.STATE_ERROR
        LogHelper.e(mMediaKernel.javaClass.simpleName, "Player Exception:", e)
        mPlayerCallBack?.let {
            it.onError(e)
        }
    }

    /**
     * 视频大小改变回调
     */
    fun onVideoSizeChanged(width: Int, height: Int) {
        mTextureView?.let {
            it.resetVideoSize(width, height)
        }
        mPlayerCallBack?.let {
            it.onVideoSizeChanged(width, height)
        }
    }


    //为多线程定义Handler
    private val mProgressHandler = Handler()

    /**
     * 定义一个Runnable对象
     * 用于更新播发进度
     */
    private val mProgressTicker: Runnable = object : Runnable {
        override fun run() {
            //延迟200ms再次执行runnable,就跟计时器一样效果
            mProgressHandler.postDelayed(this, 200)
            //更新播放进度
            onProgressChanged(getDuration(), getCurrentPosition())
        }
    }

}