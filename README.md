# Modern SpringBoot API

> Proyecto de API REST modular y escalable desarrollado con **Java moderno** y **Spring Boot**, pensado como portafolio para demostrar buenas prácticas en backend profesional.

---

## 🔹Tecnologías y herramientas

- **Lenguaje:** Java 21  
- **Framework:** Spring Boot 3.x  
- **Persistencia:** Spring Data JPA + Hibernate  
- **Base de datos:** PostgreSQL / MySQL (con Docker)  
- **Seguridad:** Spring Security, roles y contraseñas cifradas con BCrypt  
- **Testing:** JUnit 5, Mockito, tests unitarios e integración  
- **Documentación:** SpringDoc / Swagger UI (eliminada por razones de vulnerabilidad)  
- **Observabilidad:** Spring Boot Actuator  
- **Gestión de configuración:** `application.yml` con perfiles `dev` / `prod`  

---

## 🔹Arquitectura

La API sigue una **arquitectura en capas**:

Cliente (Front / Postman / otra API)
↓
Controlador (Controller)
↓
Servicio (Service)
↓
Repositorio (Repository)
↓
Base de datos (PostgreSQL / MySQL)


**Extras / Cross-cutting concerns:**
- Validaciones con Bean Validation (`@Valid`)  
- Manejo global de excepciones  
- Configuración por perfiles (dev / prod)  
- Observabilidad: métricas, logs, health checks  
- Seguridad: roles y hashing de contraseñas  


---

## 🔹Funcionalidades implementadas
 
- Validación de datos de entrada  
- Testing unitario y de integración entre capas  
- Configuración modular y escalable  
- Observabilidad con Actuator y logs básicos  

---

## 🔹Próximos pasos / roadmap

- Implementar **JWT** para autenticación y autorización avanzada  
- Migrar a **arquitectura de microservicios**  
- Mejorar **despliegue en cloud y escalabilidad**  
- Logging avanzado, métricas enriquecidas y tracing distribuido  

---

## 🔹Cómo ejecutar el proyecto

1. Clonar el repositorio:  

git clone https://github.com/CSamuelRod/modern-springboot-api.git


2.Configurar base de datos (PostgreSQL o MySQL) y perfiles dev / prod en application.yml

3.Ejecutar con Maven:

- Perfil dev
mvnw spring-boot:run -Dspring-boot.run.profiles=dev
- Perfil prod
mvnw spring-boot:run -Dspring-boot.run.profiles=prod
## 🔹Contribuciones

Este proyecto es personal, pero cualquier feedback o sugerencia es bienvenida para mejorar las buenas prácticas en backend con Spring Boot.