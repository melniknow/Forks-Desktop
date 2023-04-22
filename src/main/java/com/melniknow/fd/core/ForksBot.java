package com.melniknow.fd.core;

import com.melniknow.fd.context.Context;
import com.melniknow.fd.oddscorp.Parser;
import com.melniknow.fd.tg.Sender;

import java.util.concurrent.TimeUnit;

public class ForksBot implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);

                var forks = Parser.getForks(Context.parserParams);
                var calculated = MathUtils.calculate(forks);

                if (calculated != null) {
//                    Делаем ставку, используя Selenium, betsParams и настройки букмекера (валюта и тд)
//
//                    WebDriverManager.chromedriver().setup();
//                    new ChromeDriver().get("https://selenium.dev");
//
//                    ...
//
//                    После того как сделали ставку извещаем об этом юзера
//
                    var completed = new BetsUtils.CompleteBetsFork(calculated, "some info");

                    Logger.writeToLogSession(completed.calculatedFork().fork().income().toPlainString());
                    Sender.send(completed);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}