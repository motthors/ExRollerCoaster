package jp.mochisystems.erc._mc.proxy;

import jp.mochisystems.core._mc.renderer.renderTileEntityLimitFrame;
import jp.mochisystems.core.manager.RollingSeatManager;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc.block.rail.BlockRail;
import jp.mochisystems.erc._mc.entity.EntityCoaster;
import jp.mochisystems.erc._mc.entity.EntitySUSHI;
import jp.mochisystems.erc._mc.gui.GuiInGameEngineControl;
import jp.mochisystems.erc._mc.renderer.RenderEntityCoaster;
import jp.mochisystems.erc._mc.renderer.RenderEntitySUSHI;
import jp.mochisystems.erc._mc.renderer.RenderTileEntityRailBase;
import jp.mochisystems.erc._mc.renderer.RendererCoasterModelConstructor;
import jp.mochisystems.erc._mc.tileentity.TileEntityCoasterModelConstructor;
import jp.mochisystems.erc._mc.tileentity.TileEntityRail;
import jp.mochisystems.erc._mc.tileentity.TileEntityRailModelConstructor;
import jp.mochisystems.erc.loader.ModelPackLoader;
import jp.mochisystems.erc.renderer.rail.IRailRenderer;
import jp.mochisystems.erc.sound.CoasterMovingSoundRiding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy implements IProxy{

//	public CoasterCameraController coasterCameraController;

	@Override
	public void preInit()
	{
		// Entity�`��o�^
//		RenderEntityCoaster renderer = new RenderEntityCoaster();
//		RenderingRegistry.registerEntityRenderingHandler(EntityCoaster.class, renderer);
//		RenderingRegistry.registerEntityRenderingHandler(EntityCoasterSeat.class, new renderEntityCoasterSeat());
//		RenderingRegistry.registerEntityRenderingHandler(EntitySUSHI.class, new renderEntitySUSIHI());
//		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterMonodentate.class, renderer);
//		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterDoubleSeat.class, renderer);
//		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterConnector.class, renderer);

		// Handler�̓o�^
//		ERC.coasterCameraController = new CoasterCameraController(mc);
//		coasterCameraController = new CoasterCameraController(mc);

//		FMLCommonHandler.instance().bus().register(new TickEventHandler());
//		FMLCommonHandler.instance().bus().register(ERC_Core.coasterCameraController);
//		MinecraftForge.EVENT_BUS.register(new ERC_InputEventHandler(mc));
//		MinecraftForge.EVENT_BUS.register(new ERC_RenderEventHandler());
		MinecraftForge.EVENT_BUS.register(ERC.itemWrench);
		MinecraftForge.EVENT_BUS.register(ERC.itemRailChecker);
		MinecraftForge.EVENT_BUS.register(new GuiInGameEngineControl());

		// �X�V�̃��f���Ƃ�
//		EntitySUSHI.clientInitSUSHI();
	}

	@Override
	public void init() 
	{
		RollingSeatManager.Init();
	}

	@Override
	public void postInit() 
	{
//		ModelPackLoader.Load();
	}

	@Override
	public void OnRideCoaster(Entity player, EntityCoaster coaster) {
		if (player == Minecraft.getMinecraft().player) {
			Minecraft.getMinecraft().getSoundHandler().playSound(new CoasterMovingSoundRiding((EntityPlayer) player, coaster));
		}
	}

	@Override
	public IRailRenderer GetRailRenderer()
	{
		return null;
	}




	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {

		ModelLoader.setCustomModelResourceLocation(ERC.itemBasePipe, 0, new ModelResourceLocation(ERC.itemBasePipe.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.itemWrench, 0, new ModelResourceLocation(ERC.itemWrench.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.itemRailChecker, 0, new ModelResourceLocation(ERC.itemRailChecker.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.ItemSwitchRailModel, 0, new ModelResourceLocation(ERC.ItemSwitchRailModel.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.ItemRailBlockModelChanger, 0, new ModelResourceLocation(ERC.ItemRailBlockModelChanger.getRegistryName(), "inventory"));


		IStateMapper mapper = new StateMap.Builder().ignore(BlockRail.FACING).build();
		ModelLoader.setCustomStateMapper(ERC.railNormal, mapper);
		ModelLoader.setCustomStateMapper(ERC.railAccel, mapper);
		ModelLoader.setCustomStateMapper(ERC.railConst, mapper);
		ModelLoader.setCustomStateMapper(ERC.railDetect, mapper);
		ModelLoader.setCustomStateMapper(ERC.railBranch, mapper);
		ModelLoader.setCustomStateMapper(ERC.railInvisible, mapper);
		ModelLoader.setCustomStateMapper(ERC.railNonGravity, mapper);
		ModelLoader.setCustomStateMapper(ERC.railModelConstructor, new StateMap.Builder().build());
		ModelLoader.setCustomStateMapper(ERC.coasterModelConstructor, new StateMap.Builder().build());

		ModelLoader.setCustomModelResourceLocation(ERC.itemRailNormal, 0, new ModelResourceLocation(ERC.itemRailNormal.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.itemRailAccel, 0, new ModelResourceLocation(ERC.itemRailAccel.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.itemRailConst, 0, new ModelResourceLocation(ERC.itemRailConst.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.itemRailDetect, 0, new ModelResourceLocation(ERC.itemRailDetect.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.itemRailBranch, 0, new ModelResourceLocation(ERC.itemRailBranch.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.itemRailInvisible, 0, new ModelResourceLocation(ERC.itemRailInvisible.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.itemRailNonGravity, 0, new ModelResourceLocation(ERC.itemRailNonGravity.getRegistryName(), "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ERC.itemSmoothAll, 0, new ModelResourceLocation(itemSmoothAll.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.itemCoaster, 0, new ModelResourceLocation(ERC.itemCoaster.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.itemCoasterModel, 0, new ModelResourceLocation(ERC.itemCoasterModel.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ERC.itemSUSHI, 0, new ModelResourceLocation(ERC.itemSUSHI.getRegistryName(), "inventory"));

		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ERC.railModelConstructor), 0, new ModelResourceLocation(ERC.railModelConstructor.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ERC.coasterModelConstructor), 0, new ModelResourceLocation(ERC.coasterModelConstructor.getRegistryName(), "inventory"));

		OBJLoader.INSTANCE.addDomain(ERC.MODID);
		OBJLoader.INSTANCE.addDomain("ercmodels");
		ModelPackLoader.Instance.Load();

		RenderTileEntityRailBase tileRenderer = new RenderTileEntityRailBase();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.class, tileRenderer);
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.Accel.class, tileRenderer);
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.Const.class, tileRenderer);
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.Detector.class, tileRenderer);
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.Branch.class,tileRenderer);
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.Invisible.class, tileRenderer);
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.AntiGravity.class, tileRenderer);

		renderTileEntityLimitFrame rendererFrame = new renderTileEntityLimitFrame();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailModelConstructor.class, rendererFrame);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoasterModelConstructor.class, new RendererCoasterModelConstructor());

		//EntityRegistry.registerModEntity(new ResourceLocation(MODID+":entityCoaster"), EntityCoaster.class, "entitycoaster", 64, this, 256, 20, true);
		RenderingRegistry.registerEntityRenderingHandler(EntityCoaster.class, RenderEntityCoaster::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySUSHI.class, RenderEntitySUSHI::new);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre event) {
		ModelPackLoader.Instance.RegisterTextures(event.getMap());
	}

	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event)
	{
		ModelPackLoader.Instance.Bake();
		ModelPackLoader.Instance.RegisterTexItemModel(event);
		EntitySUSHI.clientInitSUSHI();
	}


}