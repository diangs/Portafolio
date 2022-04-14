package com.example.eventbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import org.jetbrains.annotations.NotNull;

public class RecuperarContraActivity extends AppCompatActivity {
    private EditText edtCorreoRecuperar;
    private Button btnRecuperarContraseña;
    private Boolean registrado = false;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contra);

        edtCorreoRecuperar = findViewById(R.id.edtCorreoRecuperar);
        btnRecuperarContraseña = findViewById(R.id.btnRecuperarContraseña);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRecuperarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = edtCorreoRecuperar.getText().toString().trim();
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
                });
                if (!registrado) {
                    mAuth.setLanguageCode("es");
                    mAuth.sendPasswordResetEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Se ha enviado un correo de restablecimiento. Verifica tu bandeja.", Toast.LENGTH_SHORT).show();
                                edtCorreoRecuperar.setText("");
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "La dirección de correo no se encuentra registrada.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}