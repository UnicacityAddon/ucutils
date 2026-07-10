package de.rettichlp.ucutils.integrations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.rettichlp.ucutils.common.gui.screens.options.MainOptionsScreen;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return _ -> new MainOptionsScreen();
    }
}
