package ar.com.utn.restogo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import ar.com.utn.restogo.conexion.ConstructorUrls;
import ar.com.utn.restogo.conexion.TaskListener;
import ar.com.utn.restogo.conexion.TaskRequestUrl;
import ar.com.utn.restogo.provieder.MainActivity;
import ar.com.utn.restogo.provieder.SplashScreenActivity;

public class RegistroFragment extends Fragment implements TaskListener {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    private TextView txtEmail;
    private TextView txtPassword;
    private Button btnCrearCuenta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        txtEmail = (TextView) view.findViewById(R.id.txtEmail);
        txtPassword = (TextView) view.findViewById(R.id.txtPassword);
        btnCrearCuenta = (Button) view.findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearCuenta();
            }
        });

        return view;
    }

    private void crearCuenta() {
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
        } else {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                txtEmail.setError(getString(R.string.error_campo_formato));
                campoConError = txtEmail;
                cancelar = true;
            }
        }

        if (cancelar) {
            campoConError.requestFocus();
        } else {
            crearCuenta(email, password);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void crearCuenta(final String email, String password) {
        progressDialog = ProgressDialog.show(getContext(), getString(R.string.msj_espere),
                getString(R.string.msj_cargando), true);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (task.isSuccessful()) {
                            //Se manda el email y el token asociado al dispositivo al server
                            String token = FirebaseInstanceId.getInstance().getToken();
                            new TaskRequestUrl(RegistroFragment.this).execute(ConstructorUrls.armarURL("Clientes"), ConstructorUrls.getJSONUsuario(auth.getCurrentUser().getUid(), token), "POST");

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String handleExceptionCode(FirebaseAuthException exception){
        switch (exception.getErrorCode()){
            default:
                return exception.getMessage();
        }
    }


    @Override
    public void inicioRequest() {
        //TODO
    }

    @Override
    public void finRequest(JSONObject json) {
        //TODO
    }

}
