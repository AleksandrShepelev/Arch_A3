package Controllers;

import Framework.BaseConsole;
import Framework.BaseController;
import Framework.MessageProtocol;
import Framework.TimeMessage;
import InstrumentationPackage.Indicator;

public class FireController extends BaseController {
    private static final String INDICATOR_ALARM_OFF_MSG = "F_ALARM OFF";
    private static final String INDICATOR_ALARM_ON_MSG = "F_ALARM ON";
    private boolean _fireAlarmState;
    private Indicator _fi;

    protected FireController(String[] args) {
        super(args, MessageProtocol.Type.FIRE_ALARM);
    }

    @Override
    protected void messageWindowAfterCreate()
    {
        // Put the status indicators under the panel...
        _fi = new Indicator(INDICATOR_ALARM_OFF_MSG, _mw.GetX(), _mw.GetY()+_mw.Height());
    }

    @Override
    protected void unload()
    {
        _fi.dispose();
    }

    public static void main(String args[]) {
        FireController controller = new FireController(args);
        controller.execute();
    }

    @Override
    protected String handleDeviceOutput(TimeMessage msg)
    {
        String msgBody = msg.getMessageText();
        switch (msgBody) {
            case MessageProtocol.Body.FIRE_ALARM_ON:
                _fireAlarmState = true;
                _mw.WriteMessage("Security alarm is turned on!");
                return MessageProtocol.Body.ACK_FIRE_ALARM_ON;
            case MessageProtocol.Body.FIRE_ALARM_OFF:
                _fireAlarmState = false;
                _mw.WriteMessage("Security alarm is turned off!");
                return MessageProtocol.Body.ACK_FIRE_ALARM_OFF;
            default:
                _mw.WriteMessage("Unexpected message body in Fire alarm controller: " + msgBody);
                return null;
        }
    }
    @Override
    protected String getName() {
        return "Fire alarm controller";
    }

    @Override
    protected float getWinPosX() {
        return 0.4f;
    }

    @Override
    protected float getWinPosY() {
        return 0.2f;
    }

    @Override
    protected void afterHandle() {
        handleAlarmState();
    }

    private void handleAlarmState()
    {
        if (_fireAlarmState) {
            // Set to green, chiller is on
            _fi.SetLampColorAndMessage(INDICATOR_ALARM_ON_MSG, 3);
        } else {
            // Set to black, chiller is off
            _fi.SetLampColorAndMessage(INDICATOR_ALARM_OFF_MSG, 0);
        }
    }
}
