package snw.ncemod;

import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

import static snw.ncemod.ResourceLoader.loadLang;


public final class NoCreeperExplosion implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoCreeperExplosion.class);
    private static final String LANG_DEFAULT = "zh_cn";
    /**
     * Decide the explosion will affect the world.
     */
    public static boolean enabled = true;
    private JsonObject translation;

    @Override
    public void onInitialize() {
        final Locale defaultLocale = Locale.getDefault();
        final String langCode = defaultLocale.getLanguage() + "_" + defaultLocale.getCountry().toLowerCase();
        try {
            translation = loadLang(langCode);
        } catch (IOException e) {
            LOGGER.error("Unable to load language {}, using default {}", langCode, LANG_DEFAULT);
            try {
                translation = loadLang(LANG_DEFAULT);
            } catch (IOException ex) {
                LOGGER.error("Unable to load default language {}, please report to author!!!", LANG_DEFAULT, ex);
            }
        }

        final LiteralArgumentBuilder<ServerCommandSource> cmd =
                CommandManager.literal("nce")
                        .requires(s -> s.hasPermissionLevel(4)) // only console or opped players can execute this!
                        .executes(ctx -> {
                            enabled = !enabled;
                            final String key = enabled ? "nce.enabled" : "nce.disabled";
                            ctx.getSource().getServer().getPlayerManager().broadcast(text(key), false);
                            return Command.SINGLE_SUCCESS;
                        }); // command as the switch of the feature of this mod
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> dispatcher.register(cmd)); // this is applicable to anywhere!
        ServerPlayConnectionEvents.JOIN
                .register((handler, sender, server) ->
                        handler.getPlayer().sendMessage(
                                text("nce.status").append(text("nce.status." + enabled))));
    }

    private @NotNull MutableText text(String key, Object... arguments) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            if (translation.has(key)) {
                return Text.translatableWithFallback(key, translation.get(key).getAsString(), arguments);
            }
        }
        return Text.translatable(key, arguments);
    }
}
