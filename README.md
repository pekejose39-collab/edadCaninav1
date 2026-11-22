# 🐶 EdadCaninaMVC · v3 (rama `v3-mvc`)

La v3 hace lo mismo que la v2 (convierte la edad y valida la entrada) y añade un botón **Limpiar**. Lo que cambia es cómo está organizado el código: usa **DataBinding** y el patrón **MVC**. `applicationId` es `es.davante.edadcanina.mvc`, así que se puede instalar junto a la v2.

| Pieza | Fichero | Qué hace |
|---|---|---|
| Modelo | `modelo/EdadCaninaModelo`, `modelo/Validacion` | Guarda el texto, la edad humana y la validación; valida, calcula y avisa (`BaseObservable`) |
| Vista | `activity_main.xml`, `vista/AdaptadoresVista` | Se pinta a partir del modelo (`@{}`, `@={}`, `app:errorValidacion`) y manda los clics al controlador |
| Controlador | `controlador/EdadCaninaControlador` | Un método por cada acción del usuario: le da una orden al modelo |
| Activity | `MainActivity` | Crea las piezas, las conecta y guarda/restaura el estado al girar |

Hay un commit por paso, etiquetado de `paso-1` a `paso-7`. Las pruebas JUnit están en `app/src/test` (23 pruebas: modelo, controlador y casos raros).

## Reto elegido: «Casos raros»

**Qué pasaba.** El modelo confiaba en `Integer.parseInt`, que es más permisivo de lo que parece:

| Entrada | Antes | Ahora | Por qué |
|---|---|---|---|
| `05` | OK (35) | OK (35) | Un cero a la izquierda no cambia el número; rechazarlo no ayuda a nadie |
| `+5` | OK (35) | `NO_NUMERO` | Un signo no es una cifra; una edad no lleva signo |
| `-4` | `FUERA_DE_RANGO` | `NO_NUMERO` | "Debe estar entre 1 y 30" confunde: el problema es el signo, no el tamaño |
| `3.5` | `NO_NUMERO` | `NO_NUMERO` | Solo enteros |
| `99999999999` | `NO_NUMERO` | `FUERA_DE_RANGO` | Son cifras, pero el número es demasiado grande: el error correcto es el de rango |
| `٥` (cinco árabe-índico) | OK (35) | `NO_NUMERO` | `parseInt` acepta cifras de otros alfabetos; nos quedamos con 0-9 |

**Cómo lo he resuelto.** Primero escribí las pruebas (`CasosRarosTest`) y comprobé que cuatro fallaban. Después añadí una regla al modelo: antes de convertir, el texto (sin espacios) tiene que cumplir `[0-9]+`. Si no la cumple, es `NO_NUMERO`. Si son cifras pero `parseInt` falla, es porque el número no cabe en un `int`, así que es `FUERA_DE_RANGO`. El teclado numérico ya impide escribir muchos de estos casos, pero el modelo no debe fiarse de la vista: se puede pegar texto, y el layout puede cambiar. Solo ha habido que tocar el modelo, y la vista ha mostrado los mensajes nuevos sin cambiar nada.

## Explícalo con mis palabras

**1. ¿Qué diferencia hay entre `@{...}` y `@={...}`?**
`@{...}` va en un solo sentido, del modelo a la vista: la vista lee el dato y se repinta cuando el modelo avisa. Lo uso para el resultado y para el error. `@={...}` es bidireccional: además, cuando el usuario cambia la vista, DataBinding llama al setter del modelo (`setEdadTexto`). Por eso solo tiene sentido en entradas como el `EditText`. Y por eso el setter comprueba si el valor ha cambiado de verdad: si no, el aviso volvería a escribir el texto en la vista y la vista volvería a llamar al setter, en un bucle.

**2. ¿Qué ocurre, paso a paso, desde que pulsas Calcular hasta que aparece «42»?**
1. Mientras escribo "6", el enlace `@={modelo.edadTexto}` ya ha llamado a `setEdadTexto("6")`. El modelo guarda el texto y avisa.
2. Al pulsar Calcular, el `android:onClick="@{() -> controlador.onCalcularClick()}"` ejecuta la lambda.
3. El controlador solo da la orden: `modelo.calcular()`.
4. El modelo valida ("6" son cifras, está entre 1 y 30) y calcula 6 × 7 = 42. `setEdadHumana(42)` avisa con `notifyPropertyChanged(BR.edadHumana)` y `setValidacion(OK)` avisa con `BR.validacion`.
5. El binding recibe los avisos y vuelve a evaluar las expresiones que dependen de esas propiedades: el texto `@string/tu_edad_format(modelo.edadHumana)` pasa a «… tendría 42 años», la visibilidad pasa a `VISIBLE` porque la validación es `OK`, y `app:errorValidacion` llama al adaptador, que quita el error.
6. En pantalla aparece «42». Ni la Activity ni el controlador han tocado una vista.

**3. ¿Qué cambiarías para que el factor fuera 6 en vez de 7, y por qué solo tocas un fichero?**
Solo la constante `FACTOR_CANINO = 6` en `EdadCaninaModelo`. El factor es una regla de negocio y en MVC las reglas viven solo en el modelo: la vista muestra la `edadHumana` que le da el modelo sin saber cómo se ha calculado, el controlador solo dice "calcula" y la Activity solo conecta. Habría que cambiar también las pruebas que esperan 42, que es justo lo que se quiere: avisan de que la regla ha cambiado.

## Respuestas a las preguntas «Piensa»

- **Paso 1.** Todavía no aparece ninguna clase nueva: DataBinding genera las clases al compilar y solo para los layouts envueltos en `<layout>`, y aún no había ninguno.
- **Paso 2.** Con `findViewById`, un id mal escrito se descubría en ejecución (`NullPointerException`). Con el binding, el campo no existe y el proyecto no compila: me entero antes.
- **Paso 3.** No. El modelo devuelve una `Validacion`, no textos. Traducir la app es añadir `values-en/strings.xml`.
- **Paso 4.** La cadena se corta en el `if (nuevo.equals(edadTexto)) return;`: cuando la vista reescribe el mismo texto y vuelve a llamar al setter, ya no hay aviso.
- **Paso 5.** Porque decide cómo se ve algo: usa `getString` (texto según el idioma), `setError` y el foco. Todo eso es Android y presentación. Si estuviera en el modelo, el modelo dependería de Android y no se podría probar sin emulador.
- **Paso 6.** Es una buena señal: el controlador solo traduce clics en órdenes. Si creciera, sería porque se le está colando trabajo de la vista o del modelo.
- **Paso 7.** Sin guardar nada, al girar se perdía todo: la Activity nueva crea un modelo vacío y la vista se pinta con él. El modelo podría vivir fuera de la Activity, en un `ViewModel`, que sobrevive a los giros (MVVM).

## Autoevaluación

- [x] Hace lo mismo que la v2: calcula, valida los cuatro casos de error y no se cierra con ninguna entrada.
- [x] `MainActivity` solo crea las piezas, las conecta y guarda/restaura el estado.
- [x] El modelo no importa nada de `android.*` (solo `androidx.databinding`) ni contiene textos para el usuario.
- [x] El controlador no tiene referencias a vistas.
- [x] La vista usa `@={}` en la entrada, `@{}` en el resultado y un atributo propio para el error.
- [x] Al girar la pantalla no se pierde nada.
- [x] Al menos 5 pruebas JUnit en verde (hay 23).
- [x] Los comentarios explican por qué se hace cada cosa.
- [x] Un commit por paso.

# 🐶 EdadCanina · v1

Aplicación Android (Java) que calcula la edad "humana" de un perro multiplicando su edad por 7.

Esta es la **versión 1**: funciona, pero no comprueba lo que escribe el usuario. Tu tarea es convertirla en la **versión 2** (rama `v2-validacion`), que valida la entrada y controla los errores sin que la app se cierre.

---

## Objetivos

Al terminar sabrás:

- Detectar por qué una app se cierra (`NumberFormatException`) y evitarlo con `try/catch`.
- Validar datos de entrada: campo vacío, texto no numérico y rango de valores.
- Mostrar errores al usuario con `EditText.setError()`.
- Sustituir "números mágicos" por constantes.
- Llevar los textos a `strings.xml` y usar formatos con varios parámetros.
- Trabajar en una rama de Git distinta de `main`.

---

## Paso 0 · Prepara el proyecto

1. Haz un **fork** de este repositorio (botón *Fork*) y clona tu fork:
   ```bash
   git clone https://github.com/TU_USUARIO/edadCaninav1.git
   ```
2. Ábrelo en Android Studio y ejecútalo en el emulador.

## Paso 1 · Rompe la app (a propósito)

Antes de arreglar nada, comprueba qué falla. Prueba estas entradas y anota qué pasa:

| Entrada | ¿Qué ocurre? |
|---|---|
| *(campo vacío)* | |
| `abc` | |
| `3.5` | |
| `0` | |
| `-4` | |
| `99999999999` | |
| `400000000` | |

> 💡 Cuando la app se cierre, abre el **Logcat** y busca la excepción. ¿Qué método la lanza? ¿Por qué el último caso no cierra la app pero da un resultado absurdo?

## Paso 2 · Crea la rama de trabajo

```bash
git checkout -b v2-validacion
```

Todo lo que hagas a partir de aquí irá en esta rama; `main` se queda como está.

## Paso 3 · Corrige los fallos que ya trae la v1

Antes de añadir nada nuevo, limpia lo que hay:

1. **`activity_main.xml`**: busca la etiqueta de cierre `</TextView>>` y quita el `>` que sobra.
2. **`strings.xml`**: en `tu_edad_format` hay una falta de ortografía ("fuero"). Corrígela.
3. **Texto escrito "a fuego"**: el `android:hint="Ejemplo: 6"` del `EditText` está directamente en el layout. Llévalo a `strings.xml` con el nombre `hint_edad` y úsalo con `@string/hint_edad`.
   > 💡 Android Studio marca este aviso en amarillo (*Hardcoded string*). Prueba `Alt + Enter` encima.
4. **`MainActivity.java`**: hay tres `import` que no se usan (`Insets`, `ViewCompat`, `WindowInsetsCompat`). Elimínalos (`Ctrl + Alt + O`).

## Paso 4 · Añade los mensajes de error

En `res/values/strings.xml` añade tres textos nuevos:

```xml
<string name="error_vacio">Introduce la edad de tu perro</string>
<string name="error_no_numero">Introduce un número entero válido</string>
<string name="error_rango">La edad debe estar entre %1$d y %2$d años</string>
```

> ❓ ¿Qué significan `%1$d` y `%2$d`? ¿En qué orden se rellenan?

## Paso 5 · Restringe la entrada desde el layout

En el `EditText` (`@+id/age_input`) añade:

```xml
android:inputType="number"
android:maxLength="2"
android:gravity="center"
android:importantForAutofill="no"
```

| Atributo | Para qué sirve |
|---|---|
| `inputType="number"` | Muestra el teclado numérico: sin letras, sin signo menos ni decimales. |
| `maxLength="2"` | No deja escribir más de 2 cifras. |
| `gravity="center"` | Centra el número en el campo. |
| `importantForAutofill="no"` | Evita que el sistema ofrezca autocompletar. |

> ⚠️ Esto **no sustituye** a la validación en Java: el campo puede quedarse vacío y nunca conviene fiarse solo de la interfaz. Por eso seguimos con el código.

## Paso 6 · Sustituye los números mágicos por constantes

Al principio de la clase `MainActivity` declara:

```java
private static final int FACTOR_CANINO = 7;
private static final int EDAD_MINIMA = 1;
private static final int EDAD_MAXIMA = 30; // ningún perro supera esta edad
```

y usa `FACTOR_CANINO` en lugar del `7`.

## Paso 7 · Convierte las vistas en atributos de la clase

Ahora `ageEdit` y `resultText` son variables locales de `onCreate()`. Como vas a usarlas desde otros métodos, decláralas como atributos:

```java
private EditText ageEdit;
private TextView resultText;
```

y en `onCreate()` asígnalas **sin volver a escribir el tipo**:

```java
ageEdit = findViewById(R.id.age_input);
resultText = findViewById(R.id.result_text);
```

> ❓ ¿Qué pasaría si dejaras `EditText ageEdit = findViewById(...)` dentro de `onCreate()`?

## Paso 8 · Saca la lógica del botón a un método

El `setOnClickListener` debe quedar así de corto:

```java
button1.setOnClickListener(view -> calcularEdad());
```

Toda la lógica pasa a un método nuevo: `private void calcularEdad()`.

## Paso 9 · Crea un método para mostrar errores

```java
private void mostrarError(String mensaje) {
    // 1. Mostrar el mensaje en el EditText (setError)
    // 2. Poner el foco en el EditText (requestFocus)
    // 3. Borrar el resultado anterior del TextView
}
```

## Paso 10 · Valida la entrada en `calcularEdad()`

Implementa el método siguiendo este orden. En cuanto algo falle, llama a `mostrarError(...)` y sal con `return`:

1. **Leer** el texto del `EditText` quitando espacios (`trim()`).
2. **¿Está vacío?** → `error_vacio`. Recuerda que `Integer.parseInt("")` lanza `NumberFormatException`.
3. **Convertir a `int`** dentro de un `try/catch (NumberFormatException e)` → si falla, `error_no_numero`.
4. **¿Está en rango?** Si `age < EDAD_MINIMA || age > EDAD_MAXIMA` → `error_rango`. Para rellenar `%1$d` y `%2$d`:
   ```java
   getString(R.string.error_rango, EDAD_MINIMA, EDAD_MAXIMA)
   ```
5. **Todo correcto**: quita el error del campo (`setError(null)`), calcula `age * FACTOR_CANINO` y muestra el resultado con `tu_edad_format`.

> ❓ ¿Por qué limitar la edad a 30 evita también el desbordamiento que viste en el Paso 1?

## Paso 11 · Vuelve a probar

Comprueba que **la app nunca se cierra**:

| Entrada | Resultado esperado |
|---|---|
| *(vacío)* | Error: "Introduce la edad de tu perro" |
| `0` | Error: "La edad debe estar entre 1 y 30 años" |
| `31` | Error de rango |
| `1` | "Si tu perro fuera humano tendría 7 años" |
| `30` | "Si tu perro fuera humano tendría 210 años" |

> 💡 Con el teclado numérico no puedes escribir `abc` ni `-4`. ¿Cómo comprobarías que el `try/catch` funciona? (Pista: quita `inputType` un momento.)

## Paso 12 · Guarda y sube tu trabajo

```bash
git add .
git commit -m "Validación de la entrada y control de errores aritméticos"
git push -u origin v2-validacion
```

Comprueba en GitHub que tu fork tiene ahora dos ramas: `main` y `v2-validacion`.

---

## ✅ Lista de comprobación

- [ ] La app no se cierra con ninguna entrada.
- [ ] Todos los textos están en `strings.xml` (ningún aviso *Hardcoded string*).
- [ ] No quedan números mágicos en `MainActivity`.
- [ ] `onCreate()` solo prepara la pantalla; la lógica está en `calcularEdad()`.
- [ ] El trabajo está en la rama `v2-validacion`, no en `main`.
