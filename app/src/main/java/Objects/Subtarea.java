package Objects;

public class Subtarea {
    public String nombre;
    private String tid;
    private String sid;
    private String estado;

    public Subtarea() {
        //constructor vacio
    }

    public Subtarea(String nombre, String tid, String sid) {
        this.nombre = nombre;
        this.tid = tid;
        this.sid = sid;
        this.estado = "No Finalizado";
    }

    //Getters y Setters


    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
