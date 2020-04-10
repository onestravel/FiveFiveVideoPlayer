package cn.onestravel.fivefiveplayer.view

import android.content.Context
import android.graphics.Color
import android.view.TextureView
import android.view.View
import cn.onestravel.fivefiveplayer.interf.PlayerInterface
import cn.onestravel.fivefiveplayer.utils.LogHelper

/**
 * @author onestravel
 * @createTime 2020-02-11
 * @description 视频播放TextureView
 */
open class VideoTextureView(context: Context?) : TextureView(context) {
    private var mVideoHeight = 0
    private var mVideoWidth = 0
     var videoDisplayType: Int = PlayerInterface.VIDEO_DISPLAY_TYPE_ADAPTER
        set(value) {
            field = value
            requestLayout()
        }


    /**
     * 重置视频宽和高
     * @param videoWidth
     * @param videoHeight
     */
    fun resetVideoSize(videoWidth: Int, videoHeight: Int) {
        if (this.mVideoWidth != videoWidth && this.mVideoHeight != videoHeight) {
            this.mVideoWidth = videoWidth
            this.mVideoHeight = videoHeight
            requestLayout()
        }
    }

    override fun setRotation(rotation: Float) {
        if (rotation != getRotation()) {
            super.setRotation(rotation)
            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        val viewRotation = rotation

        // 如果判断成立，则说明显示的TextureView和本身的位置是有90度的旋转的，所以需要交换宽高参数。
        if (viewRotation == 90f || viewRotation == 270f) {
            val tempMeasureSpec = widthMeasureSpec
            widthMeasureSpec = heightMeasureSpec
            heightMeasureSpec = tempMeasureSpec
        }
        var width = View.getDefaultSize(mVideoWidth, widthMeasureSpec)
        var height = View.getDefaultSize(mVideoHeight, heightMeasureSpec)
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
            val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
            val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize
                height = heightSpecSize
                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize
                height = width * mVideoHeight / mVideoWidth
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize
                    width = height * mVideoWidth / mVideoHeight
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize
                width = height * mVideoWidth / mVideoHeight
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize
                    height = width * mVideoHeight / mVideoWidth
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth
                height = mVideoHeight
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize
                    width = height * mVideoWidth / mVideoHeight
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize
                    height = width * mVideoHeight / mVideoWidth
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        var parentHeight = (parent as View).measuredHeight
        var parentWidth = (parent as View).measuredWidth

        if (viewRotation == 90f || viewRotation == 270f) {
            val tempSize: Int = parentWidth
            parentWidth = parentHeight
            parentHeight = tempSize
        }

        if (parentWidth != 0 && parentHeight != 0 && mVideoWidth != 0 && mVideoHeight != 0) {
            if (videoDisplayType == PlayerInterface.VIDEO_DISPLAY_TYPE_ORIGINAL) {
                /**原图 */
                height = mVideoHeight
                width = mVideoWidth
            } else if (videoDisplayType == PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER) {
                /**保持原比例，填充至某一边达到最大宽度*/
                if (parentWidth.toDouble() / mVideoWidth > parentHeight.toDouble() / mVideoHeight) {
                    (parentWidth.toDouble() / width.toDouble() * height.toDouble()).toInt()
                    width =
                        (mVideoWidth.toDouble() * parentHeight.toDouble() / mVideoHeight.toDouble()).toInt()
                    height = parentHeight
                } else if (parentWidth.toDouble() / mVideoWidth < parentHeight.toDouble() / mVideoHeight) {
                    width = parentWidth;
                    height =
                        (parentWidth.toDouble() * mVideoHeight.toDouble() / mVideoWidth.toDouble()).toInt()
                }
            } else if (videoDisplayType == PlayerInterface.VIDEO_DISPLAY_TYPE_CENTER_CROP) {
                /**保持原比例，填充满后中心裁剪*/
                if (mVideoHeight.toDouble() / mVideoWidth > parentHeight.toDouble() / parentWidth) {
                    height =
                        (parentWidth.toDouble() / width.toDouble() * height.toDouble()).toInt()
                    width = parentWidth
                } else if (mVideoHeight.toDouble() / mVideoWidth < parentHeight.toDouble() / parentWidth) {
                    width =
                        (parentHeight.toDouble() / height.toDouble() * width.toDouble()).toInt()
                    height = parentHeight
                }
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        LogHelper.e("==================", "onAttachedToWindow=" + this)
    }

    init {
        isOpaque = true
    }
}