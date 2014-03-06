package rs485.secondarymonitor.firstjvm.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class LoggingOutStream extends ByteArrayOutputStream {
    private static final CharSequence LINE_SEPARATOR = System.getProperty("line.separator");
	private Logger log;
    private StringBuilder currentMessage;

    public LoggingOutStream(Logger log)
    {
        this.log = log;
        this.currentMessage = new StringBuilder();
    }

    @Override
    public void flush() throws IOException {
        String record;
        super.flush();
        record = this.toString();
        super.reset();

        currentMessage.append(record.replace(LINE_SEPARATOR, "\n"));
        // Are we longer than just the line separator?
        int lastIdx = -1;
        int idx = currentMessage.indexOf("\n",lastIdx+1);
        while (idx >= 0)
        {
        	Level lev = Level.INFO;
        	String msg = currentMessage.substring(lastIdx+1,idx);
        	if(msg.contains("[")) {
	        	String p1 = msg.substring(0, msg.indexOf('['));
	        	if(!Pattern.matches("[a-zA-Z]+", p1)) {
	        		msg = msg.substring(msg.indexOf("["));
		        	String level = msg.substring(1, msg.indexOf(']'));
		        	try {
		        		lev = Level.parse(level);
	        			msg = msg.substring(msg.indexOf("]") + 1);
		        	} catch(Exception e) {}
	        	}
        	}
            log.log(lev, msg);
            lastIdx = idx;
            idx = currentMessage.indexOf("\n",lastIdx+1);
        }
        if (lastIdx >= 0)
        {
            String rem = currentMessage.substring(lastIdx+1);
            currentMessage.setLength(0);
            currentMessage.append(rem);
        }
    }
}