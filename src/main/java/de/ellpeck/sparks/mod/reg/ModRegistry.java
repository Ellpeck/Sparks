package de.ellpeck.sparks.mod.reg;

import de.ellpeck.sparks.mod.Sparks;
import de.ellpeck.sparks.mod.reg.IModelProvider.ModelVariant;
import de.ellpeck.sparks.mod.util.CreativeTab;
import de.ellpeck.sparks.mod.util.ModUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ModRegistry{

    private static final List<IModItem> ALL_ITEMS = new ArrayList<IModItem>();

    public static void addItemOrBlock(IModItem item){
        ALL_ITEMS.add(item);
    }

    private static void registerItem(Item item, String name, boolean addCreative){
        item.setUnlocalizedName(ModUtil.MOD_ID+"."+name);

        item.setRegistryName(ModUtil.MOD_ID, name);
        GameRegistry.register(item);

        if(addCreative){
            item.setCreativeTab(CreativeTab.INSTANCE);
        }
        else{
            item.setCreativeTab(null);
        }
    }

    private static void registerBlock(Block block, String name, ItemBlock item, boolean addCreative){
        block.setUnlocalizedName(ModUtil.MOD_ID+"."+name);

        block.setRegistryName(ModUtil.MOD_ID, name);
        GameRegistry.register(block);

        item.setRegistryName(block.getRegistryName());
        GameRegistry.register(item);

        if(addCreative){
            block.setCreativeTab(CreativeTab.INSTANCE);
        }
        else{
            block.setCreativeTab(null);
        }
    }

    public static void preInit(FMLPreInitializationEvent event){
        for(IModItem item : ALL_ITEMS){
            if(item instanceof Item){
                registerItem((Item)item, item.getBaseName(), item.shouldAddCreative());
            }
            else if(item instanceof Block){
                Block block = (Block)item;

                ItemBlock itemBlock;
                if(item instanceof ICustomItemBlockProvider){
                    itemBlock = ((ICustomItemBlockProvider)item).getItemBlock();
                }
                else{
                    itemBlock = new ItemBlock(block);
                }

                registerBlock(block, item.getBaseName(), itemBlock, item.shouldAddCreative());
            }

            if(item instanceof IModelProvider){
                Map<ItemStack, ModelVariant> models = ((IModelProvider)item).getModelLocations();

                for(ItemStack stack : models.keySet()){
                    ModelVariant variant = models.get(stack);
                    Sparks.proxy.registerRenderer(stack, variant.location, variant.variant);
                }
            }

            item.onPreInit(event);
        }
    }

    public static void init(FMLInitializationEvent event){
        for(IModItem item : ALL_ITEMS){
            if(item instanceof IColorProvidingBlock){
                Sparks.proxy.addColorProvidingBlock((IColorProvidingBlock)item);
            }

            if(item instanceof IColorProvidingItem){
                Sparks.proxy.addColorProvidingItem((IColorProvidingItem)item);
            }

            item.onInit(event);
        }
    }

    public static void postInit(FMLPostInitializationEvent event){
        for(IModItem item : ALL_ITEMS){
            item.onPostInit(event);
        }
    }
}
