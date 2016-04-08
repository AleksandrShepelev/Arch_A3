/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 08.04.16
 */
package Monitors;

import Framework.BaseMonitor;
import Framework.MessageProtocol;
import Framework.TimeMessage;
import InstrumentationPackage.MaintenanceIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaintenanceMonitor extends BaseMonitor {

    private Map<Long, String> _devices = new HashMap<>();
    private Map<Long, Long> _devicesAlive = new HashMap<>();

    private int _regRequestsCounter = 0;
    private MaintenanceIndicator _mi;

    private MaintenanceMonitor(String[] args) {
        super(args);
    }

    private void sendRegisterRequest() {
        try {
            TimeMessage msg = new TimeMessage(MessageProtocol.Type.REGISTER_DEVICE_REQUEST, "");
            _em.SendMessage(msg.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            MaintenanceMonitor monitor = new MaintenanceMonitor(args);
            monitor.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unload() {
        _mi.dispose();
    }

    @Override
    protected void messageWindowAfterCreate() {
        _mi = new MaintenanceIndicator();
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

        List<String[]> rows = new ArrayList<>();

        for(Map.Entry<Long, String> entry : _devices.entrySet()) {
            String[] row = new String[MaintenanceIndicator.columnNames.length];
            Long key = entry.getKey();
            String value = entry.getValue();
            long diff = System.currentTimeMillis() - _devicesAlive.get(key);
            float seconds = diff / 1000f;
            _mw.WriteMessage("The device " + value + " was alive " + seconds + " seconds ago");
            String[] parts = value.split(TimeMessage.BODY_DELIMETER);
            row[0] = String.valueOf(key); // ID
            row[1] = parts[0]; // Type
            row[2] = parts[1]; // Name
            row[3] = String.valueOf(seconds); // Last seconds was online
            rows.add(row);
        }

        _mi.setRows(rows);

        // if we are empty somehow and retry count is not fulfilled then register anyone
        if (_regRequestsCounter < RETRY_COUNT && _devices.size() < 1) {
            // push everyone to be registered
            sendRegisterRequest();
            _regRequestsCounter++;
        }

    }
}
