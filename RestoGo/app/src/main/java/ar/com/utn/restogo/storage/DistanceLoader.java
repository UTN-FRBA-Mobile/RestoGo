package ar.com.utn.restogo.storage;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.LocationSource;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar.com.utn.restogo.adapter.RestauranteAdapter;

public class DistanceLoader implements LocationListener{
    public final static DistanceLoader instance = new DistanceLoader();

    private Executor executor = Executors.newFixedThreadPool(2);
    private Handler handler = new Handler(Looper.getMainLooper());
    private Set<RestauranteAdapter.OnNewDistance> loadMap = new HashSet<>();
    private Location lastLocation = null;

    public void loadDistance(final RestauranteAdapter.OnNewDistance resolveLoader) {
        loadMap.add(resolveLoader);
        if (lastLocation != null){
            resolveLoader.onSuccessNewDistance(lastLocation);
        } else {
            resolveLoader.onFailedCalculate();
        }
    }

    public void newLocation(Location location) {
        lastLocation = location;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (RestauranteAdapter.OnNewDistance entry: loadMap) {
                            if (lastLocation != null){
                                entry.onSuccessNewDistance(lastLocation);
                            } else {
                                entry.onFailedCalculate();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        newLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}