package model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Esta clase representa una región en el sistema.
 * Una región puede tener varios campeones asociados.
 */
@Entity
@Table(name = "region")
public class Region {

    @Id
    @Column(name = "id_region")
    int id_region;

    @Column(name = "nombre_region")
    String nombre_region;

    @Column(name = "descripcion_region", length = 10485760)
    String descripcion;

    @Column(name = "historias_relacionadas")
    int historias_relacionadas;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "region_campeon",
            joinColumns = @JoinColumn(name = "id_region"),
            inverseJoinColumns = @JoinColumn(name = "id_campeon")
    )
    private Set<Campeon> campeones = new HashSet<>();

    /**
     * Obtiene los campeones asociados a esta región.
     *
     * @return el conjunto de campeones asociados a la región
     */
    public Set<Campeon> getCampeones() {
        return campeones;
    }

    /**
     * Establece los campeones asociados a esta región.
     *
     * @param campeones el conjunto de campeones a establecer
     */
    public void setCampeones(Set<Campeon> campeones) {
        this.campeones = campeones;
    }

    /**
     * Obtiene el ID de la región.
     *
     * @return el ID de la región
     */
    public int getId_region() {
        return id_region;
    }

    /**
     * Establece el ID de la región.
     *
     * @param id_region el ID de la región a establecer
     */
    public void setId_region(int id_region) {
        this.id_region = id_region;
    }

    /**
     * Obtiene el nombre de la región.
     *
     * @return el nombre de la región
     */
    public String getNombre_region() {
        return nombre_region;
    }

    /**
     * Establece el nombre de la región.
     *
     * @param nombre_region el nombre de la región a establecer
     */
    public void setNombre_region(String nombre_region) {
        this.nombre_region = nombre_region;
    }

    /**
     * Obtiene la descripción de la región.
     *
     * @return la descripción de la región
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la región.
     *
     * @param descripcion la descripción de la región a establecer
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el número de historias relacionadas con la región.
     *
     * @return el número de historias relacionadas
     */
    public int getHistorias_relacionadas() {
        return historias_relacionadas;
    }

    /**
     * Establece el número de historias relacionadas con la región.
     *
     * @param historias_relacionadas el número de historias relacionadas a establecer
     */
    public void setHistorias_relacionadas(int historias_relacionadas) {
        this.historias_relacionadas = historias_relacionadas;
    }

    /**
     * Constructor vacío de la clase Region.
     */
    public Region() {
    }

    /**
     * Constructor de la clase Region.
     *
     * @param id_region el ID de la región
     * @param nombre_region el nombre de la región
     * @param descripcion la descripción de la región
     * @param historias_relacionadas el número de historias relacionadas
     * @param campeones el conjunto de campeones asociados a la región
     */
    public Region(int id_region, String nombre_region, String descripcion, int historias_relacionadas, Set<Campeon> campeones) {
        this.id_region = id_region;
        this.nombre_region = nombre_region;
        this.descripcion = descripcion;
        this.historias_relacionadas = historias_relacionadas;
        this.campeones = campeones;
    }
}