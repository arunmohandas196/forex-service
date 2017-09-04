package com.amohandas.forex.client;

import com.amohandas.forex.client.fixer.FixerCurrency;
import com.amohandas.forex.domain.ForexCurrency;

import java.util.List;

/**
 * Created by arunmohandas on 04/09/17.
 */
public interface ForexClient {

    public List<ForexCurrency> getForexFor(String source, String targets, String date) ;

}
