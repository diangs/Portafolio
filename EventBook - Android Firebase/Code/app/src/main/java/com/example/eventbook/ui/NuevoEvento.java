package com.example.eventbook.ui;

import java.io.Serializable;
import java.util.ArrayList;

public class NuevoEvento implements Serializable {
    String nombre;
    String descripcion;
    String fecha;
    ArrayList<String> imagen;
    String lugar;
    String tipo;
    String propietario;
    Long creacion;
    ArrayList<String> suscripciones;

    public ArrayList<String> getSuscripciones() {
        return suscripciones;
    }

    public void setSuscripciones(ArrayList<String> suscripciones) {
        this.suscripciones = suscripciones;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public ArrayList<String> getImagen() {
        return imagen;
    }

    public void setImagen(ArrayList<String> imagenEvento) {
        this.imagen = imagenEvento;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public Long getCreacion() {
        return creacion;
    }

    public void setCreacion(Long creacion) {
        this.creacion = creacion;
    }
}
