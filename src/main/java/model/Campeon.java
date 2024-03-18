package model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Esta clase representa a un campeón en el sistema.
 * Cada campeón puede tener asociadas varias habilidades y pertenecer a múltiples regiones.
 */
@Entity
@Table(name = "campeon")
public class Campeon {

    @Id
    @Column(name = "id_campeon")
    int id_campeon;

    @Column(name = "nombre_campeon")
    String nombre;

    @Column(name = "apodo")
    String apodo;

    @Column(name = "campeones_con_relacion")
    int campeones_con_relacion;

    @Column(name = "biografia",length = 10485760)
    String biografia;

    @Column(name = "apariencion_en_cinematicas")
    String apariencion_en_cinematicas;

    @Column(name = "numero_de_relatos_cortos")
    int numero_de_relatos_cortos;

    @Column(name = "rol")
    String rol;

    @Column(name = "raza")
    String raza;

    @Column(name = "numero_de_aspectos")
    int numero_de_aspectos;

    @Column(name = "dificultad")
    String dificultad;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "campeones", cascade = CascadeType.ALL)
    private Set<Region> regiones = new HashSet<>();

    @OneToMany(mappedBy = "campeon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Habilidad> habilidades;

    /**
     * Constructor de la clase Campeon que inicializa todos los atributos de un campeón.
     *
     * @param id_campeon                 el ID del campeón
     * @param nombre                     el nombre del campeón
     * @param apodo                      el apodo del campeón
     * @param campeones_con_relacion     el número de campeones relacionados
     * @param biografia                  la biografía del campeón
     * @param apariencion_en_cinematicas   la aparición del campeón en cinemáticas
     * @param numero_de_relatos_cortos   el número de relatos cortos del campeón
     * @param rol                        el rol del campeón
     * @param raza                       la raza del campeón
     * @param numero_de_aspectos         el número de aspectos del campeón
     * @param dificultad                 la dificultad del campeón
     * @param habilidades                la lista de habilidades del campeón
     */
    public Campeon(int id_campeon, String nombre, String apodo, int campeones_con_relacion, String biografia, String apariencion_en_cinematicas, int numero_de_relatos_cortos, String rol, String raza, int numero_de_aspectos, String dificultad, List<Habilidad> habilidades) {
        this.id_campeon = id_campeon;
        this.nombre = nombre;
        this.apodo = apodo;
        this.campeones_con_relacion = campeones_con_relacion;
        this.biografia = biografia;
        this.apariencion_en_cinematicas = apariencion_en_cinematicas;
        this.numero_de_relatos_cortos = numero_de_relatos_cortos;
        this.rol = rol;
        this.raza = raza;
        this.numero_de_aspectos = numero_de_aspectos;
        this.dificultad = dificultad;
        this.habilidades = habilidades;
    }

    /**
     * Constructor de la clase Campeon.
     */
    public Campeon() {
    }

    public int getId_campeon() {
        return id_campeon;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public Set<Region> getRegiones() {
        return regiones;
    }

    public void setRegiones(Set<Region> regiones) {
        this.regiones = regiones;
    }

    public List<Habilidad> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<Habilidad> habilidades) {
        this.habilidades = habilidades;
    }
}
