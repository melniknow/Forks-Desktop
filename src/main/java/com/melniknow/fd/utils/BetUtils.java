package com.melniknow.fd.utils;

import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Currency;

import java.math.BigDecimal;
import java.util.Arrays;

public class BetUtils {
    public record BetsParams(String link, Currency currency, BigDecimal minBetSum,
                             BigDecimal maxBetSum,
                             String userAgent, String proxyIp, Integer proxyPort,
                             String proxyLogin, String proxyPassword, String screenSize,
                             String lang, BigDecimal minCf,
                             BigDecimal maxCf, BigDecimal accuracy) { }

    public record CompleteBetsFork(MathUtils.CalculatedFork calculatedFork, String income,
                                   BigDecimal betRubBalance1,
                                   BigDecimal betRubBalance2,
                                   BigDecimal bet1Rub,
                                   BigDecimal bet2Rub) { }

    public record Proxy(String ip, String port, String username, String password) { }

    public static Bookmaker getBookmakerByNameInApi(String nameInApi) {
        return Arrays.stream(Bookmaker.values()).
            filter(n -> n.nameInAPI.equals(nameInApi))
            .findAny()
            .orElse(null);
    }
}
