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
            getInfo(value);
        }

        binding.buttonBackFromSop2FieldsFrag.setOnClickListener(this::backSummaryFragment);
    }

    private void getInfo(String value) {
        if ("Apnea".equals(value)) {
            String definition = getString(R.string.apnea_definition);
            String info1Title = getString(R.string.apnea_info_1_title);
            String info1Content = getString(R.string.apnea_info_1_content);
            String reference = getString(R.string.apnea_reference);

            binding.infoTextView.setText(definition);
            binding.info1TitleTextView.setText(info1Title);
            binding.info1ContentTextView.setText(info1Content);
            binding.info2TitleTextView.setText("");
            binding.info2ContentTextView.setText("");
            binding.referenceTextView.setText(reference);
        } else if ("Blood Oxygen".equals(value)) {
            String definition = getString(R.string.blood_oxygen_definition);
            String info1Title = getString(R.string.blood_oxygen_info_1_title);
            String info1Content = getString(R.string.blood_oxygen_info_1_content);
            String reference = getString(R.string.blood_oxygen_reference);

            binding.infoTextView.setText(definition);
            binding.info1TitleTextView.setText(info1Title);
            binding.info1ContentTextView.setText(info1Content);
            binding.info2TitleTextView.setText("");
            binding.info2ContentTextView.setText("");
            binding.referenceTextView.setText(reference);

        }else if ("Respiration Rate".equals(value)) {
            String definition = getString(R.string.respiration_rate_definition);
            String info1Title = getString(R.string.respiration_rate_info_1_title);
            String info1Content = getString(R.string.respiration_rate_info_1_content);
            String info2Title = getString(R.string.respiration_rate_info_2_title);
            String info2Content = getString(R.string.respiration_rate_info_2_content);
            String reference = getString(R.string.respiration_rate_reference);

            binding.infoTextView.setText(definition);
            binding.info1TitleTextView.setText(info1Title);
            binding.info1ContentTextView.setText(info1Content);
            binding.info2TitleTextView.setText(info2Title);
            binding.info2ContentTextView.setText(info2Content);
            binding.referenceTextView.setText(reference);

        } else if ("Hypoxia".equals(value)) {
        String definition = getString(R.string.hypoxia_definition);
        String reference = getString(R.string.hypoxia_reference);

        binding.infoTextView.setText(definition);
        binding.info1TitleTextView.setText("");
        binding.info1ContentTextView.setText("");
        binding.info2TitleTextView.setText("");
        binding.info2ContentTextView.setText("");
        binding.referenceTextView.setText(reference);

    }else if ("Cardiac Load".equals(value)) {
            String definition = getString(R.string.cardiac_output_definition);
            String info1Title = getString(R.string.cardiac_output_info_1_title);
            String info1Content = getString(R.string.cardiac_output_info_1_content);
            String info2Title = "";
            String info2Content = "";
            String reference = getString(R.string.cardiac_output_reference);

            binding.infoTextView.setText(definition);
            binding.info1TitleTextView.setText(info1Title);
            binding.info1ContentTextView.setText(info1Content);
            binding.info2TitleTextView.setText(info2Title);
            binding.info2ContentTextView.setText(info2Content);
            binding.referenceTextView.setText(reference);

        }
    }

    protected void backSummaryFragment(View view) {
        getParentFragmentManager().popBackStack();
    }
}