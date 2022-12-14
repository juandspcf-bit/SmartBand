package com.misawabus.project.heartRate.adapters;

import com.inuker.bluetooth.library.search.SearchResult;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.adapters.recyclerView.OnViewClickedCallback;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;


/**
 * Created by timaimee on 2016/7/25.
 */
public class BleScanViewAdapter extends RecyclerView.Adapter<BleScanViewAdapter.NormalTextViewHolder> {
    private final LayoutInflater mLayoutInflater;
    List<SearchResult> itemData;
    OnViewClickedCallback mBleCallback;

    public BleScanViewAdapter(Context context, List<SearchResult> data) {
        this.itemData = data;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NormalTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_main, parent, false));
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        holder.mBleRssi.setText(itemData.get(position).getName() + "-" + itemData.get(position).getAddress() + "-" + itemData.get(position).rssi);

    }


    @Override
    public int getItemCount() {
        return itemData == null ? 0 : itemData.size();
    }


    public void setBleItemOnclick(OnViewClickedCallback bleCallback) {
        this.mBleCallback = bleCallback;
    }


    public class NormalTextViewHolder extends RecyclerView.ViewHolder {

        TextView mBleRssi;


        NormalTextViewHolder(View view) {
            super(view);
            mBleRssi = view.findViewById(R.id.tv);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBleCallback.OnViewClicked(getAdapterPosition());
                    Log.d("SummaryViewHolder", "onClick--> position = " + getAdapterPosition());
                }
            });
        }
    }
}