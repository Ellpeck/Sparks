package de.ellpeck.deflection.mod;

import de.ellpeck.deflection.mod.block.BlockRegistry;
import de.ellpeck.deflection.mod.entity.EntityManager;
import de.ellpeck.deflection.mod.item.ItemRegistry;
import de.ellpeck.deflection.mod.proxy.IProxy;
import de.ellpeck.deflection.mod.reg.ModRegistry;
import de.ellpeck.deflection.mod.util.ModUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModUtil.MOD_ID, name = ModUtil.NAME, version = ModUtil.VERSION)
public class Deflection{

    @Instance
    public static Deflection instance;

    @SidedProxy(clientSide = ModUtil.CLIENT_PROXY, serverSide = ModUtil.SERVER_PROXY)
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        BlockRegistry.preInit();
        ItemRegistry.preInit();
        ModRegistry.preInit(event);

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