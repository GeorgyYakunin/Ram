package com.batterysaver.rambooster.battery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import com.batterysaver.rambooster.R;

public class ActivityCharge extends Activity {

    Animation anim;
    TextView BatteryPercentageLoader;
    TextView txt;
    ImageView image,image1,image2,image3;
    ChromeHelpPopup chromeHelpPopup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.charging_battery);

        image = (ImageView)findViewById(R.id.imageView3);
        image1 = (ImageView)findViewById(R.id.imageView4);
        image2 = (ImageView)findViewById(R.id.imageView5);
        image3 = (ImageView)findViewById(R.id.imageView2);
        //image3.startAnimation(anim);
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        BatteryPercentageLoader = (TextView) findViewById(R.id.tv_percentage);
        txt = (TextView)findViewById(R.id.textView1);
        getBattery();


        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String str = "Use unfluctuated Power Supply to Charge Efficiently and Fastly.";

                if (chromeHelpPopup != null) {
                    chromeHelpPopup.dismiss();
                }

                chromeHelpPopup = new ChromeHelpPopup(ActivityCharge.this, str);
                chromeHelpPopup.show(v);
            }
        });

        image1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String str = "Remove Power Supply When Fully Charged.";

                if (chromeHelpPopup != null) {
                    chromeHelpPopup.dismiss();
                }

                chromeHelpPopup = new ChromeHelpPopup(ActivityCharge.this, str);
                chromeHelpPopup.show(v);

            }
        });

        image2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String str = "Use Trikle Charge To Keep Electrons Flowing Inside The Lithium Battery,to Make Up For Self-Discharge And Get Longer Battery Life.";

                if (chromeHelpPopup != null) {
                    chromeHelpPopup.dismiss();
                }

                chromeHelpPopup = new ChromeHelpPopup(ActivityCharge.this, str);
                chromeHelpPopup.show(v);

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

            int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level= -1;
            if (currentLevel >= 0 && scale > 0) {
                level = (currentLevel * 100) / scale;
            }
            BatteryPercentageLoader.setText(level + "%");

            if (usbCharge == true)
            {
                txt.setText("USB");
                image3.startAnimation(anim);
                image2.callOnClick();
            }
            else if (acCharge == true) {
                txt.setText("AC");
                image3.startAnimation(anim);
                image2.callOnClick();
            }
            else
            {
                txt.setText("Unconnected");
                image3.startAnimation(anim);
                if(level>=90){
                    image1.callOnClick();
                }else {
                    if(level<=50){
                        image.callOnClick();
                    }
                }
                //Toast.makeText(getApplicationContext(), "Not Charged", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void getBattery() {

        IntentFilter batteryLevelFilter = new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevel, batteryLevelFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(batteryLevel);
        super.onDestroy();
    }
}