package com.amohandas.forex.service;

import com.amohandas.forex.dao.jpa.ForexCurrencyRepository;
import com.amohandas.forex.domain.ForexCurrency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForexCurrencyService {

    private static final Logger log = LoggerFactory.getLogger(ForexCurrencyService.class);

    @Autowired
    private ForexCurrencyRepository forexCurrencyRepository;

    public ForexCurrencyService() {
    }

    public ForexCurrency save(ForexCurrency forexCurrency) {
        return forexCurrencyRepository.save(forexCurrency);
    }

    public ForexCurrency getForexCurrency(long id) {
        return forexCurrencyRepository.findOne(id);
    }

    public void updateForexCurrency(ForexCurrency forexCurrency) {
        forexCurrencyRepository.save(forexCurrency);
    }

    public void deleteForexCurrency(Long id) {
        forexCurrencyRepository.delete(id);
    }

    public List<ForexCurrency> retrieveForexCurrenciesBySourceAndTarget(String source,
                                                                        String[] targets,
                                                                        String exchangeDate) {
        return forexCurrencyRepository.retrieveForexCurrenciesBySourceAndTarget(source, targets, exchangeDate);
    }

    public List<ForexCurrency> retrieveForexCurrenciesBySource(String source,
                                                               String date) {
        return forexCurrencyRepository.retrieveForexCurrenciesBySource(source, date);
    }

}
