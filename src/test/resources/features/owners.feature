# language: es
Requisito: Validar la gestión de dueños en la plataforma Petclinic

  # Good Path pantalla New Owner
  Escenario: Agregar un nuevo dueño de mascota con éxito
    Dado que el usuario abre el navegador en la pantalla de alta de dueños
    Cuando escribe el nombre "Angel"
    Y escribe el apellido "Vega"
    Y la dirección "Av. Tecnologico 456"
    Y la ciudad "Nogales"
    Y el teléfono "6311234567"
    Y hace clic en el botón de registrar dueño
    Entonces el sistema debe redirigirlo a la pantalla de listado de dueños

# Pruebas pantalla New Owner
  Escenario: Registrar un dueño con acentos o caracteres especiales válidos (ñ)
    Dado que el usuario abre el navegador en la pantalla de alta de dueños
    Cuando escribe el nombre "José"
    Y escribe el apellido "Muñoz"
    Y la dirección "Av. Tecnológico 456"
    Y la ciudad "Nogales"
    Y el teléfono "6311234567"
    Y hace clic en el botón de registrar dueño
    Entonces el sistema debe redirigirlo a la pantalla de listado de dueños

  Escenario: Iniciar un nombre con numeros
    Dado que el usuario abre el navegador en la pantalla de alta de dueños
    Cuando escribe el nombre "123Saul"
    Entonces el sistema debe marcar error

  Escenario: Incluir caracteres especiales en apellido
    Dado que el usuario abre el navegador en la pantalla de alta de dueños
    Cuando escribe el apellido "Jose @#"
    Entonces el sistema debe marcar error

  Escenario: Intentar registrar un dueño con un teléfono inválido (letras)
    Dado que el usuario abre el navegador en la pantalla de alta de dueños
    Cuando escribe el nombre "Sofia"
    Y escribe el apellido "Soto"
    Y la dirección "Av. Tecnologico 123"
    Y la ciudad "Nogales"
    Y el teléfono "abcde"
    Entonces el sistema debe marcar error

  Escenario: Intentar registrar un dueño dejando un campo obligatorio en blanco
    Dado que el usuario abre el navegador en la pantalla de alta de dueños
    Cuando escribe el nombre "Giselle"
    Y escribe el apellido ""
    Y la dirección "Av. Tecnologico 123"
    Y la ciudad "Nogales"
    Y el teléfono "6311234567"
    Entonces el botón de registrar dueño debe estar deshabilitado

  Escenario: Intentar registrar un dueño usando solo espacios en blanco
    Dado que el usuario abre el navegador en la pantalla de alta de dueños
    Cuando escribe el nombre "     "
    Y escribe el apellido "Torres"
    Y la dirección "Av. Tecnologico 123"
    Y la ciudad "Nogales"
    Y el teléfono "6311234567"
    Entonces el botón de registrar dueño debe estar deshabilitado

  Escenario: Intentar registrar un nombre de una sola letra (Límite inferior)
    Dado que el usuario abre el navegador en la pantalla de alta de dueños
    Cuando escribe el nombre "A"
    Y escribe el apellido "Yocupicio"
    Y la dirección "Av. Tecnologico 123"
    Y la ciudad "Nogales"
    Y el teléfono "6311234567"
    Entonces el sistema debe marcar error

  Escenario: Intentar registrar un dueño con un teléfono más de 10 digitos
    Dado que el usuario abre el navegador en la pantalla de alta de dueños
    Cuando escribe el nombre "Victor"
    Y escribe el apellido "Moroyoqui"
    Y la dirección "Av. Tecnologico 123"
    Y la ciudad "Nogales"
    Y el teléfono "12345678901234567890"
    Entonces el botón de registrar dueño debe estar deshabilitado

  Escenario: Intentar registrar un dueño exactamente igual a uno existente (Duplicidad)
    Dado que el usuario abre el navegador en la pantalla de alta de dueños
    Cuando escribe el nombre "George"
    Y escribe el apellido "Franklin"
    Y la dirección "110 W. Liberty St."
    Y la ciudad "Madison"
    Y el teléfono "6085551023"
    Y hace clic en el botón de registrar dueño
    Entonces el sistema debe marcar error

    #Good Path pantalla Owners
  Escenario: Buscar un dueño existente por su apellido
    Dado que el usuario se encuentra en la pantalla de búsqueda de dueños
    Cuando ingresa el apellido "Davis" en el cuadro de texto
    Y presiona el botón de buscar
    Entonces el sistema debe mostrar los registros que contienen "Davis"

    #Pruebas pantalla Owners

  Escenario: Buscar un dueño que no existe en la base de datos
    Dado que el usuario se encuentra en la pantalla de búsqueda de dueños
    Cuando ingresa el apellido "Soto" en el cuadro de texto
    Y presiona el botón de buscar
    Entonces el sistema debe indicar que no se encontró al dueño

  Escenario: Intentar buscar usando caracteres especiales
    Dado que el usuario se encuentra en la pantalla de búsqueda de dueños
    Cuando ingresa el apellido "@#$%" en el cuadro de texto
    Y presiona el botón de buscar
    Entonces el sistema debe indicar que no se encontró al dueño

  Escenario: Intentar buscar usando numeros
    Dado que el usuario se encuentra en la pantalla de búsqueda de dueños
    Cuando ingresa el apellido "2341" en el cuadro de texto
    Y presiona el botón de buscar
    Entonces el sistema debe indicar que no se encontró al dueño

  Escenario: Realizar una búsqueda con espacios en blanco para ver todos los registros
    Dado que el usuario se encuentra en la pantalla de búsqueda de dueños
    Cuando ingresa el apellido "   " en el cuadro de texto
    Y presiona el botón de buscar
    Entonces el sistema debe redirigirlo a la pantalla de listado de dueños

  Escenario: Buscar un dueño que existe, pero iniciando su apellido con minusculas
    Dado que el usuario se encuentra en la pantalla de búsqueda de dueños
    Cuando ingresa el apellido "franklin" en el cuadro de texto
    Y presiona el botón de buscar
    Entonces el sistema debe mostrar los registros que contienen "Franklin"

  Escenario: Buscar un dueño escribiendo solo el inicio de su apellido (Búsqueda parcial)
    Dado que el usuario se encuentra en la pantalla de búsqueda de dueños
    Cuando ingresa el apellido "Fra" en el cuadro de texto
    Y presiona el botón de buscar
    Entonces el sistema debe mostrar los registros que contienen "Franklin"

  Escenario: Buscar un dueño con espacios accidentales al inicio y al final
    Dado que el usuario se encuentra en la pantalla de búsqueda de dueños
    Cuando ingresa el apellido " Franklin " en el cuadro de texto
    Y presiona el botón de buscar
    Entonces el sistema debe mostrar los registros que contienen "Franklin"

