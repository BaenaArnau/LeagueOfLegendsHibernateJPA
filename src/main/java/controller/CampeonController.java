package controller;

import model.Campeon;
import model.Habilidad;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Controlador para la gestión de campeones en la base de datos.
 */
public class CampeonController {

    private int idCounter = 1;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private EntityTransaction transaction;

    public CampeonController() { }

    /**
     * Constructor que inicializa el controlador con una {@code EntityManagerFactory}.
     *
     * @param entityManagerFactory La fábrica de entity manager a utilizar.
     */
    public CampeonController(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManagerFactory.createEntityManager();
        this.transaction = entityManager.getTransaction();
    }

    /**
     * Obtiene una representación de cadena de todos los campeones almacenados en la base de datos.
     *
     * @return Una cadena que representa la lista de campeones.
     */
    public String listarCampeones() {
        StringBuilder builder = new StringBuilder();
        builder.append("┌───────────┬──────────────┬─────────────────────┬───────────────┬──────────────────────────────────┐\n");
        builder.append(String.format("│ %-9s │ %-13s │ %-18s │ %-13s │ %-28s │\n", "ID", "Nombre", "Apodo", "Rol", "Dificultad"));
        builder.append("├───────────┼──────────────┼─────────────────────┼───────────────┼──────────────────────────────────┤\n");

        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT c FROM Campeon c");
            List<Campeon> campeones = query.getResultList();

            for (Campeon campeon : campeones) {
                builder.append(String.format("│ %-9s │ %-13s │ %-18s │ %-13s │ %-28s │\n",
                        campeon.getId_campeon(),
                        (campeon.getNombre().length() > 13) ? campeon.getNombre().substring(0, 10) + "..." : campeon.getNombre(),
                        (campeon.getApodo().length() > 18) ? campeon.getApodo().substring(0, 15) + "..." : campeon.getApodo(),
                        (campeon.getRol().length() > 13) ? campeon.getRol().substring(0, 10) + "..." : campeon.getRol(),
                        (campeon.getDificultad().length() > 28) ? campeon.getDificultad().substring(0, 25) + "..." : campeon.getDificultad()));
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        builder.append("└───────────┴──────────────┴─────────────────────┴───────────────┴──────────────────────────────────┘\n");
        return builder.toString();
    }

    /**
     * Lee datos de campeones desde un archivo CSV y los agrega a la base de datos.
     *
     * @param filePath La ruta del archivo CSV que contiene los datos de los campeones.
     * @throws IOException Si ocurre un error de lectura del archivo.
     */
    public void readCampeonFromFile(String filePath) throws IOException {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Ignoramos la primera línea que contiene los encabezados
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // Expresión regular para evitar separar comas dentro de comillas
                if (data.length == 10) { // Comprobamos que haya 10 elementos en la línea
                    try {
                        // Eliminamos las comillas de los números antes de convertirlos
                        int campeones_con_relacion = Integer.parseInt(data[2].replaceAll("\"", "").trim());
                        int numero_de_relatos_cortos = Integer.parseInt(data[5].replaceAll("\"", "").trim());
                        int numero_de_aspectos = Integer.parseInt(data[8].replaceAll("\"", "").trim());

                        // Creamos el campeón con los datos del archivo
                        Campeon campeon = new Campeon(
                                idCounter++,
                                data[0].replaceAll("\"", "").trim(), // nombre
                                data[1].replaceAll("\"", "").trim(), // apodo
                                campeones_con_relacion,
                                data[3].replaceAll("\"", "").trim(), // biografia
                                data[4].replaceAll("\"", "").trim(), // apariencion_en_cinematicas
                                numero_de_relatos_cortos,
                                data[6].replaceAll("\"", "").trim(), // rol
                                data[7].replaceAll("\"", "").trim(), // raza
                                numero_de_aspectos,
                                data[9].replaceAll("\"", "").trim(), // dificultad
                                new ArrayList<>()
                        );

                        // Guardar el campeón en la base de datos
                        em.persist(campeon);
                        System.out.println("Campeon añadido a la base de datos");
                    } catch (NumberFormatException e) {
                        // Manejar excepción si no se puede convertir un valor numérico
                        System.err.println("Error al convertir un valor numérico: " + e.getMessage());
                    }
                } else {
                    System.err.println("Formato incorrecto de la línea: " + line);
                }
            }
            // Commit de la transacción después de procesar todas las líneas del archivo
            em.getTransaction().commit();
        }
    }

    /**
     * Elimina la tabla de campeones de la base de datos.
     */
    public void eliminarTablaCampeon() {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createNativeQuery("DROP TABLE IF EXISTS campeon").executeUpdate();
            transaction.commit();
            System.out.println("Tabla 'campeon' eliminada correctamente.");
        } catch (PersistenceException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Error al eliminar la tabla 'campeon': " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Crea la tabla de campeones en la base de datos.
     */
    public void crearTablaCampeon() {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createNativeQuery("CREATE TABLE campeon (id_campeon INT PRIMARY KEY, nombre VARCHAR(255), apodo VARCHAR(255), rol VARCHAR(255), dificultad VARCHAR(255))").executeUpdate();
            transaction.commit();
            System.out.println("Tabla 'campeon' creada correctamente.");
        } catch (PersistenceException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Error al crear la tabla 'campeon': " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Busca campeones cuyos nombres o apodos contienen el texto especificado.
     *
     * @param texto El texto a buscar en los nombres y apodos de los campeones.
     * @return Una cadena que representa los campeones encontrados.
     */
    public String buscarCampeonPorTexto(String texto) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            String queryStr = "SELECT c FROM Campeon c WHERE c.nombre LIKE :texto OR c.apodo LIKE :texto";
            Query query = em.createQuery(queryStr);
            query.setParameter("texto", "%" + texto + "%");
            List<Campeon> campeones = query.getResultList();
            em.getTransaction().commit();

            StringBuilder result = new StringBuilder();
            result.append("| ID | Nombre                | Apodo             | Rol            | Dificultad |\n");
            result.append("|----|-----------------------|-------------------|----------------|------------|\n");
            for (Campeon campeon : campeones) {
                result.append("| ")
                        .append(padRight(String.valueOf(campeon.getId_campeon()), 3))
                        .append(" | ")
                        .append(padRight(campeon.getNombre(), 23))
                        .append(" | ")
                        .append(padRight(campeon.getApodo(), 17))
                        .append(" | ")
                        .append(padRight(campeon.getRol(), 15))
                        .append(" | ")
                        .append(campeon.getDificultad())
                        .append("          |\n");
            }
            return result.toString();
        } finally {
            em.close();
        }
    }

    /**
     * Busca campeones por su rol en el juego.
     *
     * @param rol El rol a buscar.
     * @return Una cadena que representa los campeones encontrados.
     */
    public String buscarCampeonesPorRol(String rol) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            String queryStr = "SELECT c FROM Campeon c WHERE c.rol = :rol";
            Query query = em.createQuery(queryStr);
            query.setParameter("rol", rol);
            List<Campeon> campeones = query.getResultList();
            em.getTransaction().commit();

            StringBuilder result = new StringBuilder();
            result.append("| ID | Nombre                | Apodo             | Rol            | Dificultad |\n");
            result.append("|----|-----------------------|-------------------|----------------|------------|\n");
            for (Campeon campeon : campeones) {
                result.append("| ")
                        .append(padRight(String.valueOf(campeon.getId_campeon()), 3))
                        .append(" | ")
                        .append(padRight(campeon.getNombre(), 23))
                        .append(" | ")
                        .append(padRight(campeon.getApodo(), 17))
                        .append(" | ")
                        .append(padRight(campeon.getRol(), 15))
                        .append(" | ")
                        .append(campeon.getDificultad())
                        .append("          |\n");
            }
            return result.toString();
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene la información de un campeón por su ID.
     *
     * @param id El ID del campeón a buscar.
     * @return Una cadena que representa la información del campeón encontrado.
     */
    public String obtenerCampeonPorId(int id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            Campeon campeon = em.find(Campeon.class, id);
            em.getTransaction().commit();

            StringBuilder result = new StringBuilder();
            result.append("| ID | Nombre                | Apodo             | Rol            | Dificultad |\n");
            result.append("|----|-----------------------|-------------------|----------------|------------|\n");
            result.append("| ")
                    .append(padRight(String.valueOf(campeon.getId_campeon()), 3))
                    .append(" | ")
                    .append(padRight(campeon.getNombre(), 23))
                    .append(" | ")
                    .append(padRight(campeon.getApodo(), 17))
                    .append(" | ")
                    .append(padRight(campeon.getRol(), 15))
                    .append(" | ")
                    .append(campeon.getDificultad())
                    .append("          |\n");
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
     * Modifica la información de un campeón por su ID.
     *
     * @param id El ID del campeón a modificar.
     */
    public void modificarCampeonPorId(int id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        Scanner scanner = new Scanner(System.in);
        try {
            transaction.begin();
            Campeon campeon = em.find(Campeon.class, id);
            if (campeon != null) {
                System.out.println("Ingrese los nuevos valores para el campeón con ID " + id + ":");

                // Modificar el nombre
                System.out.print("Nombre (dejar vacío para mantener el valor anterior): ");
                String nombre = scanner.nextLine();
                if (!nombre.isEmpty()) {
                    campeon.setNombre(nombre);
                }

                // Modificar el apodo
                System.out.print("Apodo (dejar vacío para mantener el valor anterior): ");
                String apodo = scanner.nextLine();
                if (!apodo.isEmpty()) {
                    campeon.setApodo(apodo);
                }

                // Modificar el rol
                System.out.print("Rol (dejar vacío para mantener el valor anterior): ");
                String rol = scanner.nextLine();
                if (!rol.isEmpty()) {
                    campeon.setRol(rol);
                }

                // Modificar la dificultad
                System.out.print("Dificultad (dejar vacío para mantener el valor anterior): ");
                String dificultad = scanner.nextLine();
                if (!dificultad.isEmpty()) {
                    campeon.setDificultad(dificultad);
                }

                em.merge(campeon);
                transaction.commit();
                System.out.println("Registro modificado correctamente.");
            } else {
                System.out.println("No se encontró ningún campeón con la ID especificada.");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Error al modificar el campeón: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Elimina un campeón de la base de datos por su ID.
     *
     * @param id El ID del campeón a eliminar.
     */
    public void eliminarCampeonPorId(int id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Campeon campeon = em.find(Campeon.class, id);
            if (campeon != null) {
                em.remove(campeon);
                transaction.commit();
                System.out.println("Registro eliminado correctamente.");
            } else {
                System.out.println("No se encontró ningún campeón con la ID especificada.");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Error al eliminar el campeón: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Elimina campeones de la base de datos que aparecen o no en cinemáticas, según la respuesta del usuario.
     */
    public void eliminarCampeonPorCinematicas() {
        Scanner scanner = new Scanner(System.in);
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            // Solicitar al usuario si el campeón aparece en cinemáticas
            System.out.println("¿El campeón aparece en cinemáticas? (s/n)");
            char respuesta = scanner.next().charAt(0);
            boolean apareceEnCinematicas = (respuesta == 's' || respuesta == 'S');

            // Iniciar transacción
            transaction.begin();

            // Consultar los campeones que aparecen o no en cinemáticas
            String queryString = "SELECT c FROM Campeon c WHERE c.apareceEnCinematicas = :apareceEnCinematicas";
            Query query = em.createQuery(queryString);
            query.setParameter("apareceEnCinematicas", apareceEnCinematicas);
            List<Campeon> campeones = query.getResultList();

            // Eliminar los campeones obtenidos
            for (Campeon campeon : campeones) {
                em.remove(campeon);
            }

            // Confirmar la transacción
            transaction.commit();

            System.out.println("Campeones eliminados correctamente.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Error al eliminar los campeones: " + e.getMessage());
        } finally {
            // Cerrar el EntityManager
            em.close();
        }
    }
}
