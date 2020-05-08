package com.example.bikesharing;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class Login extends AppCompatActivity {

    private EditText controlloEmail;
    private EditText controlloPassword;
    private String valoreKey;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getDelegate().getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        session = new UserSessionManager(getApplicationContext());
        controlloEmail = findViewById(R.id.EmailLogin);
        controlloPassword = findViewById(R.id.PasswordLogin);
    }

    public void HOME (View v)
    {
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        controlloRegistro();
        if(controlloRegistro()){
            session.createUserLoginSession(controlloEmail.getText().toString(), valoreKey);
            Intent SchermataHome = new Intent (this, Home.class);
            SchermataHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(SchermataHome);
        }
        else{
            AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
            a_builder.setMessage("UTENTE NON REGISTRATO").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = a_builder.create();
            alert.setTitle("ERRORE");
            alert.show();
            controlloEmail.getText().clear();
            controlloPassword.getText().clear();
        }
    }

    private boolean controlloRegistro()
    {
        HttpURLConnection client = null;
        boolean result = false;
        try {
            URL url = new URL("http://nicolaricciardi.altervista.org/controlla_registro.php");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");


            InputStream in = client.getInputStream();
            String json_string = LeggiRisposta.readStream(in);
            JSONObject json_data = convert2JSON(json_string);
            result = fill_listview(json_data);
            if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {

            } else {
                //response = "FAILED";
            }
        } catch (MalformedURLException error) {

        } catch (SocketTimeoutException error) {

        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
        return result;
    }


    private boolean fill_listview(JSONObject json_data)
    {
        boolean result = false;
        Iterator<String> iter = json_data.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                JSONObject value = json_data.getJSONObject(key);
                if((controlloEmail.getText().toString()).equals(value.getString("Email")) &&
                        (controlloPassword.getText().toString()).equals(value.getString("Password"))){
                    valoreKey = key;
                    result = true;
                    break;
                }//
            } catch (JSONException e) {
                // Something went wrong!
            }
        }
        return result;
    }


    private JSONObject convert2JSON(String json_data)
    {
        JSONObject obj = null;
        try {
            obj = new JSONObject(json_data);
            //Log.d("My App", obj.toString());
        } catch (Throwable t) {
            //Log.e("My App", "Could not parse malformed JSON: \"" + json_data + "\"");
        }
        return obj;
    }
}
