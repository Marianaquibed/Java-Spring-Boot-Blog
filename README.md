# Java Spring Boot Blog

Aplicación web de un blog con gestión de publicaciones, comentarios, categorías, galería de imágenes, formulario de contacto y panel de administración. Está desarrollada con **Spring Boot 3**, **Spring Security 6** y **Thymeleaf**, sobre una base de datos **MySQL**.

---

## Funcionalidades

- **Blog público** con listado paginado, búsqueda por contenido y lectura de cada post.
- **Comentarios** en cada publicación (nombre, email y texto, con validación).
- **Likes** asíncronos mediante `fetch`.
- **Registro e inicio de sesión** de usuarios con contraseñas cifradas.
- **Creación de posts** (con imagen) solo para usuarios autenticados.
- **Panel de administración** (`ROLE_ADMIN`) para gestionar categorías e imágenes.
- **Galería en la página principal**, organizada por categorías en pestañas.
- **Formulario de contacto** con persistencia en base de datos.

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17, HTML5, CSS3, JavaScript |
| Backend | Spring Boot 3.2.5 (Spring Web MVC) |
| Seguridad | Spring Security 6 (sesiones, BCrypt, roles `ROLE_USER` / `ROLE_ADMIN`) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL |
| Plantillas | Thymeleaf + `thymeleaf-extras-springsecurity6` |
| Frontend | Bootstrap 3, jQuery, FontAwesome, Magnific Popup |
| Utilidades | Lombok, Spring Boot DevTools, Bean Validation |
| Build | Maven (`./mvnw`) |

---

## 📁 Estructura del proyecto

```text
Java-Spring-Boot-Blog/
├── public/
│   └── uploads/                    # Archivos subidos por los usuarios
│       ├── images/                 # Imágenes de posts y galería
│       └── portfolio/              # Copia para la galería / portafolio
├── src/
│   ├── main/
│   │   ├── java/com/blog/
│   │   │   ├── config/
│   │   │   │   └── WebConfig.java              # Expone /uploads/** desde el disco
│   │   │   ├── controller/
│   │   │   │   ├── AdminController.java        # Gestión de categorías e imágenes (solo ADMIN)
│   │   │   │   ├── LoginController.java        # Vista de login (/login)
│   │   │   │   ├── PageController.java         # Home, About, Contact, Thank you
│   │   │   │   ├── PostController.java         # Listado, búsqueda, lectura, creación, comentarios y likes
│   │   │   │   └── RegistrationController.java # Registro de usuarios
│   │   │   ├── entity/
│   │   │   │   ├── Category.java
│   │   │   │   ├── Comment.java
│   │   │   │   ├── Contact.java
│   │   │   │   ├── Image.java
│   │   │   │   ├── Post.java
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── CommentRepository.java
│   │   │   │   ├── ContactRepository.java
│   │   │   │   ├── ImageRepository.java
│   │   │   │   ├── PostRepository.java         # Búsquedas, paginación y últimos posts
│   │   │   │   └── UserRepository.java         # findByEmail(...)
│   │   │   ├── security/
│   │   │   │   ├── SecurityConfig.java         # Reglas HTTP, login, logout, beans
│   │   │   │   └── UserDetailsServiceImpl.java # Puente entre User (MySQL) y Spring Security
│   │   │   └── BlogApplication.java            # Punto de entrada
│   │   └── resources/
│   │       ├── static/                         # CSS, JS, fuentes e imágenes base
│   │       ├── templates/
│   │       │   ├── admin/                      # Panel de administración
│   │       │   ├── blog/                       # blog.html, new_post.html, single_post.html
│   │       │   ├── login/                      # index.html
│   │       │   ├── page/                       # about, contact, thankyou
│   │       │   ├── partials/                   # _post, _recent_post, _post_comments, _image...
│   │       │   ├── post/                       # Plantillas auxiliares de artículos
│   │       │   ├── registration/               # register.html
│   │       │   ├── base.html                   # Plantilla base
│   │       │   └── index.html                  # Home
│   │       └── application.properties
│   └── test/
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md
```

---

## Arquitectura por capas (MVC)

1. **Entidades (`com.blog.entity`)**: clases `@Entity` que representan las tablas de MySQL. Definen atributos, validaciones (`@NotBlank`, `@Email`) y relaciones (un `User` tiene muchos `Post`; un `Post` tiene muchos `Comment`).
2. **Repositorios (`com.blog.repository`)**: interfaces que extienden `JpaRepository`. Permiten CRUD sin SQL manual y consultas derivadas como `findByEmail(...)`.
3. **Controladores (`com.blog.controller`)**: reciben las peticiones HTTP, validan formularios, usan los repositorios, cargan datos en el `Model` y devuelven la plantilla Thymeleaf.
4. **Seguridad (`com.blog.security`)**:
   - `SecurityConfig`: define rutas públicas, rutas autenticadas (`/blog/new`) y exclusivas de administradores (`/admin/**`).
   - `UserDetailsServiceImpl`: durante el login consulta `UserRepository` para verificar el email, la contraseña BCrypt y los roles.
5. **Configuración web (`com.blog.config`)**: `WebConfig` publica `public/uploads/` como `/uploads/**` para que el navegador muestre las imágenes subidas.
6. **Vistas (`resources/templates`)**: plantillas Thymeleaf renderizadas en el servidor. `partials/` contiene fragmentos reutilizables.

---

## Base de datos y configuración

Crea la base de datos en MySQL:

```sql
CREATE DATABASE blog_java_spring_boot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Configura `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/blog_java_spring_boot?useSSL=false&serverTimezone=UTC
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD

# update conserva los datos entre reinicios. NO uses "create" salvo que quieras borrar todo.
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

```

> Con `ddl-auto=create` Hibernate borra y recrea las tablas en cada arranque (y DevTools reinicia la app al guardar archivos), por lo que perderías usuarios y posts continuamente.

---

## Cómo ejecutar el proyecto

### Requisitos

- JDK 17 o superior
- MySQL 8 en ejecución
- Maven (o el wrapper incluido `./mvnw`)

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/Marianaquibed/Java-Spring-Boot-Blog.git
cd Java-Spring-Boot-Blog

# 2. Configurar application.properties (usuario y contraseña de MySQL)

# 3. Arrancar la aplicación
./mvnw spring-boot:run
```

La aplicación queda disponible en **http://localhost:8080**.

---

## Usuarios y roles

- Los usuarios se registran desde `/register`. La contraseña se guarda cifrada con BCrypt.
- Los roles se guardan como texto (por ejemplo `USER` o `ADMIN`) y `UserDetailsServiceImpl` añade el prefijo `ROLE_` automáticamente si falta.
- Para convertir un usuario en administrador, regístralo y ejecuta en MySQL (ajusta el nombre de la tabla y la columna a los de tu entidad `User`):

```sql
UPDATE user SET roles = 'ROLE_ADMIN' WHERE email = 'tu_correo@ejemplo.com';
```

> Si insertas usuarios a mano en la base de datos, la contraseña debe estar en formato BCrypt (`$2a$...`), nunca en texto plano.

---

## 🌐 Rutas principales

| Ruta | Método | Acceso | Descripción |
|---|---|---|---|
| `/` | GET | Público | Home con galería por categorías |
| `/about`, `/contact`, `/thankyou` | GET/POST | Público | Páginas informativas y contacto |
| `/login`, `/register` | GET/POST | Público | Autenticación y registro |
| `/blog`, `/blog/{page}` | GET | Público | Listado paginado de posts |
| `/blog/buscar`, `/blog/buscar/{page}` | GET | Público | Búsqueda (`?searchTerm=...`) |
| `/single_post/{slug}` | GET/POST | Público | Leer un post y comentar |
| `/single_post/{slug}/like` | POST | Público | Sumar un like (AJAX, con CSRF) |
| `/blog/new` | GET/POST | Autenticado | Crear un nuevo post con imagen |
| `/admin/categories` | GET/POST | `ROLE_ADMIN` | Gestión de categorías |
| `/admin/images` | GET/POST | `ROLE_ADMIN` | Gestión de imágenes |

---

## Subida de archivos

Las imágenes se guardan en `public/uploads/images/` (y una copia en `public/uploads/portfolio/`), una carpeta del disco fuera del classpath. Spring Boot no las sirve por defecto, por eso `WebConfig` registra el recurso:

```java
registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:" + Paths.get("public/uploads").toAbsolutePath() + "/");
```

Las rutas son relativas al directorio desde el que se arranca la aplicación (la raíz del proyecto).