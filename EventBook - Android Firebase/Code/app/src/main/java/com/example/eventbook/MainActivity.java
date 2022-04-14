package com.example.eventbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventbook.ui.Evento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView tvRegistrar, tvRecuperar;
    private EditText edtCorreo, edtContraseña;
    private Button btnIngresar;
    private String correo = "usuario@correo.com";
    private String contraseña;
    private FirebaseAuth mAuth;
    public static FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = new ProgressDialog(this);

        tvRegistrar = findViewById(R.id.tv_Registrar);
        tvRecuperar = findViewById(R.id.tv_recuperar_main);

        edtCorreo = findViewById(R.id.edt_correo);
        edtContraseña = findViewById(R.id.edt_contra);

        btnIngresar = findViewById(R.id.btn_ingresar);

        mAuth = FirebaseAuth.getInstance();

        cargarUsuario();

        tvRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecuperarContraActivity.class);
                startActivity(intent);
            }
        });

        tvRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Evento evento = new Evento();
                Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(intent);
            }
        });

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correo = edtCorreo.getText().toString().trim();
                contraseña = edtContraseña.getText().toString().trim();
                login(correo, contraseña);
            }
        });


    }

    private void login(String correo, String contraseña) {
        if (!correo.equals("") & !contraseña.equals("")) {
            mAuth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseUser = mAuth.getCurrentUser();
                                Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                                guardarUsuario();
                                progress.dismiss();
                                startActivity(intent);
                                finish();
                            } else {
                                // Mensaje de error en la respuesta del servidor
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Datos incorrectos.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Se requiere correo y contraseña para ingresar.", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarUsuario() {
        sharedPreferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("correo", correo);
        editor.putString("contraseña", contraseña);
        editor.commit();
    }

    private void cargarUsuario() {
        sharedPreferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String usuario = sharedPreferences.getString("correo", "false");
        String contraseña = sharedPreferences.getString("contraseña", "false");
        if(!usuario.equals("false") && !contraseña.equals("false")) {
            edtCorreo.setText(usuario);
            edtContraseña.setText(contraseña);
            progress.setTitle("Cargando");
            progress.setMessage("Cargando datos del usuario...");
            progress.setCancelable(false);
            progress.show();
            login(usuario, contraseña);
        }
    }
}