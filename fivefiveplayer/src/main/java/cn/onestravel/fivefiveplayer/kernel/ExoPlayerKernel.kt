package cn.onestravel.fivefiveplayer.kernel

import android.net.Uri
import android.os.Handler
import android.view.Surface
import cn.onestravel.fivefiveplayer.MediaDataSource
import cn.onestravel.fivefiveplayer.impl.FivePlayerImpl
import cn.onestravel.fivefiveplayer.interf.PlayerInterface
import cn.onestravel.fivefiveplayer.utils.LogHelper
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import java.lang.Exception

/**
 * @author onestravel
 * @createTime 2020-04-09
 * @description ExoPlayer内核处理类
 */
class ExoPlayerKernel(player: FivePlayerImpl) : MediaKernelApi(player), Player.EventListener,
    VideoListener {
    private var ready: Boolean = false
    private var mMediaPlayer: SimpleExoPlayer = createExoPlayer()
    private val callback by lazy { BufferingUpdate() }
    private val handler by lazy { Handler() }
    private fun createExoPlayer(): SimpleExoPlayer {
        val builder: SimpleExoPlayer.Builder = SimpleExoPlayer.Builder(player.context)
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter.Builder(player.context).build()
        val videoTrackSelectionFactory: TrackSelection.Factory =
            AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector: TrackSelector =
            DefaultTrackSelector(player.context, videoTrackSelectionFactory)

        val loadControl: LoadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
            .setBufferDurationsMs(360000, 600000, 1000, 5000)
            .setTargetBufferBytes(C.LENGTH_UNSET)
            .setPrioritizeTimeOverSizeThresholds(false)
            .createDefaultLoadControl()

        builder.setLoadControl(loadControl)
        builder.setTrackSelector(trackSelector)
        builder.setBandwidthMeter(bandwidthMeter)
        return builder.build()
    }

    override fun prepare(dataSource: MediaDataSource) {
        super.prepare(dataSource)
        LogHelper.i(TAG, "prepare")
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying) {
                mMediaPlayer.stop()
            }
            mMediaPlayer.release()
        }
        ready = false
        mMediaPlayer = createExoPlayer()
        mSurfaceTexture?.let {
            mMediaPlayer.addListener(this)
            mMediaPlayer.addVideoListener(this)
            mMediaPlayer.repeatMode = if (dataSource.isLooping) {
                Player.REPEAT_MODE_ONE
            } else {
                Player.REPEAT_MODE_OFF
            }
            // Produces DataSource instances through which media data is loaded.
            val dataSourceFactory: DataSource.Factory =
                DefaultDataSourceFactory(
                    player.context,
                    Util.getUserAgent(
                        player.context,
                        player.context.packageName
                    )
                )
            dataSource.uri?.let { uri ->
                val videoSource: MediaSource = if (uri.path!!.contains(".m3u8")) {
                    val vs = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
                    vs.addEventListener(handler, null)
                    vs
                } else {
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
                }
                mMediaPlayer.prepare(videoSource)
//                mMediaPlayer.playWhenReady = true
                mMediaPlayer.setVideoSurface(Surface(it))
            }
        }

    }

    override fun start() {
        LogHelper.i(TAG, "start")
        mMediaPlayer.playWhenReady = true
        player.onPlaying()
    }

    override fun start(position: Long) {
        seekTo(position)
        start()
    }

    override fun pause() {
        LogHelper.i(TAG, "pause")
        mMediaPlayer.playWhenReady = false
        player.onPaused()
    }

    override fun stop() {
        LogHelper.i(TAG, "stop")
        mMediaPlayer.stop()
        player.onStopped()
    }

    override fun resume() {
        LogHelper.i(TAG, "resume")
        mMediaPlayer.playWhenReady = true
        player.onPlaying()

    }

    override fun seekTo(position: Long) {
        mMediaPlayer.seekTo(position)
    }

    override fun reset() {
        LogHelper.i(TAG, "reset")
        mMediaPlayer.stop(true)
    }

    override fun release() {
        super.release()
        LogHelper.i(TAG, "release")
        mMediaPlayer.removeListener(this)
        runThread(Runnable {
            try {
                mMediaPlayer.setVideoSurface(null)
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
        mMediaPlayer.volume = leftVolume
    }

    override fun setSpeed(speed: Float) {
        val playbackParameters = PlaybackParameters(speed, 1.0f)
        mMediaPlayer.setPlaybackParameters(playbackParameters)
    }

    override fun setSurface(surface: Surface) {
        mMediaPlayer.setVideoSurface(surface)
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        LogHelper.i(TAG, "onVideoSizeChanged")
        player.onVideoSizeChanged(width, height)
    }

    override fun onRenderedFirstFrame() {
        if (!ready) {
            player.onPrepared()
            ready = true
            LogHelper.i(TAG, "onRenderedFirstFrame")
        }
    }


    override fun onLoadingChanged(isLoading: Boolean) {
        LogHelper.i(TAG, "onLoadingChanged=$isLoading")
        if (isLoading) {
            if (player.getState() == PlayerInterface.STATE_PAUSED || player.getState() == PlayerInterface.STATE_BUFFERING_PAUSED) {
                LogHelper.i(
                    TAG,
                    "onLoadingChanged ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED"
                )
                player.onBufferingPaused()
            } else if (player.getState() != PlayerInterface.STATE_IDLE && player.getState() != PlayerInterface.STATE_PREPARING && player.getState() != PlayerInterface.STATE_ERROR && player.getState() != PlayerInterface.STATE_PREPARED) {
                LogHelper.i(
                    TAG,
                    "onLoadingChanged ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING"
                )
                player.onBufferingPlaying()
            }
        } else {
            LogHelper.i(TAG, "onLoadingChanged ——> player.getState() =${player.getState()}")
            // 填充缓冲区后，MediaPlayer恢复播放/暂停
            if (player.getState() == PlayerInterface.STATE_BUFFERING_PLAYING) {
                player.onPlaying()
                LogHelper.i(TAG, "onLoadingChanged ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING")
            }
            if (player.getState() == PlayerInterface.STATE_BUFFERING_PAUSED) {
                player.onPaused()
                LogHelper.i(TAG, "onLoadingChanged ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED")
            }
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        LogHelper.i(
            TAG,
            "onPlayerStateChanged->playWhenReady=$playWhenReady ; playbackState=$playbackState"
        )
        runUi(Runnable {
            when (playbackState) {
                Player.STATE_IDLE -> {
                }
                Player.STATE_BUFFERING -> {
                    runUi(callback)
                }
                Player.STATE_READY -> {
                    if(playWhenReady){
                        player.onPlaying()
                    }else{
                        player.onPaused()
                    }
                }
                Player.STATE_ENDED -> {
                    player.onCompletion()
                }
            }
        })
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        LogHelper.i(TAG, "onRepeatModeChanged-> $repeatMode")
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        LogHelper.i(TAG, "onShuffleModeEnabledChanged-> $shuffleModeEnabled")
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        LogHelper.e(TAG, "onPlayerError$error")
        player.onError(error)
    }

    override fun onPositionDiscontinuity(reason: Int) {
        LogHelper.i(TAG, "onPositionDiscontinuity ->$reason")
    }


    override fun onSeekProcessed() {
        player.onSeekComplete()
    }


    private inner class BufferingUpdate : Runnable {
        override fun run() {
            if (mMediaPlayer != null) {
                val percent: Int = mMediaPlayer.bufferedPercentage
                runUi(Runnable { player.onBufferingUpdate(percent) })
                if (percent < 100) {
                    runUi(this, 300)
                } else {
                    removeUiCallbacks(this)
                }
            }
        }
    }
}