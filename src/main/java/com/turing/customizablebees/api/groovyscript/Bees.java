package com.turing.customizablebees.api.groovyscript;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.turing.customizablebees.api.APIHelper;
import com.turing.customizablebees.api.BeeBuilder;
import com.turing.customizablebees.api.groovyscript.recipe.BeeProduct;
import com.turing.customizablebees.bees.effects.EffectBase;
import com.turing.customizablebees.mixin.AlleleBeeSpeciesAccessor;
import com.turing.customizablebees.mixin.BeeRootAccessor;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeMutationBuilder;
import forestry.api.genetics.IMutationBuilder;
import forestry.apiculture.genetics.BeeMutation;
import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public class Bees extends VirtualizedRegistry<BeeProduct> {
    public final Mutations mutations = new Mutations();

    public BeeBuilder beeBuilder(String name, String branchName) {
        return BeeBuilder.create(GroovyScript.ID, name, branchName);
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(this::removeProductFromBee);
        restoreFromBackup().forEach(this::addProductToBee);
    }

    protected void addProductToBee(BeeProduct product) {
        if (product.special) ((AlleleBeeSpeciesAccessor) product.species).getSpecialtyChances().put(product.stack, product.chance);
        else ((AlleleBeeSpeciesAccessor) product.species).getProductChances().put(product.stack, product.chance);
    }

    protected void removeProductFromBee(BeeProduct product) {
        if (product.special) ((AlleleBeeSpeciesAccessor) product.species).getSpecialtyChances().remove(product.stack);
        else ((AlleleBeeSpeciesAccessor) product.species).getProductChances().remove(product.stack);
    }

    public <T extends EffectBase> void addEffect(String effectName, Class<T> effect) {
        APIHelper.addGroovyEffect(effectName, effect);
    }

    public void add(BeeProduct product) {
        if (product == null || product.species == null) return;
        addScripted(product);
        addProductToBee(product);
    }

    public boolean remove(BeeProduct product) {
        if (product == null || product.species == null) return false;
        addBackup(product);
        removeProductFromBee(product);
        return true;
    }

    public BeeProduct addBeeProduct(AlleleBeeSpecies species, ItemStack output, float chance) {
        BeeProduct product = new BeeProduct(species, output, chance, false);
        add(product);
        return product;
    }

    public BeeProduct addBeeSpecialty(AlleleBeeSpecies species, ItemStack output, float chance) {
        BeeProduct product = new BeeProduct(species, output, chance, true);
        add(product);
        return product;
    }

    public BeeProduct addBeeProduct(AlleleBeeSpecies species, ItemStack output) {
        return addBeeProduct(species, output, 1.0F);
    }

    public BeeProduct addBeeSpecialty(AlleleBeeSpecies species, ItemStack output) {
        return addBeeSpecialty(species, output, 1.0F);
    }

    public static class Mutations extends VirtualizedRegistry<IBeeMutation> {
        @Override
        public void onReload() {
            removeScripted().forEach(BeeRootAccessor.getBeeMutations()::remove);
            restoreFromBackup().forEach(BeeRootAccessor.getBeeMutations()::add);
        }

        public IBeeMutation add(AlleleBeeSpecies a, AlleleBeeSpecies b, AlleleBeeSpecies result, double chance, @Nullable Function<IBeeMutationBuilder, IMutationBuilder> requirement) {
            BeeMutation mutation = new BeeMutation(a, b, Objects.requireNonNull(BeeManager.beeRoot).getTemplate(result), (int) Math.round(100 * chance));
            if (requirement != null) mutation = (BeeMutation) requirement.apply(mutation);
            add(mutation);
            return mutation;
        }

        public IBeeMutation add(AlleleBeeSpecies a, AlleleBeeSpecies b, AlleleBeeSpecies result, double chance) {
            return add(a, b, result, chance, null);
        }

        public void add(IBeeMutation mutation) {
            if (mutation == null) return;
            addScripted(mutation);
            BeeRootAccessor.getBeeMutations().add(mutation);
        }

        public boolean remove(IBeeMutation mutation) {
            if (mutation == null) return false;
            addBackup(mutation);
            BeeRootAccessor.getBeeMutations().remove(mutation);
            return true;
        }

        public boolean removeByResult(AlleleBeeSpecies result) {
            if (BeeRootAccessor.getBeeMutations().removeIf(mutation -> {
                boolean found = Arrays.equals(mutation.getTemplate(), Objects.requireNonNull(BeeManager.beeRoot).getTemplate(result));
                if (found) addBackup(mutation);
                return found;
            })) return true;

            GroovyLog.msg("Error removing bee mutation")
                    .add("could not find bee mutation with result {}", result)
                    .error()
                    .post();
            return false;
        }

        public void removeAll() {
            BeeRootAccessor.getBeeMutations().forEach(this::addBackup);
            BeeRootAccessor.getBeeMutations().forEach(BeeRootAccessor.getBeeMutations()::remove);
        }

        public SimpleObjectStream<IBeeMutation> streamMutations() {
            return new SimpleObjectStream<>(BeeRootAccessor.getBeeMutations()).setRemover(this::remove);
        }
    }
}
