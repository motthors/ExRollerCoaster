package jp.mochisystems.erc._mc.proxy;

import jp.mochisystems.erc._mc.entity.EntityCoaster;
import net.minecraft.entity.Entity;

public class ERC_ServerProxy implements IProxy{
	
	@Override
	public void preInit()
	{
//		ERC.coasterCameraController = new CoasterCameraController(Minecraft.getMinecraft());
//		FMLCommonHandler.instance().bus().register(ERC_Core.coasterCameraController);
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}

	@Override
	public void OnRideCoaster(Entity player, EntityCoaster coaster) {}
}
