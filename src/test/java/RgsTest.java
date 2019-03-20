import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class RgsTest extends BaseTest {

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"PETR PETROV", "07081987", false, "Швеция"},
                {"NIKOVAI NIKOLAEV", "10102000", false, "Греция"},
                {"IVAN IVANOV", "11121998", true, "Испания"}});
    }

    @Parameterized.Parameter
    public String name;
    @Parameterized.Parameter(1)
    public String birthday;
    @Parameterized.Parameter(2)
    public boolean active;
    @Parameterized.Parameter(3)
    public String country;

    @Test
    public void rgsTest() {

        // Ввод страны
        scrollByXpath("//*[contains(text(), 'Несколько')]");
        fillFormCountries("Countries", country);

        // Заполнение поля дата первой поездки
        scrollByXpath("//*[contains(@data-bind,'FirstD')]");
        fillFormXpath("//*[contains(@data-bind,'FirstD')]", dateGenerate());

        // Выбор времени нахождени не боле 90 дней
        scrollByXpath("//*[contains(text(), 'Не более 90')]");
        findByXpathAndClick("//*[contains(text(), 'Не более 90')]");

        // Заполнение ФИО
        scrollByXpath("//input[contains(@class,'form-control')][@data-test-name='FullName']");
        fillFormXpathName("//input[contains(@class,'form-control')][@data-test-name='FullName']", name);

        // Заполнение даты рождения
        scrollByXpath("//*[@data-test-name='BirthDate']");
        fillFormXpath("//*[@data-test-name='BirthDate']", birthday);

        // Чек-бокс планируется ли активный отдых
        checkBoxActive(active, "//div[contains(@data-bind,'active')]/div[contains(@class,'toggle')]");

        // Чек-бокс согласие на обработку персональных данных
        clickCheckBox("//input[contains(@data-test-name , 'IsProcessingPersonalDataTo')]");
    }

    @AfterClass
    public static void check() {
        // Нажатие кнопки рассчитать
        scrollByXpath("//*[@data-test-name='NextButton'][contains(@data-bind,'Misc.NextButton')]");
        findByXpathAndClick("//*[@data-test-name='NextButton'][contains(@data-bind,'Misc.NextButton')]");
        //Проверка значений
        assertPlus("Многократные поездки в течение года", "//*[contains(@class,'summary-value')][contains(@data-bind,'Trips')]");
        assertPlus("Шенген", ("//span/span/strong[contains(@data-bind, 'text: Name')]"));
        assertPlus("IVAN IVANOV", ("//strong[contains(@data-bind, 'text: Last')]"));
        assertPlus("11.12.1998", ("//strong[contains(@data-bind, 'text: Birth')]"));
        assertPlus("Включен", ("//div[contains(@data-bind, 'Актив')]/div[@class='summary-row']/span[@class='summary-value']/span"));
    }
}

