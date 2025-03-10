package com.hidoni.customizableelytra.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/*
    This is the default handler, if there's no data we'll just render the most basic elytra with the vanilla colors.
 */
public class CustomizationHandler {
    private final boolean wingCapeHidden;
    private int wingLightLevel;

    public CustomizationHandler(boolean wingCapeHidden, int wingLightLevel) {
        this.wingCapeHidden = wingCapeHidden;
        this.wingLightLevel = wingLightLevel;
    }

    public int getColor(int index) {
        return 16777215;
    }

    public boolean isWingCapeHidden(int index) {
        return wingCapeHidden;
    }

    public int modifyWingLight(int lightLevel, int index) {
        if (wingLightLevel > 0) {
            lightLevel |= 0xFF;
        }
        return lightLevel;
    }

    public boolean isModified() {
        return wingLightLevel != 0 || wingCapeHidden;
    }

    @OnlyIn(Dist.CLIENT)
    public <T extends LivingEntity, M extends AgeableListModel<T>> void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, M renderModel, ResourceLocation textureLocation, boolean hasGlint) {
        renderModel.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer ivertexbuilder = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(textureLocation), false, hasGlint);
        renderModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
