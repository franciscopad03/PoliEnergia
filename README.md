# PoliEnergía 🔋☀️🌬️

**PoliEnergía** es una aplicación educativa de Android diseñada para enseñar conceptos de energías renovables (solar y eólica) y eficiencia energética. Este proyecto se desarrolló como parte del programa de vinculación con la sociedad: *"Ingeniería para el desarrollo de prototipos didácticos para formación educativa"*.

## 📋 Descripción del Proyecto
La aplicación permite a los estudiantes simular el consumo de energía en el hogar y observar cómo las fuentes renovables interactúan con la carga de una batería.

### Características Principales:
* **Simuladores Interactivos:** Ajuste de parámetros solares y eólicos en tiempo real.
* **Glosario Técnico:** Definiciones claras de términos energéticos fundamentales.
* **Persistencia de Datos:** Guardado automático del estado de la simulación mediante `SharedPreferences`.

## 🛠️ Especificaciones Técnicas
* **Lenguaje:** Kotlin.
* **SDK Mínimo:** API 24 (Android 7.0).
* **SDK Objetivo:** API 36.
* **Arquitectura:** Basada en lógica desacoplada de la interfaz para facilitar el mantenimiento de recursos locales (`strings.xml`).

## 🏗️ Arquitectura del Software
El sistema sigue un flujo de datos organizado para garantizar que la interfaz de usuario se mantenga sincronizada con la lógica de negocio y la persistencia de datos.

## 🚀 Instalación y Mantenimiento
Para clonar y ejecutar esta aplicación localmente:

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/franciscopad03/PoliEnergia.git](https://github.com/franciscopad03/PoliEnergia.git)

2. **Abrir en Android Studio:** Asegúrate de tener instalada la versión **Jellyfish** o superior.
3. **Generar APK:** Dirígete a `Build` > `Build Bundle(s) / APK(s)` > `Build APK(s)`.

## 📝 Edición de Contenidos
Para cambiar los textos o términos del glosario sin modificar el código fuente, simplemente edita el archivo de recursos:
`app/src/main/res/values/strings.xml`

## 🤝 Créditos y Autores
* **Autor Principal:** Francisco Javier Padilla Almeida.
* **Colaborador:** Jorge Alberto Apolo Acosta.
* **Director de Vinculación:** Ing. Galo Durazno.
* **Institución:** Escuela Superior Politécnica del Litoral (**ESPOL**).
