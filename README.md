# Sistema de Gestión del Gimnasio "You vs You" - Proyecto Integrador POO

Proyecto Maven en Java 17 (Programación Orientada a Objetos - UNL, 2.° ciclo, APE 03).

## Cómo abrirlo en IntelliJ IDEA
1. Clonar el repositorio (`git clone https://github.com/MatiasOC10/GimnasioPOO.git`) o descargarlo con Code -> Download ZIP.
2. IntelliJ IDEA -> File -> Open -> seleccionar la carpeta `GimnasioPOO` (la que tiene el `pom.xml`).
3. Si pregunta, elegir "Trust Project" y esperar a que cargue Maven.
4. Ejecutar:
   - `app/Main.java` -> menú interactivo por consola.
   - `app/PruebasDominio.java` -> todos los casos de prueba válidos (CP) e inválidos (CI) del informe.

## Organización por paquetes
```
ec.edu.unl.gimnasio
├── app          -> Main (menú) y PruebasDominio (casos de prueba)
├── modelo       -> Persona (abstracta), Socio, Entrenador, Plan, Membresia, Pago, ClaseGrupal
├── servicio     -> Gimnasio: clase coordinadora que administra las listas
├── vista        -> MenuConsola: pide datos y muestra resultados por consola
└── excepciones  -> AccesoDenegadoException (excepción verificada)
```

## Relaciones implementadas
| Relación | Tipo | Multiplicidad | Implementación |
|---|---|---|---|
| Persona <- Socio, Entrenador | Herencia | - | `extends Persona`, `getRol()` sobrescrito |
| Gimnasio - Socio / Entrenador / Plan | Agregación | 1 - 0..* | `List<...>` en Gimnasio |
| Gimnasio - ClaseGrupal | Composición | 1 - 0..* | `crearClase(...)` crea la clase |
| Socio - Membresia | Composición | 1 - 0..* | `contratarMembresia(...)` crea la membresía |
| Membresia - Pago | Composición | 1 - 0..* | `registrarPago(...)` crea el pago |
| Membresia - Plan | Asociación | 0..* - 1 | atributo `plan` |
| ClaseGrupal - Entrenador | Asociación | 0..* - 1 | atributo `entrenador` |
| ClaseGrupal - Socio | Agregación | 0..* - 0..* | `List<Socio> inscritos` |

## Buenas prácticas aplicadas
- Atributos privados con getters y setters validados (encapsulación); las listas se devuelven de solo lectura.
- Constantes `static final` (`MESES_MAXIMOS`, `CUPO_MAXIMO_PERMITIDO`, `DIAS_AVISO_VENCIMIENTO`...) y contadores `static` (`totalSocios`, `totalPagos`).
- Validación con excepciones: `IllegalArgumentException` (dato inválido), `IllegalStateException` (operación no permitida en el estado actual) y `AccesoDenegadoException` (verificada, declarada con `throws`).
- El menú captura `NumberFormatException`, `DateTimeParseException` y las excepciones del dominio para que el programa nunca termine abruptamente.

## Diagramas
En `docs/`: `DiagramaClases_Gimnasio.mdj` (modelo de StarUML que contiene el diagrama de clases y el diagrama de paquetes).
