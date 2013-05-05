package currency;


import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soulofpeace
 * Date: 5/5/13
 * Time: 9:20 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(JUnit4.class)
public class CurrencyReaderWriterTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }


    public void cleanUpStreams() {
        System.setOut(null);
    }

    @Test
    public void testInitializeCurrencyMap() throws IOException {
        CurrencyReaderWriter currencyReaderWriter = new CurrencyReaderWriter("src/test/resources/TestInput.txt");
        Map<String, Double> currencyMap = currencyReaderWriter.getCurrencyMap();
        Assert.assertEquals(currencyMap.get("USD"), new Double(900.0));
        Assert.assertEquals(currencyMap.get("HKD"), new Double(300.0));
    }

    @Test
    public void testPrinterThread() throws IOException {
        this.setUpStreams();
        Map<String, Double>  map = new ConcurrentHashMap<String, Double>(){{
            put("USD", 1000.0);
            put("SGD", 20.0);
            put("MYR", 0.0);

        }};
        ExchangeRate rates = new ExchangeRate();
        PrinterThread printerThread = new PrinterThread("USD", map, rates);
        PrinterThread printerThread2 = new PrinterThread("MYR", map, rates);
        PrinterThread printerThread3 = new PrinterThread("SGD", map, rates);
        printerThread.run();
        printerThread2.run();
        printerThread3.run();
        String output = outContent.toString();
        Assert.assertEquals(output,  "USD 1000 ( USD 1000 )\nSGD 20 ( USD 16.21 )\n");
        this.cleanUpStreams();

    }

   @Test
   public void testUpateEntryInMap() throws InterruptedException, IOException {
       CurrencyReaderWriter readerWriter = new CurrencyReaderWriter();
       ByteArrayInputStream in = new ByteArrayInputStream("USD 1000\nSGD 200\nUSD -200\nSGD 300\nquit".getBytes());
       System.setIn(in);
       readerWriter.run();
       Map<String, Double> map = readerWriter.getCurrencyMap();
       Assert.assertEquals(map.get("USD"), new Double(800.0));
       Assert.assertEquals(map.get("SGD"), new Double(500.0));
   }




}
