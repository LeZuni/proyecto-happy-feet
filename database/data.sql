-- =========================================================
-- HAPPY FEET - DATOS INICIALES Y DE PRUEBA
-- Ejecutar DESPUES de schema.sql
-- Las fechas usan CURDATE()/NOW() para que las alertas de
-- vencimiento y las citas siempre tengan sentido al probar.
-- =========================================================

USE happy_feet_veterinaria;
SET NAMES utf8mb4;

-- =========== DATOS DE LAS TABLAS DE CONSULTA ===========

INSERT INTO especies (nombre, descripcion) VALUES
('Perro', 'Canis lupus familiaris'),
('Gato', 'Felis catus'),
('Ave', 'Aves domésticas'),
('Roedor', 'Pequeños mamíferos roedores'),
('Reptil', 'Reptiles domésticos'),
('Conejo', 'Oryctolagus cuniculus');

INSERT INTO razas (especie_id, nombre, caracteristicas) VALUES
(1, 'Labrador Retriever', 'Tamaño grande, amigable, energético'),
(1, 'Golden Retriever', 'Tamaño grande, inteligente, leal'),
(1, 'Pastor Alemán', 'Tamaño grande, inteligente, protector'),
(1, 'Bulldog Francés', 'Tamaño pequeño, cariñoso, juguetón'),
(1, 'Chihuahua', 'Tamaño muy pequeño, alerta, valiente'),
(1, 'Poodle', 'Inteligente, hipoalergénico'),
(1, 'Mestizo', 'Cruza de razas'),
(2, 'Persa', 'Pelo largo, tranquilo'),
(2, 'Siamés', 'Vocal, social, activo'),
(2, 'Maine Coon', 'Grande, peludo, amigable'),
(2, 'Bengalí', 'Activo, manchas de leopardo'),
(2, 'Mestizo', 'Cruza de razas'),
(3, 'Canario', 'Pequeño, cantante'),
(3, 'Periquito', 'Pequeño, social'),
(3, 'Loro', 'Inteligente, longevo'),
(4, 'Hámster Sirio', 'Pequeño, nocturno'),
(4, 'Cobayo', 'Mediano, social'),
(5, 'Iguana Verde', 'Herbívoro, grande'),
(5, 'Tortuga', 'Lenta, longeva'),
(6, 'Conejo Enano', 'Pequeño, dócil'),
(6, 'Conejo Gigante', 'Grande, tranquilo');

INSERT INTO producto_tipos (nombre, descripcion) VALUES
('Medicamento', 'Fármacos y medicinas veterinarias'),
('Vacuna', 'Vacunas y biológicos'),
('Insumo Médico', 'Material médico y quirúrgico'),
('Alimento', 'Alimento para mascotas'),
('Accesorio', 'Accesorios y productos de cuidado');

INSERT INTO evento_tipos (nombre, descripcion) VALUES
('Vacunación', 'Aplicación de vacunas'),
('Consulta General', 'Consulta veterinaria general'),
('Cirugía', 'Procedimiento quirúrgico'),
('Desparasitación', 'Tratamiento antiparasitario'),
('Control de Peso', 'Seguimiento de peso'),
('Examen de Sangre', 'Análisis de laboratorio'),
('Radiografía', 'Estudio de imagen'),
('Emergencia', 'Atención de emergencia'),
('Limpieza Dental', 'Profilaxis dental');

INSERT INTO cita_estados (nombre, descripcion) VALUES
('Programada', 'Cita agendada pendiente'),
('Confirmada', 'Cita confirmada por el cliente'),
('En Proceso', 'Mascota siendo atendida'),
('Finalizada', 'Cita completada'),
('Cancelada', 'Cita cancelada'),
('No Asistió', 'Cliente no se presentó');

-- =========== MÓDULO 1: DUEÑOS Y MASCOTAS ===========

-- El dueño 1 es la propia clínica: sirve como "dueño" de las mascotas en adopción
INSERT INTO duenos (nombre_completo, documento_identidad, direccion, telefono, email, contacto_emergencia) VALUES
('Clínica Happy Feet (Refugio)', '900123456-7', 'Calle 45 # 12-34, Bogotá', '6015550123', 'refugio@happyfeet.com', NULL),
('María Fernanda López', '1012345678', 'Carrera 7 # 45-10, Bogotá', '3001112233', 'maria.lopez@email.com', 'Pedro López - 3001112200'),
('Carlos Andrés Ramírez', '80123456', 'Calle 100 # 15-20, Bogotá', '3012223344', 'carlos.ramirez@email.com', 'Sandra Ramírez - 3012223300'),
('Laura Sofía Gómez', '1023456789', 'Av. Boyacá # 63-25, Bogotá', '3023334455', 'laura.gomez@email.com', NULL),
('Juan Pablo Martínez', '79234567', 'Calle 80 # 20-15, Bogotá', '3034445566', 'juan.martinez@email.com', 'Ana Martínez - 3034445500'),
('Ana Milena Torres', '52345678', 'Carrera 15 # 90-12, Bogotá', '3045556677', 'ana.torres@email.com', NULL),
('Diego Alejandro Castro', '1034567890', 'Calle 53 # 24-30, Bogotá', '3056667788', 'diego.castro@email.com', 'Luisa Castro - 3056667700'),
('Valentina Rojas', '1045678901', 'Carrera 30 # 40-50, Bogotá', '3067778899', 'valentina.rojas@email.com', NULL),
('Santiago Herrera', '1056789012', 'Calle 72 # 10-05, Bogotá', '3078889900', 'santiago.herrera@email.com', 'Marta Herrera - 3078889911'),
('Camila Vargas', '1067890123', 'Av. 68 # 25-40, Bogotá', '3089990011', 'camila.vargas@email.com', NULL);

INSERT INTO mascotas (dueno_id, nombre, raza_id, fecha_nacimiento, sexo, peso_actual, microchip, tatuaje, url_foto, alergias, condiciones_preexistentes) VALUES
(2, 'Max', 1, '2019-03-15', 'Macho', 30.50, '985112000000001', NULL, 'https://fotos.happyfeet.com/max.jpg', 'Penicilina', 'Displasia de cadera leve'),
(2, 'Luna', 9, '2021-07-20', 'Hembra', 4.20, '985112000000002', NULL, 'https://fotos.happyfeet.com/luna.jpg', NULL, NULL),
(3, 'Rocky', 3, '2018-01-10', 'Macho', 34.00, '985112000000003', 'TAT-0045', NULL, NULL, 'Artritis'),
(4, 'Mía', 6, '2020-11-05', 'Hembra', 6.80, '985112000000004', NULL, NULL, 'Pollo', NULL),
(5, 'Toby', 4, '2022-02-14', 'Macho', 12.30, '985112000000005', NULL, 'https://fotos.happyfeet.com/toby.jpg', NULL, 'Problemas respiratorios (braquicéfalo)'),
(6, 'Nala', 10, '2020-05-30', 'Hembra', 6.10, '985112000000006', NULL, NULL, NULL, NULL),
(7, 'Coco', 15, '2015-09-01', 'Macho', 0.45, NULL, 'ANILLO-77', NULL, NULL, NULL),
(8, 'Pelusa', 20, '2023-03-12', 'Hembra', 1.60, NULL, NULL, NULL, NULL, NULL),
(9, 'Bruno', 7, '2017-08-22', 'Macho', 22.00, '985112000000009', NULL, NULL, 'Ninguna conocida', 'Sobrepeso'),
(10, 'Simba', 12, '2022-12-01', 'Macho', 5.30, '985112000000010', NULL, NULL, NULL, NULL),
(4, 'Kira', 2, '2021-04-18', 'Hembra', 27.00, '985112000000011', NULL, NULL, NULL, NULL),
(1, 'Chispa', 5, '2023-06-01', 'Hembra', 2.80, '985112000000012', NULL, NULL, NULL, 'Rescatada, tímida'),
(1, 'Manchas', 7, '2022-10-10', 'Macho', 15.40, '985112000000013', NULL, NULL, NULL, 'Rescatado de la calle'),
(10, 'Michi', 12, '2023-01-20', 'Hembra', 3.90, '985112000000014', NULL, NULL, NULL, NULL);

-- =========== MÓDULO 2: VETERINARIOS, CITAS Y CONSULTAS ===========

INSERT INTO veterinarios (nombre_completo, documento_identidad, licencia_profesional, especialidad, telefono, email, fecha_contratacion) VALUES
('Dra. Patricia Salazar', '51111111', 'MV-10001', 'Medicina general', '3101110001', 'patricia.salazar@happyfeet.com', '2018-02-01'),
('Dr. Andrés Mejía', '79222222', 'MV-10002', 'Cirugía', '3101110002', 'andres.mejia@happyfeet.com', '2019-06-15'),
('Dra. Carolina Peña', '52333333', 'MV-10003', 'Odontología veterinaria', '3101110003', 'carolina.pena@happyfeet.com', '2020-09-01'),
('Dr. Felipe Ortiz', '80444444', 'MV-10004', 'Animales exóticos', '3101110004', 'felipe.ortiz@happyfeet.com', '2021-03-10');

-- Estados: 1 Programada, 2 Confirmada, 3 En Proceso, 4 Finalizada, 5 Cancelada, 6 No Asistió
INSERT INTO citas (mascota_id, veterinario_id, fecha_hora, motivo, estado_id, observaciones) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 40 DAY), 'Control de cadera', 4, NULL),
(2, 2, DATE_SUB(NOW(), INTERVAL 30 DAY), 'Valoración para esterilización', 4, NULL),
(3, 1, DATE_SUB(NOW(), INTERVAL 20 DAY), 'Dolor en las articulaciones', 4, NULL),
(4, 3, DATE_SUB(NOW(), INTERVAL 15 DAY), 'Revisión dental', 4, NULL),
(6, 1, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00:00'), 'Vacunación anual', 1, NULL),
(7, 4, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:30:00'), 'Revisión de plumaje', 2, 'El dueño confirmó por teléfono'),
(8, 1, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY), '15:00:00'), 'Control de crecimiento', 1, NULL),
(9, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Control de peso', 5, 'Cancelada por el cliente');

INSERT INTO consultas_medicas (mascota_id, veterinario_id, cita_id, fecha_hora, motivo, sintomas, diagnostico, recomendaciones, observaciones, peso_registrado, temperatura) VALUES
(1, 1, 1, DATE_SUB(NOW(), INTERVAL 40 DAY), 'Control de cadera', 'Cojea al levantarse', 'Displasia de cadera leve', 'Control de peso y ejercicio suave', NULL, 30.50, 38.50),
(2, 2, 2, DATE_SUB(NOW(), INTERVAL 30 DAY), 'Valoración para esterilización', NULL, 'Apta para cirugía', 'Ayuno de 12 horas antes', NULL, 4.20, 38.60),
(3, 1, 3, DATE_SUB(NOW(), INTERVAL 20 DAY), 'Dolor en las articulaciones', 'Dificultad para subir escaleras', 'Artritis', 'Antiinflamatorio por 10 días', 'Revisar en un mes', 34.00, 38.40),
(4, 3, 4, DATE_SUB(NOW(), INTERVAL 15 DAY), 'Revisión dental', 'Mal aliento', 'Sarro moderado', 'Limpieza dental programada', NULL, 6.80, 38.70),
(5, 2, NULL, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Tos y ronquidos', 'Tos seca', 'Parásitos intestinales', 'Desparasitar y repetir en 15 días', NULL, 12.30, 38.90),
(6, 4, NULL, DATE_SUB(NOW(), INTERVAL 7 DAY), 'Chequeo general', NULL, 'Sana', 'Seguir con el plan de vacunación', NULL, 6.10, 38.30);

INSERT INTO procedimientos_especiales (mascota_id, veterinario_id, tipo_procedimiento, nombre_procedimiento, fecha_hora, duracion_estimada_minutos, informacion_preoperatoria, detalle_procedimiento, complicaciones, seguimiento_postoperatorio, proximo_control, estado, costo_procedimiento) VALUES
(2, 1, 'Cirugía', 'Esterilización (ovariohisterectomía)', DATE_SUB(NOW(), INTERVAL 25 DAY), 60, 'Ayuno de 12 horas, exámenes normales', 'Ovariohisterectomía sin novedades', NULL, 'Reposo 10 días, collar isabelino, curación diaria', DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'Finalizado', 280000.00),
(5, 3, 'Limpieza Dental', 'Profilaxis dental con ultrasonido', DATE_SUB(NOW(), INTERVAL 12 DAY), 45, 'Anestesia ligera', 'Remoción de sarro y pulido', NULL, 'Dieta blanda por 2 días', NULL, 'Finalizado', 150000.00),
(3, 2, 'Cirugía', 'Extracción de masa cutánea', TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 5 DAY), '08:30:00'), 90, 'Exámenes de sangre previos pendientes', 'Se retirará una masa en el costado izquierdo', NULL, NULL, NULL, 'Programado', 350000.00);

-- Eventos: 1 Vacunación, 2 Consulta General, 3 Cirugía, 4 Desparasitación, 5 Control de Peso, 9 Limpieza Dental
INSERT INTO historial_medico (mascota_id, fecha_evento, evento_tipo_id, descripcion, diagnostico, tratamiento_recomendado, veterinario_id, consulta_id, procedimiento_id) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 200 DAY), 1, 'Vacuna Quíntuple Canina', NULL, 'Refuerzo en un año', 1, NULL, NULL),
(1, DATE_SUB(CURDATE(), INTERVAL 40 DAY), 2, 'Control de cadera', 'Displasia de cadera leve', 'Control de peso y ejercicio suave', 1, 1, NULL),
(2, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 2, 'Valoración para esterilización', 'Apta para cirugía', 'Ayuno de 12 horas', 2, 2, NULL),
(2, DATE_SUB(CURDATE(), INTERVAL 25 DAY), 3, 'Esterilización (ovariohisterectomía)', NULL, 'Reposo 10 días', 1, NULL, 1),
(2, DATE_SUB(CURDATE(), INTERVAL 300 DAY), 1, 'Vacuna Triple Felina', NULL, 'Refuerzo anual', 1, NULL, NULL),
(3, DATE_SUB(CURDATE(), INTERVAL 20 DAY), 2, 'Dolor en las articulaciones', 'Artritis', 'Antiinflamatorio por 10 días', 1, 3, NULL),
(4, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 2, 'Revisión dental', 'Sarro moderado', 'Limpieza dental programada', 3, 4, NULL),
(5, DATE_SUB(CURDATE(), INTERVAL 12 DAY), 9, 'Profilaxis dental con ultrasonido', NULL, 'Dieta blanda por 2 días', 3, NULL, 2),
(5, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 4, 'Desparasitación interna', 'Parásitos intestinales', 'Repetir en 15 días', 2, 5, NULL),
(6, DATE_SUB(CURDATE(), INTERVAL 7 DAY), 2, 'Chequeo general', 'Sana', 'Seguir plan de vacunación', 4, 6, NULL),
(9, DATE_SUB(CURDATE(), INTERVAL 90 DAY), 5, 'Control de peso por sobrepeso', 'Sobrepeso', 'Dieta y caminatas', 1, NULL, NULL);

-- =========== MÓDULO 3: PROVEEDORES E INVENTARIO ===========

INSERT INTO proveedores (nombre_empresa, contacto, telefono, email, direccion, sitio_web) VALUES
('VetFarma S.A.S.', 'Jorge Ruiz', '6017001001', 'ventas@vetfarma.com', 'Zona Industrial, Bogotá', 'www.vetfarma.com'),
('BioVacunas Colombia', 'Marcela Díaz', '6017002002', 'pedidos@biovacunas.co', 'Calle 13 # 50-20, Bogotá', 'www.biovacunas.co'),
('MediSuministros Vet', 'Camilo Prada', '6017003003', 'contacto@medisuministros.com', 'Av. Américas # 40-10, Bogotá', NULL),
('PetFood Distribuciones', 'Natalia Rueda', '6017004004', 'ventas@petfood.com', 'Carrera 68 # 12-30, Bogotá', 'www.petfood.com');

-- Tipos: 1 Medicamento, 2 Vacuna, 3 Insumo Médico, 4 Alimento, 5 Accesorio
INSERT INTO inventario (nombre_producto, producto_tipo_id, descripcion, fabricante, proveedor_id, lote, cantidad_stock, stock_minimo, unidad_medida, fecha_vencimiento, precio_compra, precio_venta, requiere_receta) VALUES
('Amoxicilina 500mg', 1, 'Antibiótico de amplio espectro', 'Laboratorios Vet', 1, 'AMX-2401', 120, 20, 'tableta', DATE_ADD(CURDATE(), INTERVAL 400 DAY), 900.00, 1500.00, TRUE),
('Meloxicam 1.5mg/ml', 1, 'Antiinflamatorio', 'Boehringer', 1, 'MEL-2402', 15, 5, 'frasco', DATE_ADD(CURDATE(), INTERVAL 300 DAY), 18000.00, 28000.00, TRUE),
('Ivermectina 1%', 1, 'Antiparasitario', 'Laboratorios Vet', 1, 'IVE-2403', 30, 10, 'frasco', DATE_ADD(CURDATE(), INTERVAL 250 DAY), 7000.00, 12000.00, FALSE),
('Dipirona 500mg', 1, 'Analgésico y antipirético', 'Laboratorios Vet', 3, 'DIP-2404', 80, 20, 'tableta', DATE_ADD(CURDATE(), INTERVAL 500 DAY), 400.00, 800.00, TRUE),
('Prednisolona 5mg', 1, 'Corticoide', 'Laboratorios Vet', 1, 'PRE-2405', 40, 10, 'tableta', DATE_ADD(CURDATE(), INTERVAL 25 DAY), 600.00, 1200.00, TRUE),
('Omeprazol 20mg', 1, 'Protector gástrico', 'Laboratorios Vet', 3, 'OME-2406', 8, 15, 'cápsula', DATE_ADD(CURDATE(), INTERVAL 200 DAY), 500.00, 1000.00, FALSE),
('Vacuna Triple Felina', 2, 'Panleucopenia, rinotraqueítis y calicivirus', 'Zoetis', 2, 'VTF-9001', 25, 5, 'dosis', DATE_ADD(CURDATE(), INTERVAL 180 DAY), 18000.00, 30000.00, FALSE),
('Vacuna Antirrábica', 2, 'Vacuna contra la rabia', 'Zoetis', 2, 'VAR-9002', 60, 10, 'dosis', DATE_ADD(CURDATE(), INTERVAL 220 DAY), 15000.00, 30000.00, FALSE),
('Vacuna Quíntuple Canina', 2, 'Moquillo, hepatitis, parvovirus, parainfluenza', 'MSD', 2, 'VQC-9003', 40, 8, 'dosis', DATE_ADD(CURDATE(), INTERVAL 150 DAY), 28000.00, 45000.00, FALSE),
('Vacuna Bordetella', 2, 'Tos de las perreras', 'MSD', 2, 'VBO-9004', 6, 5, 'dosis', DATE_SUB(CURDATE(), INTERVAL 10 DAY), 22000.00, 38000.00, FALSE),
('Jeringas 5ml', 3, 'Jeringas desechables', 'MedPlus', 3, 'JER-5001', 200, 50, 'unidad', DATE_ADD(CURDATE(), INTERVAL 700 DAY), 300.00, 800.00, FALSE),
('Gasas estériles', 3, 'Paquete de gasas estériles', 'MedPlus', 3, 'GAS-5002', 150, 40, 'unidad', DATE_ADD(CURDATE(), INTERVAL 600 DAY), 200.00, 500.00, FALSE),
('Guantes de látex', 3, 'Guantes desechables', 'MedPlus', 3, 'GUA-5003', 300, 100, 'unidad', DATE_ADD(CURDATE(), INTERVAL 800 DAY), 250.00, 600.00, FALSE),
('Sutura Nylon 3-0', 3, 'Hilo de sutura quirúrgica', 'SurgiVet', 3, 'SUT-5004', 5, 10, 'unidad', DATE_ADD(CURDATE(), INTERVAL 500 DAY), 7000.00, 12000.00, FALSE),
('Alimento Renal 2kg', 4, 'Dieta para enfermedad renal', 'Hill''s', 4, 'ALI-7001', 20, 5, 'bolsa', DATE_ADD(CURDATE(), INTERVAL 270 DAY), 38000.00, 55000.00, FALSE),
('Collar isabelino', 5, 'Collar de protección post-cirugía', 'PetCare', 4, NULL, 25, 5, 'unidad', NULL, 12000.00, 25000.00, FALSE),
('Antipulgas NexGard', 1, 'Antipulgas y garrapatas masticable', 'Boehringer', 1, 'NEX-2407', 35, 10, 'tableta', DATE_ADD(CURDATE(), INTERVAL 350 DAY), 45000.00, 70000.00, FALSE);

INSERT INTO prescripciones (consulta_id, procedimiento_id, producto_id, cantidad, dosis, frecuencia, duracion_dias, instrucciones) VALUES
(1, NULL, 1, 14, '1 tableta', 'Cada 12 horas', 7, 'Dar con comida'),
(3, NULL, 2, 1, '0.1 ml por kg', 'Cada 24 horas', 10, 'Dar con comida'),
(5, NULL, 3, 1, '0.2 ml por kg', 'Dosis única', 1, 'Repetir en 15 días'),
(NULL, 1, 2, 1, '0.1 ml por kg', 'Cada 24 horas', 5, 'Control del dolor postoperatorio');

INSERT INTO insumos_procedimientos (procedimiento_id, producto_id, cantidad_usada, observaciones) VALUES
(1, 11, 3, 'Anestesia y medicación'),
(1, 12, 10, 'Limpieza del campo quirúrgico'),
(1, 14, 2, 'Sutura de piel'),
(2, 13, 4, 'Uso del equipo dental');

INSERT INTO movimientos_inventario (producto_id, tipo_movimiento, cantidad, stock_anterior, stock_nuevo, motivo, referencia_consulta_id, referencia_procedimiento_id, usuario) VALUES
(1, 'Entrada', 134, 0, 134, 'Stock inicial', NULL, NULL, 'sistema'),
(1, 'Salida', 14, 134, 120, 'Prescripción en consulta #1', 1, NULL, 'sistema'),
(2, 'Entrada', 17, 0, 17, 'Stock inicial', NULL, NULL, 'sistema'),
(2, 'Salida', 1, 17, 16, 'Prescripción en consulta #3', 3, NULL, 'sistema'),
(2, 'Salida', 1, 16, 15, 'Prescripción en procedimiento #1', NULL, 1, 'sistema'),
(14, 'Entrada', 7, 0, 7, 'Stock inicial', NULL, NULL, 'sistema'),
(14, 'Salida', 2, 7, 5, 'Insumo usado en procedimiento #1', NULL, 1, 'sistema'),
(10, 'Entrada', 6, 0, 6, 'Stock inicial', NULL, NULL, 'sistema');

-- =========== MÓDULO 4: SERVICIOS Y FACTURAS ===========

INSERT INTO servicios (nombre, descripcion, categoria, precio_base, duracion_estimada_minutos) VALUES
('Consulta general', 'Consulta veterinaria general', 'Consulta', 50000.00, 30),
('Vacunación', 'Aplicación de vacuna (la vacuna se cobra aparte)', 'Prevención', 35000.00, 15),
('Esterilización', 'Cirugía de esterilización', 'Cirugía', 280000.00, 60),
('Limpieza dental', 'Profilaxis dental con ultrasonido', 'Odontología', 150000.00, 45),
('Desparasitación', 'Tratamiento antiparasitario', 'Prevención', 30000.00, 15),
('Examen de sangre', 'Hemograma y química sanguínea', 'Laboratorio', 90000.00, 20),
('Radiografía', 'Estudio de imagen', 'Laboratorio', 120000.00, 20),
('Baño y peluquería', 'Servicio de estética', 'Estética', 40000.00, 60),
('Atención de emergencia', 'Atención urgente', 'Consulta', 100000.00, 45),
('Control de peso', 'Seguimiento de peso y dieta', 'Consulta', 20000.00, 15);

-- Los totales ya incluyen el descuento del club y el IVA del 19%
INSERT INTO facturas (dueno_id, numero_factura, fecha_emision, subtotal, impuesto, descuento, total, metodo_pago, estado, observaciones) VALUES
(2, 'FAC-2026-000001', DATE_SUB(NOW(), INTERVAL 40 DAY), 71000.00, 13490.00, 0.00, 84490.00, 'Efectivo', 'Pagada', 'Consulta #1 - mascota Max'),
(3, 'FAC-2026-000002', DATE_SUB(NOW(), INTERVAL 20 DAY), 78000.00, 14820.00, 0.00, 92820.00, 'Tarjeta', 'Pagada', 'Consulta #3 - mascota Rocky'),
(2, 'FAC-2026-000003', DATE_SUB(NOW(), INTERVAL 25 DAY), 308000.00, 55594.00, 15400.00, 348194.00, 'Tarjeta', 'Pagada', 'Procedimiento #1 - mascota Luna'),
(4, 'FAC-2026-000004', DATE_SUB(NOW(), INTERVAL 8 DAY), 65000.00, 12350.00, 0.00, 77350.00, 'Transferencia', 'Pagada', 'Venta directa - vacuna antirrábica'),
(5, 'FAC-2026-000005', DATE_SUB(NOW(), INTERVAL 12 DAY), 150000.00, 28500.00, 0.00, 178500.00, NULL, 'Pendiente', 'Procedimiento #2 - mascota Toby');

INSERT INTO items_factura (factura_id, tipo_item, producto_id, servicio_id, servicio_descripcion, cantidad, precio_unitario, subtotal) VALUES
(1, 'Servicio', NULL, 1, 'Consulta general', 1, 50000.00, 50000.00),
(1, 'Producto', 1, NULL, 'Amoxicilina 500mg', 14, 1500.00, 21000.00),
(2, 'Servicio', NULL, 1, 'Consulta general', 1, 50000.00, 50000.00),
(2, 'Producto', 2, NULL, 'Meloxicam 1.5mg/ml', 1, 28000.00, 28000.00),
(3, 'Servicio', NULL, 3, 'Esterilización', 1, 280000.00, 280000.00),
(3, 'Producto', 2, NULL, 'Meloxicam 1.5mg/ml', 1, 28000.00, 28000.00),
(4, 'Servicio', NULL, 2, 'Vacunación', 1, 35000.00, 35000.00),
(4, 'Producto', 8, NULL, 'Vacuna Antirrábica', 1, 30000.00, 30000.00),
(5, 'Servicio', NULL, 4, 'Limpieza dental', 1, 150000.00, 150000.00);

-- =========== MÓDULO 5: ACTIVIDADES ESPECIALES ===========

-- Adopciones (la mascota 14 ya fue adoptada por el dueño 10)
INSERT INTO mascotas_adopcion (mascota_id, fecha_ingreso, motivo_ingreso, estado, historia, temperamento, necesidades_especiales) VALUES
(12, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'Rescatada de la calle', 'Disponible', 'Encontrada cerca de un parque, muy flaca', 'Tímida pero cariñosa', 'Necesita paciencia al inicio'),
(13, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 'Abandonado en la clínica', 'Disponible', 'Lo dejaron atado en la puerta', 'Juguetón y sociable', 'Ninguna'),
(14, DATE_SUB(CURDATE(), INTERVAL 90 DAY), 'Camada rescatada', 'Adoptada', 'Hermana de una camada de 4', 'Curiosa y tranquila', 'Ninguna');

INSERT INTO adopciones (mascota_adopcion_id, dueno_id, fecha_adopcion, contrato_texto, condiciones_especiales, seguimiento_requerido, fecha_primer_seguimiento) VALUES
(3, 10, DATE_SUB(CURDATE(), INTERVAL 45 DAY), 'CONTRATO DE ADOPCION - El adoptante se compromete a cuidar y alimentar a la mascota, no abandonarla y permitir el seguimiento de Happy Feet.', 'Esterilizar a los 6 meses', TRUE, DATE_SUB(CURDATE(), INTERVAL 15 DAY));

-- Jornadas de vacunación
INSERT INTO jornadas_vacunacion (nombre, fecha, hora_inicio, hora_fin, ubicacion, descripcion, capacidad_maxima, estado) VALUES
('Jornada antirrábica - Barrio Centro', DATE_SUB(CURDATE(), INTERVAL 60 DAY), '08:00:00', '14:00:00', 'Parque Central', 'Vacunación antirrábica a bajo costo', 50, 'Finalizada'),
('Jornada de vacunación felina', DATE_ADD(CURDATE(), INTERVAL 20 DAY), '09:00:00', '13:00:00', 'Clínica Happy Feet', 'Triple felina para gatos de la zona', 40, 'Planificada'),
('Jornada antirrábica del mes', CURDATE(), '08:00:00', '17:00:00', 'Clínica Happy Feet', 'Jornada en curso para probar el registro rápido', 30, 'En Curso');

INSERT INTO registro_jornada_vacunacion (jornada_id, mascota_id, dueno_id, vacuna_id, veterinario_id, fecha_hora, lote_vacuna, proxima_dosis, observaciones) VALUES
(1, 1, 2, 8, 1, DATE_SUB(NOW(), INTERVAL 60 DAY), 'VAR-9002', DATE_ADD(CURDATE(), INTERVAL 305 DAY), NULL),
(1, 3, 3, 8, 1, DATE_SUB(NOW(), INTERVAL 60 DAY), 'VAR-9002', DATE_ADD(CURDATE(), INTERVAL 305 DAY), NULL),
(1, 5, 5, 8, 2, DATE_SUB(NOW(), INTERVAL 60 DAY), 'VAR-9002', DATE_ADD(CURDATE(), INTERVAL 305 DAY), 'Nervioso durante la aplicación'),
(1, 9, 9, 8, 2, DATE_SUB(NOW(), INTERVAL 60 DAY), 'VAR-9002', DATE_ADD(CURDATE(), INTERVAL 305 DAY), NULL);

INSERT INTO historial_medico (mascota_id, fecha_evento, evento_tipo_id, descripcion, veterinario_id) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 1, 'Vacuna Antirrábica (Jornada antirrábica - Barrio Centro)', 1),
(3, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 1, 'Vacuna Antirrábica (Jornada antirrábica - Barrio Centro)', 1),
(5, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 1, 'Vacuna Antirrábica (Jornada antirrábica - Barrio Centro)', 2),
(9, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 1, 'Vacuna Antirrábica (Jornada antirrábica - Barrio Centro)', 2);

-- Club de mascotas frecuentes (1 punto por cada $1.000 pagados)
-- Niveles: Bronce 0, Plata 500, Oro 1500, Platino 3000 puntos acumulados
INSERT INTO club_mascotas (dueno_id, puntos_acumulados, puntos_canjeados, puntos_disponibles, nivel, fecha_inscripcion) VALUES
(2, 732, 0, 732, 'Plata', DATE_SUB(CURDATE(), INTERVAL 60 DAY)),
(3, 92, 0, 92, 'Bronce', DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(4, 1577, 200, 1377, 'Oro', DATE_SUB(CURDATE(), INTERVAL 120 DAY));

INSERT INTO beneficios_club (nombre, descripcion, nivel_requerido, puntos_necesarios, tipo_beneficio, valor_beneficio) VALUES
('Consulta con 10% de descuento', 'Descuento en una consulta general', 'Bronce', 200, 'Descuento', 10.00),
('Kit de bienvenida (collar)', 'Un collar de regalo', 'Bronce', 300, 'Producto Gratis', 25000.00),
('Baño gratis', 'Un baño y peluquería sin costo', 'Bronce', 400, 'Servicio Gratis', 40000.00),
('Desparasitación gratis', 'Una desparasitación sin costo', 'Plata', 600, 'Servicio Gratis', 30000.00),
('Doble puntos por un mes', 'Gana el doble de puntos durante 30 días', 'Oro', 500, 'Puntos Extra', 2.00),
('Limpieza dental gratis', 'Una limpieza dental sin costo', 'Platino', 3000, 'Servicio Gratis', 150000.00);

INSERT INTO transacciones_puntos (club_mascotas_id, factura_id, puntos, tipo, fecha, descripcion, saldo_anterior, saldo_nuevo) VALUES
(1, NULL, 300, 'Ajuste', DATE_SUB(NOW(), INTERVAL 60 DAY), 'Bono de bienvenida', 0, 300),
(1, 1, 84, 'Ganados', DATE_SUB(NOW(), INTERVAL 40 DAY), 'Compra factura FAC-2026-000001', 300, 384),
(1, 3, 348, 'Ganados', DATE_SUB(NOW(), INTERVAL 25 DAY), 'Compra factura FAC-2026-000003', 384, 732),
(2, 2, 92, 'Ganados', DATE_SUB(NOW(), INTERVAL 20 DAY), 'Compra factura FAC-2026-000002', 0, 92),
(3, NULL, 1500, 'Ajuste', DATE_SUB(NOW(), INTERVAL 120 DAY), 'Migración de puntos de clientes antiguos', 0, 1500),
(3, 4, 77, 'Ganados', DATE_SUB(NOW(), INTERVAL 8 DAY), 'Compra factura FAC-2026-000004', 1500, 1577),
(3, NULL, -200, 'Canjeados', DATE_SUB(NOW(), INTERVAL 8 DAY), 'Canje: Consulta con 10% de descuento', 1577, 1377);

INSERT INTO canjes_beneficios (club_mascotas_id, beneficio_id, fecha_canje, puntos_canjeados, estado, fecha_expiracion, factura_id) VALUES
(3, 1, DATE_SUB(NOW(), INTERVAL 8 DAY), 200, 'Aplicado', DATE_ADD(CURDATE(), INTERVAL 22 DAY), 4);
