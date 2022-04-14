package com.example.eventbook.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventbook.MainActivity;
import com.example.eventbook.R;
import com.example.eventbook.ui.pricipal.nav_publicaciones;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EventosAdapter extends RecyclerView.Adapter<EventosHolder> {
    private static final int REQUEST_FOR_ACTIVITY_CODE = 1;
    public static ArrayList<Evento> data;
    static Context context;
    public static int Caller;

    public EventosAdapter(Context context, int Caller){
        this.context = context;
        data= new ArrayList<>();
        this.Caller = Caller;
    }

    @NonNull
    @NotNull
    @Override
    public EventosHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.elemento_evento, parent, false);
        return new EventosHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull @NotNull EventosHolder holder, int position) {

        Evento evento = data.get(position);

        holder.getTvCorreo().setText(evento.getCorreo());
        holder.getTvDescription().setText("Descripción: "+evento.getDescripcion());
        holder.getTvEvento().setText("Evento: "+evento.getEvento());

        if (holder.getTvCorreo().getText().equals(MainActivity.firebaseUser.getEmail())) holder.getTvInscribirme().setVisibility(View.INVISIBLE);

        if (evento.getSuscrito()) {
            holder.getTvInscribirme().setText("Inscrito");
        } else {
            holder.getTvInscribirme().setText("Inscribirme");
        }
        if (evento.getPropietario()) {
            holder.getTvVerMas().setText("Editar");
        }
        Glide.with(context).load(evento.getImagenesEvento().get(0)).into(holder.getImEvento());
        holder.getImEvento().setVisibility(View.VISIBLE);

        if (!evento.getImagenUsuario().equals("")){
            Glide.with(context).load(evento.getImagenUsuario()).into(holder.getImUsuario());
            holder.getImUsuario().setVisibility(View.VISIBLE);
        }
        holder.setEvento(evento);
        holder.setNumEvento(position);
        holder.setCaller(Caller);

        if (data.size()-1 == position && data.size() > 2) {
            holder.getSpaceFinal().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(Evento evento) {
        data.add(data.size(), evento);
        //data.add(evento);
        notifyDataSetChanged();
    }

    public void updateEvento(Evento evento, int numEvento) {
        data.remove(numEvento);
        data.add(evento);
        Evento eventoTest = data.get(data.size()-1);
        data.add(numEvento, eventoTest);
        data.remove(data.size()-1);
        this.notifyDataSetChanged();
    }

    public Evento get(int numEvento) {
        return data.get(numEvento);
    }

    public void vaciar() {
        data.clear();
        notifyDataSetChanged();
    }
}
