package log;

import model.LogEntry;
import model.LogLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LogWindowSourceTest {

    private LogWindowSource logSource;

    @BeforeEach
    public void setUp() {
        logSource = new LogWindowSource(5);
    }

    @Test
    public void testAppendAndSize() {
        assertEquals(0, logSource.size());

        logSource.append(LogLevel.Debug, "Test message");
        assertEquals(1, logSource.size());

        logSource.append(LogLevel.Info, "Info message");
        logSource.append(LogLevel.Error, "Error message");
        assertEquals(3, logSource.size());
    }

    @Test
    public void testAll() {
        logSource.append(LogLevel.Debug, "Message 1");
        logSource.append(LogLevel.Debug, "Message 2");

        int count = 0;
        for (LogEntry entry : logSource.all()) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testRange() {
        logSource.append(LogLevel.Debug, "Message 1");
        logSource.append(LogLevel.Debug, "Message 2");
        logSource.append(LogLevel.Debug, "Message 3");

        Iterable<LogEntry> range = logSource.range(1, 2);
        int count = 0;
        for (LogEntry entry : range) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testQueueLimit() {
        // Добавляем 7 сообщений (лимит 5)
        for (int i = 0; i < 7; i++) {
            logSource.append(LogLevel.Debug, "Message " + i);
        }

        assertEquals(5, logSource.size());
    }

    @Test
    public void testClear() {
        logSource.append(LogLevel.Debug, "Message 1");
        logSource.append(LogLevel.Debug, "Message 2");
        assertEquals(2, logSource.size());

        logSource.clear();
        assertEquals(0, logSource.size());
    }

    @Test
    public void testGetQueueLength() {
        assertEquals(5, logSource.getQueueLength());
    }
}