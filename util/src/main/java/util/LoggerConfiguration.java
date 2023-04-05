package util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class LoggerConfiguration {

    public static void reloadLogbackConfiguration(InputStream logbackConfigFile) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);


        try (InputStream is = logbackConfigFile) {
            if (is == null) {
                throw new IllegalStateException("Logback configuration file not found");
            }
            configurator.doConfigure(is);
        } catch (JoranException | IOException e) {
            e.printStackTrace();
        }

        StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
    }
}
