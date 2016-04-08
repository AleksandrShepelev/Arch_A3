package Sensors;

import Framework.BaseSensor;
import Framework.MessageProtocol;
import MessagePackage.*;

public class HumiditySensor extends BaseSensor
{
    private float _driftValue;
    private float _relativeHumidity = getRandomNumber() * 100.00f;

    private boolean _humidifierState = false;         // Hum state: false == off, true == on
    private boolean _dehumidifierState = false;        // Dehum state: false == off, true == on

    protected HumiditySensor(String[] args)
    {
		super(args);
	}

    @Override
    protected void messageWindowAfterCreate()
    {
        _mw.WriteMessage("\nInitializing Humidity Simulation::" );

        if (coinToss()) {
            _driftValue = getRandomNumber() * (float) -1.0;
        } else {
            _driftValue = getRandomNumber();
        } // if

        _mw.WriteMessage("   Initial Humidity Set:: " + _relativeHumidity );
        _mw.WriteMessage("   Drift Value Set:: " + _driftValue );
    }

	public static void main(String args[])
	{
        HumiditySensor sensor = new HumiditySensor(args);
        sensor.execute();
	} // main

	@Override
	protected String getName()
    {
		return "Humidity Sensor";
	}

	@Override
	protected float getWinPosX()
    {
		return 0.5f;
	}

	@Override
	protected float getWinPosY()
    {
		return 0.60f;
	}

    private void handleAdjustHumidity(Message msg)
    {
        switch (msg.GetMessage().toUpperCase()) {
            case MessageProtocol.Body.HUMIDIFIER_ON:
                _humidifierState = true;
                break;
            case MessageProtocol.Body.HUMIDIFIER_OFF:
                _humidifierState = false;
                break;
            case MessageProtocol.Body.DEHUMIDIFIER_ON:
                _dehumidifierState = true;
                break;
            case MessageProtocol.Body.DEHUMIDIFIER_OFF:
                _dehumidifierState = false;
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleMessage(Message msg)
    {
        if (msg.GetMessageId() == MessageProtocol.Type.ADJUST_HUMIDITY) {
            handleAdjustHumidity(msg);
        }
    }

	@Override
	protected void beforeHandle()
    {
        // Here we create the message.
        Message msg = new Message(MessageProtocol.Type.HUMIDITY, String.valueOf(_relativeHumidity));

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg);
            _mw.WriteMessage("Current Humidity::  " + _relativeHumidity + " %");

        } catch (Exception e) {
            System.out.println( "Error Posting Relative Humidity:: " + e );
        }
	}

	@Override
	protected void afterHandle()
    {
        if (_humidifierState) {
            _relativeHumidity += getRandomNumber();
        }

        if (!_humidifierState && !_dehumidifierState) {
            _relativeHumidity += _driftValue;
        }

        if (_dehumidifierState) {
            _relativeHumidity -= getRandomNumber();
        }
	}
} // Humidity Sensor