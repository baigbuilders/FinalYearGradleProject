package automation;

import static io.restassured.RestAssured.given;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AppTest {

	
	String firstName = "Siham";
	
	static ExtentHtmlReporter reporter;
	static ExtentReports report;
	static ExtentTest test;
	ChromeDriver driver;
	String url = "http://localhost:8082";
//	String url = System.getProperty("user.dir") + "/src/test/java/webServer/index.html";
	static File file = new File(System.getProperty("user.dir") + "/Report/Automation_.html");
		
	@BeforeClass
	public static void initilizeReport() {
		reporter = new ExtentHtmlReporter(file);
		reporter.config().setEncoding("utf-8");
		reporter.config().setDocumentTitle("Final Year Report");
		reporter.config().setReportName("Automation Report for Web & API");
		reporter.config().setTheme(Theme.STANDARD);
		
		report =  new ExtentReports();
		report.attachReporter(reporter);
		report.setSystemInfo("Enviroment", "Automation Test");
		report.setSystemInfo("Platform", System.getProperty("os.name"));
	}
	
	@Test
	public void apiTesting() {
		test = report.createTest("<b>[API]</b> Validate response code");
		try {
			
			RestAssured.baseURI = url;
			RestAssured.basePath = "/myproject.com/";
			
			
			Response response =
								given()
									.headers("Accept", "*/*")
									.headers("Accept-Encoding", "gzip, deflate, br")
									.headers("Connection", "keep-alive")
									.headers("sec-ch-ua", "'Google Chrome';v='105', 'Not)A;Brand';v='8', 'Chromium';v='105'")
									.headers("sec-ch-ua-mobile", "?0")
									.headers("sec-ch-ua-platform", "'Windows'")
								.when()
									.get()
								.then()
									.extract().response();
			long statusCode = response.getStatusCode();
			System.out.print("API status code: "+statusCode);
			if(statusCode == 200) {
				test.pass("Response status code: </b>"+statusCode+"</b>");
			} else {
				test.fail("Response status code: </b>"+statusCode+"</b>");
				Assert.fail();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			test.fail("Exception occuring: "+ e);
			Assert.fail();
		}
	}
	
	@Test
	public void webTesting() throws InterruptedException {
		test = report.createTest("<b>[UI]</b> Validate fields are inserting as expected");
		try {
			WebDriverManager.chromedriver().setup();
			driver= new ChromeDriver();
			if(driver != null) {
				test.pass("Chrome browser launched sucessfully !!!");
				driver.manage().window().maximize();
				
			}else {
				test.error("<b>[FAIL]</b> Chrome browser is not launched");
			}
			
			String insertUrl = url + "/myproject.com/index.html";
			driver.navigate().to(insertUrl);
			Thread.sleep(5000);
			System.out.println(driver.getCurrentUrl());
			if(insertUrl.equals(driver.getCurrentUrl())) {
				test.pass("URL <b>"+insertUrl+"</b> opens sucessfully");
				
			} else {
				test.error("<b>[FAIL]</b> URL is not open");
			}
			
			WebElement firstN = driver.findElement(By.name("first_name"));
			Thread.sleep(2000);
			firstN.sendKeys(firstName);
			String fname = firstN.getAttribute("value");
			System.out.println(fname);
			if(fname.equals("Siham")) {
				test.pass("First name is inserted as <b>"+firstName+"</b> successfully");
			}
			else
			{
				test.fail("First name is inserted as <b>"+firstName+"</b> ");
				Assert.fail();
			};
			
		} catch (Exception e) {
			e.printStackTrace();
			test.fail("Exception occuring: "+ e);
			Assert.fail();
		}
		test.pass("Chrome browser closed successfully");
		
		driver.close();
		String status = String.valueOf(test.getStatus());
		if(status.equals("pass")) {
			test.pass(MarkupHelper.createLabel("Test Case is : "+status, ExtentColor.GREEN));
		} else {
			test.fail(MarkupHelper.createLabel("Test Case is : "+status, ExtentColor.RED));
		}
	}

	@AfterClass
	public static void shutdown() {
		report.flush();
	}
	
}
