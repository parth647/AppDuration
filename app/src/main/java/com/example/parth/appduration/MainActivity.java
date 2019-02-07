package com.example.parth.appduration;

import android.app.AppOpsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static String FACEBOOK_COUNTER="Facebook Counter";
    public static String WHATSAPP_COUNTER="Whatsapp Counter";
    private TextView facebook_view;
    private TextView whatsapp_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences( "App Duration",MODE_PRIVATE);
        if(!checkUsageStatusAllowedOrNot()){
            Intent usageAcessIntent=new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            usageAcessIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(usageAcessIntent);
            if(checkUsageStatusAllowedOrNot()){
                startService(new Intent(MainActivity.this,BackgroundService.class));
            }
            else{
                Toast.makeText(getApplicationContext(),"please give access",Toast.LENGTH_SHORT).show();
            }

        }
        else{
            startService(new Intent(MainActivity.this,BackgroundService.class));
        }
        facebook_view=findViewById(R.id.facebook);
        whatsapp_view=findViewById(R.id.whatsapp);
        TimerTask updateView = new TimerTask() {
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long facebook = sharedPreferences.getLong(FACEBOOK_COUNTER, 0);
                        long second = (facebook / 1000) % 60;
                        long minute = (facebook / (1000 * 60)) % 60;
                        long hour = (facebook / (1000 * 3600));
                        String facebook_val = hour + "h" + minute + "m" + second + "s";
                        facebook_view.setText(facebook_val);
                        long whatsapp = sharedPreferences.getLong(WHATSAPP_COUNTER, 0);
                        second = (whatsapp / 1000) % 60;
                        minute = (whatsapp / (1000 * 60)) % 60;
                        hour = (whatsapp / (1000 * 3600));
                        String whatsapp_val = hour + "h" + minute + "m" + second + "s";
                        whatsapp_view.setText(whatsapp_val);
                    }


                });
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(updateView,0,1000);
    }
    public boolean checkUsageStatusAllowedOrNot(){
        try{
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(),0);
            AppOpsManager appOpsManager = (AppOpsManager)getSystemService(APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,applicationInfo.uid,applicationInfo.packageName);
            return(mode==AppOpsManager.MODE_ALLOWED);

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Cannot find usage status ",Toast.LENGTH_SHORT).show();
            return false;

        }


    }

    @Override
    protected void onDestroy() {
        if(checkUsageStatusAllowedOrNot()){
            startService(new Intent(MainActivity.this,BackgroundService.class));

        }
        super.onDestroy();
    }
}
