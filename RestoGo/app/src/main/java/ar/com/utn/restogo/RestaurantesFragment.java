package ar.com.utn.restogo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import ar.com.utn.restogo.adapter.RestauranteAdapter;
import ar.com.utn.restogo.conexion.Utils;
import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.modelo.TipoComida;

public class RestaurantesFragment extends Fragment {
    private String TAG = "RestaurantesFragment";
    private RelativeLayout mLoadingView;
    private RecyclerView mRecyclerView;
    private RestauranteAdapter mAdapter;
    private FirebaseDatabase database;
    private DatabaseReference restaurantesReference;
    private HashMap<String, Restaurante> restaurantes = new HashMap<String, Restaurante>();

    /*Location*/
    private Location mLastLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_restaurantes, container, false);

        Button btnFiltroComidas = (Button) view.findViewById(R.id.btnFiltroComidas);
        btnFiltroComidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirFiltroComidas();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!Utils.conexionAInternetOk(getActivity())) {
            Toast.makeText(getContext(), getString(R.string.error_internet), Toast.LENGTH_LONG).show();
            //Deja que siga porque si vuelve la conexion firebase retoma
        }

        mLoadingView = (RelativeLayout) view.findViewById(R.id.loadingPanel);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mAdapter = new RestauranteAdapter(getContext(), getActivity().getSupportFragmentManager());
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mLoadingView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /*DataBase*/
        database = FirebaseDatabase.getInstance();
        restaurantesReference = database.getReference("restaurantes");
        restaurantesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Restaurante restaurante = dataSnapshot.getValue(Restaurante.class);
                String restauranteKey = dataSnapshot.getKey();
                restaurante.setKey(restauranteKey);
                mAdapter.add(restauranteKey, restaurante);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Restaurante restaurante = dataSnapshot.getValue(Restaurante.class);
                String restauranteKey = dataSnapshot.getKey();
                restaurante.setKey(restauranteKey);
                mAdapter.update(restauranteKey, restaurante);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String restauranteKey = dataSnapshot.getKey();
                mAdapter.delete(restauranteKey);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load comments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirFiltroComidas() {
        AlertDialog dialog;

        final int cantTiposComidas = TipoComida.values().length;

        final CharSequence[] disponibles = new CharSequence[cantTiposComidas];
        final ArrayList<TipoComida> seleccs = new ArrayList();

        for (int n = 0; n < cantTiposComidas; n++) {
            disponibles[n] = TipoComida.values()[n].toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_selecc_comidas));
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
                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mAdapter.setTiposDeComidaFiltro(seleccs);
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        dialog = builder.create();
        dialog.show();
    }
}
