package Monitors;

import Framework.BaseMonitor;
import Framework.MessageProtocol;
import Framework.StoredMessage;
import Framework.TimeMessage;
import InstrumentationPackage.Indicator;





class SecurityMonitor extends BaseMonitor {

    private static final String DISARMED = "DISARMED";
    private static final String NO_ALARM = "NO SEC ALARM";
    private static final String ALARM = "SEC ALARM";


    private Indicator _secAlarmIndicator;
    private boolean _armed = true;
    private boolean _isWindowBroken;
    private boolean _isDoorBroken;
    private boolean _isMotionDetected;
    private String _previousSecurityAlarmState;


    SecurityMonitor(String[] args) {
        super(args);
        _previousSecurityAlarmState = NO_ALARM;

    }

    @Override
    protected void messageWindowAfterCreate() {
        _secAlarmIndicator = new Indicator(NO_ALARM,  _mw.GetX() + _mw.Width(), 0);
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
        }
        super.handleMessage(msg);
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
        super.afterHandle();
    }

    private void sendSecurityAlarmState() {
        String body;
        boolean isSafetyEnsured = !_isWindowBroken && !_isDoorBroken && !_isMotionDetected;
        boolean isAlarming = _armed && !isSafetyEnsured;

        String displayMsg = !_armed
                ? DISARMED
                : isSafetyEnsured
                ? NO_ALARM
                : ALARM;
        int color = isAlarming ? 3 : 0;
        if(displayMsg.equalsIgnoreCase(_previousSecurityAlarmState)){
            return;
        }

        body = isAlarming
                ? MessageProtocol.Body.SECURITY_ALARM_ON
                : MessageProtocol.Body.SECURITY_ALARM_OFF;

        TimeMessage timeMsg = new TimeMessage(MessageProtocol.Type.SECURITY_ALARM, body);


        _previousSecurityAlarmState = displayMsg;

        _secAlarmIndicator.SetLampColorAndMessage(displayMsg, color);

        _mw.WriteMessage(isAlarming ? "Turning on the security alarm" : "Turning off the security alarm");
        sendMessage(timeMsg);

    }

    void setArmedState(boolean armed) {
        _armed = armed;
    }

    boolean isArmed() {
        return _armed;
    }
}