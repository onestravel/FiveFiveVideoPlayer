package cn.onestravel.fivefiveplayer.demo;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

import java.util.ArrayList;
import java.util.List;

import cn.onestravel.fivefiveplayer.FivePlayer;
import cn.onestravel.fivefiveplayer.kernel.IJKPlayerKernel;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by onestravel on 2020/3/26
 */
public class ListVideoActivity extends AppCompatActivity {
    private ListItem currentItem;
    private List<String> dataList = new ArrayList<String>() {
        {
            add("http://vfx.mtime.cn/Video/2019/03/17/mp4/190317150237409904.mp4");
            add("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319125415785691.mp4");
            add("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319104618910544.mp4");
            add("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4");
            add("http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4");
            add("http://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4");
            add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
            add("http://vjs.zencdn.net/v/oceans.mp4");
            add("https://media.w3.org/2010/05/sintel/trailer.mp4");
            add("http://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4");
            add("http://vfx.mtime.cn/Video/2019/03/09/mp4/190309153658147087.mp4");
            add("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312083533415853.mp4");
            add("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4");
            add("http://vfx.mtime.cn/Video/2019/03/13/mp4/190313094901111138.mp4");
            add("http://vfx.mtime.cn/Video/2019/03/14/mp4/190314102306987969.mp4");
            add("http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4");

        }
    };

    private RecyclerView recyclerView;
    private int visibleCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douyin_list);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_IDLE: //滚动停止
                        autoPlayVideo(recyclerView);
                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = manager.findFirstVisibleItemPosition();
                int lastVisibleItem = manager.findLastVisibleItemPosition();
                visibleCount = lastVisibleItem - firstVisibleItem;
            }
        });
        recyclerView.setLayoutManager(manager);
        ListVideoAdapter adapter = new ListVideoAdapter(this, dataList);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        FivePlayer.INSTANCE.releaseAllVideos();
        super.onDestroy();
    }

    public void onInitComplete() {
        ListItem item = (ListItem) recyclerView.getChildAt(0);
        if (item != null) {
            item.start();
            currentItem = item;
        }
    }

    //    @Override
    public void onPageRelease(boolean isNext, int position) {
        FivePlayer.INSTANCE.releaseAllVideos();
    }

    //    @Override
    public void onPageSelected(int position, boolean isBottom) {
        if (currentItem != null) {
            currentItem.pause();
        }
        ListItem item = (ListItem) recyclerView.getChildAt(position);
        if (item != null) {
            item.start();
            currentItem = item;
        }
    }


    /**
     * 自动播放
     */
    private void autoPlayVideo(RecyclerView view) {

        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();

        for (int i = 0; i < visibleCount; i++) {
            if (layoutManager != null && layoutManager.getChildAt(i) != null) {
                View homeGSYVideoPlayer = layoutManager.getChildAt(i);
                Rect rect = new Rect();
                homeGSYVideoPlayer.getLocalVisibleRect(rect);
                int videoheight = homeGSYVideoPlayer.getHeight();
                if (rect.top == 0 && rect.bottom == videoheight) {
                    onPageSelected(i, i == view.getAdapter().getItemCount() - 1);
                    return;
                }

            }
        }

        onPageRelease(true, 0);
    }


}
