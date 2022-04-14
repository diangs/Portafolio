package com.example.eventbook.ui.notificacion;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventbook.PrincipalActivity;
import com.example.eventbook.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class NotificacionesHolder extends RecyclerView.ViewHolder {
    private TextView tvTipoNotificacion, tvDescripcionNotificacion, tvVermasNotificacion, tvDescartarNotificacion, tvFecha;
    private Notificacion notificacion;
    private FirebaseDatabase db;
    private DatabaseReference dbReference;
    private int pos;

    public TextView getTvFecha() {
        return tvFecha;
    }

    public void setTvFecha(TextView tvFecha) {
        this.tvFecha = tvFecha;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
    }

    public TextView getTvTipoNotificacion() {
        return tvTipoNotificacion;
    }

    public void setTvTipoNotificacion(TextView tvTipoNotificacion) {
        this.tvTipoNotificacion = tvTipoNotificacion;
    }

    public TextView getTvDescripcionNotificacion() {
        return tvDescripcionNotificacion;
    }

    public void setTvDescripcionNotificacion(TextView tvDescripcionNotificacion) {
        this.tvDescripcionNotificacion = tvDescripcionNotificacion;
    }

    public TextView getTvVermasNotificacion() {
        return tvVermasNotificacion;
    }

    public void setTvVermasNotificacion(TextView tvVermasNotificacion) {
        this.tvVermasNotificacion = tvVermasNotificacion;
    }

    public TextView getTvDescartarNotificacion() {
        return tvDescartarNotificacion;
    }

    public void setTvDescartarNotificacion(TextView tvDescartarNotificacion) {
        this.tvDescartarNotificacion = tvDescartarNotificacion;
    }

    public NotificacionesHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        tvDescartarNotificacion = itemView.findViewById(R.id.tv_descartar_notificaciones);
        tvDescripcionNotificacion = itemView.findViewById(R.id.tv_descripcion_notificacion);
        tvVermasNotificacion = itemView.findViewById(R.id.tv_ver_mas_notificaciones);
        tvTipoNotificacion = itemView.findViewById(R.id.tv_tipo_notificacion);
        tvFecha = itemView.findViewById(R.id.tv_fecha_notificacion);

        tvDescartarNotificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarNotificacion();
            }
        });

    }

    private void eliminarNotificacion() {
        db = FirebaseDatabase.getInstance("https://eventbook-942c1-default-rtdb.firebaseio.com/");
        Notificacion notificacion = getNotificacion();
        dbReference = db.getReference(PrincipalActivity.correoPart).child(notificacion.getKey());
        dbReference.removeValue();
        PrincipalActivity.actualizarNotificaciones(pos);
    }
}
