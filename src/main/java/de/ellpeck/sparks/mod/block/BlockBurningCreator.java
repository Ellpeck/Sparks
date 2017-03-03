package de.ellpeck.sparks.mod.block;

import de.ellpeck.sparks.api.cap.IPotentialHandler;
import de.ellpeck.sparks.api.cap.SparksCapabilities;
import de.ellpeck.sparks.api.iface.ISpark;
import de.ellpeck.sparks.api.iface.ISparkInteractor;
import de.ellpeck.sparks.mod.entity.spark.pickup.EntityItemPickupSpark;
import de.ellpeck.sparks.mod.tile.TileBurningCreator;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBurningCreator extends BlockContainerBase implements ISparkInteractor{

    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    private static final float F = 1F/16F;
    private static final AxisAlignedBB AABB = new AxisAlignedBB(2*F, 0, 2*F, 1-2*F, 1-4*F, 1-2*F);

    public BlockBurningCreator(){
        super(Material.ROCK, "burning_creator", TileBurningCreator.class, "burning_creator");
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return AABB;
    }

    @Override
    public EnumActionResult interactWithSpark(World world, BlockPos pos, IBlockState state, ISpark spark){
        if(spark instanceof EntityItemPickupSpark){
            EntityItemPickupSpark pickup = (EntityItemPickupSpark)spark;
            if(pos.equals(pickup.getLastInteractor())){
                ItemStack stack = pickup.getCarryingStack();
                if(stack != null){
                    TileEntity tile = world.getTileEntity(pos);
                    if(tile instanceof TileBurningCreator){
                        if(((TileBurningCreator)tile).fuel(stack)){
                            pickup.setCarryingStack(null);
                            pickup.setKilled();

                            return EnumActionResult.SUCCESS;
                        }
                    }
                }
                else if(pickup.ticksExisted <= 5){
                    return EnumActionResult.PASS;
                }
            }
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
    public int getMetaFromState(IBlockState state){
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack){
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
}
