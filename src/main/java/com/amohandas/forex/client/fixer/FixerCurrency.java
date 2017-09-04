package com.amohandas.forex.client.fixer;

import com.amohandas.forex.client.ForexClientCurrency;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by arunmohandas on 04/09/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class FixerCurrency implements ForexClientCurrency {

    private String date;
    private String base;
    private Map<String, BigDecimal> rates;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }

}
