package com.batterysaver.rambooster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Build;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.batterysaver.rambooster.process.AppShort;
import com.batterysaver.rambooster.process.PackagesInfo;
import com.batterysaver.rambooster.process.ProcessListAdapter;
import com.batterysaver.rambooster.process.TaskInfo;

public class FragActivityTask extends FragmentActivity implements OnDismissCallback,
		OnClickListener, TaskDialog.DialogDismissListener, TaskKillDialog.DialogTaskKillListener, Animation.AnimationListener {

	private ListView swipeListView;
	private ProcessListAdapter adapter = null;
	public static int mem = 0;
	private ProgressDialog pd = null;
	private RelativeLayout btnKill;
	private ActivityManager acm = null;
	private long beforeMemory;
	private long aftermemory;
	private int processesKilled;
	private int ramFreed;

	Animation animClickBoost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_list_of_tasks);

		//setTitle(R.layout.task_list_layout);

		swipeListView = (ListView) findViewById(R.id.list);
		//btnKill = (Button) v.findViewById(R.id.btnKill);

		animClickBoost = AnimationUtils.loadAnimation(this,R.anim.retation);
		// set animation listener
		animClickBoost.setAnimationListener(this);

		btnKill = (RelativeLayout) findViewById(R.id.btnKill);
		btnKill.setOnClickListener(this);

		new TaskList(this).execute();
		acm = (ActivityManager) getSystemService(
				Context.ACTIVITY_SERVICE);

	}


	public void DisplayList(Context context) {
		
		ActivityManager am = (ActivityManager) context
				.getSystemService(this.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list = am
				.getRunningAppProcesses(); // get all running task
		
		this.mem = 0;
		ArrayList<TaskInfo> arrList = new ArrayList<TaskInfo>();
		PackagesInfo pInfo = new PackagesInfo(context);
		Iterator<ActivityManager.RunningAppProcessInfo> iterator = list
				.iterator();
		do {// iterate all running task
			if (!iterator.hasNext()) {
				break;
			}
			ActivityManager.RunningAppProcessInfo runproInfo = (ActivityManager.RunningAppProcessInfo) iterator
					.next();
			String s = runproInfo.processName;
			if (!s.contains(this.getPackageName())) {

				if (runproInfo.importance == 130
						|| runproInfo.importance == 300
						|| runproInfo.importance == 100
						|| runproInfo.importance == 400) {

					TaskInfo info = new TaskInfo(context, runproInfo);
					info.getAppInfo(pInfo);
					if (!isImportant(s)) {
						info.setChceked(true);
					}

					if (info.isGoodProcess()) {
						int j = runproInfo.pid;
						int i[] = new int[1];
						i[0] = j;
						Debug.MemoryInfo memInfo[] = am.getProcessMemoryInfo(i);
						int k = memInfo.length;
						for (int l = 0; l < memInfo.length; l++) {
							Debug.MemoryInfo mInfo = memInfo[l];
							int m = mInfo.getTotalPss() * 1024;
							info.setMem(m);
							int jl = mInfo.getTotalPss() * 1024;
							int kl = mem;
							if (jl > kl)
								mem = mInfo.getTotalPss() * 1024;
						}
						if (mem > 0)
							arrList.add(info);
					}
				}
			}
		} while (true);

		AppShort shortmem = new AppShort();
		Collections.sort(arrList, shortmem);// shorting list based on total memory used each process
		adapter = new ProcessListAdapter(context, arrList);
		swipeListView.setFocusableInTouchMode(true);
		ScaleInAnimationAdapter scaleAnimAdapter = new ScaleInAnimationAdapter(new SwipeDismissAdapter(adapter, this));
		scaleAnimAdapter.setAbsListView(swipeListView);
		swipeListView.setAdapter(scaleAnimAdapter);
		
		/*
		 * SwipeDismissListViewTouchListener listener = new
		 * SwipeDismissListViewTouchListener(swipeListView, mCallBack);
		 * swipeListView.setOnScrollListener(listener.makeScrollListener());
		 * swipeListView.setOnTouchListener(listener);
		 */
	}

	private boolean isImportant(String pname) {
		
		if (pname.equals("android") || pname.equals("android.process.acore")
				|| pname.equals("system") || pname.equals("com.android.phone")
				|| pname.equals("com.android.systemui")
				|| pname.equals("com.android.launcher")) {
			return true;
		} else {
			return false;
		}
	}

	// listview swipe listener event, when swipe left or right
	// clean specific process from the list 
	@Override
	public void onDismiss(AbsListView list, int[] reversepos) {
		
		for (int pos : reversepos) {
			TaskInfo info = (TaskInfo) adapter.getItem(pos);
			if (isImportant(info.getPackageName())) {

				TaskKillDialog dailog = new TaskKillDialog();
				dailog.setPos(pos);
				dailog.setIcon(info.getIcon());
				dailog.setAppName(info.getTitle());
				dailog.setDialgTaskKillListener(FragActivityTask.this);
				dailog.setCancelable(false);
				dailog.show(getSupportFragmentManager(), "killfrag");
				adapter.notifyDataSetChanged();
			}else {
				acm.killBackgroundProcesses(info.getPackageName());
				adapter.remove(adapter.getItem(pos));
				adapter.notifyDataSetChanged();
			}
		}
	}

	public class TaskList extends AsyncTask<Void, Void, ArrayList<TaskInfo>> {

		private Activity context;

		public TaskList(Activity context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(context);
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected ArrayList<TaskInfo> doInBackground(Void... arg0) {

			ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

			Iterator<ActivityManager.RunningServiceInfo> iteratorTask = null;
			Iterator<ActivityManager.RunningAppProcessInfo> iterator = null;

			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
				List<ActivityManager.RunningServiceInfo> listTask = am.getRunningServices(100);
				iteratorTask = listTask.iterator();
			} else {
				List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
				iterator = list.iterator();
			}

			//iteratorTask

			mem = 0;
			ArrayList<TaskInfo> arrList = new ArrayList<TaskInfo>();
			PackagesInfo pInfo = new PackagesInfo(context);

			String pkNameexist= "vide";

			do {

				ActivityManager.RunningAppProcessInfo runproInfo = null;
				ActivityManager.RunningServiceInfo runproTaskInfo = null;
				String s = "";

				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
					if (!iteratorTask.hasNext()) {
						break;
					}
					runproTaskInfo = (ActivityManager.RunningServiceInfo) iteratorTask.next();
					s = runproTaskInfo.service.getPackageName();

				}else{

					if (!iterator.hasNext()) {
						break;
					}
					runproInfo = (ActivityManager.RunningAppProcessInfo) iterator.next();
					s = runproInfo.processName;
				}

				if (!s.contains(getPackageName())) {

					if(runproInfo!=null){

					if (runproInfo.importance == 130
							|| runproInfo.importance == 300
							|| runproInfo.importance == 100
							|| runproInfo.importance == 400) {

						TaskInfo info = new TaskInfo(context, runproInfo);
						info.getAppInfo(pInfo);
						if (!isImportant(s)) {
							info.setChceked(true);
						}

						if (info.isGoodProcess()) {
							int j = runproInfo.pid;
							int i[] = new int[1];
							i[0] = j;
							Debug.MemoryInfo memInfo[] = am
									.getProcessMemoryInfo(i);
							int k = memInfo.length;
							for (int l = 0; l < memInfo.length; l++) {
								Debug.MemoryInfo mInfo = memInfo[l];
								int m = mInfo.getTotalPss() * 1024;
								info.setMem(m);
								int jl = mInfo.getTotalPss() * 1024;
								int kl = mem;
								if (jl > kl)
									mem = mInfo.getTotalPss() * 1024;
							}
							if (mem > 0)
								arrList.add(info);
						}
					}
					}else{

						if(runproTaskInfo!=null){
							TaskInfo info = new TaskInfo(context, runproTaskInfo);
							Log.e("getApplicationInfo"," info : "+runproTaskInfo.pid +" +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
							info.getTaskInfo(pInfo);
							if (!isImportant(s)) {
								info.setChceked(true);
							}

							if (info.isGoodProcess()) {
								int j = runproTaskInfo.pid;
								Log.e("isGoodProcess"," info : "+ j +" +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");

								int i[] = new int[1];
								i[0] = j;
								Debug.MemoryInfo memInfo[] = am
										.getProcessMemoryInfo(i);
								int k = memInfo.length;
								for (int l = 0; l < memInfo.length; l++) {
									Debug.MemoryInfo mInfo = memInfo[l];
									int m = mInfo.getTotalPss() * 1024;
									info.setMem(m);
									int jl = mInfo.getTotalPss() * 1024;
									int kl = mem;
									if (jl > kl)
										mem = mInfo.getTotalPss() * 1024;
									Log.e("memInfo"," info : "+ mem +" +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
								}
								if (mem > 0){

									Log.e("memInfo 2 ", " info : " + mem + " +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");

									if(pkNameexist.equals("vide")){
										arrList.add(info);
										pkNameexist = info.getTitle();
									}else{

										if(info.getTitle().equals(pkNameexist)){
											// nothing here
										}else{
											arrList.add(info);
											pkNameexist = info.getTitle();
										}
									}
								}
							}
						}
					}
				}

			} while (true);

			ArrayList<TaskInfo> arrList2 = new ArrayList<TaskInfo>();
			String existTitle;

			for(int i=0; i<arrList.size(); i++){

				if(arrList2.size()>0){

					boolean bool = true;
					for(int j=0; j<arrList2.size(); j++){

						if(arrList.get(i).getTitle().equals(arrList2.get(j).getTitle())){
							bool = false;
							break;
						}
					}

					if(bool){
						arrList2.add(arrList.get(i));
					}

				}else{
					arrList2.add(arrList.get(i));
				}
			}

			return arrList2;
		}

		@Override
		protected void onPostExecute(ArrayList<TaskInfo> arrList) {
			
			AppShort shortmem = new AppShort();
			Collections.sort(arrList, shortmem);
			adapter = new ProcessListAdapter(context, arrList);
			swipeListView.setFocusableInTouchMode(true);
			ScaleInAnimationAdapter scaleAnimAdapter = new ScaleInAnimationAdapter(
					new SwipeDismissAdapter(adapter, FragActivityTask.this));
			scaleAnimAdapter.setAbsListView(swipeListView);
			swipeListView.setAdapter(scaleAnimAdapter);

			if (pd != null) {
				pd.dismiss();
				pd = null;
			}

		}
	}

	@Override
	public void onClick(View v) {

		btnKill.startAnimation(animClickBoost);
		ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
		acm.getMemoryInfo(memInfo);
		beforeMemory = memInfo.availMem;
		new DialogTask(this).execute();
	}

	//com.extra.boostpower

	private class DialogTask extends AsyncTask<Void, Void, Void> {

		private Activity context;

		public DialogTask(Activity context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(context);
			pd.setMessage("please wait...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				int j = 0;
				TaskInfo info = null;
				while (adapter.getCount() > j) {
					info = adapter.getItem(j);
					if (info.isChceked()) {
						Log.d("TaskList: ", info.getPackageName());
						acm.killBackgroundProcesses(info.getPackageName());
					}
					j++;
				}
				processesKilled = j;
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (pd != null) {
				pd.dismiss();
			}
			ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
			acm.getMemoryInfo(memInfo);
			aftermemory = memInfo.availMem;
			if (aftermemory > beforeMemory) {
				ramFreed = (int) (aftermemory - beforeMemory);
			} else {
				ramFreed = 0;
			}
			TaskDialog dialog = new TaskDialog();
			dialog.setDialogListener(FragActivityTask.this);
			dialog.setProcessKilled(String.valueOf(processesKilled));
			dialog.setMemoryCleaned(BoostFragment.formatMemSize(ramFreed, 0));
			dialog.setCancelable(false);
			dialog.show(getSupportFragmentManager(),"");
		}
	}

	@Override
	public void onDialogDismiss() {
		new TaskList(this).execute();
	}

	@Override
	public void onTaskKIll(int pos) {
		
		TaskInfo info = (TaskInfo) adapter.getItem(pos);
		acm.killBackgroundProcesses(info.getPackageName());
		adapter.remove(adapter.getItem(pos));
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (animation == animClickBoost) {
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

}
