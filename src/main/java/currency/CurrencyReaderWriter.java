package currency;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyReaderWriter{


    public static void main(String[] args) throws IOException {
        CurrencyReaderWriter currencyReaderWriter = null;
        if ( args.length > 0 ){
            currencyReaderWriter = new CurrencyReaderWriter(args[0]);
        }
        else{
            currencyReaderWriter = new CurrencyReaderWriter();
        }
        currencyReaderWriter.run();
    }

    private Map<String, Double> currencyMap = new ConcurrentHashMap();
    private Map<String, PrinterThread> printerThreadMap = new HashMap<>();
    private static final Long ONE_MINUTE = 60*1000L;


    private Timer timer =  new Timer();


    public CurrencyReaderWriter(){

    }

    public CurrencyReaderWriter(String fileName) throws IOException {
        this.currencyMap = this.initializeCurrencyMap(this.currencyMap, fileName);
    }

    public void run(){
        try(Scanner scanner = new Scanner(System.in)){
           while(scanner.hasNext()){
               String raw = scanner.nextLine();
               if(raw.equalsIgnoreCase("quit")){
                   System.exit(0);
               }
               else{
                  this.currencyMap = updateEntryInMap(raw, this.currencyMap);
               }
           }
        }
    }

    private Map<String, Double> initializeCurrencyMap(Map<String, Double> currencyMap, String fileName) throws IOException {
       try(Scanner scanner =  new Scanner(new FileInputStream(fileName))){
           while(scanner.hasNext()){
               updateEntryInMap(scanner.nextLine(), currencyMap);
           }
           return  currencyMap;
       } catch (IOException e) {
           System.out.println("IOException while reading "+fileName);
           throw e;
       }
    }

    private  Map<String, Double> updateEntryInMap(String raw, Map<String, Double> currencyMap){
        try{
            System.out.println("Current: "+raw);
            String[] tokens =  raw.split(" ");
            String currencyCode = tokens[0];
            Double amount = Double.parseDouble(tokens[1]);
            Double newAmount = null;
            Boolean newCurrencyCode = false;
            if(currencyMap.containsKey(currencyCode)){
                newAmount = currencyMap.get(currencyCode) + amount;
            }
            else{
                newAmount = amount;
                newCurrencyCode = true;
            }

            currencyMap.put(currencyCode, newAmount);
            if(newCurrencyCode){
                PrinterThread printerThread = createNewPrinterThread(currencyCode);
            }

            return currencyMap;
        }
        catch(NumberFormatException ex){
            System.out.println("Invalid amount, "+ raw +" entered. Skipping Entry");
            return currencyMap;
        }
    }

    private PrinterThread createNewPrinterThread(String currencyCode){
        PrinterThread printerThread = new PrinterThread(currencyCode);
        this.timer.scheduleAtFixedRate(printerThread, 0, ONE_MINUTE);
        this.printerThreadMap.put(currencyCode, printerThread);
        return printerThread;
    }

    private class PrinterThread extends TimerTask{

        private String currencyCode;

        public PrinterThread(String currencyCode){
            this.currencyCode = currencyCode;
        }

        @Override
        public void run() {
            Double amount = currencyMap.get(this.currencyCode);
            if (amount != 0.0){
                System.out.println(new Date() + ":" + currencyCode + " "+ amount);
            }
        }
    }
}
