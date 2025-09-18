package io.github.cloudburst.sidebarpotion;

import eu.pb4.sidebars.api.Sidebar;
import eu.pb4.sidebars.api.lines.SidebarLine;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.scoreboard.number.BlankNumberFormat;
import net.minecraft.scoreboard.number.FixedNumberFormat;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record EffectSidebarLine(StatusEffectInstance effect) implements SidebarLine {

    private static final Map<StatusEffectCategory, TextColor> CATEGORY_COLORS = Map.of(
            StatusEffectCategory.BENEFICIAL, TextColor.fromRgb(0xA1DD70),
            StatusEffectCategory.HARMFUL, TextColor.fromRgb(0xEE4E4E),
            StatusEffectCategory.NEUTRAL, TextColor.fromRgb(0xF6EEC9)
    );

    @Override
    public int getValue() {
        if (effect.isInfinite()) return Integer.MAX_VALUE;
        return effect.getDuration();
    }

    @Override
    public boolean setValue(int value) {
        return false;
    }

    @Override
    public @NotNull NumberFormat getNumberFormat(ServerPlayNetworkHandler handler) {
        var time = Text.empty();
        var format = SidebarPotionMod.CONFIG.durationFormat;
        if (format == SidebarConfig.DurationFormat.NONE) return BlankNumberFormat.INSTANCE;
        if (effect.isInfinite()) {
            time = Text.translatable("effect.duration.infinite");
        } else {
            var ticks = effect.getDuration();

            var text = "";

            if (format == SidebarConfig.DurationFormat.TICKS) {
                text = String.valueOf(ticks);
            } else if (format == SidebarConfig.DurationFormat.SECONDS) {
                text = String.valueOf(ticks / 20);
            } else if (format == SidebarConfig.DurationFormat.MINUTES_SECONDS_MILLISECONDS) {
                var totalSeconds = ticks / 20.0;
                var minutes = (int) (totalSeconds / 60);
                var seconds = (int) (totalSeconds % 60);
                var milliseconds = (int) ((totalSeconds - Math.floor(totalSeconds)) * 100);
                text = String.format("%d:%02d.%02d", minutes, seconds, milliseconds);
            } else if (format == SidebarConfig.DurationFormat.MINUTES_SECONDS) {
                var totalSeconds = ticks / 20;
                var minutes = totalSeconds / 60;
                var seconds = totalSeconds % 60;
                text = String.format("%d:%02d", minutes, seconds);
            } else if (format == SidebarConfig.DurationFormat.SECONDS_MILLISECONDS) {
                var totalSeconds = ticks / 20.0;
                var seconds = (int) (totalSeconds);
                var milliseconds = (int) ((totalSeconds - Math.floor(totalSeconds)) * 100);
                text = String.format("%d.%02d", seconds, milliseconds);
            }

            time = Text.literal(text);
        }
        var soon = (!effect.isInfinite() && effect.getDuration() < 4 * 20);
        return new FixedNumberFormat(time.formatted(soon ? Formatting.RED : Formatting.YELLOW));
    }

    @Override
    public Text getText(ServerPlayNetworkHandler handler) {
        var type = effect.getEffectType().value();

        var text = Text.empty();

        if (SidebarPotionMod.CONFIG.showIcon) {
            text.append(getPotionIcon(handler, type)).append(" ");
        }

        var color = SidebarPotionMod.CONFIG.colorizeText ? CATEGORY_COLORS.getOrDefault(type.getCategory(), TextColor.fromRgb(0xFFFFFF)) : TextColor.fromRgb(0xFFFFFF);

        if (SidebarPotionMod.CONFIG.showEffectName) {
            var name = Text.translatable(effect.getTranslationKey());
            text.append(name.styled(s -> s.withColor(color))).append(" ");
        }

        if (SidebarPotionMod.CONFIG.showPotency) {
            var potency = Text.literal(toRoman(effect.getAmplifier() + 1));
            text.append(potency.styled(s -> s.withColor(color))).append(" ");
        }

        return text;
    }

    private Text getPotionIcon(ServerPlayNetworkHandler handler, StatusEffect type) {
        var usePolymer = FabricLoader.getInstance().isModLoaded("polymer-bundled") &&
                PolymerIntegration.hasPolymerIcons(handler) && !SidebarPotionMod.CONFIG.forceCompatIcons;

        if (usePolymer) {
            var codepoint = PolymerIntegration.getEffectCodepoint(type);
            var color = effect.getDuration() < 4 * 20 ? Formatting.GRAY : Formatting.WHITE;

            if (codepoint != null) {
                return Text.literal(codepoint).styled(s -> s
                        .withFont(Identifier.of(SidebarPotionMod.MOD_ID, "effect"))
                        .withColor(color)
                );
            }
        }

        return Text.literal("\uD83E\uDDEA").styled(s -> s.withColor(TextColor.fromRgb(type.getColor())));
    }

    @Override
    public void setSidebar(@Nullable Sidebar sidebar) {
    }

    private static String toRoman(int n) {
        if (n <= 0) return "";
        if (n >= 10) return "X+"; // cap for very high levels
        String[] romans = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return romans[n];
    }
}
