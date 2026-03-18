package com.Pink_Cats.createentitycontroller.addition;

import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class ExtendAssemblyException extends Exception {

    private static final long serialVersionUID = 1L;
    public final Component component;
    private BlockPos position = null;

    public static void write(CompoundTag compound, ExtendAssemblyException exception) {
        if (exception == null)
            return;

        CompoundTag nbt = new CompoundTag();
        nbt.putString("Component", Component.Serializer.toJson(exception.component));
        if (exception.hasPosition())
            nbt.putLong("Position", exception.getPosition()
                    .asLong());

        compound.put("LastException", nbt);
    }

    public static ExtendAssemblyException read(CompoundTag compound) {
        if (!compound.contains("LastException"))
            return null;

        CompoundTag nbt = compound.getCompound("LastException");
        String string = nbt.getString("Component");
        ExtendAssemblyException exception = new ExtendAssemblyException(Component.Serializer.fromJson(string));
        if (nbt.contains("Position"))
            exception.position = BlockPos.of(nbt.getLong("Position"));

        return exception;
    }

    public ExtendAssemblyException(Component component) {
        this.component = component;
    }

    public ExtendAssemblyException(String langKey, Object... objects) {
        this(CreateLang.translateDirect("gui.assembly.exception." + langKey, objects));
    }

    public static ExtendAssemblyException unmovableBlock(BlockPos pos, BlockState state) {
        ExtendAssemblyException e = new ExtendAssemblyException("unmovableBlock", pos.getX(), pos.getY(), pos.getZ(),
                state.getBlock().getName());
        e.position = pos;
        return e;
    }

    public static ExtendAssemblyException unloadedChunk(BlockPos pos) {
        ExtendAssemblyException e = new ExtendAssemblyException("chunkNotLoaded", pos.getX(), pos.getY(), pos.getZ());
        e.position = pos;
        return e;
    }

    public static ExtendAssemblyException structureTooLarge() {
        System.out.println("structureTooLarge");
        return new ExtendAssemblyException("structureTooLarge", AllConfigs.server().kinetics.maxBlocksMoved.get());
    }

    public static ExtendAssemblyException tooManyPistonPoles() {
        return new ExtendAssemblyException("tooManyPistonPoles", AllConfigs.server().kinetics.maxPistonPoles.get());
    }

    public static ExtendAssemblyException noPistonPoles() {

        return new ExtendAssemblyException("noPistonPoles");
    }

    public static ExtendAssemblyException SpecialBlockOverload(String blockName, int i) {

        return new ExtendAssemblyException("Block-count-exceeded-for",blockName,i);

    }
    public static ExtendAssemblyException notallowblock(BlockPos pos) {
        ExtendAssemblyException e = new ExtendAssemblyException("not-allow-block", pos.getX(), pos.getY(), pos.getZ());
        e.position = pos;
        return e;
    }

    public static ExtendAssemblyException unstablestructure(int actual, int limit) {

        return new ExtendAssemblyException("unstable-structure",actual,limit);
    }

    public static ExtendAssemblyException notEnoughSails(int sails) {
        return new ExtendAssemblyException("not_enough_sails", sails, AllConfigs.server().kinetics.minimumWindmillSails.get());
    }

    public boolean hasPosition() {
        return position != null;
    }

    public BlockPos getPosition() {
        return position;
    }
}
