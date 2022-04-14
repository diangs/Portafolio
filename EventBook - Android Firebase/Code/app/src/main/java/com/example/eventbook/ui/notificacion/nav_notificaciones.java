package com.example.eventbook.ui.notificacion;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventbook.MainActivity;
import com.example.eventbook.PrincipalActivity;
import com.example.eventbook.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link nav_notificaciones#newInstance} factory method to
 * create an instance of this fragment.
 */
public class nav_notificaciones extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHANNEL_ID = "NOTIFICACION";
    private static NotificacionesAdapter notificacionesAdapter;
    private RecyclerView rvNotificaciones;
    private FirebaseDatabase db;
    private DatabaseReference dbReference;
    NotificationCompat.Builder noty;
    String kkey = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public nav_notificaciones() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment nav_notificaciones.
     */
    // TODO: Rename and change types and number of parameters
    public static nav_notificaciones newInstance(String param1, String param2) {
        nav_notificaciones fragment = new nav_notificaciones();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void actualizarAdapter(int pos) {
        notificacionesAdapter.remove(pos);
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
        PrincipalActivity.ocultarBinding();

        View notificacionView = inflater.inflate(R.layout.fragment_nav_notificaciones, container, false);


        rvNotificaciones = notificacionView.findViewById(R.id.rv_notificaciones);

        notificacionesAdapter = new NotificacionesAdapter(getContext());

        rvNotificaciones.setAdapter(notificacionesAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvNotificaciones.setLayoutManager(linearLayoutManager);

        db = FirebaseDatabase.getInstance("https://eventbook-942c1-default-rtdb.firebaseio.com/");
        dbReference = db.getReference(PrincipalActivity.correoPart);

        dbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Notificacion notificacion = dataSnapshot.getValue(Notificacion.class);
                kkey = dataSnapshot.getKey();
                boolean encontrado = false;
                    for (int i = 0; i<NotificacionesAdapter.data.size();i++){
                        if (kkey.equals(NotificacionesAdapter.data.get(i).getKey())) encontrado = true;
                    }
                    if (!encontrado){
                        notificacion.setKey(dataSnapshot.getKey());
                        notificacionesAdapter.add(notificacion);
                    }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return notificacionView;
    }



}