package com.batterysaver.rambooster;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {

	private ViewPager pager = null;
	private MemoryBoosterAdapter pagerAdapter = null;
	TabLayout tabLayout;

	int count = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);

		/**
		 * Initialize ViewPager and adapte
		 */

		this.pager = (ViewPager) findViewById(R.id.pager);
		this.pagerAdapter = new MemoryBoosterAdapter(
				getSupportFragmentManager());
		this.pager.setAdapter(pagerAdapter);

		tabLayout = (TabLayout) findViewById(R.id.tablayoutpager);
		tabLayout.setupWithViewPager(pager);

		tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				pager.setCurrentItem(tab.getPosition());
				count = count + 1;
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
				pager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
				pager.setCurrentItem(tab.getPosition());
			}

		});

	}


	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
