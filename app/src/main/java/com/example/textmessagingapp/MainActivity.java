package com.example.textmessagingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Broadcast Listener for SMS
        BroadcastReceiver broadcastReceiver = new smsReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcastReceiver, intentFilter);

    }

    public static class smsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();
            Log.d("SMS", "SMS Received");
        }
    }
}

// TODO:
// Create Interface
// Send messages
// Receive messages
// AI
// Icon
// App Name
// Save Messages
// Different Chats for each number