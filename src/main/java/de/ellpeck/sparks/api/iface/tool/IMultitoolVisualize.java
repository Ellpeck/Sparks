package de.ellpeck.sparks.api.iface.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMultitoolVisualize{

    boolean visualize(World world, BlockPos pos, EntityPlayer player, EnumHand hand);

}
