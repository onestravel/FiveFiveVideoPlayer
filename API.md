# FiveFiveVideoPlayer API 文档

## 1. 播放器统一开放API  ``PlayerInterface``

``FiveVideoPlayer``和``FiveVideoView``都实现了以下统一开放API
> * 注：自定义视频播放器View可使用``FiveVideoView``进行内部实现，在此基础上进行控制器，手势控制的开发；自定义的播放器View 都需要实现``PlayerInterface``接口，来实现对应功能；详细请参考``FiveVideoPlayer``类

开放接口主要方法如下：

|    method  | param desc（name: Type） | return | description | version  |
| ---- | ---- | ---- | ---- | ---- |
| setDataSource() | url: String | void | 设置播放视频资源路径 | V 1.0.0 |
| setDataSource() | dataSource: MediaDataSource | void | 设置播放视频资源,可设置标题，是否循环播放 | V 1.0.0 |
| start() | --- | void | 开始播放视频 |  |
| start() | position: Long | void | 从指定位置开始播放视频 | V 1.0.0 |
| pause() | --- | void | 暂停播放视频 | V 1.0.0 |
| stop() | --- | void | 停止播放视频，停止之后必须重新设置视频源 | V 1.0.0 |
| resume() | --- | void | 继续播放视频 | V 1.0.0 |
| seekTo() | position: Long | void | 视频定位到指定位置 | V 1.0.0 |
| reset() | --- | void | 重置播放器 | V 1.0.0 |
| release() | --- | void | 释放播放器 | V 1.0.0 |
| getDuration() | --- | Long | 获取视频总时长 | V 1.0.0 |
| getCurrentPosition() | --- | Long | 获取视频当前位置时长 | V 1.0.0 |
| isPlaying() | --- | Boolean | 获取视频当前是否正在播放 | V 1.0.0 |
| isPaused() | --- | Boolean | 获取视频当前是否正在暂停 | V 1.0.0 |
| isCompletion() | --- | Boolean | 获取视频当前是否播放完成 | V 1.0.0 |
| setVolume() | leftVolume: Float, <br/>rightVolume: Float | void | 设置音量 | V 1.0.0 |
| setSpeed() | speed: Float | void | 设置倍速 | V 1.0.0 |
| setVideoDisplayType() | displayType: Int |  | 设置视频显示类型<br/>{ PlayerInterface.VIDEO_DISPLAY_TYPE_ADAPTER,<br/> PlayerInterface.VIDEO_DISPLAY_TYPE_ORIGINAL,<br/>               PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER,<br/>               PlayerInterface.VIDEO_DISPLAY_TYPE_CENTER_CROP<br/>              } | V 1.0.0 |
| setOnPreparedListener() | onPreparedListener: OnPreparedListener | void | 设置准备完成监听事件 | V 1.0.0 |
| setOnProgressListener() | onProgressListener: OnProgressListener | void | 设置播放进度监听事件 | V 1.0.0 |
| setOnCompleteListener() | onCompleteListener: OnCompleteListener | void | 设置播放完成监听事件 | V 1.0.0 |
| setOnErrorListener() | onErrorListener: OnErrorListener | void | 设置播放异常监听事件 | V 1.0.0 |
| setMediaKernelClass() | clazz:Class<out MediaKernelApi> | void | 设置播放器媒体内核,参数为内核类class,必须是MediaKernelApi的子类 | V 1.0.1 |

## 2. 控制器统一开发API ``ControllerInterface``
> * 注：在``FiveVideoPlayer``基础上实现自定义控制器View时，可以实现该接口类，来实现有视频播放器状态和播放进度反应到控制器上；实现``ControllerActionCallback``接口类，将控制器对应操作回调至播放器类，进行对应的操作（播放/暂停/seekTo/设置倍速等）

|    method  | param desc（name: Type） | return | description | version  |
| ---- | ---- | ---- | ---- | ---- |
| getControllerView() | --- | View | 返回控制器View,改View会加入到播放器底部控制器区域 | V 1.0.0 |
| setMaxProgress() | max: Long | void | 设置控制器进度条最大进度，一般为视频总时长 | V 1.0.0 |
| setBufferingProgress() | percent: Float | void | 设置缓存进度 百分比 |  |
| setProgress() | position: Long, <br/>duration: Long | void | 设置播放进度<br/> @param position 当前播放位置<br/> @param duration 视频总时长 | V 1.0.0 |
| setRotation() | rotation: Float | void | 当控制使用旋转时设置对应的旋转角度，控制横竖屏显示不同的控制器 | V 1.0.0 |
| onStart() | --- | void | 当视频播放时调用，用于更改控制器状态 | V 1.0.0 |
| onPause() | --- | void | 当视频暂停时调用，用于更改控制器状态 | V 1.0.0 |
| onChangePlayerState() | playerState: Int | void | 当播放器状态发生改变时调用<br/>@value PlayerInterface.PLAYER_STATE_NORMAL  正常播放<br/>@value PlayerInterface.PLAYER_STATE_FULL_SCREEN 全屏播放<br/> @value PlayerInterface.PLAYER_STATE_TINY_WINDOW 小窗口播放播放 | V 1.0.0 |
| onSelectorSelected() | obj: Any, <br/>selectData: String | void | 当播放器选择器选中时调用<br/> @param obj 任意类型标志<br/> @param selectData 选择器选中的值 | V 1.0.0 |
| setActionCallBack() | actionCallback: ControllerActionCallback | void | 设置控制器操作动作回调，回调至播放器进行处理 | V 1.0.0 |

## 3. 控制器操作回调统一开发API ``ControllerActionCallback``
> * 注：在``FiveVideoPlayer``基础上实现自定义控制器View时，可以实现该接口类，将控制器对应操作回调至播放器类，进行对应的操作（播放/暂停/seekTo/设置倍速等）

|    method  | param desc（name: Type） | return | description | version  |
| ---- | ---- | ---- | ---- | ---- |
| onActionPlay() | --- | void | 控制器播放操作时调用 | V 1.0.0 |
| onActionPause() | --- | void | 控制器暂停操作时调用 | V 1.0.0 |
| onActionSeekTo() | position: Long | void | 控制器拖动进度条操作时调用 |  |
| onActionChangeSpeed() | speed: Float | void | 控制器改变倍速操作时调用 | V 1.0.0 |
| onActionSetPlayerState() | playerState: Int | void | 当播放器状态发生改变时调用<br/>@value PlayerInterface.PLAYER_STATE_NORMAL  正常播放<br/>@value PlayerInterface.PLAYER_STATE_FULL_SCREEN 全屏播放<br/> @value PlayerInterface.PLAYER_STATE_TINY_WINDOW 小窗口播放播放 | V 1.0.0 |
| onActionShowSelector() | obj: Any,<br/> datas: Array<String>,<br/> selectedData: String | void | 当控制器展示选择器窗口时调用<br/> @param obj 任意类型标志<br/> @param datas 选择器可选值<br/> @param selectData 选择器选中的值 |  |

## 4. 播放器View API
### 4.1  ``FiveVideoView`` API
> * 注：``FiveVideoView``还开放以下API,可以在使用``FiveVideoView``实现对应播放器播放视频时根据需求进行调用

|    method  | param desc（name: Type） | return | description | version  |
| ---- | ---- | ---- | ---- | ---- |
| setDoubleClickPlay() | douleClickPlay:Boolean | void | 设置是否允许双击控制播放暂停,默认 true | V 1.0.0 |
| setClickPlay() | clickPlay:Boolean | void | 设置是否允许单击控制播放暂停,默认 false | V 1.0.0 |
| setGestureControlEnable() | enable:Boolean | void | 设置是否允许开启手势控制（音量/亮度/进度），默认 false | V 1.0.0 |
| setPlayerCallback() | playerCallBack: PlayerCallBack | void | 播放器状态回调（全状态回调） | V 1.0.0 |
| setOnDoubleClickListener() | onDoubleClickListener: OnDoubleClickListener | void | 设置双击事件监听器 | V 1.0.0 |

### 4.2  ``FiveVideoPlayer`` API
> * 注：``FiveVideoPlayer``还开放以下API,可以在使用``FiveVideoPlayer``播放视频时，轻松设置标题栏，控制器，小窗口播放/全屏播放

|    method  | param desc（name: Type） | return | description | version  |
| ---- | ---- | ---- | ---- | ---- |
| setHideViewEnable() | enable:Boolean | void | 设置是否可以隐藏控制器，顶部标题栏，默认 true | V 1.0.0 |
| setGestureControlEnable() | enable:Boolean                   | void   | 设置是否允许开启手势控制（音量/亮度/进度），默认 false | V 1.0.0 |
| setDataSource() | url: String, title: String | void | 设置视频源地址和标题 | V 1.0.0 |
| setMediaController() | controller: ControllerInterface? | void | 设置视频播放控制器，可以为null， | V 1.0.0 |
| enterFullScreen() | --- | void | 进入全屏播放 | V 1.0.0 |
| enterTinyWindow() | --- | void | 进入小窗口播放 | V 1.0.0 |
| exitFullScreenOrTinyWindow() | --- | void | 退出全屏/小窗口播放 | V 1.0.0 |

