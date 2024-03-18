import java.io.IOException;
import java.util.Scanner;

import controller.CampeonController;
import controller.HabilidadController;
import controller.RegionController;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import view.Menu;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Esta clase representa el punto de entrada principal del programa.
 * Contiene el método principal {@link #main(String[])} que permite interactuar
 * con el usuario mediante un menú de opciones para realizar diversas operaciones
 * relacionadas con las regiones, campeones y habilidades.
 * Además, proporciona métodos para crear la sesión de la fábrica de entidades,
 * lo que permite la interacción con la base de datos mediante Hibernate.
 */
public class Main {

    /**
     * Fábrica de sesiones para la interacción con la base de datos mediante Hibernate.
     */
    static SessionFactory sessionFactoryObj;

    /**
     * Construye y devuelve una fábrica de sesiones para la interacción con la base de datos.
     * Esta fábrica se configura utilizando el archivo de configuración "hibernate.cfg.xml".
     *
     * @return la fábrica de sesiones configurada
     * @throws HibernateException si ocurre un error al crear la fábrica de sesiones
     */
    private static SessionFactory buildSessionFactory() {
        try {
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml").build();
            Metadata metadata = new MetadataSources(standardRegistry).getMetadataBuilder().build();
            return metadata.getSessionFactoryBuilder().build();
        } catch (HibernateException he) {
            System.out.println("Session Factory creation failure");
            throw he;
        }
    }

    /**
     * Crea y devuelve la fábrica de entidades para la interacción con la base de datos
     * utilizando JPA (Java Persistence API).
     *
     * @return la fábrica de entidades creada
     */
    public static EntityManagerFactory createEntityManagerFactory() {
        EntityManagerFactory emf;
        try {
            emf = Persistence.createEntityManagerFactory("JPAMagazines");
        } catch (Throwable ex) {
            System.err.println("Failed to create EntityManagerFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        return emf;
    }

    /**
     * Método principal que inicia el programa.
     * Permite interactuar con el usuario mediante un menú de opciones para realizar
     * diversas operaciones relacionadas con las regiones, campeones y habilidades.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     * @throws IOException si ocurre un error de entrada/salida al leer la entrada del usuario
     */
    public static void main(String[] args) throws IOException {
        EntityManagerFactory entityManagerFactory = createEntityManagerFactory();
        RegionController regionController = new RegionController(entityManagerFactory);
        CampeonController campeonController = new CampeonController(entityManagerFactory);
        HabilidadController habilidadController = new HabilidadController(entityManagerFactory, campeonController);
        int opcio;
        Scanner input = new Scanner(System.in);
        do {
            Menu menu = new Menu();

            opcio = menu.mainMenu();
            switch (opcio) {

                case 1:
                    System.out.println("Listando Regiones...");
                    System.out.println(regionController.listarRegiones());
                    break;
                case 2:
                    System.out.println("Listando Campeones...");
                    System.out.println(campeonController.listarCampeones());
                    break;
                case 3:
                    System.out.println("Listando Habilidades...");
                    System.out.println(habilidadController.listarHabilidades());
                    break;
                case 4:
                    System.out.println("Borrando y creando todas las tablas...");
                    habilidadController.eliminarTablaHabilidad();
                    regionController.eliminarTablaRegion();
                    campeonController.eliminarTablaCampeon();
                    campeonController.crearTablaCampeon();
                    regionController.crearTablaRegion();
                    habilidadController.crearTablaHabilidad();
                    break;
                case 5:
                    System.out.println("Introduce el texto a buscar en las regiones:");
                    System.out.println(regionController.buscarRegionPorTexto(input.next()));
                    break;
                case 6:
                    System.out.println("Introduce el número de historias para buscar regiones:");
                    input.next();
                    System.out.println(regionController.buscarRegionesPorHistorias(input.nextInt()));
                    break;
                case 7:
                    System.out.println("Introduce el ID de la región a obtener:");
                    input.next();
                    System.out.println(regionController.obtenerRegionPorId(input.nextInt()));
                    break;
                case 8:
                    System.out.println("Introduce el texto a buscar en los campeones:");
                    System.out.println(campeonController.buscarCampeonPorTexto(input.next()));
                    break;
                case 9:
                    System.out.println("Introduce el rol para buscar campeones:");
                    System.out.println(campeonController.buscarCampeonesPorRol(input.next()));
                    break;
                case 10:
                    System.out.println("Introduce el ID del campeón a obtener:");
                    input.next();
                    System.out.println(campeonController.obtenerCampeonPorId(input.nextInt()));
                    break;
                case 11:
                    System.out.println("Introduce el texto a buscar en las habilidades:");
                    System.out.println(habilidadController.buscarHabilidadPorTexto(input.next()));
                    break;
                case 12:
                    System.out.println("Introduce el ID del campeón para buscar habilidades:");
                    input.next();
                    System.out.println(habilidadController.buscarHabilidadPorIdCampeon(input.nextInt()));
                    break;
                case 13:
                    System.out.println("Introduce el nombre de la habilidad a obtener:");
                    System.out.println(habilidadController.obtenerHabilidadPorNombre(input.next()));
                    break;
                case 14:
                    System.out.println("Introduce el ID de la región a modificar:");
                    input.next();
                    regionController.modificarRegionPorId(input.nextInt());
                    break;
                case 15:
                    System.out.println("Introduce el ID del campeón a modificar:");
                    input.next();
                    campeonController.modificarCampeonPorId(input.nextInt());
                    break;
                case 16:
                    System.out.println("Introduce el ID de la habilidad a modificar:");
                    habilidadController.modificarHabilidadPorId(input.nextLine());
                    break;
                case 17:
                    System.out.println("Introduce el ID de la región a eliminar:");
                    input.next();
                    regionController.eliminarRegionPorId(input.nextInt());
                    break;
                case 18:
                    System.out.println("Introduce el ID del campeón a eliminar:");
                    input.next();
                    campeonController.eliminarCampeonPorId(input.nextInt());
                    break;
                case 19:
                    System.out.println("Introduce el ID de la habilidad a eliminar:");
                    habilidadController.eliminarHabilidadPorId(input.next());
                    break;
                case 20:
                    System.out.println("Introduce la tecla asignada para eliminar habilidades:");
                    habilidadController.eliminarHabilidadPorTecla(input.next().charAt(0));
                    break;
                case 21:
                    System.out.println("Borrando campeones por aparición en cinemáticas...");
                    campeonController.eliminarCampeonPorCinematicas();
                    break;
                case 22:
                    System.out.println("Cargando datos desde archivos CSV...");
                    campeonController.readCampeonFromFile("src/main/resources/Campeons.csv");;
                    regionController.readRegionFromFile("src/main/resources/Regiones.csv");
                    habilidadController.readHabilidadFromFile("src/main/resources/Habilidades.csv");
                    break;
                case 23:
                    break;
                default:
                    System.out.println("Adeu!!");
                    System.exit(1);

            }
        } while (opcio != 0);
    }
}