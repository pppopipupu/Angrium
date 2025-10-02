package com.pppopipupu.angry;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.pppopipupu.angry.block.AngryBlock;
import com.pppopipupu.angry.block.AngryFemaleBlock;
import com.pppopipupu.angry.block.MultiPartBlock;
import com.pppopipupu.angry.item.AngryBlockItem;
import com.pppopipupu.angry.item.AngryLightningItem;
import com.pppopipupu.angry.tileentity.AngryBlockEntity;
import com.pppopipupu.angry.tileentity.AngryFemaleEntity;
import com.pppopipupu.angry.world.AngryBlockFeature;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.state.BlockBehaviour.simpleCodec;

@Mod(Angry.MODID)
public class Angry {
    public static final String MODID = "angry";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Registries
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MapCodec<? extends Block>> REGISTRAR = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, MODID);

    // Blocks
    public static final DeferredBlock<AngryBlock> ANGRY_BLOCK = BLOCKS.register("angry_block", () -> new AngryBlock(Block.Properties.of().strength(2.0f).mapColor(MapColor.STONE).noOcclusion()));
    public static final DeferredBlock<AngryFemaleBlock> ANGRY_FEMALE_BLOCK = BLOCKS.register("angry_female_block", () -> new AngryFemaleBlock(Block.Properties.of().strength(2.0f).mapColor(MapColor.STONE)));

    // Codec
    public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<AngryBlock>> SIMPLE_CODEC = REGISTRAR.register("simple", () -> simpleCodec(AngryBlock::new));

    // Items
    public static final DeferredItem<BlockItem> ANGRY_LIGHTNING_BLOCK_ITEM = ITEMS.register("angry_lightning_block", () -> new AngryLightningItem(ANGRY_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ANGRY_BLOCK_ITEM = ITEMS.register("angry_block", () -> new AngryBlockItem(ANGRY_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ANGRY_FEMALE_ITEM = ITEMS.registerSimpleBlockItem("angry_female_block", ANGRY_FEMALE_BLOCK);

    // Block Entities
    public static final Supplier<BlockEntityType<AngryBlockEntity>> ANGRY_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("angry_block_entity", () -> BlockEntityType.Builder.of(AngryBlockEntity::new, ANGRY_BLOCK.get()).build(null));
    public static final Supplier<BlockEntityType<AngryFemaleEntity>> ANGRY_FEMALE_ENTITY = BLOCK_ENTITY_TYPES.register("angry_female_entity", () -> BlockEntityType.Builder.of(AngryFemaleEntity::new, ANGRY_FEMALE_BLOCK.get()).build(null));

    // World Generation
    public static final List<MultiPartBlock> MULTI_PART_BLOCKS_TO_GENERATE = new ArrayList<>();
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MODID);
    public static final Supplier<Feature<NoneFeatureConfiguration>> ANGRY_BLOCK_FEATURE = FEATURES.register("angry_block_feature", () -> new AngryBlockFeature(NoneFeatureConfiguration.CODEC, MULTI_PART_BLOCKS_TO_GENERATE));
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registries.CONFIGURED_FEATURE, MODID);
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANGRY_BLOCK_CONFIGURED_FEATURE_KEY = createConfiguredFeatureKey("angry_block_feature");
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registries.PLACED_FEATURE, MODID);
    public static final ResourceKey<PlacedFeature> ANGRY_BLOCK_PLACED_FEATURE_KEY = createPlacedFeatureKey("angry_block_placed");
    public static final ResourceKey<BiomeModifier> ADD_ANGRY_BLOCK_BIOME_MODIFIER_KEY = createBiomeModifierKey("add_angry_block");

    public Angry(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        REGISTRAR.register(modEventBus);
        FEATURES.register(modEventBus);
        CONFIGURED_FEATURES.register(modEventBus);
        PLACED_FEATURES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onBlocksRegistered);
        modEventBus.addListener(this::gatherData);
        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void onBlocksRegistered(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            MULTI_PART_BLOCKS_TO_GENERATE.add(ANGRY_BLOCK.get());
            MULTI_PART_BLOCKS_TO_GENERATE.add(ANGRY_FEMALE_BLOCK.get());
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }


    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
                packOutput, lookupProvider,
                new RegistrySetBuilder()
                        .add(Registries.CONFIGURED_FEATURE, Angry::bootstrapConfiguredFeatures)
                        .add(Registries.PLACED_FEATURE, Angry::bootstrapPlacedFeatures)
                        .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Angry::bootstrapBiomeModifiers),
                Set.of(MODID)
        ));
    }

    private static void bootstrapConfiguredFeatures(net.minecraft.data.worldgen.BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(ANGRY_BLOCK_CONFIGURED_FEATURE_KEY, new ConfiguredFeature<>(ANGRY_BLOCK_FEATURE.get(), new NoneFeatureConfiguration()));
    }

    private static void bootstrapPlacedFeatures(net.minecraft.data.worldgen.BootstrapContext<PlacedFeature> context) {
        var configuredFeatureRegistry = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(ANGRY_BLOCK_PLACED_FEATURE_KEY, new PlacedFeature(
                configuredFeatureRegistry.getOrThrow(ANGRY_BLOCK_CONFIGURED_FEATURE_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(32),
                        CountPlacement.of(UniformInt.of(2, 5)),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                )
        ));
    }

    private static void bootstrapBiomeModifiers(net.minecraft.data.worldgen.BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_ANGRY_BLOCK_BIOME_MODIFIER_KEY, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ANGRY_BLOCK_PLACED_FEATURE_KEY)),
                GenerationStep.Decoration.SURFACE_STRUCTURES
        ));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> createConfiguredFeatureKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static ResourceKey<PlacedFeature> createPlacedFeatureKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static ResourceKey<BiomeModifier> createBiomeModifierKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }
}
