package com.example.bikesharing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class Home_Fragment extends Fragment implements AdapterView.OnItemSelectedListener, Runnable
{
    UserSessionManager session;
    Button logout;
    Button riconsegna;
    CheckBox checkBox;
    private TextView nomeUser;
    private TextView cognomeUser;
    private TextView emailUser;
    private ArrayList<String> indicePrestiti;
    private ArrayList<String> vettoreCheckBox;
    private Dialog nomeStazione;
    private ArrayList<String> strStazione;
    private String text="";
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View v = inflater.inflate(R.layout.fragment_user, container, false);
        session = new UserSessionManager(getActivity().getApplicationContext());
        nomeUser = v.findViewById(R.id.textView);
        cognomeUser = v.findViewById(R.id.textView2);
        emailUser = v.findViewById(R.id.textView3);
        riconsegna = v.findViewById(R.id.button2);
        logout = (Button) v.findViewById(R.id.button4);
        indicePrestiti = new ArrayList<>();
        vettoreCheckBox = new ArrayList<>();
        strStazione = new ArrayList<>();

        HashMap<String, String> user = session.getUserDetails();
        final String id = user.get(UserSessionManager.KEY_ID_USER);
        dati_utente(id);
        dati_prenotazione(id);

        riconsegna.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                for (int s=0; s<vettoreCheckBox.size(); s++){
                    final int ordine = Integer.valueOf(vettoreCheckBox.get(s));
                        AlertDialog.Builder a_builder = new AlertDialog.Builder(getActivity());
                        a_builder.setMessage("RESTITUZIONE ORDINE " + vettoreCheckBox.get(s) + "?").setPositiveButton("SI", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                                strStazione = new ArrayList<>();
                                StazioneRestituzione(id, ordine);
                            }
                        })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which){
                                        dialog.cancel();
                                        resume();
                                    }
                                });
                        AlertDialog alert = a_builder.create();
                        alert.setTitle("CONFERMA RESTITUZIONE");
                        alert.show();
                }
                vettoreCheckBox.clear();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                Intent ritornoMain = new Intent(getActivity(), MainActivity.class);
                ritornoMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(ritornoMain);
            }
        });


        LinearLayout linearMain;
        linearMain = (LinearLayout) v.findViewById(R.id.linear_user);
        for (int i = 0; i < indicePrestiti.size(); i++){
            checkBox = new CheckBox(getActivity());
            checkBox.setId(i);
            checkBox.setText(indicePrestiti.get(i));
            checkBox.setOnClickListener(getOnClickDoSomething(checkBox));
            linearMain.addView(checkBox);
        }
        return v;
    }

    @Override
    public void run()
    {
        while (running) {
            synchronized (pauseLock) {
                if (!running) {
                    break;
                }
                if (paused) {
                    try {
                        synchronized (pauseLock) {
                            pauseLock.wait();
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                    if (!running) {
                        break;
                    }
                }
            }
        }
    }

    public void stop()
    {
        running = false;
        resume();
    }

    public void pause() {
        paused = true;
    }

    public void resume()
    {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    View.OnClickListener getOnClickDoSomething(final Button button)
    {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean b = ((CheckBox)v).isChecked();
                v.getId();
                if(b)
                {
                    vettoreCheckBox.add(button.getText().toString());
                }
            }
        };
    }


    private void dati_utente(String chiave)
    {
        HttpURLConnection client = null;
        try {
            URL url = new URL("http://nicolaricciardi.altervista.org/info_utente.php");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            client.setDoInput(true);
            OutputStream out = new BufferedOutputStream(client.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String data = URLEncoder.encode("id_utente", "UTF-8")
                    + "=" + URLEncoder.encode(chiave, "UTF-8");
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();
            InputStream in = client.getInputStream();
            String json_string = LeggiRisposta.readStream(in);
            JSONObject json_data = convert2JSON(json_string);
            fill_listview(json_data);
            if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {

            } else {

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
    }

    private void fill_listview(JSONObject json_data)
    {
        String nome;
        String cognome;
        String email;
        Iterator<String> iter = json_data.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                JSONObject value = json_data.getJSONObject(key);
                nome = value.getString("Nome");
                cognome = value.getString("Cognome");
                email = value.getString("Email");
                nomeUser.setText(nome);
                cognomeUser.setText(cognome);
                emailUser.setText(email);
            } catch (JSONException e) {

            }
        }
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


    private void dati_prenotazione(String chiave)
    {
        HttpURLConnection client = null;
        try {
            URL url = new URL("http://nicolaricciardi.altervista.org/visualizza_prestiti.php");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            client.setDoInput(true);
            OutputStream out = new BufferedOutputStream(client.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String data = URLEncoder.encode("id_utente", "UTF-8")
                    + "=" + URLEncoder.encode(chiave, "UTF-8");
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();
            InputStream in = client.getInputStream();
            String json_string1 = LeggiRisposta.readStream(in);
            JSONObject json_data1 = convert2JSON1(json_string1);

            if(json_data1 != null)
            {
                fill_listview1(json_data1);
            }

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
    }

    private void fill_listview1(JSONObject json_data)
    {
        Iterator<String> iter1 = json_data.keys();
        while (iter1.hasNext()) {
            String key1 = iter1.next();
            try {
                JSONObject value1 = json_data.getJSONObject(key1);
                indicePrestiti.add(value1.getString("Prenotazione"));
            } catch (JSONException e) {
                // Something went wrong!
            }
        }
    }

    private JSONObject convert2JSON1(String json_data1)
    {
        JSONObject obj1 = null;
        try {
            obj1 = new JSONObject(json_data1);
            //Log.d("My App", obj.toString());
        } catch (Throwable t) {
            //Log.e("My App", "Could not parse malformed JSON: \"" + json_data + "\"");
        }
        return obj1;
    }

    private void StazioneRestituzione(final String chiave, final int valore)
    {
        stazione_restituzione();
        nomeStazione = new Dialog(getActivity());
        TextView textclose;
        Button buttonConfirm;
        nomeStazione.setContentView(R.layout.dialog_station);
        textclose = nomeStazione.findViewById(R.id.closeText);
        buttonConfirm = nomeStazione.findViewById(R.id.buttonConfirm);
        Spinner mySpinner = (Spinner) nomeStazione.findViewById(R.id.spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, strStazione);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(this);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restituzione_ordine(chiave, valore, text);
                nomeStazione.dismiss();
            }
        });
        textclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomeStazione.dismiss();
            }
        });
        nomeStazione.show();


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(),text,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    private void stazione_restituzione()
    {

        HttpURLConnection client = null;
        try {
            URL url = new URL("http://nicolaricciardi.altervista.org/stazioni_disponibili.php");
            client = (HttpURLConnection) url.openConnection();
            //System.out.println("CLIENT"+client.getResponseCode());
            client.setRequestMethod("POST");
            InputStream in = client.getInputStream();
            String json_string2 = LeggiRisposta.readStream(in);

            JSONObject json_data2 = convert2JSON2(json_string2);

            fill_listview2(json_data2);

            if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {

            } else {

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

    }

    private void fill_listview2(JSONObject json_data)
    {
        Iterator<String> iter2 = json_data.keys();

        while (iter2.hasNext()) {
            String key2 = iter2.next();

            try {

                JSONObject value2 = json_data.getJSONObject(key2);
                strStazione.add(value2.getString("Nome"));

            } catch (JSONException e) {

            }
        }


    }

    private JSONObject convert2JSON2(String json_data2)
    {
        JSONObject obj2 = null;
        try {
            obj2 = new JSONObject(json_data2);
            //Log.d("My App", obj.toString());
        } catch (Throwable t) {
            //Log.e("My App", "Could not parse malformed JSON: \"" + json_data + "\"");
        }
        return obj2;
    }

    private void restituzione_ordine(String utente, int ordine, String stazione)
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        String date = month + "/"+ day + "/" + year;

        HttpURLConnection client = null;
        try {

            URL url = new URL("http://nicolaricciardi.altervista.org/restituzione_ordine.php");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            client.setDoInput(true);
            OutputStream out = new BufferedOutputStream(client.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String numero_ordine = String.valueOf(ordine);
            String data1 = URLEncoder.encode("id_utente","UTF-8")
                    + "=" + URLEncoder.encode(utente,"UTF-8");
            data1 += "&" + URLEncoder.encode("id_ordine","UTF-8")
                    + "=" + URLEncoder.encode(numero_ordine,"UTF-8");
            data1 += "&" + URLEncoder.encode("nome_stazione","UTF-8")
                    + "=" + URLEncoder.encode(stazione,"UTF-8");
            data1 += "&" + URLEncoder.encode("date","UTF-8")
                    + "=" + URLEncoder.encode(date,"UTF-8");
            writer.write(data1);
            writer.flush();
            writer.close();
            out.close();

            InputStream in = client.getInputStream();
            String json_string = LeggiRisposta.readStream(in).trim();

        } catch (MalformedURLException error) {

        } catch (SocketTimeoutException error) {

        } catch (IOException error) {

            error.printStackTrace();
        } finally {
            if (client != null) {
                client.disconnect();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(this).attach(this).commit();
            }
        }
    }
}