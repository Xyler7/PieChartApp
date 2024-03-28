package com.kutaydem.piechart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.kutaydem.piechart.Fragments.RandomlyFragment;
import com.kutaydem.piechart.Fragments.ManuallyFragment;

public class MainActivity extends AppCompatActivity {
    Button btnRandom, btnManual;
    private FragmentManager fragmentManager;


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Recreate the current fragment to apply the new layout
        if (getSupportFragmentManager().getFragments() != null) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof ManuallyFragment) {
                    ManuallyFragment manuallyFragment = (ManuallyFragment) fragment;
                    getSupportFragmentManager().beginTransaction().detach(manuallyFragment).commitNowAllowingStateLoss();
                    getSupportFragmentManager().beginTransaction().attach(manuallyFragment).commitNowAllowingStateLoss();
                    break;
                }
                if (fragment instanceof RandomlyFragment) {
                    RandomlyFragment randomlyFragment = (RandomlyFragment) fragment;
                    getSupportFragmentManager().beginTransaction().detach(randomlyFragment).commitNowAllowingStateLoss();
                    getSupportFragmentManager().beginTransaction().attach(randomlyFragment).commitNowAllowingStateLoss();
                    break;
                }
            }
        }
    }

    protected void onCreate(Bundle savedIntancesStates) {
        super.onCreate(savedIntancesStates);
        setContentView(R.layout.activity_main);
        init();

    }


    public void init() {
        fragmentManager = getSupportFragmentManager();

        btnRandom = findViewById(R.id.btnRandomly);
        btnManual = findViewById(R.id.btnManually);

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent= new Intent();
                finish();
                startActivity(intent);*/
                System.out.println("btnRandom Pressedx1");
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.constraintMain, new RandomlyFragment());
                fragmentTransaction.commit();
                btnHide();
                System.out.println("btnRandom Pressed");
            }
        });

        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("btnManual Pressedx1");
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.constraintMain, new ManuallyFragment());
                fragmentTransaction.commit();
                btnHide();
                System.out.println("btnManual Pressed");
            }
        });

    }

    public void btnHide() {
        btnRandom.setEnabled(false);
        btnManual.setEnabled(false);
        btnRandom.setVisibility(View.GONE);
        btnManual.setVisibility(View.GONE);
    }
}