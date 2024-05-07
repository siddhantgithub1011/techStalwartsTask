package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.LongFunction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView locationTextView;
    private TextView getLocationButton;
    double latitude, longitude;
    RecyclerView rvProspectList;
    Activity context = MainActivity.this;
    String addressLone, State;
    EditText searchEditText;
    boolean isLocationFetched = false;
    private ApiService apiService;
    private UserAdapter adapter;
    Button logout;
    private List<UsersList.User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvProspectList = findViewById(R.id.rvProspectList);
        RecyclerView.LayoutManager verticalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvProspectList.setLayoutManager(verticalLayoutManager);
        rvProspectList.setNestedScrollingEnabled(false);

        logout=findViewById(R.id.logout);
        searchEditText=findViewById(R.id.searchEditText);
        locationTextView = findViewById(R.id.areaTextView);
        getLocationButton = findViewById(R.id.findlocation);
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
                expandTextView();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
        fetchUsersFromApi();

        setUpSearch();

    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i=new Intent(MainActivity.this, UserLogin.class);
                startActivity(i);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void expandTextView() {
        TextView areaTextView = findViewById(R.id.areaTextView);
        ViewGroup.LayoutParams params = areaTextView.getLayoutParams();
        params.height = dpToPx(165);
        areaTextView.setLayoutParams(params);
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void setUpSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void filter(String text) {

        List<UsersList.User> filteredList = new ArrayList<>();
        for (UsersList.User user : users) {
            if (user.getFirst_name().toLowerCase().contains(text.toLowerCase())
                    || user.getLast_name().toLowerCase().contains(text.toLowerCase())
                    || user.getEmail().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.filterList(filteredList);
    }


    private void fetchUsersFromApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        Call<UsersList> call = apiService.getUsers(2);
        call.enqueue(new Callback<UsersList>() {
            @Override
            public void onResponse(Call<UsersList> call, Response<UsersList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UsersList usersList = response.body();
                    if (usersList.getPage() == 2) {
                        users = usersList.getData();
                        adapter = new UserAdapter(users);
                        rvProspectList.setAdapter(adapter);
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<UsersList> call, Throwable t) {
            }
        });
    }



    private void checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (!isLocationFetched) {
            Toast.makeText(context, "Fetching Location .", Toast.LENGTH_SHORT).show();
            LocationRequest locationRequest = new LocationRequest()
                    .setInterval(500)
                    .setFastestInterval(500)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            if (locationResult != null && locationResult.getLastLocation() != null) {
                                Location location = locationResult.getLastLocation();
                                updateLocationUI(location);
                                isLocationFetched = true;
                            }
                        }
                    }, Looper.myLooper());
        }
    }

    private void updateLocationUI(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                addressLone = address.getAddressLine(0);
                State = address.getAdminArea();
                locationTextView.setText("Address: " + addressLone + "\nState: " + State);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }
}


