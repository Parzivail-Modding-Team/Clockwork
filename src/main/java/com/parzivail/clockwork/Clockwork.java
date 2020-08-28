package com.parzivail.clockwork;

import com.parzivail.clockwork.block.VariableClockBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Clockwork implements ModInitializer
{
	public static final String MODID = "clockwork";

	public static final Identifier ID_VARIABLE_CLOCK = new Identifier(MODID, "variable_clock");
	public static final Block BLOCK_VARIABLE_CLOCK = new VariableClockBlock();

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "general"), () -> new ItemStack(BLOCK_VARIABLE_CLOCK));

	@Override
	public void onInitialize()
	{
		registerBlock(ID_VARIABLE_CLOCK, BLOCK_VARIABLE_CLOCK);
	}

	public static void registerBlock(Identifier id, Block block)
	{
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ITEM_GROUP)));
	}
}
