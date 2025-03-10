package com.hidoni.customizableelytra.renderers.models;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ElytraWingModel<T extends LivingEntity> extends AgeableListModel<T> {
    protected ModelPart wing;
    private final boolean is_right_wing;

    public ElytraWingModel(ModelPart rootLayer, boolean is_right_wing) {
        wing = is_right_wing ? rootLayer.getChild("right_wing") : rootLayer.getChild("left_wing");
        this.is_right_wing = is_right_wing;
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.wing);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = 0.2617994F;
        float f1 = -0.2617994F;
        float f2 = 0.0F;
        float f3 = 0.0F;
        if (entityIn.isFallFlying()) {
            float f4 = 1.0F;
            Vec3 vector3d = entityIn.getDeltaMovement();
            if (vector3d.y < 0.0D) {
                Vec3 vector3d1 = vector3d.normalize();
                f4 = 1.0F - (float) Math.pow(-vector3d1.y, 1.5D);
            }

            f = f4 * 0.34906584F + (1.0F - f4) * f;
            f1 = f4 * (-(float) Math.PI / 2F) + (1.0F - f4) * f1;
        } else if (entityIn.isCrouching()) {
            f = 0.6981317F;
            f1 = (-(float) Math.PI / 4F);
            f2 = 3.0F;
            f3 = 0.08726646F;
        }

        this.wing.x = 5.0F;
        this.wing.y = f2;
        if (entityIn instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractclientplayerentity = (AbstractClientPlayer) entityIn;
            abstractclientplayerentity.elytraRotX = (float) ((double) abstractclientplayerentity.elytraRotX + (double) (f - abstractclientplayerentity.elytraRotX) * 0.1D);
            abstractclientplayerentity.elytraRotY = (float) ((double) abstractclientplayerentity.elytraRotY + (double) (f3 - abstractclientplayerentity.elytraRotY) * 0.1D);
            abstractclientplayerentity.elytraRotZ = (float) ((double) abstractclientplayerentity.elytraRotZ + (double) (f1 - abstractclientplayerentity.elytraRotZ) * 0.1D);
            this.wing.xRot = abstractclientplayerentity.elytraRotX;
            this.wing.yRot = abstractclientplayerentity.elytraRotY;
            this.wing.zRot = abstractclientplayerentity.elytraRotZ;
        } else {
            this.wing.xRot = f;
            this.wing.zRot = f1;
            this.wing.yRot = f3;
        }

        if (this.is_right_wing) {
            this.wing.x = -this.wing.x;
            this.wing.yRot = -this.wing.yRot;
            this.wing.zRot = -this.wing.zRot;
        }
    }
}
