package com.example.eventbook.ui;

import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Evento implements Serializable {

    String id;
    String correo;
    String evento;
    Boolean suscrito;
    String imagenUsuario;
    ArrayList<String> imagenesEvento;
    String descripcion;
    String lugar;
    String tipo;
    String fecha;

    public Long getCreacion() {
        return creacion;
    }

    public void setCreacion(Long creacion) {
        this.creacion = creacion;
    }

    Long creacion;

    public ArrayList<String> getSuscripciones() {
        return suscripciones;
    }

    public void setSuscripciones(ArrayList<String> suscripciones) {
        this.suscripciones = suscripciones;
    }

    ArrayList<String> suscripciones;

    public String getDescripcionLarga() {
        return descripcionLarga;
    }

    public void setDescripcionLarga(String descripcionLarga) {
        this.descripcionLarga = descripcionLarga;
    }

    String descripcionLarga;
    Boolean propietario;

    public Boolean getPropietario() {
        return propietario;
    }

    public void setPropietario(Boolean propietario) {
        this.propietario = propietario;
    }

    public Boolean getSuscrito() {
        return suscrito;
    }

    public void setSuscrito(Boolean suscrito) {
        this.suscrito = suscrito;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getImagenUsuario() {
        return imagenUsuario;
    }

    public void setImagenUsuario(String imagenUsuario) {
        this.imagenUsuario = imagenUsuario;
    }

    public ArrayList<String> getImagenesEvento() {
        return imagenesEvento;
    }

    public void setImagenesEvento(ArrayList<String> imagenesEvento) {
        this.imagenesEvento = imagenesEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
