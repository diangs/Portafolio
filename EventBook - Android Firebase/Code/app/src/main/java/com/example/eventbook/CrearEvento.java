package com.example.eventbook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventbook.ui.EventosAdapter;
import com.example.eventbook.ui.NuevoEvento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class CrearEvento extends AppCompatActivity {
    private static final int REQUEST_IMAGE_NEW_EVENT = 1;
    private TextView edtContadorEventoCrear, tvMostrarFechaSeleccionada, tvMostrarHoraSeleccionada;
    private EditText edt_descripcion_evento_crear, edt_nombre_evento_crear;
    private Spinner spEstados, spTipo;
    private LinearLayout lilaImagenes;
    private Button btn_seleccionar_imagen, btnSeleccionarFechaCrear, btnSeleccionarHoraCrear;
    private Uri imagenUri;
    private List<Uri> listaImagenes;
    private LayoutInflater inflater;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private int dia, mes, año, hora, minutos;
    private GregorianCalendar date;
    private Long time;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ClipData clipData;
    private StorageReference storageRef;
    private ArrayList<String> url_imagenes;
    private ArrayList<Boolean> comprobar_subida;
    private ProgressDialog progress;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_NEW_EVENT) {
            clipData = data.getClipData();
            listaImagenes.clear();
            if (clipData == null) {
                imagenUri = data.getData();
                listaImagenes.add(imagenUri);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    listaImagenes.add(clipData.getItemAt(i).getUri());
                }
            }
            lilaImagenes.removeAllViews();
            for (int i = 0; i < listaImagenes.size(); i++) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), listaImagenes.get(i));
                    View view = inflater.inflate(R.layout.item_image, lilaImagenes, false);

                    ImageView imageView = view.findViewById(R.id.imageView);
                    imageView.setImageBitmap(bitmap);

                    lilaImagenes.addView(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        if (resultCode == RESULT_CANCELED && requestCode == REQUEST_IMAGE_NEW_EVENT) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_eventos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaImagenes = new ArrayList<>();
        comprobar_subida = new ArrayList<>();
        url_imagenes = new ArrayList<String>();

        setContentView(R.layout.activity_crear_evento);
        iniciarSelector();
        iniciarSelectorHora();
        getSupportActionBar().setTitle(R.string.crear_evento_eventbook);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        lilaImagenes = findViewById(R.id.lilaImagenes);
        inflater = LayoutInflater.from(this);

        for (int i = 0; i < 3; i++) {
            View view = inflater.inflate(R.layout.item_image, lilaImagenes, false);

            ImageView imageView = view.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.event_holder);

            lilaImagenes.addView(view);
        }

        edtContadorEventoCrear = findViewById(R.id.edtContadorEventoCrear);
        edt_descripcion_evento_crear = findViewById(R.id.edt_descripcion_evento_crear);
        edt_nombre_evento_crear = findViewById(R.id.edt_nombre_evento_crear);

        btn_seleccionar_imagen = findViewById(R.id.btn_seleccionar_imagen);
        btnSeleccionarFechaCrear = findViewById(R.id.btnSeleccionarFechaCrear);
        btnSeleccionarHoraCrear = findViewById(R.id.btnSeleccionarHoraCrear);

        tvMostrarFechaSeleccionada = findViewById(R.id.tvMostrarFechaSeleccionada);
        tvMostrarHoraSeleccionada = findViewById(R.id.tvMostrarHoraSeleccionada);

        spEstados = findViewById(R.id.spEstados);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.estados, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstados.setAdapter(adapter);

        spTipo = findViewById(R.id.spTipo);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.tipos_evento, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter2);

        edt_descripcion_evento_crear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtContadorEventoCrear.setText(edt_descripcion_evento_crear.length() + " / 1000");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edt_descripcion_evento_crear.length() >= 1001) {
                    String texto = edt_descripcion_evento_crear.getText().toString().trim();
                    texto.substring(0, 998);
                    edt_descripcion_evento_crear.setText(texto);
                }
            }
        });

        btn_seleccionar_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_NEW_EVENT);
            }
        });

        btnSeleccionarFechaCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        btnSeleccionarHoraCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });
    }

    public void crearEvento() {
        if (!edt_nombre_evento_crear.getText().toString().trim().equals("") && !edt_descripcion_evento_crear.getText().toString().trim().equals("") && !tvMostrarFechaSeleccionada.getText().toString().equals("") && !tvMostrarHoraSeleccionada.getText().toString().equals("")) {
            if (edt_descripcion_evento_crear.getText().toString().length() > 100) {
                if (listaImagenes.size() > 0) {
                    if (!verificarFecha()) {
                        progress.show();
                        date = new GregorianCalendar(año, mes, dia, hora, minutos);
                        time = date.getTimeInMillis();

                        NuevoEvento evento = new NuevoEvento();
                        evento.setNombre(edt_nombre_evento_crear.getText().toString().trim());
                        evento.setDescripcion(edt_descripcion_evento_crear.getText().toString().trim());
                        evento.setFecha(String.valueOf(time));
                        evento.setTipo(spTipo.getSelectedItem().toString().trim());
                        evento.setLugar(spEstados.getSelectedItem().toString().trim());
                        evento.setPropietario(mAuth.getCurrentUser().getEmail());
                        time = System.currentTimeMillis();
                        evento.setCreacion(time);
                        ArrayList<String> suscripciones = new ArrayList<String>();
                        suscripciones.add("");
                        evento.setSuscripciones(suscripciones);

                        for (int i=0; i <listaImagenes.size(); i++) {
                            comprobar_subida.add(false);
                        }

                        for (int i=0; i <listaImagenes.size(); i++) {
                            url_imagenes.add("fasd");
                        }

                        for (int i=0; i <listaImagenes.size(); i++) {
                            Uri uri = listaImagenes.get(i);
                            int len = i;
                            StorageReference reference = storageRef.child(""+uri.getLastPathSegment()+System.currentTimeMillis());
                            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            url_imagenes.set(len,uri.toString());
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("delta", "no se subio");
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Uri> task) {
                                            comprobar_subida.set(len, true);
                                            if (!comprobar_subida.contains(false)) {
                                                evento.setImagen(url_imagenes);
                                                db.collection("eventos").document().set(evento).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        progress.dismiss();
                                                        PrincipalActivity.addEventos(EventosAdapter.Caller);
                                                        onBackPressed();
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull @NotNull Exception e) {
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Fallo: "+e, Toast.LENGTH_SHORT);
                                                        toast.show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {

                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("delta", "ni pudo "+e);
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Se requiere una tolerancia de 2 horas para crear eventos.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No se han seleccinado imagenes para el evento.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "La descripción del evento es muy corta.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Todos los campos son obligatorios para crear eventos.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean verificarFecha() {
        Long time_actual = System.currentTimeMillis();
        GregorianCalendar verificacion_time = new GregorianCalendar(año, mes, dia, hora, minutos);
        Long time_verificaction = verificacion_time.getTimeInMillis();
        return time_actual + 7200000 >= time_verificaction;
    }

    private void iniciarSelectorHora() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String minute_resuelto = String.valueOf(minute);
                if (minute_resuelto.length() == 1) {
                    minute_resuelto = "0" + minute_resuelto;
                }
                hora = hourOfDay;
                minutos = minute;

                Calendar datetime = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                datetime.set(Calendar.MINUTE, minute);

                hourOfDay = hourOfDay == 0 ? 12 : hourOfDay;
                hourOfDay = hourOfDay > 12 ? hourOfDay - 12 : hourOfDay;
                String hora_msg = hourOfDay + ":" + minute_resuelto;

                if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                    hora_msg += " a.m.";
                else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                    hora_msg += " p.m.";
                tvMostrarHoraSeleccionada.setText(hora_msg);
            }
        };

        Calendar calendar = Calendar.getInstance();
        hora = 12;
        minutos = 00;

        int style = AlertDialog.THEME_HOLO_LIGHT;

        timePickerDialog = new TimePickerDialog(this, style, timeSetListener, hora, minutos, false);
    }

    private void iniciarSelector() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tvMostrarFechaSeleccionada.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                año = year;
                mes = month;
                dia = dayOfMonth;
            }
        };

        Calendar calendar = Calendar.getInstance();
        año = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, año, mes, dia);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opc_guardar_evento_editado:
                progress = new ProgressDialog(this);
                progress.setTitle("Cargando");
                progress.setMessage("Creando evento, espere un momento...");
                progress.setCancelable(false);
                crearEvento();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}