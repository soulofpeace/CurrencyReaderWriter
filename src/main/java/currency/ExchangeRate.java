package currency;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: soulofpeace
 * Date: 5/5/13
 * Time: 11:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExchangeRate {
    private final Map<String, Double> rates = new HashMap<>();

    public ExchangeRate() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/exchange_rates.json");
        this.initializeRates(inputStream);
    }

    public ExchangeRate(String exchangeRateFile) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(exchangeRateFile);
        this.initializeRates(fileInputStream);

    }

    public boolean isValidCurrencyCode(String currencyCode){
        return this.rates.keySet().contains(currencyCode);
    }

    public Double convert(String currencyCode, Double amount){
        Double rate = rates.get(currencyCode);
        return amount/rate;
    }

    private void initializeRates(InputStream inputStream) throws IOException {
        String raw = new Scanner(inputStream).useDelimiter("\\A").next();
        ObjectMapper mapper = new ObjectMapper();
        Map rawMap = mapper.readValue(raw, Map.class);
        Map<String, Object> rawRates = (Map<String, Object>) rawMap.get("rates");
        for(String currencyCode :  rawRates.keySet()){
            Object rate = rawRates.get(currencyCode);
            if (rate instanceof Double){
                this.rates.put(currencyCode, (Double) rate);
            }
            else{
                this.rates.put(currencyCode, new Double(rate.toString()));
            }

        }
    }
}
