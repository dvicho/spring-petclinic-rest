# REST Assured Testing — Owner Endpoints

Pruebas de integración para los endpoints de `/owners` usando [REST Assured](https://rest-assured.io/) con el módulo `spring-mock-mvc`.

## ✨ Estado del proyecto

- ✅ Tests escritos con anotaciones de Allure (Feature, Story, Description, Severity)
- ✅ Textos traducidos al español (Gestión de Propietarios, Listar, Crear, Actualizar, Eliminar)
- ✅ Maven Failsafe Plugin configurado para ejecutar tests `*IT.java`
- ✅ Allure Maven Plugin configurado para generar reportes visuales
- ✅ 12 tests de integración cubriendo CRUD operations en `/owners`

---

## 🚀 Comandos rápidos

```bash
# ⭐ OPCIÓN 1: Tests + Reporte Allure servido via HTTP (RECOMENDADO)
./mvnw clean verify && ./mvnw allure:report && ./mvnw exec:exec

# ⭐ OPCIÓN 2: Tests + Reportes completos (Maven Site + JaCoCo + Surefire)
./mvnw clean verify && ./mvnw site && open target/site/index.html

# Solo tests de integración REST-Assured
./mvnw clean verify -Dtest=OwnerRestControllerIT

# Solo tests unitarios (sin REST-Assured)
./mvnw clean test

# Solo generar reporte Allure y servir via HTTP
./mvnw allure:report && ./mvnw exec:exec

# Solo servir reporte Allure (si ya fue generado)
./mvnw exec:exec

# Solo generar sitio Maven (sin tests ni Allure)
./mvnw clean verify && ./mvnw site && open target/site/index.html
```

> **Nota sobre `./mvnw exec:exec`:** Levanta un servidor HTTP en `http://localhost:8080` que sirve el reporte Allure. Requiere Python 3 (disponible por defecto en macOS y Linux).

---

## Archivo de prueba

`src/test/java/org/springframework/samples/petclinic/rest/controller/OwnerRestControllerIT.java`

### Anotaciones de Allure

Cada método de prueba está decorado con anotaciones de Allure para mejorar la claridad y trazabilidad del reporte:

- **`@Feature("Owner Management")`** — Agrupa todas las pruebas bajo una característica principal
- **`@Story("List all owners")`** — Agrupa pruebas relacionadas (GET, POST, PUT, DELETE)
- **`@Description("...")`** — Describe en detalle qué valida cada test
- **`@Severity(SeverityLevel.CRITICAL/NORMAL)`** — Marca la criticidad del test:
  - `CRITICAL`: Funcionalidad core (happy path CRUD)
  - `NORMAL`: Casos edge/error handling
- **`@Step("...")`** — Documenta pasos dentro de un test (opcional)

Ejemplo:
```java
@Story("List all owners")
@Description("Retrieve all owners from the system. Should return at least 10 seed owners.")
@Severity(SeverityLevel.CRITICAL)
@Test
void listOwners_returnsAllSeedOwners() {
    given()
        .when()
        .get("/owners")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("$", hasSize(greaterThanOrEqualTo(10)));
}
```

> **Nota:** Previamente usábamos `.filter(new AllureRestAssured())` para capturar detalles HTTP, pero esto es incompatible con el módulo `spring-mock-mvc`. Allure + JUnit 5 captura automáticamente los resultados de los tests sin necesidad de este filtro.

| Test | Story | Endpoint | Qué valida |
|---|---|---|---|
| `listOwners_returnsAllSeedOwners` | Listar todos los propietarios | `GET /owners` | Retorna al menos 10 propietarios del seed |
| `listOwners_filteredByLastName_returnsMatchingOwner` | Listar filtrados por apellido | `GET /owners?lastName=Franklin` | Filtra correctamente por apellido |
| `listOwners_filteredByLastName_notFound` | Listar filtrados por apellido | `GET /owners?lastName=NonExistentOwnerXYZ` | 404 cuando no hay coincidencias |
| `getOwner_returnsOwner` | Obtener propietario por ID | `GET /owners/1` | Retorna detalles completos (id, firstName, lastName, address, etc.) |
| `getOwner_notFound` | Obtener propietario por ID | `GET /owners/9999` | 404 para ID inexistente |
| `addOwner_createsOwnerAndReturns201` | Crear nuevo propietario | `POST /owners` | Status 201, ID generado, detalles correctos |
| `addOwner_badRequest_whenMissingRequiredFields` | Crear nuevo propietario | `POST /owners` | 400 cuando faltan campos requeridos |
| `addOwner_badRequest_whenTelephoneContainsLetters` | Crear nuevo propietario | `POST /owners` | 400 cuando el teléfono no es numérico |
| `updateOwner_returns204` | Actualizar propietario existente | `PUT /owners/{id}` | Status 204 (actualización exitosa) |
| `updateOwner_notFound` | Actualizar propietario existente | `PUT /owners/9999` | 404 para ID inexistente |
| `deleteOwner_returns204AndOwnerIsGone` | Eliminar propietario | `DELETE /owners/{id}` | Status 204, verificación de ausencia con GET 404 |
| `deleteOwner_notFound` | Eliminar propietario | `DELETE /owners/9999` | 404 para ID inexistente |

## Notas de configuración

- **Módulo usado:** `io.rest-assured:spring-mock-mvc:6.0.0` — requerido para compatibilidad con Spring Framework 7 (Spring Boot 4.x). El módulo HTTP principal (`rest-assured`) tiene incompatibilidades con Groovy 3.x y Java 17+.
- **Seguridad:** Las pruebas corren con `petclinic.security.enable=false`. Esto deshabilita `@PreAuthorize` en los controladores, manteniendo el foco en la lógica de negocio. La seguridad está cubierta por las pruebas unitarias existentes con `@WithMockUser`.
- **Base de datos:** El perfil de test usa HSQLDB en memoria (`hsqldb,spring-data-jpa`) con datos de seed idénticos al perfil H2 de desarrollo.

---

## Configuración del pom.xml

Para ejecutar las pruebas de integración REST-Assured (`*IT.java`), se requieren dos plugins Maven:

### 1. Maven Surefire Plugin (pruebas unitarias)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <argLine>
            -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/1.9.21/aspectjweaver-1.9.21.jar"
        </argLine>
        <systemProperties>
            <allure.results.directory>${project.build.directory}/allure-results</allure.results.directory>
        </systemProperties>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.21</version>
        </dependency>
    </dependencies>
</plugin>
```

### 2. Maven Failsafe Plugin (pruebas de integración) — CRÍTICO ⚠️

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <argLine>
            -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/1.9.21/aspectjweaver-1.9.21.jar"
        </argLine>
        <systemProperties>
            <allure.results.directory>${project.build.directory}/allure-results</allure.results.directory>
        </systemProperties>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.21</version>
        </dependency>
    </dependencies>
</plugin>
```

**Sin el Failsafe Plugin, los tests `*IT.java` NO se ejecutarán** durante `mvn verify`. Este plugin es responsable de descubrir y ejecutar todas las pruebas que terminan en `IT.java` o `ITTest.java`.

### 3. Allure Maven Plugin (generación de reportes)

```xml
<plugin>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-maven</artifactId>
    <version>2.12.0</version>
    <configuration>
        <reportVersion>${allure.version}</reportVersion>
        <resultsDirectory>${project.build.directory}/allure-results</resultsDirectory>
        <reportDirectory>${project.build.directory}/allure-report</reportDirectory>
    </configuration>
</plugin>
```

---

## Correr las pruebas

### ⚠️ Importante: `mvn test` vs `mvn verify`

- **`mvn test`** — Ejecuta solo pruebas unitarias (`*Test.java`, `*Tests.java`) con Surefire
- **`mvn verify`** — Ejecuta pruebas unitarias + pruebas de integración (`*IT.java`) con Failsafe

Las pruebas REST-Assured están en `OwnerRestControllerIT.java`, por lo que **requieren `mvn verify`**.

### Comando recomendado: Tests + Reporte Allure (TODO EN UNO)

```bash
./mvnw clean verify && ./mvnw allure:report && allure open target/allure-report
```

Este comando:
1. Limpia compilaciones previas (`clean`)
2. Corre todas las pruebas: unitarias + integración (`verify`)
3. Genera el reporte HTML de Allure (`allure:report`)
4. Abre automáticamente el reporte en el navegador (`allure open`)

### Solo las pruebas de integración REST-Assured

```bash
./mvnw verify -Dtest=OwnerRestControllerIT
```

### Solo las pruebas unitarias (sin integración)

```bash
./mvnw test
```

### Todas las pruebas del proyecto (unitarias + integración)

```bash
./mvnw clean verify
```

### Con salida detallada (logs de Spring visibles)

```bash
./mvnw clean verify -Dlogging.level.root=DEBUG
```

### Modo silencioso (sin output verboso)

```bash
./mvnw clean verify -q
```

---

## Generar reportes

### Reporte Allure (REST Assured + JUnit 5)

Allure captura automáticamente todos los detalles de las peticiones HTTP (request, response, headers, body, status code, etc.) en un reporte visual interactivo.

#### Generar reporte Allure (Opción 1: Servidor HTTP Maven)

La forma correcta de ver el reporte Allure es **servir los archivos via HTTP**. Usa el plugin Maven configurado:

```bash
# Paso 1: Ejecutar tests
./mvnw clean verify

# Paso 2: Generar reporte Allure
./mvnw allure:report

# Paso 3: Servir el reporte via HTTP (http://localhost:8080)
./mvnw exec:exec
```

**O en un solo comando:**

```bash
./mvnw clean verify && ./mvnw allure:report && ./mvnw exec:exec
```

Cuando ejecutes `./mvnw exec:exec`, verás:

```
Serving HTTP on :: port 8080 (http://127.0.0.1:8080/) ...
```

Abre tu navegador en `http://localhost:8080` y el reporte se mostrará correctamente con todos los datos.

**Para detener el servidor:** Presiona `Ctrl+C` en la terminal.

> **Requisito:** Python 3 (disponible por defecto en macOS y Linux)

---

#### Generar reporte Allure (Opción 2: Maven Site)

También puedes generar un sitio Maven completo que integre **JaCoCo + Surefire** (Allure se genera por separado):

```bash
# Paso 1: Ejecutar tests y generar reporte Allure
./mvnw clean verify
./mvnw allure:report

# Paso 2: Generar sitio Maven (JaCoCo, Surefire, etc.)
./mvnw site

# Paso 3: Abrir el sitio principal
open target/site/index.html
```

**O en un solo comando (recomendado):**

```bash
./mvnw clean verify && ./mvnw allure:report && ./mvnw site && open target/site/index.html
```

**El sitio genera:**
- `target/site/index.html` — Portal principal con enlaces a todos los reportes
- `target/site/jacoco/index.html` — Cobertura de código
- `target/site/surefire-report.html` — Resultados de tests unitarios
- `target/allure-report/index.html` — Reporte Allure (generado por separado)

**Desde la página principal del sitio puedes:**
- Ver cobertura de código en "Project Reports → Code Coverage"
- Ver resultados de tests unitarios en "Project Reports → Surefire Report"
- Abrir `target/allure-report/index.html` en una pestaña separada para ver los tests REST-Assured

El reporte incluye:
- **Tree view**: Organización jerárquica por Feature → Story → Test
  - Feature: "Gestión de Propietarios"
  - Stories: "Listar todos los propietarios", "Obtener propietario por ID", etc.
- **Test details**: Descripción, severidad (CRITICAL/NORMAL), duración
- **Request/Response**: Detalles HTTP completos (método, path, headers, body, status code)
- **Timeline**: Gráfico de duración de cada test
- **History**: Comparación entre ejecuciones previas
- **Categories**: Tests exitosos vs fallidos
- **Severity filter**: Filtrar por criticidad (CRITICAL, NORMAL)
- **Duración total**: Tiempo total de ejecución

> Los resultados sin procesar se guardan en `target/allure-results/` (`.json` files). Allure los procesa para generar el reporte HTML.

#### Interpretar el reporte de Allure

---

#### Comparación: Opción 1 vs Opción 2

| Característica | Opción 1: Servidor HTTP Maven | Opción 2: Maven Site Completo |
|---|---|---|
| **Comando** | `./mvnw clean verify && ./mvnw allure:report && ./mvnw exec:exec` | `./mvnw clean verify && ./mvnw allure:report && ./mvnw site && open target/site/index.html` |
| **URL** | `http://localhost:8080` | Múltiples reportes en `target/site/` |
| **Tiempo total** | ~25 segundos | ~60 segundos (incluye más reportes) |
| **Reporte Allure** | ✅ Sí (con datos cargados correctamente) | ✅ Sí (en separado) |
| **Cobertura JaCoCo** | ❌ No | ✅ Sí |
| **Resultados Surefire** | ❌ No | ✅ Sí |
| **Portal centralizado** | ❌ No (solo Allure) | ✅ Sí (`http://localhost:8080/site/`) |
| **Datos visibles** | ✅ Sí (HTTP) | ✅ Sí |
| **Mejor para** | Desarrollo rápido, ver solo REST-Assured tests | CI/CD, auditoría completa |
| **Requiere** | Python 3 (preinstalado) | Solo Maven |

**Recomendación:**
- 👨‍💻 **Durante desarrollo:** Opción 1 (más rápido, focus en tests REST-Assured)
- 📊 **Para CI/CD o auditoría:** Opción 2 (vista completa del proyecto con cobertura)

---

#### ℹ️ Por qué el reporte Allure necesita HTTP

Cuando abres `target/allure-report/index.html` directamente en el navegador (protocolo `file://`), el navegador **bloquea los archivos JavaScript y CSS** por restricciones de seguridad. Por eso los datos no se cargan.

**Solución:** Servir los archivos a través de HTTP. El `pom.xml` incluye `exec-maven-plugin` configurado para servir el reporte usando Python:

```bash
./mvnw exec:exec
```

Este comando levanta un servidor HTTP en `http://localhost:8080` que sirve los archivos correctamente.

---

#### 📝 Resumen de opciones

**Opción 1: Rápido (solo Allure) — RECOMENDADO para desarrollo**
```bash
./mvnw clean verify && ./mvnw allure:report && ./mvnw exec:exec
```
- Levanta servidor en `http://localhost:8080`
- ~25 segundos de ejecución
- Datos se cargan correctamente

**Opción 2: Completo (Allure + JaCoCo + Surefire) — RECOMENDADO para CI/CD**
```bash
./mvnw clean verify && ./mvnw allure:report && ./mvnw site && open target/site/index.html
```
- Portal centralizado con múltiples reportes
- ~60 segundos de ejecución
- Requiere solo Maven (sin Python)

---

#### Interpretar el reporte de Allure

El reporte está organizado jerárquicamente con las anotaciones de Allure:

1. **Feature** → Expande "Gestión de Propietarios" (Feature principal)
2. **Stories** → Cada Story agrupa pruebas relacionadas:
   - "Listar todos los propietarios" (2 tests)
   - "Listar propietarios filtrados por apellido" (2 tests)
   - "Obtener propietario por ID" (2 tests)
   - "Crear nuevo propietario" (3 tests: success + validaciones)
   - "Actualizar propietario existente" (2 tests)
   - "Eliminar propietario" (2 tests)
3. **Test Details** → Al hacer clic en un test, ves:
   - Descripción (e.g., "Obtener todos los propietarios del sistema. Debe retornar al menos 10 propietarios de inicio.")
   - Severidad: `CRITICAL` (funcionalidad core) o `NORMAL` (casos edge/validaciones)
   - Detalles HTTP completos:
     - Request: método, path, headers, body
     - Response: status code, headers, body
   - Timeline: duración exacta del test

### Reporte HTML de resultados (Surefire)

Primero correr las pruebas, luego generar el reporte:

```bash
./mvnw test
./mvnw surefire-report:report-only
```

Salida: `target/reports/surefire.html`

> Para abrirlo directamente en macOS: `open target/reports/surefire.html`

Los reportes XML por clase están en `target/surefire-reports/TEST-*.xml` y son consumibles por herramientas CI/CD (Jenkins, GitHub Actions).

### Reporte de cobertura de código (JaCoCo)

```bash
./mvnw clean test jacoco:report
```

Salida: `target/site/jacoco/index.html`

> Para abrirlo directamente en macOS: `open target/site/jacoco/index.html`

El reporte XML para SonarCloud se genera automáticamente en `target/site/jacoco/jacoco.xml`.

Umbrales configurados en `pom.xml`:
- Líneas cubiertas: mínimo **85%**
- Ramas cubiertas: mínimo **66%**

---

## Troubleshooting

### ❌ Problema: No veo las pruebas de `OwnerRestControllerIT` en el reporte

**Causa:** El Maven Failsafe Plugin no está configurado en el `pom.xml`.

**Solución:** Verifica que el `pom.xml` incluya el plugin Failsafe (ver sección "Configuración del pom.xml"). Sin él, Maven solo ejecuta tests `*Test.java` con Surefire, ignorando los tests `*IT.java`.

```bash
# Esto NO ejecuta OwnerRestControllerIT (falso):
./mvnw test

# Esto SÍ ejecuta OwnerRestControllerIT (correcto):
./mvnw verify
```

### ❌ Problema: `mvn verify` falla con errores de JaCoCo

El plugin JaCoCo requiere datos de cobertura del archivo `jacoco.exec`. Si falta este archivo, el check fallará.

**Solución:** Asegúrate de que tu configuración de Surefire y Failsafe tenga el `<goal>prepare-agent</goal>` de JaCoCo ejecutándose antes de los tests.

---

### Reporte completo del proyecto (Maven Site)

Genera todos los reportes de análisis en un solo sitio HTML:

```bash
./mvnw clean test site -DgenerateReports=true
```

Salida: `target/site/index.html`

---

## Estructura de resultados

```
target/
├── allure-results/                          ← resultados JSON de Allure (generados por surefire)
│   └── *.json                               ← un archivo por test execution
├── allure-report/
│   └── index.html                           ← reporte HTML de Allure (generado por allure:report)
├── surefire-reports/
│   ├── TEST-...OwnerRestControllerIT.xml   ← resultado IT tests
│   └── TEST-...OwnerRestControllerTests.xml ← resultado unit tests
├── reports/
│   └── surefire.html                        ← reporte HTML de resultados
├── site/
│   └── jacoco/
│       ├── index.html                       ← reporte HTML de cobertura
│       └── jacoco.xml                       ← reporte XML para SonarCloud
```
