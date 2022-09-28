package com.misawabus.project.heartRate.adapters.viewPager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewPagerAdapter extends RecyclerView.Adapter<GenericViewHolder> {
    private final OnEntityClickListener onEntityClickListener;
    private final int layout_resource;
    private final FillViewsFieldsWithEntitiesValues fillViewsFieldsWithEntitiesValues;
    private final int size ;
    private final Context context;
    private final ViewsInRowHolder viewsInRowHolder;


    public ViewPagerAdapter(int size,
                            int layout_resource,
                            Context context,
                            OnEntityClickListener onEntityClickListener,
                            FillViewsFieldsWithEntitiesValues fillViewsFieldsWithEntitiesValues,
                            ViewsInRowHolder viewsInRowHolder) {
        this.size = size;
        this.onEntityClickListener = onEntityClickListener;
        this.layout_resource = layout_resource;
        this.fillViewsFieldsWithEntitiesValues = fillViewsFieldsWithEntitiesValues;
        this.context = context;
        this.viewsInRowHolder = viewsInRowHolder;
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout_resource, parent, false);

        return new GenericViewHolder(view,
                onEntityClickListener,
                viewsInRowHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        fillViewsFieldsWithEntitiesValues.fill(position, holder.getViewsInRow());
    }

    @Override
    public int getItemCount() {
        return size;
    }

}


