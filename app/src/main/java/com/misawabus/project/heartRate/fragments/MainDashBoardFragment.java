package com.misawabus.project.heartRate.fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.misawabus.project.heartRate.DashBoardActivity;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.databinding.FragmentMainDashboardBinding;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;

public class MainDashBoardFragment extends Fragment{
    private static final String TAG = MainDashBoardFragment.class.getSimpleName();
    private FragmentMainDashboardBinding binding;
    private DashBoardViewModel dashBoardViewModel;
    private DeviceViewModel deviceViewModel;

    public MainDashBoardFragment(){

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_dashboard, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WindowInsetsControllerCompat windowInsetsController2 =
                WindowCompat.getInsetsController(getActivity().getWindow(), getActivity().getWindow().getDecorView());
        windowInsetsController2.setAppearanceLightStatusBars(true);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color_for_main_fragment, null));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat windowInsetsController =
                    WindowCompat.getInsetsController(getActivity().getWindow(), getView());
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
        }else {
            DashBoardActivity.hideWindowForAndroidVersionLessR(getActivity());
        }

        binding.bottomNavigationV2.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.page_1){
                getChildFragmentManager().beginTransaction().replace(R.id.navigationFragmentContainer, new TabsDailyActivityFragment())
                        .commit();
            }else if(item.getItemId()==R.id.page_2){
                getChildFragmentManager().beginTransaction().replace(R.id.navigationFragmentContainer, new RealTimeInfoFragment())
                        .commit();
            }else if(item.getItemId()==R.id.page_3){
                getChildFragmentManager().beginTransaction().replace(R.id.navigationFragmentContainer, new PersonalFieldsScrollingFragment())
                        .commit();
            }else if(item.getItemId()==R.id.page_4){
                getChildFragmentManager().beginTransaction().replace(R.id.navigationFragmentContainer, new SettingsFragment())
                        .commit();
            }

            return true;
        });



        ValueAnimator animation = ValueAnimator.ofFloat(1f, 0f);
        animation.setDuration(1000);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue = (float)updatedAnimation.getAnimatedValue();
                binding.statusSynchronizingBluetooth.setAlpha(animatedValue);
                binding.progressBar.setAlpha(animatedValue);
                if(animatedValue==0.0){
                    dashBoardViewModel.setIsFetching(View.INVISIBLE);
                    binding.statusSynchronizingBluetooth.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

        binding.imageView8.setImageResource(R.drawable.ic_baseline_link_24);
        binding.statusConnectionBluetooth.setText(deviceViewModel.getMacAddress());
        if(dashBoardViewModel.getIsFetching() == View.VISIBLE){
            binding.progressBar.setVisibility(dashBoardViewModel.getIsFetching());
            binding.statusSynchronizingBluetooth.setVisibility(dashBoardViewModel.getIsFetching());
        }


        dashBoardViewModel.getIsConnected().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean) return;
            binding.statusConnectionBluetooth.setText("Disconnected");
            binding.imageView8.setImageResource(R.drawable.ic_baseline_link_off_24);
            hideProgressBar(animation);

        });


        dashBoardViewModel.getIsEnableFeatures().observe(getViewLifecycleOwner(), isEnabled -> {

            if (isEnabled && dashBoardViewModel.getIsFetching()==View.INVISIBLE) {
                binding.statusSynchronizingBluetooth.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
                return;
            }
            if (isEnabled && dashBoardViewModel.getIsFetching()==View.VISIBLE && !MainDashBoardFragment.this.isDetached()) {
                Log.d("MainDashBoardFragment", "..............");
                dashBoardViewModel.setIsFetching(View.INVISIBLE);
                binding.statusSynchronizingBluetooth.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }else if(isEnabled && dashBoardViewModel.getIsFetching()==View.VISIBLE && MainDashBoardFragment.this.isVisible()) {
                hideProgressBar(animation);
            }else if(!isEnabled && dashBoardViewModel.getIsFetching()==View.INVISIBLE){
                binding.statusSynchronizingBluetooth.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        WindowInsetsControllerCompat windowInsetsController2 =
                WindowCompat.getInsetsController(getActivity().getWindow(), getActivity().getWindow().getDecorView());
        windowInsetsController2.setAppearanceLightStatusBars(true);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color_for_main_fragment, null));

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "passddddd onAttach: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "passddddd onStart: ");
    }

    private void hideProgressBar(ValueAnimator animation) {
        dashBoardViewModel.setIsFetching(View.INVISIBLE);
        animation.start();
    }




}
