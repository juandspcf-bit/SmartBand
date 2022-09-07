package com.misawabus.project.heartRate.adapters.tabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.misawabus.project.heartRate.fragments.daysFragments.PastYesterdayFragment;
import com.misawabus.project.heartRate.fragments.SummaryActivityFragment;
import com.misawabus.project.heartRate.fragments.daysFragments.TodayFragment;
import com.misawabus.project.heartRate.fragments.daysFragments.YesterdayFragment;

public class TabStateAdapterV2 extends FragmentStateAdapter {
    int tabCount;

    public TabStateAdapterV2(FragmentActivity fa, int numberOfTabs) {
        super(fa);
        this.tabCount = numberOfTabs;
    }


    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TodayFragment();
            case 1:
                return new YesterdayFragment();
            case 2:
                return new PastYesterdayFragment();
            case 3:
                return new SummaryActivityFragment();
            default:
                return null;

        }

    }


    @Override
    public int getItemCount() {
        return tabCount;
    }

}
