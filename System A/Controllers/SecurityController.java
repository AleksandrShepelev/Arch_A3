package Controllers;

import Framework.BaseController;
import Framework.MessageProtocol;
import Framework.TimeMessage;
import InstrumentationPackage.Indicator;

public class SecurityController extends BaseController{

    private static final String CONTROLLER_NAME ="Security controller";
    private boolean _securityAlarmState;
    private Indicator _ai;
    private SecurityController(String[] args) {
        super(args,MessageProtocol.Type.SECURITY_ALARM);
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
        _ai = new Indicator("SEC ALARM OFF", _mw.GetX(), _mw.GetY()+_mw.Height());
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
                return MessageProtocol.Body.ACK_SECURITY_ALARM_ON;
            case MessageProtocol.Body.SECURITY_ALARM_OFF:
                _securityAlarmState = false;
                _mw.WriteMessage("Security alarm is turned off!");
                return MessageProtocol.Body.ACK_SECURITY_ALARM_OFF;
            default:
                _mw.WriteMessage("Unexpected message body in Security Controller: " + msgBody);
                return null;
        }
    }

    @Override
    protected String getName()
    {
        return CONTROLLER_NAME;
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
            _ai.SetLampColorAndMessage("SEC ALARM ON", 1);
        } else {
            // Set to black, chiller is off
            _ai.SetLampColorAndMessage("SEC ALARM OFF", 0);
        }
    }
}
