package controller;

import model.Campeon;
import model.Habilidad;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Controlador para la gestión de habilidades en la base de datos.
 */
public class HabilidadController {

  private EntityManagerFactory entityManagerFactory;
  private CampeonController campeonController;

  /**
   * Constructor vacío de la clase HabilidadController.
   */
  public HabilidadController() { }

  /**
   * Constructor que inicializa el controlador con una {@code EntityManagerFactory} y un {@code CampeonController}.
   *
   * @param entityManagerFactory La fábrica de entity manager a utilizar.
   * @param campeonController     El controlador de campeones asociado.
   */
  public HabilidadController(EntityManagerFactory entityManagerFactory, CampeonController campeonController) {
    this.entityManagerFactory = entityManagerFactory;
    this.campeonController = campeonController;
  }

  /**
   * Obtiene una representación de cadena de todas las habilidades almacenadas en la base de datos.
   *
   * @return Una cadena que representa la lista de habilidades.
   */
  public String listarHabilidades() {
    EntityManager em = entityManagerFactory.createEntityManager();
    try {
      em.getTransaction().begin();
      Query query = em.createQuery("SELECT h FROM Habilidad h");
      List<Habilidad> habilidades = query.getResultList();
      em.getTransaction().commit();

      // Determinar la longitud máxima para cada columna
      int maxLengthNombre = 0;
      int maxLengthPasiva = 5; // Longitud máxima de "Sí/No"
      int maxLengthTecla = 0;
      int maxLengthDescripcion = 0;
      int maxLengthLink = 0;
      int maxLengthCampeon = 0;
      for (Habilidad habilidad : habilidades) {
        maxLengthNombre = Math.max(maxLengthNombre, habilidad.getNombre().length());
        maxLengthTecla = Math.max(maxLengthTecla, String.valueOf(habilidad.getAsignacion_de_tecla()).length());
        maxLengthDescripcion = Math.max(maxLengthDescripcion, habilidad.getDescripcion().length());
        maxLengthLink = Math.max(maxLengthLink, habilidad.getLink().length());
        maxLengthCampeon = Math.max(maxLengthCampeon, habilidad.getCampeon().getNombre().length());
      }

      // Construir la salida como una tabla formateada
      StringBuilder builder = new StringBuilder();
      builder.append(StringUtils.rightPad("Nombre", maxLengthNombre)).append(" | ");
      builder.append(StringUtils.rightPad("Pasiva", maxLengthPasiva)).append(" | ");
      builder.append(StringUtils.rightPad("Tecla", maxLengthTecla)).append(" | ");
      builder.append(StringUtils.rightPad("Descripción", maxLengthDescripcion)).append(" | ");
      builder.append(StringUtils.rightPad("Link", maxLengthLink)).append(" | ");
      builder.append(StringUtils.rightPad("Campeón", maxLengthCampeon)).append("\n");

      for (Habilidad habilidad : habilidades) {
        builder.append(StringUtils.rightPad(habilidad.getNombre(), maxLengthNombre)).append(" | ");
        builder.append(StringUtils.rightPad(habilidad.isPasiva() ? "Sí" : "No", maxLengthPasiva)).append(" | ");
        builder.append(StringUtils.rightPad(String.valueOf(habilidad.getAsignacion_de_tecla()), maxLengthTecla)).append(" | ");
        builder.append(StringUtils.rightPad(habilidad.getDescripcion(), maxLengthDescripcion)).append(" | ");
        builder.append(StringUtils.rightPad(habilidad.getLink(), maxLengthLink)).append(" | ");
        builder.append(StringUtils.rightPad(habilidad.getCampeon().getNombre(), maxLengthCampeon)).append("\n");
      }

      return builder.toString();
    } finally {
      em.close();
    }
  }

  /**
   * Lee datos de habilidades desde un archivo CSV y los agrega a la base de datos.
   *
   * @param filePath La ruta del archivo CSV que contiene los datos de las habilidades.
   */
  public void readHabilidadFromFile(String filePath) {
    EntityManager em = entityManagerFactory.createEntityManager();
    em.getTransaction().begin();

    try {
      // Leer el archivo CSV
      String line = "";
      String cvsSplitBy = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"; // Expresión regular para evitar dividir comas dentro de comillas dobles
      try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        String[] header = br.readLine().split(cvsSplitBy); // Leer el encabezado del CSV

        while ((line = br.readLine()) != null) {
          String[] data = line.split(cvsSplitBy);

          // Crear un objeto Habilidad y asignar los valores desde el CSV
          Habilidad habilidad = new Habilidad();
          habilidad.setNombre(data[1].replaceAll("\"", "")); // Eliminar las comillas dobles del nombre
          habilidad.setPasiva(Boolean.parseBoolean(data[2].replaceAll("\"", "")));
          char[] asignacionDeTecla = data[3].replaceAll("\"", "").toCharArray();
          habilidad.setAsignacion_de_tecla(asignacionDeTecla[0]);
          habilidad.setDescripcion(data[4].replaceAll("\"", ""));
          habilidad.setLink(data[5].replaceAll("\"", ""));

          System.out.println(habilidad.toString());

          // Obtener el campeón asociado a esta habilidad
          String nombreCampeon = data[0].replaceAll("\"", "");
          Campeon campeon = em.createQuery("SELECT c FROM Campeon c WHERE c.nombre = :nombre", Campeon.class)
                  .setParameter("nombre", nombreCampeon)
                  .getSingleResult();

          habilidad.setCampeon(campeon);
          em.persist(habilidad);
        }
        em.getTransaction().commit();
      } catch (IOException e) {
        e.printStackTrace();
        em.getTransaction().rollback();
      } finally {
        em.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Elimina la tabla de habilidades de la base de datos.
   */
  public void eliminarTablaHabilidad() {
    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();
    try {
      transaction.begin();
      em.createNativeQuery("DROP TABLE IF EXISTS habilidad").executeUpdate();
      transaction.commit();
      System.out.println("Tabla 'habilidad' eliminada correctamente.");
    } catch (PersistenceException e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      System.out.println("Error al eliminar la tabla 'habilidad': " + e.getMessage());
    } finally {
      em.close();
    }
  }

  /**
   * Crea la tabla de habilidades en la base de datos.
   */
  public void crearTablaHabilidad() {
    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();
    try {
      transaction.begin();
      em.createNativeQuery("CREATE TABLE habilidad (nombre_habilidad VARCHAR(255) PRIMARY KEY, pasiva BOOLEAN, asignacion_de_tecla CHAR, descripcion_habilidad TEXT, link VARCHAR(255), id_campeon INT, FOREIGN KEY (id_campeon) REFERENCES campeon (id_campeon))").executeUpdate();
      transaction.commit();
      System.out.println("Tabla 'habilidad' creada correctamente.");
    } catch (PersistenceException e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      System.out.println("Error al crear la tabla 'habilidad': " + e.getMessage());
    } finally {
      em.close();
    }
  }

  /**
   * Busca habilidades cuyos nombres o descripciones contienen el texto especificado.
   *
   * @param texto El texto a buscar en los nombres y descripciones de las habilidades.
   * @return Una cadena que representa las habilidades encontradas.
   */
  public String buscarHabilidadPorTexto(String texto) {
    EntityManager em = entityManagerFactory.createEntityManager();
    try {
      em.getTransaction().begin();
      String queryStr = "SELECT h FROM Habilidad h WHERE h.nombre LIKE :texto OR h.descripcion LIKE :texto";
      Query query = em.createQuery(queryStr);
      query.setParameter("texto", "%" + texto + "%");
      List<Habilidad> habilidades = query.getResultList();
      em.getTransaction().commit();

      StringBuilder result = new StringBuilder();
      result.append("| Nombre                | Pasiva | Tecla | Descripción           | Enlace |\n");
      result.append("|-----------------------|--------|-------|-----------------------|--------|\n");
      for (Habilidad habilidad : habilidades) {
        result.append("| ")
                .append(padRight(habilidad.getNombre(), 23))
                .append(" | ")
                .append(habilidad.isPasiva() ? "Sí    " : "No    ")
                .append(" | ")
                .append(habilidad.getAsignacion_de_tecla())
                .append("     | ")
                .append(padRight(habilidad.getDescripcion(), 23))
                .append(" | ")
                .append(padRight(habilidad.getLink(), 6))
                .append(" |\n");
      }
      return result.toString();
    } finally {
      em.close();
    }
  }

  /**
   * Busca habilidades asociadas a un campeón por su ID.
   *
   * @param idCampeon El ID del campeón cuyas habilidades se quieren buscar.
   * @return Una cadena que representa las habilidades encontradas.
   */
  public String buscarHabilidadPorIdCampeon(int idCampeon) {
    EntityManager em = entityManagerFactory.createEntityManager();
    try {
      em.getTransaction().begin();
      String queryStr = "SELECT h FROM Habilidad h WHERE h.campeon.id_campeon = :idCampeon";
      Query query = em.createQuery(queryStr);
      query.setParameter("idCampeon", idCampeon);
      List<Habilidad> habilidades = query.getResultList();
      em.getTransaction().commit();

      StringBuilder result = new StringBuilder();
      result.append("| Nombre                | Pasiva | Tecla | Descripción           | Enlace |\n");
      result.append("|-----------------------|--------|-------|-----------------------|--------|\n");
      for (Habilidad habilidad : habilidades) {
        result.append("| ")
                .append(padRight(habilidad.getNombre(), 23))
                .append(" | ")
                .append(habilidad.isPasiva() ? "Sí    " : "No    ")
                .append(" | ")
                .append(habilidad.getAsignacion_de_tecla())
                .append("     | ")
                .append(padRight(habilidad.getDescripcion(), 23))
                .append(" | ")
                .append(padRight(habilidad.getLink(), 6))
                .append(" |\n");
      }
      return result.toString();
    } finally {
      em.close();
    }
  }

  /**
   * Obtiene la información de una habilidad por su nombre.
   *
   * @param nombre El nombre de la habilidad a buscar.
   * @return Una cadena que representa la información de la habilidad encontrada.
   */
  public String obtenerHabilidadPorNombre(String nombre) {
    EntityManager em = entityManagerFactory.createEntityManager();
    try {
      em.getTransaction().begin();
      String queryStr = "SELECT h FROM Habilidad h WHERE h.nombre = :nombre";
      Query query = em.createQuery(queryStr);
      query.setParameter("nombre", nombre);
      Habilidad habilidad = (Habilidad) query.getSingleResult();
      em.getTransaction().commit();

      StringBuilder result = new StringBuilder();
      result.append("| Nombre                | Pasiva | Tecla | Descripción           | Enlace |\n");
      result.append("|-----------------------|--------|-------|-----------------------|--------|\n");
      result.append("| ")
              .append(padRight(habilidad.getNombre(), 23))
              .append(" | ")
              .append(habilidad.isPasiva() ? "Sí    " : "No    ")
              .append(" | ")
              .append(habilidad.getAsignacion_de_tecla())
              .append("     | ")
              .append(padRight(habilidad.getDescripcion(), 23))
              .append(" | ")
              .append(padRight(habilidad.getLink(), 6))
              .append(" |\n");
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
   * Modifica la información de una habilidad por su ID.
   *
   * @param id El ID de la habilidad a modificar.
   */
  public void modificarHabilidadPorId(String id) {
    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();
    Scanner scanner = new Scanner(System.in);
    try {
      transaction.begin();
      Habilidad habilidad = em.find(Habilidad.class, id);
      if (habilidad != null) {
        System.out.println("Ingrese los nuevos valores para la habilidad con ID " + id + ":");

        // Modificar el nombre
        System.out.print("Nombre (dejar vacío para mantener el valor anterior): ");
        String nombre = scanner.nextLine();
        if (!nombre.isEmpty()) {
          habilidad.setNombre(nombre);
        }

        // Modificar la pasiva
        System.out.print("Pasiva (true/false) (dejar vacío para mantener el valor anterior): ");
        String pasivaInput = scanner.nextLine();
        if (!pasivaInput.isEmpty()) {
          boolean pasiva = Boolean.parseBoolean(pasivaInput);
          habilidad.setPasiva(pasiva);
        }

        // Modificar la asignación de tecla
        System.out.print("Asignación de tecla (dejar vacío para mantener el valor anterior): ");
        String asignacionDeTeclaInput = scanner.nextLine();
        if (!asignacionDeTeclaInput.isEmpty()) {
          char asignacionDeTecla = asignacionDeTeclaInput.charAt(0);
          habilidad.setAsignacion_de_tecla(asignacionDeTecla);
        }

        // Modificar la descripción
        System.out.print("Descripción (dejar vacío para mantener el valor anterior): ");
        String descripcion = scanner.nextLine();
        if (!descripcion.isEmpty()) {
          habilidad.setDescripcion(descripcion);
        }

        // Modificar el link
        System.out.print("Link (dejar vacío para mantener el valor anterior): ");
        String link = scanner.nextLine();
        if (!link.isEmpty()) {
          habilidad.setLink(link);
        }

        em.merge(habilidad);
        transaction.commit();
        System.out.println("Registro modificado correctamente.");
      } else {
        System.out.println("No se encontró ninguna habilidad con la ID especificada.");
      }
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      System.out.println("Error al modificar la habilidad: " + e.getMessage());
    } finally {
      em.close();
    }
  }

  /**
   * Elimina una habilidad de la base de datos por su ID.
   *
   * @param id El ID de la habilidad a eliminar.
   */
  public void eliminarHabilidadPorId(String id) {
    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();
    try {
      transaction.begin();
      Habilidad habilidad = em.find(Habilidad.class, id);
      if (habilidad != null) {
        em.remove(habilidad);
        transaction.commit();
        System.out.println("Registro eliminado correctamente.");
      } else {
        System.out.println("No se encontró ninguna habilidad con la ID especificada.");
      }
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      System.out.println("Error al eliminar la habilidad: " + e.getMessage());
    } finally {
      em.close();
    }
  }

  /**
   * Elimina habilidades de la base de datos que están asignadas a una tecla específica.
   *
   * @param tecla La tecla a la que están asignadas las habilidades a eliminar.
   */
  public void eliminarHabilidadPorTecla(char tecla) {
    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();
    try {
      transaction.begin();
      String queryStr = "SELECT h FROM Habilidad h WHERE h.asignacion_de_tecla = :tecla";
      Query query = em.createQuery(queryStr);
      query.setParameter("tecla", tecla);
      List<Habilidad> habilidades = query.getResultList();

      if (!habilidades.isEmpty()) {
        for (Habilidad habilidad : habilidades) {
          em.remove(habilidad);
        }
        transaction.commit();
        System.out.println("Registros eliminados correctamente.");
      } else {
        System.out.println("No se encontraron habilidades con la tecla especificada.");
      }
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      System.out.println("Error al eliminar las habilidades: " + e.getMessage());
    } finally {
      em.close();
    }
  }
}
