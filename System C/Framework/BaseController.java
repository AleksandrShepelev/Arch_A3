/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 07.04.16
 */
package Framework;

abstract public class BaseController extends BaseComponent
{
    private int _typeOfMessage;
    protected boolean _isSilent = true;

    private static final int HEART_BEAT_DELAY = 7; // send only every  seconds for performance
    private long _lastHeartBeat = 0;

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

    @Override
    public void beforeHandle() {
        if (!_isSilent) {
            return;
        }

        // don't flood very ofter
        if ((System.currentTimeMillis() - _lastHeartBeat) / 1000 < HEART_BEAT_DELAY) {
            return;
        }

        _lastHeartBeat = System.currentTimeMillis();

        // Here we create the message.
        TimeMessage msg = new TimeMessage(MessageProtocol.Type.HEART_BEAT, getRegistrationMessage());

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg.getMessage());
            _mw.WriteMessage("Sent heart beat...");
        } catch (Exception e) {
            System.out.println("Error Confirming Message:: " + e);
        }
    }
}
