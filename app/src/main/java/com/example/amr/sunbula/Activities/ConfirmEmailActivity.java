package com.example.amr.sunbula.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amr.sunbula.Models.APIResponses.VerfiedAccntResponse;
import com.example.amr.sunbula.R;
import com.example.amr.sunbula.RetrofitAPIs.APIService;
import com.example.amr.sunbula.RetrofitAPIs.ApiUtils;
import com.google.firebase.crash.FirebaseCrash;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmEmailActivity extends AppCompatActivity {

    private static final String TAG = "ConfirmEmailActivity";
    String UserID;
    APIService mAPIService;
    Button btn_continue;
    EditText Cnformcode;
    private ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        pdialog = new ProgressDialog(ConfirmEmailActivity.this);
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage("Loading. Please wait...");

        mAPIService = ApiUtils.getAPIService();

        Cnformcode = (EditText) findViewById(R.id.txtconfirmcontinue);
        btn_continue = (Button) findViewById(R.id.btn_continue);

        Intent in = getIntent();
        Bundle b = in.getExtras();
        UserID = b.getString("UserID");

//        Toast.makeText(this, UserID, Toast.LENGTH_SHORT).show();

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Cnformcode.getText().toString().isEmpty()) {
                    Cnformcode.setError("Please enter here");
                } else
                    VerfiedAccntPost(UserID, Cnformcode.getText().toString());
            }
        });
    }

    public void VerfiedAccntPost(String UserId, String Code) {
        pdialog.show();
        mAPIService.VerfiedAccnt(UserId, Code).enqueue(new Callback<VerfiedAccntResponse>() {

            @Override
            public void onResponse(Call<VerfiedAccntResponse> call, Response<VerfiedAccntResponse> response) {

                if (response.isSuccessful()) {

                    if (response.body().isSuccess()) {
                        Log.i(TAG, "post submitted to API." + response.body().toString());

                        SharedPreferences sharedPreferences = ConfirmEmailActivity.this.getSharedPreferences("sharedPreferences_name", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("UserID", UserID);
                        editor.putBoolean("isVerified", true);
                        editor.putBoolean("facebookID", false);
                        editor.apply();
                        Toast.makeText(ConfirmEmailActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ConfirmEmailActivity.this, HomeActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {
                        Toast.makeText(ConfirmEmailActivity.this, response.body().getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                pdialog.dismiss();
            }

            @Override
            public void onFailure(Call<VerfiedAccntResponse> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API.");
                Toast.makeText(ConfirmEmailActivity.this, R.string.string_internet_connection_warning, Toast.LENGTH_SHORT).show();
                pdialog.dismiss();
            }
        });
    }
}
