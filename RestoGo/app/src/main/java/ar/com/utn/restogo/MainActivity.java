package ar.com.utn.restogo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
            itemIniciarSesion.setEnabled(false);
            itemCerrarSesion.setEnabled(true);
        } else {
            txtNombreUsuario.setText(getString(R.string.nav_nombre_usuario));
            txtEmailUsuario.setText("");
            itemIniciarSesion.setEnabled(true);
            itemCerrarSesion.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mapa) {
            cargarFragment(MapFragment.newInstance());
        } else if (id == R.id.nav_iniciar_sesion) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_cerrar_sesion) {
            auth.signOut();
            actualizarDatosUsuario(auth.getCurrentUser());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void cargarFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
    }
}
