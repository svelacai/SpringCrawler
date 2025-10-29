CREATE TABLE IF NOT EXISTS producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(255) NOT NULL,
    nombre VARCHAR(500) NOT NULL,
    precio_actual DECIMAL(10,2),
    precio_anterior DECIMAL(10,2),
    disponibilidad VARCHAR(255) NOT NULL,
    url_origen TEXT,
    nombre_sitio VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_sku (sku)
);

CREATE TABLE IF NOT EXISTS imagenes_producto (
    producto_id BIGINT NOT NULL,
    url_imagen TEXT NOT NULL,
    orden INT NOT NULL,
    PRIMARY KEY (producto_id, orden),
    FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(500) NOT NULL,
    ruta_categoria VARCHAR(1000),
    total_paginas INT,
    productos_por_pagina INT,
    url_origen TEXT,
    nombre_sitio VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);