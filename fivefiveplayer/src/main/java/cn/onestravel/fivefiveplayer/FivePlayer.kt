package cn.onestravel.fivefiveplayer

import cn.onestravel.fivefiveplayer.interf.PlayerInterface
import java.util.*

/**
 * @author onestravel
 * @createTime 2020-03-21
 * @description 播放器全局配置
 */
object FivePlayer {
    private val mPlayerList: LinkedList<PlayerInterface> by lazy { LinkedList<PlayerInterface>() }

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
            mPlayerList.remove(player)
        }
    }

    /**
     * 释放全部播放器View
     */
    fun releaseAllVideos() {
        while (mPlayerList.size > 0) {
            val player: PlayerInterface = mPlayerList.first
            releaseVideo(player)
        }
    }

    /**
     * 释放播放器View
     */
    private fun releaseVideo(player: PlayerInterface) {
        player.stop()
        player.release()
        unRegisterPlayer(player)
    }
}