package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class AddAddressActivity extends AppCompatActivity {

    DBHelper db;
    double latitude;
    double longitude;
    String address;
    DecimalFormat fourDForm = new DecimalFormat("#.####"); //Rounds latitude and longitude to 4 decimals

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        db = new DBHelper(this);
    }

    //This method is used to add a new location to the database
    public void addLocation(View v) {
        displayCoordinates();
        if (this.address != null) {
            if (db.checkAddress(this.address)) {
                db.insertData(this.address, this.latitude, this.longitude);
                Toast.makeText(this, "Location Has Been Added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location already Exists!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter an Address", Toast.LENGTH_SHORT).show();
        }
    }

    //This method updates the address and coordinates on the current page
    public void displayCoordinates() {
        this.address = ((EditText) findViewById(R.id.address_new_enter)).getText().toString();
        //The following code has been taken from the lecture
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> ls = geocoder.getFromLocationName(this.address, 1);
                for (Address addr : ls) {
                    this.latitude = Double.parseDouble(fourDForm.format(addr.getLatitude()));
                    this.longitude = Double.parseDouble(fourDForm.format(addr.getLongitude()));
                }
                TextView address_new = findViewById(R.id.address_new);
                address_new.setText("Address: " + this.address);
                TextView latitude_text = findViewById(R.id.latitude);
                latitude_text.setText("Latitude " + String.valueOf(this.latitude));
                TextView longitude_text = findViewById(R.id.longitude);
                longitude_text.setText("Longitude " + String.valueOf(this.longitude));
            } catch (IOException e) {
                Toast.makeText(this, "That is not a Valid Address!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //This code brings the app back to the MainActivity
    public void goToMainActivity(View v) {
        Intent intent = new Intent(AddAddressActivity.this, MainActivity.class);
        this.startActivity(intent);
    }
}


