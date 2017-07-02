package ar.com.utn.restogo.conexion;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ConstructorUrls {

    private static final String PROTOCOLO = "http";
    public static String urlBase = "192.168.0.15:3000";

    public static String armarURL(String path){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PROTOCOLO)
                .encodedAuthority(urlBase)
                .appendPath(path);

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }

    public static String getJSONUsuario(String ussid, String token){
        JSONObject json = null;
        try{json = new JSONObject();
            json.put("ussid",ussid);
            json.put("token",token);
        }
        catch(JSONException je){
            je.printStackTrace();
        }
        return json.toString();
    }

    public static String armarUrlGetToken(String path, String ussid){
        Uri.Builder builder = new Uri.Builder();

            builder.scheme(PROTOCOLO)
                    .encodedAuthority(urlBase)
                    .appendPath(path)
                    .appendPath(ussid);

        String url = builder.build().toString();
        Log.d("ConstructorUrls", url);

        return url;
    }

}
