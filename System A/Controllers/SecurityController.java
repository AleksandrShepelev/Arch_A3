package Controllers;

import Framework.BaseController;
import Framework.MessageProtocol;
import MessagePackage.Message;

public class SecurityController extends BaseController{

    private static final String CONTROLLER_NAME ="Security controller";
    private Boolean _securityAlarmOn = false;

    protected SecurityController(String[] args) {
        super(args);
    }

    public void main (String args[])
    {
        SecurityController controller = new SecurityController(args);
        controller.execute();
    }

    private void handleControlSecurityAlarm(Message msg)
    {
        String msgBody = msg.GetMessage();
        switch (msgBody) {
            case MessageProtocol.Body.WINDOW_BROKEN:
                _securityAlarmOn=true;
                _mw.WriteMessage("ALARM: Window is broken!");
                break;
            case MessageProtocol.Body.WINDOW_OK:
                _securityAlarmOn=false;
                break;
            case MessageProtocol.Body.DOOR_BROKEN:
                _securityAlarmOn=true;
                _mw.WriteMessage("ALARM: Door is broken!");
                break;
            case MessageProtocol.Body.DOOR_OK:
                _securityAlarmOn=false;
                break;
            case MessageProtocol.Body.MOTION_DETECTED:
                _securityAlarmOn=true;
                _mw.WriteMessage("ALARM: Motion detected!");
                break;
            case MessageProtocol.Body.MOTION_OK:
                _securityAlarmOn=false;
                break;
            default:
                _mw.WriteMessage("Unexpected message body in Security Controller: " + msgBody);
                break;
        }
    }

    @Override
    public void handleMessage(Message msg)
    {
        if(msg.GetMessageId() == MessageProtocol.Type.SECURITY_ALARM){
            handleControlSecurityAlarm(msg);
            //senAcknowledgement();
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
