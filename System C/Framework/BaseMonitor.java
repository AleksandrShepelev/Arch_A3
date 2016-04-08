/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 07.04.16
 */
package Framework;

import MessagePackage.Message;

abstract public class BaseMonitor extends BaseComponent implements Runnable {

    protected BaseMonitor(String[] args) {
        super(args);
        _signed = true; // we don't need additional sign up for monitor
    }

    public void run() {
        execute();
    }

    public void halt() {
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

}
