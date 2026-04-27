package de.rettichlp.ucutils.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.text.ClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.StringJoiner;

import static de.rettichlp.ucutils.UCUtils.*;
import static java.net.URI.create;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;
import static net.minecraft.util.Formatting.*;

@UCUtilsCommand(label = "ucutils")
public class ModCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .then(literal("sync")
                        .executes(context -> {
                            syncService.syncFactionMembers();
                            syncService.checkForUpdates();

                            utilService.delayedAction(() -> {
                                storage.getPlayerFactionCache().clear();
                                syncService.syncFactionSpecificData();
                            }, 2000);

                            return 1;
                        }))
                .executes(context -> {
                    String version = utilService.getVersion();
                    String authors = getAuthors();

                    player.sendMessage(empty(), false);

                    messageService.sendModMessage("UCUtils Version " + version, false);

                    messageService.sendModMessage(empty()
                            .append(of("Autoren").copy().formatted(GRAY))
                            .append(of(":").copy().formatted(DARK_GRAY)).append(" ")
                            .append(of(authors).copy().formatted(WHITE)), false);

                    messageService.sendModMessage(empty()
                            .append(of("Discord").copy().formatted(GRAY))
                            .append(of(":").copy().formatted(DARK_GRAY)).append(" ")
                            .append(of("https://discord.gg/mZGAAwhPHu").copy().styled(style -> style
                                    .withColor(WHITE)
                                    .withClickEvent(new ClickEvent.OpenUrl(create("https://discord.gg/mZGAAwhPHu"))))), false);

                    messageService.sendModMessage(empty()
                            .append(of("GitHub").copy().formatted(GRAY))
                            .append(of(":").copy().formatted(DARK_GRAY)).append(" ")
                            .append(of("https://github.com/UnicacityAddon/ucutils").copy().styled(style -> style
                                    .withColor(WHITE)
                                    .withClickEvent(new ClickEvent.OpenUrl(create("https://github.com/UnicacityAddon/ucutils"))))), false);

                    player.sendMessage(empty(), false);

                    storage.print();

                    return 1;
                });
    }

    private String getAuthors() {
        Collection<Person> authors = FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(modContainer -> modContainer.getMetadata().getAuthors())
                .orElseThrow(() -> new NullPointerException("Cannot find authors"));

        StringJoiner stringJoiner = new StringJoiner(", ");
        authors.forEach(person -> stringJoiner.add(person.getName()));

        return stringJoiner.toString();
    }
}
