package cn.onestravel.fivefiveplayer.demo;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by onestravel on 2020/3/16
 */
public class PagerLinearLayoutManager extends LinearLayoutManager {
    private PagerSnapHelper mPagerSnapHelper;
    private int mDrift;
    private OnPagerListener mOnViewPagerListener;
    private int firstVisibleItem;
    private int lastVisibleItem;
    private RecyclerView mRecyclerView;
    private int visibleCount;

    public PagerLinearLayoutManager(Context context) {
        this(context, LinearLayoutManager.VERTICAL, false);
    }

    public PagerLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        initView();
    }

    public PagerLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    /**
     * attach到window窗口时，该方法必须调用
     */
    @Override
    public void onAttachedToWindow(RecyclerView view) {
        mRecyclerView = view;
        super.onAttachedToWindow(view);
        mPagerSnapHelper = new PagerSnapHelper();
        mPagerSnapHelper.attachToRecyclerView(view);
        view.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener);
    }

    private void initView() {

    }

    public void setOnViewPagerListener(OnPagerListener mOnViewPagerListener) {
        this.mOnViewPagerListener = mOnViewPagerListener;
    }

    private RecyclerView.OnChildAttachStateChangeListener mChildAttachStateChangeListener =
            new RecyclerView.OnChildAttachStateChangeListener() {
                /**
                 * 第一次进入界面的监听，可以做初始化方面的操作
                 * @param view                      view
                 */
                @Override
                public void onChildViewAttachedToWindow(@NonNull View view) {
                    if (mOnViewPagerListener != null && getChildCount() == 1) {
                        mOnViewPagerListener.onInitComplete();
                        autoPlayVideo(mRecyclerView);
                    }
                }

                /**
                 * 页面销毁的时候调用该方法，可以做销毁方面的操作
                 * @param view                      view
                 */
                @Override
                public void onChildViewDetachedFromWindow(@NonNull View view) {
                    if (mDrift >= 0) {
                        if (mOnViewPagerListener != null) {
                            mOnViewPagerListener.onPageRelease(true, getPosition(view));
                        }
                    } else {
                        if (mOnViewPagerListener != null) {
                            mOnViewPagerListener.onPageRelease(false, getPosition(view));
                        }
                    }
                }
            };
    //哪里添加该listener监听事件，如下所示。这里注意需要在页面销毁的时候移除listener监听事件。


    /**
     * 销毁的时候调用该方法，需要移除监听事件
     */
    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        view.removeOnChildAttachStateChangeListener(mChildAttachStateChangeListener);
    }


    /**
     * 滑动状态的改变
     * 缓慢拖拽-> SCROLL_STATE_DRAGGING
     * 快速滚动-> SCROLL_STATE_SETTLING
     * 空闲状态-> SCROLL_STATE_IDLE
     *
     * @param state 状态
     */
    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
                View viewIdle = mPagerSnapHelper.findSnapView(this);
                int positionIdle = 0;
                if (viewIdle != null) {
                    positionIdle = getPosition(viewIdle);
                }
                autoPlayVideo(mRecyclerView);

                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                View viewDrag = mPagerSnapHelper.findSnapView(this);
                if (viewDrag != null) {
                    int positionDrag = getPosition(viewDrag);
                }
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                View viewSettling = mPagerSnapHelper.findSnapView(this);
                if (viewSettling != null) {
                    int positionSettling = getPosition(viewSettling);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 监听竖直方向的相对偏移量
     *
     * @param dy       y轴滚动值
     * @param recycler recycler
     * @param state    state滚动状态
     * @return int值
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0 || dy == 0) {
            return 0;
        }
        mDrift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    /**
     * 自动播放
     */
    private void autoPlayVideo(RecyclerView view) {
        firstVisibleItem = findFirstVisibleItemPosition();
        lastVisibleItem = findLastVisibleItemPosition();
        visibleCount = lastVisibleItem - firstVisibleItem;
        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();

        for (int i = 0; i < visibleCount; i++) {
            if (layoutManager != null && layoutManager.getChildAt(i) != null) {
                View homeGSYVideoPlayer = layoutManager.getChildAt(i);
                Rect rect = new Rect();
                homeGSYVideoPlayer.getLocalVisibleRect(rect);
                int videoheight = homeGSYVideoPlayer.getHeight();
                if (rect.top == 0 && rect.bottom == videoheight) {
                    if (mOnViewPagerListener != null) {
                        mOnViewPagerListener.onPageSelected(i, i == getItemCount() - 1);
                    }
                    return;
                }

            }
        }
        if (mOnViewPagerListener != null) {
            mOnViewPagerListener.onPageRelease( true,0);
        }
    }

//    /**
//     * 监听水平方向的相对偏移量
//     * @param dx                                x轴滚动值
//     * @param recycler                          recycler
//     * @param state                             state滚动状态
//     * @return                                  int值
//     */
//    @Override
//    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        if (getChildCount() == 0 || dx == 0) {
//            return 0;
//        }
//        this.mDrift = dx;
//        return super.scrollHorizontallyBy(dx, recycler, state);
//    }
}