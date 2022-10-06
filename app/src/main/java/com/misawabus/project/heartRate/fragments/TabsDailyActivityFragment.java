package com.misawabus.project.heartRate.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayoutMediator;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.adapters.tabLayout.TabStateAdapterV2;
import com.misawabus.project.heartRate.databinding.FragmentTabsDailyActivityBinding;


public class TabsDailyActivityFragment extends Fragment {
    private static final String TAG = TabsDailyActivityFragment.class.getSimpleName();
    private FragmentTabsDailyActivityBinding binding;
    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    public TabsDailyActivityFragment() {
        // Required empty public constructor
    }

    public static TabsDailyActivityFragment newInstance() {
        return new TabsDailyActivityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tabs_daily_activity, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureTabLayout();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void configureTabLayout() {
        for (int i = 0; i < 4; i++) {
            binding.tabDays.addTab(binding.tabDays.newTab());
        }



        final FragmentStateAdapter adapter = new TabStateAdapterV2(getActivity(), binding.tabDays.getTabCount());
        binding.viewPagerDays.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator =
                new TabLayoutMediator(binding.tabDays,
                        binding.viewPagerDays,
                        (tab, position) -> {
                            switch (position) {
                                case 0:
                                    tab.setText("Today");
                                    break;
                                case 1:
                                    tab.setText("1 day ago");
                                    break;
                                case 2:
                                    tab.setText("2 days ago");
                                    break;
                                case 3:
                                    tab.setText("Summary Activity");
                                    break;

                            }
                        });

        tabLayoutMediator.attach();


    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat windowInsetsController =
                    WindowCompat.getInsetsController(getActivity().getWindow(), getView());
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
        }
    }
}