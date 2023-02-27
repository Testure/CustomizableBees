package com.turing.customizablebees.bees.effects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.turing.customizablebees.CustomizableBees;
import com.turing.customizablebees.bees.effects.settings.IEffectSettingsHolder;
import com.turing.customizablebees.bees.effects.settings.Setting;
import com.turing.customizablebees.bees.effects.settings.Filter;
import com.turing.customizablebees.util.ParticleHelper;
import com.turing.customizablebees.util.RandomHelper;
import forestry.api.apiculture.*;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IEffectData;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EffectBase implements IAlleleBeeEffect {
    public static final HashMap<IAlleleBeeSpecies, EffectBase> registeredEffectSpecies = new HashMap<>();
    public static final WeakHashMap<IBeeHousing, Iterable<BlockPos>> adjacentPosCache = new WeakHashMap<>();
    static int n = 0;

    static {
        MinecraftForge.EVENT_BUS.register(EffectBase.class);
    }

    private final String uid, rawname, unlocalizedName, modid;
    private final boolean isDominant;
    private final boolean isCombinable;
    public Set<IAlleleBeeSpecies> validSpecies = ImmutableSet.of();
    public final List<Setting<?, ?>> settings = new ArrayList<>();

    public EffectBase(String rawname) {
        this(rawname, false, false);
    }

    public EffectBase(String rawname, boolean isDominant, boolean isCombinable) {
        this(CustomizableBees.MODID, rawname, isDominant, isCombinable);
    }

    public EffectBase(String modid, String rawname, boolean isDominant, boolean isCombinable) {
        this.rawname = rawname;
        this.isDominant = isDominant;
        this.isCombinable = isCombinable;
        this.unlocalizedName = modid + ".allele.effect." + rawname;
        this.uid = modid + ".effect." + rawname;
        this.modid = modid;
        AlleleManager.alleleRegistry.registerAllele(this, EnumBeeChromosome.EFFECT);
    }

    public IEffectSettingsHolder getSettings(IBeeHousing housing) {
        if (!settings.isEmpty()) {
            for (IBeeModifier modifier : housing.getBeeModifiers())
                if (modifier instanceof IEffectSettingsHolder)
                    return (IEffectSettingsHolder) modifier;
        }
        return IEffectSettingsHolder.INSTANCE;
    }

    public void addSpecies(IAlleleBeeSpecies species) {
        validSpecies = ImmutableSet.<IAlleleBeeSpecies>builder().addAll(validSpecies).add(species).build();
        if (registeredEffectSpecies.put(species, this) != null) throw new IllegalStateException();
    }

    public boolean isValidSpecies(IBeeGenome genome) {
        return isValidSpecies(genome.getPrimary()) || isValidSpecies(genome.getSecondary());
    }

    public boolean isValidSpecies(IAlleleBeeSpecies species) {
        return validSpecies.contains(species);
    }

    public boolean acceptItemStack(ItemStack stack) {
        return false;
    }

    public boolean canAcceptItems() {
        return false;
    }

    public String getDescriptionTranslationKey() {
        return "description.jei." + modid + "." + rawname + ".effect";
    }

    public <T extends TileEntity> List<T> getTiles(World world, Class<T> clazz, AxisAlignedBB bounds) {
        int x_min = MathHelper.floor(bounds.minX);
        int x_max = MathHelper.ceil(bounds.maxX);
        int y_min = MathHelper.floor(bounds.minY);
        int y_max = MathHelper.ceil(bounds.maxY);
        int z_min = MathHelper.floor(bounds.minZ);
        int z_max = MathHelper.ceil(bounds.maxZ);

        ImmutableList.Builder<T> builder = ImmutableList.builder();

        for (int chunk_x = x_min >> 4; chunk_x <= x_max >> 4; chunk_x++) {
            for (int chunk_z = z_min >> 4; chunk_z <= z_max >> 4; chunk_z++) {
                Chunk chunk = world.getChunk(chunk_x, chunk_z);
                for (Map.Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet()) {
                    BlockPos pos = entry.getKey();
                    if (pos.getX() >= x_min && pos.getX() <= x_max &&
                        pos.getY() >= y_min && pos.getY() <= y_max &&
                        pos.getZ() >= z_min && pos.getZ() <= z_max
                    ) {
                        TileEntity tileEntity = entry.getValue();
                        if (clazz.isInstance(tileEntity)) builder.add(clazz.cast(tileEntity));
                    }
                }
            }
        }

        return builder.build();
    }

    protected Iterable<BlockPos> getAdjacentTiles(IBeeHousing housing) {
        BlockPos pos = housing.getCoordinates();
        World world = housing.getWorldObj();
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof IBeeHousing)) {
            return Stream.concat(
                    Stream.of(pos),
                    Stream.of(RandomHelper.getPermutation()).map(pos::offset)
            )::iterator;
        }
        return adjacentPosCache.computeIfAbsent(housing, this::getBlockPos);
    }

    private Iterable<BlockPos> getBlockPos(IBeeHousing housing) {
        World world = housing.getWorldObj();
        Random rand = world.rand;
        BlockPos pos = housing.getCoordinates();
        IBeeHousingInventory inventory = housing.getBeeInventory();
        HashSet<BlockPos> checked = new HashSet<>();

        ArrayList<BlockPos> adjToHouse = new ArrayList<>();
        LinkedList<BlockPos> toCheck = new LinkedList<>();

        Arrays.stream(RandomHelper.getPermutation(rand)).map(pos::offset).forEach(toCheck::add);
        BlockPos blockPos;
        while ((blockPos = toCheck.poll()) != null) {
            TileEntity tile = world.getTileEntity(blockPos);
            if (tile instanceof IBeeHousing && ((IBeeHousing) tile).getBeeInventory() == inventory) {
                for (EnumFacing facing : RandomHelper.getPermutation(rand)) {
                    BlockPos newPos = blockPos.offset(facing);
                    if (checked.add(newPos)) toCheck.add(newPos);
                }
            } else adjToHouse.add(blockPos);
        }
        return adjToHouse;
    }

    public <C> List<C> getAdjacentCapabilities(IBeeHousing housing, Capability<C> capability) {
        return getAdjacentCapabilities(housing, capability, t -> true);
    }

    public <C> List<C> getAdjacentCapabilities(IBeeHousing housing, Capability<C> capability, Predicate<TileEntity> filter) {
        return Streams.stream(getAdjacentTiles(housing)).map(housing.getWorldObj()::getTileEntity).filter(Objects::nonNull).filter(filter).map(t -> t.getCapability(capability, null)).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    public float getCooldown(IBeeGenome genome, Random rand) {
        return 0;
    }

    public static AxisAlignedBB getAABB(IBeeGenome genome, IBeeHousing housing) {
        Vec3d territory = getTerritory(genome, housing);
        return new AxisAlignedBB(housing.getCoordinates()).grow(territory.x, territory.y, territory.z);
    }

    public static float getModifier(IBeeGenome genome, IBeeHousing housing, BiFunction<IBeeModifier, IBeeGenome, Float> function) {
        World world = housing.getWorldObj();
        IBeekeepingMode mode = BeeManager.beeRoot.getBeekeepingMode(world);
        IBeeModifier housingModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
        IBeeModifier modeModifier = mode.getBeeModifier();

        return function.apply(housingModifier, genome) * function.apply(modeModifier, genome);
    }

    public static Vec3d getTerritory(IBeeGenome genome, IBeeHousing housing) {
        Vec3i territory = genome.getTerritory();
        float modifier = getModifier(genome, housing, (beeModifier, genome1) -> beeModifier.getTerritoryModifier(genome1, 1));
        return new Vec3d(territory.getX() * modifier, territory.getY() * modifier, territory.getZ() * modifier);
    }

    public static ItemStack tryAdd(ItemStack stack, IBeeHousingInventory inventory) {
        if (stack.isEmpty() || inventory.addProduct(stack, true)) return ItemStack.EMPTY;

        int amount = stack.getCount();
        int n;
        while (amount > 0 && (n = Integer.highestOneBit(amount)) > 0) {
            ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, n);
            if (inventory.addProduct(copy, true)) amount -= n;
            else break;
        }

        if (amount == stack.getCount()) return stack;
        else if (amount == 0) return ItemStack.EMPTY;
        else return ItemHandlerHelper.copyStackWithSize(stack, amount);
    }

    public static float getSpeed(IBeeGenome genome, IBeeHousing housing) {
        float speed = genome.getSpeed() * getModifier(genome, housing, (m, g) -> m.getProductionModifier(g, 1));
        if ("forestry.apiculture.tiles.TileBeeHouse".equals(housing.getClass().getName())) speed *= 0.4F;
        return speed;
    }

    @SubscribeEvent
    public static void tickCleanup(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            n++;
            if (n > 400) {
                adjacentPosCache.clear();
                n = 0;
            }
        }
    }

    public static int getRand(int a, int b, Random rand) {
        if (a == b) return a;
        else if (a < b) return a + rand.nextInt(b - a);
        else return b + rand.nextInt(a - b);
    }

    public abstract IEffectData doEffectBase(IBeeGenome genome, IEffectData storedData, IBeeHousing housing, IEffectSettingsHolder settingsHolder);

    @Override
    public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
        if (isValidSpecies(genome) && !housing.getWorldObj().isRemote) {
            return doEffectBase(genome, storedData, housing, getSettings(housing));
        }
        return storedData;
    }

    public <V, NBT extends NBTBase> void addSetting(Setting<V, NBT> setting) {
        settings.add(setting);
    }

    @Override
    public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
        IBeekeepingLogic logic = housing.getBeekeepingLogic();
        List<BlockPos> flowerPositions = logic.getFlowerPositions();

        ParticleHelper.BEE_HIVE_FX.addBeeHiveFX(housing, genome, flowerPositions);
        return storedData;
    }

    public static Predicate<ItemStack> getFilter(IBeeHousing housing, World world, IEffectSettingsHolder settingsHolder, AxisAlignedBB bounds, Filter filter) {
        Predicate<ItemStack> matcher = filter.getMatcher(settingsHolder);

        if (matcher == Filter.DEFAULT_MATCHER) {
            for (EntityItemFrame itemFrame : world.getEntitiesWithinAABB(EntityItemFrame.class, bounds)) {
                ItemStack stack = itemFrame.getDisplayedItem();
                if (stack.isEmpty()) continue;

                BlockPos hangingPosition = itemFrame.getHangingPosition().offset(itemFrame.getHorizontalFacing().getOpposite());
                TileEntity tile = world.getTileEntity(hangingPosition);

                if (tile instanceof IBeeHousing) {
                    if (((IBeeHousing) tile).getBeeInventory() == housing.getBeeInventory()) {
                        if (matcher == Filter.DEFAULT_MATCHER) matcher = stack1 -> OreDictionary.itemMatches(stack, stack1, false);
                        else {
                            Predicate<ItemStack> prev = matcher;
                            matcher = stack1 -> stack1 != null && (prev.test(stack1) || OreDictionary.itemMatches(stack, stack1, false));
                        }
                    }
                }
            }
        }

        return matcher;
    }

    @Override
    public String getUID() {
        return uid;
    }

    @Override
    public IEffectData validateStorage(IEffectData storedData) {
        if (storedData != null) return storedData;
        return BaseEffectDataMap.None.INSTANCE;
    }

    @Override
    public String getName() {
        return rawname;
    }

    @Override
    public String getModID() {
        return CustomizableBees.MODID;
    }

    @Override
    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    @Override
    public String getAlleleName() {
        return I18n.format(getUnlocalizedName());
    }

    @Override
    public boolean isCombinable() {
        return isCombinable;
    }

    @Override
    public boolean isDominant() {
        return isDominant;
    }
}
