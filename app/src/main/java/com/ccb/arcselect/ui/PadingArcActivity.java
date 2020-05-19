package com.ccb.arcselect.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ccb.arcselect.R;
import com.ccb.arcselect.utils.TUtils;
import com.ccb.arcselect.utils.UiUtils;


/**
 * 使用Padding实现弧形列表
 */
public class PadingArcActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private int recyclerViewHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rv);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerViewHeight = recyclerView.getHeight();
                setData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

    }

    private void setData() {
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new VH(LayoutInflater.from(PadingArcActivity.this).inflate( R.layout.item_pading,parent,false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                VH vh = (VH) holder;
                RelativeLayout mv = (RelativeLayout) vh.itemView;
                mv.setPadding(calculateTranslate(mv.getTop() , recyclerViewHeight) , 0 ,0 ,0 );
                vh.tv.setText("你好"+position+"索引");
                final int fp = position;
                vh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TUtils.show(PadingArcActivity.this , "你好"+fp+"索引");
                    }
                });
            }

            @Override
            public int getItemCount() {
                return 100;
            }

            class VH extends RecyclerView.ViewHolder{

                public TextView tv;
                public VH(@NonNull View itemView) {
                    super(itemView);
                    tv = itemView.findViewById(R.id.tv);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    int pad =  calculateTranslate(recyclerView.getChildAt(i).getTop() , recyclerViewHeight);
                    recyclerView.getChildAt(i).setPadding( pad, 0 ,0 ,0 );
                }
            }
        });
    }

    private int calculateTranslate(int top, int h) {
        int result = 0;
        h = h - UiUtils.dip2px(this , 60); //减去当前控件的高度，（60是已知当前Item的高度）
        int hh = h/2;
        result = Math.abs(hh - top);
        result = hh - result;
        return result/2;
    }


//    @Deprecated
//    public void changeGroupFlag(Object obj) throws Exception {
//        Field[] f = obj.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredFields(); // 获得成员映射数组
//        for (Field tem : f) {
//            if (tem.getName().equals("mGroupFlags")) {
//                tem.setAccessible(true);
//                Integer mGroupFlags = (Integer) tem.get(obj);
//                int newGroupFlags = mGroupFlags & 0xfffff8;
//                tem.set(obj, newGroupFlags);
//            }
//        }
//    }
}
