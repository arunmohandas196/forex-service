package com.amohandas.forex.client.fixer;

import com.amohandas.forex.domain.ForexCurrency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import com.amohandas.forex.client.ForexClient;
import com.amohandas.forex.client.ForexClientCurrency;

import java.util.List;

/**
 * Created by arunmohandas on 04/09/17.
 */
@Component("fixerClient")
public class FixerClient implements ForexClient{

    @Value("${forex.service.clients.fixer.url}")
    String baseUrl;
    String latestDatePath = "latest";
    String sourceQuery;
    String targetQuery;
    FixerConvertor convertor = new FixerConvertor();

    public FixerClient() {
    }

    public FixerClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<ForexCurrency> getForexFor(String source, String targets, String date) {
        RestTemplate restTemplate = new RestTemplate();
        String datePath = null;
        StringBuilder url = new StringBuilder(baseUrl);
        if(StringUtils.isEmpty(date) || "latest".equals(date)){
            datePath = latestDatePath;
        }else{
            datePath = date;
        }
        url.append("/"+datePath+"?");
        if(!StringUtils.isEmpty(source) ){
            url.append("base="+source+"&");
        }
        if(!StringUtils.isEmpty(targets) ){
            url.append("symbols="+targets+"&");
        }

        FixerCurrency fixerCurrency =  restTemplate.getForObject(url.toString(), FixerCurrency.class);
        return convertor.convert(fixerCurrency);
    }

}
