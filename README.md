# Práctica Guiada: Hilos en JavaFX
**Universidad Americana (UAM)**  
**Facultad de Ingeniería y Arquitectura**  
**Asignatura:** Programación de Aplicaciones de Escritorio  
**Unidad III:** Programación concurrente  
**Sesión:** 26 — 16/10/2026  
**Tema:** Creación de hilos con `Thread` y `Runnable`  
**Modalidad:** Individual  
**Evaluación:** Formativa, sin puntaje  
**Repositorio:** `JavaFX_Hilos_Practica` / `PracticaHilosJavaFX`  

---

## 1. Resultado de Aprendizaje e Indicadores

* **RA3:** Desarrolla aplicaciones de escritorio con interfaz gráfica que implementan programación concurrente mediante el uso de hilos y mecanismos de sincronización.
* **IL3.1:** Implementa hilos para ejecutar tareas concurrentes dentro de la aplicación.
* **IL3.3:** Gestiona el ciclo de vida y la detención controlada de los hilos durante la ejecución de la aplicación.

---

## 2. Objetivos de la Práctica

Al finalizar la práctica, se adquirieron y validaron las siguientes competencias:
1. Crear y ejecutar hilos en Java utilizando las clases `Thread` e interfaces funcionales `Runnable`.
2. Comprender y aplicar la ejecución asíncrona de tareas de cómputo en segundo plano (*background threads*).
3. Mantener permanentemente receptiva y fluida la interfaz de usuario (*responsive UI*) evitando bloqueos y congelamientos.
4. Actualizar de forma segura y controlada los nodos de la interfaz JavaFX mediante `Platform.runLater()`.
5. Implementar patrones de detención e interrupción controlada y cooperativa (`interrupt()`, `isInterrupted()`, `InterruptedException`).
6. Resolver un caso de negocio empresarial de alta demanda para **NicaData Solutions S.A.** mediante ejecución multi-hilo concurrente.

---

## 3. Caso de Estudio: NicaData Solutions S.A.

La empresa **NicaData Solutions S.A.** requiere una aplicación de escritorio de alto rendimiento para simular y gestionar el procesamiento masivo de datos comerciales. El procesamiento de lotes involucra operaciones de lectura, validación y persistencia que toman varios segundos por lote. 

### Requerimientos Operativos
1. **No bloqueo:** La ventana gráfica debe responder fluidamente a eventos del sistema operativo (arrastre, redimensionamiento, minimizado y clicks) durante todo el ciclo de procesamiento.
2. **Visualización de progreso:** Se debe informar al usuario el porcentaje de avance, la cantidad de elementos procesados y el estado del lote en tiempo real.
3. **Control de ciclo de vida:** Posibilidad de pausar, detener limpiamente o reiniciar el proceso en cualquier momento.
4. **Protección contra reentrancia:** No permitir invocar una tarea que ya se encuentra en ejecución activa.

---

## 4. Conceptos Fundamentales

| Concepto | Tipo / Ámbito | Rol y Descripción Técnica |
| :--- | :--- | :--- |
| **`Thread`** | Clase (`java.lang.Thread`) | Representa una unidad de ejecución concurrente gestionada por la máquina virtual y mapeada a hilos nativos del sistema operativo. |
| **`Runnable`** | Interfaz funcional | Define el contrato del trabajo o tarea ejecutable a través de su método abstracto `run()`. |
| **`start()`** | Método de `Thread` | Solicita al planificador de la JVM y del SO la creación y arranque de un nuevo hilo nativo. **Importante:** Llamar directamente a `run()` ejecuta la tarea secuencialmente en el hilo actual sin crear concurrencia. |
| **`sleep(ms)`** | Método estático de `Thread` | Suspende la ejecución del hilo invocador durante un lapso de tiempo especificado. Permite simular latencias de I/O o cálculo pesado; es sensible a señales de interrupción (`InterruptedException`). |
| **`Platform.runLater(Runnable)`** | Método estático de JavaFX | Encola un bloque de código para ser despachado asíncronamente en el **JavaFX Application Thread**. Es la única vía autorizada por el modelo de hilo único (*Single-Threaded Model*) de JavaFX para alterar nodos de la escena gráfica. |
| **`interrupt()`** | Método de `Thread` | Activa la bandera de interrupción en el hilo objetivo. Si el hilo se encuentra bloqueado en `sleep()` o `wait()`, la JVM lo despierta inmediatamente lanzando `InterruptedException`. |
| **`isInterrupted()`** | Método de `Thread` | Consulta el valor de la bandera booleana de interrupción sin modificarla ni restablecerla. |
| **`setDaemon(true)`** | Método de `Thread` | Marca el hilo como demonio (*background worker*). Los hilos demonio son finalizados automáticamente por la JVM si todos los hilos de usuario principales (como el hilo de la UI) han terminado. |

---

## 5. Estructura del Proyecto

El proyecto está construido bajo una arquitectura modular y limpia, respetando el estándar Maven:

```text
PracticaHilosJavaFX/
│
├── .mvn/                                # Configuración de Maven Wrapper
├── docs/
│   └── img/                             # Evidencias visuales de ejecución
│       ├── monitor_inicial.png
│       ├── monitor_concurrente.png
│       ├── monitor_completado.png
│       ├── proceso_basico.png
│       └── bloqueo_ui.png
│
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java         # Descriptor modular Java 21
│       │   └── ni/edu/uam/practicahilos/
│       │       ├── Launcher.java        # Punto de entrada universal
│       │       ├── MainApp.java         # Inicializador JavaFX Stage y ciclo de vida
│       │       ├── ProcesadorController.java # Lógica de hilos y concurrencia
│       │       └── GeneradorCapturas.java    # Generador automatizado de capturas
│       │
│       └── resources/
│           └── ni/edu/uam/practicahilos/
│               ├── procesador-view.fxml # Vista FXML corporativa con pestañas
│               └── styles.css           # Hoja de estilos moderna y ergonómica
│
├── .gitignore
├── mvnw                                 # Wrapper para Linux / macOS
├── mvnw.cmd                             # Wrapper para Windows
├── pom.xml                              # Gestión de dependencias y plugins
└── README.md                            # Documentación integral de la práctica
```

---

## 6. Implementación de la Guía (Pasos 1 a 5)

En la pestaña **"Proceso Básico (Pasos 1-5)"** se encuentra la implementación directa del ejercicio guiado solicitado:

* **Componentes utilizados:** `lblEstado`, `barraProgreso`, `lblPorcentaje`, `btnIniciar`, `btnDetener`, `btnReiniciar`.
* **Procesamiento:** Itera de 1 a 100 registros con un retardo de `100 ms` por iteración (~10 segundos en total).
* **Actualización segura:** Se utiliza `Platform.runLater()` en cada iteración para reflejar el progreso de la barra y la etiqueta porcentual.
* **Detención controlada:** Si el usuario presiona el botón "Detener", se ejecuta `hiloProceso.interrupt()`, el bloque atrapa `InterruptedException`, restablece la bandera con `Thread.currentThread().interrupt()` y actualiza el estado a `"Proceso detenido"`.

![Proceso Básico](docs/img/proceso_basico.png)

---

## 7. Análisis de Bloqueo de la Interfaz (Paso 6)

### Código del Experimento
```java
@FXML
private void procesoBloqueante() {
    lblEstadoBloqueo.setText("Procesando (UI Bloqueada durante 5s)...");

    try {
        // ¡PELIGRO! Esto duerme directamente el JavaFX Application Thread
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

    lblEstadoBloqueo.setText("Finalizado (UI Desbloqueada)");
}
```

### Evidencia Visual del Experimento
![Análisis de Bloqueo UI](docs/img/bloqueo_ui.png)

### Pregunta de Análisis
> **¿Por qué no debemos ejecutar procesos prolongados directamente dentro de los eventos de JavaFX?**

### Explicación Teórica y Técnica
JavaFX, al igual que la gran mayoría de frameworks gráficos modernos (como Swing, Android UI, WPF o el navegador web), opera bajo un modelo de concurrencia denominado **Single-Threaded Model** (Modelo de Hilo Único). 

1. **El rol del JavaFX Application Thread:** Este único hilo es el responsable absoluto de:
   * Atender la cola de eventos del sistema operativo (movimientos del ratón, clics, pulsaciones de teclas).
   * Redibujar y renderizar los nodos del árbol de la escena (*Scene Graph*) a una tasa de 60 fotogramas por segundo (FPS).
   * Gestionar transiciones y animaciones.
2. **Consecuencias de ejecutar una tarea pesada o bloqueante:** Si se ejecuta un método prolongado (como consultas a bases de datos, descarga de archivos o `Thread.sleep()`) dentro de un manejador de eventos en este hilo, **la cola de eventos queda completamente paralizada**. El sistema operativo detecta que la ventana no consume mensajes y muestra el mensaje del sistema *"La aplicación no responde"* (o puntero de carga infinito), la ventana no se puede arrastrar, minimizar ni cerrar, y no se actualizan los cambios visuales.
3. **Solución arquitectónica:** La regla de oro en JavaFX dicta:
   * **Cualquier cómputo pesado, I/O o espera debe ejecutarse en un hilo secundario en segundo plano (`Thread` + `Runnable`).**
   * **Cualquier actualización a los componentes visuales debe enviarse a través de `Platform.runLater()`.**

---

## 8. Reto Final: Monitor de Procesos Concurrentes Empresarial (Paso 11)

Para atender las necesidades de **NicaData Solutions S.A.**, se desarrolló una solución multi-hilo completa que permite la ejecución paralela e independiente de dos tareas críticas con características diferenciadas.

### Especificaciones Técnicas del Reto

| Proceso | Meta de Registros | Latencia por Registro | Tiempo Total Estimado | Color / Estilo |
| :--- | :---: | :---: | :---: | :--- |
| **Procesamiento de Clientes** | 100 registros | 80 ms | ~8.0 segundos | Azul corporativo (`#2563eb`) |
| **Procesamiento de Productos** | 50 registros | 140 ms | ~7.0 segundos | Verde esmeralda (`#10b981`) |

### Requerimientos Cumplidos
1. **Creación con `Thread` y `Runnable`:** Cada proceso instancia su propio `Thread` asignando nombres descriptivos (`"Hilo-Clientes"`, `"Hilo-Productos"`) y configurados como demonios (`setDaemon(true)`).
2. **Concurrencia real:** Los hilos corren de manera verdaderamente paralela en núcleos de CPU independientes.
3. **Controles individuales e integrados:**
   * Cada proceso dispone de sus propios botones: **Iniciar**, **Detener** y **Reiniciar**.
   * Se incluye una botonera global: **▶ Iniciar Ambos Procesos**, **⏹ Detener Todos** y **↺ Reiniciar Todos**.
4. **Prevención de doble ejecución:** Se valida el estado `isAlive()` antes de iniciar cada hilo; si ya está corriendo, la petición se ignora y los botones de inicio se inhabilitan.
5. **Cierre seguro:** Se capturó el evento `setOnCloseRequest` del `Stage` para garantizar la interrupción limpia de todos los hilos activos al cerrar la ventana.

### Evidencias de Funcionamiento

#### 1. Estado Inicial del Monitor
![Monitor Inicial](docs/img/monitor_inicial.png)

#### 2. Ejecución Concurrente en Progreso
*(Clientes al 65% y Productos al 40% demostrando ritmos de procesamiento independientes sin interferencia entre ellos ni bloqueo en la UI)*
![Monitor Concurrente](docs/img/monitor_concurrente.png)

#### 3. Procesamiento Completado al 100%
![Monitor Completado](docs/img/monitor_completado.png)

---

## 9. Evaluación Formativa y Cumplimiento de Criterios

| Criterio de Evaluación | Evidencia en el Proyecto | Estado |
| :--- | :--- | :---: |
| **Creación de hilos** | Uso explícito de clases `Thread` y expresiones lambda `Runnable` con nombres descriptivos y modo demonio. | **Cumplido** |
| **Concurrencia** | Ejecución de tareas en segundo plano permitiendo mover, redimensionar y operar la ventana sin congelamientos. | **Cumplido** |
| **Interfaz gráfica** | Modificación de propiedades visuales delegada de forma estricta y segura a `Platform.runLater()`. | **Cumplido** |
| **Control de ejecución** | Métodos `iniciar`, `detener` con `interrupt()` y `reiniciar` con control de botones `disable`. | **Cumplido** |
| **Reto integrador** | Monitor empresarial de dos procesos asimétricos con barras individuales, controles por lote y controles globales. | **Cumplido** |

---

## 10. Instrucciones de Compilación y Ejecución

### Requisitos Previos
* **Java Development Kit (JDK):** Versión 21 o superior.
* **Apache Maven:** Versión 3.8+ (o utilizar el wrapper incluido `mvnw.cmd` / `./mvnw`).

### Opción A: Ejecución mediante Maven (Línea de Comandos)

1. Clonar o abrir el repositorio en una terminal:
   ```bash
   git clone https://github.com/g-nzaan/PracticaHilosJavaFX.git
   cd PracticaHilosJavaFX
   ```

2. Compilar el proyecto:
   ```bash
   mvn clean compile
   ```

3. Ejecutar la aplicación JavaFX:
   ```bash
   mvn javafx:run
   ```

*(En Windows también puede utilizar `.\mvnw.cmd javafx:run`)*

### Opción B: Ejecución en IntelliJ IDEA

1. Abrir **IntelliJ IDEA**.
2. Seleccionar **File → Open** y elegir el directorio raíz del proyecto (`PracticaHilosJavaFX`).
3. IntelliJ detectará automáticamente el archivo `pom.xml`.
4. Navegar a `src/main/java/ni/edu/uam/practicahilos/MainApp.java` o `Launcher.java`.
5. Hacer clic derecho y seleccionar **Run 'MainApp.main()'**.

---

**Desarrollado para:** Programación de Aplicaciones de Escritorio — UAM  
**Autor:** Práctica Individual — Sesión 26
