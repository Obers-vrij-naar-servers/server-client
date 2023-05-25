package process;

import config.Configuration;
import core.PromptResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseProcessor {
    protected final static Logger LOGGER = LoggerFactory.getLogger(BaseProcessor.class);
    protected Configuration conf;
    protected PromptResponse promptResponse;

    public boolean done = false;

    public BaseProcessor(PromptResponse promptResponse, Configuration conf) {
        this.conf = conf;
        this.promptResponse = promptResponse;
    }

    public abstract void process() throws Exception;
}
