package com.example.fuelcal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";







    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private TextView fuel_price_input;
    private Spinner price_unit_spinner_input;
    private Spinner distance_unit_spinner_input;
    private TextView distance_input;
    private TextView fuel_consumption_input;
    private TextView trip_cost_output;
    private Button calculate_button;


    private double gallonToLitre = 4.54609;
    private double milesToKilometer = 1.609344;

    private static final DecimalFormat df = new DecimalFormat("#,##0.##");





    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



       //fuel_price_input = (TextView)getView().findViewById(R.id.fuel_price_input);
        /*price_unit_spinner_input = (Spinner)getView().findViewById(R.id.price_unit_spinner_input);
        distance_unit_spinner_input = (Spinner)getView().findViewById(R.id.distance_unit_spinner_input);
        distance_input = (TextView)getView().findViewById(R.id.distance_input);
        fuel_consumption_input = (TextView)getView().findViewById(R.id.fuel_consumption_input);
        trip_cost_output = (TextView)getView().findViewById(R.id.trip_cost_output);*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fuel_price_input = (TextView)getView().findViewById(R.id.fuel_price_input);
        price_unit_spinner_input = (Spinner)getView().findViewById(R.id.price_unit_spinner_input);
        distance_unit_spinner_input = (Spinner)getView().findViewById(R.id.distance_unit_spinner_input);
        distance_input = (TextView)getView().findViewById(R.id.distance_input);
        fuel_consumption_input = (TextView)getView().findViewById(R.id.fuel_consumption_input);
        trip_cost_output = (TextView)getView().findViewById(R.id.trip_cost_output);
        calculate_button = (Button)getView().findViewById(R.id.calculate);

        calculate_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CalTripCost(view);
            }
        });
    }

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

// fuel_price_unit
        if (fuel_price_input.getText().toString().equals("") == false) {
            fuel_price_input_double = Double.parseDouble(fuel_price_input.getText().toString());
        }

        if(price_unit_spinner_input.getSelectedItem().toString().equals("p/Litre")){
            fuel_price_input_double = fuel_price_input_double / 100;
        }

        if(price_unit_spinner_input.getSelectedItem().toString().equals("£/Litre")){
            fuel_price_input_double = fuel_price_input_double;
        }


//distance_unit
        if (distance_input.getText().toString().equals("") == false) {
            distance_input_double = Double.parseDouble(distance_input.getText().toString());
        }

        if(distance_unit_spinner_input.getSelectedItem().toString().equals("Miles")){
            distance_input_double = distance_input_double;
        }

        if(distance_unit_spinner_input.getSelectedItem().toString().equals("Kilometer")){
            distance_input_double = distance_input_double / milesToKilometer;
        }





        if (fuel_consumption_input.getText().toString().equals("") == false) {
            fuel_consumption_input_double = Double.parseDouble(fuel_consumption_input.getText().toString());
        }

        double tripCost = 0;
        String tripCostString = "";

        try {
            tripCost = tripCost(fuel_price_input_double, distance_input_double, fuel_consumption_input_double);

            tripCostString = df.format(tripCost);
        } catch (Exception e) {
            System.out.println(e);

        }


        trip_cost_output.setText(tripCostString);


    }
}