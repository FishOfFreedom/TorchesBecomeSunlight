package com.freefish.torchesbecomesunlight.mixin;

import com.lowdragmc.shimmer.ShimmerConstants;

public interface MixinPluginShared {

	static boolean isClassFound(String className) {
		try {
			Class.forName(className, false, Thread.currentThread().getContextClassLoader());
			ShimmerConstants.LOGGER.debug("find class {}", className);
			return true;
		} catch (ClassNotFoundException e) {
			ShimmerConstants.LOGGER.debug("can't find class {}", className);
			return false;
		}
	}

	boolean IS_IRIS_LOAD = isClassFound("net.coderbot.iris.compat.sodium.mixin.IrisSodiumCompatMixinPlugin");
	boolean IS_OCULUS_LOAD = IS_IRIS_LOAD;
}