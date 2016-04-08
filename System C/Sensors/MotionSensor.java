package Sensors;

import Framework.MessageProtocol;

public class MotionSensor extends EventSensor {

    protected MotionSensor (String[] args) {
        super(args,MessageProtocol.Type.MOTION);
    }

    @Override
    protected String getName() {
        return "Motion sensor";
    }

    @Override
    protected float getWinPosX() {
        return 0.5f;
    }

    @Override
    protected float getWinPosY() {
        return 0.8f;
    }

    @Override
    protected String getType() {
        return MessageProtocol.Body.REG_MOTION;
    }

    public static void main(String args[]) {
        MotionSensor sensor = new MotionSensor(args);
        sensor.execute();
    }
}
