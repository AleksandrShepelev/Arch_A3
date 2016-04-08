package Sensors;

import Framework.BaseSensor;
import Framework.MessageProtocol;
import Framework.TimeMessage;

public class TemperatureSensor extends BaseSensor
{
    private float _currentTemperature = 50.0f;
    private float _driftValue;

    private boolean _heaterState = false;         // Heater state: false == off, true == on
    private boolean _chillerState = false;        // Chiller state: false == off, true == on

    public static void main(String args[])
    {
        TemperatureSensor sensor = new TemperatureSensor(args);
        sensor.execute();
    }

    protected TemperatureSensor(String[] args)
    {
        super(args);
    }

    @Override
    protected String getName()
    {
        return "Temperature sensor";
    }

    @Override
    protected float getWinPosX()
    {
        return 0.5f;
    }

    @Override
    protected float getWinPosY()
    {
        return 0.3f;
    }

    @Override
    protected void messageWindowAfterCreate()
    {
        _mw.WriteMessage("\nInitializing Temperature Simulation::" );

        if (coinToss()) {
            _driftValue = getRandomNumber() * (float) -1.0;
        } else {
            _driftValue = getRandomNumber();
        } // if

        _mw.WriteMessage("   Initial Temperature Set:: " + _currentTemperature );
        _mw.WriteMessage("   Drift Value Set:: " + _driftValue );
    }

    private void handleAdjustTemperature(TimeMessage msg)
    {
        switch (msg.GetMessage().toUpperCase()) {
            case MessageProtocol.Body.HEATER_ON:
                _heaterState = true;
                break;
            case MessageProtocol.Body.HEATER_OFF:
                _heaterState = false;
                break;
            case MessageProtocol.Body.CHILLER_ON:
                _chillerState = true;
                break;
            case MessageProtocol.Body.CHILLER_OFF:
                _chillerState = false;
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleMessage(TimeMessage msg)
    {
        if (msg.GetMessageId() == MessageProtocol.Type.ADJUST_TEMPERATURE) {
            handleAdjustTemperature(msg);
        }
    }

    @Override
    protected void beforeHandle()
    {
        // Here we create the message.
        TimeMessage msg = new TimeMessage(MessageProtocol.Type.TEMPERATURE, String.valueOf(_currentTemperature));

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg.getMessage());
            _mw.WriteMessage("Current Temperature::  " + _currentTemperature + " F");

        } catch (Exception e) {
            System.out.println( "Error Posting Temperature:: " + e );
        }
    }

    @Override
    protected void afterHandle()
    {
        // Now we trend the temperature according to the status of the
        // heater/chiller controller.
        if (_heaterState) {
            _currentTemperature += getRandomNumber();
        }

        if (!_heaterState && !_chillerState) {
            _currentTemperature += _driftValue;
        }

        if (_chillerState) {
            _currentTemperature -= getRandomNumber();
        }
    }

}