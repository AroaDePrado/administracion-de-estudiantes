# Administración de Estudiantes

### Descripción general de la aplicación

La aplicación de administración de estudiantes es una herramienta que permite gestionar un registro de estudiantes mediante una interfaz gráfica en Java. Utiliza una base de datos MySQL y proporciona operaciones CRUD (Crear, Leer, Actualizar y Eliminar) sobre los registros de los estudiantes. La interfaz permite visualizar, añadir, modificar y eliminar estudiantes de la base de datos.

### Estructura del proyecto

El proyecto consta de la clase `AdministracionEstudiantes`. Esta clase extiende `JFrame` y maneja la interfaz gráfica de usuario. Contiene la lógica para interactuar con la base de datos y realizar operaciones CRUD.


### Instrucciones de instalación y configuración

Pasos para instalar y configurar el proyecto:

1. **Configuración de la base de datos:**
   - Instala MySQL si aún no lo tienes.
   - Crea una base de datos llamada `registro_estudiantes`.
   - Ejecuta el script SQL proporcionado para crear la tabla `estudiantes` con los campos `id`, `nombre`, `apellido`, `edad` y `curso`.

2. **Configuración del proyecto:**
   - Descarga e importa las librerías JDBC de MySQL al proyecto.
   - Modifica las variables `usuario` y `contraseña` si fuera necesario.
   - Asegúrate de tener configurado el entorno de desarrollo para Java y MySQL.

### Establecimiento de conexión JDBC y operaciones CRUD

1. **Conexión a la base de datos:**
   - En la clase `AdministracionEstudiantes`, se establece la conexión a la base de datos usando la URL de conexión, nombre de usuario y contraseña de MySQL.

2. **Operaciones CRUD:**
   - **Crear (`guardarEstudiante()`):** Se inserta un nuevo registro en la tabla `estudiantes` utilizando sentencias SQL `INSERT`.
   - **Leer (`cargar()`):** Se recuperan los datos de la tabla `estudiantes` mediante sentencias SQL `SELECT` y se cargan en la interfaz gráfica.
   - **Actualizar (`btnModificar`):** Se actualizan los registros existentes en la tabla `estudiantes` usando sentencias SQL `UPDATE` basadas en la ID seleccionada.
   - **Eliminar (`btnEliminar`):** Se eliminan registros específicos de la tabla `estudiantes` utilizando sentencias SQL `DELETE`.



