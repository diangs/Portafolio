package com.example.eventbook;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventbook.databinding.ActivityPrincipalBinding;
import com.example.eventbook.ui.Evento;
import com.example.eventbook.ui.EventosAdapter;
import com.example.eventbook.ui.EventosHolder;
import com.example.eventbook.ui.NuevoEvento;
import com.example.eventbook.ui.eventos.nav_mis_eventos;
import com.example.eventbook.ui.inscripciones.nav_inscripciones;
import com.example.eventbook.ui.notificacion.Notificacion;
import com.example.eventbook.ui.notificacion.nav_notificaciones;
import com.example.eventbook.ui.pricipal.nav_publicaciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

public class PrincipalActivity extends AppCompatActivity {

    public static final int CODE_NEW_EVENT = 1;
    private static final String CHANNEL_ID = "NOTIFICACION";
    public static ImageView im_foto_usuario_header;
    static Context context;
    static ActivityPrincipalBinding binding;
    static TextView correoHeader, nombreHeader;
    static FirebaseFirestore db;
    static String imagenUsuario, nombreUsuario;
    static FirebaseAuth mAuth;
    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferences sharedPreferences;
    public static String correoPart;
    public static String filtro = "";

    public static void updateEventos(int caller, Evento evento, int numEvento) {
        switch (caller) {
            case 0:
                nav_publicaciones.updateEventos(evento, numEvento);
                break;
            case 1:
                nav_mis_eventos.updateEventos(evento, numEvento);
                break;
            case 2:
                nav_inscripciones.updateEventos(evento, numEvento);
                break;
        }
    }

    public static void addEventos(int caller) {
        switch (caller) {
            case 0:
                nav_publicaciones.addEventos();
                break;
            case 1:
                nav_mis_eventos.addEventos();
                break;
            case 2:
                nav_inscripciones.addEventos();
                break;
        }
    }

    private static String nombreEImagenUsuario() {

        String nombre = "nombre";

        db
                .collection("usuarios")
                .whereEqualTo("correo", mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                imagenUsuario = document.getString("imagen");
                                nombreUsuario = document.getString("nombre");
                                nombreHeader.setText(nombreUsuario);
                                if (!document.getString("imagen").isEmpty())
                                    Glide.with(context).load(imagenUsuario).into(im_foto_usuario_header);
                            }
                        } else {
                            Log.w("jejeje", "Error getting documents.", task.getException());
                        }
                    }
                });


        return nombre;
    }

    public static void ocultarBinding() {
        binding.appBarPrincipal.fabAAdirEvento.setVisibility(View.INVISIBLE);
    }

    public static void mostrarBinding() {
        binding.appBarPrincipal.fabAAdirEvento.setVisibility(View.VISIBLE);
    }

    public static void actualizarDatosUsuario() {
        correoHeader.setText(mAuth.getCurrentUser().getEmail());
        nombreEImagenUsuario();
    }

    public static void updateEventosEditar(int caller, NuevoEvento evento2, int posicion, Evento evento, int i) {
        Evento eventoeditado = new Evento();
        eventoeditado.setId(evento.getId());
        eventoeditado.setSuscripciones(evento.getSuscripciones());
        eventoeditado.setSuscrito(evento.getSuscrito());
        eventoeditado.setPropietario(evento.getPropietario());
        eventoeditado.setLugar(evento2.getPropietario());
        eventoeditado.setTipo(evento2.getTipo());
        eventoeditado.setFecha(evento2.getFecha());
        eventoeditado.setDescripcion(evento2.getDescripcion().substring(0,100)+" ...");
        eventoeditado.setEvento(evento2.getNombre());
        eventoeditado.setDescripcionLarga(evento2.getDescripcion());
        eventoeditado.setCorreo(evento2.getPropietario());
        eventoeditado.setImagenesEvento(evento2.getImagen());
        eventoeditado.setImagenUsuario(evento.getImagenUsuario());
        eventoeditado.setCreacion(evento2.getCreacion());

        if (i == 1) {
            updateEventos(caller, eventoeditado,  posicion);
        } else {

        }
    }

    public static void actualizarNotificaciones(int pos) {
        nav_notificaciones.actualizarAdapter(pos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        String[] co =  MainActivity.firebaseUser.getEmail().split("[.]");
        correoPart = co[0];
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        context = getApplicationContext();

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarPrincipal.toolbar);
        binding.appBarPrincipal.fabAAdirEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 /*Snackbar.make(view, "Example of make", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(getApplicationContext(), CrearEvento.class);
                startActivityForResult(intent, CODE_NEW_EVENT);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_publicaciones, R.id.nav_mis_eventos, R.id.nav_inscripciones, R.id.nav_mi_cuenta, R.id.nav_notificaciones)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_principal);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        correoHeader = findViewById(R.id.tv_correo_header);
        correoHeader.setText(mAuth.getCurrentUser().getEmail());
        nombreHeader = findViewById(R.id.tv_nombre_header);
        im_foto_usuario_header = findViewById(R.id.im_foto_perfil_header);
        nombreEImagenUsuario();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_principal);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opc_log_out:
                mAuth.signOut();
                sharedPreferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.opc_filtrar:
                AlertDialog.Builder builder = new AlertDialog.Builder(PrincipalActivity.this);
                View vBuilder = getLayoutInflater().inflate(R.layout.dialogo_filtro, null);

                builder.setTitle("Filtros de eventos");
                Spinner spFiltroTipos = vBuilder.findViewById(R.id.spFiltroTipos);
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.tipos_evento_filtro, android.R.layout.simple_spinner_item);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFiltroTipos.setAdapter(adapter2);

                Spinner spFiltroEstados = vBuilder.findViewById(R.id.spFiltroEstados);
                ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.estados_filtro, android.R.layout.simple_spinner_item);
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFiltroEstados.setAdapter(adapter3);

                builder.setPositiveButton("Filtrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Boolean filtroTipos = !spFiltroTipos.getSelectedItem().equals("Selecciona una opción");
                        Boolean filtroEstados = !spFiltroEstados.getSelectedItem().equals("Selecciona una opción");
                        if (filtroEstados || filtroTipos) {
                            if (filtroEstados && filtroTipos) {
                                switch (EventosAdapter.Caller) {
                                    case 0:
                                        nav_publicaciones.filtrarEstadosEventos(spFiltroEstados.getSelectedItem(), spFiltroTipos.getSelectedItem());
                                        break;
                                    case 1:
                                        nav_mis_eventos.filtrarEstadosEventos(spFiltroEstados.getSelectedItem(), spFiltroTipos.getSelectedItem());
                                        break;
                                    case 2:
                                        nav_inscripciones.filtrarEstadosEventos(spFiltroEstados.getSelectedItem(), spFiltroTipos.getSelectedItem());
                                        break;
                                }
                            } else if (filtroEstados) {
                                switch (EventosAdapter.Caller) {
                                    case 0:
                                        nav_publicaciones.filtrarEstados(spFiltroEstados.getSelectedItem());
                                        break;
                                    case 1:
                                        nav_mis_eventos.filtrarEstados(spFiltroEstados.getSelectedItem());
                                        break;
                                    case 2:
                                        nav_inscripciones.filtrarEstados(spFiltroEstados.getSelectedItem());
                                        break;
                                }
                            } else if (filtroTipos) {
                                switch (EventosAdapter.Caller) {
                                    case 0:
                                        nav_publicaciones.filtrarEventos(spFiltroTipos.getSelectedItem());
                                        break;
                                    case 1:
                                        nav_mis_eventos.filtrarEventos(spFiltroTipos.getSelectedItem());
                                        break;
                                    case 2:
                                        nav_inscripciones.filtrarEventos(spFiltroTipos.getSelectedItem());
                                        break;
                                }
                            }
                        } else {
                            switch (EventosAdapter.Caller) {
                                case 0:
                                    nav_publicaciones.adapter.vaciar();
                                    nav_publicaciones.llenarEventos();
                                    break;
                                case 1:
                                    nav_mis_eventos.adapterMisEventos.vaciar();
                                    nav_mis_eventos.llenarMisEventos();
                                    break;
                                case 2:
                                    nav_inscripciones.adapterSuscripciones.vaciar();
                                    nav_inscripciones.llenarSuscripciones();
                                    break;
                            }
                        }
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setView(vBuilder);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public  void notifyAdd(Notificacion noti) {
        //Configurar la notificación
        NotificationCompat.Builder notificacion =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                        .setContentTitle(noti.getTipo())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentText(noti.getDescripcion());
//Dar un identificador a la notificación
        int notificationId = 1;
//Tomar la referencia al servicio de notificaciones
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//Ejecutar la notificación
        manager.notify(notificationId, notificacion.build());

        //Crear canal de notficacion
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}
