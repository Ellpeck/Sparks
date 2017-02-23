package de.ellpeck.deflection.mod.block;

import de.ellpeck.deflection.api.iface.ISpark;
import de.ellpeck.deflection.api.iface.ISparkInteractor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMirror extends BlockBase implements ISparkInteractor{

    public static final PropertyEnum<MirrorType> TYPE = PropertyEnum.create("type", MirrorType.class);

    private static final float F = 1F/16F;
    private static final AxisAlignedBB FLOOR_AABB = new AxisAlignedBB(2*F, 0, 2*F, 1-2*F, 1-3*F, 1-2*F);
    private static final AxisAlignedBB CEIL_AABB = new AxisAlignedBB(2*F, 3*F, 2*F, 1-2*F, 1, 1-2*F);

    public BlockMirror(){
        super(Material.ROCK, "mirror");
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        MirrorType type = state.getValue(TYPE);
        return type.isOnCeiling ? CEIL_AABB : FLOOR_AABB;
    }

    @Override
    public void interact(World world, BlockPos pos, IBlockState state, ISpark spark){
        double x = spark.getX();
        double y = spark.getY();
        double z = spark.getZ();

        if(x >= pos.getX()+0.45 && x <= pos.getX()+0.55 && y >= pos.getY()+0.45 && y <= pos.getY()+0.55 && z >= pos.getZ()+0.45 && z <= pos.getZ()+0.55){
            MirrorType type = state.getValue(TYPE);
            EnumFacing direction = type.getDeflectionDirection(spark.getFacing().getOpposite());

            if(direction != null){
                spark.setFacing(direction);
            }
        }
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
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(TYPE).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return this.getDefaultState().withProperty(TYPE, MirrorType.values()[meta]);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
        return this.getDefaultState().withProperty(TYPE, MirrorType.fromStandingFacing(EnumFacing.getDirectionFromEntityLiving(pos, placer)));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(!world.isRemote){
            MirrorType[] types = MirrorType.values();

            int currIndex = state.getValue(TYPE).ordinal();

            if(player.isSneaking()){
                currIndex--;
                if(currIndex < 0){
                    currIndex = types.length-1;
                }
            }
            else{
                currIndex++;
                if(currIndex >= types.length){
                    currIndex = 0;
                }
            }

            world.setBlockState(pos, state.withProperty(TYPE, types[currIndex]));
        }
        return true;
    }

    private enum MirrorType implements IStringSerializable{
        NORTH_EAST("north_east", EnumFacing.NORTH, EnumFacing.EAST, false),
        SOUTH_EAST("south_east", EnumFacing.SOUTH, EnumFacing.EAST, false),
        NORTH_WEST("north_west", EnumFacing.NORTH, EnumFacing.WEST, false),
        SOUTH_WEST("south_west", EnumFacing.SOUTH, EnumFacing.WEST, false),

        NORTH_UP("north_up", EnumFacing.NORTH, EnumFacing.UP, false),
        NORTH_DOWN("north_down", EnumFacing.NORTH, EnumFacing.DOWN, true),

        EAST_UP("east_up", EnumFacing.EAST, EnumFacing.UP, false),
        EAST_DOWN("east_down", EnumFacing.EAST, EnumFacing.DOWN, true),

        SOUTH_UP("south_up", EnumFacing.SOUTH, EnumFacing.UP, false),
        SOUTH_DOWN("south_down", EnumFacing.SOUTH, EnumFacing.DOWN, true),

        WEST_UP("west_up", EnumFacing.WEST, EnumFacing.UP, false),
        WEST_DOWN("west_down", EnumFacing.WEST, EnumFacing.DOWN, true),;

        public final EnumFacing in;
        public final EnumFacing out;
        public final String name;
        public final boolean isOnCeiling;

        MirrorType(String name, EnumFacing in, EnumFacing out, boolean isOnCeiling){
            this.name = name;
            this.in = in;
            this.out = out;
            this.isOnCeiling = isOnCeiling;
        }

        public static MirrorType fromStandingFacing(EnumFacing facing){
            switch(facing){
                case NORTH:
                    return NORTH_UP;
                case EAST:
                    return EAST_UP;
                case SOUTH:
                    return SOUTH_UP;
                case WEST:
                    return WEST_UP;
                case UP:
                    return SOUTH_WEST;
                default:
                    return SOUTH_WEST;
            }
        }

        public EnumFacing getDeflectionDirection(EnumFacing input){
            if(input == this.in){
                return this.out;
            }
            else if(input == this.out){
                return this.in;
            }
            else{
                return null;
            }
        }

        @Override
        public String getName(){
            return this.name;
        }
    }
}
