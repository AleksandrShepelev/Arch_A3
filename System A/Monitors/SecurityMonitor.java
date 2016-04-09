package Monitors;

import Framework.BaseMonitor;
import Framework.MessageProtocol;
import Framework.StoredMessage;
import Framework.TimeMessage;
import InstrumentationPackage.Indicator;

import java.util.HashMap;




class SecurityMonitor extends BaseMonitor {

    public static final int AGE_LIMIT = 10;
    private Indicator _ai;
    private boolean _armed = true;
    private boolean _isWindowBroken;
    private boolean _isDoorBroken;
    private boolean _isMotionDetected;
    private boolean _previousSecurityAlarmState;

    private HashMap <Long, StoredMessage> _messageStorage = new HashMap<>();

    SecurityMonitor(String[] args) {
        super(args);
    }

    @Override
    protected void messageWindowAfterCreate() {
        _ai = new Indicator("NO ALARM", _mw.GetX() + _mw.Width(), 0);
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
        sendSecurityAlarmState();
        resendNotDeliveredMessages();
    }

    private void resendNotDeliveredMessages() {
        _messageStorage.forEach((aLong, storedMessage) -> storedMessage.incAge()); //incrementing age

        HashMap<Long, StoredMessage> clonedObj = (HashMap<Long, StoredMessage>) _messageStorage.clone();

        _messageStorage.forEach((key, message) -> {
            if(message.Age > AGE_LIMIT){
                System.out.println("Lost message repeated " + message.Message.GetMessage());
                sendMessage(new TimeMessage(message.Message));
                clonedObj.remove(key);
            }});

        _messageStorage = clonedObj;
    }

    private void sendSecurityAlarmState() {
        String body;
        boolean isSafetyEnsured = !_isWindowBroken && !_isDoorBroken && !_isMotionDetected;
        boolean isAlarming = _armed && !isSafetyEnsured;

        if(isAlarming == _previousSecurityAlarmState){
            return;
        }

        _previousSecurityAlarmState = isAlarming;
        _mw.WriteMessage(isAlarming ? "Turning on the alarm" : "Turning off the alarm");

        body = isAlarming
                ? MessageProtocol.Body.SECURITY_ALARM_ON
                : MessageProtocol.Body.SECURITY_ALARM_OFF;

        TimeMessage timeMsg = new TimeMessage(MessageProtocol.Type.SECURITY_ALARM, body);

        String displayMsg = !_armed
                                ? "DISARMED"
                                : isSafetyEnsured
                                    ? "NO ALARM"
                                    : "ALARM";
        int color = isAlarming ? 3 : 0;

        _ai.SetLampColorAndMessage(displayMsg, color);

        sendMessage(timeMsg);

    }

    private void sendMessage(TimeMessage timeMsg) {
        try {
            _em.SendMessage(timeMsg.getMessage());
        } catch (Exception e) {
            System.out.println("Error sending Security alarm control message:: " + e);
        }
        _messageStorage.put(timeMsg.getTimestamp(), new StoredMessage(timeMsg.getMessage()));

    }

    void setArmedState(boolean armed) {
        _armed = armed;
    }

    boolean isArmed() {
        return _armed;
    }
}