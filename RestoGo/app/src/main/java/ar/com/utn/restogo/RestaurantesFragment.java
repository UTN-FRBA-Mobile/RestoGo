package ar.com.utn.restogo;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import ar.com.utn.restogo.adapter.RestauranteAdapter;
import ar.com.utn.restogo.modelo.Restaurante;

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
        return inflater.inflate(R.layout.fragment_restaurantes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadingView = (RelativeLayout) view.findViewById(R.id.loadingPanel);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mAdapter = new RestauranteAdapter(getContext());
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
                mAdapter.add(restauranteKey, restaurante);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Restaurante restaurante = dataSnapshot.getValue(Restaurante.class);
                String restauranteKey = dataSnapshot.getKey();
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
}
