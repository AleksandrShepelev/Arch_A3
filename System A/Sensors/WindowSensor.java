package Sensors;

import Framework.BaseSensor;
import Framework.MessageProtocol;
import MessagePackage.Message;

public class WindowSensor extends BaseSensor {

    private boolean _currentWindowState = false; //window state: false - window is not broken, true - window is broken

    /*
    constants for simulation
     */
    private static final float EVENT_PROBABILITY = 0.05f;
    private static final float BREAK_DURATION = 4; //if window is broken, we will repeat this message BREAK_DURATION times + 1
    private int _currentDuration = 0; //counter for repeated messages (See previous option)


    protected WindowSensor(String[] args) {
        super(args);
    }

    @Override
    protected String getName() {
        return "Window sensor";
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
    protected void messageWindowAfterCreate() {
        _mw.WriteMessage("\nInitializing Window Simulation::");

        _mw.WriteMessage("   Initial Window State set :: " + _currentWindowState);
    }

    public static void main(String args[]) {
        WindowSensor sensor = new WindowSensor(args);
        sensor.execute();
    }

    @Override
    protected void beforeHandle() {
        // Here we create the message.
        Message msg = new Message(MessageProtocol.Type.WINDOW, String.valueOf(_currentWindowState));

        // Here we send the message to the message manager.
        try {
            _em.SendMessage(msg);
            _mw.WriteMessage("Current Window State::  " + _currentWindowState);

        } catch (Exception e) {
            System.out.println("Error Posting Window State:: " + e);
        }
    }

    @Override
    protected void afterHandle() {
        //if window is broken then repeat it BREAK_DURATION times and randomize according to probability, otherwise randomize
        if (_currentWindowState == true) {
            if (_currentDuration >= BREAK_DURATION) {
                _currentDuration = 0;
                _currentWindowState = probabilisticCoinToss(EVENT_PROBABILITY);
            } else {
                _currentDuration++;
            }
        } else {
            _currentWindowState = probabilisticCoinToss(EVENT_PROBABILITY);
        }

    }

}
