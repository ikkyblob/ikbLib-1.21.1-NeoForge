package net.ikb.library.tags;

import net.ikb.library.IkbLibrary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;

import java.util.ArrayList;
import java.util.List;

public class IkbLibTags {

    public static class Fluids {
        public static final TagKey<Fluid> BRINE = createTag("brine");
        public static final TagKey<Fluid> STACKABLE_WET = createTag("stackable_wet");
        public static final TagKey<Fluid> STACKABLE_MOLTEN = createTag("stackable_molten");

        public static final List<TagKey<Fluid>> stackableTags = List.of(STACKABLE_WET, STACKABLE_MOLTEN);

        private static TagKey<Fluid> createTag(String name) {
            return FluidTags.create(ResourceLocation.fromNamespaceAndPath(IkbLibrary.MODID, name));
        }
    }
}
