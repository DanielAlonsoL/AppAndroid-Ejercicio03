package com.danielalonso.ejercicio03;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.danielalonso.ejercicio03.adapter.PokemonListAdapter;
import com.danielalonso.ejercicio03.model.Pokemon;
import com.danielalonso.ejercicio03.sound.ServiceBackgroundMusic;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject>  {

    public static final int MY_DEFAULT_TIMEOUT = 10000;
    public static final int MY_DEFAULT_MAX_RETRIES = 3;

    static ImageButton ibMusic;

    private ArrayList<Pokemon> pokemones;
    private RecyclerView rvPokemones;
    private PokemonListAdapter pokemonListAdapter;

    public static boolean banderaIntentCambiarActivity;
    public static boolean banderaRegresaActivity;

    Intent intent;
    public static Intent servicioMusica, intentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bandera booleana que indica si cambia de activity
        MainActivity.banderaIntentCambiarActivity = false;
        MainActivity.banderaRegresaActivity = false;

        // Inicia servicio de musica de fondo
        servicioMusica = new Intent(MainActivity.this, ServiceBackgroundMusic.class);
        startService(servicioMusica);

        // Inicializa intent para controlar la musica
        intentPlayer = new Intent();

        // Inicializa el recycler view
        rvPokemones = findViewById(R.id.rvPokemones);

        // Inicializamos el intent para el perfil del pokemon
        intent = new Intent(MainActivity.this, Perfil.class);

        // Asigna la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializa arreglo de pokemones
        pokemones = new ArrayList<>();

        // Se realiza la conexión
        connect();

        // Inicializa el adaptador
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        pokemonListAdapter = new PokemonListAdapter(pokemones, this, intent);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvPokemones.setLayoutManager(linearLayoutManager);

        // Inicializa el button para pausar musica
        ibMusic = (ImageButton) findViewById(R.id.ibMusic);

        // Asigna la accion Reproducir para la musica
        intentPlayer.setAction(getString(R.string.reproduce_musica));
        ibMusic.setTag(R.drawable.ic_music_on);
        sendBroadcast(intentPlayer);

        ibMusic.setOnClickListener(v -> {
            // Cada que se clickea se obtiene el estado de la cancion (encendida o apagada)
            if(intentPlayer.getAction().equals(getString(R.string.reproduce_musica))){
                ibMusic.setImageResource(R.drawable.ic_music_off);
                ibMusic.setTag(R.drawable.ic_music_off);
                MainActivity.intentPlayer.setAction(getString(R.string.pausa_musica));
                sendBroadcast(MainActivity.intentPlayer);
                //Toast.makeText(MainActivity.this, ""+intentPlayer.getAction(), Toast.LENGTH_SHORT).show();
            }
            else if (MainActivity.intentPlayer.getAction().equals(getString(R.string.pausa_musica))){
                ibMusic.setImageResource(R.drawable.ic_music_on);
                ibMusic.setTag(R.drawable.ic_music_on);
                MainActivity.intentPlayer.setAction(getString(R.string.reproduce_musica));
                sendBroadcast(MainActivity.intentPlayer);
                //Toast.makeText(MainActivity.this, ""+intentPlayer.getAction(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeAdaptor() {
        PokemonListAdapter adapter = new PokemonListAdapter(pokemones, this, intent);
        rvPokemones.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.aboutMe:
                // User chose the "Settings" item, show the app settings UI...
                Intent about = new Intent(this, AcercaDe.class);
                MainActivity.banderaIntentCambiarActivity = true;
                startActivity(about);
                break;

            case R.id.contact:
                Intent contacto = new Intent(this, Contacto.class);
                MainActivity.banderaIntentCambiarActivity = true;
                startActivity(contacto);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //**********************************************//
    // CICLO DE VIDA DEL ACITIVITY
    //**********************************************//

    @Override
    protected void onRestart() {
        super.onRestart();

        MainActivity.banderaIntentCambiarActivity = false;

        if ((Integer) ibMusic.getTag() == R.drawable.ic_music_off) {
            MainActivity.intentPlayer.setAction(getString(R.string.pausa_musica));
            ibMusic.setTag(R.drawable.ic_music_off);
            ibMusic.setImageResource(R.drawable.ic_music_off);
        }
        else if ((Integer) ibMusic.getTag() == R.drawable.ic_music_on) {
            MainActivity.intentPlayer.setAction(getString(R.string.reproduce_musica));
            ibMusic.setTag(R.drawable.ic_music_on);
            ibMusic.setImageResource(R.drawable.ic_music_on);
        }
        sendBroadcast(MainActivity.intentPlayer);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(!MainActivity.banderaIntentCambiarActivity) {
            intentPlayer.setAction(getString(R.string.pausa_musica));
            ibMusic.setImageResource(R.drawable.ic_music_off);
            sendBroadcast(intentPlayer);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        intentPlayer.setAction(getString(R.string.pausa_musica));
        sendBroadcast(intentPlayer);
    }

    //**********************************************//
    // RESPUESTAS DEL SERVIDOR
    //**********************************************//

    private void connect() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.base_url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                MY_DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.title_error))
                .setMessage(getString(R.string.supporting_text))
                .setNeutralButton(getString(R.string.try_again),(dialog, which) ->
                        connect())
                .setPositiveButton(getString(R.string.exit),(dialog, which) ->
                        finish())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onResponse(JSONObject response) {
        //Obtiene recursos de String
        String url_perfil = getString(R.string.url_perfil);
        String results = getString(R.string.results);
        String name = getString(R.string.name);

        try {
            // Por cada pokemon, se crea y agrega un objeto de tipo Pokemon
            for(int index=0; index<response.getJSONArray(results).length(); index++){
                pokemones.add(new Pokemon(
                        index,
                        response.getJSONArray(results).getJSONObject(index).getString(name).toUpperCase(),
                        response.getJSONArray(results).getJSONObject(index).getString(url_perfil)
                ));
            }
            // Inicializa adaptador con el arreglo de Pokemones lleno
            initializeAdaptor();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}