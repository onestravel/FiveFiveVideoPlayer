# FiveFivePlayer 使用说明

FiveFivePlayer 是一个可以高度自定义的播放器，手势改变亮度，音量，进度；支持调整倍速；可以轻松实现全屏视频播放，列表视频播放，小窗口视频播放


## 1. 效果示例
#### 1.1 效果图



![竖屏效果](resources/screenshotcut/normal_screen.gif)

竖屏效果



![横屏效果](resources/screenshotcut/full_screen.gif)

横屏效果

#### 1.2 APK 功能体验
demo比较粗糙，请勿介意

[APK 下载](resources/apk/FiveFivePlayer.apk?raw=true) 安装体验

扫描二维码图片安装体验


![二维码](resources/apk/FiveFivePlayer_qr_code.png)
## 2. 快速集成

> version: [![Download](https://api.bintray.com/packages/onestravel/fivefive/FiveFivePlayer/images/download.svg)](https://bintray.com/onestravel/fivefive/FiveFivePlayer)

#### 2.1 gradle 引入（$version 需要替换为上图对应的版本号）
```groovy
    implementation 'cn.onestravel:FiveFivePlayer:$version'
```

例如：

```groovy
    implementation 'cn.onestravel:FiveFivePlayer:1.0.0'
```

#### 2.2 添加权限
```xml
    <uses-permission android:name="android.permission.INTERNET" />
```
#### 2.3 使用 ``FiveVideoPlayerActivity`` 播放视频

FiveVideoPlayerActivity 是一个集成播放器的Activity,在只需要播放视频的情况下可直接使用

> 使用方式：在需要播放视频的地方添加以下代码，开始播放视频

```kotlin
 val path = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
 // 传入视频地址开始播放
 FiveVideoPlayerActivity.start(this, path)
 // 传入视频地址和视频标题开始播放
 FiveVideoPlayerActivity.start(this, path,"玩具总动员")
 // 传入视频地址和视频标题开始循环播放
 FiveVideoPlayerActivity.start(this, path,"玩具总动员",true)
```

#### 2.4 使用 ``FiveVideoPlayer`` 播放视频

FiveVideoPlayer ：集成了视频播放，控制器，顶部标题栏，右侧选择窗口的View，可以直接使用在xml布局中，
方便使用视频播放器时根据自己的情况更改视频播放器展示View；
可以根据不同的设计方案进行控制器的替换（需实现控制器接口或继承现有控制器类，对需要调整部分进行更改）
可以直接使用右侧选择窗口，实现倍速调整（功能已实现）或者 视频质量的切换（该功能暂未实现）

> 使用方式：
- 在布局 xml 文件中加入一下代码

```xml
  <cn.onestravel.fivefiveplayer.FiveVideoPlayer
         android:id="@+id/fiveVideoPlayer"
         android:layout_width="match_parent"
         android:layout_height="match_parent" />
```

- 在Activity 中使用该 View 进行视频播放

```kotlin
        fun initData {
            val path = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
            val title = "玩具总动员"
            fiveVideoPlayer.setOnPreparedListener {
                    it.start()
                    it.setVideoDisplayType(PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER)
            }
            fiveVideoPlayer.setDataSource(path,title)
        }

     override fun onDestroy() {
        try {
            fiveVideoPlayer?.let {
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
```
或

```kotlin
    fun initData {
        val path = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
        val title = "玩具总动员"
        val looping = false;
        val mediaDataSource = MediaDataSource(title, Uri.parse(path), looping)
        fiveVideoPlayer.setOnPreparedListener {
            it.start()
            it.setVideoDisplayType(PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER)
        }
        fiveVideoPlayer.setDataSource(it)
        }

       override fun onDestroy() {
           try {
               fiveVideoPlayer?.let {
                   it.reset()
                   it.release()
               }
           } catch (e: Exception) {
               e.printStackTrace()
           }
           super.onDestroy()
       }
```

#### 2.4 使用 ``FiveVideoView`` 播放视频

FiveVideoView ：视频播放的View，无控制栏，标题栏，可设置（单击/双击进行播放，暂停操作）可以直接使用在xml布局中，
一般使用在自定义程度比较高的视频播放器中，或者在列表播放中使用；

> 使用方式：
- 在布局 xml 文件中加入一下代码

```xml
   <cn.onestravel.fivefiveplayer.FiveVideoView
          android:id="@+id/fiveVideoView"
          android:layout_width="match_parent"
          android:layout_height="200dp"
          android:background="@android:color/black"
          app:layout_constraintTop_toTopOf="parent" />
```

- 在Activity 中使用该 View 进行视频播放

```kotlin
    fun initData {
          val path = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
        fiveVideoView.setDataSource(path)
        fiveVideoView.setOnPreparedListener {
            it.start()
            fiveVideoView.setVideoDisplayType(PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER)
    }
      
     override fun onDestroy() {
           try {
               fiveVideoPlayer?.let {
                   it.reset()
                   it.release()
               }
           } catch (e: Exception) {
               e.printStackTrace()
           }
           super.onDestroy()
       }
```


## 3. API 文档
#### 3.1 播放器统一开放API  ``PlayerInterface``

``FiveVideoPlayer``和``FiveVideo``都实现了以下统一开放API

##### 3.1.1 method

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
| setMediaKernel() | mediaKernel: MediaKernelInterface | void | 设置播放器媒体内核 | V 1.0.0 |

#### 3.2 控制器统一开发API ``ControllerInterface``
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

#### 3.3 控制器操作回调统一开发API ``ControllerActionCallback``
|    method  | param desc（name: Type） | return | description | version  |
| ---- | ---- | ---- | ---- | ---- |
| onActionPlay() | --- | void | 控制器播放操作时调用 | V 1.0.0 |
| onActionPause() | --- | void | 控制器暂停操作时调用 | V 1.0.0 |
| onActionSeekTo() | position: Long | void | 控制器拖动进度条操作时调用 |  |
| onActionChangeSpeed() | speed: Float | void | 控制器改变倍速操作时调用 | V 1.0.0 |
| onActionSetPlayerState() | playerState: Int | void | 当播放器状态发生改变时调用<br/>@value PlayerInterface.PLAYER_STATE_NORMAL  正常播放<br/>@value PlayerInterface.PLAYER_STATE_FULL_SCREEN 全屏播放<br/> @value PlayerInterface.PLAYER_STATE_TINY_WINDOW 小窗口播放播放 | V 1.0.0 |
| onActionShowSelector() | obj: Any,<br/> datas: Array<String>,<br/> selectedData: String | void | 当控制器展示选择器窗口时调用<br/> @param obj 任意类型标志<br/> @param datas 选择器可选值<br/> @param selectData 选择器选中的值 |  |

#### 3.4 其他API
##### 3.4.1  ``FiveVideoView`` API
|    method  | param desc（name: Type） | return | description | version  |
| ---- | ---- | ---- | ---- | ---- |
| setDoubleClickPlay() | douleClickPlay:Boolean | void | 设置是否允许双击控制播放暂停,默认 true | V 1.0.0 |
| setClickPlay() | clickPlay:Boolean | void | 设置是否允许单击控制播放暂停,默认 false | V 1.0.0 |
| setGestureControlEnable() | enable:Boolean | void | 设置是否允许开启手势控制（音量/亮度/进度），默认 false | V 1.0.0 |
| setPlayerCallback() | playerCallBack: PlayerCallBack | void | 播放器状态回调（全状态回调） | V 1.0.0 |
| setOnDoubleClickListener() | onDoubleClickListener: OnDoubleClickListener | void | 设置双击事件监听器 | V 1.0.0 |

##### 3.4.2  ``FiveVideoPlayer`` API
|    method  | param desc（name: Type） | return | description | version  |
| ---- | ---- | ---- | ---- | ---- |
| setHideViewEnable() | enable:Boolean | void | 设置是否可以隐藏控制器，顶部标题栏，默认 true | V 1.0.0 |
| setGestureControlEnable() | enable:Boolean                   | void   | 设置是否允许开启手势控制（音量/亮度/进度），默认 false | V 1.0.0 |
| setDataSource() | url: String, title: String | void | 设置视频源地址和标题 | V 1.0.0 |
| setMediaController() | controller: ControllerInterface? | void | 设置视频播放控制器，可以为null， | V 1.0.0 |
| enterFullScreen() | --- | void | 进入全屏播放 | V 1.0.0 |
| enterTinyWindow() | --- | void | 进入小窗口播放 | V 1.0.0 |
| exitFullScreenOrTinyWindow() | --- | void | 退出全屏/小窗口播放 | V 1.0.0 |


## 4. 版本记录


> V 1.0.0

	1. 集成 Media Player 内核
	
	2. 实现视频播放View,实现单击/双击进行暂停，播放
	
	3. 实现视频播放器View,视频播放器Activity
	
	4. 支持手势滑动调整音量，亮度，进度
	
	5. 支持自定义视频播放控制器，播放器内含默认视频控制器


