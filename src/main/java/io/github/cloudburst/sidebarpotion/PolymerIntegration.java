package io.github.cloudburst.sidebarpotion;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PolymerIntegration {

    private static final Map<StatusEffect, String> effectCodepoints = new HashMap<>();

    public static void init() {

        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(builder -> {
            effectCodepoints.clear();
            var providers = new JsonArray();

            Registries.STATUS_EFFECT.streamEntries().forEach(ref -> {
                if (ref.getKey().isEmpty()) return;

                var key = ref.getKey().get();
                var id = key.getValue();

                if (!textureExists(id, builder)) {
                    SidebarPotionMod.LOGGER.warn("Skipping status effect \"{}\" as it has no texture", id);
                    return;
                }

                var provider = new JsonObject();

                var namespace = id.getNamespace();
                var entryname = id.getPath();

                var effect = ref.value();

                var codepoint = 0x41 + effectCodepoints.size();
                var codepointStr = new String(Character.toChars(codepoint));
                effectCodepoints.put(effect, codepointStr);

                var chars = new JsonArray();
                chars.add(codepointStr);
                provider.add("chars", chars);

                provider.addProperty("type", "bitmap");
                provider.addProperty("file", String.format("%s:mob_effect/%s.png", namespace, entryname));
                provider.addProperty("ascent", 8);
                provider.addProperty("height", 8);

                providers.add(provider);
            });
            var font = new JsonObject();
            font.add("providers", providers);

            builder.addData("assets/" + SidebarPotionMod.MOD_ID + "/font/effect.json", font.toString().getBytes(StandardCharsets.UTF_8));

            SidebarPotionMod.LOGGER.info("Assigned codepoints from \"{}\" for \"{}\" status effects",
                    "A",
                    new String(Character.toChars(0x41 + effectCodepoints.size() - 1))
            );
        });
    }

    private static boolean textureExists(Identifier identifier, ResourcePackBuilder builder) {
        var path = String.format("assets/%s/textures/mob_effect/%s.png", identifier.getNamespace(), identifier.getPath());
        return builder.getDataOrSource(path) != null;
    }

    public static boolean hasPolymerIcons(ServerPlayNetworkHandler handler) {
        return PolymerResourcePackUtils.hasMainPack(handler);
    }

    public static String getEffectCodepoint(StatusEffect effect) {
        return effectCodepoints.getOrDefault(effect, null);
    }
}
