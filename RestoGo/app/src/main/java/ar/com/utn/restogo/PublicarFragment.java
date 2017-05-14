package ar.com.utn.restogo;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;

import ar.com.utn.restogo.modelo.TipoComida;

import static android.app.Activity.RESULT_OK;

public class PublicarFragment extends Fragment {

    private static final int RC_SELECC_UBIC = 9002;

    private TextView txtNombre;
    private TextView txtDireccion;
    private Button btnSeleccUbicac;
    private TextView txtTiposComida;
    private Button btnSeleccTiposComida;

    public PublicarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publicar, container, false);

        txtNombre = (TextView) view.findViewById(R.id.txtNombre);
        txtDireccion = (TextView) view.findViewById(R.id.txtDireccion);
        btnSeleccUbicac = (Button) view.findViewById(R.id.btnSeleccUbicac);
        txtTiposComida = (TextView) view.findViewById(R.id.txtTiposComida);
        btnSeleccTiposComida = (Button) view.findViewById(R.id.btnSeleccTiposComida);

        txtDireccion.setEnabled(false);
        btnSeleccUbicac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccUbicacion();
            }
        });

        txtTiposComida.setEnabled(false);
        btnSeleccTiposComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogTiposComidas();
            }
        });

        return view;
    }

    private void mostrarDialogTiposComidas() {
        AlertDialog dialog;

        final CharSequence[] disponibles = new CharSequence[TipoComida.values().length];
        final ArrayList<TipoComida> seleccs = new ArrayList();

        for (int n = 0; n < TipoComida.values().length; n++) {
            disponibles[n] = TipoComida.values()[n].toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccione tipos de comida");
        builder.setMultiChoiceItems(disponibles, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        TipoComida selecc = TipoComida.values()[indexSelected];

                        if (isChecked) {
                            seleccs.add(selecc);
                        } else if (seleccs.contains(selecc)) {
                            seleccs.remove(selecc);
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String stringTipos = "";
                        for (TipoComida tipo : seleccs) {
                            stringTipos = stringTipos + " ," + tipo.toString();
                        }
                        txtTiposComida.setText(stringTipos);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    private void seleccUbicacion() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .setCountry("ARG")
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter)
                    .build(getActivity());
            startActivityForResult(intent, RC_SELECC_UBIC);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SELECC_UBIC) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                txtDireccion.setText(place.getName());
            }
        }
    }
}
