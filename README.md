# VytalProtect

Plugin Java para Paper/Spigot, organizado como proyecto Gradle y preparado para GitHub Actions.

## Estado de esta entrega

Esta es una **primera base funcional de desarrollo**, no una versión final certificada. Incluye creación de protecciones por bloque, límites, prevención básica de solapamiento, guardado YAML, comprobaciones iniciales de construcción/interacción, mensajes de entrada/salida, GUI básica, protecciones ante explosiones/pistones/líquidos y hologramas con ArmorStand. La compilación no se pudo ejecutar en el entorno que generó este ZIP porque no tiene Gradle instalado; GitHub Actions ejecutará la compilación al subir el proyecto.

**Pendiente antes de considerarlo listo para producción:** administración completa de miembros/roles/bloqueos desde GUI y chat, sistema de confirmación para eliminar, control más exhaustivo de eventos (contenedores específicos, daño a entidades, fuego, dispensadores y otros mecanismos), identificación robusta de la protección activa en todos los menús, migraciones y pruebas de integración por versión. No se afirma que todas las funciones solicitadas estén ya implementadas ni que todas las versiones estén probadas.

## Requisitos

- JDK 21 para compilar el proyecto.
- Gradle instalado localmente, o usar GitHub Actions.
- API de compilación Spigot 1.16.5.
- Para ejecutar el plugin, el servidor debe usar una versión de Java compatible con su versión de Minecraft. El JAR se compila con `--release 8` para generar bytecode Java 8, pero esto **no garantiza por sí solo** compatibilidad con todas las APIs de versiones posteriores.

## Compilar

```bash
gradle build
```

El JAR se genera en `build/libs/VytalProtect-1.0.0.jar`.

## Instalar

1. Compila el proyecto o descarga el artefacto de GitHub Actions.
2. Copia el JAR en la carpeta `plugins/` del servidor.
3. Inicia el servidor y revisa la consola.
4. Ajusta los YAML generados en `plugins/VytalProtect/`.
5. Haz una copia de seguridad antes de actualizar o editar `protections.yml`.

## Comandos

- `/vp` muestra ayuda.
- `/vp menu` abre el menú de la protección actual.
- `/vp info` muestra información básica.
- `/vp reload` recarga archivos de configuración.
- `/vp admin` muestra un resumen administrativo.
- `/vp admin delete <id>` elimina una protección por ID.

## Permisos

- `vytalprotect.use`
- `vytalprotect.admin`
- `vytalprotect.reload`
- `vytalprotect.bypass`
- `vytalprotect.admin.delete`

## Pruebas recomendadas antes de producción

- Compilar en GitHub Actions.
- Arrancar en una instancia de pruebas de Spigot/Paper 1.16.5 y una instancia moderna compatible.
- Probar creación, reinicio, límites, solapamientos y recuperación de `protections.yml`.
- Probar cada evento de protección y verificar que acciones fuera de terrenos no se cancelen.
- Probar GUI con miembros, propietarios y administradores.
- Inspeccionar logs, duplicación de hologramas y consumo con muchos terrenos.

## GitHub Actions

El workflow `.github/workflows/build.yml` se ejecuta en cada `push` y también manualmente desde la pestaña **Actions**. Publica el JAR como artefacto si la compilación termina correctamente.
