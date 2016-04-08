package Controllers;

import Framework.BaseController;
import Framework.MessageProtocol;
import Framework.TimeMessage;
import InstrumentationPackage.*;
import MessagePackage.*;

public class TemperatureController extends BaseController
{
    private Indicator _ci;
    private Indicator _hi;
    private boolean _heaterState = false;
    private boolean _chillerState = false;

    protected TemperatureController(String[] args) {
        super(args, MessageProtocol.Type.CONTROL_TEMPERATURE);
    }

    @Override
    protected void messageWindowAfterCreate()
    {
        // Put the status indicators under the panel...
        _ci = new Indicator ("Chiller OFF", _mw.GetX(), _mw.GetY()+_mw.Height());
        _hi = new Indicator ("Heater OFF", _mw.GetX()+(_ci.Width()*2), _mw.GetY()+_mw.Height());
    }

    @Override
    protected void unload()
    {
        _ci.dispose();
        _hi.dispose();
    }

    public static void main(String args[])
	{
        TemperatureController controller = new TemperatureController(args);
        controller.execute();
	}

	private void adjustTemperature(String m)
	{
		// Here we create the message.
		Message msg = new Message(MessageProtocol.Type.ADJUST_TEMPERATURE, m);

		// Here we send the message to the message manager.
		try {
			_em.SendMessage( msg );
		} catch (Exception e) {
			System.out.println("Error Confirming Message:: " + e);
		}
	}

    @Override
    protected String getName() {
        return "Temperature Controller Status Console";
    }

    @Override
    protected float getWinPosX() {
        return 0.0f;
    }

    @Override
    protected float getWinPosY() {
        return 0.3f;
    }

    @Override
    protected String handleDeviceOutput(TimeMessage msg)
    {
        switch (msg.getMessageText().toUpperCase()) {
            case MessageProtocol.Body.HEATER_ON:
                _heaterState = true;
                _mw.WriteMessage("Received heater on message" );
                return MessageProtocol.Body.ACK_HEATER_ON;
            case MessageProtocol.Body.HEATER_OFF:
                _heaterState = false;
                _mw.WriteMessage("Received heater off message" );
                return MessageProtocol.Body.ACK_HEATER_OFF;
            case MessageProtocol.Body.CHILLER_ON:
                _chillerState = true;
                _mw.WriteMessage("Received chiller on message" );
                return MessageProtocol.Body.ACK_CHILLER_ON;
            case MessageProtocol.Body.CHILLER_OFF:
                _chillerState = false;
                _mw.WriteMessage("Received chiller off message" );
                return MessageProtocol.Body.ACK_CHILLER_OFF;
            default:
        }
        adjustTemperature(msg.getMessageText().toUpperCase());
        return null;
    }

    private void handleHeaterState()
    {
        if (_heaterState) {
            // Set to green, heater is on
            _hi.SetLampColorAndMessage("HEATER ON", 1);
        } else {
            // Set to black, heater is off
            _hi.SetLampColorAndMessage("HEATER OFF", 0);
        }
    }

    private void handleChillerState()
    {
        if (_chillerState) {
            // Set to green, chiller is on
            _ci.SetLampColorAndMessage("CHILLER ON", 1);
        } else {
            // Set to black, chiller is off
            _ci.SetLampColorAndMessage("CHILLER OFF", 0);
        }
    }

    @Override
    protected void afterHandle() {
        handleHeaterState();
        handleChillerState();
    }

}