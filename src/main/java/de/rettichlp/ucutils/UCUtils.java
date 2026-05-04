package de.rettichlp.ucutils;

import de.rettichlp.ucutils.common.Storage;
import de.rettichlp.ucutils.common.api.Api;
import de.rettichlp.ucutils.common.configuration.Configuration;
import de.rettichlp.ucutils.common.registry.Registry;
import de.rettichlp.ucutils.common.services.CommandService;
import de.rettichlp.ucutils.common.services.MessageService;
import de.rettichlp.ucutils.common.services.NameTagService;
import de.rettichlp.ucutils.common.services.NotificationService;
import de.rettichlp.ucutils.common.services.RenderService;
import de.rettichlp.ucutils.common.services.SyncService;
import de.rettichlp.ucutils.common.services.UtilService;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Boolean.getBoolean;
import static java.util.Objects.isNull;

public class UCUtils implements ModInitializer {

    public static final String MOD_ID = "ucutils";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final CommandService commandService = new CommandService();
    public static final MessageService messageService = new MessageService();
    public static final NameTagService nameTagService = new NameTagService();
    public static final NotificationService notificationService = new NotificationService();
    public static final RenderService renderService = new RenderService();
    public static final SyncService syncService = new SyncService();
    public static final UtilService utilService = new UtilService();

    public static final Api api = new Api();
    public static final Storage storage = new Storage();
    public static final Configuration configuration = new Configuration().loadFromFile();

    public static ClientPlayerEntity player;
    public static ClientPlayNetworkHandler networkHandler;

    private static final String NEOPROTECT_ENTRYPOINT = "c970141b-0cca-4ad2-894b-21ac9c171cbe.shield.neoprotect.ovh";

    private final Registry registry = new Registry();

    @Override
    public void onInitialize() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        syncService.syncFactionMembers();

        this.registry.registerSounds();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            player = client.player;
            networkHandler = handler;

            boolean isUnicaCity = isUnicaCity(handler);
            storage.setUnicaCity(isUnicaCity);
            if (isUnicaCity) {
                client.execute(() -> {
                    this.registry.registerListeners();
                    renderService.initializeWidgets();
                    syncService.syncFactionSpecificData();
                    syncService.startRepeatingSync();
                    syncService.checkForUpdates();
                });
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> this.registry.registerCommands(dispatcher));

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> configuration.saveToFile());
    }

    private boolean isUnicaCity(ClientPlayNetworkHandler networkHandler) {
        if (getBoolean("fabric.development") || !configuration.getOptions().checkUnicacityServer()) {
            return true;
        }

        if (isNull(networkHandler)) {
            LOGGER.warn("Not connected to UnicaCity: Network handler is null");
            return false;
        }

        String addressString = networkHandler.getConnection().getAddress().toString();
        // for LabyMod players, there is no dot at the end of the domain
        if (!addressString.matches(NEOPROTECT_ENTRYPOINT + "\\.?/\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d+")) {
            LOGGER.warn("Not connected to UnicaCity: {}", addressString);
            return false;
        }

        LOGGER.info("Connected to UnicaCity: {}", addressString);
        return true;
    }
}
