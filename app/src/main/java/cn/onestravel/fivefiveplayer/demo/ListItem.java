package cn.onestravel.fivefiveplayer.demo;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.onestravel.fivefiveplayer.FiveVideoView;
import cn.onestravel.fivefiveplayer.interf.PlayerInterface;
import kotlin.Unit;

/**
 * Created by onestravel on 2020/3/26
 */
public class ListItem extends FrameLayout {
    private FiveVideoView mVideoView;
    private int mState;

    public ListItem(@NonNull Context context) {
        this(context, null);
    }

    public ListItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_list_item, this);
        mVideoView = findViewById(R.id.videoView);
        mVideoView.setVideoDisplayType(PlayerInterface.VIDEO_DISPLAY_TYPE_FIT_CENTER);
        mVideoView.setClickPlay(true);
        mVideoView.setOnPreparedListener(this::onPrepared);
        mVideoView.setOnProgressListener(this::onProgressChanged);
        setBackgroundColor(Color.BLACK);
    }


    private Unit onProgressChanged(Long aLong, Long aLong1) {
        return null;
    }

    private Unit onPrepared(PlayerInterface playerInterface) {
        mState = PlayerInterface.STATE_PREPARED;
        return null;
    }

    public void setData(String path) {
        mVideoView.setDataSource(path);
    }

    public void start() {
        if (mVideoView.isPaused()) {
            mVideoView.resume();
        } else {
            mVideoView.start();
        }
    }


    public void pause() {
        mVideoView.pause();
    }


    public void stop() {
        mVideoView.stop();
    }

    public void release() {
        mVideoView.release();
    }
}
