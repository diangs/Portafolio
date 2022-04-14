package com.example.eventbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eventbook.ui.Evento;
import com.example.eventbook.ui.EventosHolder;
import com.example.eventbook.ui.pricipal.nav_publicaciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.Inflater;

public class DescripcionEvento extends AppCompatActivity {
    private ImageView im_evento_fila2, im_usuario_fila2;
    private TextView tv_correo_fila2, tv_evento_fila2, tv_description_fila2, tv_fecha_fila2, tv_lugar_fila2, tv_tipo_fila2, tvNunPar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button btnInscribirme2;
    private LinearLayout lila2Imagenes;
    private LayoutInflater inflater;
    private Evento evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descripcion_evento);

        im_evento_fila2 = findViewById(R.id.im_evento_fila2);
        tv_correo_fila2 = findViewById(R.id.tv_correo_fila2);
        tv_evento_fila2 = findViewById(R.id.tv_evento_fila2);
        tv_description_fila2 = findViewById(R.id.tv_description_fila2);
        tv_fecha_fila2 = findViewById(R.id.tv_fecha_fila2);
        tv_lugar_fila2 = findViewById(R.id.tv_lugar_fila2);
        tv_tipo_fila2 = findViewById(R.id.tv_tipo_fila2);
        im_usuario_fila2 = findViewById(R.id.im_usuario_fila2);
        btnInscribirme2 = findViewById(R.id.btnInscribirme2);
        tvNunPar = findViewById(R.id.tv_num_participantes);


        lila2Imagenes = findViewById(R.id.lila2Imagenes);
        inflater = LayoutInflater.from(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        evento = (Evento) intent.getSerializableExtra(EventosHolder.KEY_EVENTO);
        int numEvento = intent.getIntExtra(EventosHolder.KEY_POSITION, -1);
        int Caller = intent.getIntExtra(EventosHolder.KEY_CALLER, -1);

        ArrayList<String> imagenEvento = evento.getImagenesEvento();

        Glide.with(getApplicationContext()).load(imagenEvento.get(0)).into(im_evento_fila2);
        if (!evento.getImagenUsuario().equals("")) Glide.with(getApplicationContext()).load(evento.getImagenUsuario()).into(im_usuario_fila2);
        tv_correo_fila2.setText(evento.getCorreo());
        tv_evento_fila2.setText(evento.getEvento());
        tvNunPar.setText("Participantes: "+(evento.getSuscripciones().size()-1));
        tv_description_fila2.setText(evento.getDescripcionLarga());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy, hh:mm a", new Locale("es"));
        Long i = null;
        i = Long.valueOf(evento.getFecha());
        String fechaHora = simpleDateFormat.format(new Timestamp(i));
        tv_fecha_fila2.setText(fechaHora);
        tv_lugar_fila2.setText(evento.getLugar());
        tv_tipo_fila2.setText(getString(R.string.tipo_evento)+" "+evento.getTipo());

        ArrayList<String> imagenesEvento = evento.getImagenesEvento();
        lila2Imagenes.removeAllViews();
        for (int a = 0; a < imagenesEvento.size(); a++) {
            View view = inflater.inflate(R.layout.item_image_desc, lila2Imagenes, false);

            ImageView imageView = view.findViewById(R.id.imageView);
            Glide.with(getApplicationContext()).load(imagenesEvento.get(a)).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            lila2Imagenes.addView(view);
        }

        if (evento.getSuscrito()) {
            btnInscribirme2.setText("Inscrito");
        } else {
            btnInscribirme2.setText("Inscribirme");
        }

        btnInscribirme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = evento.getEvento();
                String eventoId = evento.getId();

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                db = FirebaseFirestore.getInstance();
                Map<String, Object> inscripcion = new HashMap<>();
                inscripcion.put("eventoID", evento.getId());
                inscripcion.put("correo", user.getEmail());
                if (btnInscribirme2.getText().toString().trim().equals("Inscribirme")) {
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
                                        Toast.makeText(getApplicationContext(), "Te has incrito al evento " + nombre + ".", Toast.LENGTH_SHORT).show();
                                        evento.setSuscrito(true);
                                        btnInscribirme2.setText("Inscrito");
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
                                        btnInscribirme2.setText("Inscribirme");
                                        Toast.makeText(getApplicationContext(), "Te has desuscribido del evento " + nombre + ".", Toast.LENGTH_SHORT).show();
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
        if(tv_correo_fila2.getText().equals(MainActivity.firebaseUser.getEmail())) btnInscribirme2.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        nav_publicaciones.adapter.notifyDataSetChanged();
        finish();
    }
}