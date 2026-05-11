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
        russianTexts.put("menu.color", "Цвет робота");
        russianTexts.put("menu.color.red", "Красный");
        russianTexts.put("menu.color.blue", "Синий");
        russianTexts.put("menu.color.green", "Зелёный");
        russianTexts.put("menu.color.yellow", "Жёлтый");
        russianTexts.put("menu.color.purple", "Фиолетовый");
        russianTexts.put("menu.targetColor", "Цвет цели");
        russianTexts.put("menu.targetColor.red", "Красный");
        russianTexts.put("menu.targetColor.blue", "Синий");
        russianTexts.put("menu.targetColor.green", "Зелёный");
        russianTexts.put("menu.targetColor.yellow", "Жёлтый");
        russianTexts.put("menu.targetColor.purple", "Фиолетовый");
        russianTexts.put("menu.trailColor", "Цвет следа");
        russianTexts.put("menu.trailColor.red", "Красный");
        russianTexts.put("menu.trailColor.blue", "Синий");
        russianTexts.put("menu.trailColor.green", "Зелёный");
        russianTexts.put("menu.trailColor.yellow", "Жёлтый");
        russianTexts.put("menu.trailColor.purple", "Фиолетовый");
        russianTexts.put("menu.trailColor.gray", "Серый");
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
        englishTexts.put("menu.color", "Robot Color");
        englishTexts.put("menu.color.red", "Red");
        englishTexts.put("menu.color.blue", "Blue");
        englishTexts.put("menu.color.green", "Green");
        englishTexts.put("menu.color.yellow", "Yellow");
        englishTexts.put("menu.color.purple", "Purple");
        englishTexts.put("menu.targetColor", "Target Color");
        englishTexts.put("menu.targetColor.red", "Red");
        englishTexts.put("menu.targetColor.blue", "Blue");
        englishTexts.put("menu.targetColor.green", "Green");
        englishTexts.put("menu.targetColor.yellow", "Yellow");
        englishTexts.put("menu.targetColor.purple", "Purple");
        englishTexts.put("menu.trailColor", "Trail Color");
        englishTexts.put("menu.trailColor.red", "Red");
        englishTexts.put("menu.trailColor.blue", "Blue");
        englishTexts.put("menu.trailColor.green", "Green");
        englishTexts.put("menu.trailColor.yellow", "Yellow");
        englishTexts.put("menu.trailColor.purple", "Purple");
        englishTexts.put("menu.trailColor.gray", "Gray");
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