package com.Pink_Cats.createentitycontrol.mixin;

import com.Pink_Cats.createentitycontrol.Config;
import com.Pink_Cats.createentitycontrol.addition.EntityEnrollment;
import com.Pink_Cats.createentitycontrol.addition.OpacCompatBridge;
import com.Pink_Cats.createentitycontrol.addition.StructureBlockStorage;
import com.Pink_Cats.createentitycontrol.addition.StructureFunc;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllBlocks;

import com.simibubi.create.content.contraptions.BlockMovementChecks;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.NBTProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;


import static com.Pink_Cats.createentitycontrol.addition.StructureBlockStorage.generateRandomUUID;

@Mixin(value = Contraption.class,remap = false)
public class ContraptionMixin {

    @Shadow  protected Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks;
	@Shadow  protected List<AABB> superglue;
	@Shadow  public boolean disassembled;
    @Shadow  protected void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair)  {}
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

	@Unique
	Map<String, Integer> blockCountMap_r = new HashMap<>();
	@Unique
	BlockPos createentitycontrol$minPos = new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	@Unique
	BlockPos createentitycontrol$maxPos = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

	@Unique
	private static final Logger createentitycontrol$LOGGER = LogUtils.getLogger();




	@Inject(method = "searchMovedStructure", at = @At("HEAD"))
	public void createentitycontrol$resetSearchMovedStructureState(Level world, BlockPos pos, Direction forcedDirection,
																   CallbackInfoReturnable<Boolean> cir) {
		blockCountMap_r.clear();
		createentitycontrol$minPos = new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		createentitycontrol$maxPos = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	@Inject(method = "searchMovedStructure", at = @At("RETURN"))
	public void createentitycontrol$validateSearchMovedStructureStability(Level world, BlockPos pos,
																	 Direction forcedDirection,
																	 CallbackInfoReturnable<Boolean> cir) throws AssemblyException {
		if (!Boolean.TRUE.equals(cir.getReturnValue()) || !Config.enableBlockEntityExperimentPara) {
			return;
		}

		int totalValue = createentitycontrol$getTotalStabilizeValue();
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

	@Unique
	private int createentitycontrol$getTotalStabilizeValue() {
		int totalValue = 0;
		int defaultValue = 100;
		for (Map.Entry<String, Integer> entry : blockCountMap_r.entrySet()) {
			String blockType = entry.getKey();
			Integer blockCount = entry.getValue();

			boolean found = false;
			for (List<Object> limitValue : Config.blocksLimitValues) {
				if (limitValue.size() > 1 && limitValue.get(0).equals(blockType)) {
					totalValue += blockCount * ((Integer) limitValue.get(2));
					found = true;
					break;
				}
			}

			if (!found) {
				totalValue += blockCount * defaultValue;
			}
		}
		return totalValue;
	}

	@Redirect(
			method = "moveBlock",
			at = @At(value = "INVOKE",
					target = "Lcom/simibubi/create/content/contraptions/Contraption;addBlock(Lnet/minecraft/core/BlockPos;Lorg/apache/commons/lang3/tuple/Pair;)V")
	)
	protected void createentitycontrol$addBlockWithLimits(Contraption instance, BlockPos pos,
																  Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair)
			throws AssemblyException {
		addBlock(pos, pair);
		if (blocks.size() > AllConfigs.server().kinetics.maxBlocksMoved.get()) {
			return;
		}
		createentitycontrol$validateMovedBlockLimits(pos, pair.getLeft().state);
	}

	@Unique
	private void createentitycontrol$validateMovedBlockLimits(BlockPos pos, BlockState state) throws AssemblyException {
		String blockName = state.getBlock().toString().replaceAll("Block\\{(.*?)\\}", "$1");
		if (Config.blocks_unmoved.stream().anyMatch(blockName::equals)) {
			EntityEnrollment.setControlStatus(8);
			throw AssemblyException.unmovableBlock(pos, state);
		}

		blockCountMap_r.put(blockName, blockCountMap_r.getOrDefault(blockName, 0) + 1);
		for (List<Object> entry : Config.blocksLimitValues) {
			String limitBlockName = (String) entry.get(0);
			int allowedCount = (Integer) entry.get(1);
			int currentCount = blockCountMap_r.getOrDefault(limitBlockName, 0);
			if (currentCount > allowedCount) {
				if (Config.debug_block_entity_problem) {
					createentitycontrol$LOGGER.info("{} count: {} allowed: {}", limitBlockName, currentCount, allowedCount);
				}
				EntityEnrollment.setControlStatus(16);
				throw AssemblyException.unmovableBlock(pos, state);
			}
		}

		createentitycontrol$expandMovedBlockBounds(pos);
		if ((createentitycontrol$maxPos.getX() - createentitycontrol$minPos.getX()) > Config.blockEntityXZMaxLength
				|| (createentitycontrol$maxPos.getY() - createentitycontrol$minPos.getY()) > Config.blockEntityYMaxLength
				|| (createentitycontrol$maxPos.getZ() - createentitycontrol$minPos.getZ()) > Config.blockEntityXZMaxLength) {
			EntityEnrollment.setControlStatus(32);
			throw AssemblyException.unmovableBlock(pos, state);
		}
	}

	@Unique
	private void createentitycontrol$expandMovedBlockBounds(BlockPos pos) {
		createentitycontrol$minPos = new BlockPos(
				Math.min(pos.getX(), createentitycontrol$minPos.getX()),
				Math.min(pos.getY(), createentitycontrol$minPos.getY()),
				Math.min(pos.getZ(), createentitycontrol$minPos.getZ())
		);
		createentitycontrol$maxPos = new BlockPos(
				Math.max(pos.getX(), createentitycontrol$maxPos.getX()),
				Math.max(pos.getY(), createentitycontrol$maxPos.getY()),
				Math.max(pos.getZ(), createentitycontrol$maxPos.getZ())
		);
	}

	/**
	 * @author Pink_Cats
	 * @reason catch_add_block_base
	 */
	@Inject(method = "addBlocksToWorld", at = @At("HEAD"),cancellable = true)
	public void injectAddBlocksToWorld(Level world, StructureTransform transform, CallbackInfo ci) {
        //same structure ignore
        if (Config.keep_structure_at_first) {

            StructureBlockStorage.removeOldEntries(1000L *Config.keep_structure_refresh_time);

            if (StructureFunc.StructureMatch(blocks,transform)){
                if (Config.debug_block_entity_problem) {
                    createentitycontrol$LOGGER.warn("same structure jump control");

                }
            }
            else {
                StructureBlockStorage.storeData(generateRandomUUID(), blocks);
                return;
            }

        }


		// add ignore fix in some entity exp: big cannon added
		int calculate = 0;
		for (StructureTemplate.StructureBlockInfo block : blocks.values()) {
			String blockString = block.state.getBlock().toString();

			if (Config.blocks_ignore.stream().anyMatch(blockString::contains))
			{
				calculate +=1;
			}
			else{
				if (Config.debug_block_entity_problem) {
					createentitycontrol$LOGGER.warn("entity has not ignore block：{}", blockString);

				}
			}
		}
		if (calculate == blocks.size())
		{
			//some entity might have its own logic, use return to make them acquire vanilla.
			return;
		}



		if (disassembled) {
            ci.cancel();
			return;
		}
		disassembled = true;



		translateMultiblockControllers(transform);

		for (boolean nonBrittles : Iterate.trueAndFalse) {



			for (StructureTemplate.StructureBlockInfo block : blocks.values()) {

                BlockPos targetPos = transform.apply(block.pos);
                BlockState state = transform.apply(block.state);
                OpacCompatBridge.captureCreateTargetPos(targetPos);
                BlockState blockState = world.getBlockState(targetPos);
                blockState = OpacCompatBridge.replaceCreateBreakBlockState(blockState, world, this);

                boolean squeezeBlock;
                String blockStateString = blockState.getBlock().toString().replaceAll("Block\\{(.*?)\\}", "$1");
                boolean isInWhitelist = Config.blocks_uncrushable.stream().anyMatch(blockStateString::equals);
                boolean isInDropList = Config.blocks_uncrushableIgnore.stream().anyMatch(blockStateString::equals);

				if (nonBrittles == BlockMovementChecks.isBrittle(block.state)) {
					continue;
				}



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


				if (isInWhitelist) {
					squeezeBlock = true;
				} else if (isInDropList) {
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
				CompoundTag tag = block.nbt;

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
			BlockPos targetPos = transform.apply(block.pos);
			world.markAndNotifyBlock(targetPos, world.getChunkAt(targetPos), block.state, block.state,
					Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL, 512);
		}

        boolean opacGlueHooked = false;
        if (!world.isClientSide && !superglue.isEmpty()) {
            opacGlueHooked = OpacCompatBridge.preCreateDisassembleSuperGlue(world, this);
        }
        try {
            for (AABB box : superglue) {
                box = new AABB(transform.apply(new Vec3(box.minX, box.minY, box.minZ)),
                        transform.apply(new Vec3(box.maxX, box.maxY, box.maxZ)));
                if (!world.isClientSide) {
                    world.addFreshEntity(new SuperGlueEntity(world, box));
                }
            }
        } finally {
            if (opacGlueHooked) {
                OpacCompatBridge.postCreateDisassembleSuperGlue();
            }
        }

        ci.cancel();
	}
}


