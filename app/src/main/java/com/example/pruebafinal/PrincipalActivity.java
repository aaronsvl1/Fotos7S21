package com.example.pruebafinal;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    Spinner splista;
    ImageView ivimagen;
    ArrayAdapter<String> adaptador;

    List<String> nombresList = new ArrayList<>();
    List<String> imagenesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        splista = findViewById(R.id.splista);
        ivimagen = findViewById(R.id.ivimagen);

        fetchGoogleSheetData();
    }

    private void fetchGoogleSheetData() {
        new Thread(() -> {
            try {
                URL url = new URL("https://script.google.com/macros/s/AKfycbwGJOcqZT4H7rc9Lk5sy6yUTKkCjHoi7L0iYGDEXy9Y_xGTwPentDP8jItzJ50IyNfuEQ/exec");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder jsonResponse = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(jsonResponse.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    nombresList.add(jsonObject.getString("nombre"));
                    imagenesList.add(jsonObject.getString("imagen"));
                }

                runOnUiThread(() -> {
                    adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresList);
                    adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    splista.setAdapter(adaptador);

                    splista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (i >= 0) {
                                String estadoSeleccionado = adapterView.getItemAtPosition(i).toString();
                                String imageUrl = imagenesList.get(i);

                                Toast.makeText(PrincipalActivity.this, "Se seleccionó: " + estadoSeleccionado, Toast.LENGTH_SHORT).show();
                                Glide.with(ivimagen).load(imageUrl).into(ivimagen);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}


