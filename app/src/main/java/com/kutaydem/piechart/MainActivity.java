package com.kutaydem.piechart;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView colorG, colorR, colorB, responseTextView;
    PieChart pieChart;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorG = findViewById(R.id.colorG);
        colorR = findViewById(R.id.colorR);
        colorB = findViewById(R.id.colorB);
        pieChart = findViewById(R.id.piechart);
        sendButton = findViewById(R.id.sendButton);
        responseTextView = findViewById(R.id.responseTextView);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
                sendPostRequestAsync();
            }
        });

        setData();
    }

    private void setData() {
        Random random = new Random();
        int num1 = random.nextInt(100);
        int num2 = random.nextInt(100 - num1);
        int num3 = 100 - (num1 + num2);

        colorG.setText(Integer.toString(num1));
        colorR.setText(Integer.toString(num2));
        colorB.setText(Integer.toString(num3));

        pieChart.clearChart();

        pieChart.addPieSlice(
                new PieModel(
                        "Green",
                        Integer.parseInt(colorG.getText().toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "Red",
                        Integer.parseInt(colorR.getText().toString()),
                        Color.parseColor("#EF5350")));
        pieChart.addPieSlice(
                new PieModel(
                        "Blue",
                        Integer.parseInt(colorB.getText().toString()),
                        Color.parseColor("#29B6F6")));

        pieChart.startAnimation();
    }

    private void sendPostRequestAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendPostRequest();
                } catch (IOException e) {
                    Log.e(TAG, "Error sending POST request", e);
                }
            }
        }).start();
    }

    private void sendPostRequest() throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"color_g\":\"" + colorG.getText() + "\", \"color_r\":\"" + colorR.getText() + "\", \"color_b\":\"" + colorB.getText() + "\"}");
        Request request = new Request.Builder()
                .url("http://thingsboard.cloud/api/v1/YOUR_API_KEY/telemetry")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the error
                Log.e(TAG, "Error sending POST request", e); responseTextView.post(new Runnable() { @Override public void run() { responseTextView.setText("Error sending POST request"); } }); }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle the response
                    Log.d(TAG, "POST request successful: " + response.body().string());
                    responseTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            responseTextView.setText("POST request successful");
                        }
                    });
                } else {
                    // Handle the error
                    Log.e(TAG, "POST request not successful: " + response.code());
                    responseTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            responseTextView.setText("POST request not successful");
                        }
                    });
                }
            }
        });
    }
}