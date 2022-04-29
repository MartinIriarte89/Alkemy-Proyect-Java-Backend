# **Alkemy-Proyect-Java-Backend**
### Challenge Backend de Alkemy.

Este proyecto es un challenge brindado por Alkemy para acelerar el seniority de los juniors.

El objetivo es construir una api con **Java** y **Spring**, que maneje un crud de los personajes y películas de Disney. Además decidí agregarle un registro y login
de usuarios con perfiles de Admin y User. Los Admin son los únicos que pueden acceder a la edicion, creacion y eliminacion de los distintos personajes y peliculas, y los
usuarios solo tienen permisos de lectura. 

La api está documentada accediendo a la dirección de : **/swagger-ui.html** la cual es pública!!

Por defecto la api está funcionando con H2 con unos datos precargados de películas, personajes, géneros y usuarios para poder descargar el proyecto y con tan solo correrlo
esté funcionando.

Se solicitó también que la api pudiera listar las películas y los personajes con distinto tipos de filtrados como por ejemplo: películas filtradas por id del género, pero 
opte por cambiar esto, y no filtrar por los id sino por los nombres de los mismos. Creí que resultaba un poco más intuitivo.

También se implementó el envío de un mail de bienvenida a la casilla de correo indicada en el registro de un usuario, pero hace falta que coloque su propia key de 
sendgrid en el properties del proyecto!!



Los usuarios para poder realizar pruebas son los dos siguientes:

**ADMINISTRADOR** 

usuario: juanbatista y contraseña: 123456

**USUARIO**

usuario:  mariaalmada y contraseña: 123456


