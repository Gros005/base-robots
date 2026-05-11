package config;

/**
 * Интерфейс для объектов, которые могут сохранять и загружать своё состояние.
 */
public interface Saveable {
    boolean save();
    boolean load();
}