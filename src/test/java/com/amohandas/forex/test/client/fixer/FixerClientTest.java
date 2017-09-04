package com.amohandas.forex.test.client.fixer;


import com.amohandas.forex.client.fixer.FixerClient;
import com.amohandas.forex.client.fixer.FixerCurrency;
import com.amohandas.forex.domain.ForexCurrency;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by arunmohandas on 04/09/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class FixerClientTest {

    FixerClient fixerClient = new FixerClient("http://api.fixer.io/");

    @Test
    public void shouldRetrieveEurToUsdForexForToday()  {
        List<ForexCurrency> forexCurrencies =  fixerClient.getForexFor("EUR", "USD", "");
        Assert.assertTrue(forexCurrencies.size() == 1);
        ForexCurrency eurToUsdForexCurrency = forexCurrencies.get(0);
        Assert.assertTrue(eurToUsdForexCurrency.getSource().equals("EUR"));
        Assert.assertTrue(eurToUsdForexCurrency.getTarget().equals("USD"));
        Assert.assertTrue(eurToUsdForexCurrency.getRate().compareTo(BigDecimal.ONE) > 0);
    }

}
