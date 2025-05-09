package com.Pink_Cats.createentitycontroller.mixin;

import com.Pink_Cats.createentitycontroller.Config;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllBlocks;

import com.simibubi.create.content.contraptions.BlockMovementChecks;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.contraptions.chassis.AbstractChassisBlock;
import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.infrastructure.config.AllConfigs;
import info.journeymap.shaded.org.jetbrains.annotations.Nullable;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.UniqueLinkedList;
import com.simibubi.create.foundation.utility.NBTProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock.isExtensionPole;
import static com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock.isPistonHead;

@Mixin(value = Contraption.class,remap = false)
public class ContraptionMixin {

    @Shadow
    protected boolean addToInitialFrontier(Level world, BlockPos pos, Direction forcedDirection,
                                           Queue<BlockPos> frontier) {
        return true;
    }
    @Shadow
    protected boolean movementAllowed(BlockState state, Level world, BlockPos pos) {
        return false;
    }
    @Shadow
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return pos.equals(anchor);
    }
    @Shadow
    private boolean moveChassis(Level world, BlockPos pos, Direction movementDirection, Queue<BlockPos> frontier,
                                Set<BlockPos> visited) {return false;}

    @Shadow  public AABB bounds;
    @Shadow  protected Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks;
    @Shadow  public BlockPos anchor;
    @Shadow  private Map<BlockPos, Entity> initialPassengers;
    @Shadow  private Set<SuperGlueEntity> glueToRemove;
	@Shadow  protected List<AABB> superglue;
	@Shadow  public boolean disassembled;
    @Shadow  protected void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair)  {}
    @Shadow  private void moveBelt(BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {}
	@Shadow  protected Multimap<BlockPos, StructureTemplate.StructureBlockInfo> capturedMultiblocks;
	@Shadow  protected MountedStorageManager storage;

	@Shadow
	protected boolean shouldUpdateAfterMovement(StructureTemplate.StructureBlockInfo info) {
		return false;
	}
	@Shadow
	protected boolean customBlockPlacement(LevelAccessor world, BlockPos pos, BlockState state) {
		return false;
	}
	@Shadow
	protected void translateMultiblockControllers(StructureTransform transform) {}
    @Shadow
    protected Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
        return null;
    }
    @Shadow
    protected void moveGantryPinion(Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited,
                                    BlockState state) {}
    @Shadow
    protected void moveGantryShaft(Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited,
                                   BlockState state) {}
    @Shadow
    private void moveBearing(BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {}
    @Shadow
    private void moveWindmillBearing(BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {}
    @Shadow
    private void moveSeat(Level world, BlockPos pos) {}
    @Shadow
    private void movePulley(Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited) {}
    @Shadow
    private boolean moveMechanicalPiston(Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited,
                                         BlockState state) {return true;}
    @Shadow
    protected void movePistonPole(Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited,
                                  BlockState state) {}
    @Shadow
    protected void movePistonHead(Level world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited,
                                  BlockState state) {}
	@Shadow
	protected boolean customBlockRemoval(LevelAccessor world, BlockPos pos, BlockState state) {
		return false;
	}

	@Shadow
	protected boolean moveBlock(Level world, @javax.annotation.Nullable Direction forcedDirection, Queue<BlockPos> frontier,
								Set<BlockPos> visited) {return false;}

	@Unique
	Map<String, Integer> blockCountMap_r = new HashMap<>();
	@Unique
	BlockPos createentitycontroller$minPos = new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	@Unique
	BlockPos createentitycontroller$maxPos = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

	@Unique
	private static final Logger createentitycontroller$LOGGER = LogUtils.getLogger();

    /*@Shadow
    protected boolean moveBlock(Level world, @javax.annotation.Nullable Direction forcedDirection, Queue<BlockPos> frontier,
                                Set<BlockPos> visited) throws AssemblyException {return true;}*/



	@Inject(
			method = "searchMovedStructure",
			at = @At("HEAD"), // 在方法开始时执行
			cancellable = true // 可选：如果需要可以取消原方法的执行
	)
	public void injectSearchMovedStructure(Level world, BlockPos pos, Direction forcedDirection, CallbackInfoReturnable<Boolean> cir) throws AssemblyException {
		initialPassengers.clear();
		// LOGGER.error("searchMovedStructure");

		Queue<BlockPos> frontier = new UniqueLinkedList<>();
		Set<BlockPos> visited = new HashSet<>();
		anchor = pos;

		if (bounds == null)
			bounds = new AABB(BlockPos.ZERO);

		if (!BlockMovementChecks.isBrittle(world.getBlockState(pos)))
			frontier.add(pos);

		if (!addToInitialFrontier(world, pos, forcedDirection, frontier))
		{
			cir.setReturnValue(false);
			return; }// 直接返回，继续执行原方法

		for (int limit = 100000; limit > 0; limit--) {
			if (frontier.isEmpty()) {
				if (Config.enableBlockEntityExperimentPara) {
					int totalValue = createentitycontroller$getTotalStabilizeValue();

					int globalCount = 0;
					for (Integer count : blockCountMap_r.values()) {
						globalCount += count;
					}
					System.setProperty("globalValue", Integer.toString(totalValue));
					System.setProperty("globalCount", Integer.toString(globalCount));

					if (totalValue > Config.block_entity_max_stabilize_count) {
						throw AssemblyException.structureTooLarge();
					}
				}
				cir.setReturnValue(true); // 返回 true，结束执行
				return;
			}
			if (!moveBlock(world, forcedDirection, frontier, visited)) {
				cir.setReturnValue(false); // 返回 false，结束执行
				return;
			}
		}
		throw AssemblyException.structureTooLarge();
	}



	@Unique
	private int createentitycontroller$getTotalStabilizeValue() {
		int totalValue = 0;
		int defaultValue = 100;
		for (Map.Entry<String, Integer> entry : blockCountMap_r.entrySet()) {
			String blockType = entry.getKey();
			Integer blockCount = entry.getValue();

			// 在 blocksLimitValues 中查找匹配的方块
			boolean found = false;
			for (List<Object> limitValue : Config.blocksLimitValues) {
				if (limitValue.size() > 1 && limitValue.get(0).equals(blockType)) {
					totalValue += blockCount * ((Integer) limitValue.get(2)); // 使用 limitValue 的第二个值
					found = true;
					break;
				}
			}

			// 如果方块不在 blocksLimitValues 中，使用默认值
			if (!found) {
				totalValue += blockCount * defaultValue;
			}
		}
		return totalValue;
	}

	@Inject(
			method = "moveBlock",
			at = @At("HEAD"), // 在方法开始时执行
			cancellable = true // 可选：如果需要可以取消原方法的执行
	)
	protected void injectMoveBlock(Level world, @Nullable Direction forcedDirection, Queue<BlockPos> frontier,
								   Set<BlockPos> visited, CallbackInfoReturnable<Boolean> cir) throws AssemblyException {
		BlockPos pos = frontier.poll();

		if (pos == null) {
			cir.setReturnValue(false);
			return;
		}
		visited.add(pos);

		if (world.isOutsideBuildHeight(pos)) {
			cir.setReturnValue(true);
			return;
		}
		if (!world.isLoaded(pos)) {
			throw AssemblyException.unloadedChunk(pos);
		}
		if (isAnchoringBlockAt(pos)) {
			cir.setReturnValue(true);
			return;
		}
		BlockState state = world.getBlockState(pos);
		if (!BlockMovementChecks.isMovementNecessary(state, world, pos)) {
			cir.setReturnValue(true);
			return;
		}
		if (!movementAllowed(state, world, pos)) {
			throw AssemblyException.unmovableBlock(pos, state);
		}
		if (state.getBlock() instanceof AbstractChassisBlock
				&& !moveChassis(world, pos, forcedDirection, frontier, visited)) {
			cir.setReturnValue(false);
			return;
		}

		// 继续你的逻辑
		if (AllBlocks.BELT.has(state)) {
			moveBelt(pos, frontier, visited, state);
		}

		if (AllBlocks.WINDMILL_BEARING.has(state) && world.getBlockEntity(pos) instanceof WindmillBearingBlockEntity wbbe) {
			wbbe.disassembleForMovement();
		}

		if (AllBlocks.GANTRY_CARRIAGE.has(state)) {
			moveGantryPinion(world, pos, frontier, visited, state);
		}

		if (AllBlocks.GANTRY_SHAFT.has(state)) {
			moveGantryShaft(world, pos, frontier, visited, state);
		}

		if (AllBlocks.STICKER.has(state) && state.getValue(StickerBlock.EXTENDED)) {
			Direction offset = state.getValue(StickerBlock.FACING);
			BlockPos attached = pos.relative(offset);
			if (!visited.contains(attached)
					&& !BlockMovementChecks.isNotSupportive(world.getBlockState(attached), offset.getOpposite())) {
				frontier.add(attached);
			}
		}


		// Double Chest halves stick together
		if (state.hasProperty(ChestBlock.TYPE) && state.hasProperty(ChestBlock.FACING)
				&& state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
			Direction offset = ChestBlock.getConnectedDirection(state);
			BlockPos attached = pos.relative(offset);
			if (!visited.contains(attached)) {
				frontier.add(attached);
			}
		}

		// Bogeys tend to have sticky sides
		if (state.getBlock() instanceof AbstractBogeyBlock<?> bogey) {
			for (Direction d : bogey.getStickySurfaces(world, pos, state)) {
				if (!visited.contains(pos.relative(d))) {
					frontier.add(pos.relative(d));
				}
			}
		}

		// Bearings potentially create stabilized sub-contraptions
		if (AllBlocks.MECHANICAL_BEARING.has(state)) {
			moveBearing(pos, frontier, visited, state);
		}

		// WM Bearings attach their structure when moved
		if (AllBlocks.WINDMILL_BEARING.has(state)) {
			moveWindmillBearing(pos, frontier, visited, state);
		}

		// Seats transfer their passenger to the contraption
		if (state.getBlock() instanceof SeatBlock) {
			moveSeat(world, pos);
		}

		// Pulleys drag their rope and their attached structure
		if (state.getBlock() instanceof PulleyBlock) {
			movePulley(world, pos, frontier, visited);
		}

		// Pistons drag their attaches poles and extension
		if (state.getBlock() instanceof MechanicalPistonBlock) {
			if (!moveMechanicalPiston(world, pos, frontier, visited, state)) {
				cir.setReturnValue(false);
				return;
			}
		}
		if (isExtensionPole(state)) {
			movePistonPole(world, pos, frontier, visited, state);
		}
		if (isPistonHead(state)) {
			movePistonHead(world, pos, frontier, visited, state);
		}

		// Cart assemblers attach themselves
		BlockPos posDown = pos.below();
		BlockState stateBelow = world.getBlockState(posDown);
		if (!visited.contains(posDown) && AllBlocks.CART_ASSEMBLER.has(stateBelow)) {
			frontier.add(posDown);
		}

		// Slime blocks and super glue drag adjacent blocks if possible
		for (Direction offset : Iterate.directions) {
			BlockPos offsetPos = pos.relative(offset);
			BlockState blockState = world.getBlockState(offsetPos);
			if (isAnchoringBlockAt(offsetPos)) {
				continue;
			}
			if (!movementAllowed(blockState, world, offsetPos)) {
				if (offset == forcedDirection) {
					throw AssemblyException.unmovableBlock(pos, state);
				}
				continue;
			}

			boolean wasVisited = visited.contains(offsetPos);
			boolean faceHasGlue = SuperGlueEntity.isGlued(world, pos, offset, glueToRemove);
			boolean blockAttachedTowardsFace =
					BlockMovementChecks.isBlockAttachedTowards(blockState, world, offsetPos, offset.getOpposite());
			boolean brittle = BlockMovementChecks.isBrittle(blockState);
			boolean canStick = !brittle && state.canStickTo(blockState) && blockState.canStickTo(state);
			if (canStick) {
				if (state.getPistonPushReaction() == PushReaction.PUSH_ONLY
						|| blockState.getPistonPushReaction() == PushReaction.PUSH_ONLY) {
					canStick = false;
				}
				if (BlockMovementChecks.isNotSupportive(state, offset)) {
					canStick = false;
				}
				if (BlockMovementChecks.isNotSupportive(blockState, offset.getOpposite())) {
					canStick = false;
				}
			}

			if (!wasVisited && (canStick || blockAttachedTowardsFace || faceHasGlue
					|| (offset == forcedDirection && !BlockMovementChecks.isNotSupportive(state, forcedDirection)))) {
				frontier.add(offsetPos);
			}
		}

		addBlock(pos, capture(world, pos));
		if (blocks.size() <= AllConfigs.server().kinetics.maxBlocksMoved.get()) {
			BlockState blockState = world.getBlockState(pos); // 获取方块状态
			String blockStateString = blockState.toString();
			if (Config.blocks_unmoved.stream().anyMatch(blockStateString::contains)) {
				throw AssemblyException.unmovableBlock(pos, state);
			}

			// count
			String blockString = blockState.toString();
			Pattern pattern = Pattern.compile("Block\\{(.*?)}");
			Matcher matcher = pattern.matcher(blockString);
			if (matcher.find()) {
				String blockName = matcher.group(1); // 提取方块名称

				// 更新字典中的数量
				blockCountMap_r.put(blockName, blockCountMap_r.getOrDefault(blockName, 0) + 1);
			}
			for (List<Object> entry : Config.blocksLimitValues) {
				// 解析条目
				String blockName = (String) entry.get(0); // 获取方块名称
				int allowedCount = (Integer) entry.get(1); // 获取允许的数量
				// 获取当前方块数量
				int currentCount = blockCountMap_r.getOrDefault(blockName, 0);
				// 检查是否超过允许数量
				if (currentCount > allowedCount) {
					// 可以选择抛出异常
					if (Config.debug_block_entity_problem) {
                        createentitycontroller$LOGGER.info("{} count: {} allowed: {}", blockName, currentCount, allowedCount);
					}
					throw AssemblyException.unmovableBlock(pos, state);
				}
			}

			// distance
			// System.out.println("pos: " + pos);

			if (pos.getX() < createentitycontroller$minPos.getX()) {
				createentitycontroller$minPos = new BlockPos(pos.getX(), createentitycontroller$minPos.getY(), createentitycontroller$minPos.getZ());
			}
			if (pos.getY() < createentitycontroller$minPos.getY()) {
				createentitycontroller$minPos = new BlockPos(createentitycontroller$minPos.getX(), pos.getY(), createentitycontroller$minPos.getZ());
			}
			if (pos.getZ() < createentitycontroller$minPos.getZ()) {
				createentitycontroller$minPos = new BlockPos(createentitycontroller$minPos.getX(), createentitycontroller$minPos.getY(), pos.getZ());
			}
			// 更新最大坐标
			if (pos.getX() > createentitycontroller$maxPos.getX()) {
				createentitycontroller$maxPos = new BlockPos(pos.getX(), createentitycontroller$maxPos.getY(), createentitycontroller$maxPos.getZ());
			}
			if (pos.getY() > createentitycontroller$maxPos.getY()) {
				createentitycontroller$maxPos = new BlockPos(createentitycontroller$maxPos.getX(), pos.getY(), createentitycontroller$maxPos.getZ());
			}
			if (pos.getZ() > createentitycontroller$maxPos.getZ()) {
				createentitycontroller$maxPos = new BlockPos(createentitycontroller$maxPos.getX(), createentitycontroller$maxPos.getY(), pos.getZ());
			}

			// System.out.println("maxPos: " + maxPos +", minPos: " + minPos);
			if ((createentitycontroller$maxPos.getX() - createentitycontroller$minPos.getX()) > Config.blockEntityXZMaxLength) {
				throw AssemblyException.unmovableBlock(pos, state);
			}
			if ((createentitycontroller$maxPos.getY() - createentitycontroller$minPos.getY()) > Config.blockEntityYMaxLength) {
				throw AssemblyException.unmovableBlock(pos, state);
			}
			if ((createentitycontroller$maxPos.getZ() - createentitycontroller$minPos.getZ()) > Config.blockEntityXZMaxLength) {
				throw AssemblyException.unmovableBlock(pos, state);
			}

			cir.setReturnValue(true);
        } else {
			throw AssemblyException.structureTooLarge();
		}
	}


	/**
	 * @author Pink_Cats
	 * @reason catch_add_block_base
	 */
	@Inject(
			method = "addBlocksToWorld",
			at = @At("HEAD") // 在方法开始时执行
			// 可选：如果需要可以取消原方法的执行
	)
	public void injectAddBlocksToWorld(Level world, StructureTransform transform, CallbackInfo ci) {
		if (disassembled) {
			return;
		}
		disassembled = true;

		translateMultiblockControllers(transform);

		for (boolean nonBrittles : Iterate.trueAndFalse) {
			for (StructureTemplate.StructureBlockInfo block : blocks.values()) {
				if (nonBrittles == BlockMovementChecks.isBrittle(block.state())) {
					continue;
				}

				BlockPos targetPos = transform.apply(block.pos());
				BlockState state = transform.apply(block.state());

				if (customBlockPlacement(world, targetPos, state)) {
					continue;
				}

				// 处理非脆弱块
				if (nonBrittles) {
					for (Direction face : Iterate.directions) {
						state = state.updateShape(face, world.getBlockState(targetPos.relative(face)), world, targetPos,
								targetPos.relative(face));
					}
				}

				BlockState blockState = world.getBlockState(targetPos);
				boolean squeezeBlock;
				String blockStateString = blockState.toString();
				boolean isInWhitelist = Config.blocks_uncrushable.stream().anyMatch(blockStateString::contains);
				boolean isInIgnoreList = Config.blocks_uncrushableIgnore.stream().anyMatch(blockStateString::contains);

				if (isInWhitelist) {
					squeezeBlock = true;
				} else if (isInIgnoreList) {
					squeezeBlock = false;
				} else {
					squeezeBlock = (blockState.getDestroySpeed(world, targetPos) > Config.squeeze_destroy_speed);
				}

				if (blockState.getDestroySpeed(world, targetPos) == -1 || squeezeBlock ||
						(state.getCollisionShape(world, targetPos).isEmpty() && !blockState.getCollisionShape(world, targetPos).isEmpty())) {

					if (targetPos.getY() == world.getMinBuildHeight()) {
						targetPos = targetPos.above();
					}
					world.levelEvent(2001, targetPos, Block.getId(state));
					Block.dropResources(state, world, targetPos, null);
					continue;
				}

				// 继续原方法的其余逻辑
				if (state.getBlock() instanceof SimpleWaterloggedBlock && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
					FluidState fluidState = world.getFluidState(targetPos);
					state = state.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
				}

				world.destroyBlock(targetPos, true);

				if (AllBlocks.SHAFT.has(state)) {
					state = ShaftBlock.pickCorrectShaftType(state, world, targetPos);
				}
				if (state.hasProperty(SlidingDoorBlock.VISIBLE)) {
					state = state.setValue(SlidingDoorBlock.VISIBLE, !state.getValue(SlidingDoorBlock.OPEN))
							.setValue(SlidingDoorBlock.POWERED, false);
				}

				// 处理 Sculk Shriekers
				if (state.is(Blocks.SCULK_SHRIEKER)) {
					state = Blocks.SCULK_SHRIEKER.defaultBlockState();
				}

				world.setBlock(targetPos, state, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL);

				boolean verticalRotation = transform.rotationAxis == null || transform.rotationAxis.isHorizontal();
				verticalRotation = verticalRotation && transform.rotation != Rotation.NONE;
				if (verticalRotation) {
					if (state.getBlock() instanceof PulleyBlock.RopeBlock ||
							state.getBlock() instanceof PulleyBlock.MagnetBlock ||
							state.getBlock() instanceof DoorBlock) {
						world.destroyBlock(targetPos, true);
					}
				}

				BlockEntity blockEntity = world.getBlockEntity(targetPos);
				CompoundTag tag = block.nbt();

				// 处理 Sculk Sensor
				if (state.is(Blocks.SCULK_SENSOR) || state.is(Blocks.SCULK_SHRIEKER)) {
					tag = null;
				}

				if (blockEntity != null) {
					tag = NBTProcessors.process(state, blockEntity, tag, false);
					if (tag != null) {
						tag.putInt("x", targetPos.getX());
						tag.putInt("y", targetPos.getY());
						tag.putInt("z", targetPos.getZ());

						if (verticalRotation && blockEntity instanceof PulleyBlockEntity) {
							tag.remove("Offset");
							tag.remove("InitialOffset");
						}

						if (blockEntity instanceof IMultiBlockEntityContainer) {
							if (tag.contains("LastKnownPos") || capturedMultiblocks.isEmpty()) {
								tag.put("LastKnownPos", NbtUtils.writeBlockPos(BlockPos.ZERO.below(Integer.MAX_VALUE - 1)));
								tag.remove("Controller");
							}
						}

						blockEntity.load(tag);
						storage.addStorageToWorld(block, blockEntity);
					}
				}

				if (blockEntity != null) {
					transform.apply(blockEntity);
				}
			}
		}

		for (StructureTemplate.StructureBlockInfo block : blocks.values()) {
			if (!shouldUpdateAfterMovement(block)) {
				continue;
			}
			BlockPos targetPos = transform.apply(block.pos());
			world.markAndNotifyBlock(targetPos, world.getChunkAt(targetPos), block.state(), block.state(),
					Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL, 512);
		}

		for (AABB box : superglue) {
			box = new AABB(transform.apply(new Vec3(box.minX, box.minY, box.minZ)),
					transform.apply(new Vec3(box.maxX, box.maxY, box.maxZ)));
			if (!world.isClientSide) {
				world.addFreshEntity(new SuperGlueEntity(world, box));
			}

			storage.clear();
		}
	}
}


