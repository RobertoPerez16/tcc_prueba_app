package com.example.prueba_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button btn;

    private Button btn_guardar, btn_eliminar_o_actualizar;

    private EditText txtNombre, txtCedula;

    private Spinner sp;

    private RequestQueue rq;

    private RadioButton radio1, radio2, radio3;

    public String tipoIdentificacion, genero, nombre, idNumero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rq = Volley.newRequestQueue(this);

        btn = findViewById(R.id.ver_listado);
        btn_eliminar_o_actualizar = findViewById(R.id.editar_o_eliminar);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Spinner spinner = (Spinner) findViewById(R.id.tipoidentificacion);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.opciones, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        radio1 = findViewById(R.id.masculino);
        radio2 = findViewById(R.id.femenino);
        radio3 = findViewById(R.id.otro);

        spinner.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Listado_clientes.class);
                startActivity(i);
            }
        });

        btn_eliminar_o_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpdateOrDelete.class);
                startActivity(intent);
            }
        });


        btn_guardar = findViewById(R.id.guardar);

        // datos de entrada //
        txtCedula = findViewById(R.id.numeroIdentificacion);
        txtNombre = findViewById(R.id.nombre);

        this.nombre = txtNombre.getText().toString();
        this.idNumero = txtCedula.getText().toString();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoIdentificacion = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://192.168.1.26:3000/api/v1/client";

                if (radio1.isChecked()) {
                    genero = "M";
                } else if (radio2.isChecked()) {
                    genero = "F";

                } else if (radio3.isChecked()) {
                    genero = "O";
                }

                JSONObject request = new JSONObject();
                try {
                    request.put("name", txtNombre.getText().toString());
                    request.put("identificationType",tipoIdentificacion);
                    request.put("identificationNumber", txtCedula.getText().toString());
                    request.put("gender", genero);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JsonObjectRequest sendRequest = new JsonObjectRequest(Request.Method.POST,
                        url,
                        request,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.get("message").toString().equals("Client created")) {
                                        Toast.makeText(MainActivity.this,"El cliente se creó correctamente" , Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "No se pudo crear el cliente", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "No se pudo crear el cliente", Toast.LENGTH_SHORT).show();
                            }
                        });

                rq.add(sendRequest);
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