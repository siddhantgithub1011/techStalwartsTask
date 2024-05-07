package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserLogin extends AppCompatActivity {

    private EditText userEmailEditText;
    private EditText userPassEditText;
    private TextView loginButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        userEmailEditText = findViewById(R.id.useremail);
        userPassEditText = findViewById(R.id.userpass);
        loginButton = findViewById(R.id.userlogin);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        loginButton.setOnClickListener(view -> {
            String userEmail = userEmailEditText.getText().toString();
            String userPass = userPassEditText.getText().toString();
            if (!isValidEmail(userEmail)) {
                userEmailEditText.setError("Invalid email address");
                return;
            }
            if (!isValidPassword(userPass)) {
                userPassEditText.setError("Password must be 8 characters long");
                return;
            }
            login(userEmail, userPass);
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.contains("@");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    private void login(String email, String password) {
        Call<LoginResponse> call = apiService.login(email, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        String token = loginResponse.getToken();
                        Intent intent = new Intent(UserLogin.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(UserLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            if (errorBody.contains("user not found")) {
                                Toast.makeText(UserLogin.this, "User not found. Please check your credentials.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserLogin.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(UserLogin.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(UserLogin.this, "Network error. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
