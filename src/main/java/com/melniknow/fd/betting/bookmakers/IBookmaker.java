package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sports;
import com.melniknow.fd.utils.MathUtils;

import java.math.BigDecimal;

public interface IBookmaker {
    void openLink(Bookmaker bookmaker, Parser.BetInfo info);
    void clickOnBetType(Bookmaker bookmaker, Parser.BetInfo info, Sports sport);
    void enterSumAndCheckCf(Bookmaker bookmaker, BigDecimal betCoef, Parser.BetInfo info);
    void placeBet();
}
