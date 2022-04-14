package com.example.eventbook.ui.pricipal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.eventbook.MainActivity;
import com.example.eventbook.PrincipalActivity;
import com.example.eventbook.R;
import com.example.eventbook.ui.Evento;
import com.example.eventbook.ui.EventosAdapter;
import com.example.eventbook.ui.EventosHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link nav_publicaciones#newInstance} factory method to
 * create an instance of this fragment.
 */
public class nav_publicaciones extends Fragment {
    private RecyclerView rvPublicaciones;
    public static EventosAdapter adapter;
    public static FirebaseFirestore db;
    private static FirebaseAuth mAuth;
    private Query query;
    private static String correo = "";
    private static ArrayList<Evento> eventoArrayList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public nav_publicaciones() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment nav_publicaciones.
     */
    // TODO: Rename and change types and number of parameters
    public static nav_publicaciones newInstance(String param1, String param2) {
        nav_publicaciones fragment = new nav_publicaciones();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void updateTodosEvento() {

    }

    public static void addEventos() {
        adapter.vaciar();
        llenarEventos();
    }

    public static void filtrarEstados(Object selectedItem) {
        adapter.vaciar();
        db.collection("eventos")
                .whereArrayContains("suscripciones", mAuth.getCurrentUser().getEmail())
                .whereEqualTo("lugar", selectedItem)
                .orderBy("creacion", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mAuth = FirebaseAuth.getInstance();
                        int eventos = task.getResult().size();
                        int order_evento = -1;
                        Evento detEvento = new Evento();
                        eventoArrayList = new ArrayList<Evento>();
                        ArrayList<Boolean> pase = new ArrayList<Boolean>();
                        for (int i = 0; i <= eventos-1; i++) {
                            pase.add(false);
                        }
                        for (int i = 0; i <= eventos-1; i++) {
                            eventoArrayList.add(detEvento);
                        }
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                order_evento++;
                                int des_order_event = order_evento;
                                Evento evento = new Evento();
                                evento.setId(document.getId());
                                evento.setCorreo(document.get("propietario").toString());
                                correo = evento.getCorreo();
                                if (document.get("descripcion").toString().length() > 100) {
                                    String desc = document.get("descripcion").toString();
                                    evento.setDescripcion(desc.substring(0,100)+" ...");
                                } else {
                                    evento.setDescripcion(document.get("descripcion").toString());
                                }
                                evento.setEvento(document.get("nombre").toString());
                                evento.setFecha(document.get("fecha").toString());
                                evento.setDescripcionLarga(document.get("descripcion").toString());
                                evento.setImagenesEvento((ArrayList<String>) document.get("imagen"));
                                evento.setLugar(document.get("lugar").toString());
                                evento.setTipo(document.get("tipo").toString());
                                evento.setSuscrito(true);
                                evento.setPropietario(false);
                                ArrayList<String> suscripciones = (ArrayList<String>) document.get("suscripciones");
                                evento.setSuscripciones(suscripciones);
                                evento.setCreacion((Long) document.get("creacion"));
                                db.collection("usuarios")
                                        .whereEqualTo("correo", correo)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task2) {
                                                if (task2.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task2.getResult()) {
                                                        evento.setImagenUsuario(document.getString("imagen"));
                                                    }
                                                }
                                                eventoArrayList.set(des_order_event, evento);
                                                pase.set(des_order_event, true);
                                                if (!pase.contains(false)) {
                                                    llenarAdapter();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public static void filtrarEstadosEventos(Object selectedItem, Object selectedItem1) {
        adapter.vaciar();
        db.collection("eventos")
                .whereArrayContains("suscripciones", mAuth.getCurrentUser().getEmail())
                .whereEqualTo("lugar", selectedItem)
                .whereEqualTo("tipo", selectedItem1)
                .orderBy("creacion", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mAuth = FirebaseAuth.getInstance();
                        int eventos = task.getResult().size();
                        int order_evento = -1;
                        Evento detEvento = new Evento();
                        eventoArrayList = new ArrayList<Evento>();
                        ArrayList<Boolean> pase = new ArrayList<Boolean>();
                        for (int i = 0; i <= eventos-1; i++) {
                            pase.add(false);
                        }
                        for (int i = 0; i <= eventos-1; i++) {
                            eventoArrayList.add(detEvento);
                        }
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                order_evento++;
                                int des_order_event = order_evento;
                                Evento evento = new Evento();
                                evento.setId(document.getId());
                                evento.setCorreo(document.get("propietario").toString());
                                correo = evento.getCorreo();
                                if (document.get("descripcion").toString().length() > 100) {
                                    String desc = document.get("descripcion").toString();
                                    evento.setDescripcion(desc.substring(0,100)+" ...");
                                } else {
                                    evento.setDescripcion(document.get("descripcion").toString());
                                }
                                evento.setEvento(document.get("nombre").toString());
                                evento.setFecha(document.get("fecha").toString());
                                evento.setDescripcionLarga(document.get("descripcion").toString());
                                evento.setImagenesEvento((ArrayList<String>) document.get("imagen"));
                                evento.setLugar(document.get("lugar").toString());
                                evento.setTipo(document.get("tipo").toString());
                                evento.setSuscrito(true);
                                evento.setPropietario(false);
                                ArrayList<String> suscripciones = (ArrayList<String>) document.get("suscripciones");
                                evento.setSuscripciones(suscripciones);
                                evento.setCreacion((Long) document.get("creacion"));
                                db.collection("usuarios")
                                        .whereEqualTo("correo", correo)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task2) {
                                                if (task2.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task2.getResult()) {
                                                        evento.setImagenUsuario(document.getString("imagen"));
                                                    }
                                                }
                                                eventoArrayList.set(des_order_event, evento);
                                                pase.set(des_order_event, true);
                                                if (!pase.contains(false)) {
                                                    llenarAdapter();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View miView = inflater.inflate(R.layout.fragment_nav_publicaciones, container, false);


        rvPublicaciones = miView.findViewById(R.id.rv_publicaciones_principales);
        DividerItemDecoration dividerItemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);


        adapter = new EventosAdapter(getContext(), 0);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        llenarEventos();
        PrincipalActivity.mostrarBinding();

        rvPublicaciones.setAdapter(adapter);
        rvPublicaciones.addItemDecoration(dividerItemDecoration);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPublicaciones.setLayoutManager(linearLayoutManager);

        return miView;
    }

    public static void llenarEventos() {
        db.collection("eventos")
                .orderBy("creacion", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mAuth = FirebaseAuth.getInstance();
                        int eventos = task.getResult().size();
                        int order_evento = -1;
                        Evento detEvento = new Evento();
                        eventoArrayList = new ArrayList<Evento>();
                        ArrayList<Boolean> pase = new ArrayList<Boolean>();
                        for (int i = 0; i <= eventos-1; i++) {
                            pase.add(false);
                        }
                        for (int i = 0; i <= eventos-1; i++) {
                            eventoArrayList.add(detEvento);
                        }
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                order_evento++;
                                int des_order_event = order_evento;
                                Evento evento = new Evento();
                                evento.setId(document.getId());
                                evento.setCorreo(document.get("propietario").toString());
                                correo = evento.getCorreo();
                                if (document.get("descripcion").toString().length() > 100) {
                                    String desc = document.get("descripcion").toString();
                                    evento.setDescripcion(desc.substring(0,100)+" ...");
                                } else {
                                    evento.setDescripcion(document.get("descripcion").toString());
                                }
                                evento.setEvento(document.get("nombre").toString());
                                evento.setFecha(document.get("fecha").toString());
                                evento.setDescripcionLarga(document.get("descripcion").toString());
                                evento.setImagenesEvento((ArrayList<String>) document.get("imagen"));
                                evento.setLugar(document.get("lugar").toString());
                                evento.setTipo(document.get("tipo").toString());
                                ArrayList<String> suscripciones = (ArrayList<String>) document.get("suscripciones");
                                evento.setSuscripciones(suscripciones);
                                evento.setCreacion((Long) document.get("creacion"));
                                if (suscripciones.contains(mAuth.getCurrentUser().getEmail())) {
                                    evento.setSuscrito(true);
                                } else {
                                    evento.setSuscrito(false);
                                }
                                evento.setPropietario(false);
                                db.collection("usuarios")
                                        .whereEqualTo("correo", correo)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task2) {
                                                if (task2.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task2.getResult()) {
                                                        evento.setImagenUsuario(document.getString("imagen"));
                                                    }
                                                }
                                                eventoArrayList.set(des_order_event, evento);
                                                pase.set(des_order_event, true);
                                                if (!pase.contains(false)) {
                                                    llenarAdapter();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private static void llenarAdapter() {
        for (int i = 0; i <= eventoArrayList.size()-1; i++) {
            adapter.add(eventoArrayList.get(i));
        }
    }

    public static void updateEventos(Evento evento, int numEvento) {
        adapter.updateEvento(evento, numEvento);
    }

    public static void filtrarEventos(Object selectedItem) {
        adapter.vaciar();
        db.collection("eventos")
                .orderBy("creacion", Query.Direction.DESCENDING)
                .whereEqualTo("tipo", selectedItem)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mAuth = FirebaseAuth.getInstance();
                        int eventos = task.getResult().size();
                        int order_evento = -1;
                        Evento detEvento = new Evento();
                        eventoArrayList = new ArrayList<Evento>();
                        ArrayList<Boolean> pase = new ArrayList<Boolean>();
                        for (int i = 0; i <= eventos-1; i++) {
                            pase.add(false);
                        }
                        for (int i = 0; i <= eventos-1; i++) {
                            eventoArrayList.add(detEvento);
                        }
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                order_evento++;
                                int des_order_event = order_evento;
                                Evento evento = new Evento();
                                evento.setId(document.getId());
                                evento.setCorreo(document.get("propietario").toString());
                                correo = evento.getCorreo();
                                if (document.get("descripcion").toString().length() > 100) {
                                    String desc = document.get("descripcion").toString();
                                    evento.setDescripcion(desc.substring(0,100)+" ...");
                                } else {
                                    evento.setDescripcion(document.get("descripcion").toString());
                                }
                                evento.setEvento(document.get("nombre").toString());
                                evento.setFecha(document.get("fecha").toString());
                                evento.setDescripcionLarga(document.get("descripcion").toString());
                                evento.setImagenesEvento((ArrayList<String>) document.get("imagen"));
                                evento.setLugar(document.get("lugar").toString());
                                evento.setTipo(document.get("tipo").toString());
                                ArrayList<String> suscripciones = (ArrayList<String>) document.get("suscripciones");
                                evento.setSuscripciones(suscripciones);
                                evento.setCreacion((Long) document.get("creacion"));
                                if (suscripciones.contains(mAuth.getCurrentUser().getEmail())) {
                                    evento.setSuscrito(true);
                                } else {
                                    evento.setSuscrito(false);
                                }
                                evento.setPropietario(false);
                                db.collection("usuarios")
                                        .whereEqualTo("correo", correo)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task2) {
                                                if (task2.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task2.getResult()) {
                                                        evento.setImagenUsuario(document.getString("imagen"));
                                                    }
                                                }
                                                eventoArrayList.set(des_order_event, evento);
                                                pase.set(des_order_event, true);
                                                if (!pase.contains(false)) {
                                                    llenarAdapter();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}