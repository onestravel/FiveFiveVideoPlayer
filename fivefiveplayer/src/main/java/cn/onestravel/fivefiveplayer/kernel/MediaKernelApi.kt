package cn.onestravel.fivefiveplayer.kernel

import android.graphics.SurfaceTexture
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.Surface
import cn.onestravel.fivefiveplayer.MediaDataSource
import cn.onestravel.fivefiveplayer.impl.FivePlayerImpl
import cn.onestravel.fivefiveplayer.utils.LogHelper

/**
 * @author onestravel
 * @createTime 2020-03-19
 * @description 播放器内核API
 */
open abstract class MediaKernelApi(protected val player: FivePlayerImpl) : MediaKernelInterface {
    protected val TAG = javaClass.simpleName
    protected var mSurfaceTexture: SurfaceTexture? = null
    protected var mSurface: Surface? = null
    private var mDataSource: MediaDataSource? = null;


    companion object {
        private val mPlayerHandlerThread: HandlerThread by lazy { HandlerThread("FiveFivePlayer") }
        private val mPlayerHandler: Handler by lazy {
            mPlayerHandlerThread.start()
            Handler(mPlayerHandlerThread.looper)
        }
        private val mMainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    }

    fun runUi(runnable: Runnable) {
        mMainHandler.post(runnable)
    }

    fun runUi(runnable: Runnable, delayMillis: Long) {
        mMainHandler.postDelayed(runnable, delayMillis)
    }

    fun removeUiCallbacks(runnable: Runnable){
        mMainHandler.removeCallbacks(runnable)
    }

    fun runThread(runnable: Runnable) {
        mPlayerHandler.post(runnable)
    }

    fun runThread(runnable: Runnable, delayMillis: Long) {
        mPlayerHandler.postDelayed(runnable, delayMillis)
    }

    fun removeThreadCallbacks(runnable: Runnable){
        mPlayerHandler.removeCallbacks(runnable)
    }

    override fun prepare(dataSource: MediaDataSource) {
        this.mDataSource = dataSource
    }

    override fun release() {
        runThread(Runnable {
            mSurface?.let {
                it.release()
                mSurface = null
            }
            mSurfaceTexture?.let {
                it.release()
                mSurfaceTexture = null
            }
        })
    }


    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
//        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface
            mDataSource?.let {
                prepare(it)
            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                player.setSurfaceTexture(mSurfaceTexture!!)
//            }
//        }
    }

}