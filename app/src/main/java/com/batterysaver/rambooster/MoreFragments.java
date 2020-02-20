package com.batterysaver.rambooster;

import java.io.File;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.itangqi.waveloadingview.WaveLoadingView;

//import com.todddavies.components.progressbar.ProgressWheel;

public class MoreFragments extends Fragment {

	private TextView txtTotal, txtAvail, txtFree;
	private TextView txtExTotal, txtExAvail, txtExFree;
	//private ProgressWheel pw = null;
	//private ProgressWheel exPW = null;
	private LinearLayout layourExternal;
	WaveLoadingView mWaveLoadingView;
	WaveLoadingView mWaveLoadingView2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater
				.inflate(R.layout.memory_informations, container, false);

		//pw = (ProgressWheel) v.findViewById(R.id.pw_spinner_internal);
		//exPW = (ProgressWheel) v.findViewById(R.id.pw_spinner_external);
		mWaveLoadingView = (WaveLoadingView) v.findViewById(R.id.waveLoadingView);
		mWaveLoadingView2 = (WaveLoadingView) v.findViewById(R.id.waveLoadingView2);

		layourExternal = (LinearLayout) v.findViewById(R.id.layourExternal);

		txtTotal = (TextView) v.findViewById(R.id.txtTotalInternalMemory);
		txtAvail = (TextView) v.findViewById(R.id.txtAvailInternalMemory);
		txtFree = (TextView) v.findViewById(R.id.txtFreeInternalMemory);

		txtExTotal = (TextView) v.findViewById(R.id.txtTotalExternalMemory);
		txtExAvail = (TextView) v.findViewById(R.id.txtAvailExternalMemory);
		txtExFree = (TextView) v.findViewById(R.id.txtFreeExternalMemory);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		/**
		 * Get device memory info and display
		 */

		DeviceMemoryInfo info = new DeviceMemoryInfo(getActivity());

		String total = Formatter.formatFileSize(getActivity(),
				info.getTotalInternalMemorySize());
		String avail = Formatter.formatFileSize(getActivity(),
				info.getAvailableInternalMemorySize());

		txtTotal.setText("" + total);
		txtAvail.setText("" + avail);
		txtFree.setText("" + info.getFreeInternalMemorySize());

		float f = (float) info.getAvailableInternalMemorySize();
		float f1 = (float) info.getTotalInternalMemorySize();
		int i = (int) ((f / f1) * 100F);
		setInternalPercentage(i);

		String[] dir = info.getStorageDirectories();
		for (int j = 0; j < dir.length; j++) {
			if (!TextUtils.isEmpty(dir[j])) {
				File file = new File(dir[j]);
				if (file.exists() && file.length() > 0) {
					ShowExternalInfo(file);
				}
			}
		}
	}

	private void ShowExternalInfo(File file) {
		//layourExternal.setVisibility(View.VISIBLE);
		String exTotal = Formatter.formatFileSize(getActivity(),
				DeviceMemoryInfo.getTotalExternalMemorySize(file));
		String exAvail = Formatter.formatFileSize(getActivity(),
				DeviceMemoryInfo.getAvailableExternalMemorySize(file));

		txtExTotal.setText("" + exTotal);
		txtExAvail.setText("" + exAvail);
		txtExFree.setText(""
				+ DeviceMemoryInfo.getFreeExternalMemorySize(file));

		float exf = (float) DeviceMemoryInfo
				.getAvailableExternalMemorySize(file);
		float exf1 = (float) DeviceMemoryInfo.getTotalExternalMemorySize(file);
		int t = (int) ((exf / exf1) * 100F);
		setExternalPercentage(t);
	}

	public void setInternalPercentage(int info) {

		String s = (new StringBuilder()).append(info).append("%").toString();
		//pw.setText(s);
		s = s + " FREE";
		mWaveLoadingView.setCenterTitle(s);
		int j = (int) ((double) info * 3.6000000000000001D);
		mWaveLoadingView.setProgressValue(info);
		//pw.setProgress(j);

	}

	public void setExternalPercentage(int info) {

		String s = (new StringBuilder()).append(info).append("%").toString();
		//exPW.setText(s);
		s = s + " FREE";
		mWaveLoadingView2.setCenterTitle(s);
		int j = (int) ((double) info * 3.6000000000000001D);
		mWaveLoadingView2.setProgressValue(info);
		//exPW.setProgress(j);

	}

}
