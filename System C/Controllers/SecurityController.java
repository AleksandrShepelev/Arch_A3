package Controllers;

import Framework.BaseController;
import Framework.MessageProtocol;
import Framework.TimeMessage;
import InstrumentationPackage.Indicator;

public class SecurityController extends BaseController{

    private static final String CONTROLLER_NAME ="Security controller";
    private static final String INDICATOR_ALARM_OFF_MSG = "S_ALARM OFF";
    private static final String INDICATOR_ALARM_ON_MSG = "S_ALARM ON";private boolean _securityAlarmState;
    private Indicator _ai;
    private SecurityController(String[] args) {
        super(args,MessageProtocol.Type.SECURITY_ALARM);
        _isSilent = true;
    }

    public static void main(String args[])
    {
        SecurityController controller = new SecurityController(args);
        controller.execute();
    }


    @Override
    protected void messageWindowAfterCreate()
    {
        // Put the status indicators under the panel...
        _ai = new Indicator(INDICATOR_ALARM_OFF_MSG, _mw.GetX(), _mw.GetY()+_mw.Height());
    }

    @Override
    protected void unload()
    {
        _ai.dispose();
    }

    @Override
    protected String handleDeviceOutput(TimeMessage msg)
    {
        String msgBody = msg.getMessageText();
        switch (msgBody) {
            case MessageProtocol.Body.SECURITY_ALARM_ON:
                _securityAlarmState = true;
                _mw.WriteMessage("Security alarm is turned on!");
                break;
            case MessageProtocol.Body.SECURITY_ALARM_OFF:
                _securityAlarmState = false;
                _mw.WriteMessage("Security alarm is turned off!");
                break;
            default:
                _mw.WriteMessage("Unexpected message body in Security Controller: " + msgBody);
                break;
        }
        return String.valueOf(msg.getTimestamp());
    }

    @Override
    protected String getName()
    {
        return CONTROLLER_NAME;
    }

    @Override
    protected String getType() {
        return MessageProtocol.Body.REG_SECURITY_CONTROLLER;
    }

    @Override
    protected float getWinPosX()
    {
        return 0.6f;
    }

    @Override
    protected float getWinPosY()
    {
        return 0.0f;
    }

    @Override
    protected void afterHandle() {
        handleAlarmState();
    }

    private void handleAlarmState()
    {
        if (_securityAlarmState) {
            // Set to green, chiller is on
            _ai.SetLampColorAndMessage(INDICATOR_ALARM_ON_MSG, 1);
        } else {
            // Set to black, chiller is off
            _ai.SetLampColorAndMessage(INDICATOR_ALARM_OFF_MSG, 0);
        }
    }
}
