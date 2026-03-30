package gui;

import java.util.HashMap;
import java.util.Map;

public class Language {

    public enum Lang {
        ENGLISH,
        RUSSIAN
    }

    private static Lang currentLanguage = Lang.RUSSIAN; //по умолчанию русский

    private static final Map<Lang, Map<String, String>> texts = new HashMap<>();

    static {
        // Русский язык
        Map<String, String> russianTexts = new HashMap<>();
        russianTexts.put("app.title", "Управление роботом");
        russianTexts.put("menu.file", "Файл");
        russianTexts.put("menu.file.new", "Новый");
        russianTexts.put("menu.file.quit", "Выход");
        russianTexts.put("menu.view", "Вид");
        russianTexts.put("menu.view.lookAndFeel", "Режим отображения");
        russianTexts.put("menu.view.lookAndFeel.system", "Системная схема");
        russianTexts.put("menu.view.lookAndFeel.cross", "Универсальная схема");
        russianTexts.put("menu.tests", "Тесты");
        russianTexts.put("menu.tests.addLog", "Сообщение в лог");
        russianTexts.put("menu.language", "Язык");
        russianTexts.put("menu.language.russian", "Русский");
        russianTexts.put("menu.language.english", "Английский");
        russianTexts.put("dialog.exit.title", "Подтверждение выхода");
        russianTexts.put("dialog.exit.message", "Вы действительно хотите выйти?");
        russianTexts.put("dialog.exit.yes", "Да");
        russianTexts.put("dialog.exit.no", "Нет");
        russianTexts.put("dialog.new.title", "Сброс робота");
        russianTexts.put("dialog.new.message", "Робот возвращен в начальную позицию (100, 100)");
        russianTexts.put("log.robotReset", "Робот сброшен в начальную позицию");
        russianTexts.put("log.appClosing", "Приложение завершает работу");
        russianTexts.put("log.exitCancelled", "Выход отменен пользователем");
        texts.put(Lang.RUSSIAN, russianTexts);

        // Английский язык
        Map<String, String> englishTexts = new HashMap<>();
        englishTexts.put("app.title", "Robot Control");
        englishTexts.put("menu.file", "File");
        englishTexts.put("menu.file.new", "New");
        englishTexts.put("menu.file.quit", "Quit");
        englishTexts.put("menu.view", "View");
        englishTexts.put("menu.view.lookAndFeel", "Look and Feel");
        englishTexts.put("menu.view.lookAndFeel.system", "System Scheme");
        englishTexts.put("menu.view.lookAndFeel.cross", "Cross-platform Scheme");
        englishTexts.put("menu.tests", "Tests");
        englishTexts.put("menu.tests.addLog", "Add log message");
        englishTexts.put("menu.language", "Language");
        englishTexts.put("menu.language.russian", "Russian");
        englishTexts.put("menu.language.english", "English");
        englishTexts.put("dialog.exit.title", "Confirm Exit");
        englishTexts.put("dialog.exit.message", "Do you really want to exit?");
        englishTexts.put("dialog.exit.yes", "Yes");
        englishTexts.put("dialog.exit.no", "No");
        englishTexts.put("dialog.new.title", "Robot Reset");
        englishTexts.put("dialog.new.message", "Robot returned to start position (100, 100)");
        englishTexts.put("log.robotReset", "Robot reset to start position");
        englishTexts.put("log.appClosing", "Application is shutting down");
        englishTexts.put("log.exitCancelled", "Exit cancelled by user");
        texts.put(Lang.ENGLISH, englishTexts);
    }

    public static String get(String key) {
        Map<String, String> currentTexts = texts.get(currentLanguage);
        if (currentTexts == null || !currentTexts.containsKey(key)) { return "!" + key + "!"; }
        return currentTexts.get(key);
    }

    public static void setLanguage(Lang lang) {currentLanguage = lang;}
    public static Lang getCurrentLanguage() {return currentLanguage;}
}