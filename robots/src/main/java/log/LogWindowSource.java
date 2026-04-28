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
 * Хранилище всех логов в приложении.
 */
public class LogWindowSource {
    private final int queueLength;
    private final List<LogEntry> messages;
    private final List<WeakReference<LogChangeListener>> listeners;

    public LogWindowSource(int queueLength) {
        this.queueLength = queueLength;
        this.messages = Collections.synchronizedList(new LinkedList<>());
        this.listeners = Collections.synchronizedList(new ArrayList<>());
    }

    public void registerListener(LogChangeListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (listeners) {
            for (WeakReference<LogChangeListener> ref : listeners) {
                if (ref.get() == listener) {
                    return;
                }
            }
            listeners.add(new WeakReference<>(listener));
        }
    }

    public void unregisterListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.removeIf(ref -> ref.get() == listener || ref.get() == null);
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        synchronized (messages) {
            messages.add(new LogEntry(logLevel, strMessage));
            if (messages.size() > queueLength) {
                messages.remove(0);
            }
        }
        notifyListeners();
    }

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
            } catch (Exception exception) {
                System.err.println("Error notifying listener: " + exception.getMessage());
            }
        }
    }

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

    public Iterable<LogEntry> range(int startFrom, int count) {
        synchronized (messages) {
            if (startFrom < 0 || startFrom >= messages.size()) {
                return Collections.emptyList();
            }
            int indexTo = Math.min(startFrom + count, messages.size());
            return new ArrayList<>(messages.subList(startFrom, indexTo));
        }
    }

    public Iterable<LogEntry> all() {
        synchronized (messages) {
            return new ArrayList<>(messages);
        }
    }
}
