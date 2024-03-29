package jp.mochisystems.erc._mc.proxy;

import jp.mochisystems.erc._mc.entity.EntityCoaster;
import jp.mochisystems.erc.renderer.rail.IRailRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IProxy{
	void preInit();
	void init();
	void postInit();
	void OnRideCoaster(Entity player, EntityCoaster coaster);
	IRailRenderer GetRailRenderer();
}