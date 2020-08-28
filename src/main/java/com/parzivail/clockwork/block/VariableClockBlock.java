package com.parzivail.clockwork.block;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class VariableClockBlock extends Block
{
	public static final IntProperty ON_INTERVAL;
	public static final IntProperty OFF_INTERVAL;
	public static final BooleanProperty POWERED;

	static
	{
		ON_INTERVAL = IntProperty.of("on_interval", 1, 10);
		OFF_INTERVAL = IntProperty.of("off_interval", 1, 10);
		POWERED = Properties.POWERED;
	}

	public VariableClockBlock()
	{
		super(FabricBlockSettings.of(Material.METAL).breakByHand(true).breakInstantly().build());
		this.setDefaultState(this.stateManager.getDefaultState().with(ON_INTERVAL, 5).with(OFF_INTERVAL, 5).with(POWERED, false));
	}

	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		boolean isPowered = state.get(POWERED);
		world.setBlockState(pos, state.with(POWERED, !isPowered), 3);
		world.getBlockTickScheduler().schedule(pos, this, state.get(isPowered ? OFF_INTERVAL : ON_INTERVAL), TickPriority.VERY_HIGH);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
	{
		super.onPlaced(world, pos, state, placer, itemStack);
		world.getBlockTickScheduler().schedule(pos, this, state.get(ON_INTERVAL), TickPriority.VERY_HIGH);
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!player.abilities.allowModifyWorld)
		{
			return ActionResult.PASS;
		}
		else
		{
			if (player.isSneaking())
			{
				world.setBlockState(pos, state.cycle(OFF_INTERVAL), 3);
				int off = world.getBlockState(pos).get(OFF_INTERVAL);
				int on = world.getBlockState(pos).get(ON_INTERVAL);
				player.addChatMessage(new TranslatableText("block.clockwork.variable_clock.off_interval", off, off + on), true);
			}
			else
			{
				world.setBlockState(pos, state.cycle(ON_INTERVAL), 3);
				int off = world.getBlockState(pos).get(OFF_INTERVAL);
				int on = world.getBlockState(pos).get(ON_INTERVAL);
				player.addChatMessage(new TranslatableText("block.clockwork.variable_clock.on_interval", on, off + on), true);
			}
			return ActionResult.SUCCESS;
		}
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(ON_INTERVAL, OFF_INTERVAL, POWERED);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state)
	{
		return true;
	}

	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing)
	{
		return state.get(POWERED) ? 15 : 0;
	}
}
