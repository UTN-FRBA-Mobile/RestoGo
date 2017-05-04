package ar.com.utn.restogo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {

    private LoginFragmentInteractionListener listener;

    private TextView txtEmail;
    private TextView txtPassword;
    private TextView linkNuevaCuenta;
    private Button btnLogin;
    private SignInButton btnGoogleLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        txtEmail = (TextView) view.findViewById(R.id.txtEmail);
        txtPassword = (TextView) view.findViewById(R.id.txtPassword);
        linkNuevaCuenta = (TextView) view.findViewById(R.id.linkNuevaCuenta);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnGoogleLogin = (SignInButton) view.findViewById(R.id.btnGoogleLogin);

        linkNuevaCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.abrirRegistro();
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
            listener.loginApp(email, password);
        }
    }

    private void googleLogin() {
        listener.loginGoogle();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentInteractionListener) {
            listener = (LoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface LoginFragmentInteractionListener {
        void loginGoogle();
        void loginApp(String email, String password);
        void abrirRegistro();
    }
}
