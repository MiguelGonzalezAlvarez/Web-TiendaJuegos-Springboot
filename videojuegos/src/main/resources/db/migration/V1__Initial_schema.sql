-- V1__Initial_schema.sql
-- Tabla usuarios
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    telefono VARCHAR(50),
    direccion VARCHAR(255),
    ciudad VARCHAR(100),
    codigo_postal VARCHAR(20),
    pais VARCHAR(100),
    imagen_url VARCHAR(500),
    fecha_registro TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    verification_token VARCHAR(255),
    reset_password_token VARCHAR(255),
    reset_password_expires TIMESTAMP
);

-- Tabla videojuegos
CREATE TABLE videojuegos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE,
    descripcion TEXT,
    descripcion_corta VARCHAR(500),
    precio DECIMAL(10,2) NOT NULL,
    precio_oferta DECIMAL(10,2),
    imagen_url VARCHAR(500),
    fecha_lanzamiento DATE,
    plataforma VARCHAR(100),
    genero VARCHAR(100),
    desarrollador VARCHAR(200),
    distribuidor VARCHAR(200),
    requisitos_minimos TEXT,
    requisitos_recomendados TEXT,
    stock INTEGER DEFAULT 0,
    rating DOUBLE DEFAULT 0,
    valoraciones_count INTEGER DEFAULT 0,
    destacado BOOLEAN DEFAULT false,
    activo BOOLEAN DEFAULT true,
    es_oferta BOOLEAN DEFAULT false,
    rawg_id BIGINT UNIQUE,
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP
);

-- Tabla pedidos
CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    estado VARCHAR(20) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    coste_envio DECIMAL(10,2),
    descuento DECIMAL(10,2),
    total DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50),
    direccion_envio VARCHAR(500),
    ciudad_envio VARCHAR(100),
    codigo_postal_envio VARCHAR(20),
    pais_envio VARCHAR(100),
    stripe_payment_intent_id VARCHAR(255),
    stripe_customer_id VARCHAR(255),
    numero_seguimiento VARCHAR(100),
    notas VARCHAR(1000),
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    fecha_envio TIMESTAMP,
    fecha_entrega TIMESTAMP
);

-- Tabla detalle_pedido
CREATE TABLE detalle_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL REFERENCES pedidos(id),
    video_juego_id BIGINT NOT NULL REFERENCES videojuegos(id),
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

-- Tabla carrito_items
CREATE TABLE carrito_items (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    video_juego_id BIGINT NOT NULL REFERENCES videojuegos(id),
    cantidad INTEGER DEFAULT 1,
    fecha_agregado TIMESTAMP,
    fecha_actualizacion TIMESTAMP
);

-- Tabla wishlist_items
CREATE TABLE wishlist_items (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    video_juego_id BIGINT NOT NULL REFERENCES videojuegos(id),
    fecha_agregado TIMESTAMP
);

-- Tabla opiniones
CREATE TABLE opiniones (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    video_juego_id BIGINT NOT NULL REFERENCES videojuegos(id),
    calificacion INTEGER NOT NULL,
    titulo VARCHAR(200),
    contenido TEXT,
    fecha_creacion TIMESTAMP
);

-- Tabla cupones
CREATE TABLE cupones (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255),
    tipo_descuento VARCHAR(20) NOT NULL,
    valor_descuento DECIMAL(10,2),
    porcentaje_descuento DECIMAL(5,2),
    descuento_maximo DECIMAL(10,2),
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    usos_maximos INTEGER,
    usos_actuales INTEGER DEFAULT 0,
    activo BOOLEAN DEFAULT true,
    es_global BOOLEAN DEFAULT false
);

-- Tabla juego_imagenes
CREATE TABLE juego_imagenes (
    id BIGSERIAL PRIMARY KEY,
    video_juego_id BIGINT NOT NULL REFERENCES videojuegos(id),
    imagen_url VARCHAR(500)
);

-- Índices
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_videojuegos_slug ON videojuegos(slug);
CREATE INDEX idx_videojuegos_genero ON videojuegos(genero);
CREATE INDEX idx_videojuegos_plataforma ON videojuegos(plataforma);
CREATE INDEX idx_pedidos_usuario_id ON pedidos(usuario_id);
CREATE INDEX idx_pedidos_estado ON pedidos(estado);
CREATE INDEX idx_detalle_pedido_pedido_id ON detalle_pedido(pedido_id);
CREATE INDEX idx_carrito_items_usuario_id ON carrito_items(usuario_id);
CREATE INDEX idx_wishlist_items_usuario_id ON wishlist_items(usuario_id);
CREATE INDEX idx_opiniones_video_juego_id ON opiniones(video_juego_id);
CREATE INDEX idx_cupones_codigo ON cupones(codigo);
CREATE INDEX idx_juego_imagenes_video_juego_id ON juego_imagenes(video_juego_id);
