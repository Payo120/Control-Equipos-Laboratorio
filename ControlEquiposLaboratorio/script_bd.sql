CREATE DATABASE bd_laboratorio_orm;

\c bd_laboratorio_orm

CREATE SCHEMA IF NOT EXISTS laboratorio;
SET search_path TO laboratorio, public;
ALTER DATABASE bd_laboratorio_orm SET search_path TO laboratorio, public;

CREATE TABLE laboratorio.tipo_equipo (
                                         id_tipo SERIAL PRIMARY KEY,
                                         nombre VARCHAR(100) NOT NULL UNIQUE,
                                         descripcion TEXT
);

CREATE TABLE laboratorio.equipo (
                                    id_equipo SERIAL PRIMARY KEY,
                                    codigo VARCHAR(50) NOT NULL UNIQUE,
                                    nombre VARCHAR(120) NOT NULL,
                                    marca VARCHAR(80),
                                    estado VARCHAR(30) NOT NULL CHECK (estado IN ('Disponible', 'En mantenimiento', 'Fuera de servicio')),
                                    cantidad INTEGER NOT NULL CHECK (cantidad >= 0),
                                    id_tipo INTEGER NOT NULL REFERENCES laboratorio.tipo_equipo(id_tipo) ON DELETE RESTRICT
);

INSERT INTO laboratorio.tipo_equipo (nombre, descripcion) VALUES
                                                              ('Computo', 'Equipos de escritorio y portatiles de uso general'),
                                                              ('Redes', 'Switches, routers y puntos de acceso del laboratorio'),
                                                              ('Proyeccion', 'Proyectores multimedia y pantallas interactivas');

INSERT INTO laboratorio.equipo (codigo, nombre, marca, estado, cantidad, id_tipo) VALUES
                                                                                      ('EQ-001', 'Laptop Latitude 5420', 'Dell', 'Disponible', 10, 1),
                                                                                      ('EQ-002', 'PC OptiPlex 7090', 'Dell', 'Disponible', 15, 1),
                                                                                      ('EQ-003', 'Workstation Z2', 'HP', 'En mantenimiento', 2, 1),
                                                                                      ('EQ-004', 'Switch Catalyst 2960', 'Cisco', 'Disponible', 4, 2),
                                                                                      ('EQ-005', 'Router ISR 4321', 'Cisco', 'En mantenimiento', 3, 2),
                                                                                      ('EQ-006', 'Access Point UniFi', 'Ubiquiti', 'Fuera de servicio', 1, 2),
                                                                                      ('EQ-007', 'Proyector PowerLite E20', 'Epson', 'Disponible', 5, 3),
                                                                                      ('EQ-008', 'Proyector Smart Beam', 'ViewSonic', 'Fuera de servicio', 1, 3);