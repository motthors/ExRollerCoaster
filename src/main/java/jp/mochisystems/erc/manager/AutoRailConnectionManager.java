package jp.mochisystems.erc.manager;

import jp.mochisystems.erc._mc._core.ERC_Logger;
import jp.mochisystems.erc._mc.network.ERC_MessageConnectRailCtS;
import jp.mochisystems.erc._mc.network.ERC_PacketHandler;
import jp.mochisystems.erc.rail.IRailController;
import jp.mochisystems.erc.rail.Rail;
import net.minecraft.util.math.BlockPos;

public class AutoRailConnectionManager {

	private static int savedPrevX = -1;
	private static int savedPrevY = -1;
	private static int savedPrevZ = -1;
	private static int savedNextX = -1;
	private static int savedNextY = -1;
	private static int savedNextZ = -1;

	public static BlockPos GetPrevBlockPos(){return new BlockPos(savedPrevX, savedPrevY, savedPrevZ);}
	public static BlockPos GetNextBlockPos(){return new BlockPos(savedNextX, savedNextY, savedNextZ);}

	public AutoRailConnectionManager()
	{
		ResetData();
	}

	public static void MemoryOrConnect(int x, int y, int z)
	{
		if(!isSavedPrevRail()) {
			SetPrevRailPosConnectedDestroyBlock(x, y, z);
		}
		else{
			ERC_MessageConnectRailCtS packet
					= new ERC_MessageConnectRailCtS(savedPrevX, savedPrevY, savedPrevZ, x, y, z);
			ERC_PacketHandler.INSTANCE.sendToServer(packet);
			AutoRailConnectionManager.ResetData();
		}
	}

	public static void SetPrevRailPosConnectedDestroyBlock(int x, int y, int z)
	{
		savedPrevX = x;
		savedPrevY = y;
		savedPrevZ = z;
//		ERC_Logger.debugInfo("save prev : x="+x);
	}
	public static void SetPrevRailPosConnectedDestroyBlock(Rail rail)
	{
		if(rail != null)
		{
			IRailController controller = rail.GetController();
			savedPrevX = (int) controller.CorePosX();
			savedPrevY = (int) controller.CorePosY();
			savedPrevZ = (int) controller.CorePosZ();
//			ERC_Logger.debugInfo("save prev by break : x="+savedPrevX);
		}
		else {
			savedPrevX = savedPrevY = savedPrevZ = -1;
//			ERC_Logger.debugInfo("reset prev by break");
		}
	}

	public static void SetNextRailPosConnectedDestroyBlock(int x, int y, int z)
	{
		savedNextX = x;
		savedNextY = y;
		savedNextZ = z;
//		ERC_Logger.debugInfo("save next : x="+x);
	}
	public static void SetNextRailPosConnectedDestroyBlock(Rail rail)
	{
		if(rail != null)
		{
			IRailController controller = rail.GetController();
			savedNextX = (int) controller.CorePosX();
			savedNextY = (int) controller.CorePosY();
			savedNextZ = (int) controller.CorePosZ();
//			ERC_Logger.debugInfo("save next by break : x="+savedPrevX);
		}
		else {
			savedNextX = savedNextY = savedNextZ = -1;
//			ERC_Logger.debugInfo("reset next by break");
		}
	}

	public static void ResetData()
	{
		savedPrevX = -1;
		savedPrevY = -1;
		savedPrevZ = -1;
		savedNextX = -1;
		savedNextY = -1;
		savedNextZ = -1;
	}
	
	public static void ConnectToMemorizedPosition(int x, int y, int z)
	{
		if(AutoRailConnectionManager.isSavedNextRail()) {
			AutoRailConnectionManager.NotifyConnectNextRail(x, y, z);
		}

		if(AutoRailConnectionManager.isSavedPrevRail()){
			AutoRailConnectionManager.NotifyConnectPrevRail(x, y, z);
		}

		SetPrevRailPosConnectedDestroyBlock(x, y, z);
	}

	public static boolean isSavedPrevRail()
	{
		return savedPrevY >= 0;
	}
	public static boolean isSavedNextRail()
	{
		return savedNextY >= 0;
	}

	private static void NotifyConnectPrevRail(int x, int y, int z)
	{
		ERC_MessageConnectRailCtS packet
			= new ERC_MessageConnectRailCtS(
					savedPrevX, savedPrevY, savedPrevZ,
					x, y, z
					);
		ERC_PacketHandler.INSTANCE.sendToServer(packet);

		savedPrevX = -1;
		savedPrevY = -1;
		savedPrevZ = -1;
	}

	private static void NotifyConnectNextRail(int x, int y, int z)
	{
		ERC_MessageConnectRailCtS packet 
			= new ERC_MessageConnectRailCtS(
				x, y, z,
				savedNextX, savedNextY, savedNextZ
				);
		ERC_PacketHandler.INSTANCE.sendToServer(packet);

		savedNextX = -1;
		savedNextY = -1;
		savedNextZ = -1;
	}
	
//	public static TileEntityRail GetPrevTileEntity(World world)
//	{
//		return ((TileEntityRail)world.getTileEntity(savedPrevX, savedPrevY, savedPrevZ));
//	}
//	public static TileEntityRail GetNextTileEntity(World world)
//	{
//		return ((TileEntityRail)world.getTileEntity(savedNextX, savedNextY, savedNextZ));
//	}



//    static Vec3d dir;
//    static double speed;
//    static EntityPlayer player;
//    public static void GetOffAndButtobi(EntityPlayer Player)
//    {
//    	if(/*!Player.worldObj.isRemote &&*/ Player.isSneaking())
//    	{
//    		if(Player.getRidingEntity() instanceof EntityCoasterSeat)
//    		{
//    			Seat seat = ((EntityCoasterSeat)Player.getRidingEntity()).seat;
////    			dir = seat.GetParent().AttitudeMatrix.Dir();
//    			player = Player;
//    			speed = seat.GetParent().getSpeed();
//    			//Player.motionX += seat.parent.Speed * dir.xCoord * 1;
//    			//Player.motionY += seat.parent.Speed * dir.yCoord * 1;
//    			//Player.motionZ += seat.parent.Speed * dir.zCoord * 1;
//    			ERC_Logger.debugInfo("NotifyRailConnectionMgr : " + dir.toString());
//    		}
//    	}
//    }
//    public static void motionactive()
//    {
//    	player.motionX += speed * dir.x * 1;
//    	player.motionY += speed * dir.y * 1;
//		player.motionZ += speed * dir.z * 1;
//    }
}

