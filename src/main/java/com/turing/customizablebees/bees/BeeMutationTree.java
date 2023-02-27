package com.turing.customizablebees.bees;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.turing.customizablebees.util.CollectionHelper;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeMutationBuilder;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IMutationBuilder;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class BeeMutationTree {
    final List<Entry> entries = new ArrayList<>();
    final HashMultimap<SpeciesEntry, Entry> recipes = HashMultimap.create();

    public void add(SpeciesEntry a, SpeciesEntry b, SpeciesEntry result, double chance) {
        add(a, b, result, chance, null);
    }

    public void add(SpeciesEntry a, SpeciesEntry b, SpeciesEntry result, double chance, @Nullable Function<IBeeMutationBuilder, IMutationBuilder> requirement) {
        Entry entry = new Entry(a, b, result, chance, requirement);
        entries.add(entry);
        recipes.put(result, entry);
    }

    public void registerMutations() {
        for (Entry entry : entries) {
            ArrayList<SpeciesEntry> pair = Lists.newArrayList(entry.a, entry.b);
            pair.sort(CollectionHelper.checkEqualThen(Comparator.comparing(SpeciesEntry::isVanilla).thenComparing(this::getComplexity)));

            IAlleleBeeSpecies primary = pair.get(0).get();
            IAlleleBeeSpecies secondary = pair.get(1).get();
            IBeeMutationBuilder mutation = Objects.requireNonNull(BeeManager.beeMutationFactory).createMutation(primary, secondary, Objects.requireNonNull(BeeManager.beeRoot).getTemplate(entry.result.get()), (int) Math.round(100 * entry.chance));

            if (entry.requirement != null) mutation = (IBeeMutationBuilder) entry.requirement.apply(mutation);
            mutation.build();
        }
    }

    public int getComplexity(SpeciesEntry entry) {
        return getLeastParents(entry).stream().mapToInt(HashSet::size).min().orElse(0);
    }

    public Set<HashSet<SpeciesEntry>> getLeastParents(SpeciesEntry species) {
        return getLeastParents(species, new HashSet<>());
    }

    public Set<HashSet<SpeciesEntry>> getLeastParents(SpeciesEntry species, Set<SpeciesEntry> visited) {
        if (!recipes.containsKey(species)) return ImmutableSet.of(Sets.newHashSet(species));
        Set<HashSet<SpeciesEntry>> entries = new HashSet<>();
        Set<SpeciesEntry> updatedVisited = Sets.newHashSet(visited);
        updatedVisited.add(species);

        for (Entry entry : recipes.get(species)) {
            if (updatedVisited.contains(entry.a) || updatedVisited.contains(entry.b)) continue;

            if (entry.a == entry.b) {
                for (HashSet<SpeciesEntry> set : getLeastParents(entry.a, updatedVisited)) {
                    HashSet<SpeciesEntry> s = new HashSet<>(set);
                    s.add(species);
                    entries.add(s);
                }
            } else for (HashSet<SpeciesEntry> setA : getLeastParents(entry.a, updatedVisited)) {
                for (HashSet<SpeciesEntry> setB : getLeastParents(entry.b, updatedVisited)) {
                    HashSet<SpeciesEntry> s = new HashSet<>(setA);
                    s.addAll(setB);
                    s.add(species);
                    entries.add(s);
                }
            }
        }

        return entries;
    }

    public List<SpeciesEntry> getVanillaParents(SpeciesEntry species) {
        HashSet<SpeciesEntry> checked = new HashSet<>();
        LinkedList<SpeciesEntry> toCheck = new LinkedList<>();
        ArrayList<SpeciesEntry> results = new ArrayList<>();
        SpeciesEntry poll;

        toCheck.add(species);
        while ((poll = toCheck.poll()) != null) {
            if (poll.isVanilla()) results.add(poll);
            for (Entry entry : recipes.get(poll)) {
                if (checked.add(entry.a)) toCheck.add(entry.a);
                if (checked.add(entry.b)) toCheck.add(entry.b);
            }
        }
        return results;
    }

    public static class GenericEntry extends VanillaEntry {
        public GenericEntry(String name) {
            super(name);
        }

        @Override
        public String toString() {
            return "GenericEntry{name='" + name + "'}";
        }

        @Override
        public boolean isVanilla() {
            return false;
        }
    }

    public static class VanillaEntry implements SpeciesEntry {
        final String name;

        public VanillaEntry(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VanillaEntry)) return false;

            VanillaEntry that = (VanillaEntry) o;

            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "VanillaEntry{name='" + name + "'}";
        }

        @Override
        public boolean isVanilla() {
            return true;
        }

        @Override
        public String getEntryName() {
            return name;
        }

        @Override
        public IAlleleBeeSpecies get() {
            return Objects.requireNonNull((IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(name));
        }
    }

    public interface SpeciesEntry extends Supplier<IAlleleBeeSpecies> {
        boolean isVanilla();

        String getEntryName();
    }

    public static class Entry {
        public final SpeciesEntry a;
        public final SpeciesEntry b;
        public final SpeciesEntry result;
        public final double chance;
        public final Function<IBeeMutationBuilder, IMutationBuilder> requirement;

        public Entry(SpeciesEntry a, SpeciesEntry b, SpeciesEntry result, double chance, @Nullable Function<IBeeMutationBuilder, IMutationBuilder> requirement) {
            this.a = a;
            this.b = b;
            this.result = result;
            this.chance = chance;
            this.requirement = requirement;
        }
    }
}
