package de.ellpeck.sparks.mod.block;

import de.ellpeck.sparks.api.iface.ISpark;
import de.ellpeck.sparks.api.iface.ISparkInteractor;
import de.ellpeck.sparks.mod.entity.spark.EntityPickupSpark;
import de.ellpeck.sparks.mod.tile.TileBurningCreator;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBurningCreator extends BlockContainerBase implements ISparkInteractor{

    public BlockBurningCreator(){
        super(Material.ROCK, "burning_creator", TileBurningCreator.class, "burning_creator");
    }

    @Override
    public boolean interact(World world, BlockPos pos, IBlockState state, ISpark spark){
        if(spark instanceof EntityPickupSpark){
            EntityPickupSpark pickup = (EntityPickupSpark)spark;
            ItemStack stack = pickup.getCarryingStack();
            if(stack != null){
                TileEntity tile = world.getTileEntity(pos);
                if(tile instanceof TileBurningCreator){
                    if(((TileBurningCreator)tile).fuel(stack)){
                        pickup.setCarryingStack(null);
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
