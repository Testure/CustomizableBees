package com.turing.customizablebees.bees;

import com.cleanroommc.groovyscript.GroovyScript;
import com.turing.customizablebees.bees.effects.EffectBase;
import com.turing.customizablebees.mixin.AlleleBeeSpeciesAccessor;
import forestry.api.apiculture.*;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpeciesBuilder;
import forestry.api.genetics.IClassification;
import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomBeeEntry implements Supplier<IAlleleBeeSpecies>, BeeMutationTree.SpeciesEntry {
    public static final List<CustomBeeEntry> BEE_ENTRIES = new ArrayList<>();
    public static final int BEE_YELLOW = 0xffdc16;

    final List<Consumer<CustomBeeFactory>> speciesInstructions = new ArrayList<>();

    private final String name;
    private final boolean dominant;
    private final String branchName;
    private final int primaryColor;
    private final int secondaryColor;
    private String authority = "Unknown";
    public IAlleleBeeSpecies species;
    public String modelName;
    public final String modid;
    boolean shouldAddVanillaProducts = true;

    public CustomBeeEntry(String modid, String name, boolean dominant, String branchName, int primaryColor) {
        this(modid, name, dominant, branchName, primaryColor, BEE_YELLOW);
    }

    public CustomBeeEntry(String modid, String name, boolean dominant, String branchName, int primaryColor, int secondaryColor) {
        BEE_ENTRIES.add(this);
        this.modid = modid;
        this.name = name;
        this.dominant = dominant;
        this.branchName = branchName;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.modelName = name;
    }

    @Override
    public String getEntryName() {
        return this.modid + "." + this.name;
    }

    public final CustomBeeEntry setAuthority(String authority) {
        this.authority = authority;
        return this;
    }

    public final CustomBeeEntry setTemperature(EnumTemperature temperature) {
        speciesInstructions.add(c -> c.setTemperature(temperature));
        return this;
    }

    public final CustomBeeEntry setHumidity(EnumHumidity humidity) {
        speciesInstructions.add(c -> c.setHumidity(humidity));
        return this;
    }

    public final CustomBeeEntry setHasEffect() {
        speciesInstructions.add(IAlleleSpeciesBuilder::setHasEffect);
        return this;
    }

    public final CustomBeeEntry setIsNotCounted() {
        speciesInstructions.add(IAlleleBeeSpeciesBuilder::setIsNotCounted);
        return this;
    }

    public final CustomBeeEntry setComplexity(int complexity) {
        speciesInstructions.add(c -> c.setComplexity(complexity));
        return this;
    }

    @Nonnull
    public final CustomBeeEntry addProduct(ItemStack product, float chance) {
        speciesInstructions.add(c -> c.addProduct(product, chance));
        return this;
    }

    @Nonnull
    public final CustomBeeEntry addSpecialty(ItemStack specialty, float chance) {
        speciesInstructions.add(c -> c.addSpecialty(specialty, chance));
        return this;
    }

    public final CustomBeeEntry setNocturnal() {
        speciesInstructions.add(IAlleleBeeSpeciesBuilder::setNocturnal);
        return this;
    }

    public final CustomBeeEntry setJubilanceProvider(IJubilanceProvider provider) {
        speciesInstructions.add(c -> c.setJubilanceProvider(provider));
        return this;
    }

    public final CustomBeeEntry setCustomBeeModelProvider(IBeeModelProvider provider) {
        speciesInstructions.add(c -> c.setCustomBeeModelProvider(provider));
        return this;
    }

    public final CustomBeeEntry setCustomBeeSpriteColourProvider(IBeeSpriteColourProvider provider) {
        speciesInstructions.add(c -> c.setCustomBeeSpriteColourProvider(provider));
        return this;
    }

    public final CustomBeeEntry setTemplateAlleleBool(EnumBeeChromosome effect, boolean val) {
        return setTemplateAlleleString(effect, val ? "forestry.boolTrue" : "forestry.boolFalse");
    }

    public final CustomBeeEntry setTemplateAlleleString(EnumBeeChromosome effect, String val) {
        speciesInstructions.add(c -> c.setTemplateAllele(effect, val));
        return this;
    }

    public final CustomBeeEntry setTemplateEffect(Supplier<? extends IAllele> supplier) {
        return setTemplate(EnumBeeChromosome.EFFECT, supplier);
    }

    public final CustomBeeEntry setTemplate(EnumBeeChromosome effect, Supplier<? extends IAllele> supplier) {
        speciesInstructions.add(c -> c.setTemplateAllele(effect, supplier.get()));
        return this;
    }

    public final CustomBeeEntry removeVanillaProducts() {
        shouldAddVanillaProducts = false;
        return this;
    }

    public static float roundSig(float k, float n) {
        if (k <= 0 || n <= 0) return 0;
        if (k < 1) return roundSig(k * n, n) / n;
        return Math.round(k);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomBeeEntry)) return false;

        CustomBeeEntry that = (CustomBeeEntry) o;

        return name.equals(that.name);
    }

    @Override
    public IAlleleBeeSpecies get() {
        return species;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "CustomBeeEntry{name='" + name + "'}";
    }

    public void preInit() {

    }

    public void init() {

    }

    @Override
    public boolean isVanilla() {
        return false;
    }

    public String getAlleleName() {
        return species.getAlleleName();
    }

    public final void build() {
        CustomBeeFactory factory = CustomBeeFactory.factory(modid, name, dominant, branchName, primaryColor, secondaryColor, authority);
        speciesInstructions.forEach(c -> c.accept(factory));
        if (shouldAddVanillaProducts) addVanillaProducts(factory);
        species = factory.build();
    }

    private void addVanillaProducts(CustomBeeFactory factory) {
        List<BeeMutationTree.SpeciesEntry> vanillaParents = CustomBees.tree.getVanillaParents(this);
        HashMap<ItemStack, Pair<Float, Float>> sumMap = new HashMap<>();

        for (BeeMutationTree.SpeciesEntry entry : vanillaParents) {
            Map<ItemStack, Float> productChances = entry.get().getProductChances();
            mainLoop:
            for (Map.Entry<ItemStack, Float> inEntry : productChances.entrySet()) {
                for (Map.Entry<ItemStack, Pair<Float, Float>> existEntry : sumMap.entrySet()) {
                    if (ItemHandlerHelper.canItemStacksStack(existEntry.getKey(), inEntry.getKey())) {
                        existEntry.setValue(Pair.of(inEntry.getValue() + existEntry.getValue().getLeft(), Math.max(inEntry.getValue(), existEntry.getValue().getRight())));
                        continue mainLoop;
                    }
                }
                sumMap.put(inEntry.getKey().copy(), Pair.of(inEntry.getValue(), inEntry.getValue()));
            }
        }

        for (Map.Entry<ItemStack, Pair<Float, Float>> entry : sumMap.entrySet()) {
            float meanChance = entry.getValue().getLeft() / vanillaParents.size();
            float maxChance = entry.getValue().getRight();
            float finalChance = (maxChance + maxChance) / 2;
            float chance = roundSig(finalChance, 20);
            if (chance > 0) factory.addProduct(entry.getKey(), chance);
        }
    }

    public static class CustomBeeFactory implements IAlleleBeeSpeciesBuilder {
        public static final HashMap<IAlleleBeeSpecies, IAlleleBeeEffect> SPECIES_EFFECT_MAP = new HashMap<>();
        public static final HashMap<String, IAlleleBeeSpecies> STRING_SPECIES_MAP = new HashMap<>();

        static final HashSet<String> assignedUIDs = new HashSet<>();
        @Nonnull
        final AlleleTemplate template;
        final IAlleleBeeSpeciesBuilder species;

        final List<Pair<ItemStack, Float>> products = new ArrayList<>();

        public CustomBeeFactory(IAlleleBeeSpeciesBuilder species) {
            this.species = species;
            this.template = AlleleTemplate.createAlleleTemplate(null);
        }

        public static CustomBeeFactory factory(String modid, String name, boolean dominant, String branchName, int primaryColor) {
            return factory(modid, name, dominant, branchName, primaryColor, "Unknown");
        }

        public static CustomBeeFactory factory(String modid, String name, boolean dominant, String branchName, int primaryColor, String authority) {
            return factory(modid, name, dominant, branchName, primaryColor, BEE_YELLOW, authority);
        }

        public static CustomBeeFactory factory(String modid, String name, boolean dominant, String branchName, int primaryColor, int secondaryColor, String authority) {
            if (modid.equalsIgnoreCase("groovyscript")) modid = GroovyScript.getRunConfig().getPackId();
            branchName = new ResourceLocation(modid, new ResourceLocation(branchName).getPath()).toString();
            IClassification branch = CustomBees.classificationHashMap.computeIfAbsent(branchName, s -> {
                ResourceLocation location = new ResourceLocation(s);
                IClassification classification = BeeManager.beeFactory.createBranch(location.getPath(), location.getNamespace());
                AlleleManager.alleleRegistry.getClassification("family.apidae").addMemberGroup(classification);
                return classification;
            });
            String uid = modid + "." + name;

            if (!assignedUIDs.add(uid)) throw new IllegalStateException(uid + " is already registered!");

            String speciesKey = "bees.species." + uid;
            String speciesDescKey = "description." + uid;
            String speciesBinomKey = "bees.binomal.species." + uid;
            IAlleleBeeSpeciesBuilder species = BeeManager.beeFactory.createSpecies(modid, uid, dominant, authority, speciesKey, speciesDescKey, branch, speciesBinomKey, primaryColor, secondaryColor);

            return new CustomBeeFactory(species);
        }

        @Nonnull
        public CustomBeeFactory setTemplateAllele(EnumBeeChromosome effect, Object value) {
            template.setTemplateAllele(effect, value);
            return this;
        }

        @Nonnull
        public IAlleleBeeSpecies build() {
            products.forEach(pair -> this.species.addProduct(pair.getKey(), pair.getValue()));

            IAlleleBeeSpecies species = this.species.build();
            IAlleleBeeEffect effect = template.getEffect();

            if (effect instanceof EffectBase) {
                ((EffectBase) effect).addSpecies(species);
            }

            template.setTemplateAllele(EnumBeeChromosome.SPECIES, species);
            template.register();
            STRING_SPECIES_MAP.put(species.getUID(), species);
            SPECIES_EFFECT_MAP.put(species, template.getEffect());
            return species;
        }

        public CustomBeeFactory addProduct(Item item, float chance) {
            return addProduct(item.getDefaultInstance(), chance);
        }

        public CustomBeeFactory addSpecialty(Item item, float chance) {
            return addSpecialty(item.getDefaultInstance(), chance);
        }

        @Nonnull
        @Override
        public CustomBeeFactory addProduct(ItemStack stack, Float chance) {
            products.add(Pair.of(stack, chance));
            return this;
        }

        @Nonnull
        @Override
        public CustomBeeFactory addSpecialty(ItemStack stack, Float chance) {
            species.addSpecialty(stack, chance);
            return this;
        }

        @Nonnull
        @Override
        public CustomBeeFactory setJubilanceProvider(IJubilanceProvider provider) {
            species.setJubilanceProvider(provider);
            return this;
        }

        @Nonnull
        @Override
        public CustomBeeFactory setNocturnal() {
            species.setNocturnal();
            return this;
        }

        @Nonnull
        @Override
        public CustomBeeFactory setHasEffect() {
            species.setHasEffect();
            return this;
        }

        @Override
        @Nonnull
        public CustomBeeFactory setIsSecret() {
            setIsNotCounted();
            return this;
        }

        @Nonnull
        @Override
        public CustomBeeFactory setIsNotCounted() {
            species.setIsNotCounted();
            return this;
        }

        @Override
        public void setComplexity(int complexity) {
            species.setComplexity(complexity);
        }

        @Nonnull
        public CustomBeeFactory setComplexityRet(int complexity) {
            setComplexity(complexity);
            return this;
        }

        @Nonnull
        public CustomBeeFactory clearDefaultProducts() {
            products.clear();
            return this;
        }

        @Nonnull
        @Override
        public CustomBeeFactory setTemperature(EnumTemperature temperature) {
            species.setTemperature(temperature);
            return this;
        }

        @Nonnull
        @Override
        public CustomBeeFactory setHumidity(EnumHumidity humidity) {
            species.setHumidity(humidity);
            return this;
        }

        @Nonnull
        @Override
        public CustomBeeFactory setCustomBeeModelProvider(IBeeModelProvider provider) {
            species.setCustomBeeModelProvider(provider);
            return this;
        }

        @Nonnull
        @Override
        public CustomBeeFactory setCustomBeeSpriteColourProvider(IBeeSpriteColourProvider provider) {
            species.setCustomBeeSpriteColourProvider(provider);
            return this;
        }
    }
}
