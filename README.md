# [FiveFiveVideoPlayer](https://github.com/onestravel/FiveFiveVideoPlayer) 使用说明

FiveFiveVideoPlayer 是一个可以高度自定义的播放器，手势改变亮度，音量，进度；支持调整倍速；可以轻松实现全屏视频播放，列表视频播放，小窗口视频播放


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

> version: [![Download](https://api.bintray.com/packages/onestravel/fivefive/FiveFiveVideoPlayer/images/download.svg)](https://bintray.com/onestravel/fivefive/FiveFiveVideoPlayer)

#### 2.1 gradle 引入（$version 需要替换为上图对应的版本号）
```groovy
    implementation 'cn.onestravel:FiveFiveVideoPlayer:$version'
```

例如：

```groovy
    implementation 'cn.onestravel:FiveFiveVideoPlayer:1.0.1'
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
#### 2.5 全局设置播放器内核

在设置播放器视频资源地址之前可设置播放器内核，默认为MediaPlayer内核，可选内核有``IJKPlayer``内核和``ExoPlayer``内核
具体设置方法如下：
kotlin 
```kotlin
 
fun initData {
          val path = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
//         设置为IJKPlayer 播放器内核
//         FivePlayer.mediaKernelClass = IJKPlayerKernel::class.java
//         设置为ExoPlayer播放器内核
         FivePlayer.mediaKernelClass = ExoPlayerKernel::class.java
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

Java

```java
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douyin_list);
        //设置为IJKPlayer 播放器内核
        //FivePlayer.INSTANCE.setMediaKernelClazz(IJKPlayerKernel.class);
        //设置为ExoPlayer播放器内核
        FivePlayer.INSTANCE.setMediaKernelClass(ExoPlayerKernel.class);
       //...
    }

```

#### 2.6 设置是否开启硬件解码

kotlin
```kotlin
    //关闭硬件解码
    FivePlayer.enableMediaCodec = false
```
java
```java
    //关闭硬件解码
    FivePlayer.INSTANCE.setEnableMediaCodec(false);
```

#### 2.7 单个视频播放器设置播放器内核
除了上面所说的可以全局设置播放器内核外，也可以针对某一个播放器设置播放器内核，方法时通过调用播放器``FiveVideoView``或``FiveVideoPlayer``对象的``setMediaKernelClass(clazz:Class<out MediaKernelApi>)``方法来设置单个播放器内核

eg:

```kotlin
    fun initData {
          val path = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
        //设置当前播放器内核为ExoPlayer 内核
        fiveVideoView.setMediaKernelClass(ExoPlayerKernel::class.java)
        fiveVideoView.setDataSource(path)
        fiveVideoView.setOnPreparedListener {
            it.start()
            fiveVideoView.setVideoDisplayType(PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER)
    }
```




## 3. API 文档
点击 [FiveFiveVideoPlayer API 文档](API.md)查看详细API


## 4. 版本记录
> V 1.0.1

    1. 实现集成 IJKplayer 内核，可开启硬解码;
    
    2. 实现ExoPlayer内核;
    
    3. 实现可全局设置播放器内核功能;
    
    4. 修复已知bug


> V 1.0.0

	1. 集成 Media Player 内核
	
	2. 实现视频播放View,实现单击/双击进行暂停，播放
	
	3. 实现视频播放器View,视频播放器Activity
	
	4. 支持手势滑动调整音量，亮度，进度
	
	5. 支持自定义视频播放控制器，播放器内含默认视频控制器
	
## 温馨提示：

在使用过程中，如遇到任何问题，可 添加 ``issues`` 或发送邮件至server@onestravel.cn说明相关问题，我在看到邮件第一时间，会针对相应问题进行沟通解决，谢谢支持！

## License
   Copyright 2020 onestravel
   
   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
   
   http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
