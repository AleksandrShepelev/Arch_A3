package Sensors;

import Framework.BaseController;
import Framework.MessageProtocol;
import InstrumentationPackage.*;
import MessagePackage.*;

public class HumidityController extends BaseController {
    private Indicator _di;
    private Indicator _hi;
    private boolean _humidifierState = false;
    private boolean _dehumidifierState = false;

    protected HumidityController(String[] args) {
        super(args);
    }

    @Override
    protected void messageWindowAfterCreate() {
        // Put the status indicators under the panel...
        _di = new Indicator("DeHumid OFF", _mw.GetX(), _mw.GetY() + _mw.Height());
        _hi = new Indicator("Humid OFF", _mw.GetX() + (_di.Width() * 2), _mw.GetY() + _mw.Height());
    }

    @Override
    protected void unload() {
        _di.dispose();
        _hi.dispose();
    }

    public static void main(String args[]) {
        HumidityController controller = new HumidityController(args);
        controller.run();
    } // main

    private void adjustHumidity(String m) {
        // Here we create the message.
        Message msg = new Message(MessageProtocol.Type.ADJUST_HUMIDITY, m);

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg);
        } catch (Exception e) {
            System.out.println("Error Confirming Message:: " + e);
        }
    }

    @Override
    protected String getName() {
        return "Humidity Controller Status Console";
    }

    @Override
    protected float getWinPosX() {
        return 0.0f;
    }

    @Override
    protected float getWinPosY() {
        return 0.60f;
    }

    private void handleControlHumidity(Message msg) {
        switch (msg.GetMessage().toUpperCase()) {
            case MessageProtocol.Body.HUMIDIFIER_ON:
                _humidifierState = true;
                _mw.WriteMessage("Received humidifier on message");
                break;
            case MessageProtocol.Body.HUMIDIFIER_OFF:
                _humidifierState = false;
                _mw.WriteMessage("Received humidifier off message");
                break;
            case MessageProtocol.Body.DEHUMIDIFIER_ON:
                _dehumidifierState = true;
                _mw.WriteMessage("Received dehumidifier on message");
                break;
            case MessageProtocol.Body.DEHUMIDIFIER_OFF:
                _dehumidifierState = false;
                _mw.WriteMessage("Received dehumidifier off message");
                break;
            default:
                break;
        }
        adjustHumidity(msg.GetMessage().toUpperCase());
    }

    @Override
    protected void handleMessage(Message msg) {
        if (msg.GetMessageId() == MessageProtocol.Type.CONTROL_HUMIDITY) {
            handleControlHumidity(msg);
        }
    }

    @Override
    protected void afterHandle() {
        // Now we trend the temperature according to the status of the
        // heater/chiller controller.
        if (_humidifierState) {
            // Set to green, heater is on
            _hi.SetLampColorAndMessage("HUMID ON", 1);
        } else {
            // Set to black, heater is off
            _hi.SetLampColorAndMessage("HUMID OFF", 0);
        }

        if (_dehumidifierState) {
            // Set to green, chiller is on
            _di.SetLampColorAndMessage("DEHUMID ON", 1);
        } else {
            // Set to black, chiller is off
            _di.SetLampColorAndMessage("DEHUMID OFF", 0);
        }
    }

} // HumidityControllers