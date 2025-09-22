package net.ikb.library;

import net.fabricmc.api.ModInitializer;
import net.ikb.library.world.gen.IkbLibraryDensityFunctions;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import org.slf4j.LoggerFactory;


public class IkbLibrary implements ModInitializer {

    public static final String MODID = "ikblibrary";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    @Override
    public void onInitialize() {

        IkbLibraryDensityFunctions.register();

    }
}
