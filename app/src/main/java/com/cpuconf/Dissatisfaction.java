package com.cpuconf;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Created by emir on 4/19/16.
 * This is a class for logging user dissatisfaction in 3 levels (normal, angry, very angry)
 */
public class Dissatisfaction extends Activity {
    private TextView info;
    private LinearLayout infoLay;
    private Button first, second, third;
    final private static String TAG = "Info";


    private long startingTime = 0;
    private String remaining;
    private double cpu;
    private long previousPower = 0;
    private boolean noData = true;
    private int hour=0;
    public String timeInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);


        if(getData()==false || writeData() == false){
            Toast.makeText(this, "Applications are buffering please try later", Toast.LENGTH_LONG).show();
            return;
        }



        first = (Button) this.findViewById(R.id.firstLevel);
        if (first != null) {
            first.setOnClickListener(firstListener);
        }

        second = (Button) this.findViewById(R.id.secondLevel);
        if (second != null) {
            second.setOnClickListener(secondListener);
        }


        third = (Button) this.findViewById(R.id.thirdLevel);
        if (third != null) {
            third.setOnClickListener(thirdListener);
        }



    }

    private boolean getData(){
        return true;
    }

    private boolean writeData() {


        return true;
    }




    private View.OnClickListener firstListener = new View.OnClickListener() { // normal
        @Override
        public void onClick(View v) {
            try {
                ServiceClass.getLogger().logEntry("User_satisfaction: " + 1);
                Toast.makeText(Dissatisfaction.this, "Thank you! (1)", Toast.LENGTH_SHORT).show();
                DataHolder.getInstance().setUserSatisfaction(1);
            }catch (Exception e){}

        }
    };


    private View.OnClickListener secondListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {   // angry
            try {
           ServiceClass.getLogger().logEntry("User_satisfaction: " + 2);
           Toast.makeText(Dissatisfaction.this, "Thank you! (2)", Toast.LENGTH_SHORT).show();
                DataHolder.getInstance().setUserSatisfaction(2);

            }catch (Exception e){}

        }
    };


    private View.OnClickListener thirdListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {   // so angry
            try {
                ServiceClass.getLogger().logEntry("User_satisfaction: " + 3);
                Toast.makeText(Dissatisfaction.this, "Thank you! (3)", Toast.LENGTH_SHORT).show();
                DataHolder.getInstance().setUserSatisfaction(3);



            }catch (Exception e){}

        }
    };
}

