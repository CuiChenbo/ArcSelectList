package com.ccb.arcselect.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ccb.arcselect.R;
import com.ccb.arcselect.utils.CenterItemUtils;
import com.ccb.arcselect.utils.TUtils;
import com.ccb.arcselect.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 横向滑动后自动选中居中的条目  类似 横向WheelView
 */
public class HorizontalSelectActivity extends AppCompatActivity {

private final int CHILDVIEWSIZE = 100;
    private RecyclerView recyclerView;
    private TextView tv;
    private MAdapter mAdapter;
    private int centerToLiftDistance; //RecyclerView款度的一半 ,也就是控件中间位置到左部的距离 ，
    private int childViewHalfCount = 0; //当前RecyclerView一半最多可以存在几个Item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_select_h);
        recyclerView = findViewById(R.id.rv);
        tv = findViewById(R.id.tv);
        init();

    }

    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                centerToLiftDistance = recyclerView.getWidth() / 2;

                int childViewHeight = UiUtils.dip2px(HorizontalSelectActivity.this, CHILDVIEWSIZE); //43是当前已知的 Item的高度
                childViewHalfCount = (recyclerView.getWidth() / childViewHeight + 1) / 2;
                initData();
                findView();

            }
        });
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToCenter(childViewHalfCount);
            }
        }, 100L);
    }

    private List<String> mDatas;

    private void initData() {
        if (mDatas == null) mDatas = new ArrayList<>();
        for (int i = 0; i < 55; i++) {
            mDatas.add("条目" + i);
        }
        for (int j = 0; j < childViewHalfCount; j++) { //头部的空布局
            mDatas.add(0, "");
        }
        for (int k = 0; k < childViewHalfCount; k++) {  //尾部的空布局
            mDatas.add("");
        }
    }

    private boolean isTouch = false;

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
//                    int fi = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
//                    int la = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    Log.i("ccb", "onScrollStateChanged:首个item: " + fi + "  末尾item:" + la);
                    if (isTouch) {
                        isTouch = false;
                        //获取最中间的Item View
                        int centerPositionDiffer = (la - fi) / 2;
                        int centerChildViewPosition = fi + centerPositionDiffer; //获取当前所有条目中中间的一个条目索引
                        centerViewItems.clear();
                        //遍历循环，获取到和中线相差最小的条目索引(精准查找最居中的条目)
                        if (centerChildViewPosition != 0){
                            for (int i = centerChildViewPosition -1 ; i < centerChildViewPosition+2; i++) {
                                View cView = recyclerView.getLayoutManager().findViewByPosition(i);
                                int viewLeft = cView.getLeft()+(cView.getWidth()/2);
                                centerViewItems.add(new CenterItemUtils.CenterViewItem(i ,Math.abs(centerToLiftDistance - viewLeft)));
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
    }

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
        int childVhalf = childView.getWidth() / 2;
        int childViewLeft = childView.getLeft();
        int viewCTop = centerToLiftDistance;
        int smoothDistance = childViewLeft - viewCTop + childVhalf;
        Log.i("ccb", "\n居中位置距离左部距离: " + viewCTop
                + "\n当前居中控件距离左部距离: " + childViewLeft
                + "\n当前居中控件的一半高度: " + childVhalf
                + "\n滑动后再次移动距离: " + smoothDistance);
        recyclerView.smoothScrollBy(smoothDistance, 0,null,500);
        mAdapter.setSelectPosition(position);

        tv.setText("当前选中:" + mDatas.get(position));
    }


    class MAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(HorizontalSelectActivity.this).inflate(R.layout.item_auto_select_h, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;

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
                    scrollToCenter(fp);
                    Toast.makeText(HorizontalSelectActivity.this, "点击" + mDatas.get(fp), Toast.LENGTH_SHORT).show();
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
