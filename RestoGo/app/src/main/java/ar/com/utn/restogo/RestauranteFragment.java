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

public class RestauranteFragment extends Fragment {
    public static final String RESTAURANTE_KEY = "RESTAURANTE_KEY";
    private Restaurante restaurante;

    public static RestauranteFragment newInstance(Restaurante restaurante) {
        RestauranteFragment f = new RestauranteFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(RESTAURANTE_KEY, restaurante);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View output = inflater.inflate(R.layout.fragment_restaurante, container, false);
        this.restaurante = (Restaurante) getArguments().getSerializable(RESTAURANTE_KEY);
        return output;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
