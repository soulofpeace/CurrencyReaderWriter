package currency;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: soulofpeace
 * Date: 5/5/13
 * Time: 11:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrinterThread implements Runnable{

    private String currencyCode;
    private Map<String, Double> map;
    private ExchangeRate rates;

    public PrinterThread(String currencyCode, Map<String, Double> map, ExchangeRate rates){
        this.currencyCode = currencyCode;
        this.map = map;
        this.rates = rates;
    }

    @Override
    public void run() {
        DecimalFormat df = new DecimalFormat("#.##");
        Double amount = map.get(this.currencyCode);
        if (amount != 0.0){
            Double amountInUsd = rates.convert(this.currencyCode, amount);
            System.out.println(currencyCode + " "+ df.format(amount)+" ( USD "+df.format(amountInUsd)+" )");
        }
    }
}

