package com.example.fuelcal;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.fuel_calculator_tab,
            R.string.Asda_price_tab,
            R.string.Tesco_price_tab,
            R.string.Sainsburys_price_tab};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment.

       // return PlaceholderFragment.newInstance(position + 1);
        position += 1;
        if(position == 1){
            return new FuelCalFragment();
        }
        if(position == 2){
            return new FuelPriceFragment();
        }
        if(position == 3){
            return new TescoPriceFragment();
        }
        if(position == 4){
            return new SainsburysPriceFragment();
        }
        return new Fragment();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show # total pages.
        return 4;
    }
}