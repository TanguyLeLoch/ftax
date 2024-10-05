package com.natu.ftax.transaction.importer.mexc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Component
public class MexcApi {

    private static final String API_URL = "https://api.mexc.com/api/v3/exchangeInfo?symbol=";
    private static final Logger LOGGER = LoggerFactory.getLogger(MexcApi.class);

    private final RestTemplate restTemplate;

    public MexcApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<String> getTokenFullName(String symbol) {
        String url = API_URL + symbol;
        ExchangeInfoResponse response = restTemplate.getForObject(url,
            ExchangeInfoResponse.class);

        if (response == null || response.symbols() == null || response.symbols()
            .isEmpty()) {
            LOGGER.warn("Cannot fetch token full name for {} from mexc",
                symbol);
            return Optional.empty();
        }
        SymbolInfo symbolInfo = response.symbols().get(0);
        return Optional.of(symbolInfo.fullName());
    }

    public record SymbolInfo(String fullName) {
    }

    public record ExchangeInfoResponse(List<SymbolInfo> symbols) {
    }

}
