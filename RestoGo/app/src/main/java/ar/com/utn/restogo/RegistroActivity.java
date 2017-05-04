package ar.com.utn.restogo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistroActivity extends AppCompatActivity
        implements RegistroFragment.RegistroFragmentInteractionListener{

    private static final String TAG = "RegistroActivity";

    private FirebaseAuth auth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        auth = FirebaseAuth.getInstance();

        if(savedInstanceState == null){
            RegistroFragment fragment = new RegistroFragment();
            getSupportFragmentManager().beginTransaction().add(
                    R.id.container_registro, fragment).commit();
        }
    }

    @Override
    public void crearCuenta(String email, String password) {
        progressDialog = ProgressDialog.show(this, getString(R.string.msj_espere),
                getString(R.string.msj_cargando), true);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (progressDialog != null)
                            progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            startActivity(new Intent(RegistroActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistroActivity.this, getString(R.string.error_crear_cuenta),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
