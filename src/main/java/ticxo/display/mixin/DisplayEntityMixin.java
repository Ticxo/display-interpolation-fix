package ticxo.display.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisplayEntity.class)
public abstract class DisplayEntityMixin extends Entity {

	@Shadow public abstract void setYaw(float yaw);
	@Shadow public abstract void setPitch(float pitch);
	@Shadow public abstract void setPosition(double x, double y, double z);

	protected int bodyTrackingIncrements;
	protected double serverX;
	protected double serverY;
	protected double serverZ;
	protected double serverYaw;
	protected double serverPitch;

	public DisplayEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void tick(CallbackInfo ci) {
		if (!isRemoved()) {
			tickMovement();
		}
	}

	private void tickMovement() {
		if (bodyTrackingIncrements > 0) {
			double lerpX = getX() + (serverX - getX()) / (double) bodyTrackingIncrements;
			double lerpY = getY() + (serverY - getY()) / (double) bodyTrackingIncrements;
			double lerpZ = getZ() + (serverZ - getZ()) / (double) bodyTrackingIncrements;
			double deltaYaw = MathHelper.wrapDegrees(serverYaw - (double) getYaw());
			setYaw(getYaw() + (float) deltaYaw / (float) bodyTrackingIncrements);
			setPitch(getPitch() + (float) (serverPitch - (double) getPitch()) / (float) bodyTrackingIncrements);
			--bodyTrackingIncrements;
			setPosition(lerpX, lerpY, lerpZ);
			setRotation(getYaw(), getPitch());
		}
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		serverX = x;
		serverY = y;
		serverZ = z;
		serverYaw = yaw;
		serverPitch = pitch;
		bodyTrackingIncrements = interpolationSteps;
	}

}
