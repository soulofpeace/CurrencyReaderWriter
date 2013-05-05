package currency;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CurrencyReaderWriter {


    public static void main(String[] args) throws IOException, InterruptedException {
        CurrencyReaderWriter currencyReaderWriter = null;
        if (args.length > 0) {
            currencyReaderWriter = new CurrencyReaderWriter(args[0]);
        } else {
            currencyReaderWriter = new CurrencyReaderWriter();
        }
        currencyReaderWriter.run();
    }

    private Map<String, Double> currencyMap = new ConcurrentHashMap<>();
    private Map<String, PrinterThread> printerThreadMap = new HashMap<>();
    private ExchangeRate exchangeRate;


    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);


    public CurrencyReaderWriter() throws IOException {
        this.exchangeRate = new ExchangeRate();

    }

    public CurrencyReaderWriter(String fileName) throws IOException {
        this.currencyMap = this.initializeCurrencyMap(this.currencyMap, fileName);
        this.exchangeRate = new ExchangeRate();
    }


    public void run() throws InterruptedException {
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNext()) {
                String raw = scanner.nextLine();
                if (raw.equalsIgnoreCase("quit")) {
                    break;
                } else {
                    this.currencyMap = updateEntryInMap(raw, this.currencyMap);
                }
            }
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    private Map<String, Double> initializeCurrencyMap(Map<String, Double> currencyMap, String fileName) throws IOException {
        try (Scanner scanner = new Scanner(new FileInputStream(fileName))) {
            while (scanner.hasNext()) {
                updateEntryInMap(scanner.nextLine(), currencyMap);
            }
            return currencyMap;
        } catch (IOException e) {
            System.out.println("IOException while reading " + fileName);
            throw e;
        }
    }

    private Map<String, Double> updateEntryInMap(String raw, Map<String, Double> currencyMap) {
        try {
            String[] tokens = raw.split(" ");
            String currencyCode = tokens[0];
            if (!exchangeRate.isValidCurrencyCode(currencyCode)) {
                System.out.println("Not Valid currency Code");
                return currencyMap;
            }
            Double amount = Double.parseDouble(tokens[1]);
            Double newAmount = null;
            Boolean newCurrencyCode = false;
            if (currencyMap.containsKey(currencyCode)) {
                newAmount = currencyMap.get(currencyCode) + amount;
            } else {
                newAmount = amount;
                newCurrencyCode = true;
            }

            currencyMap.put(currencyCode, newAmount);
            if (newCurrencyCode) {
                PrinterThread printerThread = createNewPrinterThread(currencyCode);
            }

            return currencyMap;
        } catch (NumberFormatException ex) {
            System.out.println("Invalid amount, " + raw + " entered. Skipping Entry");
            return currencyMap;
        }
    }

    public Map<String, Double> getCurrencyMap() {
        return currencyMap;
    }

    public void setCurrencyMap(Map<String, Double> currencyMap) {
        this.currencyMap = currencyMap;
    }

    private PrinterThread createNewPrinterThread(String currencyCode) {
        PrinterThread printerThread = new PrinterThread(currencyCode, this.currencyMap, this.exchangeRate);
        this.executorService.scheduleAtFixedRate(printerThread, 0, 1, TimeUnit.MINUTES);
        this.printerThreadMap.put(currencyCode, printerThread);
        return printerThread;
    }

}
