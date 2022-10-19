package com.misawabus.project.heartRate.fragments.summaryFragments.spo2InfoFields;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.databinding.FragmentSpo2InfoFieldsBinding;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;


public class Spo2InfoFieldsFragment extends Fragment {
    private FragmentSpo2InfoFieldsBinding binding;

    private DashBoardViewModel dashBoardViewModel;

    public Spo2InfoFieldsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_spo2_info_fields,
                container,
                false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String value = dashBoardViewModel.getCurrentTitleSpo2InfoFields().getValue();
        if(value!=null){
            binding.titleTextView.setText(value);
        }
    }
}