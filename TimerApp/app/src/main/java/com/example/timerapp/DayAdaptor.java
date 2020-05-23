package com.example.timerapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class DayAdaptor extends RecyclerView.Adapter<DayAdaptor.MyViewHolder> {
    private List<GroupedTiming> mDataset;
    final private ListItemClickListener mOnClickListener;

    public DayAdaptor(ListItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public DayAdaptor(List<GroupedTiming> dataset, ListItemClickListener mOnClickListener) {
        mDataset = dataset;
        this.mOnClickListener = mOnClickListener;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public void setDataset(List<GroupedTiming> dataset){
        mDataset = dataset;
        notifyDataSetChanged();
    }

    public GroupedTiming getDataset(int index){
        if(index < 0 || index >= mDataset.size())
            return null;

        return mDataset.get(index);
    }

    @NonNull
    @Override
    public DayAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.dayitem, parent, false);
        return new DayAdaptor.MyViewHolder(view, mOnClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DayAdaptor.MyViewHolder holder, int position) {
        if(mDataset == null)
            return;

        int hours = mDataset.get(position).totalDuration / 60;
        int minutes = mDataset.get(position).totalDuration % 60;

        String strHours = hours > 9 ? String.valueOf(hours) : "0" + String.valueOf(hours);
        String strMinutes = minutes > 9 ? String.valueOf(minutes) : "0" + String.valueOf(minutes);

        holder.timeLbl.setText(strHours + ":" + strMinutes);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd/MM/yyyy");

        String date = dateFormat.format(mDataset.get(position).date);
        holder.dateLbl.setText(date);
    }

    @Override
    public int getItemCount() {
        if(mDataset == null)
            return 0;
        return mDataset.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView timeLbl;
        public TextView dateLbl;
        private ListItemClickListener mOnClickListener;

        public MyViewHolder(View v, ListItemClickListener listener) {
            super(v);
            timeLbl = itemView.findViewById(R.id.timeLbl);
            dateLbl = itemView.findViewById(R.id.dateLbl);

            itemView.setOnClickListener(this);
            mOnClickListener = listener;
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

}
