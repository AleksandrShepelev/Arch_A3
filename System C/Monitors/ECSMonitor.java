/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 08.04.16
 */
package Monitors;

import Framework.BaseMonitor;
import Framework.MessageProtocol;
import InstrumentationPackage.Indicator;
import MessagePackage.Message;

public class ECSMonitor extends BaseMonitor {

    private static final int SLEEP_DELAY = 1000;    // The loop delay (1 second)

    private Indicator _ti;
    private Indicator _hi;

    private float _tempRangeHigh = 100;            // These parameters signify the temperature and humidity ranges in terms
    private float _tempRangeLow = 0;                // of high value and low values. The ECSmonitor will attempt to maintain
    private float _humiRangeHigh = 100;            // this temperature and humidity. Temperatures are in degrees Fahrenheit
    private float _humiRangeLow = 0;                // and humidity is in relative humidity percentage.

    private float _currentTemperature = 0;
    private float _currentHumidity = 0;

    private boolean ON = true;                // Used to turn on heaters, chillers, humidifiers, and dehumidifiers
    private boolean OFF = false;            // Used to turn off heaters, chillers, humidifiers, and dehumidifiers

    protected ECSMonitor(String[] args) {
        super(args);
    }

    @Override
    protected int getSleepDelay() {
        return SLEEP_DELAY;
    }

    @Override
    protected void messageWindowAfterCreate() {
        // Now we create the ECS status and message panel
        // Note that we set up two indicators that are initially yellow. This is
        // because we do not know if the temperature/humidity is high/low.
        // This panel is placed in the upper left hand corner and the status
        // indicators are placed directly to the right, one on top of the other
        _ti = new Indicator("TEMP UNK", _mw.GetX() + _mw.Width(), 0);
        _hi = new Indicator("HUMI UNK", _mw.GetX() + _mw.Width(), _mw.Height() / 2, 2);
    }

    @Override
    protected void unload() {
        _ti.dispose();
        _hi.dispose();
    }

    @Override
    protected String getName() {
        return "ECS Monitoring Console";
    }

    @Override
    protected float getWinPosX() {
        return 0;
    }

    @Override
    protected float getWinPosY() {
        return 0;
    }

    @Override
    protected void handleMessage(Message msg) {
        if (msg.GetMessageId() == MessageProtocol.Type.TEMPERATURE) {
            handleTemperature(msg);
        }
        if (msg.GetMessageId() == MessageProtocol.Type.HUMIDITY) {
            handleHumidity(msg);
        }
    }

    private void handleTemperature(Message msg) {
        try {
            _currentTemperature = Float.valueOf(msg.GetMessage());
        } catch (Exception e) {
            _mw.WriteMessage("Error reading temperature: " + e);
        } // catch
    }

    private void handleHumidity(Message msg) {
        try {
            _currentHumidity = Float.valueOf(msg.GetMessage());
        } catch (Exception e) {
            _mw.WriteMessage("Error reading humidity: " + e);
        } // catch
    }

    @Override
    protected void afterHandle() {
        _mw.WriteMessage("Temperature:: " + _currentTemperature + "F  Humidity:: " + _currentHumidity);

        // Check temperature and effect control as necessary
        controlTemperature();

        // Check humidity and effect control as necessary
        controlHumidity();
    }

    private void controlHumidity() {
        if (_currentHumidity < _humiRangeLow) {
            _hi.SetLampColorAndMessage("HUMI LOW", 3); // humidity is below thresh hold
            humidifier(ON);
            dehumidifier(OFF);
        } else {
            if (_currentHumidity > _humiRangeHigh) { // humidity is above thresh hold
                _hi.SetLampColorAndMessage("HUMI HIGH", 3);
                humidifier(OFF);
                dehumidifier(ON);
            } else {
                _hi.SetLampColorAndMessage("HUMI OK", 1); // humidity is within thresh hold
                humidifier(OFF);
                dehumidifier(OFF);
            } // if
        } // if
    }

    private void controlTemperature() {
        if (_currentTemperature < _tempRangeLow) { // temperature is below thresh hold
            _ti.SetLampColorAndMessage("TEMP LOW", 3);
            heater(ON);
            chiller(OFF);
        } else {
            if (_currentTemperature > _tempRangeHigh) { // temperature is above threshhold
                _ti.SetLampColorAndMessage("TEMP HIGH", 3);
                heater(OFF);
                chiller(ON);
            } else {
                _ti.SetLampColorAndMessage("TEMP OK", 1); // temperature is within threshhold
                heater(OFF);
                chiller(OFF);
            } // if
        } // if
    }

    public void setTemperatureRange(float lowTemp, float highTemp) {
        _tempRangeHigh = highTemp;
        _tempRangeLow = lowTemp;
        _mw.WriteMessage("***Temperature range changed to::" + _tempRangeLow + "F - " + _tempRangeHigh + "F***");

    } // SetTemperatureRange

    public void setHumidityRange(float lowHumi, float highHumi) {
        _humiRangeHigh = highHumi;
        _humiRangeLow = lowHumi;
        _mw.WriteMessage("***Humidity range changed to::" + _humiRangeLow + "% - " + _humiRangeHigh + "%***");
    } // SetTemperatureRange

    private void heater(boolean ON) {
        // Here we create the message.
        Message msg;
        String body;
        if (ON) {
            body = MessageProtocol.Body.HEATER_ON;
        } else {
            body = MessageProtocol.Body.HEATER_OFF;
        }
        msg = new Message(MessageProtocol.Type.CONTROL_TEMPERATURE, body);

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error sending heater control message:: " + e);
        }
    } // Heater

    private void chiller(boolean ON) {
        // Here we create the message.
        Message msg;
        String body;
        if (ON) {
            body = MessageProtocol.Body.CHILLER_ON;
        } else {
            body = MessageProtocol.Body.CHILLER_OFF;
        }
        msg = new Message(MessageProtocol.Type.CONTROL_TEMPERATURE, body);

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error sending chiller control message:: " + e);
        }
    } // Chiller

    private void humidifier(boolean ON) {
        // Here we create the message.
        Message msg;
        String body;
        if (ON) {
            body = MessageProtocol.Body.HUMIDIFIER_ON;
        } else {
            body = MessageProtocol.Body.HUMIDIFIER_OFF;
        }
        msg = new Message(MessageProtocol.Type.CONTROL_HUMIDITY, body);

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error sending humidifier control message:: " + e);
        }
    } // Humidifier

    private void dehumidifier(boolean ON) {
        // Here we create the message.
        Message msg;
        String body;
        if (ON) {
            body = MessageProtocol.Body.DEHUMIDIFIER_ON;
        } else {
            body = MessageProtocol.Body.DEHUMIDIFIER_OFF;
        }
        msg = new Message(MessageProtocol.Type.CONTROL_HUMIDITY, body);

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error sending dehumidifier control message:: " + e);
        }
    } // Dehumidifier

    public float getTempRangeHigh() {
        return _tempRangeHigh;
    }

    public float getTempRangeLow() {
        return _tempRangeLow;
    }

    public float getHumiRangeHigh() {
        return _humiRangeHigh;
    }

    public float getHumiRangeLow() {
        return _humiRangeLow;
    }
}
