# Users API

Una REST API para gestionar usuarios con validaciones, autenticación y operaciones CRUD completas.
Git history included in the repository.
## Características

- CRUD completo de usuarios (Crear, Leer, Actualizar, Eliminar)
- Sistema de login con encriptación de contraseñas
- Validaciones de email y formato de teléfono
- Filtrado y ordenamiento dinámico de usuarios
- Documentación automática con Swagger/OpenAPI
- Manejo de direcciones multiples por usuario
- Case-insensitive search
- Unit tests completos

## Tecnologías

- Java 17
- Spring Boot 3
- Maven
- JUnit 5
- Mockito
- Swagger/OpenAPI 3
- Lombok

## Requisitos Previos

- Java 17 o superior
- Maven 3.6 o superior
- IDE recomendado: IntelliJ IDEA o Eclipse

## Como Correr el Proyecto

### Opción 1: Desde Terminal

1. Clona el repositorio:
```bash
git clone <repository-url>
cd usersapi
```

2. Compila el proyecto:
```bash
mvn clean compile
```

3. Ejecuta la aplicación:
```bash
mvn spring-boot:run
```

La aplicación iniciara en `http://localhost:8080`

### Opcion 2: Desde tu IDE

1. Importa el proyecto como proyecto Maven
2. Espera a que Maven descargue las dependencias
3. Ejecuta la clase principal `UsersapiApplication.java`
4. La aplicación iniciara en `http://localhost:8080`

## Documentación de la API

Una vez que la aplicación este corriendo, puedes acceder a:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

En Swagger encontraras:
- Documentación completa de todos los endpoints
- Ejemplos de uso para cada operación
- Esquemas de request/response
- Posibilidad de probar los endpoints directamente

## Endpoints Disponibles

### Usuarios
- `GET /users` - Obtener todos los usuarios (con filtros y ordenamiento)
- `POST /users` - Crear un nuevo usuario
- `PUT /users/{id}` - Actualizar un usuario existente
- `DELETE /users/{id}` - Eliminar un usuario
- `POST /users/login` - Autenticación de usuario

### Health Check
- `GET /` - Verificar si la API esta funcionando

## Ejemplos de Uso

### Obtener todos los usuarios
```bash
curl -X GET "http://localhost:8080/users"
```

### Crear un usuario
```bash
curl -X POST "http://localhost:8080/users" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@ejemplo.com",
    "name": "Usuario Ejemplo",
    "phone": "+1234567890",
    "password": "contraseña123",
    "taxId": "USUA123456",
    "addresses": [
      {
        "id": 1,
        "name": "casa",
        "street": "Calle Principal 123",
        "countryCode": "US"
      }
    ]
  }'
```

### Actualizar un usuario
```bash
curl -X PUT "http://localhost:8080/users/{id}" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nuevo@ejemplo.com",
    "name": "Nombre Actualizado"
  }'
```

### Login
```bash
curl -X POST "http://localhost:8080/users/login?taxId=USUA123456&password=contraseña123"
```

## Filtrado y Ordenamiento

### Ordenamiento
```bash
# Ordenar por nombre
GET /users?sortedBy=name

# Ordenar por email
GET /users?sortedBy=email

# Ordenar por fecha de creacion
GET /users?sortedBy=created_at
```

### Filtrado
El formato del filtro es: `campo+operador+valor`

Operadores disponibles:
- `co` - contains (contiene)
- `sw` - starts with (empieza con)
- `ew` - ends with (termina con)
- `eq` - equals (igual)

Ejemplos:
```bash
# Usuarios cuyo nombre contiene "juan"
GET /users?filter=name+co+juan

# Usuarios cuyo email termina con "@gmail.com"
GET /users?filter=email+ew+gmail.com

# Usuarios cuyo telefono empieza con "+123"
GET /users?filter=phone+sw+123

# Usuarios con taxId especifico
GET /users?filter=tax_id+eq=USUA123456
```

### Combinado
```bash
# Ordenar por email y filtrar por nombre
GET /users?sortedBy=email&filter=name+co+juan
```

## Validaciones

### Email
- Debe tener formato valido de email
- Ejemplo valido: `usuario@dominio.com`

### Teléfono
- Debe contener exactamente 10 dígitos
- Puede incluir código de país
- Ejemplos validos: `+1234567890`, `1234567890`
- Ejemplos inválidos: `123`, `telefono`, `+123456`

### RFC (Tax ID)
- Debe seguir formato estándar mexicano: XXXX000000XXX
- Donde XXXX son las primeras 4 letras del apellido y nombre
- 000000 es una fecha (AAMMDD)
- XXX son 3 caracteres homoclave
- Ejemplos validos: `AARR990101XXX`, `BOMM850101YYY`
- Ejemplos inválidos: `123`, `RFC`, `AARR99XXX`

### Campos Requeridos para Creación
- email (formato válido)
- name (no vacío)
- phone (formato válido)
- password (no vacío)
- taxId (formato RFC válido: XXXX000000XXX)

## Tests

El proyecto incluye unit tests completos para el servicio principal.

### Ejecutar Tests
```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report

# Ver reporte de cobertura
open target/site/jacoco/index.html
```

### Tests Incluidos
- Tests de CRUD completo
- Tests de validaciones
- Tests de filtrado y ordenamiento
- Tests de login
- Tests de casos limite y errores

## Estructura del Proyecto

```
src/
├── main/
│   └── java/
│       └── usersapi/
│           ├── controller/     # Endpoints REST
│           ├── service/        # Lógica de negocio
│           ├── repository/     # Acceso a datos
│           ├── model/          # Entidades
│           ├── dto/            # Data Transfer Objects
│           ├── validation/     # Validaciones personalizadas
│           ├── util/           # Utilidades (encriptación)
│           └── exception/      # Manejo de errores
└── test/
    └── java/
        └── usersapi/
            └── service/        # Unit tests
```

## Datos Iniciales

La API se inicializa con 3 usuarios de prueba:

- **user1@mail.com** - taxId: AARR990101XXX
- **user2@mail.com** - taxId: AARR990101XXY  
- **user3@mail.com** - taxId: AARR990101XXZ

Todos tienen la misma contraseña encriptada para facilitar pruebas.

## Consideraciones Importantes

- Las contraseñas se encriptan usando AES antes de guardarlas
- Los usuarios se almacenan en memoria (se reinician al reiniciar la aplicación)
- La búsqueda es case-insensitive para mejor experiencia de usuario
- Los campos de actualización son opcionales (solo se actualizan si se proporcionan)
- La documentación se genera automáticamente y esta siempre sincronizada

## Problemas Comunes

### Error de Validación de Teléfono
Asegúrate de usar exactamente 10 dígitos:
- Correcto: `+1234567890`
- Incorrecto: `123456789` (solo 9 dígitos)

### Error de Formato JSON
Verifica que tu JSON este bien formado:
- Sin comas extra al final
- Todas las claves y valores string entre comillas
- Sintaxis JSON valida

### Usuario No Encontrado
Verifica que el UUID sea correcto. Puedes obtener un UUID valido desde la respuesta de creación o listado de usuarios.

## Contribuciones

1. Fork del proyecto
2. Crear una rama para tu feature
3. Commit tus cambios
4. Push a la rama
5. Crear un Pull Request

## Licencia

Este proyecto es para fines educativos y de demostración.
