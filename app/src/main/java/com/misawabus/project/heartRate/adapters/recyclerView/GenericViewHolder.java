package com.misawabus.project.heartRate.adapters.recyclerView;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class GenericViewHolder extends RecyclerView.ViewHolder {
    private final OnEntityClickListener onEntityClickListener;
    private final ViewsInRowHolder viewsInRowHolder;

    public GenericViewHolder(@NonNull View itemView,
                             OnEntityClickListener onEntityClickListener,
                             ViewsInRowHolder viewsInRowHolder) {
        super(itemView);
        itemView.setOnClickListener(this::onClickEntityRow);
        this.onEntityClickListener = onEntityClickListener;

        this.viewsInRowHolder = viewsInRowHolder.getInstance();
        this.viewsInRowHolder.fillViews(itemView);
    }

    public void onClickEntityRow(View view) {
        int id = view.getId();
        int position = getAdapterPosition();
        onEntityClickListener.onEntityClick(id, position);
    }

    public ViewsInRowHolder getViewsInRow() {
        return viewsInRowHolder;
    }

}

