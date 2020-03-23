# FiveFivePlayer 使用说明

## 1. 效果示例
#### 1.1 效果图

#### 1.2 APK 功能体验
[APK 下载](https://github.com/onestravel/FiveFivePlayer/tree/master/resources/apk/FiveFivePlayer.apk)

扫描二维码图片安装体验
![二维码](https://github.com/onestravel/FiveFivePlayer/tree/master/resources/apk/FiveFivePlayer_qr_code.png)
## 2. 快速集成
#### 2.1 gradle 引入
```
    implementation project(path: ':fivefiveplayer')
```
#### 2.2 添加权限
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission-sdk-23 android:name="android.permission.WRITE_SETTINGS" />
```
#### 2.3 使用 ``FiveVideoPlayerActivity`` 播放视频

在需要播放视频的地方添加以下代码，开始播放视频

```
 val path = "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
 // 传入视频地址开始播放
 FiveVideoPlayerActivity.start(this, path)
 // 传入视频地址和视频标题开始播放
 FiveVideoPlayerActivity.start(this, path,"玩具总动员")
 // 传入视频地址和视频标题开始循环播放
 FiveVideoPlayerActivity.start(this, path,"玩具总动员",true)
```

## 3. API 文档



## 4. 版本记录


> V 1.0.0
1. 集成 Media Player 内核
2. 实现视频播放View,实现单击/双击进行暂停，播放
4. 实现视频播放器View,视频播放器Activity
3. 支持手势滑动调整音量，亮度，进度
5. 支持自定义视频播放控制器，播放器内含默认视频控制器
