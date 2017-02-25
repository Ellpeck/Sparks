package de.ellpeck.sparks.mod.tile;

import de.ellpeck.sparks.mod.block.BlockSparkInitiator;
import de.ellpeck.sparks.mod.entity.spark.EntityPotentialSpark;
import de.ellpeck.sparks.mod.entity.spark.base.EntityTravellingSparkBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileSparkInitiator extends TileBase implements ITickable{

    @Override
    public void update(){
        if(!this.world.isRemote){
            if(this.world.getTotalWorldTime()%60 == 0){
                IBlockState state = this.world.getBlockState(this.pos);
                EnumFacing facing = state.getValue(BlockSparkInitiator.FACING);

                double x = this.pos.getX()+facing.getFrontOffsetX()+0.5;
                double y = this.pos.getY()+facing.getFrontOffsetY()+0.5;
                double z = this.pos.getZ()+facing.getFrontOffsetZ()+0.5;

                EntityPotentialSpark spark = new EntityPotentialSpark(this.world, x, y, z, facing, 0.1, 100);
                spark.setColor(0x97B4FF);
                this.world.spawnEntity(spark);
            }
        }
    }
}
