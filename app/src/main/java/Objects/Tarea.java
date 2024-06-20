package Objects;

import java.util.List;

public class Tarea {
    private String uid;
    private String tid;
    private String titulo;
    private String descripcion;
    private String fecha;
    private String fechaCreacion;
    private String estado;
    private String filtro;
    public List<Subtarea> subtareas;


    public Tarea() {
        // Constructor vacío requerido para Firebase
    }



    public Tarea(String titulo, String descripcion, String fecha, String fechaCreacion, String estado, String tid, String uid, String filtro, List<Subtarea> subtareas) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.tid = tid;
        this.uid = uid;
        this.filtro = filtro;
        this.subtareas = subtareas;
    }

    public void AddSubtarea(Subtarea subtarea){
        subtareas.add(subtarea);
    }

    public Subtarea getSubtarea(String sid){
        for (Subtarea subtarea : subtareas) {
            if (subtarea.getSid().equals(sid)) {
                return subtarea;
            }
        }
        return null;
    }

    // Getters y setters

    public List<Subtarea> getSubtareas() {
        return subtareas;
    }

    public void setSubtareas(List<Subtarea> subtareas) {
        this.subtareas = subtareas;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
