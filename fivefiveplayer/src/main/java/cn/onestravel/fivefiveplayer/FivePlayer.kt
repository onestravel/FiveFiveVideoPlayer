package cn.onestravel.fivefiveplayer

import cn.onestravel.fivefiveplayer.impl.FivePlayerConfig
import cn.onestravel.fivefiveplayer.interf.PlayerInterface
import java.lang.Exception
import java.util.*

/**
 * @author onestravel
 * @createTime 2020-03-21
 * @description 播放器全局配置
 */
object FivePlayer {
    private val mPlayerList: LinkedList<PlayerInterface> by lazy { LinkedList() }

    /**
     * 播放器注册
     */
    fun registerPlayer(player: PlayerInterface) {
        if (!mPlayerList.contains(player)) {
            mPlayerList.add(player)
        }
    }

    /**
     * 取消播放器注册,并且释放
     */
    fun unRegisterPlayer(player: PlayerInterface) {
        if (mPlayerList.contains(player)) {
            try {
                player.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mPlayerList.remove(player)
        }
    }

    /**
     * 取消播放器注册
     */
    fun releaseAllViews() {
        while (mPlayerList.size > 0) {
            val player: PlayerInterface = mPlayerList.first
            unRegisterPlayer(player)
        }
    }
}