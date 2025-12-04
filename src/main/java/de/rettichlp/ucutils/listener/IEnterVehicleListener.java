package de.rettichlp.ucutils.listener;

import net.minecraft.entity.Entity;

public interface IEnterVehicleListener extends IUCUtilsListener {

    void onEnterVehicle(Entity vehicle);
}
