package ar.com.utn.restogo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.modelo.RestauranteAdapter;

public class RestaurantesFragment extends Fragment {
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_restaurantes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(new RestauranteAdapter(getContext(), getListaRestaurantes()));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private ArrayList<Restaurante> getListaRestaurantes(){
        ArrayList<Restaurante> output = new ArrayList<Restaurante>();
        return output;
    }
}
