package ar.com.utn.restogo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth auth;
    private GoogleApiClient googleApiClient;

    private ProgressDialog progressDialog;

    private TextView txtEmail;
    private TextView txtPassword;
    private TextView linkNuevaCuenta;
    private Button btnLogin;
    private SignInButton btnGoogleLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        auth = FirebaseAuth.getInstance();
        configGoogleSignInApiClient();

        txtEmail = (TextView) view.findViewById(R.id.txtEmail);
        txtPassword = (TextView) view.findViewById(R.id.txtPassword);
        linkNuevaCuenta = (TextView) view.findViewById(R.id.linkNuevaCuenta);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnGoogleLogin = (SignInButton) view.findViewById(R.id.btnGoogleLogin);

        linkNuevaCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirRegistro();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin();
            }
        });

        return view;
    }

    private void login() {
        boolean cancelar = false;
        View campoConError = null;

        // Resetea msjs de error
        txtEmail.setError(null);
        txtPassword.setError(null);

        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (TextUtils.isEmpty(password)) {
           txtPassword.setError(getString(R.string.error_campo_requerido));
            campoConError = txtPassword;
            cancelar = true;
        }
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getString(R.string.error_campo_requerido));
            campoConError = txtEmail;
            cancelar = true;
        }

        if (cancelar) {
            campoConError.requestFocus();
        } else {
            loginApp(email, password);
        }
    }

    private void googleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void configGoogleSignInApiClient() {
        if (googleApiClient != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestEmail()
                    .build();

            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity(), this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    public void loginApp(String email, String password) {
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.msj_espere),
                getString(R.string.msj_cargando), true);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (progressDialog != null)
                            progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            loginOk();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            errorLogin();
                        }
                    }
                });
    }

    public void abrirRegistro() {
        RegistroFragment registroFragmenent = new RegistroFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, registroFragmenent, "RegistroFragment")
                .addToBackStack("registro")
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            Log.d(TAG, "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                firebaseAuthGoogle(acct);
            } else {
                errorLogin();
            }

        }
    }

    private void firebaseAuthGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            loginOk();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            errorLogin();
                        }
                    }
                });
    }

    private void loginOk() {
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void errorLogin() {
        Toast.makeText(getActivity(), getString(R.string.error_autenticacion), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Error al conectar GoogleAPIClient", Toast.LENGTH_SHORT).show();
    }
}
