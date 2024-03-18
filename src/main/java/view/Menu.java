package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Esta clase representa el menú principal del programa.
 * Permite al usuario seleccionar diversas opciones para interactuar con el sistema.
 */
public class Menu {
    private int option;

    /**
     * Constructor de la clase Menu.
     */
    public Menu() {
        super();
    }
    Scanner scanner = new Scanner(System.in);

    /**
     * Muestra el menú principal y permite al usuario seleccionar una opción.
     *
     * @return la opción seleccionada por el usuario
     */
    public int mainMenu() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        do {
            System.out.println("\nMENU PRINCIPAL\n");
            System.out.println("1. Listar Regiones");
            System.out.println("2. Listar Campeones");
            System.out.println("3. Listar Habilidades");
            System.out.println("4. Borrar y crear todas las tablas");
            System.out.println("5. Buscar Regiones por texto");
            System.out.println("6. Buscar Regiones por número de historias");
            System.out.println("7. Buscar Región por ID");
            System.out.println("8. Buscar Campeones por texto");
            System.out.println("9. Buscar Campeones por rol");
            System.out.println("10. Buscar Campeón por ID");
            System.out.println("11. Buscar Habilidades por texto");
            System.out.println("12. Buscar Habilidades por ID de Campeón");
            System.out.println("13. Buscar Habilidad por nombre");
            System.out.println("14. Modificar Región por ID");
            System.out.println("15. Modificar Campeón por ID");
            System.out.println("16. Modificar Habilidad por ID");
            System.out.println("17. Eliminar Región por ID");
            System.out.println("18. Eliminar Campeón por ID");
            System.out.println("19. Eliminar Habilidad por ID");
            System.out.println("20. Eliminar Habilidad por tecla asignada");
            System.out.println("21. Eliminar Campeones por aparición en cinemáticas");
            System.out.println("22. Cargar datos desde archivos CSV");
            System.out.println("0. Salir");

            System.out.println("Elige opción: ");
            try {
                option = Integer.parseInt(br.readLine());
            } catch (NumberFormatException | IOException e) {
                System.out.println("Valor no válido");
                e.printStackTrace();
            }
        } while (option < 0 || option > 22);

        return option;
    }
}