package com.danielalonso.ejercicio03;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.danielalonso.ejercicio03.adapter.PokemonListAdapter;
import com.danielalonso.ejercicio03.model.Pokemon;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Perfil extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    private boolean banderaIntentCambiarActivity;

    private ImageView ivPokemon;
    private ImageView type1;
    private ImageView type2;

    private MaterialTextView tvName;
    private MaterialTextView tvExperience;
    private MaterialTextView tvHeight;
    private MaterialTextView tvWeight;

    private ProgressBar pbImage;
    private ObjectAnimator anim;

    String[] types_array;

    String text_type_1;
    String text_type_2;

    Bundle bundle;

    String name;
    String url_perfil;
    String url_imagen;

    Intent intentPlayer;

    public static ImageButton ib_music_perfil;
    public static final int MY_DEFAULT_TIMEOUT = 10000;
    public static final int MY_DEFAULT_MAX_RETRIES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        banderaIntentCambiarActivity = false;
        intentPlayer = new Intent();

        // Inicializa el boton de musica
        ib_music_perfil = (ImageButton) findViewById(R.id.ibMusic_Perfil);

        // Verifica el estado del sonido de fondo de la activity anterior
        Integer tag = (Integer) MainActivity.ibMusic.getTag();

        if(tag == R.drawable.ic_music_on){
            ib_music_perfil.setImageResource(R.drawable.ic_music_on);
            ib_music_perfil.setTag(R.drawable.ic_music_on);
            intentPlayer.setAction(getString(R.string.reproduce_musica));
        } else{
            ib_music_perfil.setImageResource(R.drawable.ic_music_off);
            ib_music_perfil.setTag(R.drawable.ic_music_off);
            intentPlayer.setAction(getString(R.string.pausa_musica));
        }
        sendBroadcast(intentPlayer);

        ib_music_perfil.setOnClickListener(v -> {
            // Cada que se clickea se obtiene el estado de la cancion (encendida o apagada)
            if(intentPlayer.getAction().equals(getString(R.string.reproduce_musica))){
                ib_music_perfil.setImageResource(R.drawable.ic_music_off);
                ib_music_perfil.setTag(R.drawable.ic_music_off);
                MainActivity.ibMusic.setTag(R.drawable.ic_music_off);
                intentPlayer.setAction(getString(R.string.pausa_musica));
                sendBroadcast(intentPlayer);
            }
            else if (intentPlayer.getAction().equals(getString(R.string.pausa_musica))){
                ib_music_perfil.setImageResource(R.drawable.ic_music_on);
                ib_music_perfil.setTag(R.drawable.ic_music_on);
                MainActivity.ibMusic.setTag(R.drawable.ic_music_on);
                intentPlayer.setAction(getString(R.string.reproduce_musica));
                sendBroadcast(intentPlayer);
            }
        });

        // Array que contiene los elementos de los tipos pokemon
        types_array = getResources().getStringArray(R.array.types_array);

        // Text views
        tvName = (MaterialTextView) findViewById(R.id.tvNamePokemon);
        tvExperience = (MaterialTextView) findViewById(R.id.tvExperience);
        tvHeight = (MaterialTextView) findViewById(R.id.tvHeight);
        tvWeight = (MaterialTextView) findViewById(R.id.tvWeight);
        type1 = (ImageView) findViewById(R.id.ivType1);
        type2 = (ImageView) findViewById(R.id.ivType2);


        // Inicializa el progress bar y se le agrega animacion
        pbImage = (ProgressBar) findViewById(R.id.pbImage);
        anim = ObjectAnimator.ofInt(pbImage, getString(R.string.progress), 0, 100);
        anim.start();

        // Inicializa imageView de pokemon
        ivPokemon = (ImageView) findViewById(R.id.ivPokemon);

        bundle = getIntent().getExtras();

        name = bundle.getString(getString(R.string.name));
        tvName.setText(name);
        //tvExperience.setText(experience);
        connect();

        // Asigna la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            case android.R.id.home:
                finish();
                return true;

            case R.id.aboutMe:
                // User chose the "Settings" item, show the app settings UI...
                Intent about = new Intent(this, AcercaDe.class);
                MainActivity.banderaIntentCambiarActivity = true;
                banderaIntentCambiarActivity = true;
                startActivity(about);
                break;

            case R.id.contact:
                Intent contacto = new Intent(this, Contacto.class);
                banderaIntentCambiarActivity = true;
                MainActivity.banderaIntentCambiarActivity = true;
                startActivity(contacto);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!banderaIntentCambiarActivity) {
            intentPlayer.setAction(getString(R.string.pausa_musica));
            sendBroadcast(intentPlayer);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        banderaIntentCambiarActivity = false;
        if ((Integer) ib_music_perfil.getTag() == R.drawable.ic_music_off) {
            intentPlayer.setAction(getString(R.string.pausa_musica));
            MainActivity.ibMusic.setTag(R.drawable.ic_music_off);
            ib_music_perfil.setTag(R.drawable.ic_music_off);
            ib_music_perfil.setImageResource(R.drawable.ic_music_off);
        }
        else if ((Integer) ib_music_perfil.getTag() == R.drawable.ic_music_on) {
            intentPlayer.setAction(getString(R.string.reproduce_musica));
            MainActivity.ibMusic.setTag(R.drawable.ic_music_on);
            ib_music_perfil.setTag(R.drawable.ic_music_on);
            ib_music_perfil.setImageResource(R.drawable.ic_music_on);
        }
        sendBroadcast(MainActivity.intentPlayer);
    }

    //**********************************************//
    // RESPUESTAS DEL SERVIDOR
    //**********************************************//

    public void connect(){
        RequestQueue queue = Volley.newRequestQueue(this);
        url_perfil = bundle.getString(getString(R.string.url_perfil));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url_perfil, null, this, this);
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
        try {
            // Se obtiene la imagen
            url_imagen = response.getJSONObject(getString(R.string.sprites)).
                   getJSONObject(getString(R.string.other)).
                   getJSONObject(getString(R.string.official_artwork)).
                   getString(getString(R.string.front_default));
            // Se carga la imagen en el Image View
           Picasso.with(this)
                   .load(url_imagen)
                   .into(ivPokemon);

           tvExperience.setText(""+response.getInt(getString(R.string.base_experience)));
           tvHeight.setText(""+response.getInt(getString(R.string.height)));
           tvWeight.setText(""+response.getInt(getString(R.string.weight)));

           if(response.getJSONArray(getString(R.string.types)).length() == 1){
               text_type_1 = response.getJSONArray(getString(R.string.types)).
                       getJSONObject(0).
                       getJSONObject(getString(R.string.type)).
                       getString(getString(R.string.name));
               type1.setImageResource(setImagePokemon(text_type_1));
           }
           else if(response.getJSONArray(getString(R.string.types)).length()==2){
               text_type_1 = response.getJSONArray(getString(R.string.types)).
                       getJSONObject(0).
                       getJSONObject(getString(R.string.type)).
                       getString(getString(R.string.name));
               text_type_2 = response.getJSONArray(getString(R.string.types)).
                       getJSONObject(1).
                       getJSONObject(getString(R.string.type)).
                       getString(getString(R.string.name));
               type1.setImageResource(setImagePokemon(text_type_1));
               type2.setImageResource(setImagePokemon(text_type_2));
           }
            //url_pokemon = response.getJSONArray("results").getJSONObject(i).getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        anim.cancel();
        anim.end();
        pbImage.setVisibility(View.GONE);
    }

    public int setImagePokemon(String name){
        int image = 0;
        for (String type : types_array) {
            if (type.equals(name)) {
                image = getResources().getIdentifier(name, getResources().getString(R.string.drawable), getPackageName());
            }
        }
        return image;
    }
}