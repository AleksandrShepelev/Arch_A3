package Controllers;

import Framework.BaseController;
import Framework.MessageProtocol;
import Framework.TimeMessage;
import InstrumentationPackage.Indicator;


public class SprinklerController extends BaseController {

    private static final String INDICATOR_SPRINKLER_OFF_MSG = "SPRINK OFF";
    private static final String INDICATOR_SPRINKLER_ON_MSG = "SPRINK ON";

    private boolean _sprinklerState;
    private Indicator _si;


    protected SprinklerController(String[] args) {
        super(args, MessageProtocol.Type.SPRINKLER);
    }

    @Override
    protected void messageWindowAfterCreate()
    {
        // Put the status indicators under the panel...
        _si = new Indicator(INDICATOR_SPRINKLER_OFF_MSG, _mw.GetX(), _mw.GetY()+_mw.Height());
    }

    public static void main(String args[]) {
        SprinklerController controller = new SprinklerController(args);
        controller.execute();
    }

    @Override
    protected void unload()
    {
        _si.dispose();
    }

    @Override
    protected String handleDeviceOutput(TimeMessage msg)
    {
        String msgBody = msg.getMessageText();
        switch (msgBody) {
            case MessageProtocol.Body.SPRINKLER_ON:
                _sprinklerState = true;
                _mw.WriteMessage("Security alarm is turned on!");
                return MessageProtocol.Body.ACK_SPRINKLER_ON;
            case MessageProtocol.Body.SPRINKLER_OFF:
                _sprinklerState = false;
                _mw.WriteMessage("Security alarm is turned off!");
                return MessageProtocol.Body.ACK_SPRINKLER_OFF;
            default:
                _mw.WriteMessage("Unexpected message body in Sprinkler controller: " + msgBody);
                return null;
        }
    }
    @Override
    protected String getName() {
        return "Sprinkler controller";
    }

    @Override
    protected float getWinPosX() {
        return 0.6f;
    }

    @Override
    protected float getWinPosY() {
        return 0.4f;
    }

    @Override
    protected void afterHandle() {
        handleAlarmState();
    }

    private void handleAlarmState()
    {
        if (_sprinklerState) {
            // Set to green, chiller is on
            _si.SetLampColorAndMessage(INDICATOR_SPRINKLER_OFF_MSG, 1);
        } else {
            // Set to black, chiller is off
            _si.SetLampColorAndMessage(INDICATOR_SPRINKLER_ON_MSG, 0);
        }
    }
}
