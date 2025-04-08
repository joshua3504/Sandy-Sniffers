package net.joshua3504.sandysniffers.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnifferEntity.class)
public abstract class SnifferSandMixin {
    @Accessor("FINISH_DIG_TIME")
    protected static final TrackedData<Integer> getFinishDigTime() {
        throw new UnsupportedOperationException();
    }

    @Invoker("getDigPos")
    protected abstract BlockPos invokeGetDigPos();

    @Inject(method = "dropSeeds", at = @At("HEAD"))
    protected void addSand(CallbackInfo info) {
        SnifferEntity sniffer = (SnifferEntity) (Object) this;

        if (!(sniffer.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        if (!sniffer.getWorld().isClient() && sniffer.getDataTracker().get(getFinishDigTime()) == sniffer.age) {
            BlockPos digPos = invokeGetDigPos();
            digPos = new BlockPos(digPos.getX(), digPos.getY() - 1, digPos.getZ());

            if (serverWorld.getBlockState(digPos).getBlock().equals(Blocks.SAND)) {
                ItemEntity itemEntity = new ItemEntity(serverWorld, digPos.getX(), digPos.getY() + 1, digPos.getZ(),
                        new ItemStack(Items.SAND, (int) (Math.random() * 4) + 1));
                itemEntity.setToDefaultPickupDelay();
                serverWorld.spawnEntity(itemEntity);
            } else if (serverWorld.getBlockState(digPos).getBlock().equals(Blocks.RED_SAND)) {
                ItemEntity itemEntity = new ItemEntity(serverWorld, digPos.getX(), digPos.getY() + 1, digPos.getZ(),
                        new ItemStack(Items.RED_SAND, (int) (Math.random() * 4) + 1));
                itemEntity.setToDefaultPickupDelay();
                serverWorld.spawnEntity(itemEntity);
            }
        }
    }
}