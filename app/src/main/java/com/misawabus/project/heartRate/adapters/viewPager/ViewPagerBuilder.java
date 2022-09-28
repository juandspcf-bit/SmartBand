package com.misawabus.project.heartRate.adapters.viewPager;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewPagerBuilder {

    private final Context context;
    private final RecyclerView recyclerView;
    private final int layout_resource;
    private final OnEntityClickListener onEntityClickListener;
    private final int sizeEntities;

    FillViewsFieldsWithEntitiesValues fillViewsFieldsWithEntitiesValues;
    private final ViewsInRowHolder viewsInRowHolder;

    public ViewPagerBuilder(int sizeEntities,
                            Context context,
                            RecyclerView recyclerView,
                            int layout_resource,
                            OnEntityClickListener onEntityClickListener,
                            FillViewsFieldsWithEntitiesValues fillViewsFieldsWithEntitiesValues,
                            ViewsInRowHolder viewsInRowHolder) {
        this.sizeEntities = sizeEntities;

        this.context = context;
        this.recyclerView = recyclerView;
        this.layout_resource = layout_resource;
        this.onEntityClickListener = onEntityClickListener;
        this.fillViewsFieldsWithEntitiesValues = fillViewsFieldsWithEntitiesValues;
        this.viewsInRowHolder = viewsInRowHolder;
    }

    public void build(){

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ViewPagerAdapter recyclerViewAdapter = new ViewPagerAdapter(sizeEntities,
                layout_resource,
                this.context,
                onEntityClickListener,
                fillViewsFieldsWithEntitiesValues,
                viewsInRowHolder);
        recyclerView.setAdapter(recyclerViewAdapter);
    }



}
