package cn.onestravel.fivefiveplayer.kernel

import android.view.Surface
import cn.onestravel.fivefiveplayer.FivePlayer
import cn.onestravel.fivefiveplayer.MediaDataSource
import cn.onestravel.fivefiveplayer.impl.FivePlayerImpl
import cn.onestravel.fivefiveplayer.interf.PlayerInterface
import cn.onestravel.fivefiveplayer.utils.LogHelper
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.lang.Exception

/**
 * @author onestravel
 * @createTime 2020-04-09
 * @description IJKPlayer内核处理类
 */
open class IJKPlayerKernel(player: FivePlayerImpl) : MediaKernelApi(player),
    IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener,
    IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener,
    IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnVideoSizeChangedListener {
    private var mMediaPlayer: IMediaPlayer = createPlayer()

    /**
     *  创建一个新的player
     */
    private fun createPlayer(): IMediaPlayer {
        val ijkMediaPlayer = IjkMediaPlayer()
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1)
        ijkMediaPlayer.setOption(
            IjkMediaPlayer.OPT_CATEGORY_PLAYER,
            "overlay-format",
            IjkMediaPlayer.SDL_FCC_RV32.toLong()
        )
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"soundtouch", 1)
        ijkMediaPlayer.setVolume(1.0f, 1.0f)
        setEnableMediaCodec(ijkMediaPlayer, FivePlayer.enableMediaCodec)
        return ijkMediaPlayer
    }

    /**
     * 设置是否开启硬解码
     */
    private fun setEnableMediaCodec(
        ijkMediaPlayer: IjkMediaPlayer,
        isEnable: Boolean
    ) {
        val value = if (isEnable) 1 else 0
        ijkMediaPlayer.setOption(
            IjkMediaPlayer.OPT_CATEGORY_PLAYER,
            "mediacodec",
            value.toLong()
        ) //开启硬解码
        ijkMediaPlayer.setOption(
            IjkMediaPlayer.OPT_CATEGORY_PLAYER,
            "mediacodec-auto-rotate",
            value.toLong()
        )
        ijkMediaPlayer.setOption(
            IjkMediaPlayer.OPT_CATEGORY_PLAYER,
            "mediacodec-handle-resolution-change",
            value.toLong()
        )
    }

    override fun prepare(dataSource: MediaDataSource) {
        super.prepare(dataSource)
        LogHelper.i(TAG, "prepare")
        if (mMediaPlayer != null) {
            if(mMediaPlayer.isPlaying) {
                mMediaPlayer.stop()
            }
            mMediaPlayer.release()
        }
        mMediaPlayer = createPlayer()
        mSurfaceTexture?.let {
            mMediaPlayer.isLooping = dataSource.isLooping
            mMediaPlayer.setSurface(Surface(mSurfaceTexture))
            dataSource.uri?.let { uri ->
                try {
                    mMediaPlayer.setOnPreparedListener(this)
                    mMediaPlayer.setOnCompletionListener(this)
                    mMediaPlayer.setOnErrorListener(this)
                    mMediaPlayer.setOnInfoListener(this)
                    mMediaPlayer.setOnBufferingUpdateListener(this)
                    mMediaPlayer.setOnVideoSizeChangedListener(this)
                    mMediaPlayer.setDataSource(player.context, uri, dataSource.header)
                    mMediaPlayer.prepareAsync()
                } catch (e: Exception) {
                    LogHelper.e(TAG, "prepare error ", e)
                }
            }

        }

    }

    override fun start() {
        mMediaPlayer.start()
        mMediaPlayer.setScreenOnWhilePlaying(true)
    }

    override fun start(position: Long) {
        seekTo(position)
        start()
    }

    override fun pause() {
        LogHelper.i(TAG, "pause")
        mMediaPlayer.pause()
        mMediaPlayer.setScreenOnWhilePlaying(false)
    }

    override fun stop() {
        mMediaPlayer.stop()
        mMediaPlayer.setScreenOnWhilePlaying(false)
    }

    override fun resume() {
        LogHelper.i(TAG, "resume")
        mMediaPlayer.setScreenOnWhilePlaying(true)
        mMediaPlayer.start()
    }

    override fun seekTo(position: Long) {
        mMediaPlayer.seekTo(position)

    }

    override fun reset() {
        mMediaPlayer.setScreenOnWhilePlaying(false)
        mMediaPlayer.reset()
    }

    override fun release() {
        mMediaPlayer.setScreenOnWhilePlaying(false)
        super.release()
        runThread(Runnable {
            try {
                mMediaPlayer.setSurface(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mMediaPlayer.release()
        })
    }

    override fun getDuration(): Long {
        return mMediaPlayer.duration
    }

    override fun getCurrentPosition(): Long {
        return mMediaPlayer.currentPosition
    }

    override fun isPlaying(): Boolean {
        return mMediaPlayer.isPlaying
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        mMediaPlayer.setVolume(leftVolume, rightVolume)
    }

    override fun setSpeed(speed: Float) {
        if (mMediaPlayer is IjkMediaPlayer) {
            (mMediaPlayer as IjkMediaPlayer).setSpeed(speed)
        }
    }

    override fun setSurface(surface: Surface) {
        mMediaPlayer.setSurface(surface)
    }

    override fun onPrepared(p0: IMediaPlayer?) {
        player.onPrepared()
    }

    override fun onCompletion(p0: IMediaPlayer?) {
        player.onCompletion()
    }

    override fun onError(p0: IMediaPlayer?, what: Int, extra: Int): Boolean {
        player.onError(Exception("MediaPlayerKernel Error what:" + what + ",extra:" + extra + ", hashCode:[" + this.hashCode() + "] "))
        player.release()
        return true
    }

    override fun onInfo(p0: IMediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {
            IMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
//                statusChange(STATUS_LOADING)
                if (player.getState() == PlayerInterface.STATE_PAUSED || player.getState() == PlayerInterface.STATE_BUFFERING_PAUSED) {
                    LogHelper.i(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED")
                    player.onBufferingPaused()
                } else {
                    LogHelper.i(
                        TAG,
                        "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING"
                    )
                    player.onBufferingPlaying()
                }
            }
            IMediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (player.getState() == PlayerInterface.STATE_BUFFERING_PLAYING) {
                    player.onPlaying()
                    LogHelper.i(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING")
                }
                if (player.getState() == PlayerInterface.STATE_BUFFERING_PAUSED) {
                    player.onPaused()
                    LogHelper.i(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED")
                }
            }

            IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH ->
                //显示下载速度
                LogHelper.i(TAG, "onInfo ——> download rate：$extra")
            IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START
                //                statusChange(STATUS_PLAYING);
            -> player.onStartRender()
        }
        return false

    }

    override fun onBufferingUpdate(p0: IMediaPlayer?, percent: Int) {
        player.onBufferingUpdate(percent)
    }

    override fun onVideoSizeChanged(
        p0: IMediaPlayer?,
        width: Int,
        height: Int,
        sar_num: Int,
        sar_den: Int
    ) {
        player.onVideoSizeChanged(width, height)
    }
}