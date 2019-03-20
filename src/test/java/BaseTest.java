import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

public class BaseTest {
    public static WebDriver driver;

    @BeforeClass
    public static void testsSetup() {
        String browser = System.getProperty("webdriver");
        switch (browser) {
            case "Chrome": {
                System.out.println("Выбран Chrome");
                System.setProperty("webdriver.chrome.driver", "drv/chromedriver.exe");
                driver = new ChromeDriver();
                break;
            }
            case "Firefox": {
                System.out.println("Выбран Firefox");
                System.setProperty("webdriver.gecko.driver", "drv/geckodriver.exe");
                driver = new FirefoxDriver();
                break;
            }
            case "IE": {
                System.out.println("Выбран IE");
                System.setProperty("webdriver.ie.driver", "drv/IEDriverServer.exe");
                driver = new InternetExplorerDriver();
                break;
            }
            default: {
                System.out.println("Выбор браузера некорректен, запускаем в Chrome");
                System.setProperty("webdriver.chrome.driver", "drv/chromedriver.exe");
                driver = new ChromeDriver();
                break;
            }
        }
        String url = "https://www.rgs.ru/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get(url);

        //Выбор Страхования
        findByXpathAndClick("//ol/li/a[contains(text(),'Страхование')]");

        // Выбор пункта Страхование выезжающих за рубеж
        findByXpathAndClick("//*[contains(text(),'Выезжающим')]");

        // Нажатие кнопки рассчитат онйлан
        scrollByXpath("//a[contains(text(),'Рассчитать')]");
        findByXpathAndClick("//a[contains(text(),'Рассчитать')]");

        // Сравнение текста в заголовке
        Wait<WebDriver> wait = new WebDriverWait(driver, 10, 1000);
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[contains(@class,'page-header')]/span"))));
        assertPlus("Страхование выезжающих за рубеж", "//*[contains(@class,'page-header')]/span");

        // Выбор нескольких поездок в течении года
        scrollByXpath("//*[contains(text(), 'Несколько')]");
        findByXpathAndClick("//*[contains(text(), 'Несколько')]");
    }

    @AfterClass
    public static void close() {
        driver.quit();
    }

    /**
     * Метод нажимающий чек-бокс если он не активен
     *
     * @param xPath - xPath чек-бокса
     */
    public static void clickCheckBox(String xPath) {
        WebElement ScrollLocation = driver.findElement(By.xpath(xPath));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", ScrollLocation);
        if (!driver.findElement(By.xpath(xPath)).isSelected()) {
            driver.findElement(By.xpath(xPath)).click();
        }
    }

    /**
     * Метод находящий элемент и кликающий на него
     *
     * @param xPath xPath элемента
     */
    public static void findByXpathAndClick(String xPath) {
        driver.findElement(By.xpath(xPath)).click();
    }

    /**
     * Метод проскролливающий до элемента
     *
     * @param xPath xPath элемента
     */
    public static void scrollByXpath(String xPath) {
        WebElement element = (new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath(xPath))));
        WebElement ScrollLocation = driver.findElement(By.xpath(xPath));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", ScrollLocation);
    }

    /**
     * Метод заполнения поля по его ID
     *
     * @param formId Id элемента
     * @param text   текст для заполнения
     */
    public void fillFormCountries(String formId, String text) {
        driver.findElement(By.id(formId)).click();
        driver.findElement(By.id(formId)).clear();
        driver.findElement(By.id(formId)).sendKeys(text);
        driver.findElement(By.id(formId)).sendKeys(Keys.DOWN, Keys.RETURN);
    }

    /**
     * Метод заполнения поля по его xPath
     *
     * @param xPath xPath элемента
     * @param text  текст для заполнения
     */

    public void fillFormXpath(String xPath, String text) {
        driver.findElement(By.xpath(xPath)).click();
        driver.findElement(By.xpath(xPath)).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        driver.findElement(By.xpath(xPath)).sendKeys(text);
    }

    /**
     * Метод для заполнения фио
     *
     * @param xPath xPath элемента
     * @param name  фамилия
     */
    public static void fillFormXpathName(String xPath, String name) {
        JavascriptExecutor jst = (JavascriptExecutor) driver;
        jst.executeScript("arguments[1].value = arguments[0]; ", name, driver.findElement(By.xpath((xPath))));
        driver.findElement(By.xpath(xPath)).sendKeys(Keys.DOWN);
    }

    /**
     * Метод генерирующий дату в промежутке от завтра до двух недель вперед
     *
     * @return сгенерированная дата
     */
    public String dateGenerate() {
        Random random = new Random();
        Long date = (new Date().getTime()) + ((random.nextInt(13) + 1) * 24 * 3600 * 1000);
        Date Date = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM y");
        return dateFormat.format(Date);

    }

    /**
     * Метод сравнивающий реальный и ожидаемый результаты
     */

    public static void assertPlus(String expected, String xPath) {
        scrollByXpath(xPath);
        String actual = driver.findElement(By.xpath(xPath)).getText();
        assertEquals("Error instead of expected: " + expected + " found: " + actual, expected, actual);
        if (actual.contains(expected)) {
            System.out.println(expected + "  -  found");
        }
    }

    /**
     * Метод устанавливающий чек-бокс либо снимающий, в зависимости от передаваемого аргумента
     *
     * @param active должен ли чек-бок быть установлен
     * @param xPath  -xPath чек-бокса
     */

    public static void checkBoxActive(boolean active, String xPath) {
        if (active & driver.findElement(By.xpath(xPath)).getAttribute("class").contains("off")) {
            driver.findElement(By.xpath(xPath)).click();
        }
        if (!active & !driver.findElement(By.xpath(xPath)).getAttribute("class").contains("off")) {
            driver.findElement(By.xpath(xPath)).click();
        }
    }
}