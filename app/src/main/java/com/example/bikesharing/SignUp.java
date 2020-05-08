package com.example.bikesharing;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private static final Pattern TEXT_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[a-zA-Z])" +
                    ".{5,}" +
                    "$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +       //inizio linea
                    "(?=.*[0-9])" +     //almeno 1 digit
                    "(?=.*[a-z])" +     //almeno 1 minuscolo
                    "(?=.*[A-Z])" +     //almeno 1 maiuscolo
                    "(?=.*[@#$%^&+?=])"+ //almeno 1 carattere speciale
                    "(?=\\S+$)" +       //no spazi vuoti(fine stringa + spazio($))
                    ".{6,}" +           //almeno 6 caratteri
                    "$");               //fine linea


    public static final Pattern EMAIL_ADDRESS =
            Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" + "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"
    );

    private TextInputLayout textEmail;
    private TextInputLayout textFirstname;
    private TextInputLayout textLastname;
    private TextInputLayout textPassword;
    private TextInputLayout textConfirmPassword;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String date;
    private ImageButton mImage;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getDelegate().getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up);
        textEmail = findViewById(R.id.EmailSignUp);
        textFirstname = findViewById(R.id.FirstNameSignUp);
        textLastname = findViewById(R.id.LastNameSignUp);
        textPassword = findViewById(R.id.PasswordSignUp);
        textConfirmPassword = findViewById(R.id.ConfirmPasswordSignUp);
        mImage = (ImageButton) findViewById(R.id.imageButton);
        mDisplayDate = (TextView) findViewById(R.id.DateSelected);

        mImage.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Calendar cal = Calendar.getInstance();
                                                int year = cal.get(Calendar.YEAR);
                                                int month = cal.get(Calendar.MONTH);
                                                int day = cal.get(Calendar.DAY_OF_MONTH);

                                                DatePickerDialog dialog = new DatePickerDialog(
                                                        SignUp.this,
                                                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                                        mDateSetListener,
                                                        year, month, day);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                dialog.show();

                                            }
                                        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            boolean anni = false;
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                Calendar cal_conf = Calendar.getInstance();
                int year_conf = cal_conf.get(Calendar.YEAR);
                int month_conf = cal_conf.get(Calendar.MONTH);
                int day_conf = cal_conf.get(Calendar.DAY_OF_MONTH);

                if ((year_conf - year) > 18)
                {
                    anni=true;
                }
                else if ((year_conf - year) == 18)
                {
                    if (month_conf > month)
                    {
                        anni=true;
                    }
                    else if (month_conf == month)
                    {
                        if (day_conf >= day)
                        {
                            anni=true;
                        }
                        else
                        {

                        }
                    }
                    else
                    {

                    }
                }
                else if((year_conf - year) < 18)
                {

                }
                month = month+1;
                date = month + "/" + day + "/" + year;
                if(!anni)
                {
                    System.out.println("PRINT DIALOG PER ANNI < 18");
                    AlertDialog.Builder a_builder = new AlertDialog.Builder(SignUp.this);
                    a_builder.setMessage("ETA MINIMA 18 ANNI").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = a_builder.create();
                    alert.setTitle("ERRORE");
                    alert.show();
                    return;

                }
                else
                    {
                        mDisplayDate.setText(date);
                    }
            }
        };
    }


    private boolean validateEmail()
    {
        String emailInput = textEmail.getEditText().getText().toString().trim(); //metodo trim(restituisce una copia della stringa chiamante da cui è stato eliminato qualsiasi spazio vuoto iniziale e finale)
        if (emailInput.isEmpty())
        {
            textEmail.setError("CAMPO VUOTO NON AMMESSO");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
        {
            textEmail.setError("INSERIRE UNA MAIL VALIDA");
            return false;
        }
        else
        {
            textEmail.setError(null);
            return true;
        }
    }

    private boolean validateFirstname()
    {
        String firstnameInput = textFirstname.getEditText().getText().toString().trim();
        if (firstnameInput.isEmpty())
        {
            textFirstname.setError("CAMPO VOUTO NON AMMESSO");
            return false;
        }
        else if(!TEXT_PATTERN.matcher(firstnameInput).matches() || firstnameInput.length() < 5)
        {
            if (firstnameInput.length() < 5)
            {
                textFirstname.setError("ERRORE. CAMPO TROPPO CORTO");
                textFirstname.getEditText().getText().clear();

            }
            else if (!TEXT_PATTERN.matcher(firstnameInput).matches())
            {
                textFirstname.setError("ERRORE. INSERIRE SOLO CARATTERI");
                textFirstname.getEditText().getText().clear();
            }
            return false;
        }
        else
        {
            textFirstname.setError(null);
            return true;
        }
    }


    private boolean validateLastname()
    {
        String lastnameInput = textLastname.getEditText().getText().toString().trim();
        if (lastnameInput.isEmpty())
        {
            textLastname.setError("CAMPO VOUTO NON AMMESSO");
            return false;
        }
        else if(!TEXT_PATTERN.matcher(lastnameInput).matches() || lastnameInput.length() < 5)
        {
            if (lastnameInput.length() < 5)
            {
                textLastname.setError("ERRORE. CAMPO TROPPO CORTO");
                textLastname.getEditText().getText().clear();
            }
            else if (!TEXT_PATTERN.matcher(lastnameInput).matches())
            {
                textLastname.setError("ERRORE. INSERIRE SOLO CARATTERI");
                textLastname.getEditText().getText().clear();
            }
            return false;
        }

        else
        {
            textLastname.setError(null);
            return true;
        }
    }

    private boolean validatePassword()
    {
        String passwordInput = textPassword.getEditText().getText().toString().trim();
        if (passwordInput.isEmpty())
        {
            textPassword.setError("CAMPO VUOTO NON AMMESSO");
            return false;
        }
        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches())
        {
            textPassword.setError("PASSWORD DEBOLE. INSERIRE UN NUMERO, MINUSCOLO, MAIUSCOLO, CARATTERE  SPECIALE");
            textPassword.getEditText().getText().clear();
            return false;
        }
        else
        {
            textPassword.setError(null);
            return true;
        }
    }


    private boolean validatePasswordConfirm()
    {
        String passwordConfirmInput = textConfirmPassword.getEditText().getText().toString().trim();
        if (passwordConfirmInput.isEmpty())
        {
            textConfirmPassword.setError("CAMPO VUOTO NON AMMESSO");
            return false;
        }
        else if (!PASSWORD_PATTERN.matcher(passwordConfirmInput).matches())
        {
            textConfirmPassword.setError("PASSWORD DEBOLE. INSERIRE UN NUMERO, MINUSCOLO, MAIUSCOLO, CARATTERE  SPECIALE");
            textConfirmPassword.getEditText().getText().clear();
            return false;
        }
        else
        {
            textConfirmPassword.setError(null);
            return true;
        }

    }



    public void confirmInput(View v)
    {
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if(!validateFirstname() || !validateLastname() || !validateEmail() || !validatePassword() || !validatePasswordConfirm())
        {
            return;
        }
        else
        {
            if(!(textPassword.getEditText().getText().toString()).equals(textConfirmPassword.getEditText().getText().toString()))
            {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
                a_builder.setMessage("PASSWORD ERRATA").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = a_builder.create();
                alert.setTitle("ERRORE");
                alert.show();
                return;
            }

        }

        radioGroup = findViewById(R.id.radio_group);
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        HttpURLConnection client = null;

        try {

            URL url = new URL("http://nicolaricciardi.altervista.org/aggiungi_utente_post.php");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            client.setDoInput(true);
            OutputStream out = new BufferedOutputStream(client.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String data = URLEncoder.encode("nome", "UTF-8")
                    + "=" + URLEncoder.encode(textFirstname.getEditText().getText().toString(), "UTF-8");
            data += "&" + URLEncoder.encode("cognome", "UTF-8")
                    + "=" + URLEncoder.encode(textLastname.getEditText().getText().toString(), "UTF-8");
            data += "&" + URLEncoder.encode("email","UTF-8")
                    + "=" + URLEncoder.encode(textEmail.getEditText().getText().toString(),"UTF-8");
            data += "&" + URLEncoder.encode("password","UTF-8")
                    + "=" + URLEncoder.encode(textPassword.getEditText().getText().toString(),"UTF-8");
            data += "&" + URLEncoder.encode("date","UTF-8")
                    + "=" + URLEncoder.encode(date,"UTF-8");
            data += "&" + URLEncoder.encode("sesso","UTF-8")
                    + "=" + URLEncoder.encode(radioButton.getText().toString(),"UTF-8");

            writer.write(data);
            writer.flush();
            writer.close();
            out.close();
            if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {

            } else {
                //response = "FAILED";
            }
        }
        catch (MalformedURLException error) {

        } catch (SocketTimeoutException error) {

        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
        Intent Start = new Intent (this, MainActivity.class);
        Start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(Start);

    }
}
