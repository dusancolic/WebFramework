package example.tester;

import example.annotations.Autowired;
import example.annotations.Qualifier;

public class Test {

    @Autowired(verbose = true)
    @Qualifier(MyLogger.class)
    private Logger logger;

    public void log() {
        logger.log("dusan");
    }

}
