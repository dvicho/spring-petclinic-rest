package stepDefinitions;

import io.cucumber.java.es.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.junit.Assert;
import java.time.Duration;
import io.cucumber.java.After;

public class OwnersSteps {

    WebDriver driver;

    // /////////////////////////////////////////////////////////////////////////
    // CONFIGURACIÓN Y NAVEGACIÓN BASE  ///////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////

    @Dado("que el usuario abre el navegador en la pantalla de alta de dueños")
    public void abrirPantallaNewOwner() throws InterruptedException {
        driver = new EdgeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("http://localhost:4200/petclinic/owners/add");
        //Thread.sleep(2000);
    }

    @Dado("que el usuario se encuentra en la pantalla de búsqueda de dueños")
    public void abrirPantallaOwners() throws InterruptedException {
        driver = new EdgeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("http://localhost:4200/petclinic/owners");
        //Thread.sleep(2000);
    }

    // //////////////////////////////////////////////////////////////////////////
    // LLENADO DE FORMULARIOS DE PANTALLA NEW OWNER  ///////////////////////////
    // /////////////////////////////////////////////////////////////////////////

    @Cuando("escribe el nombre {string}")
    public void ingresarNombre(String nombre) throws InterruptedException {
        driver.findElement(By.name("firstName")).sendKeys(nombre);
        //Thread.sleep(1000);
    }

    @Y("escribe el apellido {string}")
    public void ingresarApellido(String apellido) throws InterruptedException {
        driver.findElement(By.name("lastName")).sendKeys(apellido);
        //Thread.sleep(1000);
    }

    @Y("la dirección {string}")
    public void ingresarDireccion(String dir) throws InterruptedException {
        driver.findElement(By.name("address")).sendKeys(dir);
        //Thread.sleep(1000);
    }

    @Y("la ciudad {string}")
    public void ingresarCiudad(String ciudad) throws InterruptedException {
        driver.findElement(By.name("city")).sendKeys(ciudad);
        //Thread.sleep(1000);
    }

    @Y("el teléfono {string}")
    public void ingresarTelefono(String tel) throws InterruptedException {
        driver.findElement(By.name("telephone")).sendKeys(tel);
        //Thread.sleep(2000);
    }

    @Y("hace clic en el botón de registrar dueño")
    public void hacerClicRegistrar() {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    // /////////////////////////////////////////////////////////////////////////
    //  LLENADO DE FORMULARIOS PANTALLA OWNERS  ///////////////////////////////
    // ////////////////////////////////////////////////////////////////////////

    @Cuando("ingresa el apellido {string} en el cuadro de texto")
    public void ingresarApellidoBusqueda(String apellido) throws InterruptedException {
        WebElement inputBusqueda = driver.findElement(By.tagName("input"));
        inputBusqueda.clear();
        inputBusqueda.sendKeys(apellido);
        //Thread.sleep(2000);
    }

    @Y("presiona el botón de buscar")
    public void presionarBuscar() {
        driver.findElement(By.className("btn-default")).click();
    }

    // /////////////////////////////////////////////////////////////////////////
    // VALIDACIONES - PANTALLA NEW OWNER  /////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////

    @Entonces("el sistema debe redirigirlo a la pantalla de listado de dueños")
    public void verificarRedireccion() throws InterruptedException {
        Thread.sleep(2000);
        String urlActual = driver.getCurrentUrl();
        Assert.assertTrue("Error en redirección tras alta exitosa", urlActual.endsWith("/owners"));
    }

    @Entonces("el sistema debe marcar error")
    public void verificarMensajeDeError() {
        try {
            WebElement mensajeError = driver.findElement(By.className("help-block"));
            Assert.assertTrue(mensajeError.isDisplayed());
            System.out.println("Validación OK - Error detectado en UI: " + mensajeError.getText());
        } catch (org.openqa.selenium.NoSuchElementException e) {
            Assert.fail("Fallo de seguridad: El sistema no bloqueó el dato inválido.");
        }
    }

    @Entonces("el botón de registrar dueño debe estar deshabilitado")
    public void verificarBotonDeshabilitado() throws InterruptedException {
        //Thread.sleep(1000);
        WebElement botonRegistrar = driver.findElement(By.cssSelector("button[type='submit']"));
        Assert.assertFalse("Bug UI: El botón de registro sigue activo con datos erróneos", botonRegistrar.isEnabled());
        System.out.println("Validación OK - El botón 'Add Owner' está correctamente bloqueado por faltar datos.");
    }

    // //////////////////////////////////////////////////////////////////////////
    // VALIDACIONES - PANTALLA OWNERS  //////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////

    @Entonces("el sistema debe mostrar los registros que contienen {string}")
    public void verificarRegistroEnTabla(String textoEsperado) throws InterruptedException {
        //Thread.sleep(3000);
        WebElement tabla = driver.findElement(By.className("table"));
        Assert.assertTrue("Validación fallida: Registro no encontrado en la tabla", tabla.getText().contains(textoEsperado));
        System.out.println("Validación OK - Se encontraron registros con el término: " + textoEsperado);
    }

    @Entonces("el sistema debe indicar que no se encontró al dueño")
    public void verificarMensajeNoEncontrado() throws InterruptedException {
        //Thread.sleep(2000);
        try {
            // XPath para busca cualquier texto en la pantalla
            WebElement mensajeError = driver.findElement(By.xpath("//*[contains(text(), 'No owners') or contains(@class, 'help-block') or contains(text(), 'not been found')]"));

            Assert.assertTrue("Fallo: El mensaje de 'no encontrado' no es visible", mensajeError.isDisplayed());
            System.out.println("Validación OK - El sistema mostró correctamente el aviso: " + mensajeError.getText());
        } catch (org.openqa.selenium.NoSuchElementException e) {
            Assert.fail("Bug UI: El sistema no notificó visualmente que la búsqueda falló.");
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // EL "BOTÓN DE REINICIO" AUTOMÁTICO DE NAVEGADOR  /////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////

    @After
    public void cerrarNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }
}
