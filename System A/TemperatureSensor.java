import Framework.BaseSensor;
import Framework.MessageProtocol;
import MessagePackage.*;

class TemperatureSensor extends BaseSensor
{
    private float _currentTemperature = 50.0f;
    private float _driftValue;

    boolean _heaterState = false;         // Heater state: false == off, true == on
    boolean _chillerState = false;        // Chiller state: false == off, true == on

    public static void main(String args[])
    {
        TemperatureSensor sensor = new TemperatureSensor(args);
        sensor.run();
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

    @Override
    protected void handleMessages(Message msg)
    {
        if (msg.GetMessageId() == MessageProtocol.Type.ADJUST_TEMPERATURE) {
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
        } // if
    }

    @Override
    protected void postData()
    {
        // Here we create the message.
        Message msg = new Message(MessageProtocol.Type.TEMPERATURE, String.valueOf(_currentTemperature));

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg);
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
        } // if heater is on

        if (!_heaterState && !_chillerState) {
            _currentTemperature += _driftValue;
        } // if both the heater and chiller are off

        if (_chillerState) {
            _currentTemperature -= getRandomNumber();
        } // if chiller is on
    }

} // TemperatureSensor