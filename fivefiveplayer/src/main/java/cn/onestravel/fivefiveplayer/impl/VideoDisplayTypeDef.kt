package cn.onestravel.fivefiveplayer.impl

import androidx.annotation.IntDef
import cn.onestravel.fivefiveplayer.interf.PlayerInterface

/**
 * @author onestravel
 * @createTime 2020-03-20
 * @description TODO
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [PlayerInterface.VIDEO_DISPLAY_TYPE_ADAPTER, PlayerInterface.VIDEO_DISPLAY_TYPE_ORIGINAL, PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER, PlayerInterface.VIDEO_DISPLAY_TYPE_CENTER_CROP])
annotation class VideoDisplayTypeDef {
}