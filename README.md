# VytalProtect

Plugin Java para servidores Spigot/Paper: protecciones mediante bloques especiales, menú de administración, rangos internos y hologramas propios.

## Requisitos de desarrollo
- JDK 8 o superior para compilar este proyecto con bytecode Java 8.
- Maven 3.8+.
- Conexión a internet durante la primera compilación para descargar Spigot API 1.8.8.

## Compilar
Desde esta carpeta:

```bash
mvn clean package
```

El archivo resultante será `target/VytalProtect.jar`. Copia el JAR a `plugins/` y reinicia el servidor.

## Comandos
- `/vp` — ayuda.
- `/vp give <jugador> [cantidad]` — entregar bloques de protección.
- `/vp menu` — abrir el menú de la protección donde estás (si eres propietario o miembro).
- `/vp info` — mostrar información de la protección donde estás.
- `/vp reload` — recargar configuración.

Permisos Bukkit:
- `vytalprotect.admin` — administración completa y bypass configurado.
- `vytalprotect.give` — entregar bloques de protección.

## Archivos
- `config.yml`: radios, bloque, límites y hologramas.
- `messages.yml`: mensajes editables.
- `language.yml`: nombres traducibles de rangos y permisos.
- `permissions.yml`: permisos por rango interno.
- `menus.yml`: ranuras y configuración del menú.
- `holograms.yml`: líneas del holograma.
- `events.yml`: mensajes de entrada y salida.
- `protections.yml`: se genera automáticamente con los datos.

## Estado de esta entrega
Es una **primera base funcional de desarrollo**, no una versión de producción certificada. Incluye creación de zonas al colocar el ítem, rechazo de solapamientos, persistencia YAML, comprobaciones básicas de protección, menú inicial, gestión por chat iniciada desde el menú, controles de permisos y hologramas ArmorStand. Antes de usarla en producción, hay que compilarla y probarla en las versiones objetivo, y ampliar las pruebas de exploits y compatibilidad.

## Alcance pendiente antes de producción
- Pruebas automatizadas y manuales para cada versión soportada (1.8.8, 1.12.2, 1.16.5, 1.20.6 y versiones 1.21.x).
- Cobertura adicional de eventos (pistones, líquidos, fuego, entidades, dispensadores, redstone, portales y mecánicas específicas de cada versión).
- Migración a una base de datos SQLite real; la primera base usa `protections.yml` para mantener la instalación sencilla.
- Paginación de miembros y listas de baneados para terrenos grandes.
- Gestión de nombres sin conexión y UUIDs con verificación de cuentas premium/no premium.
- Pruebas de limpieza y recuperación de hologramas tras reinicios.

La compatibilidad desde 1.8 es un objetivo de diseño; no se debe anunciar como garantizada hasta completar la matriz de pruebas.
