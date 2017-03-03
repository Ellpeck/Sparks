package de.ellpeck.sparks.api.iface.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMultitoolInteract{

    boolean interactWithMultitool(World world, BlockPos pos, EntityPlayer player, EnumHand hand);

}
