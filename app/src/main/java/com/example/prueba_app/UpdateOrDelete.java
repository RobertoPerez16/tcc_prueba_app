package com.example.prueba_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateOrDelete extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // EditText

    private EditText txtBusqueda, txtNombre;
    private Button buscarCliente, btnEditarCliente, btnEliminarCliente;

    private RadioButton radioMasculino, radioFemenino, radioOtro;

    private Spinner sp;
    private RequestQueue rq;

    public String [] listaTipoIdentificacion = {
            "CC", "CE", "TI", "NIT", "RUC"
    };

    private String tipoIdentificacion;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_or_delete);
        sp = findViewById(R.id.tipoIdentificacion);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                listaTipoIdentificacion
        );
        sp.setAdapter(adapter);


        // Edit text
        txtBusqueda = findViewById(R.id.txtBusqueda);
        txtNombre = findViewById(R.id.txtNombre);

        // Buttons //
        buscarCliente = findViewById(R.id.buscarCliente);
        btnEditarCliente = findViewById(R.id.btnEditarCliente);
        btnEliminarCliente = findViewById(R.id.btnEliminarCliente);

        // RadioButtons //
        radioMasculino = findViewById(R.id.radio_masculino);
        radioFemenino = findViewById(R.id.radio_femenino);
        radioOtro = findViewById(R.id.radio_otro);

        rq = Volley.newRequestQueue(this);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoIdentificacion = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        // Events //
        buscarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String busqueda = txtBusqueda.getText().toString();

                String url = "http://192.168.1.26:7000/api/v1/client/search?identificationNumber="+busqueda;

                if (busqueda.equals("")) {
                    Toast.makeText(UpdateOrDelete.this, "Este campo es requerido", Toast.LENGTH_SHORT).show();
                } else {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                            url,
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("Response: ", response.toString());

                                    try {
                                        JSONObject responseObject = new JSONObject(response.toString());
                                        String data = responseObject.get("data").toString();
                                        JSONArray resp = new JSONArray(data);
                                        Log.d("Response complete: ", resp.toString());
                                        if (resp.length() > 0 ) {
                                            for (int j = 0; j < resp.length(); j++) {
                                                txtNombre.setText(resp.getJSONObject(j).get("nombre").toString());
                                                if (resp.getJSONObject(j).get("genero").equals("M") || resp.getJSONObject(j).get("genero").equals("Masculino")) {
                                                    radioMasculino.setChecked(true);
                                                } else if (resp.getJSONObject(j).get("genero").equals("F") || resp.getJSONObject(j).get("genero").equals("Femenino")) {
                                                    radioFemenino.setChecked(true);
                                                } else if (resp.getJSONObject(j).get("genero").equals("O") || resp.getJSONObject(j).get("genero").equals("Otro")) {
                                                    radioOtro.setChecked(true);
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(UpdateOrDelete.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                    );

                    rq.add(jsonObjectRequest);
                }


            }
        });

        btnEditarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String busqueda = txtBusqueda.getText().toString();
                if (busqueda.equals("")) {
                    Toast.makeText(UpdateOrDelete.this, "No ha ingresado un número de identificación", Toast.LENGTH_SHORT).show();
                } else {
                    String url = "http://192.168.1.26:3000/api/v1/client/"+busqueda;
                    JSONObject request = new JSONObject();

                    String genero = "";

                    if (radioMasculino.isChecked()) {
                        genero = "M";
                    } else if (radioFemenino.isChecked()) {
                        genero = "F";
                    } else if (radioOtro.isChecked()) {
                        genero = "O";
                    }


                    try {
                        request.put("name", txtNombre.getText().toString());
                        request.put("identificationType",tipoIdentificacion);
                        request.put("identificationNumber", txtBusqueda.getText().toString());
                        request.put("gender", genero);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
                            url,
                            request,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    int statusCode = 0;
                                    try {
                                        statusCode = Integer.parseInt(response.get("statusCode").toString());
                                        if (statusCode == 200) {
                                            Toast.makeText(UpdateOrDelete.this, "Cliente editado correctamente", Toast.LENGTH_SHORT).show();
                                        }

                                        if (statusCode == 400) {
                                            Toast.makeText(UpdateOrDelete.this, "Error al editar", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(UpdateOrDelete.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                    rq.add(jsonObjectRequest);
                }
            }
        });

        btnEliminarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String busqueda = txtBusqueda.getText().toString();
                if (busqueda.equals("")) {
                    Toast.makeText(UpdateOrDelete.this, "No ha ingresado un número de identificación", Toast.LENGTH_SHORT).show();
                } else {
                    String url = "http://192.168.1.26:3000/api/v1/client/"+busqueda;

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,
                            url,
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String message = response.get("message").toString();
                                        int statusCode = Integer.parseInt(response.get("statusCode").toString());
                                        if (statusCode == 200) {
                                            Toast.makeText(UpdateOrDelete.this, "Cliente elminado correctamente", Toast.LENGTH_SHORT).show();
                                        }

                                        if (statusCode == 400) {
                                            Toast.makeText(UpdateOrDelete.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(UpdateOrDelete.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                    rq.add(jsonObjectRequest);
                }

            }
        });

    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}