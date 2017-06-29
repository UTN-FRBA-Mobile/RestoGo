package ar.com.utn.restogo.conexion;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EnvioNotificacion implements TaskListener  {

    private Map<String, String>  message;
    private String titulo;
    private String email;

    //Se va a usar cuando el Cliente haga un pedido y ademas cuando el Resurante le de el OK o no por el mismo
    public void sendNotificationToUser(Map<String, String> message, String titulo) {
        this.message = message;
        this.titulo = titulo;
        try {
            email = Utils.parsearAJson(message.get("data")).get("destino").toString();
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        ejecutarGetToken(email);
    }

    private void ejecutarGetToken(String email){
        new TaskRequestUrl(EnvioNotificacion.this).execute(ConstructorUrls.armarUrlGetToken("Clientes", email), null, "GET");
    }

    @Override
    public void inicioRequest() {}

    @Override
    public void finRequest(JSONObject json) {
        if(json != null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference notifications = ref.child("notificationRequests");
            Map notification = new HashMap<>();
            try{
                notification.put("message", message);
                notification.put("titulo",titulo);
                notification.put("token", json.get("token"));
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            notifications.push().setValue(notification);
        }
    }
}
