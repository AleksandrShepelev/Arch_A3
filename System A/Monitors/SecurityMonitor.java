package Monitors;

import Framework.BaseMonitor;
import Framework.MessageProtocol;
import Framework.TimeMessage;
import InstrumentationPackage.Indicator;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;

public class SecurityMonitor extends BaseMonitor{

    private static final int SLEEP_DELAY = 1000;
    private boolean _armed = true;
    private Indicator _ai;
    private Boolean _isWindowBroken;
    private boolean _isDoorBroken;
    private boolean _isMotionDetected;

    protected SecurityMonitor(String[] args) {
        super(args);
    }

    @Override
    protected int getSleepDelay() {
        return SLEEP_DELAY;
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
        return 0.5f;
    }

    @Override
    protected float getWinPosY() {
        return 0.2f;
    }
    @Override
    public void handleMessage(TimeMessage msg)
    {
        switch (msg.GetMessageId()){
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
    }



    private void handleWindow(TimeMessage msg)
    {
        if(msg.getMessageText().equals(MessageProtocol.Body.WINDOW_BROKEN)) {
            _isWindowBroken = true;
        }

        if(msg.getMessageText().equals(MessageProtocol.Body.WINDOW_OK)) {
            _isWindowBroken = false;
        }
    }

    private void handleDoor(TimeMessage msg) {
        if(msg.getMessageText().equals(MessageProtocol.Body.DOOR_BROKEN)){
            _isDoorBroken = true;
        }

        if(msg.getMessageText().equals(MessageProtocol.Body.DOOR_OK)){
            _isDoorBroken = false;
        }
    }

    private void handleMotion(TimeMessage msg) {
        if(msg.getMessageText().equals(MessageProtocol.Body.MOTION_DETECTED)){
            _isMotionDetected = true;
        }

        if(msg.getMessageText().equals(MessageProtocol.Body.MOTION_OK)){
            _isMotionDetected = false;
        }
    }

    protected void afterHandle()
    {
        sendAlarmState();
    }

    private void sendAlarmState()
    {
        TimeMessage msg;
        String body;

        body = _isWindowBroken || _isDoorBroken || _isMotionDetected
                ? MessageProtocol.Body.WDM_ALARM_ON
                : MessageProtocol.Body.WDM_ALARM_OFF;
        msg = new TimeMessage(MessageProtocol.Type.SECURITY_ALARM, body);

        try {
            _em.SendMessage(msg.getMessage());
        } catch (Exception e) {
            System.out.println("Error sending heater control message:: " + e);
        }
    }

    public void setArmedState(boolean armed) {
        _armed = armed;
    }

    public boolean isArmed() {
        return _armed;
    }
}
