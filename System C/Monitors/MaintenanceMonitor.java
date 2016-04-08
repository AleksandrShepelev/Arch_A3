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
    private Map<Long, Long> _devicesAlive = new HashMap<>();

    private MaintenanceMonitor(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        MaintenanceMonitor monitor = new MaintenanceMonitor(args);
        monitor.execute();
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
            default:
                //check if ID in the database and update the time
                if (_devicesAlive.containsKey(msg.getMessage().GetSenderId())) {
                    _devicesAlive.put(msg.getMessage().GetSenderId(), System.currentTimeMillis());
                }
        }
    }

    private void registerDevice(TimeMessage msg) {

        //if not in the list yet - register it!
        if (_devices.containsKey(msg.getMessage().GetSenderId())) {
            return;
        }

        String messageBody = msg.getMessageText();
        String[] parts = messageBody.split(TimeMessage.BODY_DELIMETER);
        String deviceType = parts[0];

        try {
            TimeMessage ackMessage = new TimeMessage(MessageProtocol.Type.ACKNOWLEDGEMENT, deviceType);
            _em.SendMessage(ackMessage.getMessage());
            _devices.put(msg.getMessage().GetSenderId(), msg.getMessageText());
            _devicesAlive.put(msg.getMessage().GetSenderId(), System.currentTimeMillis());
            _mw.WriteMessage("Registered new device: " + msg.getMessageText());
        } catch (Exception e) {
            _mw.WriteMessage("An error occurred during device register:: " + e);
            e.printStackTrace();
        }
    }

    @Override
    protected void afterHandle() {

        for(Map.Entry<Long, String> entry : _devices.entrySet()) {
            Long key = entry.getKey();
            String value = entry.getValue();
            long diff = System.currentTimeMillis() - _devicesAlive.get(key);
            float seconds = diff / 1000f;
            _mw.WriteMessage("The device " + value + " was alive " + seconds + " seconds ago");
        }

    }
}
