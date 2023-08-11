package snw.ncemod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static snw.ncemod.NoCreeperExplosion.enabled;

/**
 * We hack into the explosion "event" object to cancel the affection!
 */
@Mixin(Explosion.class)
public abstract class MixinExplosion {

    @Shadow
    public abstract Entity getEntity();

    /**
     * @reason The core feature is implemented here!
     */
    @Inject(method = "collectBlocksAndDamageEntities", at = @At(value = "HEAD"), cancellable = true)
    public void onCollectAndDamage(CallbackInfo ci) {
        if (getEntity() instanceof CreeperEntity && enabled) {
            ci.cancel();
        }
    }
}
