package com.example.timerapp.Adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timerapp.Database.Timer;
import com.example.timerapp.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class DetailAdaptor extends RecyclerView.Adapter<DetailAdaptor.MyViewHolder> {
    private List<Timer> mDataset;

    public DetailAdaptor(List<Timer> myDataset) {
        mDataset = myDataset;
    }

    public DetailAdaptor() {}

    public void setDataset(List<Timer> dataset){
        mDataset = dataset;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timeLbl;
        public TextView dateLbl;
        public TextView startLbl;
        public TextView stopLbl;

        public MyViewHolder(View v) {
            super(v);
            timeLbl = itemView.findViewById(R.id.timeLbl);
            dateLbl = itemView.findViewById(R.id.dateLbl);
            startLbl = itemView.findViewById(R.id.startLbl);
            stopLbl = itemView.findViewById(R.id.stopLbl);
        }
    }

    @NonNull
    @Override
    public DetailAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.timeritem, parent, false);

        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DetailAdaptor.MyViewHolder holder, int position) {
        if(mDataset == null)
            return;

        int hours = mDataset.get(position).duration / 60;
        int minutes = mDataset.get(position).duration % 60;

        String strHours = hours > 9 ? String.valueOf(hours) : "0" + hours;
        String strMinutes = minutes > 9 ? String.valueOf(minutes) : "0" + minutes;

        holder.timeLbl.setText(strHours + ":" + strMinutes);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String startDate = dateFormat.format(mDataset.get(position).startTime);
        String endTime = timeFormat.format(mDataset.get(position).endTime);

        String startTime = timeFormat.format(mDataset.get(position).startTime);

        //holder.dateLbl.setText(startDate);

        holder.startLbl.setText(startTime);
        holder.stopLbl.setText(endTime);
    }

    @Override
    public int getItemCount() {
        if(mDataset == null)
            return 0;
        return mDataset.size();
    }

    public void remove(int index){
        mDataset.remove(index);
        notifyDataSetChanged();
    }

    public Timer getItem(int index){
        return mDataset.get(index);
    }


}
