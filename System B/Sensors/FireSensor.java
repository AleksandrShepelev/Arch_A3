package Sensors;

import Framework.MessageProtocol;

public class FireSensor extends EventSensor {

    protected FireSensor (String[] args) {
        super(args, MessageProtocol.Type.FIRE);
    }

    @Override
    protected String getName() {
        return "Fire sensor";
    }

    @Override
    protected float getWinPosX() {
        return 0.5f;
    }

    @Override
    protected float getWinPosY() {
        return 0.2f;
    }


    public static void main(String args[]) {
        FireSensor sensor = new FireSensor(args);
        sensor.execute();
    }
}
