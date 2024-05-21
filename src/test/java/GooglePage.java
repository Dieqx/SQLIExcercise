import lombok.Getter;
import org.openqa.selenium.By;

@Getter
public class GooglePage {
    private final By discardButton = By.xpath("//*[ text() ='Odrzuć wszystko']");
    private final By wikiButton = By.xpath("//*[ text() ='https://en.wikipedia.org']");
    private final By textAreaElement = By.tagName("textarea");
}
