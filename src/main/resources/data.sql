insert into usuario (nombre_completo , nombre_usuario , email , contrasena ) values 
('Juan Batista', 'juanbatista', 'jbatista@gmail.com', '$2a$10$yMlntyPah0iF6VXNPpqJe.24bSdBMkRPtwtyTig24waNk1xZ.yKKy'),
('Maria Almada', 'mariaalmada', 'malmada@gmail.com', '$2a$10$yMlntyPah0iF6VXNPpqJe.24bSdBMkRPtwtyTig24waNk1xZ.yKKy');
insert into usuario_roles ( usuario_id, roles) values ('1', 'ROLE_ADMIN'), ('2', 'ROLE_USER');
insert into genero (nombre) values ('Musical'), ('Animado'), ('Drama'), ('Accion'), ('Aventura');
insert into personaje (nombre, edad, peso, url_imagen, historia) values 
('Mickey Mouse', '22', '10', 'https://akns-images.eonline.com/eol_images/Entire_Site/20121016/634.mm.cm.111612_copy.jpg', 'Creado el 18 de noviembre de 1928, este ratón tiene un origen disputado. La leyenda oficial explica que fue creado por Walt Disney durante un viaje en tren y que su nombre inicial fue Mortimer, pero que cambió a Mickey a petición de su esposa, Lillian.'),
('Pato Donald', '24', '15', 'https://estaticos.serpadres.es/uploads/images/gallery/5624e7276e0630f93313c01b/donald_hola.jpg', 'Cuenta la historia que Walt Disney se inspiró en crearlo cuando escuchó al actor Clarence Nash recitar una poesía infantil con voz de pato y lo contrató en un corto protagonizado por Mickey Mouse. Un tiempo después Disney pidió a su equipo de animadores que creara un personaje que se adecuara a la voz.'),
('Aladdin', '18', '65', 'https://areajugones.sport.es/wp-content/uploads/2019/06/aladdin-mena-massoud-1559014964.jpg', 'Aladdín es un joven pobre que, junto con su mono Abú, se dedica a robar y engañar a la gente de Agrabah para poder sobrevivir. Él y Abú viven en una guarida, una casa abandonada y medio derruida en el bazar de la ciudad donde tienen una amplia vista al palacio.');
insert into audiovisual (dtype, calificacion, fecha_de_estreno, titulo, url_imagen, genero_id) values
('pelicula', '3.6', '1940-12-25', 'fantasia', 'https://animacionparaadultos.es/wp-content/uploads/2020/03/fantasia-1940.jpg', '1'),
('pelicula', '3.9', '1999-11-09', 'Mickey celebra la Navidad', 'https://static.wikia.nocookie.net/doblaje/images/3/30/Navidadmickey.jpg/revision/latest?cb=20200724195947&path-prefix=es', '2'),
('pelicula', '3.0', '1977-04-15', 'Los tres caballeros', 'https://es.web.img3.acsta.net/pictures/14/04/07/12/57/250010.jpg', '2'),
('pelicula', '4.2', '2019-05-24', 'Aladdin', 'https://pics.filmaffinity.com/Aladdin-443262605-large.jpg', '5');
insert into audiovisual_personajes (audiovisual_id, personaje_id) values ('1','1'), ('2','1'),('3','2'),('4','3');