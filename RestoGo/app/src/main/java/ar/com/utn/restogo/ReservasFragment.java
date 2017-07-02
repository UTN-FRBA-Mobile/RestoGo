package ar.com.utn.restogo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

import ar.com.utn.restogo.adapter.ReservaAdapter;
import ar.com.utn.restogo.adapter.RestauranteAdapter;
import ar.com.utn.restogo.conexion.Utils;
import ar.com.utn.restogo.modelo.Reserva;
import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.modelo.TipoComida;

public class ReservasFragment extends Fragment {
    private String TAG = "ReservasFragment";

    private FirebaseAuth auth;
    private Boolean firstCall = true;
    private RelativeLayout mLoadingView;
    private RecyclerView mRecyclerView;

    private ReservaAdapter mAdapter;
    private RecyclerView.AdapterDataObserver observer;
    private FirebaseDatabase database;
    private DatabaseReference proviederReference;
    private HashMap<String, Reserva> reservas = new HashMap<String, Reserva>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_reservas, container, false);
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
        mAdapter = new ReservaAdapter(getContext(), getActivity().getSupportFragmentManager());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        auth = FirebaseAuth.getInstance();
        auth.getCurrentUser();
        mLoadingView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mLoadingView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        };
        mAdapter.registerAdapterDataObserver(observer);
        database = FirebaseDatabase.getInstance();

        proviederReference = database.getReference("provieder/"+auth.getCurrentUser().getUid()+"/");
        proviederReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String proviederKey = dataSnapshot.getValue(String.class);
                add(proviederKey);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
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

    public void add(String key){
        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mLoadingView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        };
        DatabaseReference reservasReference = database.getReference("reservas/" + key + "/");
        reservasReference.orderByChild("fueRespondida").equalTo(false);
        reservasReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Reserva reserva = dataSnapshot.getValue(Reserva.class);
                String reservaKey = dataSnapshot.getKey();
                mAdapter.add(reservaKey, reserva);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
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
}
