package com.example.eventbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventbook.ui.notificacion.Notificacion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {
    private TextView login;
    private EditText edtNombre, edtCorreo, edtContraseña, edtContraseñaConfirmar;
    private Button btnRegistrar;
    private String nombre, correo, contraseña, contraseñaConfirmar, mensaje = "Error desconocido.";
    private Boolean registrado = false;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    private FirebaseDatabase dbs;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        login = findViewById(R.id.tv_ingresar_login);
        edtNombre = findViewById(R.id.edt_nombre_registro);
        edtCorreo = findViewById(R.id.edt_correo_registro);
        edtContraseña = findViewById(R.id.edt_contra_registro);
        edtContraseñaConfirmar = findViewById(R.id.edt_contra_confirmar_registro);

        btnRegistrar = findViewById(R.id.btn_registrar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Boton de registro de usuarios
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Asignación de variables
                nombre = edtNombre.getText().toString().trim();
                correo = edtCorreo.getText().toString().trim();
                contraseña = edtContraseña.getText().toString().trim();
                contraseñaConfirmar = edtContraseñaConfirmar.getText().toString().trim();

                if (!nombre.equals("") & !correo.equals("") & !contraseña.equals("") & !contraseñaConfirmar.equals("")) {
                    if(contraseña.equals(contraseñaConfirmar)) {
                        //Crear al usuario
                        documentReference = db.collection("usuarios").document(correo);
                        db.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @org.jetbrains.annotations.Nullable
                            @Override
                            public Void apply(@NonNull @NotNull Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot snapshot = transaction.get(documentReference);
                                String nombre_db = snapshot.getString("correo");
                                registrado = nombre_db == null ? false : true;
                                return null;
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (registrado) {
                                    mensaje = "El correo electronico ya se encuentra registrado.";
                                    alerta(mensaje);
                                } else {
                                    mAuth.createUserWithEmailAndPassword(correo, contraseña)
                                            .addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        // El usuario se ha creado
                                                        Map<String, Object> user = new HashMap<>();
                                                        user.put("nombre", nombre);
                                                        user.put("correo", correo);
                                                        user.put("imagen" ,"");

                                                        db.collection("usuarios").document(correo).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                mensaje = "Se ha registrado el usuario.";
                                                                alerta(mensaje);
                                                                //inicializarNotificaciones(correo, nombre);
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull @NotNull Exception e) {

                                                            }
                                                        });
                                                    } else {
                                                        // Mensaje de error en la respuesta del servidor
                                                        Toast.makeText(getApplicationContext(), task.getException().toString(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    } else {
                        mensaje = "Las contraseñas no coindiden.";
                        alerta(mensaje);
                    }
                } else {
                    mensaje = "Todos los campos son obligatorios.";
                    alerta(mensaje);
                }
            }
        });
    }

    private void inicializarNotificaciones(String correo,String nomabre) {
        dbs = FirebaseDatabase.getInstance("https://eventbook-942c1-default-rtdb.firebaseio.com/");
        String[] arCorreo = correo.split("[.]");
        dbReference = dbs.getReference(arCorreo[0]);
        dbReference.setValue(nomabre);
    }


    private void alerta(String mensaje) {
        Toast toast = Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}