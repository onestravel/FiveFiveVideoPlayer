package cn.onestravel.fivefiveplayer.kernel

import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import cn.onestravel.fivefiveplayer.impl.FivePlayer

/**
 * @author onestravel
 * @createTime 2020-03-19
 * @description 播放器内核API
 */
abstract class MediaKernelApi(player: FivePlayer) : MediaKernelInterface {
    protected val TAG = javaClass.simpleName
    protected var mSurfaceTexture: SurfaceTexture? = null
    private val mPlayerHandlerThread: HandlerThread by lazy { HandlerThread("FiveFivePlayer") }
    private val mPlayerHandler: Handler by lazy { Handler(mPlayerHandlerThread.looper) }
    private val mMainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    init {

    }


    fun runUi(runnable: Runnable) {
        mMainHandler.post(runnable);
    }

    fun runUi(runnable: Runnable, delayMillis: Long) {
        mMainHandler.postDelayed(runnable, delayMillis)
    }

    fun runThread(runnable: Runnable) {
        mPlayerHandler.post(runnable);
    }

    fun runThread(runnable: Runnable, delayMillis: Long) {
        mPlayerHandler.postDelayed(runnable, delayMillis)
    }
}