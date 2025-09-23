package net.ikb.library;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ikb.library.world.gen.IkbLibraryDensityFunctions;

@Environment(EnvType.SERVER)
public final class IkbLibrary {
    public static final String MODID = "ikblibrary";

    public static void init() {
        IkbLibraryDensityFunctions.register();
    }
}
