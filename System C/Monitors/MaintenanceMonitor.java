/**
 * @author Anton V. Trantin (anton@trantin.ru)
 * Innopolis University
 * Date: 08.04.16
 */
package Monitors;

import Framework.BaseMonitor;

public class MaintenanceMonitor extends BaseMonitor {

    protected MaintenanceMonitor(String[] args) {
        super(args);
    }

    @Override
    protected String getName() {
        return null;
    }

    @Override
    protected float getWinPosX() {
        return 0;
    }

    @Override
    protected float getWinPosY() {
        return 0;
    }
}
