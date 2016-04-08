/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 08.04.16
 */
package Monitors;

import Framework.BaseMonitor;
import Framework.MessageProtocol;
import Framework.TimeMessage;

import java.util.HashMap;
import java.util.Map;

public class MaintenanceMonitor extends BaseMonitor {

    private Map<Long, String> _devices = new HashMap<>();

    protected MaintenanceMonitor(String[] args) {
        super(args);
    }

    @Override
    protected String getName() {
        return null;
    }

    @Override
    protected String getType() {
        return null;
    }

    @Override
    protected float getWinPosX() {
        return 0;
    }

    @Override
    protected float getWinPosY() {
        return 0;
    }

    // not all sensors will handle the messages
    protected void handleMessage(TimeMessage msg) {
        switch (msg.getMessage().GetMessageId()) {
            case MessageProtocol.Type.REGISTER_DEVICE:
                registerDevice(msg);
                break;
        }
    }

    private void registerDevice(TimeMessage msg) {

        //if not in the list yet - register it!
        if (_devices.containsKey(msg.getMessage().GetSenderId())) {
            return;
        }

        try {
            TimeMessage ackMessage = new TimeMessage(MessageProtocol.Type.ACKNOWLEDGEMENT, msg.getMessage().GetMessage());
            _em.SendMessage(ackMessage.getMessage());
            _devices.put()

        } catch (Exception e) {
            _mw.WriteMessage("An error occurred during device register:: " + e);
            e.printStackTrace();
        }
    }
}
