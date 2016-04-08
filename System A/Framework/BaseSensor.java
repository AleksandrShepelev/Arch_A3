package Framework;

import java.util.Random;

public abstract class BaseSensor extends BaseComponent {
    private static final Random _random = new Random();

    protected BaseSensor(String args[]) {
        super(args);
    }

    /***************************************************************************
     * CONCRETE METHOD:: GetRandomNumber
     * Purpose: This method provides the simulation with random floating point
     * temperature values between 0.1 and 0.9.
     * <p>
     * Arguments: None.
     * <p>
     * Returns: float
     * <p>
     * Exceptions: None
     ***************************************************************************/

    protected static float getRandomNumber() {
        Float val = -1.0f;

        while (val < 0.1) {
            val = _random.nextFloat();
        }

        return val;
    }

    /***************************************************************************
     * CONCRETE METHOD:: CoinToss
     * Purpose: This method provides a random true or false value used for
     * determining the positiveness or negativeness of the drift value.
     * <p>
     * Arguments: None.
     * <p>
     * Returns: boolean
     * <p>
     * Exceptions: None
     ***************************************************************************/

    protected static boolean coinToss() {
        return (_random.nextBoolean());
    }

    protected static boolean probablisticCoinToss(float eventProbability) {
        float val = _random.nextFloat();
        if (val > (eventProbability)) {
            return false;
        } else {
            return true;
        }
    }
}
