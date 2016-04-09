package Monitors;

import Framework.BaseMonitor;
import Framework.MessageProtocol;
import Framework.StoredMessage;
import Framework.TimeMessage;
import InstrumentationPackage.Indicator;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Pack200;

enum SprinklerState {ON, WAIT, OFF };

class SecurityMonitor extends BaseMonitor {

    public static final int AGE_LIMIT = 10;
    private Indicator _secAlarmIndicator;
    private Indicator _fireAlarmIndicator;
    private Indicator _sprinklerAlarmIndicator;

    private boolean _armed = true;
    private boolean _isWindowBroken;
    private boolean _isDoorBroken;
    private boolean _isMotionDetected;
    private String _previousSecurityAlarmState;
    private boolean _previousFireAlarmState;
    private boolean _isOnFire;
    private boolean _isSprinklerOn;
    SprinklerState sprinklerState = SprinklerState.OFF;


    private Timer timer = new Timer("Sprinkler timer");
    TimerTask timerTask;
    private int secToRunSprinkler = 10;

    private HashMap <Long, StoredMessage> _messageStorage = new HashMap<>();

    SecurityMonitor(String[] args) {
        super(args);
    }

    @Override
    protected void messageWindowAfterCreate() {
        _secAlarmIndicator = new Indicator("NO SEC ALARM", _mw.GetX(), _mw.Width(), 1);
        _fireAlarmIndicator = new Indicator("NO FIRE ALARM", _mw.GetX(), _mw.Width(), 1);
        _sprinklerAlarmIndicator = new Indicator("SPRINKLER OFF", _mw.GetX(), _mw.Width(), 0);
    }

    @Override
    protected void unload() {
        _secAlarmIndicator.dispose();
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
                sprinklerState = SprinklerState.WAIT;
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
                turnOnTheSprinkler();
            }
            _isOnFire = true;
        }else if(msg.getMessageText().equalsIgnoreCase(MessageProtocol.Body.NO_FIRE)) {

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
        sendSecurityAlarmState();
        sendFireAlarmState();
        resendNotDeliveredMessages();
    }

    private void sendFireAlarmState() {
        if(_previousFireAlarmState == _isOnFire){
            return;
        }
        _previousFireAlarmState = _isOnFire;
        String body =
                _isOnFire
                    ? MessageProtocol.Body.FIRE_ALARM_ON
                    : MessageProtocol.Body.FIRE_ALARM_OFF;

        if(_isOnFire) {
            _fireAlarmIndicator.SetLampColorAndMessage("FIRE ALARM", 3);
        }else {
            _fireAlarmIndicator.SetLampColorAndMessage("NO FIRE ALARM", 1);
        }
        sendMessage(new TimeMessage(MessageProtocol.Type.FIRE_ALARM, body));
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

        body = isAlarming
                ? MessageProtocol.Body.SECURITY_ALARM_ON
                : MessageProtocol.Body.SECURITY_ALARM_OFF;

        TimeMessage timeMsg = new TimeMessage(MessageProtocol.Type.SECURITY_ALARM, body);

        String displayMsg = !_armed
                                ? "DISARMED"
                                : isSafetyEnsured
                                    ? "NO SEC ALARM"
                                    : "SEC ALARM";
        int color = isAlarming ? 3 : 0;
        if(displayMsg.equalsIgnoreCase(_previousSecurityAlarmState)){
            return;
        }
        _previousSecurityAlarmState = displayMsg;

        _secAlarmIndicator.SetLampColorAndMessage(displayMsg, color);

        _mw.WriteMessage(isAlarming ? "Turning on the security alarm" : "Turning off the security alarm");
        sendMessage(timeMsg);

    }

    private void sendSprinklerStateToController() {
        String body;

        _mw.WriteMessage(_isSprinklerOn ? "Turning on the sprinkler" : "Turning off the sprinkler");

        body = _isSprinklerOn
                ? MessageProtocol.Body.SPRINKLER_ON
                : MessageProtocol.Body.SPRINKLER_OFF;

        TimeMessage timeMsg = new TimeMessage(MessageProtocol.Type.SPRINKLER, body);
        if(_isSprinklerOn) {
            _sprinklerAlarmIndicator.SetLampColorAndMessage("SPRINKLER ON", 1);
        }else {
            _sprinklerAlarmIndicator.SetLampColorAndMessage("SPRINKLER OFF", 0);
        }
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

    void turnOnTheSprinkler() {
        sprinklerState = SprinklerState.ON;
        System.out.println("Sprinkler is turned on. Enter TO to turn off the sprinkler");
        _isSprinklerOn = true;
        sendSprinklerStateToController();
        timerTask.cancel();
        secToRunSprinkler=10;
    }

    void cancelSprinkler() {
        sprinklerState = SprinklerState.OFF;
        timerTask.cancel();
        secToRunSprinkler=10;
    }

    void turnOffTheSprinkler() {
        sprinklerState = SprinklerState.OFF;
        _isSprinklerOn = false;
        sendSprinklerStateToController();
    }


    void setArmedState(boolean armed) {
        _armed = armed;
    }

    boolean isArmed() {
        return _armed;
    }
}