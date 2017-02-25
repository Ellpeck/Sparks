package de.ellpeck.sparks.mod.block;

import de.ellpeck.sparks.api.iface.ISpark;
import de.ellpeck.sparks.api.iface.ISparkInteractor;
import de.ellpeck.sparks.mod.entity.spark.EntityPickupSpark;
import de.ellpeck.sparks.mod.tile.TilePickerUpper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class BlockPickerUpper extends BlockContainerBase implements ISparkInteractor{

    public BlockPickerUpper(){
        super(Material.ROCK, "picker_upper", TilePickerUpper.class, "picker_upper");
    }

    @Override
    public boolean interact(World world, BlockPos pos, IBlockState state, ISpark spark){
        if(spark instanceof EntityPickupSpark){
            EntityPickupSpark pickup = (EntityPickupSpark)spark;
            ItemStack stack = pickup.getCarryingStack();
            if(stack != null){
                BlockPos up = pos.up();
                TileEntity tile = world.getTileEntity(up);
                if(tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN)){
                    IItemHandler cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
                    if(cap != null){
                        ItemStack left = ItemHandlerHelper.insertItem(cap, stack.copy(), false);

                        pickup.setCarryingStack(left);
                        pickup.setKilled();

                        return true;
                    }
                }
            }
            else{
                return true;
            }
        }
        return false;
    }
}
