package com.example.eventbook.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventbook.DescripcionEvento;
import com.example.eventbook.EditarEvento;
import com.example.eventbook.MainActivity;
import com.example.eventbook.PrincipalActivity;
import com.example.eventbook.R;
import com.example.eventbook.ui.notificacion.Notificacion;
import com.example.eventbook.ui.pricipal.nav_publicaciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

public class EventosHolder extends RecyclerView.ViewHolder {
    public static final String KEY_EVENTO = "key_evento";
    public static final String KEY_POSITION = "key_position";
    public static final String KEY_CALLER = "key_caller";
    private final TextView tvCorreo;
    private final TextView tvEvento;
    private final TextView tvDescription;
    private final ImageView imUsuario;
    private final ImageView imEvento;
    public Space spaceFinal;
    public Evento evento;
    private TextView tvInscribirme;
    private TextView tvVerMas;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int numEvento;
    private int Caller;
    private FirebaseDatabase dbs;
    private DatabaseReference dbReference;

    public EventosHolder(@NonNull @NotNull View itemView) {
        super(itemView);


        dbs = FirebaseDatabase.getInstance("https://eventbook-942c1-default-rtdb.firebaseio.com/");
        tvCorreo = itemView.findViewById(R.id.tv_correo_fila);
        tvEvento = itemView.findViewById(R.id.tv_evento_fila);
        tvDescription = itemView.findViewById(R.id.tv_description_fila);
        spaceFinal = itemView.findViewById(R.id.space_final);
        imEvento = itemView.findViewById(R.id.im_evento_fila);
        imUsuario = itemView.findViewById(R.id.im_usuario_fila);

        tvInscribirme = itemView.findViewById(R.id.tvInscribirme);
        tvVerMas = itemView.findViewById(R.id.tvVerMas);

        tvInscribirme.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                evento = getEvento();
                String nombre = evento.getEvento();
                String eventoId = evento.getId();

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                db = FirebaseFirestore.getInstance();
                Map<String, Object> inscripcion = new HashMap<>();
                inscripcion.put("eventoID", evento.getId());
                inscripcion.put("correo", user.getEmail());
                if (tvInscribirme.getText().toString().trim().equals("Inscribirme")) {
                    addNotify(evento,itemView);
                    db.collection("eventos").document(eventoId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    ArrayList<String> suscripciones = new ArrayList<String>();
                                    suscripciones = (ArrayList<String>) documentSnapshot.get("suscripciones");
                                    suscripciones.add(user.getEmail());
                                db.collection("eventos").document(eventoId).update("suscripciones",suscripciones).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        Toast.makeText(itemView.getContext(), "Te has incrito al evento " + nombre + ".", Toast.LENGTH_SHORT).show();
                                        evento.setSuscrito(true);
                                        tvInscribirme.setText("Inscrito");
                                        PrincipalActivity.updateEventos(Caller, evento, numEvento);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    db.collection("eventos").document(eventoId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                ArrayList<String> suscripciones = new ArrayList<String>();
                                suscripciones = (ArrayList<String>) documentSnapshot.get("suscripciones");
                                suscripciones.remove(mAuth.getCurrentUser().getEmail());
                                db.collection("eventos").document(eventoId).update("suscripciones",suscripciones).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        tvInscribirme.setText("Inscribirme");
                                        Toast.makeText(itemView.getContext(), "Te has desuscribido del evento " + nombre + ".", Toast.LENGTH_SHORT).show();
                                        evento.setSuscrito(false);
                                        PrincipalActivity.updateEventos(Caller, evento, numEvento);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        tvVerMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvVerMas.getText().toString().trim().equals("Editar")) {
                    context = itemView.getContext();
                    Intent intent = new Intent(context.getApplicationContext(), EditarEvento.class);
                    intent.putExtra(KEY_EVENTO, getEvento());
                    intent.putExtra(KEY_POSITION, numEvento);
                    intent.putExtra(KEY_CALLER, Caller);
                    ((Activity) (context)).startActivityForResult(intent, 2);
                } else {
                    context = itemView.getContext();

                    Intent intent = new Intent(context.getApplicationContext(), DescripcionEvento.class);
                    intent.putExtra(KEY_EVENTO, getEvento());
                    intent.putExtra(KEY_POSITION, numEvento);
                    intent.putExtra(KEY_CALLER, Caller);
                    ((Activity) (context)).startActivityForResult(intent, 1);
                }
            }
        });
    }

    private void addNotify(Evento evento, View view) {
        Notificacion notificacion = new Notificacion();
        notificacion.setDescripcion("El usuario "+ MainActivity.firebaseUser.getEmail() +" se ha suscrito a tu evento "+ evento.getEvento());
        notificacion.setTipo("Inscripción");
        notificacion.setFecha(System.currentTimeMillis());
        String[] arCorreo = evento.getCorreo().split("[.]");
        notificacion.setRemitente(MainActivity.firebaseUser.getEmail());
        dbReference = dbs.getReference(arCorreo[0]);
        dbReference.push().setValue(notificacion);
    }



    public Space getSpaceFinal() {
        return spaceFinal;
    }

    public void setSpaceFinal(Space spaceFinal) {
        this.spaceFinal = spaceFinal;
    }

    public int getCaller() {
        return Caller;
    }

    public void setCaller(int caller) {
        Caller = caller;
    }

    public int getNumEvento() {
        return numEvento;
    }

    public void setNumEvento(int numEvento) {
        this.numEvento = numEvento;
    }

    public TextView getTvVerMas() {
        return tvVerMas;
    }

    public void setTvVerMas(TextView tvVerMas) {
        this.tvVerMas = tvVerMas;
    }

    public TextView getTvInscribirme() {
        return tvInscribirme;
    }

    public void setTvInscribirme(TextView tvInscribirme) {
        this.tvInscribirme = tvInscribirme;
    }

    public TextView getTvCorreo() {
        return tvCorreo;
    }

    public TextView getTvEvento() {
        return tvEvento;
    }

    public TextView getTvDescription() {
        return tvDescription;
    }

    public ImageView getImUsuario() {
        return imUsuario;
    }

    public ImageView getImEvento() {
        return imEvento;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}