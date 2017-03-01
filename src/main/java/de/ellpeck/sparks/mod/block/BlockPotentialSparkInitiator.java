package de.ellpeck.sparks.mod.block;

import de.ellpeck.sparks.api.cap.IPotentialHandler;
import de.ellpeck.sparks.api.cap.SparksCapabilities;
import de.ellpeck.sparks.api.iface.ISpark;
import de.ellpeck.sparks.api.iface.ISparkInteractor;
import de.ellpeck.sparks.mod.entity.spark.EntityPotentialSpark;
import de.ellpeck.sparks.mod.entity.spark.pickup.EntityPotentialPickupSpark;
import de.ellpeck.sparks.mod.item.ItemStaff;
import de.ellpeck.sparks.mod.tile.TilePotentialSparkInitiator;
import de.ellpeck.sparks.mod.util.CachedEntity;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

//TODO Add interaction to connect spark handlers to the initiator
//TODO Fix spawn/arrival positions in initiator
public class BlockPotentialSparkInitiator extends BlockContainerBase implements ISparkInteractor{

    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockPotentialSparkInitiator(){
        super(Material.ROCK, "potential_spark_initiator", TilePotentialSparkInitiator.class, "potential_spark_initiator");
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack){
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public EnumActionResult interact(World world, BlockPos pos, IBlockState state, ISpark spark){
        if(spark instanceof EntityPotentialPickupSpark){
            EntityPotentialPickupSpark pickup = (EntityPotentialPickupSpark)spark;
            if(pos.equals(pickup.getLastInteractor())){
                int amount = pickup.getPotential();
                if(amount > 0){
                    TileEntity tile = world.getTileEntity(pos);
                    if(tile instanceof TilePotentialSparkInitiator){
                        ((TilePotentialSparkInitiator)tile).accumulatedPotential += pickup.getPotential();

                        pickup.setPotential(0);
                        pickup.setKilled();

                        return EnumActionResult.SUCCESS;
                    }
                }
                else if(pickup.ticksExisted <= 20){
                    return EnumActionResult.PASS;
                }
            }
        }
        else if(spark instanceof EntityPotentialSpark && ((EntityPotentialSpark)spark).ticksExisted <= 5){
            return EnumActionResult.PASS;
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(stack != null && stack.getItem() instanceof ItemStaff){
            BlockPos storedPos = ItemStaff.getStoredPos(stack);
            if(storedPos != null){
                TileEntity storedTile = world.getTileEntity(storedPos);
                if(storedTile != null){
                    if(!world.isRemote){
                        if(storedTile.hasCapability(SparksCapabilities.capabilityPotential, null)){
                            TileEntity tile = world.getTileEntity(pos);
                            if(tile instanceof TilePotentialSparkInitiator){
                                ((TilePotentialSparkInitiator)tile).connectedHandlers.put(storedPos, new CachedEntity<EntityPotentialPickupSpark>());
                                tile.markDirty();

                                ItemStaff.setStoredPos(stack, null);
                            }
                        }
                    }
                    return true;
                }
            }
        }
        else{
            if(!world.isRemote){
                TileEntity tile = world.getTileEntity(pos);
                if(tile instanceof TilePotentialSparkInitiator){
                    player.sendMessage(new TextComponentString("Stored: "+((TilePotentialSparkInitiator)tile).accumulatedPotential));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }
}
