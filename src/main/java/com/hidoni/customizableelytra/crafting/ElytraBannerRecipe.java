package com.hidoni.customizableelytra.crafting;

import com.hidoni.customizableelytra.setup.ModItems;
import com.hidoni.customizableelytra.setup.ModRecipes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ElytraBannerRecipe extends CustomRecipe {

    public ElytraBannerRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        ItemStack elytraItem = ItemStack.EMPTY;
        ItemStack bannerItem = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack inventoryItem = inv.getItem(i);
            if (!inventoryItem.isEmpty()) {
                if (inventoryItem.getItem() instanceof BannerItem) {
                    if (!bannerItem.isEmpty()) {
                        return false;
                    }

                    bannerItem = inventoryItem;
                } else {
                    if (inventoryItem.getItem() != Items.ELYTRA && inventoryItem.getItem() != ModItems.CUSTOMIZABLE_ELYTRA.get() && inventoryItem.getItem() != ModItems.ELYTRA_WING.get()) {
                        return false;
                    }

                    if (!elytraItem.isEmpty()) {
                        return false;
                    }

                    if (inventoryItem.getTagElement("BlockEntityTag") != null || inventoryItem.getTagElement("WingInfo") != null) {
                        return false;
                    }

                    elytraItem = inventoryItem;
                }
            }
        }

        return !elytraItem.isEmpty() && !bannerItem.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack bannerItem = ItemStack.EMPTY;
        ItemStack elytraItem = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack inventoryItem = inv.getItem(i);
            if (!inventoryItem.isEmpty()) {
                if (inventoryItem.getItem() instanceof BannerItem) {
                    if (!bannerItem.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    bannerItem = inventoryItem;
                } else if (inventoryItem.getItem() == Items.ELYTRA) {
                    if (!elytraItem.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    ItemStack customizableElytraItem = new ItemStack(ModItems.CUSTOMIZABLE_ELYTRA.get());
                    EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(inventoryItem), customizableElytraItem);
                    if (!inventoryItem.getHoverName().equals(Component.translatable(Items.ELYTRA.getDescriptionId()))) {
                        customizableElytraItem.setHoverName(inventoryItem.getHoverName());
                    }
                    customizableElytraItem.setDamageValue(inventoryItem.getDamageValue());
                    customizableElytraItem.setRepairCost(inventoryItem.getBaseRepairCost());
                    elytraItem = customizableElytraItem;
                } else if (inventoryItem.getItem() == ModItems.CUSTOMIZABLE_ELYTRA.get() || inventoryItem.getItem() == ModItems.ELYTRA_WING.get()) {
                    if (!elytraItem.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    elytraItem = inventoryItem.copy();
                    elytraItem.setCount(1);
                }
            }
        }

        if (!elytraItem.isEmpty()) {
            CompoundTag compoundnbt = bannerItem.getTagElement("BlockEntityTag");
            CompoundTag compoundnbt1 = compoundnbt == null ? new CompoundTag() : compoundnbt.copy();
            compoundnbt1.putInt("Base", ((BannerItem) bannerItem.getItem()).getColor().getId());
            elytraItem.addTagElement("BlockEntityTag", compoundnbt1);
            CompoundTag displayTag = elytraItem.getTagElement("display");
            if (displayTag != null) {
                displayTag.remove("color");
            }
        }
        return elytraItem;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ELYTRA_BANNER_RECIPE.get();
    }
}
