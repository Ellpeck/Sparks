package de.ellpeck.sparks.mod.block;

import de.ellpeck.sparks.api.cap.SparksCapabilities;
import de.ellpeck.sparks.api.iface.ISpark;
import de.ellpeck.sparks.api.iface.ISparkInteractor;
import de.ellpeck.sparks.api.iface.tool.IMultitoolInteract;
import de.ellpeck.sparks.api.iface.tool.IMultitoolVisualize;
import de.ellpeck.sparks.mod.Sparks;
import de.ellpeck.sparks.mod.entity.spark.EntityPotentialSpark;
import de.ellpeck.sparks.mod.entity.spark.pickup.EntityPotentialPickupSpark;
import de.ellpeck.sparks.mod.item.ItemMultitool;
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
import net.minecraft.world.World;

public class BlockPotentialSparkInitiator extends BlockContainerBase implements ISparkInteractor, IMultitoolInteract, IMultitoolVisualize{

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
                        ((TilePotentialSparkInitiator)tile).storage.receiveInternal(pickup.getPotential(), false);

                        pickup.setPotential(0);
                        pickup.setKilled();

                        return EnumActionResult.SUCCESS;
                    }
                }
                else if(pickup.ticksExisted <= 5){
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
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }

    @Override
    public boolean interact(World world, BlockPos pos, EntityPlayer player, EnumHand hand){
        ItemStack stack = player.getHeldItem(hand);
        BlockPos storedPos = ItemMultitool.getStoredPos(stack);
        if(storedPos != null){
            TileEntity storedTile = world.getTileEntity(storedPos);
            if(storedTile != null){
                if(!world.isRemote){
                    if(storedTile.hasCapability(SparksCapabilities.capabilityPotential, null)){
                        TileEntity tile = world.getTileEntity(pos);
                        if(tile instanceof TilePotentialSparkInitiator){
                            TilePotentialSparkInitiator initiator = (TilePotentialSparkInitiator)tile;
                            initiator.connectedHandlers.put(storedPos, new CachedEntity<EntityPotentialPickupSpark>());
                            initiator.markDirty();
                            initiator.sendToClient();

                            ItemMultitool.setStoredPos(stack, null);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean visualize(World world, BlockPos pos, EntityPlayer player, EnumHand hand){
        if(world.isRemote){
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TilePotentialSparkInitiator){
                for(BlockPos aPos : ((TilePotentialSparkInitiator)tile).connectedHandlers.keySet()){
                    Sparks.proxy.spawnMagicParticle(world, aPos.getX()+0.5, aPos.getY()+1.25, aPos.getZ()+0.5, 0, 0, 0, 0x8E8E00, 2F, 100, 0F, false);
                }
                Sparks.proxy.spawnMagicParticle(world, pos.getX()+0.5, pos.getY()+1.25, pos.getZ()+0.5, 0, 0, 0, 0x8E8E00, 2F, 100, 0F, false);

                return true;
            }
        }
        return false;
    }
}
