package Monitors;

import Framework.BaseMonitor;
import Framework.MessageProtocol;
import Framework.StoredMessage;
import Framework.TimeMessage;
import InstrumentationPackage.Indicator;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

class SecurityMonitor extends BaseMonitor {

    public static final int AGE_LIMIT = 10;
    private Indicator _ai;
    private boolean _armed = true;
    private boolean _isWindowBroken;
    private boolean _isDoorBroken;
    private boolean _isMotionDetected;
    private boolean _previousAlarmingState;
    private boolean _isOnFire;
    private boolean _isSprinklerOn;

    private Timer timer = new Timer("Sprinkler timer");
    TimerTask timerTask;
    private int secToRunSprinkler = 10;

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
            case MessageProtocol.Type.FIRE:
                handleFire(msg);
                break;
            case MessageProtocol.Type.ACKNOWLEDGEMENT:
                handleAcknowledgement(msg);
                break;
        }
    }

    private void handleFire(TimeMessage msg) {
        if(msg.getMessageText().equalsIgnoreCase(MessageProtocol.Body.FIRE)){
            if (!_isOnFire) {
                System.out.println("Fire alarm detected. Enter Y to confirm sprinkler launch or N to cancel. You have "
                        + secToRunSprinkler + "sec to do it");
               timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        secToRunSprinkler--;
                    }
                };
                timer.scheduleAtFixedRate(timerTask, 50, 1000);//start timer in 0ms to increment  counter
            }

            if (secToRunSprinkler <= 0)
            {
                setSprinkerState(true);
            }
            _isOnFire = true;
        }else {

            if (_isOnFire && _isSprinklerOn) {
                _isSprinklerOn = false;
                sendSprinklerStateToController();
            }
            _isOnFire = false;
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
        sendAlarmStateToController();
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

    private void sendAlarmStateToController() {
        String body;
        boolean isSecured = !_isWindowBroken && !_isDoorBroken && !_isMotionDetected;
        boolean isAlarming = _armed && !isSecured;

        if(isAlarming == _previousAlarmingState){
            return;
        }

        _previousAlarmingState = isAlarming;
        _mw.WriteMessage(isAlarming ? "Turning on the alarm" : "Turning off the alarm");

        body = isAlarming
                ? MessageProtocol.Body.SECURITY_ALARM_ON
                : MessageProtocol.Body.SECURITY_ALARM_OFF;

        TimeMessage timeMsg = new TimeMessage(MessageProtocol.Type.SECURITY_ALARM, body);

        String displayMsg = !_armed
                                ? "DISARMED"
                                : isSecured
                                    ? "NO ALARM"
                                    : "ALARM";
        int color = isAlarming ? 3 : 0;

        _ai.SetLampColorAndMessage(displayMsg, color);

        sendMessage(timeMsg);

    }


    private void sendSprinklerStateToController() {
        String body;

        _mw.WriteMessage(_isSprinklerOn ? "Turning on the sprinkler" : "Turning off the sprinkler");

        body = _isSprinklerOn
                ? MessageProtocol.Body.SPRINKLER_ON
                : MessageProtocol.Body.SPRINKLER_OFF;

        TimeMessage timeMsg = new TimeMessage(MessageProtocol.Type.SPRINKLER, body);

      /*  String displayMsg = !_armed
                ? "DISARMED"
                : isSecured
                ? "NO ALARM"
                : "ALARM";
        int color = isAlarming ? 3 : 0;

        _ai.SetLampColorAndMessage(displayMsg, color);*/

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


    void setSprinkerState(boolean state) {
        _isSprinklerOn = state;
        sendSprinklerStateToController();
        timerTask.cancel();
        secToRunSprinkler=10;
    }


    void setArmedState(boolean armed) {
        _armed = armed;
    }

    boolean isArmed() {
        return _armed;
    }
}