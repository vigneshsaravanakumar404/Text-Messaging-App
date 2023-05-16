package com.example.textmessagingapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    final String phoneNumber = "+15555215556";
    BroadcastReceiver br;
    Handler handler = new Handler();
    TextView textView;
    String history = "";

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

    public static String chatGPT(String text) throws Exception {
        String url = "https://api.openai.com/v1/completions";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + APIKey.API_KEY);

        JSONObject data = new JSONObject();
        data.put("model", "gpt-3.5-turbo");
        data.put("prompt", text);
        data.put("max_tokens", 4000);
        data.put("temperature", 1.0);

        con.setDoOutput(true);
        con.getOutputStream().write(data.toString().getBytes());

        String output = new BufferedReader(new InputStreamReader(con.getInputStream())).lines().reduce((a, b) -> a + b).get();

        return new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text");
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
                        Log.d("TAG", messageBody);
                    }
                }

                history += intent.getStringExtra("sms");

                // Respond after a few seconds
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SendMessageTask sendMessageTask = new SendMessageTask();
                        sendMessageTask.execute();
                    }
                }, 100);


            }
        };
        registerReceiver(br, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));


    }

    private class SendMessageTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // Perform your background task, such as sending the message
            SmsManager smsManager = SmsManager.getDefault();
            try {
                String response = chatGPT("Pretend you are in a text conversation and the conversation went like this '" + history + "' respond to the conversation appropriately in text message style");
                history += response;
                smsManager.sendTextMessage(phoneNumber, null, response, null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

}
// TODO:
// ChatGPT integration or hardcoded AI