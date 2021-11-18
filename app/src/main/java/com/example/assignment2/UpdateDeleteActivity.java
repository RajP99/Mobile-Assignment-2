package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
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

public class UpdateDeleteActivity extends AppCompatActivity {
    DBHelper db;
    int id = 0;
    String address;
    double latitude;
    double longitude;
    DecimalFormat fourDForm = new DecimalFormat("#.####"); //Formats latitude and longitude to 4 decimals

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);
        db = new DBHelper(this);

        this.id = getIntent().getIntExtra("id", 0);
        fillLocationDetails();
    }

    //This method updates the fields on the page to hold the location details
    public void fillLocationDetails() {
        Cursor cursor = db.queryId(this.id);
        cursor.moveToNext();
        this.address = cursor.getString(0);
        this.latitude = cursor.getDouble(1);
        this.longitude = cursor.getDouble(2);

        TextView address_new = findViewById(R.id.address_updated);
        address_new.setText("Address: " + this.address);
        TextView latitude_text = findViewById(R.id.latitude_updated);
        latitude_text.setText("Latitude " + String.valueOf(this.latitude));
        TextView longitude_text = findViewById(R.id.longitude_updated);
        longitude_text.setText("Longitude " + String.valueOf(this.longitude));
    }

    //This method is used to update the address which the user entered
    public void updateAddress(View v) {
        if (displayCoordinates()) {
            if (this.address != null) {
                if (db.checkAddress(this.address)) {
                    db.updateLocation(this.id, this.address, this.latitude, this.longitude);
                    Toast.makeText(this, "Location has been updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Address already Exists!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter an Address", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //This method is used to check if the user entered a valid address, and it updates the TextViews on the page
    public Boolean displayCoordinates() {
        this.address = ((EditText) findViewById(R.id.address_update_enter)).getText().toString();
        //The following code had been taken from the lecture slides
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> ls = geocoder.getFromLocationName(this.address, 1);
                for (Address addr : ls) {
                    this.latitude = Double.parseDouble(fourDForm.format(addr.getLatitude()));
                    this.longitude = Double.parseDouble(fourDForm.format(addr.getLongitude()));
                }
                TextView address_new = findViewById(R.id.address_updated);
                address_new.setText("Address: " + this.address);
                TextView latitude_text = findViewById(R.id.latitude_updated);
                latitude_text.setText("Latitude " + String.valueOf(this.latitude));
                TextView longitude_text = findViewById(R.id.longitude_updated);
                longitude_text.setText("Longitude " + String.valueOf(this.longitude));
                return true;
            } catch (IOException e) {
                Toast.makeText(this, "That is not a Valid Address!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }

    //This method deletes the current location from the database and then goes to the MainActivity
    public void deleteLocation(View v) {
        db.deleteLocation(this.id);
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        Toast.makeText(this, "Location Deleted", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    //This method is used to go the the MainActivity
    public void goToMainActivity(View v) {
        Intent intent = new Intent(UpdateDeleteActivity.this, MainActivity.class);
        this.startActivity(intent);
    }
}