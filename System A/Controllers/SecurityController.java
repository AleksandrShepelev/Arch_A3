package Controllers;

import Framework.BaseController;
import Framework.MessageProtocol;
import Framework.TimeMessage;

public class SecurityController extends BaseController{

    private static final String CONTROLLER_NAME ="Security controller";

    private SecurityController(String[] args) {
        super(args,MessageProtocol.Type.SECURITY_ALARM);
    }

    public void main (String args[])
    {
        SecurityController controller = new SecurityController(args);
        controller.execute();
    }

    @Override
    protected String handleDeviceOutput(TimeMessage msg)
    {
        String msgBody = msg.getMessageText();
        switch (msgBody) {
            case MessageProtocol.Body.SECURITY_ALARM_ON:
                _mw.WriteMessage("Security alarm is turned on!");
                return MessageProtocol.Body.ACK_SECURITY_ALARM_ON;
            case MessageProtocol.Body.SECURITY_ALARM_OFF:
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
}
