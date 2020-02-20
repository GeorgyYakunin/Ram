package com.batterysaver.rambooster;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.batterysaver.rambooster.battery.BatteryFragment;

public class MemoryBoosterAdapter extends FragmentStatePagerAdapter {

	public MemoryBoosterAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {// for three tab page
		Fragment frag = null;
		if (index == 0) {
			frag = new BoostFragment();
		}

		if (index == 1) {
			frag = new BatteryFragment();
		}

		if (index == 2) {
			frag = new MoreFragments();
		}
		return frag;
	}

	@Override
	public CharSequence getPageTitle(int position) {

		switch (position){
			case 0:
				return "Boost";
			case 1:
				return "Battery";
			case 2:
				return "Memory";
			default:
				return null;
		}
	}

	@Override
	public int getCount() {
		return 3;
	}

}
