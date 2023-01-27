package dev.yurisuika.compost.block;

import dev.yurisuika.compost.mixin.block.ComposterBlockInvoker;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;

public class ArrayFullInventory extends Inventory implements ISidedInventory {

    private final BlockState state;
    private final IWorld level;
    private final BlockPos pos;
    private boolean changed;

    public ArrayFullInventory(BlockState state, IWorld level, BlockPos pos, ItemStack[] outputItems) {
        super(outputItems);
        this.state = state;
        this.level = level;
        this.pos = pos;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? new int[]{0} : new int[0];
    }

    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return !this.changed && dir == Direction.DOWN && !stack.isEmpty();
    }

    public void setChanged() {
        ComposterBlockInvoker.invokeEmpty(this.state, this.level, this.pos);
        this.changed = true;
    }

}