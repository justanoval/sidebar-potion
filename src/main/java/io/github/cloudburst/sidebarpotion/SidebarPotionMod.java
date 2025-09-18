package io.github.cloudburst.sidebarpotion;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SidebarPotionMod implements ModInitializer, SimpleSynchronousResourceReloadListener {
	public static final String MOD_ID = "sidebar-potion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static SidebarConfig CONFIG = SidebarConfig.load();

	public static final PotionSidebar POTION_SIDEBAR = new PotionSidebar();

	@Override
	public void onInitialize() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			POTION_SIDEBAR.addPlayer(handler);
		});
		if (FabricLoader.getInstance().isModLoaded("polymer-bundled")) {
			PolymerIntegration.init();
		}
		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(this);
	}

	@Override
	public Identifier getFabricId() {
		return Identifier.of(MOD_ID);
	}

	@Override
	public void reload(ResourceManager manager) {
		CONFIG = SidebarConfig.load();
	}
}