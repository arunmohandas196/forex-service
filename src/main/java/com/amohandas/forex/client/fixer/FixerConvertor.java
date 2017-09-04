package com.amohandas.forex.client.fixer;

import com.amohandas.forex.domain.ForexCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by arunmohandas on 04/09/17.
 */
public class FixerConvertor {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<ForexCurrency> convert(FixerCurrency fixerCurrency) {
        List<ForexCurrency> forexCurrencies = new ArrayList<ForexCurrency>();
        ForexCurrency forexCurrency;
        if (fixerCurrency != null && fixerCurrency.getRates().size() > 0) {

            for (Map.Entry<String, BigDecimal> rate : fixerCurrency.getRates().entrySet()) {
                forexCurrencies.add(new ForexCurrency(fixerCurrency.getBase(), rate.getKey(),
                        fixerCurrency.getDate(),
                        rate.getValue()));
            }

        }

        return forexCurrencies;
    }
}
