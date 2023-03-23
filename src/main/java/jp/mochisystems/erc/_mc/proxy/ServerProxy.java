package jp.mochisystems.erc._mc.proxy;

import jp.mochisystems.erc._mc.entity.EntityCoaster;
import jp.mochisystems.erc.renderer.rail.IRailRenderer;
import net.minecraft.entity.Entity;

public class ServerProxy implements IProxy{
	
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

	@Override
	public IRailRenderer GetRailRenderer()
	{
		return null;
	}
}
