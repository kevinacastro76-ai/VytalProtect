# VytalProtect

Plugin de protecciones para Minecraft **Spigot/Paper 1.16.5+**, con bloques de protección, menú de administración, miembros, roles, expulsiones y hologramas.

## Requisitos de compilación
- JDK 21
- Gradle 9.1.0 (el workflow de GitHub Actions lo configura automáticamente)

El proyecto se compila usando Java 21, pero genera bytecode Java 8 y usa la API de Spigot 1.16.5 para mantener compatibilidad con servidores antiguos. El servidor necesita una versión de Java compatible con su versión de Minecraft.

## Compilar
```bash
gradle build
```
El archivo se genera en `build/libs/VytalProtect-1.0.0.jar`.

## Instalar
1. Sube el JAR a la carpeta `plugins/`.
2. Reinicia el servidor.
3. Edita `plugins/VytalProtect/config.yml`, `messages.yml` y `holograms.yml`.
4. Usa `/vytalprotect give <jugador> [cantidad]` para darte bloques.
5. Coloca el bloque configurado para crear una protección. Usa `/vp` para abrir el menú de la protección donde estás.

## Comandos
- `/vp` — abre el menú de la protección donde estás.
- `/vp info` — muestra información de la protección.
- `/vp trust <jugador>` — añade un miembro.
- `/vp untrust <jugador>` — elimina un miembro.
- `/vp give <jugador> [cantidad]` — entrega bloques (permiso `vytalprotect.admin`).
- `/vp reload` — recarga YAML (permiso `vytalprotect.admin`).

## Características
- Protección cúbica centrada en el bloque especial.
- Impide que se creen protecciones solapadas.
- Dueño, miembros, roles y lista de jugadores bloqueados por protección.
- Menú para permisos, añadir/quitar miembros, ascender/degradar y bloquear/desbloquear jugadores.
- Holograma propio con dueño, miembros y tamaño configurable.
- Mensajes YAML, incluidos entrada y salida.
- Datos de protecciones en `data.yml`.

## Notas
- Esta primera versión es una base funcional extensible. La protección cubre bloques e interacciones habituales, pero plugins de terceros pueden tener sus propios sistemas de interacción. Prueba en un servidor de staging antes de producción.
- Para añadir a alguien desde el menú, selecciona “Añadir miembro” y escribe su nombre en el chat. Escribe `cancelar` para abortar.
