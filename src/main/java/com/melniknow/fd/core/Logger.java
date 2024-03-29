package com.melniknow.fd.core;

import com.melniknow.fd.ui.panels.impl.ForksPanel;
import com.melniknow.fd.ui.panels.impl.SessionPanel;
import com.melniknow.fd.utils.BetUtils;

public class Logger {
    public static void writeToLogSession(String message) {
        SessionPanel.addMessageToLog(message);
    }
    public static void writePrettyMessageAboutFork(BetUtils.CompleteBetsFork completed) {
        writeToLogSession("Вилка поставлена!");
        ForksPanel.addMessageToForkLog(TelegramSender.getForkAsMessageInTextArea(completed));
    }
}
