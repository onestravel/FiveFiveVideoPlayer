package cn.onestravel.fivefiveplayer

import androidx.annotation.IntDef
import cn.onestravel.fivefiveplayer.interf.PlayerInterface

/**
 * Created by wanghu on 2020/3/20
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [PlayerInterface.VIDEO_DISPLAY_TYPE_ADAPTER, PlayerInterface.VIDEO_DISPLAY_TYPE_ORIGINAL, PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER, PlayerInterface.VIDEO_DISPLAY_TYPE_CENTER_CROP])
annotation class VideoDisplayTypeDef {
}