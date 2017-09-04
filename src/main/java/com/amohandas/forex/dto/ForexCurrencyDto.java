package com.amohandas.forex.dto;

import com.amohandas.forex.domain.ForexCurrency;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Created by arunmohandas on 04/09/17.
 */
public class ForexCurrencyDto {

    BigDecimal rate;
    private String source;
    private String target;
    private String exchangeDate;

    public ForexCurrencyDto(String source, String target, String exchangeDate, BigDecimal rate) {
        this.source = source;
        this.target = target;
        this.exchangeDate = exchangeDate;
        this.rate = rate;
    }

    public static ForexCurrencyDto toForexCurrencyDto(ForexCurrency fc, DateTimeFormatter dateFormatter) {
        return new ForexCurrencyDto(fc.getSource(), fc.getTarget(),
                fc.getExchangeDate(), fc.getRate());

    }

    @Override
    public String toString() {
        return "ForexCurrencyDto{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", exchangeDate='" + exchangeDate + '\'' +
                ", rate=" + rate +
                '}';
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(String exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
