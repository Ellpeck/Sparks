package de.ellpeck.sparks.mod.item;

import de.ellpeck.sparks.api.iface.tool.IMultitoolInteract;
import de.ellpeck.sparks.api.iface.tool.IMultitoolVisualize;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ItemMultitool extends ItemBase{

    public ItemMultitool(){
        super("multitool");
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

    public static void modifyAttachment(ItemStack stack, ToolAttachment attachment, boolean remove){
        if(!stack.hasTagCompound()){
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound compound = stack.getTagCompound();
        NBTTagList list = compound.getTagList("Attachments", 8);

        String name = attachment.name();
        if(remove){
            for(int i = 0; i < list.tagCount(); i++){
                if(name.equals(list.getStringTagAt(i))){
                    list.removeTag(i);
                    return;
                }
            }
        }
        else{
            list.appendTag(new NBTTagString(name));
        }

        compound.setTag("Attachments", list);
    }

    public static boolean hasAttachment(ItemStack stack, ToolAttachment attachment){
        if(stack.hasTagCompound()){
            NBTTagList list = stack.getTagCompound().getTagList("Attachments", 8);

            String name = attachment.name();
            for(int i = 0; i < list.tagCount(); i++){
                if(name.equals(list.getStringTagAt(i))){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems){
        ItemStack stack = new ItemStack(item);
        for(ToolAttachment attachment : ToolAttachment.values()){
            modifyAttachment(stack, attachment, false);
        }
        subItems.add(stack);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(player.isSneaking()){
            if(hasAttachment(stack, ToolAttachment.CONNECTOR)){
                if(!world.isRemote){
                    setStoredPos(stack, pos);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        else{
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if(block instanceof IMultitoolInteract){
                if(((IMultitoolInteract)block).interact(world, pos, player, hand)){
                    return EnumActionResult.SUCCESS;
                }
            }

            if(hasAttachment(stack, ToolAttachment.VISUALIZE)){
                if(block instanceof IMultitoolVisualize){
                    if(((IMultitoolVisualize)block).visualize(world, pos, player, hand)){
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand){
        if(player.isSneaking() && getStoredPos(stack) != null){
            if(!world.isRemote){
                setStoredPos(stack, null);
            }

            player.swingArm(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

    public enum ToolAttachment{
        CONNECTOR,
        GAUGE,
        VISUALIZE
    }
}
