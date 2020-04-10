package cn.onestravel.fivefiveplayer.demo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;

import cn.onestravel.fivefiveplayer.demo.common.BaseRecyclerAdapter;

/**
 * Created by onestravel on 2019-10-30
 */
public class ListVideoAdapter extends BaseRecyclerAdapter<String> {
    private static String TAG = ListVideoAdapter.class.getSimpleName();

    public ListVideoAdapter(Context context, List<String> list) {
        super(context, list, R.layout.item_list_layout);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    /**
     * @param viewGroup
     * @param view
     * @param viewType  default position
     * @return
     */
    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup viewGroup, View view, int viewType) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        view.setLayoutParams(lp);
        return super.onCreateVH(viewGroup, view, viewType);
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder holder, String data, int position) {
        if (data != null) {
            ListItem item = (ListItem) holder.itemView;
            item.setData(data);
        }
    }

    @Override
    public void onViewRecycled(@NonNull BaseRecyclerViewHolder holder) {
        super.onViewRecycled(holder);
        ListItem item = (ListItem) holder.itemView;
        item.stop();
        item.release();
    }
}
