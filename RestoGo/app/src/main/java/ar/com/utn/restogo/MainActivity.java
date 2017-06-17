package ar.com.utn.restogo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import ar.com.utn.restogo.modelo.FacadeMain;
import ar.com.utn.restogo.modelo.Restaurante;
import ar.com.utn.restogo.storage.DistanceLoader;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FacadeMain, GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth auth;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawer;
    private ActionBar mActionBar;
    private boolean mToolBarNavigationListenerIsRegistered = false;
    private NavigationView navigationView;
    private Location mlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*ActionBar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        /*Menu*/
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState != null){
            resolveUpButtonWithFragmentStack();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RestaurantesFragment(), "RestaurantesFragment")
                    .commit();
        }

        /*GoogleClient*/
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .requestIdToken(getString(R.string.web_client_id))
                                .build())
                .build();

        /*Location Manager*/
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(permissionGranted) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        /*Firebase*/
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(permissionGranted) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        mlocation = location;
                        DistanceLoader.instance.newLocation(location);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = auth.getCurrentUser();
        actualizarDatosUsuario(user);
    }

    private void actualizarDatosUsuario(FirebaseUser user) {
        TextView txtNombreUsuario = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtNombreUsuario);
        TextView txtEmailUsuario = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtEmailUsuario);
        MenuItem itemIniciarSesion = navigationView.getMenu().findItem(R.id.nav_iniciar_sesion);
        MenuItem itemCerrarSesion = navigationView.getMenu().findItem(R.id.nav_cerrar_sesion);

        if (user != null) {
            txtNombreUsuario.setText(user.getDisplayName());
            txtEmailUsuario.setText(user.getEmail());
            itemIniciarSesion.setVisible(false);
            itemCerrarSesion.setVisible(true);
        } else {
            txtNombreUsuario.setText(getString(R.string.nav_nombre_usuario));
            txtEmailUsuario.setText("");
            itemIniciarSesion.setVisible(true);
            itemCerrarSesion.setVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackCount >= 1) {
                getSupportFragmentManager().popBackStack();
                // Change to hamburger icon if at bottom of stack
                if(backStackCount == 1){
                    showUpButton(false);
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mapa) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,new MapFragment())
                    .addToBackStack("mapa")
                    .commit();
            showUpButton(true);
        }
        else if (id == R.id.nav_publicar){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,new PublicarFragment())
                    .addToBackStack("publicar")
                    .commit();
            showUpButton(true);
        } else if (id == R.id.nav_iniciar_sesion) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,new LoginFragment())
                    .addToBackStack("login")
                    .commit();
            showUpButton(true);
        } else if (id == R.id.nav_cerrar_sesion) {
            auth.signOut();
            actualizarDatosUsuario(auth.getCurrentUser());
        } else if (id == R.id.nav_carga_tipo_comida) {
            /*Temporal para cargar un registro*/
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            Restaurante restaurante = new Restaurante();
            restaurante.setDescripcion("La Farola");

            if (mlocation != null) {
                restaurante.setLatitute(mlocation.getLatitude());
                restaurante.setLongitute(mlocation.getLongitude());
            }
            database.getReference("restaurantes").push().setValue(restaurante);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void resolveUpButtonWithFragmentStack() {
        showUpButton(getSupportFragmentManager().getBackStackEntryCount() > 0);
    }

    private void showUpButton(boolean show) {
        if(show) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            if(!mToolBarNavigationListenerIsRegistered) {
                mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
                mToolBarNavigationListenerIsRegistered = true;
            }
        } else {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Error al conectar GoogleAPIClient", Toast.LENGTH_SHORT).show();
    }

    @Override
    public GoogleApiClient getClient() {
        return mGoogleApiClient;
    }
}
