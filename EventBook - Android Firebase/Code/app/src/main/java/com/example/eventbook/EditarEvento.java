package com.example.eventbook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
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

import com.bumptech.glide.Glide;
import com.example.eventbook.ui.Evento;
import com.example.eventbook.ui.EventosAdapter;
import com.example.eventbook.ui.EventosHolder;
import com.example.eventbook.ui.NuevoEvento;
import com.example.eventbook.ui.notificacion.Notificacion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Handler;

public class EditarEvento extends AppCompatActivity {
    private static final int REQUEST_IMAGE_EDIT_EVENT = 1;
    private static final int REQUEST_IMAGE_CHANGE = 2;
    private TextView edtContadorEventoEditar, tvMostrarFechaSeleccionada, tvMostrarHoraSeleccionada;
    private EditText edt_descripcion_evento_editar, edt_nombre_evento_editar;
    private Spinner spEstados, spTipo;
    private LinearLayout lilaImagenes;
    private Button btn_seleccionar_imagen, btnSeleccionarFechaEditar, btnSeleccionarHoraEditar;
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
    private int posicion, posicion_imagen;
    private int caller;
    private ArrayAdapter<CharSequence> adapter2, adapter;
    private Evento evento;
    private ArrayList<String> imagenesEvento;
    private FirebaseDatabase dbs;
    private DatabaseReference dbReference;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_EDIT_EVENT) {
            imagenesEvento.clear();
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

                    StorageReference reference = storageRef.child(""+listaImagenes.get(i).getLastPathSegment()+System.currentTimeMillis());
                    reference.putFile(listaImagenes.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imagenesEvento.add(uri.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("delta", "no se subio");
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                                    llenarLila();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("delta", "ni pudo "+e);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CHANGE) {
            Uri uri = data.getData();
            StorageReference reference = storageRef.child(""+uri.getLastPathSegment()+System.currentTimeMillis());
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imagenesEvento.set(posicion_imagen,uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("delta", "no se subio");
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Uri> task) {
                            llenarLila();
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_evento);

        dbs = FirebaseDatabase.getInstance("https://eventbook-942c1-default-rtdb.firebaseio.com/");

        listaImagenes = new ArrayList<>();
        comprobar_subida = new ArrayList<>();
        url_imagenes = new ArrayList<String>();

        setContentView(R.layout.activity_editar_evento);
        getSupportActionBar().setTitle(R.string.editar_evento_eventbook);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        lilaImagenes = findViewById(R.id.lila3Imagenes);
        inflater = LayoutInflater.from(this);

        edtContadorEventoEditar = findViewById(R.id.edtContadorEventoEditar);
        edt_descripcion_evento_editar = findViewById(R.id.edt_descripcion_evento_editar);
        edt_nombre_evento_editar = findViewById(R.id.edt_nombre_evento_editar);

        btn_seleccionar_imagen = findViewById(R.id.btn_seleccionar_imagen2);
        btnSeleccionarFechaEditar = findViewById(R.id.btnSeleccionarFechaEditar);
        btnSeleccionarHoraEditar = findViewById(R.id.btnSeleccionarHoraEditar);

        tvMostrarFechaSeleccionada = findViewById(R.id.tvMostrarFechaSeleccionada2);
        tvMostrarHoraSeleccionada = findViewById(R.id.tvMostrarHoraSeleccionada2);

        spEstados = findViewById(R.id.spEstados2);
        adapter = ArrayAdapter.createFromResource(this, R.array.estados, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstados.setAdapter(adapter);

        spTipo = findViewById(R.id.spTipo2);
        adapter2 = ArrayAdapter.createFromResource(this, R.array.tipos_evento, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter2);

        edt_descripcion_evento_editar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtContadorEventoEditar.setText(edt_descripcion_evento_editar.length() + " / 1000");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edt_descripcion_evento_editar.length() >= 1001) {
                    String texto = edt_descripcion_evento_editar.getText().toString().trim();
                    texto.substring(0, 998);
                    edt_descripcion_evento_editar.setText(texto);
                }
            }
        });

        btn_seleccionar_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_EDIT_EVENT);
            }
        });

        btnSeleccionarFechaEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        btnSeleccionarHoraEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        Intent intent = getIntent();
        evento = (Evento) intent.getSerializableExtra(EventosHolder.KEY_EVENTO);
        posicion = intent.getIntExtra(EventosHolder.KEY_POSITION, -1);
        caller = intent.getIntExtra(EventosHolder.KEY_CALLER, -1);

        llenarDatosEvento();
        iniciarSelector();
        iniciarSelectorHora();
    }

    private void llenarDatosEvento() {
        edt_nombre_evento_editar.setText(evento.getEvento());
        edt_descripcion_evento_editar.setText(evento.getDescripcionLarga());

        spEstados.setSelection(adapter.getPosition(evento.getLugar()));
        spTipo.setSelection(adapter2.getPosition(evento.getTipo()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy-hh:mm a", new Locale("es"));
        Long i = null;
        i = Long.valueOf(evento.getFecha());
        String fechaHora = simpleDateFormat.format(new Timestamp(i));
        dia = Integer.parseInt(fechaHora.substring(0,2));
        mes = Integer.parseInt(fechaHora.substring(3,5));
        año = Integer.parseInt("20"+fechaHora.substring(6,8));
        hora = fechaHora.substring(15,16).equals("p") ? 12 + Integer.parseInt(fechaHora.substring(9,11)) : Integer.parseInt(fechaHora.substring(9,11));
        minutos = Integer.parseInt(fechaHora.substring(12,14));

        imagenesEvento = evento.getImagenesEvento();

        llenarLila();
    }

    private void llenarLila() {
        lilaImagenes.removeAllViews();
        for (int a = 0; a < imagenesEvento.size(); a++) {
            int numeroImagen = a;
            View view = inflater.inflate(R.layout.item_image_desc, lilaImagenes, false);

            ImageView imageView = view.findViewById(R.id.imageView);
            Glide.with(getApplicationContext()).load(imagenesEvento.get(a)).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(EditarEvento.this);
                    alert.setTitle("Editar Imagen")
                            .setMessage("¿Desea eliminar o remplazar la imagen?")
                            .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    imagenesEvento.remove(numeroImagen);
                                    posicion_imagen = numeroImagen;
                                    llenarLila();
                                }
                            })
                            .setNegativeButton("Remplazar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                    posicion_imagen = numeroImagen;
                                    startActivityForResult(intent, REQUEST_IMAGE_CHANGE);
                                }
                            }).show();
                }
            });
            lilaImagenes.addView(view);
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
                datetime.set(Calendar.HOUR_OF_DAY, hora);
                datetime.set(Calendar.MINUTE, minutos);

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
        int style = AlertDialog.THEME_HOLO_LIGHT;

        timePickerDialog = new TimePickerDialog(this, style, timeSetListener, hora, minutos, false);

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hora);
        datetime.set(Calendar.MINUTE, minutos);

        String minute_resuelto = String.valueOf(minutos);
        if (minute_resuelto.length() == 1) {
            minute_resuelto = "0" + minute_resuelto;
        }

        int hourOfDay = 0;
        hourOfDay = hora == 0 ? 12 : hora;
        hourOfDay = hora > 12 ? hora - 12 : hora;
        String hora_msg = hourOfDay + ":" + minute_resuelto;

        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            hora_msg += " a.m.";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            hora_msg += " p.m.";
        tvMostrarHoraSeleccionada.setText(hora_msg);
    }

    private void iniciarSelector() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                año = year;
                mes = month;
                dia = dayOfMonth;
                if (mes+1 >= 10) {
                    tvMostrarFechaSeleccionada.setText(dia + "/" + (mes + 1) + "/" + año);
                } else {
                    tvMostrarFechaSeleccionada.setText(dia + "/" + "0"+(mes + 1) + "/" + año);
                }
            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, año, mes-1, dia);
        if (mes+1 >= 10) {
            tvMostrarFechaSeleccionada.setText(dia + "/" + (mes + 1) + "/" + año);
        } else {
            tvMostrarFechaSeleccionada.setText(dia + "/" + "0"+(mes + 1) + "/" + año);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opc_guardar_evento_editado:
                progress = new ProgressDialog(this);
                progress.setTitle("Cargando");
                progress.setMessage("Editando evento, espere un momento...");
                progress.setCancelable(false);
                actualizarEvento();
                break;
            case R.id.opc_borrar_evento:
                borrarEvento();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void borrarEvento() {

        AlertDialog.Builder alert = new AlertDialog.Builder(EditarEvento.this);
        alert.setTitle("Eliminar Evento")
                .setMessage("¿Estás seguro que deseas eliminar este evento?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        db.collection("eventos").document(evento.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Se ha eliminado el evento", Toast.LENGTH_SHORT).show();
                                PrincipalActivity.addEventos(caller);
                                onBackPressed();
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).show();
    }

    private void actualizarEvento() {
        if (!edt_nombre_evento_editar.getText().toString().trim().equals("") && !edt_descripcion_evento_editar.getText().toString().trim().equals("") && !tvMostrarFechaSeleccionada.getText().toString().equals("") && !tvMostrarHoraSeleccionada.getText().toString().equals("")) {
            if (edt_descripcion_evento_editar.getText().toString().length() > 100) {
                if (lilaImagenes.getChildCount() > 0) {
                    if (!verificarFecha()) {
                        progress.show();
                        date = new GregorianCalendar(año, mes, dia, hora, minutos);
                        time = date.getTimeInMillis();

                        NuevoEvento evento2 = new NuevoEvento();
                        evento2.setNombre(edt_nombre_evento_editar.getText().toString().trim());
                        evento2.setDescripcion(edt_descripcion_evento_editar.getText().toString().trim());
                        evento2.setFecha(String.valueOf(time));
                        evento2.setTipo(spTipo.getSelectedItem().toString().trim());
                        evento2.setLugar(spEstados.getSelectedItem().toString().trim());
                        evento2.setPropietario(mAuth.getCurrentUser().getEmail());
                        evento2.setCreacion(evento.getCreacion());
                        evento2.setSuscripciones(evento.getSuscripciones());
                        evento2.setImagen(imagenesEvento);

                        db.collection("eventos").document(evento.getId()).set(evento2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                progress.dismiss();
                                PrincipalActivity.updateEventosEditar(caller, evento2, posicion, evento, 1);
                                enviarNotificacion(evento2.getSuscripciones() , evento2.getNombre());
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
                    } else {
                        Toast.makeText(getApplicationContext(), "Se requiere una tolerancia de 2 horas para notificar el cambio del evento.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No se han seleccinado imagenes para el evento.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "La descripción del evento es muy corta.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Todos los campos son obligatorios para editar el evento.", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarNotificacion(ArrayList<String> suscripciones, String evento) {
        for (int i = 0; i<suscripciones.size(); i++){
            if (!suscripciones.get(i).equals("")){
                String [] arCorreo = suscripciones.get(i).split("[.]");
                dbReference = dbs.getReference(arCorreo[0]);
                Notificacion notificacion = new Notificacion();
                notificacion.setDescripcion("El evento "+ evento +" ha sido editado, reviselo");
                notificacion.setTipo("Edición de evento");
                notificacion.setRemitente(MainActivity.firebaseUser.getEmail());
                notificacion.setFecha(System.currentTimeMillis());
                dbReference.push().setValue(notificacion);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_eventos_editar, menu);
        return super.onCreateOptionsMenu(menu);
    }
}