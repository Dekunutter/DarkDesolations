package com.deku.darkdesolations.common.entity;

import com.deku.darkdesolations.common.entity.monster.Coralfish;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.deku.darkdesolations.Main.MOD_ID;

public class ModEntityTypeInitializer {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<EntityType<Coralfish>> CORALFFISH_ENTITY_TYPE = ENTITY_TYPES.register("coralfish", () ->
        EntityType.Builder.<Coralfish>of(Coralfish::new, MobCategory.MONSTER)
            .sized(1.0f, 1.0f)
            .clientTrackingRange(8)
            .build(new ResourceLocation(MOD_ID, "coralfish").toString())
    );
}
