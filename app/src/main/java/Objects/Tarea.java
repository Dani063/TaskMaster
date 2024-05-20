package Objects;

public class Tarea {
    private String uid;
    private String tid;
    private String titulo;
    private String descripcion;
    private String fecha;
    private String fechaCreacion;
    private String estado;


    public Tarea() {
        // Constructor vacío requerido para Firebase
    }

    public Tarea(String titulo, String descripcion, String fecha, String fechaCreacion, String estado, String tid, String uid) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.tid = tid;
        this.uid = uid;
    }

    // Getters y setters

    public String getuid() {
        return uid;
    }

    public void setuid(String uid) {
        uid = uid;
    }

    public String gettid() {
        return tid;
    }

    public void settid(String tid) {
        tid = tid;
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
