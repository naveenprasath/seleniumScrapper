import com.jaunt.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCrawler {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\navee\\Desktop\\chromedriver.exe");

        Document AB_PWR = Jsoup.connect("https://www.directenergy.com/alberta/electricity-plans")
                .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .timeout(80000).ignoreHttpErrors(true).get();
        String AB_PWRtext = "2-Year Simple Two Fixed Electricity Plan, " +extractPrice(AB_PWR.select("div[class=price-container]").text());

//        Document AB_GAS = Jsoup.connect("https://www.directenergy.com/alberta/natural-gas-plans")
//                .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0")
//                .timeout(80000).ignoreHttpErrors(true).get();
//        String AB_GAStext = AB_GAS.body().text().trim();
//
//        Document AB_BOTH = Jsoup.connect("https://www.directenergy.com/alberta/dual-fuel-plans")
//                .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0")
//                .timeout(80000).ignoreHttpErrors(true).get();
//        String AB_BOTHtext = AB_BOTH.body().text().trim();

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\Users\\navee\\Desktop\\JE.exe\\Default");
        options.addArguments("incognito");
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        WebDriver driver = new ChromeDriver(capabilities);
        driver.manage().window().setSize(new Dimension(200,200));
        driver.get("https://www.directenergy.com/texas");
        driver.findElement(By.name("goto_service_type")).sendKeys("electric_grid_url");
        driver.findElement(By.name("zipcode")).clear();
        driver.findElement(By.name("zipcode")).sendKeys("77098");
        driver.findElement(By.cssSelector(".startForm button em")).submit();
        Thread.sleep(800);
        Document TX_PWRH = Jsoup.parse(driver.getPageSource());
        String TX_PWRHtext = "Live Brighter 12 Fixed-Rate Plan for 12 Months, " +extractPrice(TX_PWRH.select("div[style=z-index:197]")
                .select("div[addr-title=Lock in Your Rate for 12 Months]").select("div[class=price]").text());

        driver.get("https://www.directenergy.com/texas");
        driver.findElement(By.name("goto_service_type")).sendKeys("electric_grid_url");
        driver.findElement(By.name("zipcode")).clear();
        driver.findElement(By.name("zipcode")).sendKeys("75201");
        driver.findElement(By.cssSelector(".startForm button em")).submit();
        Thread.sleep(800);
        Document TX_PWRD = Jsoup.parse(driver.getPageSource());
        String TX_PWRDtext = "Live Brighter 12 Fixed-Rate Plan for 12 Months, " +extractPrice(TX_PWRD.select("div[style=z-index:197]")
                .select("div[addr-title=Lock in Your Rate for 12 Months]").select("div[class=price]").text());

        driver.get("https://shop.constellation.com/#/19130/PECO_");
        driver.navigate().refresh();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.HOURS);
        Document PECO = Jsoup.parse(driver.getPageSource());
        String PECOtext = PECO.body().text();

        driver.get("https://shop.constellation.com/#/17815/PPL_");
        driver.navigate().refresh();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.HOURS);
        Document PPL = Jsoup.parse(driver.getPageSource());
        String PPLtext = PPL.body().text();

        driver.get("https://shop.constellation.com/#/10001/CONED_");
        driver.navigate().refresh();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.HOURS);
        Document CONED = Jsoup.parse(driver.getPageSource());
        String CONEDtext = CONED.body().text();

        System.out.println("DirectEnergy_Alberta_PWR, " +AB_PWRtext);
        System.out.println("DirectEnergy_Houston_PWR, " +TX_PWRHtext);
        System.out.println("DirectEnergy_Dallas_PWR, " +TX_PWRDtext);
        System.out.println("constellation_19130_PWR, 12 Month Fixed Rate, " +extractPrice(PECOtext.substring(PECOtext.lastIndexOf("12 Month Fixed Rate")+3, PECOtext.lastIndexOf("12 Month Fixed Rate")+35)));
        System.out.println("constellation_17815_PWR, 12 Month Fixed Rate, " +extractPrice(PPLtext.substring(PPLtext.lastIndexOf("12 Month Fixed Rate")+3, PPLtext.lastIndexOf("12 Month Fixed Rate") +35)));
        System.out.println("constellation_10001_PWR, 12 Month Fixed Rate, " +extractPrice(CONEDtext.substring(CONEDtext.lastIndexOf("12 Month Fixed Rate")+3, CONEDtext.lastIndexOf("12 Month Fixed Rate") +35)));
        Scanner sc = new Scanner(new File("urls.txt"));
        while(sc.hasNextLine()) {
            String url = sc.nextLine();
            driver.get(url.substring(url.indexOf("\t")+1));
//            driver.navigate().refresh();
//            driver.manage().timeouts().implicitlyWait(30, TimeUnit.HOURS);
            Document CON = Jsoup.parse(driver.getPageSource());
            String CONtext = CON.body().text().replaceAll("\n", "");
            System.out.println(CONtext);
        }
        driver.quit();
    }
    public static String extractPrice(String text) {
        Pattern regex = Pattern.compile("(\\d+(?:\\.\\d+)?)");
        Matcher matcher = regex.matcher(text);
        while (matcher.find()) {
            return (matcher.group(1));
        }
        return null;
    }
}