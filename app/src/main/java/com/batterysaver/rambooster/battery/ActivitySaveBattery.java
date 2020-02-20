package com.batterysaver.rambooster.battery;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.batterysaver.rambooster.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;


/**
 * Created by ElMakkaoui on 05/11/2016.
 */


public class ActivitySaveBattery extends AppCompatActivity {

    int planestatus;
    ScheduledExecutorService scheduledExecutorService;
    private SeekBar seekBar;
    public Intent intent;
    TextView BatteryPercentageLoader;
    AudioManager myAudioManager;
    static final String TURN_ON = "Enable";
    static final String TURN_OFF = "Disable";
    RelativeLayout blue;
    RelativeLayout wifi;
    RelativeLayout gps;
    RelativeLayout plane;
    RelativeLayout data;
    RelativeLayout ring;
    ImageView blue1;
    ImageView wifi1;
    ImageView gps1;
    //ToggleButton plane1;
    ImageView plane1;
    ImageView data1;
    ImageView ring1;
    ImageView setting;
    ImageView aboutimage;
    WifiManager wifiManager;
    ToggleButton mod;
    int count ;
    Method dataConnSwitchmethod;
    Class telephonyManagerClass;
    Object ITelephonyStub;
    Class ITelephonyClass;
    ConnectivityManager conman;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savingbattery);

        blue = (RelativeLayout)findViewById(R.id.blue);
        wifi = (RelativeLayout)findViewById(R.id.wifi);
        wifi1 = (ImageView)findViewById(R.id.wifi1);
        blue1 = (ImageView)findViewById(R.id.blue1);
        ring1 = (ImageView)findViewById(R.id.ring1);
        gps = (RelativeLayout)findViewById(R.id.gps);
        data = (RelativeLayout)findViewById(R.id.data);
        ring = (RelativeLayout)findViewById(R.id.ring);
        plane = (RelativeLayout)findViewById(R.id.plane);
        gps1 = (ImageView)findViewById(R.id.gps1);
        plane1 = (ImageView)findViewById(R.id.plane1);
        data1 = (ImageView)findViewById(R.id.data1);
        ring1 = (ImageView)findViewById(R.id.ring1);
        //setting = (ImageView)findViewById(R.id.setting);
        //aboutimage = (ImageView)findViewById(R.id.aboutimage);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        conman = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        //wifiManager
        if(wifiManager.isWifiEnabled())
            {
                wifi1.setImageResource(R.drawable.ic_action_network_wif);
            }
            else
            {
                //wifiManager.setWifiEnabled(true);
                wifi1.setImageResource(R.drawable.ic_action_network_wifi);
            }

        //Bluetooth
        if(adapter.getState() == BluetoothAdapter.STATE_ON) {
            //adapter.disable();
            blue1.setImageResource(R.drawable.ic_action_bluetootha);
        } else if (adapter.getState() == BluetoothAdapter.STATE_OFF){
            //adapter.enable();
            blue1.setImageResource(R.drawable.ic_action_bluetooth);
        } else {
            //State.INTERMEDIATE_STATE;
        }

        //RingTone
        int modee = myAudioManager.getRingerMode();
        if (modee == AudioManager.RINGER_MODE_SILENT)
        { //Add item selected

            ring1.setImageResource(R.drawable.ic_action_volume_mued);
        }
        else if (modee == AudioManager.RINGER_MODE_NORMAL)
        { //Accept item selected

            ring1.setImageResource(R.drawable.ic_action_volume_o);
        }
        else if (modee == AudioManager.RINGER_MODE_VIBRATE) { //Upload item selected

            ring1.setImageResource(R.drawable.untitle_1);
        }

        //DataConnection
        if(isMobileDataEnable() == true)
        {
            //Toast.makeText(getApplicationContext(), "Disable", Toast.LENGTH_SHORT).show();
            data1.setImageResource(R.drawable.ic_action_import_exporta);
        }
        else
        {
            //Toast.makeText(getApplicationContext(), "Enable", Toast.LENGTH_SHORT).show();
            data1.setImageResource(R.drawable.ic_action_import_export);
        }

        mod = (ToggleButton)findViewById(R.id.mod1);

        mod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true)
                {
                    wifiManager.setWifiEnabled(false);
                    wifi1.setImageResource(R.drawable.ic_action_network_wifi);

                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    mBluetoothAdapter.disable();
                    blue1.setImageResource(R.drawable.ic_action_bluetooth);

                    toggleMobileDataConnection(false);
                    data1.setImageResource(R.drawable.ic_action_import_export);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(ActivitySaveBattery.this)) {
                            android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
                                    android.provider.Settings.System.SCREEN_BRIGHTNESS, 100);
                        }
                        else {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }else{
                        android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
                             android.provider.Settings.System.SCREEN_BRIGHTNESS, 50);
                    }

                    //android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
                      //      android.provider.Settings.System.SCREEN_BRIGHTNESS, 50);
                    seekBar.setProgress(50);

                    myAudioManager.setRingerMode(1);
                    ring1.setImageResource(R.drawable.untitle_1);


                }
                else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(ActivitySaveBattery.this)) {
                            android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
                                 android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);
                        }
                        else {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }else{
                        android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
                              android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);
                    }

                    seekBar.setProgress(255);
                    myAudioManager.setRingerMode(2);
                    ring1.setImageResource(R.drawable.ic_action_volume_o);
                }
            }
        });

        BatteryPercentageLoader = (TextView) findViewById(R.id.tv_percentage);
        getBatteryPercentage();

        blue.setOnClickListener(new View.OnClickListener() {
            //BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            @Override
            public void onClick(View v) {

                if(adapter != null) {
                    if(adapter.getState() == BluetoothAdapter.STATE_ON) {
                        adapter.disable();
                        blue1.setImageResource(R.drawable.ic_action_bluetooth);
                    } else if (adapter.getState() == BluetoothAdapter.STATE_OFF){
                        adapter.enable();
                        blue1.setImageResource(R.drawable.ic_action_bluetootha);
                    } else {
                        //State.INTERMEDIATE_STATE;
                    }
                }
            }
        });

        //Wifi
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(wifiManager.isWifiEnabled())
                {
                    wifiManager.setWifiEnabled(false);
                    wifi1.setImageResource(R.drawable.ic_action_network_wifi);
                }
                else
                {
                    wifiManager.setWifiEnabled(true);
                    wifi1.setImageResource(R.drawable.ic_action_network_wif);
                }
            }
        });

        //Gps
        gps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(isMobileDataEnable() == true)
                {
                    //Toast.makeText(getApplicationContext(), "Disable", Toast.LENGTH_SHORT).show();
                    toggleMobileDataConnection(false);
                    data1.setImageResource(R.drawable.ic_action_import_export);
                }
                else
                {
                    //Toast.makeText(getApplicationContext(), "Enable", Toast.LENGTH_SHORT).show();
                    toggleMobileDataConnection(true);
                    data1.setImageResource(R.drawable.ic_action_import_exporta);
                }
            }
        });

        //Profile
        ring.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int pos = myAudioManager.getRingerMode();
                if (pos == 2) { //Add item selected
                    myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    ring1.setImageResource(R.drawable.ic_action_volume_mued);
                } else if (pos == 0) { //Accept item selected
                    myAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    ring1.setImageResource(R.drawable.untitle_1);
                } else if (pos == 1) { //Upload item selected
                    myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    ring1.setImageResource(R.drawable.ic_action_volume_o);
                }
            }
        });

        //plane
        plane1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        //Brightness

        seekBar.setMax(255);
        float curBrightnessValue = 0;
        try {
            curBrightnessValue = android.provider.Settings.System.getInt(
                    getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        int screen_brightness = (int) curBrightnessValue;
        seekBar.setProgress(screen_brightness);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            public void onProgressChanged(SeekBar seekBar, int progresValue,
                                          boolean fromUser) {
                progress = progresValue;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do something here,
                // if you want to do anything at the start of
                // touching the seekbar
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                ContentResolver resolver = getContentResolver();
                Uri uri = android.provider.Settings.System
                        .getUriFor("screen_brightness");

                // android.provider.Settings.System.putInt(resolver, "screen_brightness",
                //       progress);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(ActivitySaveBattery.this)) {
                        android.provider.Settings.System.putInt(resolver, "screen_brightness", progress);
                    } else {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } else {
                    android.provider.Settings.System.putInt(resolver, "screen_brightness", progress);
                }

                resolver.notifyChange(uri, null);
            }
        });




    }



    private BroadcastReceiver batteryLevel = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            //context.unregisterReceiver(this);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            if (usbCharge == true)
            {
                //Toast.makeText(getApplicationContext(), "USB", Toast.LENGTH_LONG).show();
            }
            else if (acCharge == true) {
                //Toast.makeText(getApplicationContext(), "AC", Toast.LENGTH_LONG).show();
            }
            else
            {
                //Toast.makeText(getApplicationContext(), "Not Charged", Toast.LENGTH_LONG).show();
            }
            int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level= -1;
            if (currentLevel >= 0 && scale > 0) {
                level = (currentLevel * 100) / scale;
            }
            BatteryPercentageLoader.setText(level + "%");

        }
    };

    private void getBatteryPercentage() {

        IntentFilter batteryLevelFilter = new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevel, batteryLevelFilter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(batteryLevel);
        super.onDestroy();
    }

    public boolean isMobileDataEnable() {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {

            enforceCallingOrSelfPermission(android.Manifest.permission.MODIFY_PHONE_STATE, null);
            Class cmClass = Class.forName(cm.getClass().getName());
            @SuppressWarnings("unchecked")
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"

            mobileDataEnabled = (Boolean) method.invoke(cm);

        } catch (Exception e) {
            // Some problem accessible private API and do whatever error handling you want here
        }
        return mobileDataEnabled;
    }

    public boolean toggleMobileDataConnection(boolean ON)
    {
        try {
            //create instance of connectivity manager and get system connectivity service
            final ConnectivityManager conman = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

            enforceCallingOrSelfPermission(android.Manifest.permission.MODIFY_PHONE_STATE, null);
            //create instance of class and get name of connectivity manager system service class
            final Class conmanClass  = Class.forName(conman.getClass().getName());
            //create instance of field and get mService Declared field
            final Field iConnectivityManagerField= conmanClass.getDeclaredField("mService");
            //Attempt to set the value of the accessible flag to true
            iConnectivityManagerField.setAccessible(true);
            //create instance of object and get the value of field conman
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            //create instance of class and get the name of iConnectivityManager field
            final Class iConnectivityManagerClass=  Class.forName(iConnectivityManager.getClass().getName());
            //create instance of method and get declared method and type
            final Method setMobileDataEnabledMethod= iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled",Boolean.TYPE);
            //Attempt to set the value of the accessible flag to true
            setMobileDataEnabledMethod.setAccessible(true);
            //dynamically invoke the iConnectivityManager object according to your need (true/false)
            setMobileDataEnabledMethod.invoke(iConnectivityManager, ON);

        } catch (Exception e){
        }
        return true;
    }
}
