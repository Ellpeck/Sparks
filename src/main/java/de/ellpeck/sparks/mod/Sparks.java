package de.ellpeck.sparks.mod;

import de.ellpeck.sparks.api.cap.IPotentialHandler;
import de.ellpeck.sparks.api.cap.PotentialStorage;
import de.ellpeck.sparks.api.cap.SparksCapabilities.CapabilityPotential;
import de.ellpeck.sparks.mod.block.BlockRegistry;
import de.ellpeck.sparks.mod.entity.EntityManager;
import de.ellpeck.sparks.mod.item.ItemRegistry;
import de.ellpeck.sparks.mod.packet.PacketHandler;
import de.ellpeck.sparks.mod.proxy.IProxy;
import de.ellpeck.sparks.mod.reg.ModRegistry;
import de.ellpeck.sparks.mod.util.ModUtil;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModUtil.MOD_ID, name = ModUtil.NAME, version = ModUtil.VERSION)
public class Sparks{

    @Instance
    public static Sparks instance;

    @SidedProxy(clientSide = ModUtil.CLIENT_PROXY, serverSide = ModUtil.SERVER_PROXY)
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        CapabilityManager.INSTANCE.register(IPotentialHandler.class, new CapabilityPotential(), PotentialStorage.class);

        BlockRegistry.preInit();
        ItemRegistry.preInit();
        ModRegistry.preInit(event);

        PacketHandler.preInit();
        EntityManager.preInit();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        ModRegistry.init(event);
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        ModRegistry.postInit(event);
        proxy.postInit(event);
    }
}