package com.ccb.arcselect.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccb.arcselect.R;
import com.ccb.arcselect.utils.CenterItemUtils;
import com.ccb.arcselect.utils.TUtils;
import com.ccb.arcselect.utils.UiUtils;
import com.ccb.arcselect.view.MatrixTranslateLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 矩阵实现弧形列表  &&   滑动后自动选中居中的条目
 * 未使用精确计算居中
 */
public class ArcSelectActivity extends AppCompatActivity {


    private ImageView iv;
    private RecyclerView recyclerView;
    private MAdapter mAdapter;
    private int centerToTopDistance; //RecyclerView高度的一半 ,也就是控件中间位置到顶部的距离 ，
    private int childViewHalfCount = 0; //当前RecyclerView一半最多可以存在几个Item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_arc);
        recyclerView = findViewById(R.id.rv);
        iv = findViewById(R.id.iv);
        init();

    }

    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                centerToTopDistance = recyclerView.getHeight() / 2;

                int childViewHeight = UiUtils.dip2px(ArcSelectActivity.this, 68); //68是当前已知的 Item的高度
                childViewHalfCount = (recyclerView.getHeight() / childViewHeight + 1) / 2;
                initData();
                findView();

            }
        });
    }

    private List<String> mDatas;

    private void initData() {
        if (mDatas == null) mDatas = new ArrayList<>();
        for (int i = 0; i < 55; i++) {
            mDatas.add("CAR_Item" + i);
        }
        for (int j = 0; j < childViewHalfCount; j++) { //头部的空布局
            mDatas.add(0, null);
        }
        for (int k = 0; k < childViewHalfCount; k++) {  //尾部的空布局
            mDatas.add(null);
        }


    }

    private boolean isTouch = false; //用户主动触摸后的标记
    private List<CenterItemUtils.CenterViewItem> centerViewItems = new ArrayList<>();

    private void findView() {
        mAdapter = new MAdapter();
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int fi = linearLayoutManager.findFirstVisibleItemPosition();
                    int la = linearLayoutManager.findLastVisibleItemPosition();
                    Log.i("ccb", "onScrollStateChanged:首个item: " + fi + "  末尾item:" + la +"   是否需要移动："+isTouch);
                    if (isTouch) {
                        isTouch = false;
                        //粗略获取最中间的Item View
                        int centerPositionDiffer = (la - fi) / 2;
                        int centerChildViewPosition = fi + centerPositionDiffer;

                        centerViewItems.clear();
                        //遍历循环，获取到和中线相差最小的条目索引(精准查找最居中的条目)
                        if (centerChildViewPosition != 0){
                            for (int i = centerChildViewPosition -1 ; i < centerChildViewPosition+2; i++) {
                                View cView = recyclerView.getLayoutManager().findViewByPosition(i);
                                int viewTop = cView.getTop()+(cView.getHeight()/2);
                                centerViewItems.add(new CenterItemUtils.CenterViewItem(i ,Math.abs(centerToTopDistance - viewTop)));
                            }

                            CenterItemUtils.CenterViewItem centerViewItem = CenterItemUtils.getMinDifferItem(centerViewItems);
                            centerChildViewPosition = centerViewItem.position;
                        }
                        scrollToCenter(centerChildViewPosition);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    recyclerView.getChildAt(i).invalidate();
                }
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isTouch = true;
                return false;
            }
        });
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToCenter(childViewHalfCount);
            }
        }, 100L);

    }

    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    /**
     * 移动指定索引到中心处 ， 只可以移动可见区域的内容
     * @param position
     */
    private void scrollToCenter(int position){
        position = position < childViewHalfCount ? childViewHalfCount : position;
        position = position < mAdapter.getItemCount() - childViewHalfCount -1 ? position : mAdapter.getItemCount() - childViewHalfCount -1;

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        View childView = linearLayoutManager.findViewByPosition(position);
        Log.i("ccb", "滑动后中间View的索引: " + position);
        //把当前View移动到居中位置
        if (childView == null) return;
        int childVhalf = childView.getHeight() / 2;
        int childViewTop = childView.getTop();
        int viewCTop = centerToTopDistance;
        int smoothDistance = childViewTop - viewCTop + childVhalf;
        Log.i("ccb", "\n居中位置距离顶部距离: " + viewCTop
                + "\n当前居中控件距离顶部距离: " + childViewTop
                + "\n当前居中控件的一半高度: " + childVhalf
                + "\n滑动后再次移动距离: " + smoothDistance);
        recyclerView.smoothScrollBy(0, smoothDistance,decelerateInterpolator);
        mAdapter.setSelectPosition(position);
        TUtils.show(ArcSelectActivity.this , "滑动后选中:" + mDatas.get(position));
    }

    /**
     * 移动指定索引
     * @param position
     */
    private void smoothToPosition(int position){
        position = position < childViewHalfCount ? childViewHalfCount : position;
        position = position < mAdapter.getItemCount() - childViewHalfCount -1 ? position : mAdapter.getItemCount() - childViewHalfCount -1;

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        linearLayoutManager.scrollToPosition(position);
    }


    class MAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(ArcSelectActivity.this).inflate(R.layout.item_arc, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;
            ((MatrixTranslateLayout) vh.itemView).setParentHeight(recyclerView.getHeight());

            if (selectPosition == position) {
                vh.tv.setTextColor(getResources().getColor(R.color.textSelect));
            } else {
                vh.tv.setTextColor(getResources().getColor(R.color.white));
            }
            if (TextUtils.isEmpty(mDatas.get(position))){
                vh.itemView.setVisibility(View.INVISIBLE);
            }else {
                vh.itemView.setVisibility(View.VISIBLE);
                vh.tv.setText(mDatas.get(position));
            }
            final int fp = position;
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TUtils.show(ArcSelectActivity.this , "点击" + mDatas.get(fp));
                    scrollToCenter(fp);
                }
            });
        }

        private int selectPosition = -1;

        public void setSelectPosition(int cposition) {
            selectPosition = cposition;
//            notifyItemChanged(cposition);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class VH extends RecyclerView.ViewHolder {

            public TextView tv;

            public VH(@NonNull View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv);
            }
        }
    }
}
