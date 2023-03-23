package jp.mochisystems.erc.loader;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.erc._mc._core.ERC;
import jp.mochisystems.erc._mc._core.ERC_Logger;
import jp.mochisystems.erc._mc.renderer.ItemCoasterBakedModel;
import jp.mochisystems.erc._mc.renderer.ItemRailModelSwitcherBakedModel;
import jp.mochisystems.erc.coaster.CoasterSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector2f;

public class ModelPackLoader implements ISelectiveResourceReloadListener {
	public static ModelPackLoader Instance = new ModelPackLoader();

	public static final String defaultCoasterID = "MochiSystems.Default";

	public static class PackData {
		public String packID;
	}
	public static class CoasterPackData{
		public ResourcePackRepository.Entry entry;
		public String IconName;
		public String ModelNameMain;
		public String ModelNameConnect;
		public String TextureName;
		public CoasterSettings MainSetting;
		public CoasterSettings ConnectSetting;
	}

	public static class RailPackData{
		public static class RailInfo {
			public ResourcePackRepository.Entry entry;
			public String id;
			public String icon;
			public String mesh;
			public String texture;
		}
		RailInfo[] models;
	}

	private final Map<String, CoasterPackData> CoasterPackDataMap = new HashMap<>();
	private final Map<String, RailPackData.RailInfo> RailPackMap = new HashMap<>();

	private final Map<String, IBakedModel> CoasterModelMap = new HashMap<>();
	private final Map<String, ResourceLocation> CoasterTextureMap = new HashMap<>();
	private final Map<String, IBakedModel> RailModelMap = new HashMap<>();
	private final Map<String, ResourceLocation> RailTextureMap = new HashMap<>();

	public void Load() {
		try {
			loadDefaultCoasterModelPack();
			loadExternalResourcePackModel();
		}
		catch (Exception e) {
			ERC_Logger.error("cant load model");
			ERC_Logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
	{
//		Logger.debugInfo("CHECK");
	}

	public void RegisterTextures(TextureMap textureMap)
	{
		for(CoasterPackData pack : CoasterPackDataMap.values())
		{
			if(!IsActivePack(pack.entry)) continue;
			textureMap.registerSprite(new ResourceLocation(pack.IconName));
		}
		for(RailPackData.RailInfo pack : RailPackMap.values())
		{
			if(!IsActivePack(pack.entry)) continue;
			textureMap.registerSprite(new ResourceLocation(pack.icon));
		}
	}

	public void Bake()
	{
		for(CoasterPackData pack : CoasterPackDataMap.values())
		{
			if(!IsActivePack(pack.entry)) continue;
			try{
				ERC_Logger.info("load model : "+pack.ModelNameMain);
				CoasterModelMap.put(
						pack.MainSetting.ModelID,
						loadModel(pack.ModelNameMain));
				if(pack.ModelNameMain.equals(pack.ModelNameConnect)){
					ERC_Logger.info("ConnectModel is same as MainModel. skip.");
				}
				else {
					ERC_Logger.info("load model : " + pack.ModelNameConnect);
					CoasterModelMap.put(
							pack.ConnectSetting.ModelID,
							loadModel(pack.ModelNameConnect));
				}
				ERC_Logger.info("load texture : "+pack.TextureName);
				ResourceLocation resource = new ResourceLocation(pack.TextureName);
				CoasterTextureMap.put(pack.MainSetting.ModelID, resource);
				CoasterTextureMap.put(pack.ConnectSetting.ModelID, resource);
			}
			catch (Exception e){
				ERC_Logger.error("モデルのロードができませんでした。");
			}
		}
		for(RailPackData.RailInfo info : RailPackMap.values())
		{
			if(!IsActivePack(info.entry)) continue;
			BakeRail(info.id, info.mesh, info.texture);
		}
	}
	private void BakeRail(String id, String meshName, String texName)
	{
		try{
			if (!RailModelMap.containsKey(id)) {
				ERC_Logger.info("load model : " + id);
				RailModelMap.put(id, loadModel(meshName));
			}
			ERC_Logger.info("load texture : " + texName);
			RailTextureMap.put(id, new ResourceLocation(texName));
		}
		catch (Exception e){
			ERC_Logger.error("モデルのロードができませんでした。");
			ERC_Logger.error(e.toString());
		}
	}


	public void RegisterTexItemModel(ModelBakeEvent event)
	{
		ArrayList<ItemOverride> overrides = new ArrayList<>();

		for(ModelPackLoader.CoasterPackData pack : CoasterPackDataMap.values())
		{
			if(!IsActivePack(pack.entry)) continue;
			BakeExternalItemModel(pack.IconName, overrides, event);
		}
		ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(ERC.itemCoaster.getRegistryName(), "inventory");
		IBakedModel existingModel = event.getModelRegistry().getObject(itemModelResourceLocation);
		if (existingModel == null) {
			ERC_Logger.warn("Did not find the expected vanilla baked model for ChessboardModel in registry");
		} else if (existingModel instanceof ItemCoasterBakedModel) {
			ERC_Logger.warn("Tried to replace ChessboardModel twice");
		} else {
			ItemCoasterBakedModel customModel = new ItemCoasterBakedModel(existingModel, overrides);
			event.getModelRegistry().putObject(itemModelResourceLocation, customModel);
		}


		overrides = new ArrayList<>();
		for(RailPackData.RailInfo pack : RailPackMap.values())
		{
			if(!IsActivePack(pack.entry)) continue;
			BakeExternalItemModel(pack.icon, overrides, event);
		}
		itemModelResourceLocation = new ModelResourceLocation(ERC.ItemSwitchRailModel.getRegistryName(), "inventory");
		existingModel = event.getModelRegistry().getObject(itemModelResourceLocation);
		if (existingModel == null) {
			ERC_Logger.warn("Did not find the expected vanilla baked model for ChessboardModel in registry");
		} else if (existingModel instanceof ItemRailModelSwitcherBakedModel) {
			ERC_Logger.info("Tried to replace ChessboardModel twice");
		} else {
			ItemRailModelSwitcherBakedModel customModel = new ItemRailModelSwitcherBakedModel(existingModel, overrides);
			event.getModelRegistry().putObject(itemModelResourceLocation, customModel);
		}
	}

	private void BakeExternalItemModel(String iconPath, ArrayList<ItemOverride> overrides, ModelBakeEvent event)
	{
		ModelBlock model = new ModelBlock(
				new ResourceLocation("minecraft:item/generated"),
				new ArrayList<BlockPart>(),
				java.util.Collections.singletonMap("layer0", iconPath),
				true, true, ItemCameraTransforms.DEFAULT,
				java.util.Collections.emptyList()
		);

		TRSRTransformation state = TRSRTransformation.identity();
		ItemCameraTransforms transforms = model.getAllTransforms();
		Map<ItemCameraTransforms.TransformType, TRSRTransformation> tMap = Maps.newEnumMap(ItemCameraTransforms.TransformType.class);
		tMap.putAll(PerspectiveMapWrapper.getTransforms(transforms));
		tMap.putAll(PerspectiveMapWrapper.getTransforms(state));
		IModelState perState = new SimpleModelState(ImmutableMap.copyOf(tMap), state.apply(Optional.empty()));

		IBakedModel b = new ItemLayerModel(model).bake(perState, DefaultVertexFormats.ITEM, location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		ModelResourceLocation rl = new ModelResourceLocation(iconPath);
		event.getModelRegistry().putObject(rl, b);

		overrides.add(new ItemOverride(rl,
				java.util.Collections.singletonMap(new ResourceLocation("minecraft:index"), (float)overrides.size()+1f)));

	}







	public CoasterSettings GetHeadCoasterSettings(String id) {
		if(!CoasterPackDataMap.containsKey(id)) id = defaultCoasterID;
		return CoasterSettings.Default().CopyFrom(CoasterPackDataMap.get(id).MainSetting);
	}

	public CoasterSettings GetConnectCoasterSettings(String id) {
		if(!CoasterPackDataMap.containsKey(id)) id = defaultCoasterID;
		return CoasterSettings.Default().CopyFrom(CoasterPackDataMap.get(id).ConnectSetting);
	}

	public String GetCoasterIconName(String id) {
		if(!CoasterPackDataMap.containsKey(id)) id = defaultCoasterID;
		return CoasterPackDataMap.get(id).IconName;
	}
	public String GetRailIconName(String id) {
		if(!RailPackMap.containsKey(id)) return "";
		return RailPackMap.get(id).icon;
	}

	public Set<String> GetCoasterPackIds(){
		return CoasterPackDataMap.keySet();
	}
	public Set<String> GetRailPackIds(){
		return RailPackMap.keySet();
	}

	public boolean IsActivePack_forCoaster(String id) {
		CoasterPackData data = CoasterPackDataMap.get(id);
		if(data == null) return false;
		return IsActivePack(data.entry);
	}
	public boolean IsActivePack_forRail(String id) {
		RailPackData.RailInfo data = RailPackMap.get(id);
		if(data == null) return false;
		return IsActivePack(data.entry);
	}
	public boolean IsActivePack(ResourcePackRepository.Entry entry) {
		ResourcePackRepository rpr = Minecraft.getMinecraft().getResourcePackRepository();
		return entry == null || rpr.getRepositoryEntries().contains(entry);
	}

	public IBakedModel GetModelById(CoasterSettings settings)
	{
		return CoasterModelMap.get(settings.ModelID);
	}
	public ResourceLocation GetTextureById(CoasterSettings settings)
	{
		return CoasterTextureMap.get(settings.ModelID);
	}

	public IBakedModel GetRailModelById(String id)
	{
		return RailModelMap.get(id);
	}
	public ResourceLocation GetRailTextureById(String id)
	{
		return RailTextureMap.get(id);
	}







    private void loadDefaultCoasterModelPack() throws Exception
	{
		CoasterPackData def = loadSettingsFromJsonForDef("MochiSystems.Default", ERC.MODID+":models/coaster.json");
		loadSettingsFromJsonForDef("MochiSystems.DoubleSeat", ERC.MODID+":models/double.json");
		loadSettingsFromJsonForDef("MochiSystems.Inverted", ERC.MODID+":models/inverted.json");

		CoasterPackData withEngine = new CoasterPackData();
		withEngine.IconName = def.IconName;
		withEngine.ModelNameMain = def.ModelNameMain;
		withEngine.ModelNameConnect = def.ModelNameConnect;
		withEngine.TextureName = def.TextureName;
		withEngine.MainSetting = CoasterSettings.Default().CopyFrom(def.MainSetting);
		withEngine.ConnectSetting = CoasterSettings.Default().CopyFrom(def.ConnectSetting);
		withEngine.MainSetting.AccelUnit = 10f;
//		withEngine.MainSetting.MaxSpeed = 1f;
		withEngine.MainSetting.MaxEngineLevel = 5;
		withEngine.MainSetting.ModelID = "MochiSystems.Engine";
		withEngine.ConnectSetting.ModelID = "MochiSystems.Engine"+".connect";
		CoasterPackDataMap.put("MochiSystems.Engine", withEngine);
	}

	private void loadExternalResourcePackModel()
	{
		ResourcePackRepository rpr = Minecraft.getMinecraft().getResourcePackRepository();
		List<ResourcePackRepository.Entry> repos = rpr.getRepositoryEntriesAll();
		for(ResourcePackRepository.Entry repo : repos){
			try {
				IResourcePack pack = repo.getResourcePack();
				boolean isPack = pack.resourceExists(new ResourceLocation("ercmodels", "setting.json"));
				if(!isPack) continue;
				PackData data = LoadSettingsFromResourcePack(pack.getInputStream(new ResourceLocation("ercmodels","setting.json")), PackData.class);
				boolean hasCoaster = pack.resourceExists(new ResourceLocation("ercmodels", "coaster.json"));
				if(hasCoaster){
					InputStream stream = pack.getInputStream(new ResourceLocation("ercmodels","coaster.json"));
					CoasterPackData coasterData = LoadSettingsFromResourcePack(stream, CoasterPackData.class);
					stream.close();
					coasterData.entry = repo;
					ModifyAndRegisterCoasterPackData(data.packID, coasterData, "ercmodels", data.packID +"/", "textures/", "");
				}
				boolean hasRail = pack.resourceExists(new ResourceLocation("ercmodels", "rail.json"));
				if(hasRail) {
					InputStream stream = pack.getInputStream(new ResourceLocation("ercmodels","rail.json"));
					RailPackData railData = LoadSettingsFromResourcePack(stream, RailPackData.class);
					stream.close();
					for(RailPackData.RailInfo info : railData.models)
					{
						info.entry = repo;
						ModifyAndRegisterRailPackData(data.packID, info, "ercmodels", data.packID +"/", "textures/");
					}
				}
			}
			catch (IOException e){
				ERC_Logger.warn("カスタムモデル読み込み中にエラーが発生しました。");
				return;
			}
		}
	}


	private <T> T LoadSettingsFromResourcePack(InputStream stream, Class<T> clazz) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(stream);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		for (int result = bis.read(); result != -1; result = bis.read()) {
			buf.write((byte) result);
		}
		String jsonStr = buf.toString("UTF-8");
		Gson gson = new Gson();
		return gson.fromJson(jsonStr, clazz);
	}

//	private DirectoryStream<Path> EnumerateExternalModelFolder() throws IOException
//	{
//		Path modelFolderPath = Paths.get(Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/erc_models");
//        if (!Files.exists(modelFolderPath)) {
//            Files.createDirectory(modelFolderPath);
//        }
//		return Files.newDirectoryStream(modelFolderPath);
//	}

//	private static void loadModelFromFolder(Path folderPath) throws Exception
//	{
//		Path settingFile = null;
//		try {
//			for (Path filePath : Files.newDirectoryStream(folderPath)) {
//				String fileName = filePath.toString().toLowerCase();
//				if (fileName.contains(".json")){
//					settingFile = filePath;
//					break;
//				}
//			}
//		}
//		catch (IOException e){
//			return;
//		}
//
//		if (settingFile == null) {
//			throw new IOException(folderPath.getFileName().toString() + "モデルのフォルダにsetting.jsonがありません");
//		}
//		StringBuilder builder = new StringBuilder();
//		for(String str : Files.readAllLines(settingFile)){
//			builder.append(str);
//		}
//		Gson gson = new Gson();
//		CoasterPackData packData = gson.fromJson(builder.toString(), CoasterPackData.class);
//		Path packFolderName = folderPath.getFileName();
//		ModifyAndRegisterPackData(packData, "minecraft", "/../../../erc_models/"+packFolderName+"/", "", "");
//	}


	private CoasterPackData loadSettingsFromJsonForDef(String id, String jsonName) throws Exception
	{
		// load file
        StringBuilder builder = new StringBuilder();
        try {
            ResourceLocation setting = new ResourceLocation(jsonName);
            InputStream stream = ERC.GetInModPackageFileStream(setting);
            InputStreamReader reader = new InputStreamReader(stream);
            char[] buffer = new char[512];
            int read;
			while (0 <= (read = reader.read(buffer))) {
				builder.append(buffer, 0, read);
			}
		}
		catch (IOException e){
			ERC_Logger.error("setting.jsonがModPackage内にありませんでした。");
			return null;
		}

		Gson gson = new Gson();
		CoasterPackData data = gson.fromJson(builder.toString(), CoasterPackData.class);
		ModifyAndRegisterCoasterPackData(id, data, ERC.MODID, "", "textures/entities/", "items/");
		return data;
	}

	private void ModifyAndRegisterCoasterPackData(String packId, CoasterPackData packData, String modId, String packagePath, String texturePath, String iconPath) throws IOException
	{
		//verify
		if (packData.ModelNameMain == null) {
			throw new IOException(String.format("%sコースターモデルのメインモデル情報のファイルがありません。\"ModelNameMain\"を設定してください。", packId));
		}
		if (packData.TextureName == null) {
			throw new IOException(String.format("%sコースターモデルのテクスチャ情報がありません。\"TextureName\"を設定してください。", packId));
		}
		if (packData.IconName == null) {
			throw new IOException(String.format("%sコースターモデルのアイコンファイル情報が見つかりません。\"IconName\"を設定してください。", packId));
		}

		//modify blank
		if(packData.ModelNameConnect == null || packData.ModelNameConnect.isEmpty())
			packData.ModelNameConnect = packData.ModelNameMain;

		packData.MainSetting.Fix();
		packData.MainSetting.ModelID = packId;
		if (packData.ConnectSetting == null)
			packData.ConnectSetting = packData.MainSetting;
		else {
			packData.ConnectSetting.Fix();
			packData.ConnectSetting.ModelID = packId + ".connect";
		}

		packData.IconName = modId + ":" + packagePath + iconPath + packData.IconName;

		//modify mesh location
		packData.ModelNameMain = modId+":"+packagePath+packData.ModelNameMain;
		if(packData.ModelNameConnect != null && !packData.ModelNameConnect.isEmpty())
			packData.ModelNameConnect = modId + ":" + packagePath + packData.ModelNameConnect;
		packData.TextureName = modId + ":" + texturePath + packagePath + packData.TextureName;

		CoasterPackDataMap.put(packId, packData);
	}

	private void ModifyAndRegisterRailPackData(String id, RailPackData.RailInfo packData, String modId, String packagePath, String texturePath) throws IOException {
		//verify
		if (packData.mesh == null) {
			throw new IOException(String.format("%sレールモデルのメッシュ情報がありません。\"models[].id\"を設定してください。", id));
		}
		if (packData.texture == null) {
			throw new IOException(String.format("%sレールモデルのテクスチャ情報がありません。\"models[].texture\"を設定してください。", id));
		}
		if (packData.icon == null) {
			throw new IOException(String.format("%sレールモデルのアイコン情報が見つかりません。\"models[].icon\"を設定してください。", id));
		}
		packData.id = id + ":" + packData.id;
		packData.icon = modId + ":" + packagePath + packData.icon;
		packData.mesh = modId+":"+packagePath + packData.mesh;
		packData.texture = modId+":"+texturePath + packagePath + packData.texture;

		RailPackMap.put(packData.id, packData);
	}



	private IBakedModel loadModel(String fileNameWithDomain) throws Exception {
		OBJModel model = (OBJModel)ModelLoaderRegistry.getModel(new ResourceLocation(fileNameWithDomain));
//		Map<String, String> map = new HashMap<>(); map.put("#OBJModel.Default.Texture.Name", ERC_Core.MODID+":entities/coaster.png");
//		ImmutableMap<String, String> imm = ImmutableMap.copyOf(map);
//		model = model.retexture(imm);
//		ImmutableMap<String, String> customs = ImmutableMap.of("flip-v", "true");
		model.getMatLib().getMaterial(OBJModel.Material.DEFAULT_NAME).setTexture(new OBJModel.Texture("builtin/white", new Vector2f(0, 0), new Vector2f(1, 1), 0));
		IBakedModel baked = model.bake(
				model.getDefaultState(),
				DefaultVertexFormats.POSITION_TEX_NORMAL,
				name -> Dummy.Instance);
		return baked;
	}






	public static final class Dummy extends TextureAtlasSprite
	{
		public static final ResourceLocation LOCATION = new ResourceLocation("Dummy");
		public static final Dummy Instance = new Dummy();

		private Dummy()
		{
			super(LOCATION.toString());
			this.width = this.height = 32;
		}

		public float getMinU(){return 0f;}
		public float getMinV(){return 0f;}
		public float getMaxU(){return 1f;}
		public float getMaxV(){return 1f;}
		public float getInterpolatedU(double u)
		{
			return (float)u/16f;
		}
		public float getInterpolatedV(double v)
		{
			return 16f - (float)v/16f;
		}
		public float getUnInterpolatedU(float u){return u*16f;}
		public float getUnInterpolatedV(float v){return 16f - v*16f;}

		@Override
		public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
		{
			return true;
		}

		@Override
		public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter)
		{
			return false;
		}
	}


}
