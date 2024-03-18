# Proyecto de Gestión de Campeones y Regiones

Este proyecto consiste en una aplicación de gestión de campeones y regiones para un videojuego. Permite realizar diversas operaciones como agregar, eliminar, modificar y buscar campeones y regiones en una base de datos.

## Características

- **Gestión de Campeones:** Permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre campeones, incluyendo la asignación de habilidades y regiones.
- **Gestión de Regiones:** Permite realizar operaciones CRUD sobre regiones, incluyendo la asociación de campeones y el manejo de historias relacionadas.
- **Importación de Datos:** Permite importar datos de campeones y regiones desde archivos CSV para cargar rápidamente información en la base de datos.
- **Interfaz de Consola:** La aplicación cuenta con una interfaz de consola simple y fácil de usar para interactuar con las funcionalidades.

## Tecnologías Utilizadas

- **Java:** El lenguaje de programación principal utilizado en el proyecto.
- **JPA (Java Persistence API):** Utilizado para el mapeo objeto-relacional y el acceso a la base de datos.
- **Hibernate:** Implementación de JPA utilizada para la gestión de entidades y transacciones.
- **MySQL:** Sistema de gestión de bases de datos relacional utilizado para almacenar los datos del juego.

## Requisitos de Instalación

1. **Java Development Kit (JDK):** Se requiere JDK 8 o superior para compilar y ejecutar el proyecto.
2. **MySQL Server:** Se necesita un servidor MySQL instalado y configurado para almacenar los datos del juego.
3. **IDE Java:** Se recomienda utilizar un IDE Java como Eclipse, IntelliJ IDEA o NetBeans para abrir y trabajar en el proyecto

## Agradecimientos

Este trabajo ha sido posible gracias a la ayuda del profe de M06 Rubén Arroyo, y no como otros que no saben lo que están dando como el Dani, él ha sido de gran ayuda para este proyecto

## Configuración

1. **Base de Datos:** Cree una base de datos en MySQL para el proyecto y actualice la configuración de conexión en el archivo `persistence.xml` con los detalles de su base de datos.

```xml
<persistence-unit name="NombreUnidadPersistencia">
    <properties>
        <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/nombre_basedatos"/>
        <property name="javax.persistence.jdbc.user" value="usuario"/>
        <property name="javax.persistence.jdbc.password" value="contraseña"/>
        <property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver"/>
    </properties>
</persistence-unit>

