package com.example.bikesharing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
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

public class Catalogo extends AppCompatActivity
{
    private ListView lista;
    private Button prenota;
    private String id_u;
    private String nomeStazione;
    UserSessionManager session;


    private class ImageAdapter extends PagerAdapter
    {
        private Context mContext;
        private int[] mImageIds = new int[]{R.drawable.modello3, R.drawable.modello1, R.drawable.modello2};
        ImageAdapter(Context context)
        {
            mContext = context;
        }
        @Override
        public int getCount()
        {
            return mImageIds.length;
        }
        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.imageview, null);
            lista = (ListView) layout.findViewById(R.id.listView);
            prenota = (Button) layout.findViewById(R.id.Prenota);
            ImageView imageView = (ImageView) layout.findViewById(R.id.imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(mImageIds[position]);
            switch(position)
            {
                case 0:
                    inserimento_catalogo(1);
                    prenota.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder a_builder = new AlertDialog.Builder(Catalogo.this);
                            a_builder.setMessage("SI VUOLE CONFERMARE IL PRESTITO?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Toast.makeText(Catalogo.this, "Prestito Effettuato. L'ordine sarà presente nella sezione utente",
                                            Toast.LENGTH_LONG).show();
                                    inserimento_prenotazione(id_u, nomeStazione, 1);
                                    Intent ritornoHome = new Intent (Catalogo.this, Home.class);
                                    ritornoHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(ritornoHome);
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert = a_builder.create();
                            alert.setTitle("PRENOTAZIONE");
                            alert.show();
                            return;

                        }
                    });
                    break;

                case 1:
                    inserimento_catalogo(2);
                    prenota.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder a_builder = new AlertDialog.Builder(Catalogo.this);
                            a_builder.setMessage("SI VUOLE CONFERMARE IL PRESTITO?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Toast.makeText(Catalogo.this, "Prestito Effettuato. L'ordine sarà presente nella sezione utente",
                                            Toast.LENGTH_LONG).show();
                                    inserimento_prenotazione(id_u, nomeStazione, 2);
                                    Intent ritornoHome = new Intent (Catalogo.this, Home.class);
                                    ritornoHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(ritornoHome);
                                }
                            })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which){
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = a_builder.create();
                            alert.setTitle("PRENOTAZIONE");
                            alert.show();
                            return;
                        }
                    });
                    break;


                case 2:
                    inserimento_catalogo(3);
                    prenota.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder a_builder = new AlertDialog.Builder(Catalogo.this);
                            a_builder.setMessage("SI VUOLE CONFERMARE IL PRESTITO?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Toast.makeText(Catalogo.this, "Prestito Effettuato. L'ordine sarà presente nella sezione utente",
                                            Toast.LENGTH_LONG).show();
                                    inserimento_prenotazione(id_u, nomeStazione, 3);
                                    Intent ritornoHome = new Intent (Catalogo.this, Home.class);
                                    ritornoHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(ritornoHome);
                                }
                            })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which){
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = a_builder.create();
                            alert.setTitle("PRENOTAZIONE");
                            alert.show();
                            return;

                        }
                    });
                    break;

            }
            container.addView(layout, 0);
            return layout;
        }
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object)
        {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((ConstraintLayout) object);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getDelegate().getSupportActionBar().hide();
        setContentView(R.layout.activity_catalogo);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id_u = user.get(UserSessionManager.KEY_ID_USER);
        ViewPager viewPager = findViewById(R.id.viewPager);
        ImageAdapter adapter = new ImageAdapter(this);
        viewPager.setAdapter(adapter);

        Intent intent = getIntent();
        nomeStazione = intent.getStringExtra("NomeStazione");
    }

    private void inserimento_catalogo(int posizione) {


        HttpURLConnection client = null;
        try {
            URL url = new URL("http://nicolaricciardi.altervista.org/info_bici.php");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            client.setDoInput(true);
            OutputStream out = new BufferedOutputStream(client.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String id = String.valueOf(posizione);
            String data = URLEncoder.encode("id_bici", "UTF-8")
                    + "=" + URLEncoder.encode(id, "UTF-8");

            writer.write(data);
            writer.flush();
            writer.close();
            out.close();

            InputStream in = client.getInputStream();
            String json_string = LeggiRisposta.readStream(in);
            System.out.println("JSON_STRING: "+json_string);
            JSONObject json_data = convert2JSON(json_string);
            System.out.println("JSON DATA: "+json_data);
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

    private void fill_listview(JSONObject json_data) {
        ArrayList<String> modelloArray = new ArrayList<>();
        ArrayList<String> altezzaArray = new ArrayList<>();
        ArrayList<String> lunghezzaArray = new ArrayList<>();
        ArrayList<String> telaioArray = new ArrayList<>();
        ArrayList<String> freniArray = new ArrayList<>();
        Iterator<String> iter = json_data.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                JSONObject value = json_data.getJSONObject(key);
                modelloArray.add(value.getString("Modello"));
                altezzaArray.add(value.getString("Altezza"));
                lunghezzaArray.add(value.getString("Lunghezza"));
                telaioArray.add(value.getString("Telaio"));
                freniArray.add(value.getString("Freni"));
            } catch (JSONException e) {
                // Something went wrong!
            }
        }
        InserimentoCatalogo listAdapter = new InserimentoCatalogo(this, modelloArray, altezzaArray, lunghezzaArray,
                telaioArray, freniArray);
        lista.setAdapter(listAdapter);
    }

    private JSONObject convert2JSON(String json_data) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(json_data);
            //Log.d("My App", obj.toString());
        } catch (Throwable t) {
            //Log.e("My App", "Could not parse malformed JSON: \"" + json_data + "\"");
        }
        return obj;
    }


    private void inserimento_prenotazione(String utente, String stazione, int posizione)
    {
        HttpURLConnection client = null;
        try {

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            month = month + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String date = month + "/" + day + "/" + year;


            URL url = new URL("http://nicolaricciardi.altervista.org/aggiungi_prenotazione.php");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            client.setDoInput(true);
            OutputStream out = new BufferedOutputStream(client.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String bici = String.valueOf(posizione);
            String data1 = URLEncoder.encode("id_utente","UTF-8")
                    + "=" + URLEncoder.encode(utente,"UTF-8");
            data1 += "&" + URLEncoder.encode("id_stazione","UTF-8")
                    + "=" + URLEncoder.encode(stazione,"UTF-8");
            data1 += "&" + URLEncoder.encode("id_bici","UTF-8")
                    + "=" + URLEncoder.encode(bici,"UTF-8");
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
            }
        }

    }
}
