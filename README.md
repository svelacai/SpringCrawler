# SpringCrawler - E-commerce Web Crawler

## Descripción
Aplicación desarrollada en Java con Spring Boot que extrae información de productos desde sitios de e-commerce y almacena los datos en una base de datos MySQL.

## Funcionalidades

### 1. Extracción de Productos Individuales
- **Endpoint**: `POST /api/crawler/product?url={URL}`
- **Datos extraídos**:
  - SKU: Código identificador del producto
  - Nombre: Nombre completo del producto
  - Precios: Precio actual (y precio anterior si existe)
  - Imágenes: URLs de las imágenes del producto
  - Disponibilidad: Estado de disponibilidad para compra

### 2. Extracción de Categorías de Productos
- **Endpoint**: `POST /api/crawler/category?url={URL}`
- **Datos extraídos**:
  - Categoría: Nombre o ruta de la categoría
  - Cantidad de páginas: Número de páginas totales
  - Cantidad de productos por página
  - Listado completo de productos en la categoría

### 3. Documentación API
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Sitios Soportados

### 1. MercadoLibre (Productos individuales)
- **URL de prueba**: `https://www.mercadolibre.com.ar/sierra-circular-7-14-185-190mm-1600w-hs7010-makita/p/MLA19813486`
- Extrae SKU, nombre, precio, imágenes y disponibilidad

### 2. Paris.cl (Categorías y productos)
- **URL de prueba**: `https://www.paris.cl/tecnologia/celulares/smartphone/`
- Implementa paginación automática para recorrer todos los productos

### 3. Amazon (Sitio adicional)
- Soporte para Amazon.com, Amazon.es, Amazon.co.uk
- Extracción de productos individuales y categorías

## Arquitectura

### Principios SOLID Aplicados
- **Single Responsibility**: Cada clase tiene una responsabilidad específica
- **Open/Closed**: Extensible para nuevos sitios mediante Strategy Pattern
- **Liskov Substitution**: Las implementaciones de CrawlerStrategy son intercambiables
- **Interface Segregation**: Interfaces específicas para cada funcionalidad
- **Dependency Inversion**: Dependencias inyectadas mediante Spring

### Patrones de Diseño
- **Strategy Pattern**: Para diferentes implementaciones de crawlers por sitio
- **Repository Pattern**: Para acceso a datos
- **DTO Pattern**: Para transferencia de datos entre capas





### 1. Configuración de Base de Datos

**Base de Datos en la Nube (Aiven)**

La aplicación utiliza una base de datos MySQL alojada en Aiven Cloud.

```properties
# MySQL JPA Configuration
spring.datasource.url=jdbc:mysql://mysql-franquicia-santiago-4acf.b.aivencloud.com:27518/spring_crawler
spring.datasource.username=avnadmin
spring.datasource.password=AVNS_trd2DWx_sOlG0xbcMDi
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**Acceso desde IDE/Cliente Externo**

Para conectarse a la base de datos desde un IDE (DBeaver, etc.) es **obligatorio instalar el certificado SSL** de Aiven

**Nota:** Si requiere asistencia con la configuración de la base de datos, puede comunicarse conmigo.

### 3. Ejecutar la aplicación
```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## Uso de la API

### Crawlear un producto individual
```bash
curl -X POST "http://localhost:8080/api/crawler/product?url=https://www.mercadolibre.com.ar/sierra-circular-7-14-185-190mm-1600w-hs7010-makita/p/MLA19813486"
```

### Crawlear una categoría completa
```bash
curl -X POST "http://localhost:8080/api/crawler/category?url=https://www.paris.cl/tecnologia/celulares/smartphone/"
```

### Verificar estado del servicio
```bash
curl -X GET "http://localhost:8080/api/crawler/health"
```


## Características Técnicas

### Manejo de Errores
- Logging detallado de errores
- Respuestas HTTP apropiadas
- Manejo de excepciones por capas

### Persistencia
- Actualización automática de productos existentes
- Historial de precios (precio anterior)
- Timestamps automáticos

### Escalabilidad
- Patrón Strategy permite agregar nuevos sitios fácilmente
- Arquitectura modular y desacoplada
- Configuración externalizada

## Testing

### Ejecutar Tests
```bash
mvn test
```

### Cobertura
- Tests unitarios para CrawlerService
- Tests de integración para crawlers
- Mocks para dependencias externas

## Documentación API

### Swagger UI
Accede a la documentación interactiva en:
- **URL**: `http://localhost:8080/swagger-ui.html`
- **Descripción**: Interfaz para probar endpoints
- **Esquemas**: Documentación de DTOs y respuestas


## Autor
Santiago Vela