package com.amohandas.forex.service;

import com.amohandas.forex.client.ForexClient;
import com.amohandas.forex.domain.ForexCurrency;
import com.amohandas.forex.dto.ForexCurrencyDto;
import com.amohandas.forex.exception.DataFormatException;
import com.amohandas.forex.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ForexService {
    private static final Logger log = LoggerFactory.getLogger(ForexService.class);

    private ForexCurrencyService forexCurrencyService;

    private ForexClient forexClient;

    private String exchangeDateFormat;

    private long expiryDateForCurrencyInSeconds;

    private DateTimeFormatter formatter;

    private Set<String> supportedCurrencies;

    @Autowired
    public ForexService(ForexCurrencyService forexCurrencyService, @Qualifier("fixerClient") ForexClient forexClient,
                        @Value("${forex.service.exchangeDateFormat}") String exchangeDateFormat,
                        @Value("${forex.service.expiryDateForCurrencyInSeconds}") long expiryDateForCurrencyInSeconds,
                        @Value("${forex.service.supportedCurrencies}") String supportedCurrencies) {
        this.forexCurrencyService = forexCurrencyService;
        this.forexClient = forexClient;
        this.exchangeDateFormat = exchangeDateFormat;
        this.expiryDateForCurrencyInSeconds = expiryDateForCurrencyInSeconds;
        this.formatter = DateTimeFormatter.ofPattern(exchangeDateFormat);
        this.supportedCurrencies = new HashSet<String>(Arrays.asList(supportedCurrencies.split(",")));

    }

    public List<ForexCurrencyDto> getForexCurrencies(String source, String targets, String exchangeDate) {
        exchangeDate = validateInputs(source, targets, exchangeDate);

        Set<ForexCurrency> storedValidForexCurrencies = getValidStoredForexCurrencies(source, targets, exchangeDate);


        List<ForexCurrency> forexCurrenciesFromClient = new ArrayList<>();

        if ((StringUtils.isEmpty(targets) && storedValidForexCurrencies.size() == 0)
                || (!StringUtils.isEmpty(targets) && storedValidForexCurrencies.size() < targets.split(",").length)
                ) {
            forexCurrenciesFromClient = retrieveForexCurrenciesFromClient(source, targets, exchangeDate, storedValidForexCurrencies);
            forexCurrenciesFromClient.forEach(forexCurrency -> forexCurrencyService.save(forexCurrency));
        }

        List<ForexCurrency> forexCurrenciesCombined = forexCurrenciesFromClient;
        storedValidForexCurrencies.stream()
                .forEachOrdered(forexCurrenciesCombined::add);
        if(forexCurrenciesCombined.size() == 0){
            throw new ResourceNotFoundException("No exchange rates available currently for passed criteria!");
        }
        return (List<ForexCurrencyDto>) forexCurrenciesCombined.stream()
                .map(forexCurrency -> ForexCurrencyDto.toForexCurrencyDto(forexCurrency, formatter))
                .collect(Collectors.toList());

    }

    public List<ForexCurrency> retrieveForexCurrenciesFromClient(String source, String targets, String exchangeDate, Set<ForexCurrency> storedValidForexCurrencies) {
        List<ForexCurrency> forexCurrenciesFromClient;
        String validTargetsForClientCall = targets;
        if (!StringUtils.isEmpty(targets) && storedValidForexCurrencies.size() > 0) {
            String[] targetArr = targets.split(",");
            Set<String> storedValidTargetCurrencies = storedValidForexCurrencies.stream().map(currency -> currency.getTarget()).collect(Collectors.toSet());
            validTargetsForClientCall = Arrays.stream(targetArr).filter(target -> !storedValidTargetCurrencies.contains(target)).collect(Collectors.joining(","));
            ;


        }
        forexCurrenciesFromClient = forexClient.getForexFor(source, validTargetsForClientCall, exchangeDate);
        return forexCurrenciesFromClient;
    }

    public Set<ForexCurrency> getValidStoredForexCurrencies(String source, String targets, String exchangeDate) {
        List<ForexCurrency> storedForexCurrencies = getStoredForexCurrencies(source, targets, exchangeDate);

        Set<ForexCurrency> storedValidForexCurrencies = validateStoredForexCurrencies(storedForexCurrencies);

        deleteExpiredForexCurrencies(storedForexCurrencies, storedValidForexCurrencies);
        return storedValidForexCurrencies;
    }

    public String validateInputs(String source, String targets, String exchangeDate) {
        validateCurrency(source, "Invalid or not supported source currency: ");
        validateCurrency(targets, "Invalid or not supported target currency: ");
        exchangeDate = validateExchangeDate(exchangeDate);
        return exchangeDate;
    }

    public void deleteExpiredForexCurrencies(List<ForexCurrency> storedForexCurrencies, Set<ForexCurrency> storedValidForexCurrencies) {
        storedForexCurrencies.stream().filter(currency -> !storedValidForexCurrencies.contains(currency))
                .forEach(currency -> forexCurrencyService.deleteForexCurrency(currency.getId()));
    }

    public List<ForexCurrency> getStoredForexCurrencies(String source, String targets, String exchangeDate) {
        List<ForexCurrency> storedForexCurrencies;
        if (StringUtils.isEmpty(targets)) {
            storedForexCurrencies = forexCurrencyService.retrieveForexCurrenciesBySource(source, exchangeDate);
        } else {
            storedForexCurrencies = forexCurrencyService.retrieveForexCurrenciesBySourceAndTarget(source, targets.split(","), exchangeDate);
        }
        return storedForexCurrencies;
    }

    public Set<ForexCurrency> validateStoredForexCurrencies(List<ForexCurrency> forexCurrencies) {
        LocalDateTime currentTime = LocalDateTime.now();
        return forexCurrencies.stream().filter(forexCurrency -> getExchangeDateForToday().equals(forexCurrency.getExchangeDate()) &&
                ChronoUnit.SECONDS.between(forexCurrency.getCreatedTime(),currentTime) < expiryDateForCurrencyInSeconds
        ).collect(Collectors.toSet());
    }

    public String validateExchangeDate(String exchangeDate) {
        if ("latest".equals(exchangeDate)) {
            exchangeDate = getExchangeDateForToday();
        } else {
            try {
                formatter.parse(exchangeDate);
            } catch (DateTimeParseException dtpe) {
                throw new DataFormatException("Invalid date: "+exchangeDate+". Expected format is:" + exchangeDateFormat);
            }
        }
        return exchangeDate;
    }

    public void validateCurrency(String currency, String errorMessage) {
        if (!StringUtils.isEmpty(currency)) {
            for (String current : currency.split(",")) {
                if (!supportedCurrencies.contains(current)) {
                    throw new DataFormatException(errorMessage + current+". Supported currencies:" + supportedCurrencies.toString());
                }
            }
        }
    }

    public String getExchangeDateForToday() {
        return LocalDate.now().format(formatter);
    }


}



