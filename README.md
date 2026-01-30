PoliEnergía 🔋☀️🌬️

PoliEnergía es una aplicación educativa de Android diseñada para enseñar conceptos de energías renovables (solar y eólica) y eficiencia energética. Este proyecto se desarrolló como parte del programa de vinculación con la sociedad: "Ingeniería para el desarrollo de prototipos didácticos para formación educativa".

📋 Descripción del Proyecto
La aplicación permite a los estudiantes simular el consumo de energía en el hogar y observar cómo las fuentes renovables interactúan con la carga de una batería. Incluye:

Simuladores Interactivos: Ajuste de parámetros solares y eólicos en tiempo real.

Glosario Técnico: Definiciones claras de términos energéticos.

Persistencia de Datos: Guardado automático del estado de la simulación mediante SharedPreferences.

🛠️ Especificaciones Técnicas
Lenguaje: Kotlin.

SDK Mínimo: API 24 (Android 7.0).

SDK Objetivo: API 36.

Arquitectura: Basada en lógica desacoplada de la interfaz para facilitar el mantenimiento de recursos locales (strings.xml).

🏗️ Arquitectura del Software
El sistema sigue un flujo de datos horizontal para garantizar que la interfaz de usuario se mantenga sincronizada con la lógica de negocio y la persistencia:

🚀 Instalación y Mantenimiento
Para clonar y ejecutar esta aplicación localmente:

Clonar el repositorio:

Bash
git clone https://github.com/franciscopad03/PoliEnergia.git
Abrir en Android Studio: Asegúrate de tener instalada la versión Jellyfish o superior.

Generar APK: Dirígete a Build > Build Bundle(s) / APK(s) > Build APK(s).

Edición de Contenidos
Para cambiar los textos o términos del glosario sin tocar el código fuente, edita el archivo: app/src/main/res/values/strings.xml.

🤝 Créditos
Autor: Francisco Javier Padilla Almeida.

Tutor de Vinculación: Ing. Galo Durazno.

Institución: Escuela Superior Politécnica del Litoral (ESPOL).
