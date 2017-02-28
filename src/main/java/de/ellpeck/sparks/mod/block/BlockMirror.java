package de.ellpeck.sparks.mod.block;

import de.ellpeck.sparks.api.iface.ISpark;
import de.ellpeck.sparks.api.iface.ISparkInteractor;
import de.ellpeck.sparks.api.iface.ITravellingSpark;
import de.ellpeck.sparks.mod.packet.PacketHandler;
import de.ellpeck.sparks.mod.packet.PacketParticleExplosion;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

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
    public EnumActionResult interact(World world, BlockPos pos, IBlockState state, ISpark spark){
        if(spark instanceof ITravellingSpark){
            ITravellingSpark travelling = (ITravellingSpark)spark;
            EnumFacing facing = travelling.getFacing();
            double x = travelling.getX();
            double y = travelling.getY();
            double z = travelling.getZ();

            MirrorType type = state.getValue(TYPE);
            EnumFacing direction = type.getDeflectionDirection(facing.getOpposite());
            if(direction != null){
                if(x >= pos.getX()+0.45 && x <= pos.getX()+0.55 && y >= pos.getY()+0.45 && y <= pos.getY()+0.55 && z >= pos.getZ()+0.45 && z <= pos.getZ()+0.55){
                    travelling.setFacing(direction);
                    travelling.setLastInteractor(pos);

                    PacketParticleExplosion packet = new PacketParticleExplosion(x, y, z, travelling.getColor(), 20, 0.01, 1.5F, false);
                    PacketHandler.sendToAllAround(world, pos, packet);

                    return EnumActionResult.SUCCESS;
                }
                return EnumActionResult.PASS;
            }
            else if(pos.equals(travelling.getLastInteractor())){
                return EnumActionResult.PASS;
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack){
        return this.getDefaultState().withProperty(TYPE, MirrorType.fromStandingFacing(placer.getHorizontalFacing().getOpposite(), facing == EnumFacing.DOWN));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(player.isSneaking()){
            if(!world.isRemote){
                MirrorType currType = state.getValue(TYPE);
                List<MirrorType> types = MirrorType.getTypesForFace(facing);

                int index = types.indexOf(currType)+1;
                if(index >= types.size()){
                    index = 0;
                }

                MirrorType nextType = types.get(index);
                IBlockState newState = state.withProperty(TYPE, nextType);

                world.setBlockState(pos, newState);
                SoundType type = this.getSoundType(newState, world, pos, player);
                world.playSound(null, pos, type.getPlaceSound(), SoundCategory.BLOCKS, (type.getVolume()+1.0F)/2.0F, type.getPitch()*0.8F);
            }
            return true;
        }
        else{
            return false;
        }
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

        public static MirrorType fromStandingFacing(EnumFacing facing, boolean onCeiling){
            switch(facing){
                case NORTH:
                    return onCeiling ? NORTH_DOWN : NORTH_UP;
                case EAST:
                    return onCeiling ? EAST_DOWN : EAST_UP;
                case SOUTH:
                    return onCeiling ? SOUTH_DOWN : SOUTH_UP;
                default:
                    return onCeiling ? WEST_DOWN : WEST_UP;
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

        public static List<MirrorType> getTypesForFace(EnumFacing facing){
            List<MirrorType> possible = new ArrayList<MirrorType>();

            for(MirrorType type : values()){
                if(type.in == facing || type.out == facing){
                    possible.add(type);
                }
            }

            return possible;
        }

        @Override
        public String getName(){
            return this.name;
        }
    }
}
