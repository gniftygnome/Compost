package com.yurisuika.compost.mixin.world.level.block;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.yurisuika.compost.Compost;
import com.yurisuika.compost.world.level.block.ArrayOutputContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {

    @Redirect(method = "extractProduce", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean redirectExtractProduce(Level level, Entity entity) {
        return false;
    }

    @Inject(method = "extractProduce", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.AFTER))
    private static void injectExtractProduce(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        Arrays.stream(Compost.config.items).forEach(group -> {
            if(ThreadLocalRandom.current().nextDouble() < group.chance) {
                double x = (double)(level.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                double y = (double)(level.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                double z = (double)(level.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                Item item;
                int index;
                if (group.item.contains("{")) {
                    index = group.item.indexOf("{");
                    String id = group.item.substring(0, index);
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                } else {
                    index = 0;
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item));
                }
                int maxCount = item.getMaxStackSize();
                int max = Math.min(group.max, maxCount);
                int min = Math.min(Math.min(group.min, maxCount), max);
                ItemStack itemStack = new ItemStack(item, ThreadLocalRandom.current().nextInt(min, max + 1));
                if (group.item.contains("{")) {
                    CompoundTag nbt;
                    try {
                        nbt = TagParser.parseTag(group.item.substring(index));
                        itemStack.setTag(nbt);
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                }
                ItemEntity itemEntity = new ItemEntity(level, (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, itemStack);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        });
    }

    @Inject(method = "getContainer", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void injectGetContainer(BlockState state, LevelAccessor level, BlockPos pos, CallbackInfoReturnable<WorldlyContainer> cir) {
        List<ItemStack> list = Lists.newArrayList();
        Arrays.stream(Compost.config.items).forEach(group -> {
            if(ThreadLocalRandom.current().nextDouble() < group.chance) {
                Item item;
                int index;
                if (group.item.contains("{")) {
                    index = group.item.indexOf("{");
                    String id = group.item.substring(0, index);
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                } else {
                    index = 0;
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item));
                }
                int maxCount = item.getMaxStackSize();
                int max = Math.min(group.max, maxCount);
                int min = Math.min(Math.min(group.min, maxCount), max);
                ItemStack itemStack = new ItemStack(item, ThreadLocalRandom.current().nextInt(min, max + 1));
                if (group.item.contains("{")) {
                    CompoundTag nbt;
                    try {
                        nbt = TagParser.parseTag(group.item.substring(index));
                        itemStack.setTag(nbt);
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                }
                list.add(itemStack);
            }
        });

        if (Compost.config.shuffle) {
            Collections.shuffle(list);
        }

        ItemStack[] itemStacks = new ItemStack[list.size()];
        for (int m = 0; m < list.size(); m++) {
            itemStacks[m] = list.get(m);
        }

        cir.setReturnValue(new ArrayOutputContainer(state, level, pos, itemStacks));
    }

}
