package com.amohandas.forex.dao.jpa;

import com.amohandas.forex.domain.ForexCurrency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository can be used to delegate CRUD operations against the data source
 */
public interface ForexCurrencyRepository extends PagingAndSortingRepository<ForexCurrency, Long> {
    ForexCurrency findForexCurrencyBySourceAndTarget(String source, String target);

    Page findAll(Pageable pageable);

    @Query("SELECT f FROM ForexCurrency f WHERE f.source = :source and f.target in :targets and f.exchangeDate = :exchangeDate")
    List<ForexCurrency> retrieveForexCurrenciesBySourceAndTarget(@Param("source") String source,
                                                                 @Param("targets") String[] targets,
                                                                 @Param("exchangeDate") String exchangeDate);

    @Query("SELECT f FROM ForexCurrency f WHERE f.source = :source and f.exchangeDate = :exchangeDate")
    List<ForexCurrency> retrieveForexCurrenciesBySource(@Param("source") String source,
                                                        @Param("exchangeDate") String exchangeDate);
}
