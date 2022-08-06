- Página web para un chat. RS Chat.
- Base de datos relacional con Apache Cayenne:
    - Tablas:
        - Usuario (rol -> alumno, profesor, admin, ...).
        - Sesiones.
        - Asignaturas.
        - Cursos.
        - Grupos.

- Administración:
    - Añadir asignatura.
    - Añadir curso.
    - Añadir grupo.

- Profesores:
    - Añadir usuarios a grupos.
    - Añadir usuarios a cursos.
    - Añadir usuarios a asignaturas.

- Comunicación individual entre usuarios:
    - Mensajes.
    - Archivos.
    - Imágenes.
    - Videos.
    - Audio.

- Tema claro/oscuro.

- Profesores tienen una bandera para indicar su rol (o colores).
- Guardar los mensajes de texto en ficheros, dependiendo de la asignatura:
- Si son audios, vídeos o imágenes, guardar referencia a la url.
    - Mensajes de una asignatura.
    - Mensajes de un curso.
    - Mensajes de un grupo.
    - Servicio online para guardar ficheros (S3).
    - Poner estructura de carpetas según asignatura, curso, grupo.
