package net.ikb.library.neoforge;

import net.ikb.library.IkbLibrary;
import net.neoforged.fml.common.Mod;

@Mod(IkbLibrary.MODID)
public final class IkbLibraryNeoForge {
    public IkbLibraryNeoForge() {
        // Run our common setup.
        IkbLibrary.init();
    }
}
