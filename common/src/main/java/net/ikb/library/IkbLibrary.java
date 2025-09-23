package net.ikb.library;

import net.ikb.library.world.gen.IkbLibraryDensityFunctions;

public final class IkbLibrary {
    public static final String MODID = "ikblibrary";

    public static void init() {
        IkbLibraryDensityFunctions.register();
    }
}
