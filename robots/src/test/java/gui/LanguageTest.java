package gui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LanguageTest {

    @Test
    public void testRussianLanguage() {

        Language.setLanguage(Language.Lang.RUSSIAN);

        assertEquals("Файл", Language.get("menu.file"));
        assertEquals("Новый", Language.get("menu.file.new"));
        assertEquals("Выход", Language.get("menu.file.quit"));
        assertEquals("Язык", Language.get("menu.language"));
        assertEquals("Русский", Language.get("menu.language.russian"));
        assertEquals("Английский", Language.get("menu.language.english"));

        assertEquals("Подтверждение выхода", Language.get("dialog.exit.title"));
        assertEquals("Вы действительно хотите выйти?", Language.get("dialog.exit.message"));
        assertEquals("Да", Language.get("dialog.exit.yes"));
        assertEquals("Нет", Language.get("dialog.exit.no"));

        assertEquals("Робот сброшен в начальную позицию", Language.get("log.robotReset"));
    }

    @Test
    public void testEnglishLanguage() {

        Language.setLanguage(Language.Lang.ENGLISH);

        assertEquals("File", Language.get("menu.file"));
        assertEquals("New", Language.get("menu.file.new"));
        assertEquals("Quit", Language.get("menu.file.quit"));
        assertEquals("Language", Language.get("menu.language"));
        assertEquals("Russian", Language.get("menu.language.russian"));
        assertEquals("English", Language.get("menu.language.english"));

        assertEquals("Confirm Exit", Language.get("dialog.exit.title"));
        assertEquals("Do you really want to exit?", Language.get("dialog.exit.message"));
        assertEquals("Yes", Language.get("dialog.exit.yes"));
        assertEquals("No", Language.get("dialog.exit.no"));

        assertEquals("Robot reset to start position", Language.get("log.robotReset"));
    }

    @Test
    public void testLanguageSwitch() {

        Language.setLanguage(Language.Lang.RUSSIAN);
        assertEquals("Файл", Language.get("menu.file"));

        Language.setLanguage(Language.Lang.ENGLISH);
        assertEquals("File", Language.get("menu.file"));

        Language.setLanguage(Language.Lang.RUSSIAN);
        assertEquals("Файл", Language.get("menu.file"));
    }

    @Test
    public void testInvalidKey() {

        String result = Language.get("non.existent.key");
        assertTrue(result.startsWith("!") && result.endsWith("!"));
    }
}