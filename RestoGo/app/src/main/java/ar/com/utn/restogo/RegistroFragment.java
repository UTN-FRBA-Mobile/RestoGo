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

public class RegistroFragment extends Fragment {

    private RegistroFragmentInteractionListener listener;

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
        }

        if (cancelar) {
            campoConError.requestFocus();
        } else {
            listener.crearCuenta(email, password);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof RegistroFragmentInteractionListener) {
            listener = (RegistroFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RegistroFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface RegistroFragmentInteractionListener {
        void crearCuenta(String email, String password);
    }

}
