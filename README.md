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

## 2. Naturaleza del Proyecto: 100% Java Puro

> [!IMPORTANT]
> **Desarrollo 100% Java:**  
> La aplicación está desarrollada enteramente en **Java (JDK 21) y JavaFX nativo**. No contiene ninguna línea de código en JavaScript, ni WebViews, ni frameworks web. Todo el control concurrente se gestiona directamente en la JVM mediante `java.lang.Thread`, `java.lang.Runnable` y la cola de eventos de `javafx.application.Platform.runLater()`.
>
> Para mayor flexibilidad y rigor académico, el proyecto incluye dos modalidades:
> 1. **Modalidad FXML + Controlador:** (`MainApp.java` + `ProcesadorController.java` + `procesador-view.fxml`), siguiendo exactamente los pasos 1 a 5 de la guía.
> 2. **Modalidad 100% Código Java Imperativo:** (`MainAppPuroJava.java`), donde la interfaz se instancia y diseña completamente mediante clases Java puras sin archivos FXML externos.

---

## 3. Funcionalidad Clave: Pausa, Detención y Reanudación de Progreso

A diferencia de implementaciones ingenuas donde detener un hilo destruye el avance obligando a reiniciar desde 0:
* **Persistencia de estado:** Cada proceso mantiene variables de control persistentes (`progresoActual`, `progresoClientes`, `progresoProductos`).
* **Detención controlada:** Al presionar **Detener**, se interrumpe el hilo (`interrupt()`) y se conserva el último índice completado en memoria y en pantalla.
* **Reanudación transparente:** Al presionar **Continuar**, el nuevo hilo arranca automáticamente a partir de `progreso + 1` hasta completar la meta total.
* **Reinicio explícito:** El avance solo se restablece a 0% cuando el usuario presiona voluntariamente el botón **Reiniciar**, o tras finalizar exitosamente el 100%.

---

## 4. Conceptos Fundamentales

| Concepto | Tipo / Ámbito | Rol y Descripción Técnica |
| :--- | :--- | :--- |
| **`Thread`** | Clase (`java.lang.Thread`) | Representa una unidad de ejecución concurrente gestionada por la JVM y mapeada a hilos nativos del sistema operativo. |
| **`Runnable`** | Interfaz funcional | Define el contrato del trabajo o tarea ejecutable a través de su método abstracto `run()`. |
| **`start()`** | Método de `Thread` | Solicita al planificador de la JVM la creación y arranque de un nuevo hilo nativo. **Importante:** Llamar directamente a `run()` ejecutaría la tarea secuencialmente en el hilo actual sin crear concurrencia. |
| **`sleep(ms)`** | Método estático de `Thread` | Suspende la ejecución del hilo invocador durante un lapso de tiempo especificado. Permite simular latencias de cómputo; es sensible a señales de interrupción (`InterruptedException`). |
| **`Platform.runLater(Runnable)`** | Método estático de JavaFX | Encola un bloque de código para ser despachado asíncronamente en el **JavaFX Application Thread**. Es la única vía autorizada por el modelo de hilo único (*Single-Threaded Model*) de JavaFX para alterar nodos de la escena gráfica. |
| **`interrupt()`** | Método de `Thread` | Activa la bandera de interrupción en el hilo objetivo. Si el hilo se encuentra bloqueado en `sleep()` o `wait()`, la JVM lo despierta inmediatamente lanzando `InterruptedException`. |
| **`isInterrupted()`** | Método de `Thread` | Consulta el valor de la bandera booleana de interrupción sin modificarla ni restablecerla. |
| **`setDaemon(true)`** | Método de `Thread` | Configura el hilo como demonio (*background worker*). Los hilos demonio son finalizados automáticamente por la JVM si todos los hilos de usuario principales (como el hilo de la UI) han terminado. |

---

## 5. Estructura del Proyecto

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
│       │       ├── Launcher.java        # Selector de inicio universal
│       │       ├── MainApp.java         # Versión con FXML (Paso 1-5 y Reto)
│       │       ├── MainAppPuroJava.java # Versión 100% código Java sin FXML
│       │       ├── ProcesadorController.java # Lógica de hilos, concurrencia y reanudación
│       │       └── GeneradorCapturas.java    # Generador automatizado de capturas
│       │
│       └── resources/
│           └── ni/edu/uam/practicahilos/
│               ├── procesador-view.fxml # Vista FXML corporativa con pestañas
│               └── styles.css           # Hoja de estilos moderna y ergonómica
│
├── .gitignore
├── mvnw / mvnw.cmd                      # Wrappers Maven multiplataforma
├── pom.xml                              # Gestión de dependencias y plugins
└── README.md                            # Documentación integral de la práctica
```

---

## 6. Implementación de la Guía (Pasos 1 a 5)

En la pestaña **"Proceso Básico (Pasos 1-5)"** se encuentra la implementación directa del ejercicio guiado solicitado:

* **Componentes utilizados:** `lblEstado`, `barraProgreso`, `lblPorcentaje`, `btnIniciar`, `btnDetener`, `btnReiniciar`.
* **Procesamiento:** Itera de 1 a 100 registros con un retardo de `100 ms` por iteración (~10 segundos en total).
* **Persistencia al pausar:** Si el proceso va por el 45% y se hace clic en **Detener**, la barra permanece en 45%, la etiqueta indica `"Proceso detenido en 45%. Progreso conservado."` y el botón cambia a `"Continuar (45%)"`. Al hacer clic nuevamente, retoma de 46 a 100.
* **Actualización segura:** Se utiliza `Platform.runLater()` en cada iteración para reflejar el progreso de la barra y la etiqueta porcentual.

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
JavaFX opera bajo un modelo de concurrencia denominado **Single-Threaded Model** (Modelo de Hilo Único). 

1. **El rol del JavaFX Application Thread:** Este único hilo es el responsable de atender la cola de eventos del sistema operativo (mouse, teclado), despachar listeners y redibujar los nodos del árbol de la escena (*Scene Graph*) a 60 FPS.
2. **Consecuencias del bloqueo:** Si se ejecuta un método prolongado (como consultas a base de datos o `Thread.sleep()`) en este hilo, **la cola de eventos se paraliza por completo**. La ventana deja de redibujarse, el sistema operativo reporta que la aplicación no responde y el usuario no puede interactuar con ningún control.
3. **Regla de oro:** 
   * Todo cómputo prolongado debe ejecutarse en un **hilo secundario en segundo plano (`Thread` + `Runnable`)**.
   * Cualquier actualización sobre los controles gráficos debe enviarse mediante **`Platform.runLater()`**.

---

## 8. Reto Final: Monitor de Procesos Concurrentes Empresarial (Paso 11)

Para atender a **NicaData Solutions S.A.**, se implementó el monitor concurrente con dos hilos independientes de características asimétricas:

| Proceso | Meta de Registros | Retardo por Registro | Duración Aprox. | Color / Estilo |
| :--- | :---: | :---: | :---: | :--- |
| **Procesamiento de Clientes** | 100 registros | 80 ms | ~8.0 segundos | Azul corporativo (`#2563eb`) |
| **Procesamiento de Productos** | 50 registros | 140 ms | ~7.0 segundos | Verde esmeralda (`#10b981`) |

### Características Implementadas
1. **Concurrencia real:** Dos objetos `Thread` corriendo en paralelo sin interferirse.
2. **Preservación de avance:** Pausar clientes en el registro 65 o productos en el 20 no reinicia el contador. Al continuar, siguen exactamente donde quedaron.
3. **Controles independientes y colectivos:** Botoneras individuales por lote y botonera global para iniciar, pausar o reiniciar ambos a la vez.
4. **Prevención de doble ejecución:** Validación con `isAlive()` antes de iniciar.
5. **Cierre seguro:** `setOnCloseRequest` interrumpe todos los hilos vivos al salir.

### Evidencias de Funcionamiento

#### 1. Estado Inicial del Monitor
![Monitor Inicial](docs/img/monitor_inicial.png)

#### 2. Ejecución Concurrente en Progreso (Clientes 65%, Productos 40%)
![Monitor Concurrente](docs/img/monitor_concurrente.png)

#### 3. Procesamiento Completado al 100%
![Monitor Completado](docs/img/monitor_completado.png)

---

## 9. Instrucciones de Ejecución

### Opción 1: Ejecutar Versión Estándar (FXML + Controlador)
```bash
mvn javafx:run
```
*(O en Windows: `.\mvnw.cmd javafx:run`)*

### Opción 2: Ejecutar Versión 100% Java Puro (Sin FXML)
```bash
mvn javafx:run -Djavafx.mainClass="ni.edu.uam.practicahilos/ni.edu.uam.practicahilos.MainAppPuroJava"
```
O ejecutando el método `main()` de `MainAppPuroJava.java` directamente desde IntelliJ IDEA.

---

**Desarrollado para:** Programación de Aplicaciones de Escritorio — UAM  
**Autor:** Práctica Individual — Sesión 26
