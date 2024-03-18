package model;

import javax.persistence.*;

/**
 * Esta clase representa una habilidad en el sistema.
 * Una habilidad puede pertenecer a un único campeón.
 */
@Entity
@Table(name = "habilidad")
public class Habilidad {

    @Id
    @Column(name = "nombre_habilidad")
    String nombre;

    @Column(name = "pasiva")
    boolean pasiva;

    @Column(name = "asignacion_de_tecla")
    char asignacion_de_tecla;

    @Column(name = "descripcion_habilidad",length = 10485760)
    String descripcion;

    @Column(name = "link")
    String link;

    @ManyToOne
    @JoinColumn(name = "id_campeon")
    private Campeon campeon;

    /**
     * Constructor vacío de la clase Habilidad.
     */
    public Habilidad() {
    }

    /**
     * Constructor de la clase Habilidad.
     *
     * @param nombre el nombre de la habilidad
     * @param pasiva indica si la habilidad es pasiva o no
     * @param asignacion_de_tecla la tecla asignada para la habilidad
     * @param descripcion la descripción de la habilidad
     * @param link el enlace relacionado con la habilidad
     * @param campeon el campeón al que pertenece la habilidad
     */
    public Habilidad(String nombre, boolean pasiva, char asignacion_de_tecla, String descripcion, String link, Campeon campeon) {
        this.nombre = nombre;
        this.pasiva = pasiva;
        this.asignacion_de_tecla = asignacion_de_tecla;
        this.descripcion = descripcion;
        this.link = link;
        this.campeon = campeon;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isPasiva() {
        return pasiva;
    }

    public void setPasiva(boolean pasiva) {
        this.pasiva = pasiva;
    }

    public char getAsignacion_de_tecla() {
        return asignacion_de_tecla;
    }

    public void setAsignacion_de_tecla(char asignacion_de_tecla) {
        this.asignacion_de_tecla = asignacion_de_tecla;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Campeon getCampeon() {
        return campeon;
    }

    public void setCampeon(Campeon campeon) {
        this.campeon = campeon;
    }

    @Override
    public String toString() {
        return "Habilidad{" +
                "nombre='" + nombre + '\'' +
                ", pasiva=" + pasiva +
                ", asignacion_de_tecla=" + asignacion_de_tecla +
                ", descripcion='" + descripcion + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
