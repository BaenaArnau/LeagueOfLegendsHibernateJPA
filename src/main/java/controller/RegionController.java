package controller;

import model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;

/**
 * Controlador para la gestión de regiones en la base de datos.
 */
public class RegionController {

    private int idCounter = 1;
    private EntityManagerFactory entityManagerFactory;

    /**
     * Constructor vacío de la clase RegionController.
     */
    public RegionController() { }

    /**
     * Constructor que inicializa el controlador con una {@code EntityManagerFactory}.
     *
     * @param entityManagerFactory La fábrica de entity manager a utilizar.
     */
    public RegionController(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Obtiene una representación de cadena de todas las regiones almacenadas en la base de datos.
     *
     * @return Una cadena que representa la lista de regiones.
     */
    public String listarRegiones() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT r FROM Region r");
            List<Region> regiones = query.getResultList();
            em.getTransaction().commit();

            // Determinar la longitud máxima para cada columna
            int maxLengthId = 0;
            int maxLengthNombre = 0;
            int maxLengthDescripcion = 0;
            int maxLengthHistorias = 0;
            for (Region region : regiones) {
                maxLengthId = Math.max(maxLengthId, String.valueOf(region.getId_region()).length());
                maxLengthNombre = Math.max(maxLengthNombre, region.getNombre_region().length());
                maxLengthDescripcion = Math.max(maxLengthDescripcion, region.getDescripcion().length());
                maxLengthHistorias = Math.max(maxLengthHistorias, String.valueOf(region.getHistorias_relacionadas()).length());
            }

            // Construir la salida como una tabla formateada
            StringBuilder builder = new StringBuilder();
            builder.append(StringUtils.rightPad("ID", maxLengthId)).append(" | ");
            builder.append(StringUtils.rightPad("Nombre", maxLengthNombre)).append(" | ");
            builder.append(StringUtils.rightPad("Descripción", maxLengthDescripcion)).append(" | ");
            builder.append(StringUtils.rightPad("Historias", maxLengthHistorias)).append(" | ");
            builder.append("Campeones").append("\n");

            for (Region region : regiones) {
                builder.append(StringUtils.rightPad(String.valueOf(region.getId_region()), maxLengthId)).append(" | ");
                builder.append(StringUtils.rightPad(region.getNombre_region(), maxLengthNombre)).append(" | ");
                builder.append(StringUtils.rightPad(region.getDescripcion(), maxLengthDescripcion)).append(" | ");
                builder.append(StringUtils.rightPad(String.valueOf(region.getHistorias_relacionadas()), maxLengthHistorias)).append(" | ");
                // Añadir campeones
                String campeones = region.getCampeones().stream().map(Campeon::getNombre).collect(Collectors.joining(", "));
                builder.append(campeones).append("\n");
            }

            return builder.toString();
        } finally {
            em.close();
        }
    }

    /**
     * Lee datos de regiones desde un archivo CSV y los agrega a la base de datos.
     *
     * @param filePath La ruta del archivo CSV que contiene los datos de las regiones.
     */
    public void readRegionFromFile(String filePath) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        File csvFile = new File(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Saltar la primera línea (encabezados)

            String line;
            String cvsSplitBy = ",";

            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);

                System.out.println("Id " + idCounter);
                System.out.println("Nombre: " + data[0]);
                System.out.println("Descripción: " + data[1]);
                System.out.println("Historias relacionadas: " + data[2]);

                // Crear un objeto Región y asignar los valores desde el CSV
                Region region = new Region();
                region.setId_region(idCounter++);
                region.setNombre_region(data[0]);
                region.setDescripcion(data[1]);
                region.setHistorias_relacionadas(Integer.parseInt(data[2]));

                // Inicializar el conjunto de campeones
                Set<Campeon> campeones = new HashSet<>();

                // Asociar campeones con la región
                for (int i = 3; i < data.length; i++) {
                    int idCampeon = Integer.parseInt(data[i]);
                    Campeon campeon = em.find(Campeon.class, idCampeon);
                    if (campeon != null) {
                        campeones.add(campeon);
                    }
                }

                // Establecer el conjunto de campeones en la región
                region.setCampeones(campeones);

                // Persistir la región en la base de datos
                em.persist(region);
            }

            // Commit de la transacción después de procesar todas las líneas del archivo
            em.getTransaction().commit();
        } catch (IOException e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Elimina la tabla de regiones y la tabla intermedia de relación entre regiones y campeones de la base de datos.
     */
    public void eliminarTablaRegion() {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Eliminar la tabla intermedia "region_campeon"
            em.createNativeQuery("DROP TABLE IF EXISTS region_campeon").executeUpdate();
            // Eliminar la tabla "region"
            em.createNativeQuery("DROP TABLE IF EXISTS region").executeUpdate();
            transaction.commit();
            System.out.println("Tablas 'region' y 'region_campeon' eliminadas correctamente.");
        } catch (PersistenceException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Error al eliminar las tablas: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Crea la tabla de regiones y la tabla intermedia de relación entre regiones y campeones en la base de datos.
     */
    public void crearTablaRegion() {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Crear la tabla "region"
            em.createNativeQuery("CREATE TABLE IF NOT EXISTS region (id_region INT PRIMARY KEY, nombre_region VARCHAR(255), descripcion_region TEXT, historias_relacionadas INT)").executeUpdate();
            // Crear la tabla intermedia "region_campeon"
            em.createNativeQuery("CREATE TABLE IF NOT EXISTS region_campeon (id_region INT, id_campeon INT, PRIMARY KEY (id_region, id_campeon), FOREIGN KEY (id_region) REFERENCES region(id_region), FOREIGN KEY (id_campeon) REFERENCES campeon(id_campeon))").executeUpdate();
            transaction.commit();
            System.out.println("Tablas 'region' y 'region_campeon' creadas correctamente.");
        } catch (PersistenceException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Error al crear las tablas: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Busca regiones cuya descripción contenga el texto especificado.
     *
     * @param texto El texto a buscar en las descripciones de las regiones.
     * @return Una cadena que representa las regiones encontradas.
     */
    public String buscarRegionPorTexto(String texto) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            String queryStr = "SELECT r FROM Region r WHERE r.descripcion LIKE :texto";
            Query query = em.createQuery(queryStr);
            query.setParameter("texto", "%" + texto + "%");
            List<Region> regiones = query.getResultList();
            em.getTransaction().commit();

            StringBuilder result = new StringBuilder();
            result.append("| ID | Nombre        | Descripción             | Historias |\n");
            result.append("|----|---------------|-------------------------|-----------|\n");
            for (Region region : regiones) {
                result.append("| ")
                        .append(region.getId_region())
                        .append(" | ")
                        .append(padRight(region.getNombre_region(), 13))
                        .append(" | ")
                        .append(padRight(region.getDescripcion(), 23))
                        .append(" | ")
                        .append(region.getHistorias_relacionadas())
                        .append(" |\n");
            }
            return result.toString();
        } finally {
            em.close();
        }
    }

    /**
     * Busca regiones cuyo número de historias relacionadas sea mayor que la cantidad especificada.
     *
     * @param cantidadHistorias La cantidad mínima de historias relacionadas para las regiones a buscar.
     * @return Una cadena que representa las regiones encontradas.
     */
    public String buscarRegionesPorHistorias(int cantidadHistorias) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            String queryStr = "SELECT r FROM Region r WHERE r.historias_relacionadas > :cantidadHistorias";
            Query query = em.createQuery(queryStr);
            query.setParameter("cantidadHistorias", cantidadHistorias);
            List<Region> regiones = query.getResultList();
            em.getTransaction().commit();

            StringBuilder result = new StringBuilder();
            result.append("| ID | Nombre        | Descripción             | Historias |\n");
            result.append("|----|---------------|-------------------------|-----------|\n");
            for (Region region : regiones) {
                result.append("| ")
                        .append(region.getId_region())
                        .append(" | ")
                        .append(padRight(region.getNombre_region(), 13))
                        .append(" | ")
                        .append(padRight(region.getDescripcion(), 23))
                        .append(" | ")
                        .append(region.getHistorias_relacionadas())
                        .append(" |\n");
            }
            return result.toString();
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene la información de una región por su ID.
     *
     * @param id El ID de la región a buscar.
     * @return Una cadena que representa la información de la región encontrada.
     */
    public String obtenerRegionPorId(int id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            Region region = em.find(Region.class, id);
            em.getTransaction().commit();
            StringBuilder result = new StringBuilder();
            if (region != null) {
                result.append("| ID | Nombre        | Descripción             | Historias |\n");
                result.append("|----|---------------|-------------------------|-----------|\n");
                result.append("| ")
                        .append(region.getId_region())
                        .append(" | ")
                        .append(padRight(region.getNombre_region(), 13))
                        .append(" | ")
                        .append(padRight(region.getDescripcion(), 23))
                        .append(" | ")
                        .append(region.getHistorias_relacionadas())
                        .append(" |\n");
            } else {
                result.append("No se encontró ninguna región con el ID: ").append(id);
            }
            return result.toString();
        } finally {
            em.close();
        }
    }

    /**
     * Rellena una cadena con espacios en blanco para que tenga una longitud específica.
     *
     * @param input  La cadena a rellenar.
     * @param length La longitud deseada de la cadena.
     * @return La cadena rellenada con espacios en blanco.
     */
    private String padRight(String input, int length) {
        if (input.length() >= length) {
            return input;
        }
        StringBuilder padded = new StringBuilder(input);
        while (padded.length() < length) {
            padded.append(" ");
        }
        return padded.toString();
    }

    /**
     * Modifica la información de una región por su ID.
     *
     * @param id El ID de la región a modificar.
     */
    public void modificarRegionPorId(int id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        Scanner scanner = new Scanner(System.in);
        try {
            transaction.begin();
            Region region = em.find(Region.class, id);
            if (region != null) {
                System.out.println("Ingrese los nuevos valores para la región con ID " + id + ":");

                // Modificar el nombre
                System.out.print("Nombre (dejar vacío para mantener el valor anterior): ");
                String nombre = scanner.nextLine();
                if (!nombre.isEmpty()) {
                    region.setNombre_region(nombre);
                }

                // Modificar la descripción
                System.out.print("Descripción (dejar vacío para mantener el valor anterior): ");
                String descripcion = scanner.nextLine();
                if (!descripcion.isEmpty()) {
                    region.setDescripcion(descripcion);
                }

                // Modificar el número de historias relacionadas
                System.out.print("Número de historias relacionadas (dejar vacío para mantener el valor anterior): ");
                String historiasInput = scanner.nextLine();
                if (!historiasInput.isEmpty()) {
                    int historias = Integer.parseInt(historiasInput);
                    region.setHistorias_relacionadas(historias);
                }

                em.merge(region);
                transaction.commit();
                System.out.println("Registro modificado correctamente.");
            } else {
                System.out.println("No se encontró ninguna región con la ID especificada.");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Error al modificar la región: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una región de la base de datos por su ID.
     *
     * @param id El ID de la región a eliminar.
     */
    public void eliminarRegionPorId(int id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Region region = em.find(Region.class, id);
            if (region != null) {
                em.remove(region);
                transaction.commit();
                System.out.println("Registro eliminado correctamente.");
            } else {
                System.out.println("No se encontró ninguna región con la ID especificada.");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Error al eliminar la región: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}
