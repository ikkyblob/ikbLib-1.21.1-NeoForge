package net.ikb.library;

import net.ikb.library.world.gen.IkbLibraryDensityFunctions;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(IkbLibrary.MODID)
public class IkbLibrary {
    public static final String MODID = "ikblibrary";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IkbLibrary(IEventBus modEventBus, ModContainer modContainer) {

        IkbLibraryDensityFunctions.register(modEventBus);


        NeoForge.EVENT_BUS.register(this);


    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

}
