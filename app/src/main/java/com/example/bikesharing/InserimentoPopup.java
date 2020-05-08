package com.example.bikesharing;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class InserimentoPopup extends ArrayAdapter<String> {
    private Activity context;
    private ArrayList<String> tipo;
    private ArrayList<String> numero;

    public InserimentoPopup(Activity context, ArrayList<String> tipo, ArrayList<String> numero)
    {

        super(context,R.layout.listview_layout, tipo);
        this.context = context;
        this.tipo = tipo;
        this.numero = numero;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_layout, null);
        TextView numeroText = (TextView) rowView.findViewById(R.id.tipo_bici);
        TextView provaText = (TextView) rowView.findViewById(R.id.numero_bici);
        numeroText.setText(tipo.get(position));
        provaText.setText(numero.get(position));
        return rowView;
    }
}
