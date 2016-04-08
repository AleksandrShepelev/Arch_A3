/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 07.04.16
 */
package Framework;

abstract public class BaseController extends BaseComponent
{
    private int _typeOfMessage;

    protected BaseController(String[] args, int typeOfMessage) {
        super(args);
        _typeOfMessage = typeOfMessage;
    }

    private void sendAcknowledgement(String acknowledgementMsg) {
        TimeMessage msg = new TimeMessage(MessageProtocol.Type.ACKNOWLEDGEMENT, acknowledgementMsg);
        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg.getMessage());
            _mw.WriteMessage("Current "+ getName() +" Send acknowledgement  " + acknowledgementMsg);

        } catch (Exception e) {
            System.out.println("Error Posting acknowledgement "+ getName() +"  Error:: " + e);
        }
    }

    protected abstract String handleDeviceOutput(TimeMessage msg);

    @Override
    public void handleMessage(TimeMessage msg)
    {
        if(msg.GetMessageId() == _typeOfMessage){
            sendAcknowledgement(handleDeviceOutput(msg));
        }
    }
}
