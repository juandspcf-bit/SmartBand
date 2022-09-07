package com.misawabus.project.heartRate.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.inuker.bluetooth.library.search.SearchResult;

import java.util.List;

public class ScanConnectionViewModel extends ViewModel {
    private final MutableLiveData<List<SearchResult>> recyclerViewData = new MutableLiveData<>();

    public MutableLiveData<List<SearchResult>> getRecyclerViewData() {
        return recyclerViewData;
    }
    public void setRecyclerViewData(List<SearchResult> recyclerViewData){
        this.recyclerViewData.setValue(recyclerViewData);
    }


}
