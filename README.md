# Plantas Medicinales

Una aplicación móvil Android moderna diseñada para la consulta y gestión de información sobre plantas medicinales, desarrollada por el **IIAP (Instituto de Investigaciones de la Amazonía Peruana)**.

## 🌿 Características Principales

- **Exploración de Plantas**: Visualiza un catálogo detallado de plantas medicinales.
- **Búsqueda Avanzada**: Encuentra plantas específicas por nombre o propiedades.
- **Detalles Completos**: Información técnica y tradicional sobre cada especie.
- **Mi Botica**: Sección personalizada para que los usuarios gestionen su propia selección de plantas.
- **Gestión de Usuarios**: Registro e inicio de sesión seguro.
- **Interfaz Moderna**: Desarrollada íntegramente con Jetpack Compose y siguiendo las guías de Material Design 3.

## 🚀 Tecnologías Utilizadas

- **Lenguaje**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Navegación**: Compose Navigation
- **Backend/Base de Datos**: 
  - **Firebase Auth**: Para la autenticación de usuarios.
  - **Firebase Firestore**: Base de Datos NoSQL en tiempo real para el almacenamiento de información de las plantas.
- **Diseño**: Material 3

## 🛠️ Estructura del Proyecto

La aplicación sigue una organización clara por capas y módulos:

- `ui/`: Contiene los componentes de la interfaz de usuario.
  - `home/`: Pantalla principal.
  - `search/`: Funcionalidad de búsqueda.
  - `detail/`: Información detallada de plantas.
  - `login/`: Autenticación.
  - `mybotica/`: Espacio personal del usuario.
  - `theme/`: Definiciones de colores, tipografía y formas (Material 3).
- `data/`: Modelos de datos y repositorios.
- `network/`: Configuración y servicios de red/Firebase.

## ⚙️ Configuración y Ejecución

Para ejecutar este proyecto localmente, sigue estos pasos:

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/Mishell-260507/Plantas-Medicinales.git
   ```
2. **Configuración de Firebase**:
   - Crea un proyecto en la [Consola de Firebase](https://console.firebase.google.com/).
   - Añade una aplicación Android con el nombre de paquete `com.iiap.plantasmedicinales`.
   - Descarga el archivo `google-services.json` y colócalo en el directorio `app/`.
   - Habilita **Email/Password Authentication** y **Cloud Firestore**.
3. **Abrir en Android Studio**:
   - Abre el proyecto y espera a que Gradle sincronice las dependencias.
4. **Ejecutar**:
   - Selecciona un emulador o dispositivo físico y presiona "Run".

Desarrollado para la preservación y difusión del conocimiento.
