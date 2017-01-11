package com.teamtreehouse.bitcointracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    AlphanumericDisplay alphanumericDisplay;
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
            .url("https://api.gdax.com/products/BTC-USD/ticker")
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            alphanumericDisplay = new AlphanumericDisplay("I2C1");
            alphanumericDisplay.setEnabled(true);
            runForever();
        } catch (IOException e) {
            Log.e("Error", e.getMessage());
        }
    }

    private void runForever() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    getBitcoinPrice();
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        Log.e("Error", e.getMessage());
                    }
                }
            }
        }.start();
    }

    private void getBitcoinPrice() {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String price = jsonObject.getString("price");
                    alphanumericDisplay.display(price);
                    Log.d("Price", price);
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", e.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            alphanumericDisplay.close();
        } catch (IOException e) {
            Log.e("Error", e.getMessage());
        }
    }
}
