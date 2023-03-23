package jp.mochisystems.erc.manager;

import jp.mochisystems.erc._mc._core.ERC_Logger;
import jp.mochisystems.erc.coaster.Coaster;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.HashMap;
import java.util.Map;

public class CoasterIdManager {
    public static CoasterIdManager Instance = new CoasterIdManager();

    private final Map<Integer, Coaster> idMap_C = new HashMap<>();
    private final Map<Integer, Coaster> idMap_S = new HashMap<>();
    private int id;

    protected CoasterIdManager(){}

    public int RegisterNew(Coaster entity, boolean isRemote){
//        Logger.debugInfo("RegisterNew : "+entity);
        Map<Integer, Coaster> idMap = isRemote ? idMap_C : idMap_S;
        int id = this.id++;
        while(idMap.containsKey(id)){id++;}
        idMap.put(id, entity);
        return id;
    }

    public void RegisterContained(int id, Coaster coaster, boolean isRemote){
//        Logger.debugInfo("RegisterContained : "+id+" ~ "+coaster);
        Map<Integer, Coaster> idMap = isRemote ? idMap_C : idMap_S;
        if(idMap.containsKey(id)){
            ERC_Logger.error("DevError:duplicate coaster id");
            return;
        }
        idMap.put(id, coaster);
    }

    public Coaster Get(int id, boolean isRemote){
//        Logger.debugInfo("Get : "+id);
        Map<Integer, Coaster> idMap = isRemote ? idMap_C : idMap_S;
//        if(id != idMap.get(id).coasterId){
//            Logger.debugInfo("????");
//        }
        return idMap.get(id);
    }

    public void Remove(int id, boolean isRemote){
        Map<Integer, Coaster> idMap = isRemote ? idMap_C : idMap_S;
        idMap.remove(id);
    }


    @SubscribeEvent
    public void deleteAllBuffer(FMLNetworkEvent.ClientDisconnectionFromServerEvent e)
    {
        idMap_C.clear();
        idMap_S.clear();
    }
}
