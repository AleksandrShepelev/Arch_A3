package Sensors;

import Framework.MessageProtocol;

public class WindowSensor extends EventSensor {

    protected WindowSensor (String[] args) {
        super(args,MessageProtocol.Type.WINDOW);
    }

    @Override
    protected String getName() {
        return "Window sensor";
    }

    @Override
    protected float getWinPosX() {
        return 0.5f;
    }

    @Override
    protected float getWinPosY() {
        return 0.6f;
    }

    @Override
    protected String getType() {
        return MessageProtocol.Body.REG_WINDOW;
    }

    public static void main(String args[]) {
        WindowSensor sensor = new WindowSensor(args);
        sensor.execute();
    }
}
