# Control de Equipos de Laboratorio - JavaFX & Hibernate ORM

Aplicacion de escritorio desarrollada en JavaFX conectada a PostgreSQL mediante Hibernate ORM (Jakarta Persistence) para la gestion de inventario de equipos de laboratorio y sus categorias.

## Tecnologias utilizadas
* Java 21
* JavaFX 21.0.2
* Hibernate ORM 6.4.4.Final
* PostgreSQL 18 (Driver JDBC 42.7.3)
* Maven

## Estructura del proyecto
* `model`: Entidades JPA (`TipoEquipo` y `Equipo`) con relacion `@OneToMany` y `@ManyToOne`.
* `persistence`: Gestion de `EntityManagerFactory` (`JpaUtil`) y operaciones CRUD transaccionales con consultas JPQL (`EquipoDAO`).
* `controller`: Controlador de interfaz grafica (`EquipoController`) con validaciones de entrada y alertas.
* `resources`: Vista `hello-view.fxml` y configuracion `META-INF/persistence.xml`.
* `script_bd.sql`: Script SQL reproducible con creacion de base de datos, esquema `laboratorio`, tablas, restricciones y datos iniciales.

## Pasos para ejecutar el proyecto
1. Ejecutar el contenido de `script_bd.sql` en PostgreSQL para crear la base de datos `bd_laboratorio_orm` y el esquema `laboratorio`.
2. Verificar las credenciales locales en `src/main/resources/META-INF/persistence.xml`.
3. Abrir el proyecto en IntelliJ IDEA, cargar las dependencias de Maven y ejecutar la clase `me.edu.uam.controlequiposlaboratorio.Launcher`.

## Matriz de pruebas ejecutadas (P1 - P8)

| Codigo | Caso de prueba | Datos de entrada | Resultado obtenido | Estado |
| :--- | :--- | :--- | :--- | :--- |
| P1 | Registrar un equipo valido | Codigo: EQ-009, Nombre: Monitor UltraSharp 24, Marca: Dell, Cantidad: 6, Tipo: Computo, Estado: Disponible | Se persiste en PostgreSQL, muestra alerta de exito y actualiza el TableView | Aprobado |
| P2 | Intentar codigo duplicado | Codigo: EQ-001 | El sistema valida la unicidad y muestra alerta rechazando el registro duplicado | Aprobado |
| P3 | Editar nombre, estado o tipo | Seleccion de EQ-009, cambio de Estado a En mantenimiento | Se ejecuta `merge()` en transaccion y se actualiza la fila en el TableView | Aprobado |
| P4 | Eliminar un equipo con confirmacion | Seleccion de EQ-009 y confirmacion en cuadro de dialogo | Se muestra `Alert` de confirmacion, se ejecuta `remove()` y se actualiza la tabla | Aprobado |
| P5 | Buscar por nombre parcial | Texto: Proyector | Consulta JPQL con `LIKE` retorna coincidencias parciales sin distinguir mayusculas | Aprobado |
| P6 | Filtrar por tipo y combinar con busqueda | Texto: Dell, Tipo: Computo | Consulta JPQL combinada aplica ambos parametros y muestra solo los registros coincidentes | Aprobado |
| P7 | Ingresar cantidad negativa | Cantidad: -5 | Validacion previa muestra alerta de advertencia e impide guardar valores negativos | Aprobado |
| P8 | Reiniciar aplicacion | Cierre y nueva ejecucion de `Launcher` | Los cambios realizados persisten en PostgreSQL y se cargan al iniciar | Aprobado |