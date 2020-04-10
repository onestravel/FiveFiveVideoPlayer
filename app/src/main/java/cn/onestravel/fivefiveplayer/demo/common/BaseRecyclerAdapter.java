package cn.onestravel.fivefiveplayer.demo.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.util.List;

import cn.onestravel.fivefiveplayer.demo.R;
import cn.onestravel.fivefiveplayer.utils.LogHelper;

/**
 * Created by onestravel on 2019-10-30
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.BaseRecyclerViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    protected Context context;
    private List<T> data;
    private int layoutResId;
    protected RecyclerItemClickListener<T> itemClickListener;
    protected RecyclerItemLongClickListener<T> itemLongClickListener;

    public BaseRecyclerAdapter(Context context, List<T> list, int layoutResId) {
        this.context = context;
        this.data = list;
        this.layoutResId = layoutResId;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(context).inflate(layoutResId, parent, false);//这种方法不会使布局自适应
            if (itemClickListener != null) {
                view.setOnClickListener(this);
            }
            if (itemLongClickListener != null) {
                view.setOnLongClickListener(this);
            }
            return onCreateVH(parent, view, viewType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onClick(View v) {
        try {
            int position = (Integer) v.getTag();
            if (itemClickListener != null && position < getItemCount()) {
                itemClickListener.onItemClick(position, getData().get(position), v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        try {
            if (position < getItemCount()) {
                holder.itemView.setTag(position);
                if (position < data.size()) {
                    T bean = data.get(position);
                    onBindVH(holder, bean, position);
                }
            }
        } catch (Exception e) {
            LogHelper.INSTANCE.e("BaseRecyclerAdapter", "adapter=" + this, e);
        }

    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public BaseRecyclerViewHolder onCreateVH(ViewGroup viewGroup, View view, int viewType) {
        return new BaseRecyclerViewHolder(view);
    }

    public abstract void onBindVH(BaseRecyclerViewHolder holder, T data, int position);

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    @Override
    public boolean onLongClick(View v) {
        try {
            int position = (Integer) v.getTag();
            if (itemLongClickListener != null && position < getItemCount()) {
                return itemLongClickListener.onItemLongClick(position, getData().get(position), v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 设置item点击事件
     *
     * @param itemClickListener
     */
    public void setOnRecyclerItemClickListener(RecyclerItemClickListener<T> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * 设置item长按事件
     *
     * @param itemLongClickListener
     */
    public void setOnRecyclerItemLongClickListener(RecyclerItemLongClickListener<T> itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }


    public interface RecyclerItemClickListener<T> {
        /**
         * @param position 被点击的条目position
         * @param view     被点击的item中的子控件
         */
        void onItemClick(int position, T data, View view);
    }

    public interface RecyclerItemLongClickListener<T> {
        /**
         * @param position 被长按的条目position
         * @param view     被长按的item中的子控件
         */
        boolean onItemLongClick(int position, T data, View view);
    }

    public static class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
        public BaseRecyclerViewHolder(View itemView) {
            super(itemView);
        }

        public View findViewById(@IdRes int id) {
            return itemView.findViewById(id);
        }

        public void setText(@IdRes int id, String text) {
            text = text == null ? "" : text;
            View view = itemView.findViewById(id);
            if (view != null && view instanceof TextView) {
                ((TextView) view).setText(text);
            }
        }


        public void setText(@IdRes int id, @StringRes int resId) {
            View view = itemView.findViewById(id);
            if (view != null && view instanceof TextView) {
                ((TextView) view).setText(resId);
            }
        }

        public void setTextSize(@IdRes int id, int size) {
            View view = itemView.findViewById(id);
            if (view != null && view instanceof TextView) {
                ((TextView) view).setTextSize(size);
            }
        }

        public void setTextColor(@IdRes int id, @ColorInt int color) {
            View view = itemView.findViewById(id);
            if (view != null && view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            }
        }

        public void setTextColor(@IdRes int id, ColorStateList color) {
            View view = itemView.findViewById(id);
            if (view != null && view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            }
        }

        public void setImageRes(@IdRes int id, @DrawableRes int resId) {
            View view = itemView.findViewById(id);
            if (view != null && view instanceof ImageView) {
                ((ImageView) view).setImageResource(resId);
            }
        }

        public void setImageDrawable(@IdRes int id, Drawable drawable) {
            if (drawable == null) {
                return;
            }
            View view = itemView.findViewById(id);
            if (view != null && view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(drawable);
            }
        }

        public void setImageUrl(@IdRes int id, String url) {
            url = url == null ? "" : url;
            View view = itemView.findViewById(id);
            if (view != null && view instanceof ImageView) {
                loadImageUrl(url, view);
            }
        }

        public void setImageUrl(@IdRes int id, String url, int width, int height) {
            url = url == null ? "" : url;
            View view = itemView.findViewById(id);
            if (view != null && view instanceof ImageView) {
                if (width < 300 || height < 300) {
                    width = width * 100;
                    height = height * 100;
                }
                loadImageUrl(url, view, width, height);
            }
        }

        public void setImageBitmap(@IdRes int id, Bitmap bitmap) {
            if (bitmap == null) {
                return;
            }
            View view = itemView.findViewById(id);
            if (view != null && view instanceof ImageView) {
                ((ImageView) view).setImageBitmap(bitmap);
            }
        }

        public void setImageColorFilter(@IdRes int id, ColorFilter colorFilter) {
            View view = itemView.findViewById(id);
            if (view != null && view instanceof ImageView) {
                ((ImageView) view).setColorFilter(colorFilter);
            }
        }

        public void loadImageUrl(String url, View view) {
            Object glideUrl = null;
                glideUrl = url;
            GlideApp.with(view)
                    .load(glideUrl)
                    .fitCenter()
                    .thumbnail(0.1f)
                    .into((ImageView) view);
        }

        private void loadImageUrl(String url, View view, int width, int height) {
            Object glideUrl = null;
                glideUrl = url;
            GlideApp.with(view)
                    .load(glideUrl)
                    .fitCenter()
                    .skipMemoryCache(false)
                    .override(width, height)
                    .thumbnail(0.1f)
                    .into((ImageView) view);
        }

        public void setBackgroundColor(@IdRes int id, @ColorInt int color) {
            View view = itemView.findViewById(id);
            if (view != null) {
                view.setBackgroundColor(color);
            }
        }

        public void setBackgroundResource(@IdRes int id, @DrawableRes int resId) {
            View view = itemView.findViewById(id);
            if (view != null) {
                view.setBackgroundResource(resId);
            }
        }


        public void setChecked(@IdRes int id, boolean checked) {
            View view = itemView.findViewById(id);
            if (view != null && view instanceof CompoundButton) {
                ((CompoundButton) view).setChecked(checked);
            }
        }

        public void setSelected(@IdRes int id, boolean selected) {
            View view = itemView.findViewById(id);
            if (view != null && view instanceof CompoundButton) {
                ((CompoundButton) view).setSelected(selected);
            }
        }

        public void setVisibility(@IdRes int id, int visibility) {
            View view = itemView.findViewById(id);
            if (view != null) {
                view.setVisibility(visibility);
            }
        }

        public void setEnable(@IdRes int id, boolean enable) {
            View view = itemView.findViewById(id);
            if (view != null) {
                view.setEnabled(enable);
            }
        }

        public void setClickable(@IdRes int id, boolean clickable) {
            View view = itemView.findViewById(id);
            if (view != null) {
                view.setClickable(clickable);
            }
        }
    }
}