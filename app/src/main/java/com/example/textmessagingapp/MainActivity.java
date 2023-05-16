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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {


    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    final String phoneNumber = "+15555215554";
    final String API_KEY = APIKey.API_KEY;
    BroadcastReceiver br;
    Handler handler = new Handler();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().getDecorView().setSystemUiVisibility(0);
        getWindow().getDecorView().setSystemUiVisibility(0);
        getWindow().setFlags(1024, 1024);


        textView = findViewById(R.id.textView);
        if (checkSmsPermissions()) {
            registerSmsReceiver();
        } else {
            requestSmsPermissions();
        }

    }

    private boolean checkSmsPermissions() {
        int receiveSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int sendSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        return receiveSmsPermission == PackageManager.PERMISSION_GRANTED &&
                sendSmsPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermissions() {
        // Request the RECEIVE_SMS and SEND_SMS permissions
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS},
                SMS_PERMISSION_REQUEST_CODE);
        Log.d("tag", "the request was sent");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            // Check if all permissions are granted
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                registerSmsReceiver();
            } else {
                textView.setText("Permissions not granted, restart the app and grant permissions");
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
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