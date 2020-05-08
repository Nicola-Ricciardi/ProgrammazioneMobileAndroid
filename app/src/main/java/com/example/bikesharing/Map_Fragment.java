package com.example.bikesharing;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import android.app.Dialog;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
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
import java.util.Iterator;


public class Map_Fragment extends Fragment {
    private MapView mMapView;
    private IMapController mMapController;
    private Dialog markDialog;
    private ListView listView;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);
        mMapView = (MapView) v.findViewById(R.id.mapview);

        setup_map();
        insert_overlays_click();
        return v;
    }

    private void setup_map()
    {
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setUseDataConnection(true);
        mMapController = mMapView.getController();
        mMapController.setZoom(13);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                GeoPoint mapCenter = new GeoPoint(location.getLatitude(), location.getLongitude());
                mMapController.setCenter(mapCenter);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
    }

    private int checkSelfPermission(String accessFineLocation)
    {
        return 0;
    }


    private void insert_overlays_click()
    {
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        GeoPoint overlayPoint = new GeoPoint(43.6170137, 13.5170982);
        items.add(new OverlayItem("Piazza Cavour", "Overlay Description", overlayPoint));

        overlayPoint = new GeoPoint(43.6041719, 13.5076173);
        items.add(new OverlayItem("Piazza Ugo Bassi", "Overlay Description", overlayPoint));

        overlayPoint = new GeoPoint(43.607547, 13.49771);
        items.add(new OverlayItem("Stazione Centrale", "Overlay Description", overlayPoint));

        overlayPoint = new GeoPoint(43.586158, 13.516726);
        items.add(new OverlayItem("Universita", "Overlay Description", overlayPoint));

        overlayPoint = new GeoPoint(43.5527812, 13.5150488);
        items.add(new OverlayItem("Supermercato Auchan", "Overlay Description", overlayPoint));


        DefaultResourceProxyImpl resourceProxy = new DefaultResourceProxyImpl(getActivity());
        ItemizedIconOverlay<OverlayItem> myLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        String nomeStazione = item.getTitle();
                        ShowPopup(nomeStazione);
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, resourceProxy);

        mMapView.getOverlays().add(myLocationOverlay);
        mMapView.invalidate();
    }


    public void ShowPopup(String station)
    {

        final String stazione = station;
        markDialog = new Dialog(getActivity());
        TextView textclose;
        TextView namestation;
        Button buttonBook;
        markDialog.setContentView(R.layout.icon_map_popup);
        textclose = markDialog.findViewById(R.id.closeText);
        namestation = markDialog.findViewById(R.id.station_name);
        listView = (ListView) markDialog.findViewById(R.id.listView);
        namestation.setText(stazione);
        buttonBook = markDialog.findViewById(R.id.buttonBook);

        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SchermataCatalogo = new Intent(getActivity(), Catalogo.class);
                SchermataCatalogo.putExtra("NomeStazione",stazione);
                startActivity(SchermataCatalogo);
            }
        });
        textclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markDialog.dismiss();
            }
        });
        markDialog.show();
        informazioni_stazione(stazione);
    }


    private void informazioni_stazione(String stringa)
    {
        HttpURLConnection client = null;
        try {
            URL url = new URL("http://nicolaricciardi.altervista.org/visualizza_stazioni.php");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            client.setDoInput(true);
            OutputStream out = new BufferedOutputStream(client.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String data = URLEncoder.encode("nome_stazione", "UTF-8")
                    + "=" + URLEncoder.encode(stringa, "UTF-8");
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


    private void fill_listview(JSONObject json_data)
    {
        ArrayList<String> tipo_biciArray = new ArrayList<>();
        ArrayList<String> disponibilita_biciArray = new ArrayList<>();
        Iterator<String> iter = json_data.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                JSONObject value = json_data.getJSONObject(key);
                tipo_biciArray.add(value.getString("Modello"));
                disponibilita_biciArray.add(value.getString("Disponibilita"));
            } catch (JSONException e) {
                // Something went wrong!
            }
        }
        InserimentoPopup listAdapter = new InserimentoPopup(getActivity(), tipo_biciArray, disponibilita_biciArray);
        listView.setAdapter(listAdapter);
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
