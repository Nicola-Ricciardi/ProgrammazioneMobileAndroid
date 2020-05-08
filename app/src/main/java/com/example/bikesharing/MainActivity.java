package com.example.bikesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    UserSessionManager session;
    static final int REQUEST_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        session = new UserSessionManager(getApplicationContext());
        if(session.isUserLoggedIn()){
            Intent FragmentMap = new Intent(this, Home.class);
            startActivity(FragmentMap);
        }



        if (    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) +
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {

            if(     ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {

                AlertDialog.Builder builer = new AlertDialog.Builder(MainActivity.this);
                builer.setTitle("Sono necessari i seguenti permessi");
                builer.setMessage("Posizone GPS, Storage Esterno");
                builer.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                                },REQUEST_CODE );
                    }
                });
                builer.setNegativeButton("Esci", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }
                });
                AlertDialog alertDialog = builer.create();
                alertDialog.show();
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },REQUEST_CODE );
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CODE)  {
            if (grantResults.length > 0 && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED)
            {

            }
            else
            {
                finish();
            }
        }
    }
    public void LOGIN (View v)
    {
        Intent SchermataLogin = new Intent (this, Login.class);
        startActivity (SchermataLogin);
    }
    public void SIGNUP(View v)
    {
        Intent SchermataSignUp = new Intent (this, SignUp.class);
        startActivity (SchermataSignUp);
    }
}
