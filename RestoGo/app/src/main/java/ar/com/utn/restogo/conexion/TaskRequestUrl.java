package ar.com.utn.restogo.conexion;

import android.os.AsyncTask;
import org.json.JSONObject;

public class TaskRequestUrl extends AsyncTask<String, Void, JSONObject> {

    private TaskListener listener;

    public TaskRequestUrl(TaskListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        listener.inicioRequest();
    }

    //Recibe un array de string de parametros del execute():
    // el 0 es la url, el 1 el json y el 2 el metodo (GET/POST)
    @Override
    protected JSONObject doInBackground(String... params) {
        String url = params[0];
        String jsonString = params[1];
        String metodo = params[2];

        String respuesta = Utils.requestUrl(url, jsonString, metodo);
        if(respuesta != null){
            return Utils.parsearAJson(respuesta);
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        listener.finRequest(jsonObject);
    }
}
