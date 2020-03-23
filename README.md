# FiveFivePlayer 使用说明

## 1. 效果示例
#### 1.1 效果图

#### 1.2 APK 功能体验
demo比较粗糙，请勿介意

[APK 下载](resources/apk/FiveFivePlayer.apk?raw=true) 安装体验

扫描二维码图片安装体验


![二维码](resources/apk/FiveFivePlayer_qr_code.png)
## 2. 快速集成
#### 2.1 gradle 引入
```
    implementation 'cn.onestravel:FiveFivePlayer:0.0.1'
```
#### 2.2 添加权限
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission-sdk-23 android:name="android.permission.WRITE_SETTINGS" />
```
#### 2.3 使用 ``FiveVideoPlayerActivity`` 播放视频

FiveVideoPlayerActivity 是一个集成播放器的Activity,在只需要播放视频的情况下可直接使用

> 使用方式：在需要播放视频的地方添加以下代码，开始播放视频

```
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
可以根据不同的设计方案进行控制器的替换（需实现控制器接口或集成现有控制器类，对需要调整部分进行更改）
可以直接使用右侧选择窗口，实现倍速调整（功能已实现）或者 视频质量的切换（该功能暂未实现）

> 使用方式：
- 在布局 xml 文件中加入一下代码

```
  <cn.onestravel.fivefiveplayer.FiveVideoPlayer
         android:id="@+id/fiveVideoPlayer"
         android:layout_width="match_parent"
         android:layout_height="match_parent" />
```

- 在Activity 中使用该 View 进行视频播放

```
         val path = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
                val title = "玩具总动员"
                fiveVideoPlayer.setOnPreparedListener {
                    it.start()
                    it.setVideoDisplayType(PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER)
                }
                fiveVideoPlayer.setDataSource(path,title)
```
或

```
        val path = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
        val title = "玩具总动员"
        val looping = false;
        val mediaDataSource = MediaDataSource(title, Uri.parse(path), looping)
        fiveVideoPlayer.setOnPreparedListener {
            it.start()
            it.setVideoDisplayType(PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER)
        }
        fiveVideoPlayer.setDataSource(it)
```


## 3. API 文档



## 4. 版本记录


> V 1.0.0
1. 集成 Media Player 内核
2. 实现视频播放View,实现单击/双击进行暂停，播放
4. 实现视频播放器View,视频播放器Activity
3. 支持手势滑动调整音量，亮度，进度
5. 支持自定义视频播放控制器，播放器内含默认视频控制器
