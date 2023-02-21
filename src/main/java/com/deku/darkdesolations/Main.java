package com.deku.darkdesolations;

import com.deku.darkdesolations.common.entity.ModEntityTypeInitializer;
import com.deku.darkdesolations.common.entity.ai.sensing.ModSensorTypes;
import com.deku.darkdesolations.common.entity.monster.Coralfish;
import com.deku.darkdesolations.common.features.*;
import com.deku.darkdesolations.common.items.ModItems;
import com.deku.darkdesolations.common.world.gen.biomes.ModBiomeInitializer;
import com.deku.darkdesolations.common.world.gen.biomes.ModBiomeProvider;
import com.deku.darkdesolations.common.world.gen.biomes.ModSurfaceRules;
import com.deku.darkdesolations.common.world.gen.placements.ModPlacements;
import com.deku.darkdesolations.utils.LogTweaker;
import com.deku.darkdesolations.utils.ModConfiguration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

import java.util.stream.Collectors;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MOD_ID)
public class Main
{
    // TODO: Set to true to hide noise on console when mod is finished
    final boolean HIDE_CONSOLE_NOISE = false;

    // declare Mod ID
    public static final String MOD_ID = "darkdesolations";

    // Initialize logger
    public static final Logger LOGGER = LogManager.getLogger(Main.class);

    // Network Protocol Version
    public static final String NETWORK_PROTOCOL_VERSION = "1.0";

    // Network channel
    public static SimpleChannel NETWORK_CHANNEL = null;

    /**
     * Constructor for initializing the mod.
     * Handles the setup of:
     *      - Log filtering.
     *      - Event Bus listeners
     *      - Registries
     *      - Ensuring client-only registrars only execute on a client
     *      - Ensures that mod structure piece types are registered early
     *      - Ensures that biomes are registered early
     *      - Adds additional forge event listeners for biome and world loading events
     */
    public Main() {
        System.out.println("STARTING EXECUTION");

        if (HIDE_CONSOLE_NOISE) {
            LogTweaker.applyLogFilterLevel(Level.WARN);
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfiguration.COMMON_SPEC, "darkdesolations-common.toml");

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Biome logic
        ModBiomeInitializer.BIOMES.register(eventBus);
        ModBiomeInitializer.registerBiomes();

        // Structure logic

        // Enchantment logic

        // Entity Types logic
        ModEntityTypeInitializer.ENTITY_TYPES.register(eventBus);

        // Item logic

        // Trunk Placer Types

        // AI Sensor Types
        ModSensorTypes.SENSOR_TYPES.register(eventBus);

        // Custom recipe serializers

        // Register the setup method for modloading
        eventBus.addListener(this::setup);
        // Register the enqueueIMC method for modloading
        eventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        eventBus.addListener(this::processIMC);

        ClientOnlyRegistrar clientOnlyRegistrar = new ClientOnlyRegistrar(eventBus);

        // Register ourselves for server and other game events we are interested in
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.register(this);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> clientOnlyRegistrar::registerClientOnlyEvents);
    }

    /**
     * Sets up logic that is common to both the client and server
     *
     * In this case we are:
     * - Registering our custom network messages to the simple network channel.
     * - Registering our custom wood types so that we can use associated resources.
     * - Registering all our terrablender regions
     * - Registering all our different features (trees, vegetation, ores, miscellaneous)
     * - Registering all our placements (ensuring village placements register after the processor lists)
     * - Registering all our processor lists
     * - Registering our custom villager types
     * - Initializing all our modded structures, their pieces and the structure sets that they belong to.
     *
     * @param event The setup event
     */
    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Regions.register(new ModBiomeProvider());
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ModSurfaceRules.makeRules());
        });
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("darkdesolations", "helloworld", () -> { LOGGER.debug("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.debug("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // do something when the server starts
    }

    /**
     * Inner class for different event registers used by the mod
     */
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        /**
         * Used to register blocks into the game using the mod event bus
         *
         * @param registryEvent The register event with which blocks will be registered
         */
        @SubscribeEvent
        public static void onBlocksRegistry(final RegisterEvent registryEvent) {
            registryEvent.register(ForgeRegistries.Keys.BLOCKS, registrar -> {
            });
        }

        /**
         * Used to register tile entities into the game using the mod event bus
         * Associated entity tile data is assigned before registration
         *
         * @param registerEvent The register event with which tile entities will be registered
         */
        @SubscribeEvent
        public static void onTileEntityRegistry(final RegisterEvent registerEvent) {
            registerEvent.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, registrar -> {
            });
        }

        /**
         * Used to register items into the game using the mod event bus
         *
         * @param registerEvent The register event with which items will be registered
         */
        @SubscribeEvent
        public static void onItemsRegistry(final RegisterEvent registerEvent) {
            registerEvent.register(ForgeRegistries.Keys.ITEMS, registrar -> {
            });
        }

        /**
         * Used to attach attribute modifiers to entities using the Forge event bus.
         * Required for living entities.
         *
         * @param event The attachment event with which attribute modifiers will be attached to different entity types
         */
        @SubscribeEvent
        public static void onEntityAttributeRegistration(final EntityAttributeCreationEvent event) {
            event.put(ModEntityTypeInitializer.CORALFFISH_ENTITY_TYPE.get(), Coralfish.createAttributes().build());
        }

        @SubscribeEvent
        public static void onEntitySpawn(final SpawnPlacementRegisterEvent registerEvent) {
            registerEvent.register(ModEntityTypeInitializer.CORALFFISH_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
        }

        /**
         * Used to register features into the game using the mod event bus
         *
         * @param registerEvent The register event with which features will be registered
         */
        @SubscribeEvent
        public static void onFeaturesRegistry(final RegisterEvent registerEvent) {
            registerEvent.register(ForgeRegistries.Keys.FEATURES, registrar -> {

                // Misc overworld features
                registrar.register(new ResourceLocation(MOD_ID, "land_claw_coral"), new LandClawCoralFeature());
                registrar.register(new ResourceLocation(MOD_ID, "land_tree_coral"), new LandTreeCoralFeature());
                registrar.register(new ResourceLocation(MOD_ID, "land_mushroom_coral"), new LandMushroomCoralFeature());
                registrar.register(new ResourceLocation(MOD_ID, "dead_claw_coral"), new DeadClawCoralFeature());
                registrar.register(new ResourceLocation(MOD_ID, "dead_tree_coral"), new DeadTreeCoralFeature());
                registrar.register(new ResourceLocation(MOD_ID, "dead_mushroom_coral"), new DeadMushroomCoralFeature());
            });

            //TODO: Should I register configured features in here after the feature registration has run? Right now they register at the global level spread across a few new classes and sit in holders. This might not be the right stage to be registering them...
        }

        /**
         * Used to register block state provider types into the game using the mod event bus
         *
         * @param registerEvent The register event with which block state provider types will be registered
         */
        @SubscribeEvent
        public static void onBlockStateProviderTypeRegistry(final RegisterEvent registerEvent) {
            registerEvent.register(ForgeRegistries.Keys.BLOCK_STATE_PROVIDER_TYPES, registrar -> {
            });
        }

        /**
         * Used to register datapacks into the game using the mod event bus
         *
         * @param gatherEvent The register event with which datapacks will be registered
         */
        @SubscribeEvent
        public static void onGatherDataRegistry(final GatherDataEvent gatherEvent) {
            DataGenerator generator = gatherEvent.getGenerator();
            PackOutput packOutput = generator.getPackOutput();
            ExistingFileHelper fileHelper = gatherEvent.getExistingFileHelper();

            HolderLookup.Provider lookupProvider = new RegistrySetBuilder()
                    .add(Registries.CONFIGURED_FEATURE, (RegistrySetBuilder.RegistryBootstrap<ConfiguredFeature<?,?>>) ModConfiguredFeatures::bootstrap)
                    .add(Registries.PLACED_FEATURE, (RegistrySetBuilder.RegistryBootstrap) ModPlacements::bootstrap)
                    .add(Registries.BIOME, ModBiomeInitializer::bootstrap)
                    .buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), VanillaRegistries.createLookup());
        }

        /**
         * Used to register foliage placers into the game using the mod event bus
         *
         * @param registerEvent The registry event with which foliage placers will be registered
         */
        @SubscribeEvent
        public static void onFoliagePlacerRegistry(final RegisterEvent registerEvent) {
            registerEvent.register(ForgeRegistries.Keys.FOLIAGE_PLACER_TYPES, registrar -> {

            });
        }

        /**
         * Used to register particle types into the game using the mod event bus
         *
         * @param registerEvent The registry event with which particle types will be registered
         */
        @SubscribeEvent
        public static void onParticleTypeRegistry(final RegisterEvent registerEvent) {
            registerEvent.register(ForgeRegistries.Keys.PARTICLE_TYPES, registrar -> {
            });
        }

        /**
         * Used to register capabilities into the game using the mod event bus
         *
         * @param capabilityRegistryEvent The registry event with which capabilities will be registered
         */
        @SubscribeEvent
        public static void onCapabilityRegistration(RegisterCapabilitiesEvent capabilityRegistryEvent) {
        }

        /**
         * Used to register a new custom creative mode tab to the creative mode UI using the mod event bus
         *
         * @param creativeTabBuildRegistryEvent The registry event with which new creative mode tabs are created and populated with items
         */
        @SubscribeEvent
        public static void onCreativeModeTabRegister(CreativeModeTabEvent.Register creativeTabBuildRegistryEvent) {
            creativeTabBuildRegistryEvent.registerCreativeModeTab(new ResourceLocation(MOD_ID, "dark_desolations_creative_tab"), builder ->
                    builder.title(Component.translatable("Dark Desolations"))
                            .icon(() -> new ItemStack(ModItems.CORALFISH_SPAWN_EGG))
                            .displayItems((enabledFlags, populator, hasPermissions) -> {
                                        // Cherry blossom blocks

                                        // Maple blocks

                                        // Crops

                                        // Wildlife
                                        populator.accept(new ItemStack(ModItems.CORALFISH_SPAWN_EGG));

                                        // Misc building blocks

                                        // Weapons & Armour

                                        // Hidden trapdoors
                                    }
                            )
            );
        }

        /**
         * Used to register items into vanilla creative mode tabs in the creative mode UI using the mod event bus
         *
         * @param creativeTabBuilderRegistryEvent The registry event with which new items are added to vanilla creative mode tabs
         */
        @SubscribeEvent
        public static void onCreativeModeTabBuilderRegister(CreativeModeTabEvent.BuildContents creativeTabBuilderRegistryEvent) {
            MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = creativeTabBuilderRegistryEvent.getEntries();
            CreativeModeTab.TabVisibility visibility = CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;

            if (creativeTabBuilderRegistryEvent.getTab() == CreativeModeTabs.BUILDING_BLOCKS) {
                // Wood blocks

                // Misc building blocks

                // Hidden trapdoors
            } else if (creativeTabBuilderRegistryEvent.getTab() == CreativeModeTabs.NATURAL_BLOCKS) {
                // Cherry blossom blocks
            } else if (creativeTabBuilderRegistryEvent.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
                // Lanterns

                // Signs
            } else if (creativeTabBuilderRegistryEvent.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
                // Cherry blossom blocks

                // Maple blocks
            } else if (creativeTabBuilderRegistryEvent.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                // Buckets

                // Boats
            } else if (creativeTabBuilderRegistryEvent.getTab() == CreativeModeTabs.COMBAT) {
                // Melee weapons

                // Armours

                // Throwables
            } else if (creativeTabBuilderRegistryEvent.getTab() == CreativeModeTabs.FOOD_AND_DRINKS) {
                // Crops

                // Fish
            } else if (creativeTabBuilderRegistryEvent.getTab() == CreativeModeTabs.INGREDIENTS) {
                // Plants
            } else if (creativeTabBuilderRegistryEvent.getTab() == CreativeModeTabs.SPAWN_EGGS) {
                // Fish

                // Animals
                // TODO: Put ater whatever is the last animal spawn egg in the overworld

                // Monsters
                // TODO: Put after whatever is the last vanilla monster spawn egg in the overworld
                entries.putAfter(new ItemStack(Items.SILVERFISH_SPAWN_EGG), new ItemStack(ModItems.CORALFISH_SPAWN_EGG), visibility);
            }
        }
    }

    /**
     * Inner class for different event handlers overriding handlers from vanilla Minecraft
     */
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandler {
        /**
         * Used to attach modded capabilities to entities using the Forge event bus
         *
         * @param event The attachment event with which capabilities will be attached to different entity types
         */
        @SubscribeEvent
        public static void onEntityCapabilityRegistration(final AttachCapabilitiesEvent<Entity> event) {
        }

        /**
         * Used to handle changes to item attribute modifiers on vanilla items.
         * Called whenever a player equips/unequips an item or whenever an item's tooltip is being renderred.
         *
         * @param event The event object that is built when an item needs to check its attribute modifiers
         */
        @SubscribeEvent
        public static void itemAttributeModifier(ItemAttributeModifierEvent event) {
        }

        /**
         * Used to handle events that occur when a block is placed into the world.
         * Currently this handles the replacement of base game grass blocks with our modded variant.
         * We also check to see if we should replace the block below the current so that we stop issues
         * with blocks placed on top of grass reverting the block back to the vanilla variant.
         *
         * @param event The event object that is build when a block is placed
         */
        @SubscribeEvent
        public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        }

        /**
         * Used to handle events that notify neighbouring blocks of changes.
         * Currently this handles the replacement of base game grass blocks with our modded variant
         * whenever a grass propegation occurs since the random ticker in the new block does not seem to be
         * sufficient alone.
         *
         * @param event The event object that is built when a block is updated
         */
        @SubscribeEvent
        public static void onNeighbourNotified(BlockEvent.NeighborNotifyEvent event) {
        }

        /**
         * Used to handle events that occur when a block is right-clicked by a player.
         * Currently this handles the stripping that occurs with new wood-based blocks that are right-clicked with an axe
         *
         * @param event The event object that is built when a block is right-clicked by a player
         */
        @SubscribeEvent
        public static void onBlockClicked(PlayerInteractEvent.RightClickBlock event) {
        }

        /**
         * Handles any custom logic that is needed on players on any given tick.
         *
         * Currently this checks if the player has tried to perform a double jump and communicates that action
         * to the server and performs all necessary updates to the player.
         *
         * @param event The tick event for a given player
         */
        @SubscribeEvent
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        }

        /**
         * Handles any custom logic that needs to happen whenever a living entity falls and hits the ground.
         *
         * This resets the double jump state of the given entity.
         *
         * @param event The event triggered by a living entity falling and hitting the ground.
         */
        @SubscribeEvent
        public static void onPlayerFall(final LivingFallEvent event) {
        }

        /**
         * Handles any custom logic that needs to happen when an entity joins the current world.
         *
         * This attempts to communicate the saved NBT state on a player's double jump capability back onto to
         * the player so that they can't reset their double jumping state by reconnecting to the server.
         *
         * @param event The event triggered by an entity joining the world
         */
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onEntityJoinWorld(final EntityJoinLevelEvent event) {
        }
    }
}
