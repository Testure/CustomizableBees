package com.turing.customizablebees.bees;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.turing.customizablebees.BeeDefinitionEvent;
import com.turing.customizablebees.bees.effects.EffectBase;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.genetics.IClassification;
import forestry.core.config.Constants;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CustomBees {
    public static final Set<IAlleleBeeSpecies> registeredSpecies = new HashSet<>();
    public static final HashMap<String, IClassification> classificationHashMap = new HashMap<>();
    static final BeeMutationTree tree = new BeeMutationTree();
    public static ArrayList<CustomBeeEntry> sorted_bee_entries;
    static final List<Consumer<BeeMutationTree>> mutationRegisters = new ArrayList<>();
    static final Map<String, Class<? extends EffectBase>> groovyEffects = new HashMap<>();
    static final Map<String, EffectBase> EFFECTS = new HashMap<>();

    public static Map<String, EffectBase> getEffects() {
        return ImmutableMap.copyOf(EFFECTS);
    }

    public static void init() {
        sorted_bee_entries = Lists.newArrayList(CustomBeeEntry.BEE_ENTRIES);
        Map<CustomBeeEntry, Integer> bee_complexity = sorted_bee_entries.stream().collect(Collectors.toMap(t -> t, s -> tree.getLeastParents(s).stream().mapToInt(Set::size).min().orElse(0)));
        Map<String, Double> bee_model_complexity = sorted_bee_entries.stream().collect(
                Collectors.groupingBy(
                        s -> s.modelName,
                        Collectors.collectingAndThen(
                                Collectors.mapping(bee_complexity::get, Collectors.toList()),
                                s -> s.stream().mapToInt(Integer::intValue).average().orElseThrow(RuntimeException::new))));
        sorted_bee_entries.sort(Comparator
                .comparing((CustomBeeEntry s) -> false)
                .thenComparingDouble(t -> bee_model_complexity.get(t.modelName))
                .thenComparingInt(bee_complexity::get));
        sorted_bee_entries.forEach(CustomBeeEntry::build);

        CustomBeeEntry.BEE_ENTRIES.forEach(CustomBeeEntry::init);
        registeredSpecies.addAll(sorted_bee_entries.stream().map(t -> t.species).filter(Objects::nonNull).collect(Collectors.toSet()));
        tree.registerMutations();
    }

    public static void preInit() {
        groovyEffects.forEach((name, clazz) -> {
            try {
                EFFECTS.put(name, clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException ignored) {

            }
        });
        CustomBeeEntry.BEE_ENTRIES.forEach(CustomBeeEntry::preInit);
        MinecraftForge.EVENT_BUS.post(new BeeDefinitionEvent.DefineMutations());
        mutationRegisters.forEach(c -> c.accept(tree));
    }

    public static void addMutationRegister(Consumer<BeeMutationTree> register) {
        mutationRegisters.add(register);
    }

    public static void addEffect(EffectBase effect) {
        EFFECTS.put(effect.getName(), effect);
    }

    public static <T extends EffectBase> void addGroovyEffect(String name, Class<T> effect) {
        groovyEffects.put(name, effect);
    }

    public static BeeMutationTree.SpeciesEntry getForestrySpecies(String name) {
        return getVanillaSpecies(Constants.MOD_ID + ".species" + name);
    }

    public static BeeMutationTree.SpeciesEntry getVanillaSpecies(String name) {
        return new BeeMutationTree.VanillaEntry(name);
    }

    public static int col(int r, int g, int b) {
        return ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }
}
