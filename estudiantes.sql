CREATE DATABASE IF NOT EXISTS registro_estudiantes;
USE registro_estudiantes;
CREATE TABLE IF NOT EXISTS estudiantes (
    id VARCHAR(50),
    nombre VARCHAR(50),
    apellido VARCHAR(50),
    edad VARCHAR(10),
    curso VARCHAR(50)
);
