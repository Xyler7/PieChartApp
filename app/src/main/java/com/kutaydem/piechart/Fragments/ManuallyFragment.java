package com.kutaydem.piechart.Fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kutaydem.piechart.MainActivity;
import com.kutaydem.piechart.R;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ManuallyFragment extends Fragment {

    private TextView colorG, colorR, colorB;
    private int currentLayout = R.layout.pie_chart_manually;
    private PieChart pieChart;
    private TextView textView;
    int redValue, greenValue, blueValue;

    class ColorData {
        List<ColorValue> color_r;
        List<ColorValue> color_g;
        List<ColorValue> color_b;
    }

    class ColorValue {
        int value;
        long ts;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (currentLayout != 0) {
            return inflater.inflate(currentLayout, container, false);
        }
        return null;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            currentLayout = R.layout.pie_chart_manually_land;
        } else {
            currentLayout = R.layout.pie_chart_manually;
        }
        // Recreate the fragment to apply the new layout
        getParentFragmentManager().beginTransaction().detach(this).commitNowAllowingStateLoss();
        getParentFragmentManager().beginTransaction().attach(this).commitNowAllowingStateLoss();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
            colorG = view.findViewById(R.id.colorG);
            colorR = view.findViewById(R.id.colorR);
            colorB = view.findViewById(R.id.colorB);
            pieChart = view.findViewById(R.id.piechart);
            textView = view.findViewById(R.id.responseTextView);
            Button btnGet = view.findViewById(R.id.btnGet);
            Button btnBack = view.findViewById(R.id.btnBack);
            Button btnPost = view.findViewById(R.id.btnPost);
            SeekBar rSeekBar = view.findViewById(R.id.redSeekBar);
            SeekBar gSeekBar = view.findViewById(R.id.greenSeekBar);
            SeekBar bSeekBar = view.findViewById(R.id.blueSeekBar);

            rSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    redValue = progress;
                    System.out.println("redValue: " + redValue);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            gSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    greenValue = progress;
                    System.out.println("greenValue: " + greenValue);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            bSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    blueValue = progress;
                    System.out.println("blueValue " + blueValue);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            });

            btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setData();
                    sendPostRequest();
                }
            });
            // Button click listener
            btnGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new OkHttpRequestTask().execute();
                }
            });
        }

    private void calculateSeekBar() {
        int total = redValue + greenValue + blueValue;

        if (total != 100) {
            redValue = Math.round(redValue * 100 / total);
            greenValue = Math.round(greenValue * 100 / total);
            blueValue = Math.round(blueValue * 100 / total);
        }
        int newTotal = redValue + greenValue + blueValue;
        while (newTotal != 100) {
            if (newTotal < 100) {
                redValue += 1;
                newTotal = redValue + greenValue + blueValue;
            } else if (newTotal > 100) {
                redValue -= 1;
                newTotal = redValue + greenValue + blueValue;
            } else break;
        }
    }

    private void setData() {

        calculateSeekBar();

        colorR.setText(Integer.toString(redValue));
        colorG.setText(Integer.toString(greenValue));
        colorB.setText(Integer.toString(blueValue));

        pieChart.clearChart();

        pieChart.addPieSlice(new PieModel("Green", Integer.parseInt(colorG.getText().toString()), Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(new PieModel("Red", Integer.parseInt(colorR.getText().toString()), Color.parseColor("#EF5350")));
        pieChart.addPieSlice(new PieModel("Blue", Integer.parseInt(colorB.getText().toString()), Color.parseColor("#29B6F6")));

        pieChart.startAnimation();
    }

    private void sendPostRequest() {
        new Thread(new Runnable() { // Execute network request on a separate thread
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                String jsonBody = "{\"color_r\":\"" + colorR.getText().toString() + "\", \"color_g\":\"" + colorG.getText().toString() + "\", \"color_b\":\"" + colorB.getText() + "\"}";
                RequestBody body = RequestBody.create(mediaType, jsonBody);

                Request request = new Request.Builder().url("http://iot.mesebilisim.com:8080/api/v1/DEVICE_ID/telemetry").post(body).build();

                client = new OkHttpClient();
                Call call = client.newCall(request);

                // Handle response in the main thread using Handler
                Handler handler = new Handler(Looper.getMainLooper());
                call.enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "Error sending POST request", e);
                                textView.setText("Error sending POST request");
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String responseBody = response.body().string();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "POST request successful: " + responseBody);
                                    textView.setText("POST request successful");
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(TAG, "POST request not successful: " + response.code());
                                    textView.setText("POST request not successful");
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    private class OkHttpRequestTask extends AsyncTask<Void, Void, String> {

        private static final String TAG = "OkHttpRequestTask";

        @Override
        protected String doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient.Builder().build();

                Request request = new Request.Builder().url("http://iot.mesebilisim.com:8080/api/plugins/telemetry/DEVICE/YOUR_DEVICE_ID/values/timeseries?keys=color_r,color_g,color_b")
                        .header("Content-Type", "application/json")
                        .header("X-Authorization", "Bearer YOUR_API_KEY")
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.e(TAG, "Error making HTTP request", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                String formattedOutput = ""; // Initialize once
                try {
                    // Parse JSON data using Gson
                    Gson gson = new Gson();
                    ColorData colorData = gson.fromJson(result, ColorData.class);

                    // Format and display the data
                    formattedOutput += "color_r: " + colorData.color_r.get(0).value + ", " + colorData.color_r.get(0).ts + "\n";
                    formattedOutput += "color_g: " + colorData.color_g.get(0).value + ", " + colorData.color_g.get(0).ts + "\n";
                    formattedOutput += "color_b: " + colorData.color_b.get(0).value + ", " + colorData.color_b.get(0).ts + "\n";

                    textView.setText("GET request successful");

                    colorG.setText(Integer.toString(colorData.color_g.get(0).value));
                    colorR.setText(Integer.toString(colorData.color_r.get(0).value));
                    colorB.setText(Integer.toString(colorData.color_b.get(0).value));

                    pieChart.clearChart();

                    pieChart.addPieSlice(new PieModel("Green", Integer.parseInt(colorG.getText().toString()), Color.parseColor("#66BB6A")));
                    pieChart.addPieSlice(new PieModel("Red", Integer.parseInt(colorR.getText().toString()), Color.parseColor("#EF5350")));
                    pieChart.addPieSlice(new PieModel("Blue", Integer.parseInt(colorB.getText().toString()), Color.parseColor("#29B6F6")));

                    pieChart.startAnimation();

                } catch (JsonSyntaxException e) { // Belirli ayrıştırma hatasını yakalayın
                    Log.e(TAG, "Error parsing JSON data: " + e.getMessage());
                    formattedOutput = "Error parsing JSON data";
                    textView.setText(formattedOutput);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing JSON data", e);
                    formattedOutput = "Error parsing JSON data";
                    textView.setText(formattedOutput);// Set a default error message
                }


            } else {
                textView.setText("Error making HTTP request");
            }
        }
    }
}
