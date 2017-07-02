package ar.com.utn.restogo.provieder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import ar.com.utn.restogo.LoginFragment;
import ar.com.utn.restogo.R;
import ar.com.utn.restogo.modelo.FacadeMain;

public class LoginActivity extends AppCompatActivity
        implements FacadeMain, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*ActionBar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            public void onBackStackChanged()
            {
                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                if(backStackCount > 0){
                    showUpButton(true);
                } else {
                    showUpButton(false);
                }
            }
        });

        /*GoogleClient*/
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .requestIdToken(getString(R.string.web_client_id))
                                .build())
                .build();

        if(savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment(), "LoginFragment")
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount >= 1) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUpButton(boolean show) {
        if(show) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            mActionBar.setDisplayHomeAsUpEnabled(false);
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
