package io.github.cloudburst.sidebarpotion;

import eu.pb4.sidebars.api.Sidebar;
import eu.pb4.sidebars.api.SidebarInterface;
import eu.pb4.sidebars.api.SidebarUtils;
import eu.pb4.sidebars.api.lines.SidebarLine;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PotionSidebar implements SidebarInterface {
    private final Set<ServerPlayNetworkHandler> players = new HashSet<>();

    @Override
    public int getUpdateRate() {
        if (SidebarPotionMod.CONFIG.durationFormat == SidebarConfig.DurationFormat.TICKS
                || SidebarPotionMod.CONFIG.durationFormat == SidebarConfig.DurationFormat.MINUTES_SECONDS_MILLISECONDS
                || SidebarPotionMod.CONFIG.durationFormat == SidebarConfig.DurationFormat.SECONDS_MILLISECONDS) {
            return 1; // Update every tick for tick-based formats
        } else {
            return 20; // Update every second for other formats
        }
    }

    @Override
    public Sidebar.Priority getPriority() {
        return Sidebar.Priority.MEDIUM;
    }

    @Override
    public Text getTitleFor(ServerPlayNetworkHandler handler) {
        return Text.of(SidebarPotionMod.CONFIG.title);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public List<SidebarLine> getLinesFor(ServerPlayNetworkHandler handler) {
        var effects = handler.player.getStatusEffects();

        if (effects.isEmpty()) {
            this.removePlayer(handler);
            return List.of();
        }

        return effects.stream().map(this::mapEffect).toList();
    }

    private SidebarLine mapEffect(StatusEffectInstance effect) {
        return new EffectSidebarLine(effect);
    }

    @Override
    public boolean isActive() {
        return !players.isEmpty();
    }

    @Override
    public void disconnected(ServerPlayNetworkHandler handler) {
        this.removePlayer(handler);
    }

    public void addPlayer(ServerPlayNetworkHandler handler) {
        if (this.players.add(handler)) {
            SidebarUtils.addSidebar(handler, this);
        }
    }

    public void removePlayer(ServerPlayNetworkHandler handler) {
        if (this.players.remove(handler)) {
            SidebarUtils.removeSidebar(handler, this);
        }
    }
}
