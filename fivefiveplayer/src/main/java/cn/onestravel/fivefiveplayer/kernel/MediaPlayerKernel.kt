package cn.onestravel.fivefiveplayer.kernel

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.view.Surface
import cn.onestravel.fivefiveplayer.MediaDataSource
import cn.onestravel.fivefiveplayer.utils.LogHelper
import cn.onestravel.fivefiveplayer.impl.FivePlayerImpl
import cn.onestravel.fivefiveplayer.interf.PlayerInterface


/**
 * @author onestravel
 * @createTime 2020-03-19
 * @description MediaPlayer 播放器内核
 */
open class MediaPlayerKernel(player: FivePlayerImpl) : MediaKernelApi(player),
    MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnVideoSizeChangedListener {
    private var mMediaPlayer: MediaPlayer = MediaPlayer()
    override fun prepare(dataSource: MediaDataSource) {
        super.prepare(dataSource)
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying) {
                mMediaPlayer.stop()
            }
            mMediaPlayer.release()
        }
        mMediaPlayer = MediaPlayer()
        mSurfaceTexture?.let {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer.isLooping = dataSource.isLooping
            mMediaPlayer.setOnPreparedListener(this)
            mMediaPlayer.setOnCompletionListener(this)
            mMediaPlayer.setOnBufferingUpdateListener(this)
            mMediaPlayer.setScreenOnWhilePlaying(true)
            mMediaPlayer.setOnSeekCompleteListener(this)
            mMediaPlayer.setOnErrorListener(this)
            mMediaPlayer.setOnInfoListener(this)
            mMediaPlayer.setOnVideoSizeChangedListener(this)
            mSurface = Surface(it)
            mMediaPlayer.setSurface(mSurface)
            dataSource.uri?.let { uri ->
                try {
                    mMediaPlayer.setDataSource(player.context, uri, dataSource.header)
                    mMediaPlayer.prepareAsync()
                } catch (e: java.lang.Exception) {
                    player.onError(e)
                }
            }
        }
    }


    override fun start() {
        mMediaPlayer.start()
    }

    override fun start(position: Long) {
        seekTo(position)
        mMediaPlayer.start()
    }

    override fun pause() {
        mMediaPlayer.pause()
    }

    override fun stop() {
        mMediaPlayer.stop()
    }

    override fun resume() {
        mMediaPlayer.start()
    }

    override fun seekTo(position: Long) {
        mMediaPlayer.seekTo(position.toInt())
    }

    override fun reset() {
        mMediaPlayer.reset()
    }

    override fun release() {
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
        return mMediaPlayer.duration.toLong()
    }

    override fun getCurrentPosition(): Long {
        return mMediaPlayer.currentPosition.toLong()
    }

    override fun isPlaying(): Boolean {
        return mMediaPlayer.isPlaying
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        runThread(Runnable {
            mMediaPlayer.setVolume(leftVolume, rightVolume)
        })
    }

    override fun setSpeed(speed: Float) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val isPlay = isPlaying()
                val pp: PlaybackParams = mMediaPlayer.playbackParams
                pp.speed = speed
                mMediaPlayer.playbackParams = pp
                if (!isPlay) {
                    pause()
                }
            } else {
                LogHelper.e(TAG, "The current version does not support adjusting the double speed")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun setSurface(surface: Surface) {
        mMediaPlayer.setSurface(surface)
    }


    override fun onPrepared(mp: MediaPlayer?) {
        player.onPrepared()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        player.onCompletion()
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        player.onBufferingUpdate(percent)
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        player.onSeekComplete()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        if (what != 38 && extra != -38 && what != -38 && extra != 38 && extra != -19) {
            player.onError(Exception("MediaPlayerKernel Error what:" + what + ",extra:" + extra + ", hashCode:[" + this.hashCode() + "] "))
            player.release()
        }
        return true
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            // 播放器开始渲染
            LogHelper.i(TAG, "onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING")
            player.onStartRender()
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            // MediaPlayer暂时不播放，以缓冲更多的数据
            if (player.getState() == PlayerInterface.STATE_PAUSED || player.getState() == PlayerInterface.STATE_BUFFERING_PAUSED) {
                LogHelper.i(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED")
                player.onBufferingPaused()
            } else {
                LogHelper.i(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING")
                player.onBufferingPlaying()
            }
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            // 填充缓冲区后，MediaPlayer恢复播放/暂停
            if (player.getState() == PlayerInterface.STATE_BUFFERING_PLAYING) {
                player.onPlaying()
                LogHelper.i(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING")
            }
            if (player.getState() == PlayerInterface.STATE_BUFFERING_PAUSED) {
                player.onPaused()
                LogHelper.i(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED")
            }
        } else if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
            LogHelper.w(TAG, "Video cannot be seekTo, for live video")
        } else {
            LogHelper.i(TAG, "onInfo ——> what：$what")
        }
        return true
    }

    override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
        player.onVideoSizeChanged(width, height)
    }


}