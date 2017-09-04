package com.amohandas.forex.api.rest;

import com.amohandas.forex.dto.ForexCurrencyDto;
import com.amohandas.forex.service.ForexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/*
 * Rest API Endpoint for Forex-Service
 */

@RestController
@RequestMapping(value = "/forex/v1")
@Api(tags = {"forex"})
public class ForexController extends AbstractRestHandler {

    @Autowired
    private ForexService forexService;

    @RequestMapping(value = "/{exchangeDate}",
            method = RequestMethod.GET,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get latest currency rates for available currencies w.r.t source currency",
            notes = "")
    public
    @ResponseBody
    List<ForexCurrencyDto> getCurrencyRates(
            @ApiParam(value = "Exchange date to be considered. Must be of format:yyyy-MM-dd or 'latest'", required = true)
            @PathVariable("exchangeDate") String exchangeDate,
            @ApiParam(value = "Source currency to be considered.", required = true)
            @RequestParam(value = "sourceCurrency", required = true) String sourceCurrency,
            @ApiParam(value = "Target currencies to be considered in comma separated format. If this parameter is not passed it will retrieve all available target currencies",
                    required = false)
            @RequestParam(value = "targetCurrencies", required = false) String targetCurrencies,
            HttpServletRequest request, HttpServletResponse response) {
        return this.forexService.getForexCurrencies(sourceCurrency, targetCurrencies, exchangeDate);
    }
}
