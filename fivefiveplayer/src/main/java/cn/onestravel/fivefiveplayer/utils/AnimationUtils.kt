package cn.onestravel.fivefiveplayer.utils

import android.content.Context
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import cn.onestravel.fivefiveplayer.R

/**
 * @author onestravel
 * @createTime 2020-03-22
 * @description 动画工具类
 */
object AnimationUtils {
    fun makeInAnimation(context: Context, fromType: AnimationFromType): Animation {
        var anim = when (fromType) {
            AnimationFromType.TOP -> {
                AnimationUtils.loadAnimation(context, R.anim.five_anim_top_in)
            }
            AnimationFromType.BOTTOM -> {
                AnimationUtils.loadAnimation(context, R.anim.five_anim_bottom_in)
            }
            AnimationFromType.LEFT -> {
                AnimationUtils.loadAnimation(context, R.anim.five_anim_left_in)
            }
            AnimationFromType.RIGHT -> {
                AnimationUtils.loadAnimation(context, R.anim.five_anim_right_in)
            }
        }
        anim?.let {
            it.interpolator = AccelerateInterpolator()
        }
        return anim
    }


    fun makeOutAnimation(context: Context, fromType: AnimationFromType): Animation {
        var anim = when (fromType) {
            AnimationFromType.TOP -> {
                AnimationUtils.loadAnimation(context, R.anim.five_anim_top_out)
            }
            AnimationFromType.BOTTOM -> {
                AnimationUtils.loadAnimation(context, R.anim.five_anim_bottom_out)
            }
            AnimationFromType.LEFT -> {
                AnimationUtils.loadAnimation(context, R.anim.five_anim_left_out)
            }
            AnimationFromType.RIGHT -> {
                AnimationUtils.loadAnimation(context, R.anim.five_anim_right_out)
            }
        }
        anim?.let {
            it.interpolator = AccelerateInterpolator()
        }
        return anim
    }
}

enum class AnimationFromType {
    TOP, RIGHT, BOTTOM, LEFT
}