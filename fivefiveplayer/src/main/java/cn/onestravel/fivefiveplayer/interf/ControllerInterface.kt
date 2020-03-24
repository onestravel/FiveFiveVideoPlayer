package cn.onestravel.fivefiveplayer.interf

import android.view.View


/**
 * @author onestravel
 * @createTime 2020-03-21
 * @description 播放器控制器接口，播放器操作反应到控制器展示的操作
 */
interface ControllerInterface {
    /**
     * 获取控制器View
     */
    fun getControllerView(): View

    /**
     * 设置最大进度
     * @param max 最大进度
     */
    fun setMaxProgress(max: Long)

    /**
     * 设置缓存进度
     * @param percent 缓存进度百分比
     */
    fun setBufferingProgress(percent: Float)

    /**
     * 设置播放进度
     * @param position 当前播放位置
     * @param duration 视频总时长
     */
    fun setProgress(position: Long, duration: Long)

    /**
     * 设置视频旋转角度
     * @param rotation 视频旋转角度
     */
    fun setRotation(rotation: Float)

    /**
     * 当开始播放按时调用
     */
    fun onStart()

    /**
     * 当暂停播放时调用
     */
    fun onPause()

    /**
     * 当播放器状态发生改变时调用
     *
     */
    fun onChangePlayerState(playerState: Int)


    /**
     * 当播放器选择器选中时调用
     *
     */
    fun onSelectorSelected(obj: Any, selectData: String)

    /**
     * 设置控制器操作回调
     */
    fun setActionCallBack(actionCallback: ControllerActionCallback)

}

/**
 * 控制器操作更改视频播放器接口回调，回调至播放器，由播放器执行相关操作
 */
interface ControllerActionCallback {
    /**
     * 控制器播放操作
     */
    fun onActionPlay()

    /**
     * 控制器暂停播放操作
     */
    fun onActionPause()

    /**
     * 控制器拖动进度条时调用
     */
    fun onActionSeekTo(position: Long)

    /**
     * 当控制器更改倍速时调用
     */
    fun onActionChangeSpeed(speed: Float)

    /**
     * 当控制器更改全屏时调用
     */
    fun onActionSetPlayerState(playerState: Int)

    /**
     * 当控制器更改清晰度时调用
     */
    fun onActionChangeDefinition(definitionType: DefinitionType)


    /**
     * 当控制器展示选择器窗口时调用
     */
    fun onActionShowSelector(obj: Any, datas: Array<String>, selectedData: String)
}
