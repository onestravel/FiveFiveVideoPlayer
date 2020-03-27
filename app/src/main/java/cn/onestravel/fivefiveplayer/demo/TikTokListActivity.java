package cn.onestravel.fivefiveplayer.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.onestravel.fivefiveplayer.FivePlayer;

/**
 * Created by onestravel on 2020/3/26
 */
public class TikTokListActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douyin_list);
        recyclerView = findViewById(R.id.recyclerView);
        PagerLinearLayoutManager manager = new PagerLinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        TikTokListAdapter adapter = new TikTokListAdapter(this,dataList);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        FivePlayer.INSTANCE.releaseAllVideos();
        super.onDestroy();
    }
}
