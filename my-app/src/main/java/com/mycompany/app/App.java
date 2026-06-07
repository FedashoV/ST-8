package com.mycompany.app;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class App 
{
    public static void main( String[] args )
    {
        // Настройка директории для скачивания
        String downloadDir = new File("../result").getAbsolutePath();
        new File(downloadDir).mkdirs();
        System.out.println("Директория для скачивания: " + downloadDir);

        // Настройки Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadDir);
        prefs.put("plugins.always_open_pdf_externally", true);
        options.setExperimentalOption("prefs", prefs);

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            System.out.println("Открываем страницу papercdcase.com...");
            driver.get("http://www.papercdcase.com");
            Thread.sleep(2000);
            
            List<String> tracks = new ArrayList<>();
            String artist = "";
            String album = "";

            File dataFile = new File("../data/data.txt");
            System.out.println("Читаем файл: " + dataFile.getAbsolutePath());

            List<String> lines = Files.readAllLines(dataFile.toPath());

            artist = lines.get(0);
            album = lines.get(1);
            for (int i = 2; i < lines.size(); i++) {
                tracks.add(lines.get(i));
            }

            System.out.println("Artist: " + artist);
            System.out.println("Album: " + album);
            System.out.println("Tracks count: " + tracks.size());

            
            System.out.println("Artist: " + artist);
            System.out.println("Album: " + album);
            System.out.println("Tracks count: " + tracks.size());
            
            // Заполняем Artist
            WebElement artistInput = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[1]/td[2]/input"));
            artistInput.sendKeys(artist);
            System.out.println("Заполнен Artist");
            
            // Заполняем Title
            WebElement titleInput = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[2]/td[2]/input"));
            titleInput.sendKeys(album);
            System.out.println("Заполнен Album Title");
            
            // Заполняем треки 
            int trackIndex = 0;
            for (int col = 1; col <= 2; col++) {
                for (int row = 1; row <= 8; row++) {
                    if (trackIndex < tracks.size()) {
                        String xpath = String.format("/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[%d]/table/tbody/tr[%d]/td[2]/input", col, row);
                        WebElement trackInput = driver.findElement(By.xpath(xpath));
                        trackInput.sendKeys(tracks.get(trackIndex));
                        System.out.println("Заполнен трек " + (trackIndex + 1) + ": " + tracks.get(trackIndex));
                        trackIndex++;
                    }
                }
            }
            
            // Выбираем Jewel Case
            WebElement typeJewel = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[4]/td[2]/input[2]"));
            typeJewel.click();
            System.out.println("Выбран тип: Jewel Case");
            
            // Выбираем A4
            WebElement sizeA4 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[5]/td[2]/input[2]"));
            sizeA4.click();
            System.out.println("Выбран формат: A4");
            
            // Нажимаем кнопку Submit
            WebElement submitBtn = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr/td[1]/div/form/p/input"));
            submitBtn.submit();
            System.out.println("Кнопка генерации нажата, ожидаем скачивание PDF...");
            
            Thread.sleep(5000);
            
            File downloadedFile = new File(downloadDir, "papercdcase.pdf");
            long timeout = System.currentTimeMillis() + 30000;
            while (!downloadedFile.exists() && System.currentTimeMillis() < timeout) {
                Thread.sleep(500);
            }
            
            if (downloadedFile.exists()) {
                File renamedFile = new File(downloadDir, "cd.pdf");
                Files.move(downloadedFile.toPath(), renamedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Success: PDF файл сохранен в result/cd.pdf");
            } else {
                System.out.println("Error: Файл не скачался");
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}