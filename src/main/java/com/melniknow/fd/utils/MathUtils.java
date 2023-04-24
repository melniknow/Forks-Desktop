package com.melniknow.fd.utils;

import com.melniknow.fd.core.Parser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public class MathUtils {
    public record CalculatedFork(Parser.Fork fork, BigDecimal betCoef1, BigDecimal betCoef2) { }

    public static CalculatedFork calculate(List<Parser.Fork> forks) {
        if (forks == null || forks.isEmpty()) return null;

        forks.sort(Comparator.comparing(Parser.Fork::income).reversed());
        var fork = forks.get(0);

        var mode = RoundingMode.DOWN;
        var scale = 8;

        var income1 = BigDecimal.ONE.divide(fork.ratio1(), scale, mode);
        var income2 = BigDecimal.ONE.divide(fork.ratio2(), scale, mode);

        var income = income1.add(income2);

        var c1 = BigDecimal.ONE.divide(fork.ratio1(), scale, mode).divide(income, scale, mode);
        var c2 = BigDecimal.ONE.divide(fork.ratio2(), scale, mode).divide(income, scale, mode);

        return new CalculatedFork(fork, c1, c2);
    }
}