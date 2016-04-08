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
            case MessageProtocol.Body.SECURITY_ALARM_ON:
                _securityAlarmOn=true;
                _mw.WriteMessage("Security alarm is turned on!");
                break;
            case MessageProtocol.Body.SECURITY_ALARM_OFF:
                _securityAlarmOn=false;
                _mw.WriteMessage("Security alarm is turned off!");
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
            sendAcknowledgement();
        }
    }

    private String getAcknowledgementMessage() {
        if (_securityAlarmOn) {
            return MessageProtocol.Body.ACK_SECURITY_ALARM_ON;
        }
        else
            return MessageProtocol.Body.ACK_SECURITY_ALARM_OFF;
    }

    private void sendAcknowledgement() {
        Message msg = new Message(MessageProtocol.Type.ACKNOWLEDGEMENT, getAcknowledgementMessage());
        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg);
            _mw.WriteMessage("Current "+ getName() +" Send acknowledgement  " + getAcknowledgementMessage());

        } catch (Exception e) {
            System.out.println("Error Posting acknowledgement "+ getName() +"  Error:: " + e);
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
