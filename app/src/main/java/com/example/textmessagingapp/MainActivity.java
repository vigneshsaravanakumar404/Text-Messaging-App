package com.example.textmessagingapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    // Variables

    final int REQUESTCODE = 1;
    final String phoneNumber = "+15555215556";

    //    final String[] openings = {"Hello", "Hey there", "What's up", "Hi", "Hey"};
//    final String[] closings = {"Bye", "Goodbye", "See you later", "Talk to you later", "Later"};
    final String API_KEY = "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";
    BroadcastReceiver br;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Permissions
        // request permissions


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS}, REQUESTCODE);
        } else {
            registerSmsReceiver();
        }

        getWindow().getDecorView().setSystemUiVisibility(0);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUESTCODE) {

            getPackageManager();
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                registerSmsReceiver();
            } else {

                Toast.makeText(this, "SMS permissions have been denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerSmsReceiver() {


        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    for (Object pdu : pdus) {
                        SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
                        String messageBody = message.getDisplayMessageBody();
                        String response = messageBody;
                    }
                }


                /*
                 *
                 *
                 * Code for message response
                 *
                 */


                // Respond after a few seconds
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNumber, null, "Hey low", null, null);
                    }
                }, 1000);


            }
        };
        registerReceiver(br, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));


    }
}

// TODO:
// ChatGPT integration
// Icon
// App Name
