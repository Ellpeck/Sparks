package de.ellpeck.deflection.mod.tile;

import com.google.common.base.Predicate;
import de.ellpeck.deflection.mod.entity.spark.EntityPickupSpark;
import de.ellpeck.deflection.mod.util.ModUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Collections;
import java.util.List;

public class TilePickerUpper extends TileBase implements ITickable{

    public static final Predicate<EntityItem> PICKUP_PREDICATE = new Predicate<EntityItem>(){
        @Override
        public boolean apply(EntityItem input){
            if(!input.isDead){
                NBTTagCompound data = input.getEntityData();
                return !data.getBoolean(ModUtil.MOD_ID+"Pickup");
            }
            else{
                return false;
            }
        }
    };

    @Override
    public void update(){
        if(!this.world.isRemote){
            if(this.world.getTotalWorldTime()%20 == 0){
                double x = this.pos.getX()+0.5;
                double y = this.pos.getY()+0.5;
                double z = this.pos.getZ()+0.5;

                AxisAlignedBB aabb = new AxisAlignedBB(x-5, y-5, z-5, x+5, y+5, z+5);
                List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, aabb, PICKUP_PREDICATE);

                if(items != null && !items.isEmpty()){
                    Collections.shuffle(items);

                    EntityPickupSpark spark = new EntityPickupSpark(this.world, x, y, z, this.pos, items.get(0));
                    spark.setColor(0x97B4FF);
                    this.world.spawnEntity(spark);
                }
            }
        }
    }
}
