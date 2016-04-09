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

    // init the list of messages and devices to be registered if we get the
    // message of type we interested in and device is not registered still
    private static final Map<Integer, String> devicesByMessages;
    static
    {
        devicesByMessages = new HashMap<>();
        devicesByMessages.put(MessageProtocol.Type.TEMPERATURE, MessageProtocol.Body.REG_TEMPERATURE);
        devicesByMessages.put(MessageProtocol.Type.HUMIDITY, MessageProtocol.Body.REG_HUMIDITY);
        devicesByMessages.put(MessageProtocol.Type.WINDOW, MessageProtocol.Body.REG_WINDOW);
        devicesByMessages.put(MessageProtocol.Type.DOOR, MessageProtocol.Body.REG_DOOR);
        devicesByMessages.put(MessageProtocol.Type.MOTION, MessageProtocol.Body.REG_MOTION);
        devicesByMessages.put(MessageProtocol.Type.FIRE, MessageProtocol.Body.REG_FIRE);
        devicesByMessages.put(MessageProtocol.Type.ADJUST_TEMPERATURE, MessageProtocol.Body.REG_TEMPERATURE_CONTROLLER);
        devicesByMessages.put(MessageProtocol.Type.ADJUST_HUMIDITY, MessageProtocol.Body.REG_HUMIDITY_CONTROLLER);
    }

    private int _regRequestsCounter = 0;
    private MaintenanceIndicator _mi;

    private MaintenanceMonitor(String[] args) {
        super(args);
    }

    private void sendRegisterRequest(String messageBody) {
        try {
            TimeMessage msg = new TimeMessage(MessageProtocol.Type.REGISTER_DEVICE_REQUEST, messageBody);
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
        return "Maintenance Monitor";
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
            case MessageProtocol.Type.HEART_BEAT:
                registerDevice(msg);
                break;
            default:
                break;
        }
        //check if ID in the database and update the time
        if (_devicesAlive.containsKey(msg.getMessage().GetSenderId())) {
            _devicesAlive.put(msg.getMessage().GetSenderId(), System.currentTimeMillis());
        } else { // is not in the database yet
            //if the message is known in our database but we don't have such device yet - ask him to register
            checkIfRegistered(msg);
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
            row[1] = parts[0]; // name
            if (parts.length > 1) {
                row[2] = parts[1]; // description
            } else {
                row[2] = "No description"; // just in case
            }
            row[3] = String.valueOf(seconds); // Last seconds was online
            rows.add(row);
        }

        _mi.setRows(rows);

        // if we are empty somehow and retry count is not fulfilled then register anyone
        // it is a possible situation if the console was after the sensor was already working
        if (_regRequestsCounter < RETRY_COUNT && _devices.size() < 1) {
            // push everyone to be registered
            sendRegisterRequest(MessageProtocol.Body.REG_EVERYONE);
            _regRequestsCounter++;
        }
    }

    protected void checkIfRegistered(TimeMessage msg) {
        // if no devices at all skip for now because ALL REGISTER will work
        if (_devices.size() < 1 || _regRequestsCounter < 1) {
            return;
        }

        // just return if we are not interested in this device
        if (!devicesByMessages.containsKey(msg.getMessage().GetMessageId())) {
            return;
        }

        // ask him to register
        sendRegisterRequest(devicesByMessages.get(msg.getMessage().GetMessageId()));

    }
}
