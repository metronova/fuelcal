package com.example.fuelcal;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.fuelcal.databinding.ActivityScrollingBinding;

public class ScrollingActivity extends AppCompatActivity {

    private ActivityScrollingBinding binding;


    private TextView fuel_price_input;
    private TextView distance_input;
    private TextView fuel_consumption_input;
    private TextView trip_cost_output;


    private double gallonToLitre = 4.54609;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // Locate the UI widgets.
        fuel_price_input = (TextView) findViewById(R.id.fuel_price_input);
        distance_input = (TextView) findViewById(R.id.distance_input);
        fuel_consumption_input = (TextView) findViewById(R.id.fuel_consumption_input);
        trip_cost_output = (TextView) findViewById(R.id.trip_cost_output);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

///////////////////////////////


    public double tripCost(double fuel_price_input, double distance_input,
                           double fuel_consumption_input) {

        double tripCost;

        tripCost = (gallonToLitre / fuel_consumption_input) * fuel_price_input * distance_input;

        return tripCost;

    }


    public void CalTripCost(View view) {

        Double fuel_price_input_double = 0.0;
        Double distance_input_double = 0.0;
        Double fuel_consumption_input_double = 0.0;


        if (fuel_price_input.getText().toString().equals("") == false) {
            fuel_price_input_double = Double.parseDouble(fuel_price_input.getText().toString());
        }
        if (distance_input.getText().toString().equals("") == false) {
            distance_input_double = Double.parseDouble(distance_input.getText().toString());
        }
        if (fuel_consumption_input.getText().toString().equals("") == false) {
            fuel_consumption_input_double = Double.parseDouble(fuel_consumption_input.getText().toString());
        }

        double tripCost = 0;


        try {
            tripCost = tripCost(fuel_price_input_double, distance_input_double, fuel_consumption_input_double);
        } catch (Exception e) {
            System.out.println(e);

        }


        String tripCostString = Double.toString(tripCost);
        trip_cost_output.setText(tripCostString);


    }
}



