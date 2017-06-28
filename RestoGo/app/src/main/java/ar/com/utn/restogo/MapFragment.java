package ar.com.utn.restogo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import ar.com.utn.restogo.modelo.Restaurante;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{

    private static final int RC_PERMISO_LOCALIZACION = 9004;

    private static final String TAG = "MapFragment";
    private static final String ARG_LAT = "ArgLat";
    private static final String ARG_LONG = "ArgLong";

    private LatLng ubicacionPropia;

    private GoogleMap googleMap;
    private MapView mapView;

    // Para poder abrir el restaurante a partir de clickear en el info window del marker
    private Map<Marker, Restaurante> restMarkers = new HashMap<>();

    public static MapFragment newInstance(Double latitud, Double longitud) {
        MapFragment fragment = new MapFragment();

        Bundle args = new Bundle();
        if (latitud != null && longitud != null) {
            args.putDouble(ARG_LAT, latitud);
            args.putDouble(ARG_LONG, longitud);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            double latitud = getArguments().getDouble(ARG_LAT);
            double longitud = getArguments().getDouble(ARG_LONG);
            ubicacionPropia = new LatLng(latitud, longitud);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        mapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(getContext(), getString(R.string.permiso_localizacion), Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_PERMISO_LOCALIZACION);
            }
        } else {
            this.googleMap.setMyLocationEnabled(true);
        }

        // Define la posicion en la que centrar el mapa (la propia o una x)
        CameraUpdate center;
        CameraUpdate zoom;
        if (ubicacionPropia != null) {
            center = CameraUpdateFactory.newLatLng(ubicacionPropia);
            zoom = CameraUpdateFactory.zoomTo(15);
        } else {
            center = CameraUpdateFactory.newLatLng(new LatLng(-34.600509, -58.444553));
            zoom = CameraUpdateFactory.zoomTo(12);
        }

        // Mueve el mapa a la posicion definida anteriormente
        this.googleMap.moveCamera(center);
        this.googleMap.animateCamera(zoom);

        this.googleMap.setOnInfoWindowClickListener(this);
        agregarMarkersRestaurantes();
    }

    private void agregarMarkersRestaurantes() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference restaurantesReference = database.getReference("restaurantes");

        restaurantesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Restaurante restaurante = dataSnapshot.getValue(Restaurante.class);

                // Si tiene la localizacion seteada, agrego un marker al mapa
                if (restaurante.getLatitute() != null && restaurante.getLongitute() != null) {
                    LatLng latLngRestaurante = new LatLng(restaurante.getLatitute(), restaurante.getLongitute());
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(latLngRestaurante)
                            .title(restaurante.getDescripcion()));
                    restMarkers.put(marker, restaurante);
                }
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

            }
        });
    }

    /**
     * Abre el fragment del restaurante al clickear en el popup del marker
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        RestauranteFragment restauranteFragment = RestauranteFragment.newInstance(restMarkers.get(marker));
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, restauranteFragment, "RestauranteFragment")
                .addToBackStack("RestauranteFragment")
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RC_PERMISO_LOCALIZACION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        googleMap.setMyLocationEnabled(true);
                    } catch (SecurityException e) {

                    }
                }
                break;
            default:
                break;
        }
    }
}
