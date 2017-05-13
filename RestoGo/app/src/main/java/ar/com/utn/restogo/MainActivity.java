package ar.com.utn.restogo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth auth;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawer;
    private ActionBar mActionBar;
    private boolean mToolBarNavigationListenerIsRegistered = false;
    private NavigationView navigationView;

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

        /*Firebase*/
        auth = FirebaseAuth.getInstance();
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
}
