package com.example.bikesharing;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class InserimentoCatalogo extends ArrayAdapter<String> {
    private Activity context;
    private ArrayList<String> modello;
    private ArrayList<String> altezza;
    private ArrayList<String> lunghezza;
    private ArrayList<String> telaio;
    private ArrayList<String> freni;


    public InserimentoCatalogo(Activity context, ArrayList<String> modello, ArrayList<String> altezza, ArrayList<String> lunghezza,
                               ArrayList<String> telaio, ArrayList<String> freni)
    {

        super(context,R.layout.listview_layout, modello);
        this.context = context;
        this.modello = modello;
        this.altezza = altezza;
        this.lunghezza = lunghezza;
        this.telaio = telaio;
        this.freni = freni;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_catalogo, null);
        TextView modelloText = (TextView) rowView.findViewById(R.id.valoreModello);
        TextView altezzaText = (TextView) rowView.findViewById(R.id.valoreAltezza);
        TextView lunghezzaText = (TextView) rowView.findViewById(R.id.valoreLunghezza);
        TextView telaioText = (TextView) rowView.findViewById(R.id.valoreTelaio);
        TextView freniText = (TextView) rowView.findViewById(R.id.valoreFreni);
        modelloText.setText(modello.get(position));
        altezzaText.setText(altezza.get(position));
        lunghezzaText.setText(altezza.get(position));
        telaioText.setText(telaio.get(position));
        freniText.setText(freni.get(position));
        return rowView;
    }
}
