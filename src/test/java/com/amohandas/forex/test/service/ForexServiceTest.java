package com.amohandas.forex.test.service;

import com.amohandas.forex.client.ForexClient;
import com.amohandas.forex.domain.ForexCurrency;
import com.amohandas.forex.exception.DataFormatException;
import com.amohandas.forex.service.ForexCurrencyService;
import com.amohandas.forex.service.ForexService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by arunmohandas on 05/09/17.
 */
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class ForexServiceTest  {
    private ForexCurrencyService forexCurrencyService = Mockito.mock(ForexCurrencyService.class);

    private ForexClient forexClient = Mockito.mock(ForexClient.class);

    private String exchangeDateFormat="yyyy-MM-dd";

    private long expiryDateForCurrencyInSeconds=120;

    private DateTimeFormatter formatter=DateTimeFormatter.ofPattern(exchangeDateFormat);
    private String exchangeDateToday = LocalDate.now().format(formatter);


    private String supportedCurrencies="EUR,USD,JPY";

    ForexService forexService = new ForexService(forexCurrencyService,forexClient,exchangeDateFormat,
            expiryDateForCurrencyInSeconds,supportedCurrencies);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldThrowDataFormatExceptionForInvalidSourceCurrency(){
        exception.expect(DataFormatException.class);
        exception.expectMessage(Matchers.containsString("Invalid or not supported source currency: ASDF."));
        forexService.validateInputs("ASDF","USD","2017-09-04");
    }

    @Test
    public void shouldThrowDataFormatExceptionForInvalidTargetCurrency(){
        exception.expect(DataFormatException.class);
        exception.expectMessage(Matchers.containsString("Invalid or not supported target currency: ASDF."));
        forexService.validateInputs("EUR","USD,ASDF,","2017-09-04");

    }

    @Test
    public void shouldThrowDataFormatExceptionForInvalidExchangeDate(){
        exception.expect(DataFormatException.class);
        exception.expectMessage(Matchers.containsString("Invalid date: 11111-111-111. Expected format is:yyyy-MM-dd"));
        forexService.validateInputs("EUR","USD,","11111-111-111");

    }

    @Test
    public void shouldReturnExchangeRateForValidInputs(){
        String inputExchangeDate = "2017-09-04";
        String exchangeDate = forexService.validateInputs("EUR","USD","2017-09-04");
        Assert.assertEquals(inputExchangeDate,exchangeDate);

        exchangeDate = forexService.validateInputs("EUR","USD","latest");
        Assert.assertEquals(exchangeDateToday,exchangeDate);

    }

    @Test
    public void shouldRetrieveValidStoredForexCurrenciesFromDBIfPresent(){
        String source = "EUR";
        String targets = "USD,JPY";
        ForexCurrency expiredForexCurrency = new ForexCurrency("EUR","USD",exchangeDateToday, BigDecimal.valueOf(1.1905));
        expiredForexCurrency.setId(1);
        expiredForexCurrency.setCreatedTime(LocalDateTime.now().minusSeconds(expiryDateForCurrencyInSeconds));
        expiredForexCurrency.setId(2);
        ForexCurrency validForexCurrency = new ForexCurrency("EUR","JPY",exchangeDateToday, BigDecimal.valueOf(130.6));
        Mockito.when(forexCurrencyService.retrieveForexCurrenciesBySourceAndTarget("EUR",new String[]{"USD","JPY"}, exchangeDateToday))
                .thenReturn(Arrays.asList(expiredForexCurrency,validForexCurrency));

        Set<ForexCurrency> validStoredForexCurrencies = forexService.getValidStoredForexCurrencies(source,targets,exchangeDateToday);

        Assert.assertEquals(1,validStoredForexCurrencies.size());
        Assert.assertTrue(validStoredForexCurrencies.contains(validForexCurrency));
        Mockito.verify(forexCurrencyService, Mockito.times(1)).deleteForexCurrency(expiredForexCurrency.getId());

    }



}
