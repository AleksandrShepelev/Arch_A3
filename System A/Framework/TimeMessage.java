/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 08.04.16
 */
package Framework;

import MessagePackage.Message;

public class TimeMessage {

    public static final String BODY_DELIMETER = "_";

    private Message _msg;

    private int _timestamp;
    private String _message;

    public String GetMessage() {
        return _message;
    }

    public int GetMessageId() {
        return _msg.GetMessageId();
    }

    public Message getMessage() {
        return _msg;
    }

    private void SetMessage(String msg) {
        _message = msg;
    }

    public int getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(int ts) {
        _timestamp = ts;
    }

    public TimeMessage(int MsgId, String Text) {
        _msg = new Message(MsgId, Text);
    }

    public TimeMessage(Message msg) {
        String body = msg.GetMessage();
        String[] parts = body.split(BODY_DELIMETER);
        try {
            int ts = Integer.parseInt(parts[0]);
            SetMessage(parts[1]);
            _msg.SetSenderId(msg.GetSenderId());
            setTimestamp(ts);
        } catch (Exception e) {
            System.out.println("Error parsing message body: " + body);
        }
    }

}
