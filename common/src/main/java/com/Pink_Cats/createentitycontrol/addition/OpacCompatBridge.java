package com.Pink_Cats.createentitycontrol.addition;

import com.mojang.logging.LogUtils;
import com.Pink_Cats.createentitycontrol.platform.PlatformModList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class OpacCompatBridge {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String OPAC_MOD_ID = "openpartiesandclaims";
    private static final String SERVER_CORE_CLASS = "xaero.pac.common.server.core.ServerCore";
    private static final String ICREATE_CONTRAPTION_CLASS = "xaero.pac.common.server.core.accessor.ICreateContraption";

    private static boolean initialized = false;
    private static boolean available = false;

    private static Field capturedTargetPosField;
    private static Method replaceBlockFetchMethod;
    private static Method preSuperGlueMethod;
    private static Method postSuperGlueMethod;

    private OpacCompatBridge() {
    }

    public static boolean isAvailable() {
        ensureInit();
        return available;
    }

    public static void captureCreateTargetPos(BlockPos pos) {
        ensureInit();
        if (!available || pos == null) {
            return;
        }
        try {
            capturedTargetPosField.set(null, pos);
        } catch (Throwable throwable) {
            disableWithLog("captureCreateTargetPos", throwable);
        }
    }

    public static BlockState replaceCreateBreakBlockState(BlockState actual, Level level, Object contraption) {
        ensureInit();
        if (!available || actual == null || level == null || contraption == null) {
            return actual;
        }
        try {
            return (BlockState) replaceBlockFetchMethod.invoke(null, actual, level, contraption);
        } catch (Throwable throwable) {
            disableWithLog("replaceCreateBreakBlockState", throwable);
            return actual;
        }
    }

    public static boolean preCreateDisassembleSuperGlue(Level level, Object contraption) {
        ensureInit();
        if (!available || level == null || contraption == null) {
            return false;
        }
        try {
            preSuperGlueMethod.invoke(null, level, contraption);
            return true;
        } catch (Throwable throwable) {
            disableWithLog("preCreateDisassembleSuperGlue", throwable);
            return false;
        }
    }

    public static void postCreateDisassembleSuperGlue() {
        ensureInit();
        if (!available) {
            return;
        }
        try {
            postSuperGlueMethod.invoke(null);
        } catch (Throwable throwable) {
            disableWithLog("postCreateDisassembleSuperGlue", throwable);
        }
    }

    private static void ensureInit() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (!PlatformModList.isLoaded(OPAC_MOD_ID)) {
            return;
        }
        try {
            Class<?> serverCore = Class.forName(SERVER_CORE_CLASS);
            Class<?> iCreateContraption = Class.forName(ICREATE_CONTRAPTION_CLASS);

            capturedTargetPosField = serverCore.getField("CAPTURED_TARGET_POS");
            replaceBlockFetchMethod = serverCore.getMethod("replaceBlockFetchOnCreateModBreak", BlockState.class, Level.class, iCreateContraption);
            preSuperGlueMethod = serverCore.getMethod("preCreateDisassembleSuperGlue", Level.class, iCreateContraption);
            postSuperGlueMethod = serverCore.getMethod("postCreateDisassembleSuperGlue");
            available = true;
        } catch (Throwable throwable) {
            available = false;
            LOGGER.warn("OPAC compatibility bridge init failed. Falling back without OPAC bridge hooks.", throwable);
        }
    }

    private static void disableWithLog(String phase, Throwable throwable) {
        available = false;
        LOGGER.warn("OPAC compatibility bridge disabled during {}", phase, throwable);
    }
}

