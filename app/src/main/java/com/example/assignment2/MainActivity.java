package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DBHelper db; //Variable to reference the database

    //Variables to hold the locations in the listview
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    //Hard-coded original 50 locations coordinates
    double[][] original_coordinates = {{35.6897, 139.6922},
    {-6.2146, 106.8451},
    {28.66, 77.23},
    {18.9667, 72.8333},
    {14.6, 120.9833},
    {31.1667, 121.4667},
    {-46.6339, -23.5504},
    {37.56, 126.99},
    {19.4333, -99.1333},
    {23.1288, 113.259},
    {39.905, 116.3914},
    {30.0561, 31.2394},
    {-73.9249, 40.6943},
    {22.5411, 88.3378},
    {37.6178, 55.7558},
    {13.75, 100.5167},
    {-58.3819, -34.5997},
    {22.535, 114.054},
    {23.7289, 90.3944},
    {6.45, 3.4},
    {28.9603, 41.01},
    {34.75, 135.4601},
    {24.86, 67.01},
    {12.9699, 77.598},
    {35.7, 51.4167},
    {-4.3233, 15.3081},
    {10.8167, 106.6333},
    {34.1139, -118.4068},
    {-43.1964, -22.9083},
    {32.9987, 112.5292},
    {38.8671, 115.4845},
    {13.0825, 80.275},
    {30.66, 104.0633},
    {31.5497, 74.3436},
    {2.3522, 48.8566},
    {-0.1275, 51.5072},
    {35.0606, 118.3425},
    {39.1467, 117.2056},
    {38.0422, 114.5086},
    {33.625, 114.6418},
    {-77.0333, -12.05},
    {17.3667, 78.4667},
    {36.6116, 114.4894},
    {-74.0705, 4.6126},
    {36.7167, 119.1},
    {35.1167, 136.9333},
    {30.5872, 114.2881},
    {35.2333, 115.4333},
    {25.8292, 114.9336},
    {34.261, 117.1859}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView ListView = (ListView) findViewById(R.id.listview);
        db = new DBHelper(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        ListView.setAdapter(adapter);

        //Listen for when an item in the listview is clicked, it brings the user to the UpdateDeleteActivity
        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Get the text of the clicked item in the listview
                String cell = adapterView.getItemAtPosition(i).toString();
                //The following lines of code get the address from the clicked item, without the lat and long values
                int lat_index = cell.indexOf("Lat:");
                String address_only = cell.substring(0, lat_index-1);
                //Using the address to get the id of the row in the table
                int id = db.getId(address_only);
                //Pass the row id to a new activity
                Intent intent = new Intent(MainActivity.this, UpdateDeleteActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        //Insert original 50 hard-coded values only if the table is empty
        Cursor cursor = db.getAllData();
        if(cursor.getCount() == 0) {
            insertOriginalCoordinates();
        }
        //Fill the listview will locations in the table
        populateListView();

        //Listen for when the user types something in the search bar
        EditText queryField = findViewById(R.id.queryField);
        queryField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchForAddress();
            }
        });
    }

    //This method inserts the hard-coded values into the database
    public void insertOriginalCoordinates() {
        //The following code is all taken from the lecture slides
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            for (double[] original_coordinate : this.original_coordinates) {
                double latitude = original_coordinate[0];
                double longitude = original_coordinate[1];

                try {
                    List<Address> ls = geocoder.getFromLocation(latitude, longitude, 1);
                    //Get city, province, country and concatenate them into one address variable
                    String city = ls.get(0).getLocality();
                    String prov = ls.get(0).getAdminArea();
                    String country = ls.get(0).getCountryName();
                    String address = city + ", " + prov + ", " + country;
                    //Store the address, latitude, and longitude into the database
                    Boolean checkInsert = db.insertData(address, latitude, longitude);
                    if (!checkInsert) {
                        Log.e("Inserting Error", "Could not insert" + address + " " + latitude + " " + longitude);
                    }
                } catch (IOException e) {

                }
            }
        }
    }

    //This method is used to fill the listview will all of the locations in the database
    public void populateListView() {
        Cursor data = db.getAllData();
        listItems.removeAll(listItems);
        while (data.moveToNext()) {
            listItems.add(data.getString(0) + "\nLat: " + String.valueOf(data.getDouble(1)) + ", Long: " + String.valueOf(data.getDouble(2)));
        }
        adapter.notifyDataSetChanged();
    }

    //This method is used to filter the listview to find addresses that match the users query
    public void searchForAddress() {
        String address = ((EditText) findViewById(R.id.queryField)).getText().toString(); //Get the contents of the search bar
        Cursor data = db.query(address);
        listItems.removeAll(listItems); //Empty the listview and then repopulate it with matching addresses only
        while (data.moveToNext()) {
            listItems.add(data.getString(0) + "\nLat: " + String.valueOf(data.getDouble(1)) + ", Long: " + String.valueOf(data.getDouble(2)));
        }
        adapter.notifyDataSetChanged();
    }

    //This method will bring the app to the AddAddressActivity
    public void goToAddActivity(View v) {
        Intent intent = new Intent(MainActivity.this, AddAddressActivity.class);
        this.startActivity(intent);
    }
}