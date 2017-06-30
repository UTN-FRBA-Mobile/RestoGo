package ar.com.utn.restogo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ar.com.utn.restogo.conexion.EnvioNotificacion;
import ar.com.utn.restogo.modelo.Reserva;
import ar.com.utn.restogo.modelo.Restaurante;
import butterknife.BindView;

public class ReservarFragment extends Fragment {
    public static final String RESTAURANTE_KEY = "RESTAURANTE_KEY";

    private FirebaseAuth auth;
    private Restaurante restaurante;
    private Integer cantidad;

    private static TextView textDia;
    private ImageButton buttonDia;
    private static TextView textHora;
    private ImageButton buttonHora;
    private ImageButton buttonMinus;
    private TextView textCantidad;
    private ImageButton buttonPlus;
    private Button butttonConfirmar;

    public static ReservarFragment newInstance(Restaurante restaurante) {
        ReservarFragment f = new ReservarFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESTAURANTE_KEY, restaurante);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View output = inflater.inflate(R.layout.fragment_reservar, container, false);
        this.restaurante = (Restaurante) getArguments().getSerializable(RESTAURANTE_KEY);
        return output;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textDia = (TextView) getView().findViewById(R.id.dia_Text);
        buttonDia = (ImageButton) getView().findViewById(R.id.button_dia);
        textHora = (TextView) getView().findViewById(R.id.horaText);
        buttonHora = (ImageButton) getView().findViewById(R.id.button_hora);
        buttonMinus = (ImageButton) getView().findViewById(R.id.button_minus);
        textCantidad = (TextView) getView().findViewById(R.id.text_value);
        buttonPlus = (ImageButton) getView().findViewById(R.id.button_plus);
        butttonConfirmar = (Button) getView().findViewById(R.id.btnConfirmarReserva);

        auth = FirebaseAuth.getInstance();
        cantidad = 1;
        textCantidad.setText(cantidad.toString());

        buttonDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        buttonHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cantidad>1) {
                    cantidad--;
                }
                textCantidad.setText(cantidad.toString());
            }
        });

        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidad++;
                textCantidad.setText(cantidad.toString());
            }
        });

        butttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".contentEquals(textDia.getText().toString())){
                    Toast.makeText(getActivity(), getString(R.string.error_falta_fecha), Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".contentEquals(textHora.getText().toString())){
                    Toast.makeText(getActivity(), getString(R.string.error_falta_hora), Toast.LENGTH_SHORT).show();
                    return;
                }

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                Reserva reserva = new Reserva();
                reserva.setCantidadPersonas(textCantidad.getText().toString());
                reserva.setDia(textDia.getText().toString());
                reserva.setHora(textHora.getText().toString());
                reserva.setUssid(auth.getCurrentUser().getUid());

                database.getReference("reservas/"+restaurante.getKey()).push().setValue(reserva);
                enviarNotificacionReserva(reserva, restaurante);
                getActivity().getSupportFragmentManager().popBackStack("RestauranteFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            textHora.setText(new StringBuilder().append(pad(hourOfDay)).append(":").append(pad(minute)));
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            textDia.setText(new StringBuilder().append(pad(day)).append("/").append(pad(month)).append("/").append(pad(year)));
        }
    }

    private void enviarNotificacionReserva(Reserva reserva, Restaurante restaurante){
        JSONObject json = null;
        try{
            json = new JSONObject();
            json.put("destino", restaurante.getUsuarioRestaurante());
            json.put("origen", reserva.getUssid());
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        Map pedidoCompleto = new HashMap<>();
        pedidoCompleto.put("data", json.toString());
        pedidoCompleto.put("text","¡Tenés un pedido de reserva!.");

        EnvioNotificacion envio = new EnvioNotificacion();
        envio.sendNotificationToUser(pedidoCompleto, getString(R.string.app_name));
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}
