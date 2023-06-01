package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.bookmakers.ShoulderInfo;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;
import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.MathUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class _188Bet implements IBookmaker {

    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        Context.log.info("Call openLink _188Bet");
        try {
            var driver = Context.screenManager.getScreenForBookmaker(bookmaker);

            driver.manage().window().setSize(new Dimension(1000, 1000));
            driver.get(info.BK_href() + "?c=207&u=https://www.188bedt.com");

            // в этом цикле ждём прогрузки баланса
            for (int i = 0; i < 30; ++i) {
                var balanceButton = new WebDriverWait(driver, Duration.ofSeconds(30)).until(driver1
                    -> driver1.findElement(By.className("print:text-black/80")).getText()); // "print:text-black/80" - принадлежит окошку с балансом
                if (balanceButton != null && !balanceButton.isEmpty()) {
                    balanceButton = balanceButton.substring(4); // откусываем название валюты и пробел
                    balanceButton = balanceButton.replace(",", "");
                    var balance = new BigDecimal(balanceButton);
                    if (!balance.equals(BigDecimal.ZERO)) {
                        break;
                    }
                }
                // спим, ждём прогрузки
                TimeUnit.SECONDS.sleep(1);
            }

            var wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // убираем ебаный контейнер
            wait.until(ExpectedConditions.elementToBeClickable(By.id("lc_container")));
            driver.executeScript("document.getElementById('lc_container').classList.add('hidden');");

        } catch (TimeoutException ignored) {
            throw new RuntimeException("[188bet]: Страница не загружается!");
        } catch (InterruptedException e) {
            throw new RuntimeException("[188bet]: Страница не загрузилась!");
        }
    }

    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport) throws InterruptedException {
        Context.log.info("Call clickOnBetTypeAndReturnBalanceAsRub _188Bet");
        switch (info.BK_bet_type()) {
            case WIN, SET_WIN, HALF_WIN, GAME_WIN ->
                ClickSportsWin.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
            case TOTALS, SET_TOTALS, HALF_TOTALS ->
                ClickSportsTotals.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
            case HANDICAP, SET_HANDICAP, HALF_HANDICAP ->
                ClickSportHandicap.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
            default ->
                throw new RuntimeException("[188bet]: не поддерживаемый bet_type: " + info.BK_bet_type());
        }
        return BetsSupport.getBalance(Context.screenManager.getScreenForBookmaker(bookmaker), Context.betsParams.get(bookmaker).currency());
    }

    @Override
    public void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, BigDecimal sum) {
        Context.log.info("Call enterSumAndCheckCf _188Bet");
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);

        try {
            var currentCf = BetsSupport.getCurrentCf(driver);

            var inaccuracy = new BigDecimal("0.01");
            if (currentCf.subtract(inaccuracy).compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) < 0) {
                throw new RuntimeException("[pinnacle]: коэффициент упал - было %s, стало %s".formatted(info.BK_cf().setScale(2, RoundingMode.DOWN), currentCf));
            }

            if (sum.compareTo(new BigDecimal("50")) < 0) {
                throw new RuntimeException("[188bet]: Минимальная ставка на бетке - 50, а бот пытается поставить: " + sum);
            }

            enterSum(driver, sum);

        } catch (TimeoutException e) {
            BetsSupport.closeBetWindow(driver);
            BetsSupport.clearPreviousBets(driver);
            throw new RuntimeException("[188bet]: не нашлось окошко для вводы суммы ставки");
        } catch (RuntimeException e) {
            BetsSupport.closeBetWindow(driver);
            BetsSupport.clearPreviousBets(driver);
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void enterSum(ChromeDriver driver, BigDecimal sum) {
        var wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        // окошко для ввода суммы
        var enterSnake = wait.until(driver_ -> driver_.findElement(By.cssSelector("[placeholder='Enter Stake']")));
        enterSnake.sendKeys(sum.toPlainString());

        // защита от ебанутого бага
        var factSum = driver.findElement(By.xpath("//input[@placeholder='Enter Stake']")).getAttribute("value");
        if (!factSum.equals(sum.toPlainString())) {
            throw new RuntimeException("[188bet]: Ошибка при вводе суммы в купон");
        }
    }

    // это 4 состояния, в которых мы можем пребывать когда пытаемся поставить ставку
    private static final By byAccepChanges = By.xpath("//h4[text()='Accept Changes']");
    private static final By byPlaceBet = By.xpath("//h4[text()='Place Bet']");
    private static final By byClosedBet = By.xpath("//h4[text()='One or more of your selections are closed for betting.']");
    private static final By bySuccessBet = By.xpath("//h4[text()='Your bet has been successfully placed.']");

    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info, ShoulderInfo shoulderInfo) {
        Context.log.info("Call placeBetAndGetRealCf _188Bet");
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
        try {
            // от сюда мы выёдем только если поставили
            waitLoop(driver, info.BK_name(), info.BK_cf(), shoulderInfo);

            // тут мы забираем коэффициент после успешной ставки
            var realCf = BetsSupport.getCurrentCf(driver);
            // чтобы закрыть окошко мы нажимаем на "ОК", тк крестик после успешной ставки пропадает
            BetsSupport.closeAfterSuccessfulBet(driver);
            System.out.println("[188bet]: Final cf = " + realCf);
            return realCf;
        } catch (RuntimeException e) {
            BetsSupport.closeBetWindow(driver);
            System.out.println("[188bet]: Don`t Place Bet" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException("[188bet]: не смогли уснуть после AccepChanges");
        }
    }

    private void waitLoop(ChromeDriver driver, String bkName, BigDecimal oldCf, ShoulderInfo shoulderInfo) throws InterruptedException {
        // в цикле - жмём на кнопку - пытаемся подождать результата
        for (int i = 0; i < 15; ++i) {
            updateOdds(driver, bkName, oldCf, shoulderInfo);
            if (waitSuccess(driver)) {
                Context.log.info("188BET SUCCESS");
                return;
            }
        }
        throw new RuntimeException("[188bet]: Плечо не может быть проставлено");
    }

    private boolean waitSuccess(ChromeDriver driver) {
        // вечно ждать нельзя!
        for (int i = 0; i < 15; ++i) {
            try {
                System.out.println("[188bet]: Wait....");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                // ждём успеха
                wait.until(driver1 -> driver1.findElement(bySuccessBet));
                return true;
            } catch (Exception e) {
                // Пока мы ждали ничего не произошло? (ставка могла закрыться или поменяться коэфы и тд)
                if (windowContains(driver, byAccepChanges) || windowContains(driver, byPlaceBet) || windowContains(driver, byClosedBet)) {
                    System.out.println("[188bet]: Exit from wait");
                    // ждать нечего - выходим, чтобы снова нажимать на кнопку
                    return false;
                }
            }
        }
        throw new RuntimeException("[188bet]: Плечо не может быть проставлено");
    }

    private void updateOdds(ChromeDriver driver, String bkName, BigDecimal oldCf, ShoulderInfo shoulderInfo) throws InterruptedException {
        // чекаем, мейби коэфы поменялись
        if (clickIfIsClickable(driver, byAccepChanges)) {
            System.out.println("[188bet]: Click byAccepChanges");
            TimeUnit.SECONDS.sleep(1);
        }
        // чекаем, мейби ставка вовсе закрыта
        if (windowContains(driver, byClosedBet)) {
            System.out.println("[188bet]: Событие закрыто");
            throw new RuntimeException("[188bet]: Событие закрыто");
        }
        // если всё ок, то получаем коэф
        var curCf = BetsSupport.getCurrentCf(driver);
        if (curCf.compareTo(oldCf) >= 0) {
            System.out.println("[188bet]: Click Place 1");
            // кликаем на PlaceBet
            clickIfIsClickable(driver, byPlaceBet);
        } else if (!shoulderInfo.isFirst()) {
            var newIncome = MathUtils.calculateIncome(curCf, shoulderInfo.cf1());
            System.out.println("[188bet] newIncome = " + newIncome);
            if (newIncome.compareTo(Context.parserParams.maxMinus()) < 0) {
                throw new RuntimeException("[188bet]: превышен максимальный минус: maxMinus = " + Context.parserParams.maxMinus() + ", а текущий минус = " + newIncome);
            } else {
                System.out.println("[188bet]: Click Place 2");
                // считаем новую сумму
                var _188betCurrency = Context.currencyToRubCourse.get(Context.betsParams.get(BetUtils.getBookmakerByNameInApi(bkName)).currency());
                var otherCurrency2 = Context.currencyToRubCourse.get(Context.betsParams.get(BetUtils.getBookmakerByNameInApi(shoulderInfo.bk1Name())).currency());

                var scale = Context.betsParams.get(BetUtils.getBookmakerByNameInApi(bkName)).accuracy().intValue();

                var newSum = shoulderInfo.cf1()
                    .multiply(shoulderInfo.sum1().multiply(otherCurrency2))
                    .divide(curCf, 2, RoundingMode.DOWN)
                    .divide(_188betCurrency, scale, RoundingMode.DOWN);

                Context.log.info("[188bet]: newSum = " + newSum + " | with cf = " + curCf);

                enterSum(driver, newSum); // TODO handle exceptions

                // кликаем на PlaceBet
                clickIfIsClickable(driver, byPlaceBet);
            }
        } else {
            throw new RuntimeException("[188bet]: Коэфициент на первом плече упал");
        }
    }

    private static boolean clickIfIsClickable(ChromeDriver driver, By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        try {
            var button = wait.until(driver_ -> driver_.findElement(by));
            driver.executeScript("arguments[0].click();", button);
            return true;
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted() || e.getCause() instanceof InterruptedException)
                throw new RuntimeException();
            return false;
        }
    }

    // Узнать содержится ли эелемент на текущей странице
    private static boolean windowContains(ChromeDriver driver, By by) {
        try {
            var wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            wait.until(
                driver1 -> driver1.findElement(by));
            return true;
        } catch (TimeoutException | NoSuchElementException ignored) {
            return false;
        }
    }
}
