package com.pppopipupu.angry;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

import java.util.concurrent.CompletableFuture;

public class AngryRecipeProvider extends RecipeProvider {
    public AngryRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,Angry.ANGRY_SPEAR.get()).requires(Angry.ANGRY_LIGHTNING_BLOCK_ITEM,4).requires(Angry.ANGRY_SWORD).unlockedBy("法式白汁烩小牛肉",has(Angry.ANGRY_ATOMIC_BLOCK_ITEM)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,Angry.ANGRY_GUN.get()).requires(Angry.ANGRY_ATOMIC_BLOCK_ITEM,8).requires(Angry.ANGRY_GUN).unlockedBy("烤地瓜",has(Angry.ANGRY_ATOMIC_BLOCK_ITEM)).save(recipeOutput);
    }
}
