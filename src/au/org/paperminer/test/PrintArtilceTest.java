package au.org.paperminer.test;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class PrintArtilceTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8080/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testPrintArtilce() throws Exception {
    driver.get(baseUrl + "/PaperMiner/#");
    driver.findElement(By.linkText("Login or Register")).click();
    driver.findElement(By.id("em")).clear();
    driver.findElement(By.id("em")).sendKeys("mlk_771@hotmail.com");
    driver.findElement(By.xpath("(//button[@type='button'])[5]")).click();
    driver.findElement(By.linkText("New")).click();
    driver.findElement(By.id("q1")).clear();
    driver.findElement(By.id("q1")).sendKeys("paperminer");
    driver.findElement(By.id("nq-pb12")).click();
    driver.findElement(By.linkText("Raw Results")).click();
    driver.findElement(By.linkText("1890-07-19")).click();
    driver.findElement(By.linkText("Print Link")).click();
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
