import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSender;
import ru.netology.sender.MessageSenderImpl;
import java.util.HashMap;
import java.util.Map;


public class TestHomework {

    @ParameterizedTest
    @CsvSource({"Добро пожаловать, 172", "Welcome, 96"})
    @DisplayName("Проверка соответствия языка отправляемого текста ip страны отправления")
    public void test_Send(String expected, String ip) {
        String actual;

        GeoService geoService = Mockito.mock(GeoService.class);
        LocalizationService localizationService = Mockito.mock(LocalizationService.class);

        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);
        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);

        Country country = (ip.equals("172")) ? Country.RUSSIA : Country.USA;
        Location location = new Location(" ", country, " ", 0);
        Mockito.when(geoService.byIp(ip)).thenReturn(location);
        Mockito.when(localizationService.locale(location.getCountry())).thenReturn(expected);
        actual = messageSender.send(headers);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({"172.0.32.11, Moscow RUSSIA Lenina 15",
            "96.44.183.149, New York USA 10th Avenue 32",
            "127.0.0.1, null null null 0",
            "1,"})
    @DisplayName("Проверка работы метода byIp класса GeoServiceImpl")
    public void test_byIp(String ip, String expected) {
        String actual;

        GeoService geoService = new GeoServiceImpl();
        Location location = geoService.byIp(ip);
        if(location != null){
            actual = location.getCity() + " " + location.getCountry() + " " + location.getStreet() + " " + location.getBuiling();
        } else actual = null;

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Проверка работы метода byCoordinates класса GeoServiceImpl")
    public void test_byCoordinates() {
        final GeoService service = new GeoServiceImpl();
        Assertions.assertThrows(RuntimeException.class, ()-> service.byCoordinates(0.0, 0.0));
    }

    @ParameterizedTest
    @EnumSource(Country.class)
    @DisplayName("Проверка работы метода locale класса LocalizationServiceImpl")
    public void test_locale(Country country) {
        String expected = (country.equals(Country.RUSSIA)) ? "Добро пожаловать" : "Welcome";
        String actual;

        LocalizationService service = new LocalizationServiceImpl();
        actual = service.locale(country);

        Assertions.assertEquals(expected, actual);
    }
}
