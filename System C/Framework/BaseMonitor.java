/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 07.04.16
 */
package Framework;

import MessagePackage.Message;

abstract public class BaseMonitor extends BaseComponent implements Runnable {

    private static final int SLEEP_DELAY = 1000;    // The loop delay (1 second)

    protected BaseMonitor(String[] args) {
        super(args);
        _canSign = false; // we don't need additional sign up for monitor
    }

    public void run() {
        execute();
    }

    @Override
    protected String getType() {
        return "MONITOR";
    }

    void halt() {
        _mw.WriteMessage("***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***");

        // Here we create the stop message.
        Message msg;
        msg = new Message(MessageProtocol.Type.TERMINATE, "XXX");

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error sending halt message:: " + e);
        }
    }

    @Override
    protected int getSleepDelay() {
        return SLEEP_DELAY;
    }
}
