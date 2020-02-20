package com.batterysaver.rambooster.battery;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.batterysaver.rambooster.R;
import android.content.Intent;
import android.view.animation.AlphaAnimation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.batterysaver.rambooster.FragActivityTask;
import com.batterysaver.rambooster.view.CircleImageView;
import com.batterysaver.rambooster.view.CircleLayout;
import com.batterysaver.rambooster.view.CircleLayout.OnCenterClickListener;
import com.batterysaver.rambooster.view.CircleLayout.OnItemClickListener;
import com.batterysaver.rambooster.view.CircleLayout.OnRotationFinishedListener;



public class BatteryFragment extends Fragment implements OnItemClickListener, OnRotationFinishedListener, OnCenterClickListener  {

    TextView selectedTextView;
    Intent intent;
    Animation anim;

    TextView textHealth;
    TextView textTechnology;
    TextView textTemp;
    TextView textVoltage;
    TextView textPlugged;
    TextView textLevel;
    TextView textStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_battery, container, false);

        CircleLayout circleMenu = (CircleLayout) v.findViewById(R.id.main_circle_layout);
        //circleMenu.setOnItemSelectedListener(this);
        circleMenu.setOnItemClickListener(this);
        circleMenu.setOnRotationFinishedListener(this);
        circleMenu.setOnCenterClickListener(this);

        // Request for Ads
        selectedTextView = (TextView) v.findViewById(R.id.main_selected_textView);
        selectedTextView.setText(((CircleImageView) circleMenu
                .getSelectedItem()).getName());
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        textHealth = (TextView) v.findViewById(R.id.textHealth);
        textLevel = (TextView) v.findViewById(R.id.textLevel);
        textTechnology = (TextView) v.findViewById(R.id.textTechnology);
        textTemp = (TextView) v.findViewById(R.id.textTemp);
        textVoltage = (TextView) v.findViewById(R.id.textVoltage);
        textStatus = (TextView) v.findViewById(R.id.textStatus);
        textPlugged = (TextView) v.findViewById(R.id.textPlugged);

        registerBatteryLevelReceiver();

        return v;
    }

    private void registerBatteryLevelReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(battery_receiver, filter);
    }

    private BroadcastReceiver battery_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPresent = intent.getBooleanExtra("present", false);
            String technology = intent.getStringExtra("technology");
            int plugged = intent.getIntExtra("plugged", -1);
            int scale = intent.getIntExtra("scale", -1);
            int health = intent.getIntExtra("health", 0);
            int status = intent.getIntExtra("status", 0);
            int rawlevel = intent.getIntExtra("level", -1);
            int voltage = intent.getIntExtra("voltage", 0);
            int temperature = intent.getIntExtra("temperature", 0);
            String tempString= String.format("%.0f°C", new Float(temperature / 10));
            int level = 0;

            Bundle bundle = intent.getExtras();

            Log.i("BatteryLevel", bundle.toString());

            if (isPresent) {
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }

                String Level = level+" %";
                String Technology = technology;
                String Plugged = getPlugTypeString(plugged);
                String Health = getHealthString(health);
                String Status = getStatusString(status);
                String Voltage = voltage+" mV";
                String Temperature =  tempString+"";

                textLevel.setText(Level);
                textTechnology.setText(Technology);
                textPlugged.setText(Plugged);
                textHealth.setText(Health);
                textStatus.setText(Status);
                textVoltage.setText(Voltage);
                textTemp.setText(Temperature);

            } else {
                textStatus.setText("Battery not present !!!");
            }
        }
    };


    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(battery_receiver);
        super.onDestroy();
    }

    private String getPlugTypeString(int plugged) {
        String plugType = "Unknown";

        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugType = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugType = "USB";
                break;
        }

        return plugType;
    }

    private String getHealthString(int health) {
        String healthString = "Unknown";

        switch (health) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthString = "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthString = "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthString = "Over Voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthString = "Over Heat";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthString = "Failure";
                break;
        }

        return healthString;
    }

    private String getStatusString(int status) {
        String statusString = "Unknown";

        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = "Charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = "Discharging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = "Full";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = "Not Charging";
                break;
        }

        return statusString;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //new TaskList(getActivity()).execute();
        //acm = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public void onItemClick(View view, String name) {
		/*selectedTextView.setText(name);
		selectedTextView.startAnimation(anim);*/
        //Toast.makeText(getApplicationContext(),getResources().getString(R.string.start_app) + " " + name,Toast.LENGTH_SHORT).show();
        switch (view.getId()) {
            case R.id.main_battery:
                // Handle facebook click
                intent = new Intent(getContext(), ActivitySaveBattery.class);
                startActivity(intent);
                break;
            case R.id.main_charger:
                // Handle google click
                intent = new Intent(getContext(), ActivityCharge.class);
                startActivity(intent);
                break;
            case R.id.main_graph:
                // Handle twitter click
//                intent = new Intent(getContext(), UpdateServices.class);
//                getActivity().startService(intent);
//                intent = new BatteryChart().execute(getActivity());
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                break;

            case R.id.main_taskiller:
                //Handle wordpress click
                intent = new Intent(getActivity(), FragActivityTask.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCenterClick() {
        //Toast.makeText(getApplicationContext(), R.string.center_click,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRotationFinished(View view, String name) {
        Animation animation = new RotateAnimation(0, 360, view.getWidth() / 2,
                view.getHeight() / 2);
        animation.setDuration(400);
        view.startAnimation(animation);
        selectedTextView.setText(name);
        selectedTextView.startAnimation(anim);
    }
}
