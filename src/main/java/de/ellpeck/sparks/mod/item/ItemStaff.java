package de.ellpeck.sparks.mod.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemStaff extends ItemBase{

    public ItemStaff(){
        super("staff");
    }

    public static BlockPos getStoredPos(ItemStack stack){
        if(stack.hasTagCompound()){
            long pos = stack.getTagCompound().getLong("StoredPos");
            if(pos != 0){
                return BlockPos.fromLong(pos);
            }
        }
        return null;
    }

    public static void setStoredPos(ItemStack stack, BlockPos pos){
        if(!stack.hasTagCompound()){
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound compound = stack.getTagCompound();
        if(pos != null){
            compound.setLong("StoredPos", pos.toLong());
        }
        else{
            compound.removeTag("StoredPos");
        }
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(player.isSneaking()){
            if(!world.isRemote){
                setStoredPos(stack, pos);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

}
