package Monitors;

import Framework.BaseMonitor;
import Framework.MessageProtocol;
import Framework.StoredMessage;
import Framework.TimeMessage;
import InstrumentationPackage.Indicator;
import MessagePackage.Message;

import java.util.HashMap;

class SecurityMonitor extends BaseMonitor {

    private Indicator _ai;

    private boolean _armed = true;
    private boolean _isWindowBroken;
    private boolean _isDoorBroken;
    private boolean _isMotionDetected;
    private HashMap <Long, StoredMessage> _messageStorage = new HashMap<>();

    SecurityMonitor(String[] args) {
        super(args);
    }

    @Override
    protected void messageWindowAfterCreate() {
        _ai = new Indicator("ARMED", _mw.GetX() + _mw.Width(), 0);
    }

    @Override
    protected void unload() {
        _ai.dispose();
    }

    @Override
    protected String getName() {
        return "Security monitor";
    }

    @Override
    protected float getWinPosX() {
        return 0.1f;
    }

    @Override
    protected float getWinPosY() {
        return 0.1f;
    }

    @Override
    public void handleMessage(TimeMessage msg) {
        switch (msg.GetMessageId()) {
            case MessageProtocol.Type.WINDOW:
                handleWindow(msg);
                break;
            case MessageProtocol.Type.DOOR:
                handleDoor(msg);
                break;
            case MessageProtocol.Type.MOTION:
                handleMotion(msg);
                break;
            case MessageProtocol.Type.ACKNOWLEDGEMENT:
                handleAcknowledgement(msg);
        }
    }

    private void handleAcknowledgement(TimeMessage msg) {
        long key = Long.parseLong(msg.getMessageText());
        _messageStorage.remove(key);

    }

    private void handleWindow(TimeMessage msg) {
        if (msg.getMessageText().equalsIgnoreCase(MessageProtocol.Body.WINDOW_BROKEN)) {
            _isWindowBroken = true;
        }

        if (msg.getMessageText().equalsIgnoreCase(MessageProtocol.Body.WINDOW_OK)) {
            _isWindowBroken = false;
        }
    }

    private void handleDoor(TimeMessage msg) {
        if (msg.getMessageText().equalsIgnoreCase(MessageProtocol.Body.DOOR_BROKEN)) {
            _isDoorBroken = true;
        }

        if (msg.getMessageText().equalsIgnoreCase(MessageProtocol.Body.DOOR_OK)) {
            _isDoorBroken = false;
        }
    }

    private void handleMotion(TimeMessage msg) {
        if (msg.getMessageText().equalsIgnoreCase(MessageProtocol.Body.MOTION_DETECTED)) {
            _isMotionDetected = true;
        }

        if (msg.getMessageText().equalsIgnoreCase(MessageProtocol.Body.MOTION_OK)) {
            _isMotionDetected = false;
        }
    }

    @Override
    protected void afterHandle() {
        sendAlarmState();
        updateMessagesAge();
    }

    private void updateMessagesAge() {
        _messageStorage.forEach((aLong, message) -> {
            if(message.Age > 10){
                sendMessage(new TimeMessage(message.Message));
            }
            message.incAge();});
    }

    private void sendAlarmState() {
        String body;
        boolean isSecured = !_isWindowBroken && !_isDoorBroken && !_isMotionDetected;
        boolean isAlarming = _armed && !isSecured;
        _mw.WriteMessage(isAlarming ? "Turning on the alarm" : "Turning off the alarm");

        body = isAlarming
                ? MessageProtocol.Body.SECURITY_ALARM_ON
                : MessageProtocol.Body.SECURITY_ALARM_OFF;
        Message origMsg = new Message(MessageProtocol.Type.SECURITY_ALARM, body);
        TimeMessage timeMsg = new TimeMessage(origMsg);
        String displayMsg = !_armed
                                ? "DISARMED"
                                : isSecured
                                    ? "NO ALARM"
                                    : "ALARM";

        _ai.SetLampColorAndMessage(displayMsg, 3);

        sendMessage(timeMsg);

        _messageStorage.put(timeMsg.getTimestamp(), new StoredMessage(origMsg));
    }

    private void sendMessage(TimeMessage timeMsg) {
        try {
            _em.SendMessage(timeMsg.getMessage());
        } catch (Exception e) {
            System.out.println("Error sending Security alarm control message:: " + e);
        }
    }

    void setArmedState(boolean armed) {
        _armed = armed;
    }

    boolean isArmed() {
        return _armed;
    }
}