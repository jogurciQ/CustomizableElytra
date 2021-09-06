package com.hidoni.customizableelytra.items;

import com.hidoni.customizableelytra.CustomizableElytra;
import com.hidoni.customizableelytra.config.Config;
import com.hidoni.customizableelytra.util.ElytraCustomizationUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class CustomizableElytraItem extends ElytraItem implements IDyeableArmorItem {

    public final static String LEFT_WING_TRANSLATION_KEY = "item.customizable_elytra.left_wing";
    public final static String RIGHT_WING_TRANSLATION_KEY = "item.customizable_elytra.right_wing";
    public final static String HIDDEN_CAPE_TRANSLATION_KEY = "item.customizable_elytra.cape_hidden";

    public CustomizableElytraItem(Properties builder) {
        super(builder);
    }

    @OnlyIn(Dist.CLIENT)
    public static ResourceLocation getTextureLocation(BannerPattern bannerIn) {
        if (Config.useLowQualityElytraBanners.get()) {
            return new ResourceLocation(CustomizableElytra.MOD_ID, "entity/elytra_banner_low/" + bannerIn.getFileName());
        }
        return new ResourceLocation(CustomizableElytra.MOD_ID, "entity/elytra_banner/" + bannerIn.getFileName());
    }

    @Override
    public int getColor(ItemStack stack) {
        return getColor(stack, 0);
    }

    public int getColor(ItemStack stack, int index) {
        return ElytraCustomizationUtil.getData(stack).handler.getColor(index);
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        CompoundNBT bannerTag = stack.getChildTag("BlockEntityTag");
        CompoundNBT wingTag = stack.getChildTag("WingInfo");
        return IDyeableArmorItem.super.hasColor(stack) || bannerTag != null || wingTag != null || stack.getOrCreateTag().getBoolean("HideCapePattern");
    }

    @Override
    public void removeColor(ItemStack stack) {
        IDyeableArmorItem.super.removeColor(stack);
        stack.getOrCreateTag().remove("HideCapePattern");
        stack.removeChildTag("BlockEntityTag");
        stack.removeChildTag("WingInfo");
    }

    @Nullable
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.CHEST;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        applyWingTooltip(tooltip, flagIn, stack.getTag(), true);
        CompoundNBT wingInfo = stack.getChildTag("WingInfo");
        if (wingInfo != null) {
            if (wingInfo.contains("left")) {
                CompoundNBT leftWing = wingInfo.getCompound("left");
                if (!leftWing.isEmpty()) {
                    tooltip.add(new TranslationTextComponent(LEFT_WING_TRANSLATION_KEY).mergeStyle(TextFormatting.GRAY));
                    applyWingTooltip(tooltip, flagIn, leftWing);
                }
            }
            if (wingInfo.contains("right")) {
                CompoundNBT rightWing = wingInfo.getCompound("right");
                if (!rightWing.isEmpty()) {
                    tooltip.add(new TranslationTextComponent(RIGHT_WING_TRANSLATION_KEY).mergeStyle(TextFormatting.GRAY));
                    applyWingTooltip(tooltip, flagIn, rightWing);
                }
            }
        }
    }

    @Override
    public String getTranslationKey() {
        return Items.ELYTRA.getTranslationKey();
    }

    public static void applyWingTooltip(List<ITextComponent> tooltip, ITooltipFlag flagIn, CompoundNBT wingIn) {
        applyWingTooltip(tooltip, flagIn, wingIn, false);
    }

    public static void applyWingTooltip(List<ITextComponent> tooltip, ITooltipFlag flagIn, CompoundNBT wingIn, boolean ignoreDisplayTag) {
        CompoundNBT wing = ElytraCustomizationUtil.migrateOldSplitWingFormat(wingIn);
        if (wing.getBoolean("HideCapePattern")) {
            tooltip.add(new TranslationTextComponent(HIDDEN_CAPE_TRANSLATION_KEY).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
        }
        if (!ignoreDisplayTag && wing.contains("display")) {
            CompoundNBT displayTag = wing.getCompound("display");
            if (displayTag.contains("color", 99)) {
                if (flagIn.isAdvanced()) {
                    tooltip.add((new TranslationTextComponent("item.color", String.format("#%06X", displayTag.getInt("color")))).mergeStyle(TextFormatting.GRAY));
                } else {
                    tooltip.add((new TranslationTextComponent("item.dyed")).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
                }
            }
        } else if (wing.contains("BlockEntityTag")) {
            CompoundNBT blockEntityTag = wingIn.getCompound("BlockEntityTag");
            int baseColor = blockEntityTag.getInt("base");
            tooltip.add((new TranslationTextComponent("block.minecraft.banner." + BannerPattern.BASE.getFileName() + '.' + DyeColor.byId(baseColor).getTranslationKey())).mergeStyle(TextFormatting.GRAY));
            ListNBT listnbt = blockEntityTag.getList("Patterns", 10);

            for (int i = 0; i < listnbt.size() && i < 6; ++i) {
                CompoundNBT patternNBT = listnbt.getCompound(i);
                DyeColor dyecolor = DyeColor.byId(patternNBT.getInt("Color"));
                BannerPattern bannerpattern = BannerPattern.byHash(patternNBT.getString("Pattern"));
                if (bannerpattern != null) {
                    tooltip.add((new TranslationTextComponent("block.minecraft.banner." + bannerpattern.getFileName() + '.' + dyecolor.getTranslationKey())).mergeStyle(TextFormatting.GRAY));
                }
            }
        }
    }
}
