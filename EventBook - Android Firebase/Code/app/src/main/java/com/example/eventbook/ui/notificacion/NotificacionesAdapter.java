package com.example.eventbook.ui.notificacion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventbook.R;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesHolder> {
    public static ArrayList<Notificacion> data;
    private Context context;

    public NotificacionesAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    @NonNull
    @NotNull
    @Override
    public NotificacionesHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.elemento_notificacion, parent, false);
        return new NotificacionesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NotificacionesHolder holder, int position) {
        Notificacion notificacion = data.get(position);

        holder.getTvDescripcionNotificacion().setText(notificacion.getDescripcion());
        holder.getTvTipoNotificacion().setText(notificacion.getTipo());
        holder.setNotificacion(notificacion);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy, hh:mm a", new Locale("es"));
        Long i = null;
        i = notificacion.getFecha();
        String fechaHora = simpleDateFormat.format(new Timestamp(i));
        holder.getTvFecha().setText(fechaHora);
        holder.setPos(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(Notificacion notificacion){
        data.add(notificacion);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
    }

    public void remove(int pos) {
        data.remove(pos);
        notifyDataSetChanged();
    }
}
