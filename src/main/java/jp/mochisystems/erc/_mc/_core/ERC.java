package jp.mochisystems.erc._mc._core;

import jp.mochisystems.core._mc._core._Core;
import jp.mochisystems.erc._mc.block.rail.*;
import jp.mochisystems.erc._mc.block.*;
import jp.mochisystems.erc._mc.entity.EntitySUSHI;
import jp.mochisystems.erc._mc.gui.GuiInGameEngineControl;
import jp.mochisystems.erc._mc.item.*;
import jp.mochisystems.erc._mc.tileentity.TileEntityCoasterModelConstructor;
import jp.mochisystems.erc._mc.tileentity.TileEntityRailModelConstructor;
import jp.mochisystems.erc._mc.block.rail.BlockInvisibleRail;
import jp.mochisystems.erc._mc.gui.ERC_GUIHandler;
import jp.mochisystems.erc._mc.item.ItemCoaster;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc._mc.entity.EntityCoaster;
import jp.mochisystems.erc._mc.network.ERC_PacketHandler;
import jp.mochisystems.erc._mc.proxy.IProxy;
import jp.mochisystems.erc.loader.ModelPackLoader;
import jp.mochisystems.erc.manager.CoasterIdManager;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.IOException;
import java.io.InputStream;

@Mod(
	modid = ERC.MODID,
	name = "Ex Roller Coaster",
	version = ERC.VERSION,
	useMetadata = true,
	dependencies = "after:"+_Core.MODID
)
@IFMLLoadingPlugin.TransformerExclusions
public class ERC {
	public static final String MODID = "exrollercoaster";
	public static final String VERSION = "2.0beta4";


	//proxy////////////////////////////////////////
	@SidedProxy(
			clientSide = "jp.mochisystems.erc._mc.proxy.ClientProxy",
			serverSide = "jp.mochisystems.erc._mc.proxy.ServerProxy")
	public static IProxy proxy;

	//Blocks/////////////////////////////////////////
	public static final Block railNormal = new BlockRail();
	public static final Block railAccel = new BlockRailRedPowerHandlerBase.BlockAccelRail();
	public static final Block railConst = new BlockRailRedPowerHandlerBase.BlockConstVelocityRail();
	public static final Block railBranch = new BlockRailRedPowerHandlerBase.BlockBranchRail();
	public static final Block railDetect = new BlockDetectorRail();
	public static final Block railInvisible = new BlockInvisibleRail();
	public static final Block railNonGravity = new BlockNonGravityRail();

	public static final ItemBlockRail itemRailNormal = new ItemBlockRail(railNormal);
	public static final ItemBlockRail itemRailAccel = new ItemBlockRail(railAccel);
	public static final ItemBlockRail itemRailConst = new ItemBlockRail(railConst);
	public static final ItemBlockRail itemRailBranch = new ItemBlockRail(railBranch);
	public static final ItemBlockRail itemRailDetect = new ItemBlockRail(railDetect);
	public static final ItemBlockRail itemRailInvisible = new ItemBlockRail(railInvisible);
	public static final ItemBlockRail itemRailNonGravity = new ItemBlockRail(railNonGravity);

	public static Block railModelConstructor = new BlockRailModelConstructor();
	public static Block coasterModelConstructor = new BlockCoasterModelConstructor();


	// items /////////////////////////////////////////
	public static Item itemBasePipe = new Item();
	public static Item itemWrench = new ItemWrench();
	public static Item itemCoaster = new ItemCoaster();
	public static Item ItemSwitchRailModel = new ItemSwitchingRailModel();
	public static Item ItemRailBlockModelChanger = new ItemRailModelChanger();
	public static Item itemSUSHI = new itemSUSHI();
//	public static Item itemSmoothAll = new ERC_ItemSmoothAll();
	public static Item itemRailChecker = new ItemRailInfoChecker();
	public static Item itemCoasterModel = new ItemCoasterModel();

//	public static ItemRenderingHookerModel WrenchBakedModel;


	//GUI/////////////////////////////////////////
	@Mod.Instance(ERC.MODID)
    public static ERC INSTANCE;
    public static final int GUIID_RailBase = 0;
	public static final int GUIID_RailModelConstructor = 1;
	public static final int GUIID_CoasterModelConstructor = 2;
	public static final int GUIID_ItemCoasterTypeSelector = 10;
//    public static final int GUIID_FerrisBasketConstructor = 2;
//    public static final int GUIID_FerrisCore = 3;

	////////////////////////////////////////////////////////////////
	//Creative Tab
	public static ERC_CreateCreativeTab ERC_Tab = new ERC_CreateCreativeTab(MODID);


	////////////////////////////////////////////////////////////////

	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		_Core.Instance.AddTab(ERC_Tab);
		MinecraftForge.EVENT_BUS.register(this);
		ERC_PacketHandler.init();
		proxy.preInit();
		MinecraftForge.EVENT_BUS.register(proxy);
	}

	@EventHandler
	public void Init(FMLInitializationEvent e)
	{
		proxy.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ERC_GUIHandler());

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		proxy.postInit();
		FMLCommonHandler.instance().bus().register(CoasterIdManager.Instance);
	}
	////////////////////////////////////////////////////////////////

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event)
	{
		event.getRegistry().registerAll(
			EntityEntryBuilder.create().entity(EntityCoaster.class).id(MODID+":coaster", 1).name(MODID+":coaster").tracker(64, 20, false).build(),
			EntityEntryBuilder.create().entity(EntitySUSHI.class).id(MODID+":SUSHI", 2).name("SUSHI").tracker(16, 10, false).build()
//			new EntityEntry(EntityCoasterSeat.class, MODID+":coasterSeat").setRegistryName(MODID, "coasterSeat"),
		);
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		_Core.RegisterBlock(r, railNormal, "Normal Rail", MODID, "blockrailnormal", ERC_Tab);
		_Core.RegisterBlock(r, railAccel, "Accelacation Rail", MODID, "blockrailaccel", ERC_Tab);
		_Core.RegisterBlock(r, railConst, "Const Speed Rail", MODID, "blockrailconst", ERC_Tab);
		_Core.RegisterBlock(r, railDetect, "Detect Rail", MODID, "blockraildetect", ERC_Tab);
		_Core.RegisterBlock(r, railBranch, "Branch Rail", MODID, "blockrailbranch", ERC_Tab);
		_Core.RegisterBlock(r, railInvisible, "Invisible Rail", MODID, "blockrailinvisible", ERC_Tab);
		_Core.RegisterBlock(r, railNonGravity, "NoGravity Rail",  MODID,"blockrailgravity", ERC_Tab);
		_Core.RegisterBlock(r, railModelConstructor, "RailModelConstructor", MODID, "block_constructor_rail_model", ERC_Tab);
		_Core.RegisterBlock(r, coasterModelConstructor, "CoasterModelConstructor", MODID, "block_constructor_coaster_model", ERC_Tab);

		_Core.RegisterTileEntity(TileEntityRailModelConstructor.class, MODID, "RailModelConstructor");
		_Core.RegisterTileEntity(TileEntityCoasterModelConstructor.class, MODID, "CoasterModelConstructor");
		_Core.RegisterTileEntity(TileEntityRail.Normal.class, MODID, "TileEntityRail");
		_Core.RegisterTileEntity(TileEntityRail.Accel.class, MODID, "TileEntityRailRedAcc");
		_Core.RegisterTileEntity(TileEntityRail.Const.class, MODID, "TileEntityRailconstvel");
		_Core.RegisterTileEntity(TileEntityRail.Detector.class, MODID, "TileEntityRailDetector");
		_Core.RegisterTileEntity(TileEntityRail.Branch.class, MODID, "TileEntityRailBranch");
		_Core.RegisterTileEntity(TileEntityRail.Invisible.class, MODID, "TileEntityInvisible");
		_Core.RegisterTileEntity(TileEntityRail.AntiGravity.class, MODID, "TileEntityNonGravity");

	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
				itemRailNormal.setRegistryName(MODID, "itemblockrailnormal"),
				itemRailAccel.setRegistryName(MODID, "itemBlockRailAccel"),
				itemRailConst.setRegistryName(MODID, "itemBlockRailConst"),
				itemRailBranch.setRegistryName(MODID, "itemBlockRailBranch"),
				itemRailDetect.setRegistryName(MODID, "itemBlockRailDetect"),
				itemRailInvisible.setRegistryName(MODID, "itemBlockRailInvisible"),
				itemRailNonGravity.setRegistryName(MODID, "itemBlockRailGravity"),
				new ItemBlock(railModelConstructor).setRegistryName(MODID, "item_constructor_rail_model"),
				new ItemBlock(coasterModelConstructor).setRegistryName(MODID, "item_constructor_coaster_model")
		);

		event.getRegistry().registerAll(
			itemBasePipe.setRegistryName("railpipe").setUnlocalizedName("RailPipe"),
			itemWrench.setCreativeTab(ERC_Tab).setRegistryName("Wrench").setUnlocalizedName("Wrench").setMaxStackSize(1),
			itemRailChecker.setCreativeTab(ERC_Tab).setRegistryName("RailChecker").setUnlocalizedName("RailChecker").setMaxStackSize(1),
			itemCoaster.setCreativeTab(ERC_Tab).setRegistryName("Coaster").setUnlocalizedName("Coaster"),
			ItemSwitchRailModel.setCreativeTab(ERC_Tab).setRegistryName("SwitchRailModel").setUnlocalizedName("SwitchRailModel").setMaxStackSize(1),
			ItemRailBlockModelChanger
				.setRegistryName("SwitchBlockRailModel")
				.setUnlocalizedName("SwitchBlockRailModel")
				.setMaxStackSize(1),
			itemSUSHI.setCreativeTab(ERC_Tab)
				.setRegistryName("ItemSUSHI")
				.setUnlocalizedName("SUSHI"),
//			itemSmoothAll.setCreativeTab(ERC_Tab)
//				.setRegistryName("ItemSmoothAll")
//				.setUnlocalizedName("ERCSmoothAll"),
			itemCoasterModel
				.setRegistryName("ItemCoasterModel")
//				.setCreativeTab(ERC_Tab)
				.setUnlocalizedName("ERCItemCoasterModel")
		);
	}

	public static InputStream GetInModPackageFileStream(ResourceLocation resource) throws IOException
	{
        IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
        return res.getInputStream();
	}
}