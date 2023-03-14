package ticxo.display.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DisplayEntity.class)
public abstract class DisplayEntityMixin extends Entity {

	@Shadow @Final private Quaternionf fixedRotation;
	private final Quaternionf prevFixedRotation = new Quaternionf();

	public DisplayEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci) {
		prevFixedRotation.rotationYXZ(-prevYaw * MathHelper.RADIANS_PER_DEGREE, prevPitch * MathHelper.RADIANS_PER_DEGREE, 0.0f);
	}

	@Inject(method = "getFixedRotation", at = @At("HEAD"), cancellable = true)
	private void lerpRotation(CallbackInfoReturnable<Quaternionf> cir) {
		var rotation = new Quaternionf(prevFixedRotation).slerp(fixedRotation, MinecraftClient.getInstance().getTickDelta());
		cir.setReturnValue(rotation);
	}

}
