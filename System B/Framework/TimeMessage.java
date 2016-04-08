package Framework;

import MessagePackage.Message;

public class TimeMessage {

    private static final String BODY_DELIMETER = "_";

    private Message _msg;

    private long _timestamp;
    private String _message;

    public String getMessageText() {
        return _message;
    }

    public int GetMessageId() {
        return _msg.GetMessageId();
    }

    public Message getMessage() {
        return _msg;
    }

    private void setMessageText(String msgText) {
        _message = msgText;
    }

    public long getTimestamp() {
        return _timestamp;
    }

    private void setTimestamp(long ts) {
        _timestamp = ts;
    }

    public TimeMessage(int MsgId, String Text) {
        _timestamp = System.currentTimeMillis();
        _msg = new Message(MsgId, String.valueOf(_timestamp) + BODY_DELIMETER + Text);
    }

    public TimeMessage(Message msg) {
        _msg = msg;
        String body = msg.GetMessage();
        String[] parts = body.split(BODY_DELIMETER);

        //if it is a regular message just write it to the body
        if (parts.length < 2) {
            setMessageText(body);
            return;
        }

        try {
            long ts = Long.parseLong(parts[0]);
            setMessageText(body.replaceAll(parts[0] + BODY_DELIMETER, ""));
            _msg.SetSenderId(msg.GetSenderId());
            setTimestamp(ts);
        } catch (Exception e) {
            System.out.println("Error parsing message body: " + body);
            e.printStackTrace();
        }
    }
}
