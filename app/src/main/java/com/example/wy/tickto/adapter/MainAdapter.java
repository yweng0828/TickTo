package com.example.wy.tickto.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wy.tickto.R;
import com.example.wy.tickto.entity.ThingInfo;

import java.util.List;

/**
 * Created by 56989 on 2018/4/22.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainContentViewHolder> {
    /**
     * Item是否被选中监听
     */
    private OnCheckedChangeListener mOnCheckedChangeListener;
    /**
     * Item点击监听
     */
    private OnItemClickListener mItemOnClickListener;
    /**
     * 数据
     */
    private List<ThingInfo> thinginfos = null;

    /**
     * Item拖拽滑动帮助
     */
    private ItemTouchHelper itemTouchHelper;

    public MainAdapter() {
    }

    public MainAdapter(List<ThingInfo> infos) {
        this.thinginfos = infos;
    }

    public void notifyDataSetChanged(List<ThingInfo> Infos) {
        this.thinginfos = Infos;
        super.notifyDataSetChanged();
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemOnClickListener = onItemClickListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener mOnCheckedChangeListener) {
        this.mOnCheckedChangeListener = mOnCheckedChangeListener;
    }

    @Override
    public MainAdapter.MainContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recview, parent, false));
    }

    @Override
    public void onBindViewHolder(MainContentViewHolder holder, int position) {
        holder.setData();
    }

    @Override
    public int getItemCount() {
        return thinginfos == null ? 0 : thinginfos.size();
    }

    public ThingInfo getData(int position) {
        return thinginfos.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnCheckedChangeListener {
        void onItemCheckedChange(CompoundButton view, int position, boolean checked);
    }

    class MainContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {
        /**
         * 事件和重要性
         */
        private TextView thing, important;
        /**
         * 触摸就可以拖拽
         */
        private ImageView mIvTouch;
        /**
         * 是否选中
         */
        private CheckBox mCbCheck;

        public MainContentViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            thing = (TextView) itemView.findViewById(R.id.whatthings);
            important = (TextView) itemView.findViewById(R.id.howimortant);
            mIvTouch = (ImageView) itemView.findViewById(R.id.imageView_rv);
            mCbCheck = (CheckBox) itemView.findViewById(R.id.checkbox_rv);
            mCbCheck.setOnClickListener(this);
            mIvTouch.setOnTouchListener(this);
        }

        /**
         * 给这个Item设置数据
         */
        public void setData() {
            ThingInfo Info = getData(getAdapterPosition());
            thing.setText(Info.getWhatthing());
            int rank = Info.getHowimportant();
            String impr = null;
            if(rank==1){
                impr = new String("重要");
                important.setTextColor(Color.RED);
            }else if(rank==2){
                impr = new String("中等");
                important.setTextColor(Color.BLUE);
            }else{
                impr = new String("悠闲");
                important.setTextColor(Color.GREEN);
            }
            important.setText(impr);
            mCbCheck.setChecked(Info.isDone());
        }

        @Override
        public void onClick(View view) {
            if (view == itemView && itemTouchHelper != null) {
                mItemOnClickListener.onItemClick(view, getAdapterPosition());
            } else if (view == mCbCheck && mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onItemCheckedChange(mCbCheck, getAdapterPosition(), mCbCheck.isChecked());
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (view == mIvTouch)
                itemTouchHelper.startDrag(this);
            return false;
        }

    }
}
