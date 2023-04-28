package com.melniknow.fd.betting.utils._188bet;

import com.melniknow.fd.betting.utils.BetsSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Objects;

public class ClickSportsTotals {
    static public void click(ChromeDriver driver, Parser.BetInfo info) {
        var market = BetsSupport.getMarketByMarketName(driver,
            "//h4[text()='" + info.BK_market_meta().get("marketName").getAsString() + "']");

            market = BetsSupport.getParentByDeep(market, 5);

        var buttons = market.findElements(By.xpath(
                ".//div[text()='" + info.BK_market_meta().get("selectionName").getAsString() + "']"))
            .stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        Objects.requireNonNull(buttons.stream().filter(n -> BetsSupport.getTotalsByStr(n.getText()).equals(info.BK_market_meta().get("line").getAsString())).findAny().orElse(null)).click();
    }
}
