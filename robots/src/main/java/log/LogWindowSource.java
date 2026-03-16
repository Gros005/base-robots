package log;

import model.LogEntry;
import model.LogLevel;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *  Хранилище всех логов в приложении.
 */
public class LogWindowSource {
    private final int queueLength;
    private final List<LogEntry> messages;
    private final List<WeakReference<LogChangeListener>> listeners;

    /**
     * @param queueLength максимальное количество сообщений в логе
     */
    public LogWindowSource(int queueLength) {
        this.queueLength = queueLength;
        this.messages = Collections.synchronizedList(new LinkedList<>());
        this.listeners = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Регистрирует слушателя для уведомлений об изменениях
     */
    public void registerListener(LogChangeListener listener) {
        if (listener == null) return;

        synchronized (listeners) {
            for (WeakReference<LogChangeListener> ref : listeners) {
                if (ref.get() == listener) {
                    return; // Уже зарегистрирован
                }
            }
            // Добавляем через WeakReference
            listeners.add(new WeakReference<>(listener));
        }
    }

    /**
     * Удаляет слушателя
     */
    public void unregisterListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.removeIf(ref -> ref.get() == listener || ref.get() == null);
        }
    }

    /**
     * Добавляет новое сообщение в лог
     */
    public void append(LogLevel logLevel, String strMessage) {
        synchronized (messages) {
            messages.add(new LogEntry(logLevel, strMessage));

            // Если превышен лимит - удаляем самое старое сообщение
            if (messages.size() > queueLength) {
                messages.removeFirst();
            }
        }
        notifyListeners();
    }

    /**
     * Уведомляет всех активных слушателей об изменениях
     */
    private void notifyListeners() {
        List<LogChangeListener> activeListeners = new ArrayList<>();

        synchronized (listeners) {
            Iterator<WeakReference<LogChangeListener>> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                WeakReference<LogChangeListener> ref = iterator.next();
                LogChangeListener listener = ref.get();

                if (listener == null) {
                    iterator.remove();
                } else {
                    activeListeners.add(listener);
                }
            }
        }

        for (LogChangeListener listener : activeListeners) {
            try {
                listener.onLogChanged();
            } catch (Exception e) {
                // Логгируем ошибку, но не даем ей прервать уведомление других
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }

    /**
     * Очищает весь лог
     */
    public void clear() {
        synchronized (messages) {
            messages.clear();
        }
        notifyListeners();
    }

    public int size() {
        return messages.size();
    }

    public int getQueueLength() {
        return queueLength;
    }

    /**
     * Возвращает диапазон сообщений
     * @param startFrom начальный индекс
     * @param count количество сообщений
     */
    public Iterable<LogEntry> range(int startFrom, int count) {
        synchronized (messages) {
            if (startFrom < 0 || startFrom >= messages.size()) {
                return Collections.emptyList();
            }
            int indexTo = Math.min(startFrom + count, messages.size());
            return new ArrayList<>(messages.subList(startFrom, indexTo));
        }
    }

    /**
     * @return все сообщения
     */
    public Iterable<LogEntry> all() {
        synchronized (messages) {
            return new ArrayList<>(messages);
        }
    }
}