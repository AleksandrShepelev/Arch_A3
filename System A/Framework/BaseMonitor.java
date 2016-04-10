/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 07.04.16
 */
package Framework;

import MessagePackage.Message;

import java.util.HashMap;
import java.util.Map;

abstract public class BaseMonitor extends BaseComponent implements Runnable {

    private static final int SLEEP_DELAY = 1000;    // The loop delay (1 second)
    private Map<Long, StoredMessage> _messageStorage = new HashMap<>();
    private static final int AGE_LIMIT = 10; // max age for each stored message with acknowledgement

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
            e.printStackTrace();
        }
    }

    @Override
    protected void handleMessage(TimeMessage msg) {
        if (msg.GetMessageId() == MessageProtocol.Type.ACKNOWLEDGEMENT) {
            handleAcknowledgement(msg);
        }
    }

    private void handleAcknowledgement(TimeMessage msg) {
        long key = Long.parseLong(msg.getMessageText());
        _messageStorage.remove(key);
    }

    @Override
    protected void afterHandle() {
        resendNotDeliveredMessages();
    }

    private void resendNotDeliveredMessages() {
        _messageStorage.forEach((aLong, storedMessage) -> storedMessage.incAge()); //incrementing age

        Map<Long, StoredMessage> clonedObj = new HashMap<>(_messageStorage);//HashMap<Long, StoredMessage>) _messageStorage.clone();

        _messageStorage.forEach((key, message) -> {
            if (message.Age > AGE_LIMIT) {
                _mw.WriteMessage("Lost message repeated " + message.Message.GetMessage());
                sendMessage(new TimeMessage(message.Message));
                clonedObj.remove(key);
            }
        });

        _messageStorage = clonedObj;
    }

    private void putInStorage(Long key, Message msg) {
        _messageStorage.put(key, new StoredMessage(msg));
    }

    protected void sendMessage(TimeMessage timeMsg) {
        try {
            _em.SendMessage(timeMsg.getMessage());
        } catch (Exception e) {
            System.out.println("Error sending message: " + e);
        }

        putInStorage(timeMsg.getTimestamp(), timeMsg.getMessage());
    }

    @Override
    protected int getSleepDelay() {
        return SLEEP_DELAY;
    }
}
