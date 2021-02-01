package com.danielalonso.ejercicio03.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.danielalonso.ejercicio03.MainActivity;
import com.danielalonso.ejercicio03.R;
import com.danielalonso.ejercicio03.model.Pokemon;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;

public class PokemonListAdapter extends RecyclerView.Adapter<PokemonListAdapter.PokemonViewHolder>{

    private final ArrayList<Pokemon> pokemones;
    private final Context context;


    Pokemon pokemon;
    final Intent intent;

    //Constructor
    public PokemonListAdapter(ArrayList<Pokemon> pokemones, Context context, Intent intent) {
        this.pokemones = pokemones;
        this.context = context;
        this.intent = intent;
    }

    //Inflar el layout y lo pasara al viewHolder para obtener los views
    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pokemon, parent, false);
        return new PokemonViewHolder(v);
    }

    //Asocia cada elemento de la lista con cada View
    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder pokemonViewHolder, int position) {
        // Obtenemos cada uno de los pokemones
        pokemon = pokemones.get(position);

        int id = pokemon.getId();
        String name = pokemon.getName();
        String url = pokemon.getUrl();

        pokemonViewHolder.tvNombre.setText(""+name);
        pokemonViewHolder.tvNumero.setText("#" + (id + 1));

        // Cada que se de clic en un pokemon
        pokemonViewHolder.cvPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.banderaIntentCambiarActivity = true;
                // Pasamos a la otra actividad el nombre y su url del pokemon
                intent.putExtra(context.getString(R.string.name), name);
                intent.putExtra(context.getString(R.string.url_perfil), url);
                context.startActivity(intent);
            }
        });
    }

    //cantidad de elementos que contiene mi lista
    @Override
    public int getItemCount() {
        return pokemones.size();
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder{

        private final MaterialTextView tvNombre;
        private final MaterialTextView tvNumero;
        private final CardView cvPokemon;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = (MaterialTextView) itemView.findViewById(R.id.tvNombre);
            tvNumero = (MaterialTextView) itemView.findViewById(R.id.tvNumero);
            cvPokemon = (CardView) itemView.findViewById(R.id.cvPokemon);

        }
    }
}
