package com.example.eventbook.ui.cuenta;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eventbook.MainActivity;
import com.example.eventbook.PrincipalActivity;
import com.example.eventbook.R;
import com.example.eventbook.ui.EventosAdapter;
import com.example.eventbook.ui.eventos.nav_mis_eventos;
import com.example.eventbook.ui.inscripciones.nav_inscripciones;
import com.example.eventbook.ui.pricipal.nav_publicaciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link nav_mi_cuenta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class nav_mi_cuenta extends Fragment {
    private static final String TAG = "cosa";
    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseFirestore db;
    private EditText corre, nombre;
    private ImageView imagen;
    private String nombreQuery = "nada", imagenQuery = "nada";
    private Button btn_actualizar, btn_cambiar_contraseña;
    private StorageReference  storageRef;
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private Bitmap bitmap;
    private Uri imageUri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public nav_mi_cuenta() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MiCuenta.
     */
    // TODO: Rename and change types and number of parameters
    public static nav_mi_cuenta newInstance(String param1, String param2) {
        nav_mi_cuenta fragment = new nav_mi_cuenta();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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


        View miCuenta = inflater.inflate(R.layout.fragment_mi_cuenta, container, false);

        nombre = miCuenta.findViewById(R.id.edt_nombre_actualizar);
        imagen = miCuenta.findViewById(R.id.imb_actualizar);
        corre = miCuenta.findViewById(R.id.edt_correo_actualizar);
        btn_actualizar = miCuenta.findViewById(R.id.btn_actualizar);
        btn_cambiar_contraseña = miCuenta.findViewById(R.id.btn_cambiar_contraseña);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance("gs://eventbook-942c1.appspot.com").getReference();

        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizar();
            }
        });

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });

        btn_cambiar_contraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarContraseña();
            }
        });

       consultaDatos();
       PrincipalActivity.ocultarBinding();

        return miCuenta;

    }

    private void cambiarContraseña() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        View vBuilder = getLayoutInflater().inflate(R.layout.dialogo_contra, null);

        builder.setTitle("Cambiar contraseña");
        EditText contra = vBuilder.findViewById(R.id.edtCambiarContraseña);
        EditText contra2 = vBuilder.findViewById(R.id.edtCambiarContraseña2);

        builder.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String contraseña = contra.getText().toString().trim();
                String contraseña2 = contra2.getText().toString().trim();

                if (contraseña.equals(contraseña2)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(contraseña)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "La contraseña se ha cambiado", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.e("delta", e+"");
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(vBuilder);
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == MainActivity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imagen.setImageBitmap(bitmap);
            guardarImgaen();
            subirFotoEscogida(imageUri);
        }
        if (requestCode == REQUEST_IMAGE_GET && resultCode == MainActivity.RESULT_OK) {
            Uri uri = data.getData();
            subirFotoEscogida(uri);
        }
    }

    private void subirFotoEscogida(Uri uri) {
        if (uri != null){
            StorageReference reference = storageRef.child("imagenesUsuarios/"+ uri.getLastPathSegment()+MainActivity.firebaseUser.getEmail());
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = uri.toString();

                            db.collection("usuarios").document(MainActivity.firebaseUser.getEmail()).update("imagen",url);

                            Glide.with(getContext()).load(url).into(imagen);
                            PrincipalActivity.actualizarDatosUsuario();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private void cargarImagen() {

        //Crear el objeto builder
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//Configurar el diálogo
        alert.setTitle("Imagen de usuario")
                .setMessage("Elija si quiere tomar una foto o escogerla de la galeria")
                .setPositiveButton("Escoger foto", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_IMAGE_GET);
                    }
                })
                .setNegativeButton("Tomar foto", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                            dialog.dismiss();
                    }
                }).show();

    }

    private void actualizar() {
        Map<String, Object> map = new HashMap<>();

        String nombreActualizado = nombre.getText().toString().trim();
        String correoActualizado = corre.getText().toString().trim();

        if (nombreActualizado.equals("") || correoActualizado.equals("")){
            Toast.makeText(getContext(),"No puede haber campos vacios", Toast.LENGTH_LONG).show();
        }else {

        map.put("nombre",nombreActualizado);
        map.put("correo",correoActualizado);

        db.collection("usuarios").document(MainActivity.firebaseUser.getEmail()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                PrincipalActivity.actualizarDatosUsuario();
                Toast.makeText(getContext(),"Se han actualizado tus datos",Toast.LENGTH_LONG).show();
            }
        });

        }


    }

    private void consultaDatos() {

        db.collection("usuarios")
                .whereEqualTo("correo", MainActivity.firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                imagenQuery = document.getString("imagen");
                                nombreQuery = document.getString("nombre");

                                nombre.setText(nombreQuery);
                                corre.setText(MainActivity.firebaseUser.getEmail());

                                if (!document.getString("imagen").isEmpty()){
                                    Glide.with(getContext()).load(imagenQuery).into(imagen);
                                }
                            }
                        }else {
                            Log.w("jejeje", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private  void guardarImgaen(){
        OutputStream outputStream = null;
        File file = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContentResolver resolver = getActivity().getContentResolver();
            ContentValues values = new ContentValues();

            String filename = System.currentTimeMillis()+ "imagen_capture";

            values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/EventBook");
            values.put(MediaStore.Images.Media.IS_PENDING, 1 );

            Uri colection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            imageUri = resolver.insert(colection, values);

            try {
                outputStream = resolver.openOutputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(imageUri, values, null, null);

        }else {
            String imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

            String fileName = System.currentTimeMillis() + "jpg";

            file = new File(imageDir, fileName);

            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        boolean guardada = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        if (guardada){
            Toast.makeText(getContext(), "Se ha guardado la imagen",Toast.LENGTH_LONG).show();
        }

        if (outputStream != null){
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
            }
            if (file != null){
                MediaScannerConnection.scanFile(getContext(),new String[]{file.toString()}, null, null);
            }
        }
    }



}