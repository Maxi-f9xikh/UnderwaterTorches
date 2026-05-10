package de.maxi.underwatertorches;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Underwatertorches implements ModInitializer {

	public static final String MOD_ID = "underwatertorches";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("UnderwaterTorches loaded - all torches are now waterloggable!");
	}
}
