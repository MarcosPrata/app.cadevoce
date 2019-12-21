package com.example.cadevoce;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Servidor {


    public static void Sync_data(String email, String latitude, String longitude, final Context context, Response.Listener<JSONObject> listener){
        JSONObject enviar = new JSONObject();
        try {
            enviar.put("email",email);
            enviar.put("lat",latitude);
            enviar.put("lon",longitude);
        } catch (JSONException e) {e.printStackTrace();}

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "http://cadevoce.herokuapp.com/rest/usuarios.php?funcao=sync_data";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, enviar, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Desculpe, ocorreu um erro de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
