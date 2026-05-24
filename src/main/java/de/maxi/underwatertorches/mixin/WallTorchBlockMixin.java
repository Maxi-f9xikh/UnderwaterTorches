package de.maxi.underwatertorches.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallTorchBlock.class)
public abstract class WallTorchBlockMixin extends Block implements Waterloggable {

	private WallTorchBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void underwatertorches$setDefaultWaterlogged(CallbackInfo ci) {
		this.setDefaultState(this.getDefaultState().with(Properties.WATERLOGGED, false));
	}

	@Inject(method = "appendProperties", at = @At("TAIL"))
	private void underwatertorches$addWaterlogged(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(Properties.WATERLOGGED);
	}

	@Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
	private void underwatertorches$waterlogPlacement(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
		BlockState state = cir.getReturnValue();
		if (state != null) {
			FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
			cir.setReturnValue(state.with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER));
		}
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
	private void underwatertorches$scheduleFluidTick(BlockState state, Direction direction, BlockState neighborState,
			WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
		if (state.get(Properties.WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
	}
}
