package jp.mochisystems.erc.rail;

import jp.mochisystems.core.util.IModelController;

public interface IRailController extends IModelController {
    Rail GetRail(int x, int y, int z);
    void NotifyChange();
    void SyncData();
    void SyncMiscData();
    void UpdateRenderer();
    boolean IsActive();
}
