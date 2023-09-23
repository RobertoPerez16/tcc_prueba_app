package com.example.prueba_app;

import static com.android.volley.Request.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Listado_clientes extends AppCompatActivity {

    private TextView tv;
    private RequestQueue rq;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_clientes);
        tv = findViewById(R.id.listaClientes);
        rq = Volley.newRequestQueue(this);

        String url = "http://192.168.1.26:7000/api/v1/client";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("respuesta", response);
                    try {

                        JSONObject responseObject = new JSONObject(response);
                        String data = responseObject.get("data").toString();
                        JSONArray resp = new JSONArray(data);

                        if(resp.length() > 0){
                            for (int j = 0; j<resp.length(); j++ ){
                                tv.append("Nombre: "+resp.getJSONObject(j).get("nombre")+"\n");
                                tv.append("Tipo de Identificación: "+resp.getJSONObject(j).get("tipoidentificacion")+"\n");
                                tv.append("Número de identificación: "+resp.getJSONObject(j).get("nro_identificacion")+"\n");
                                tv.append("Género: "+resp.getJSONObject(j).get("genero")+"\n");
                                tv.append("__________________________________________________\n");
                            }

                        }else {
                            Toast.makeText(this,"No hay clientes registrados",Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder alerta = new AlertDialog.Builder(Listado_clientes.this);
                            alerta.setTitle("Notificacion")
                                    .setCancelable(false)
                                    .setMessage("No hay pacientes registrados ")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            alerta.show();
                        }
                    } catch (JSONException e) {
                        Log.d("error", ""+e.getMessage());
                    }

                },
                error -> Log.d("error", error.getMessage())
        );
        rq.add(stringRequest);
    }







}