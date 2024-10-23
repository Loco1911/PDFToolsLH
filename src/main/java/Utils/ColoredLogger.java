package Utils;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ColoredLogger {

    public static void setup() {
        AnsiConsole.systemInstall();

        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // Quitar manejadores por defecto
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        // Configurar nuevo manejador con formateador personalizado
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new ColoredFormatter());
        rootLogger.addHandler(consoleHandler);
    }

    private static class ColoredFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            String color;
            if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
                color = Ansi.ansi().fgRed().bold().toString();
            } else if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                color = Ansi.ansi().fgYellow().bold().toString();
            } else if (record.getLevel().intValue() >= Level.INFO.intValue()) {
                color = Ansi.ansi().fgGreen().toString();
            } else {
                color = Ansi.ansi().fgDefault().toString();
            }

            String message = formatMessage(record);
            return String.format("%s%s%s\n", color, message, Ansi.ansi().reset());
        }
    }
}