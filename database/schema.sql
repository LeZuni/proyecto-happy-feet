-- =========================================================
-- SISTEMA DE GESTIÓN INTEGRAL PARA VETERINARIA "HAPPY FEET"
-- Schema DDL - Base de Datos MySQL
-- =========================================================
-- Este script crea la estructura completa de la base de datos.
-- Los datos de prueba estan en data.sql (ejecutar despues de este).
-- =========================================================

DROP DATABASE IF EXISTS happy_feet_veterinaria;
CREATE DATABASE happy_feet_veterinaria CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE happy_feet_veterinaria;

-- =========== TABLAS DE CONSULTA (LOOKUP TABLES) ===========

CREATE TABLE especies (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) UNIQUE NOT NULL,
  descripcion TEXT,
  INDEX idx_nombre (nombre)
);

CREATE TABLE razas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  especie_id INT NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  caracteristicas TEXT,
  FOREIGN KEY (especie_id) REFERENCES especies(id) ON DELETE RESTRICT,
  UNIQUE KEY uk_raza_especie (especie_id, nombre),
  INDEX idx_especie (especie_id)
);

CREATE TABLE producto_tipos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) UNIQUE NOT NULL,
  descripcion TEXT,
  INDEX idx_nombre (nombre)
);

CREATE TABLE evento_tipos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) UNIQUE NOT NULL,
  descripcion TEXT,
  INDEX idx_nombre (nombre)
);

CREATE TABLE cita_estados (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) UNIQUE NOT NULL,
  descripcion TEXT,
  INDEX idx_nombre (nombre)
);


-- =========== MÓDULO 1: GESTIÓN DE PACIENTES ===========

CREATE TABLE duenos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre_completo VARCHAR(255) NOT NULL,
  documento_identidad VARCHAR(20) UNIQUE NOT NULL,
  direccion VARCHAR(255),
  telefono VARCHAR(20),
  email VARCHAR(100) UNIQUE NOT NULL,
  contacto_emergencia VARCHAR(255),
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
  activo BOOLEAN DEFAULT TRUE,
  INDEX idx_documento (documento_identidad),
  INDEX idx_email (email),
  INDEX idx_activo (activo)
);

CREATE TABLE mascotas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  dueno_id INT NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  raza_id INT NOT NULL,
  fecha_nacimiento DATE,
  sexo ENUM('Macho', 'Hembra') NOT NULL,
  peso_actual DECIMAL(5,2),
  microchip VARCHAR(50) UNIQUE,
  tatuaje VARCHAR(50),
  url_foto VARCHAR(255),
  alergias TEXT,
  condiciones_preexistentes TEXT,
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
  activo BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (dueno_id) REFERENCES duenos(id) ON DELETE RESTRICT,
  FOREIGN KEY (raza_id) REFERENCES razas(id) ON DELETE RESTRICT,
  INDEX idx_dueno (dueno_id),
  INDEX idx_nombre (nombre),
  INDEX idx_microchip (microchip),
  INDEX idx_activo (activo)
);


-- =========== MÓDULO 2: SERVICIOS MÉDICOS Y PERSONAL ===========

CREATE TABLE veterinarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre_completo VARCHAR(255) NOT NULL,
  documento_identidad VARCHAR(20) UNIQUE NOT NULL,
  licencia_profesional VARCHAR(50) UNIQUE NOT NULL,
  especialidad VARCHAR(100),
  telefono VARCHAR(20),
  email VARCHAR(100) UNIQUE,
  fecha_contratacion DATE,
  activo BOOLEAN DEFAULT TRUE,
  INDEX idx_documento (documento_identidad),
  INDEX idx_licencia (licencia_profesional),
  INDEX idx_activo (activo)
);

CREATE TABLE citas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  mascota_id INT NOT NULL,
  veterinario_id INT,
  fecha_hora DATETIME NOT NULL,
  motivo VARCHAR(500),
  estado_id INT NOT NULL,
  observaciones TEXT,
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
  FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id) ON DELETE SET NULL,
  FOREIGN KEY (estado_id) REFERENCES cita_estados(id) ON DELETE RESTRICT,
  INDEX idx_fecha (fecha_hora),
  INDEX idx_mascota (mascota_id),
  INDEX idx_veterinario (veterinario_id),
  INDEX idx_estado (estado_id)
);

CREATE TABLE consultas_medicas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  mascota_id INT NOT NULL,
  veterinario_id INT NOT NULL,
  cita_id INT,
  fecha_hora DATETIME NOT NULL,
  motivo VARCHAR(500) NOT NULL,
  sintomas TEXT,
  diagnostico TEXT,
  recomendaciones TEXT,
  observaciones TEXT,
  peso_registrado DECIMAL(5,2),
  temperatura DECIMAL(4,2),
  FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
  FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id) ON DELETE RESTRICT,
  FOREIGN KEY (cita_id) REFERENCES citas(id) ON DELETE SET NULL,
  INDEX idx_mascota (mascota_id),
  INDEX idx_veterinario (veterinario_id),
  INDEX idx_fecha (fecha_hora)
);

CREATE TABLE procedimientos_especiales (
  id INT AUTO_INCREMENT PRIMARY KEY,
  mascota_id INT NOT NULL,
  veterinario_id INT NOT NULL,
  tipo_procedimiento VARCHAR(100) NOT NULL,
  nombre_procedimiento VARCHAR(255) NOT NULL,
  fecha_hora DATETIME NOT NULL,
  duracion_estimada_minutos INT,
  informacion_preoperatoria TEXT,
  detalle_procedimiento TEXT NOT NULL,
  complicaciones TEXT,
  seguimiento_postoperatorio TEXT,
  proximo_control DATE,
  estado ENUM('Programado', 'En Proceso', 'Finalizado', 'Cancelado') NOT NULL DEFAULT 'Programado',
  costo_procedimiento DECIMAL(10, 2),
  FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
  FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id) ON DELETE RESTRICT,
  INDEX idx_mascota (mascota_id),
  INDEX idx_veterinario (veterinario_id),
  INDEX idx_fecha (fecha_hora),
  INDEX idx_estado (estado)
);

CREATE TABLE historial_medico (
  id INT AUTO_INCREMENT PRIMARY KEY,
  mascota_id INT NOT NULL,
  fecha_evento DATE NOT NULL,
  evento_tipo_id INT NOT NULL,
  descripcion TEXT NOT NULL,
  diagnostico TEXT,
  tratamiento_recomendado TEXT,
  veterinario_id INT,
  consulta_id INT,
  procedimiento_id INT,
  FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
  FOREIGN KEY (evento_tipo_id) REFERENCES evento_tipos(id) ON DELETE RESTRICT,
  FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id) ON DELETE SET NULL,
  FOREIGN KEY (consulta_id) REFERENCES consultas_medicas(id) ON DELETE SET NULL,
  FOREIGN KEY (procedimiento_id) REFERENCES procedimientos_especiales(id) ON DELETE SET NULL,
  INDEX idx_mascota (mascota_id),
  INDEX idx_fecha (fecha_evento),
  INDEX idx_tipo (evento_tipo_id)
);


-- =========== MÓDULO 3: INVENTARIO Y FARMACIA ===========

CREATE TABLE proveedores (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre_empresa VARCHAR(255) NOT NULL,
  contacto VARCHAR(255),
  telefono VARCHAR(20),
  email VARCHAR(100),
  direccion VARCHAR(255),
  sitio_web VARCHAR(255),
  activo BOOLEAN DEFAULT TRUE,
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_nombre (nombre_empresa),
  INDEX idx_activo (activo)
);

CREATE TABLE inventario (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre_producto VARCHAR(255) NOT NULL,
  producto_tipo_id INT NOT NULL,
  descripcion TEXT,
  fabricante VARCHAR(100),
  proveedor_id INT,
  lote VARCHAR(50),
  cantidad_stock INT NOT NULL DEFAULT 0,
  stock_minimo INT NOT NULL DEFAULT 0,
  unidad_medida VARCHAR(50) DEFAULT 'unidad',
  fecha_vencimiento DATE,
  precio_compra DECIMAL(10, 2),
  precio_venta DECIMAL(10, 2) NOT NULL,
  requiere_receta BOOLEAN DEFAULT FALSE,
  activo BOOLEAN DEFAULT TRUE,
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (producto_tipo_id) REFERENCES producto_tipos(id) ON DELETE RESTRICT,
  FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE SET NULL,
  INDEX idx_nombre (nombre_producto),
  INDEX idx_tipo (producto_tipo_id),
  INDEX idx_stock (cantidad_stock),
  INDEX idx_vencimiento (fecha_vencimiento),
  INDEX idx_proveedor (proveedor_id),
  INDEX idx_activo (activo),
  CONSTRAINT chk_stock_positivo CHECK (cantidad_stock >= 0),
  CONSTRAINT chk_stock_minimo CHECK (stock_minimo >= 0)
);

CREATE TABLE prescripciones (
  id INT AUTO_INCREMENT PRIMARY KEY,
  consulta_id INT,
  procedimiento_id INT,
  producto_id INT NOT NULL,
  cantidad INT NOT NULL,
  dosis VARCHAR(100),
  frecuencia VARCHAR(100),
  duracion_dias INT,
  instrucciones TEXT,
  fecha_prescripcion DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (consulta_id) REFERENCES consultas_medicas(id) ON DELETE CASCADE,
  FOREIGN KEY (procedimiento_id) REFERENCES procedimientos_especiales(id) ON DELETE CASCADE,
  FOREIGN KEY (producto_id) REFERENCES inventario(id) ON DELETE RESTRICT,
  INDEX idx_consulta (consulta_id),
  INDEX idx_procedimiento (procedimiento_id),
  INDEX idx_producto (producto_id),
  INDEX idx_fecha (fecha_prescripcion),
  CONSTRAINT chk_cantidad_positiva CHECK (cantidad > 0),
  CONSTRAINT chk_tiene_referencia CHECK (consulta_id IS NOT NULL OR procedimiento_id IS NOT NULL)
);

CREATE TABLE insumos_procedimientos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  procedimiento_id INT NOT NULL,
  producto_id INT NOT NULL,
  cantidad_usada INT NOT NULL,
  observaciones VARCHAR(255),
  FOREIGN KEY (procedimiento_id) REFERENCES procedimientos_especiales(id) ON DELETE CASCADE,
  FOREIGN KEY (producto_id) REFERENCES inventario(id) ON DELETE RESTRICT,
  INDEX idx_procedimiento (procedimiento_id),
  INDEX idx_producto (producto_id),
  CONSTRAINT chk_cantidad_usada_positiva CHECK (cantidad_usada > 0)
);

CREATE TABLE movimientos_inventario (
  id INT AUTO_INCREMENT PRIMARY KEY,
  producto_id INT NOT NULL,
  tipo_movimiento ENUM('Entrada', 'Salida', 'Ajuste', 'Vencimiento') NOT NULL,
  cantidad INT NOT NULL,
  stock_anterior INT NOT NULL,
  stock_nuevo INT NOT NULL,
  motivo VARCHAR(255),
  referencia_consulta_id INT,
  referencia_procedimiento_id INT,
  usuario VARCHAR(100),
  fecha_movimiento DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (producto_id) REFERENCES inventario(id) ON DELETE CASCADE,
  FOREIGN KEY (referencia_consulta_id) REFERENCES consultas_medicas(id) ON DELETE SET NULL,
  FOREIGN KEY (referencia_procedimiento_id) REFERENCES procedimientos_especiales(id) ON DELETE SET NULL,
  INDEX idx_producto (producto_id),
  INDEX idx_fecha (fecha_movimiento),
  INDEX idx_tipo (tipo_movimiento)
);


-- =========== MÓDULO 4: FACTURACIÓN Y REPORTES ===========

CREATE TABLE servicios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  descripcion TEXT,
  categoria VARCHAR(100),
  precio_base DECIMAL(10, 2) NOT NULL,
  duracion_estimada_minutos INT,
  activo BOOLEAN DEFAULT TRUE,
  INDEX idx_nombre (nombre),
  INDEX idx_categoria (categoria),
  INDEX idx_activo (activo),
  CONSTRAINT chk_precio_positivo CHECK (precio_base >= 0)
);

CREATE TABLE facturas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  dueno_id INT NOT NULL,
  numero_factura VARCHAR(50) UNIQUE NOT NULL,
  fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  subtotal DECIMAL(10, 2) NOT NULL,
  impuesto DECIMAL(10, 2) NOT NULL DEFAULT 0,
  descuento DECIMAL(10, 2) NOT NULL DEFAULT 0,
  total DECIMAL(10, 2) NOT NULL,
  metodo_pago ENUM('Efectivo', 'Tarjeta', 'Transferencia', 'Mixto'),
  estado ENUM('Pendiente', 'Pagada', 'Anulada') DEFAULT 'Pendiente',
  observaciones TEXT,
  FOREIGN KEY (dueno_id) REFERENCES duenos(id) ON DELETE RESTRICT,
  INDEX idx_dueno (dueno_id),
  INDEX idx_fecha (fecha_emision),
  INDEX idx_numero (numero_factura),
  INDEX idx_estado (estado),
  CONSTRAINT chk_subtotal_positivo CHECK (subtotal >= 0),
  CONSTRAINT chk_total_positivo CHECK (total >= 0)
);

CREATE TABLE items_factura (
  id INT AUTO_INCREMENT PRIMARY KEY,
  factura_id INT NOT NULL,
  tipo_item ENUM('Producto', 'Servicio') NOT NULL,
  producto_id INT,
  servicio_id INT,
  servicio_descripcion VARCHAR(255),
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(10, 2) NOT NULL,
  subtotal DECIMAL(10, 2) NOT NULL,
  FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE CASCADE,
  FOREIGN KEY (producto_id) REFERENCES inventario(id) ON DELETE RESTRICT,
  FOREIGN KEY (servicio_id) REFERENCES servicios(id) ON DELETE RESTRICT,
  INDEX idx_factura (factura_id),
  INDEX idx_producto (producto_id),
  INDEX idx_servicio (servicio_id),
  CONSTRAINT chk_cantidad_item_positiva CHECK (cantidad > 0),
  CONSTRAINT chk_precio_unitario_positivo CHECK (precio_unitario >= 0),
  CONSTRAINT chk_tipo_item_valido CHECK (
    (tipo_item = 'Producto' AND producto_id IS NOT NULL AND servicio_id IS NULL) OR
    (tipo_item = 'Servicio' AND servicio_id IS NOT NULL AND producto_id IS NULL)
  )
);


-- =========== MÓDULO 5: ACTIVIDADES ESPECIALES ===========

-- --------- Días de Adopción ---------
CREATE TABLE mascotas_adopcion (
  id INT AUTO_INCREMENT PRIMARY KEY,
  mascota_id INT NOT NULL,
  fecha_ingreso DATE NOT NULL,
  motivo_ingreso TEXT,
  estado ENUM('Disponible', 'En Proceso', 'Adoptada', 'Retirada') NOT NULL DEFAULT 'Disponible',
  historia TEXT,
  temperamento VARCHAR(255),
  necesidades_especiales TEXT,
  foto_adicional_url VARCHAR(255),
  FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
  INDEX idx_mascota (mascota_id),
  INDEX idx_estado (estado),
  INDEX idx_fecha_ingreso (fecha_ingreso)
);

CREATE TABLE adopciones (
  id INT AUTO_INCREMENT PRIMARY KEY,
  mascota_adopcion_id INT NOT NULL,
  dueno_id INT NOT NULL,
  fecha_adopcion DATE NOT NULL,
  contrato_texto TEXT,
  condiciones_especiales TEXT,
  seguimiento_requerido BOOLEAN DEFAULT TRUE,
  fecha_primer_seguimiento DATE,
  FOREIGN KEY (mascota_adopcion_id) REFERENCES mascotas_adopcion(id) ON DELETE RESTRICT,
  FOREIGN KEY (dueno_id) REFERENCES duenos(id) ON DELETE RESTRICT,
  INDEX idx_mascota_adopcion (mascota_adopcion_id),
  INDEX idx_dueno (dueno_id),
  INDEX idx_fecha (fecha_adopcion)
);

-- --------- Jornadas de Vacunación ---------
CREATE TABLE jornadas_vacunacion (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  fecha DATE NOT NULL,
  hora_inicio TIME,
  hora_fin TIME,
  ubicacion VARCHAR(255),
  descripcion TEXT,
  capacidad_maxima INT,
  estado ENUM('Planificada', 'En Curso', 'Finalizada', 'Cancelada') DEFAULT 'Planificada',
  INDEX idx_fecha (fecha),
  INDEX idx_estado (estado)
);

CREATE TABLE registro_jornada_vacunacion (
  id INT AUTO_INCREMENT PRIMARY KEY,
  jornada_id INT NOT NULL,
  mascota_id INT NOT NULL,
  dueno_id INT NOT NULL,
  vacuna_id INT NOT NULL,
  veterinario_id INT,
  fecha_hora DATETIME NOT NULL,
  lote_vacuna VARCHAR(50),
  proxima_dosis DATE,
  observaciones TEXT,
  FOREIGN KEY (jornada_id) REFERENCES jornadas_vacunacion(id) ON DELETE CASCADE,
  FOREIGN KEY (mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE,
  FOREIGN KEY (dueno_id) REFERENCES duenos(id) ON DELETE CASCADE,
  FOREIGN KEY (vacuna_id) REFERENCES inventario(id) ON DELETE RESTRICT,
  FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id) ON DELETE SET NULL,
  INDEX idx_jornada (jornada_id),
  INDEX idx_mascota (mascota_id),
  INDEX idx_dueno (dueno_id),
  INDEX idx_fecha (fecha_hora)
);

-- --------- Club de Mascotas Frecuentes ---------
CREATE TABLE club_mascotas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  dueno_id INT NOT NULL UNIQUE,
  puntos_acumulados INT DEFAULT 0,
  puntos_canjeados INT DEFAULT 0,
  puntos_disponibles INT DEFAULT 0,
  nivel VARCHAR(50) DEFAULT 'Bronce',
  fecha_inscripcion DATE NOT NULL,
  fecha_ultima_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  activo BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (dueno_id) REFERENCES duenos(id) ON DELETE CASCADE,
  INDEX idx_dueno (dueno_id),
  INDEX idx_nivel (nivel),
  INDEX idx_puntos (puntos_disponibles),
  INDEX idx_activo (activo),
  CONSTRAINT chk_puntos_no_negativos CHECK (puntos_acumulados >= 0 AND puntos_canjeados >= 0 AND puntos_disponibles >= 0)
);

CREATE TABLE transacciones_puntos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  club_mascotas_id INT NOT NULL,
  factura_id INT,
  puntos INT NOT NULL,
  tipo ENUM('Ganados', 'Canjeados', 'Expirados', 'Ajuste') NOT NULL,
  fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  descripcion VARCHAR(255),
  saldo_anterior INT NOT NULL,
  saldo_nuevo INT NOT NULL,
  FOREIGN KEY (club_mascotas_id) REFERENCES club_mascotas(id) ON DELETE CASCADE,
  FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE SET NULL,
  INDEX idx_club (club_mascotas_id),
  INDEX idx_factura (factura_id),
  INDEX idx_fecha (fecha),
  INDEX idx_tipo (tipo)
);

CREATE TABLE beneficios_club (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  descripcion TEXT,
  nivel_requerido VARCHAR(50) NOT NULL,
  puntos_necesarios INT NOT NULL,
  tipo_beneficio ENUM('Descuento', 'Servicio Gratis', 'Producto Gratis', 'Puntos Extra') NOT NULL,
  valor_beneficio DECIMAL(10, 2),
  activo BOOLEAN DEFAULT TRUE,
  INDEX idx_nivel (nivel_requerido),
  INDEX idx_puntos (puntos_necesarios),
  INDEX idx_activo (activo),
  CONSTRAINT chk_puntos_beneficio_positivo CHECK (puntos_necesarios > 0)
);

CREATE TABLE canjes_beneficios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  club_mascotas_id INT NOT NULL,
  beneficio_id INT NOT NULL,
  fecha_canje DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  puntos_canjeados INT NOT NULL,
  estado ENUM('Pendiente', 'Aplicado', 'Expirado') DEFAULT 'Pendiente',
  fecha_expiracion DATE,
  factura_id INT,
  FOREIGN KEY (club_mascotas_id) REFERENCES club_mascotas(id) ON DELETE CASCADE,
  FOREIGN KEY (beneficio_id) REFERENCES beneficios_club(id) ON DELETE RESTRICT,
  FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE SET NULL,
  INDEX idx_club (club_mascotas_id),
  INDEX idx_beneficio (beneficio_id),
  INDEX idx_fecha (fecha_canje),
  INDEX idx_estado (estado)
);
