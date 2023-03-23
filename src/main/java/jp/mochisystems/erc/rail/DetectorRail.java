package jp.mochisystems.erc.rail;

import io.netty.buffer.ByteBuf;

public class DetectorRail extends Rail{

    private boolean existCoaster = false;
    public boolean ExistCoaster()
    {
        return existCoaster;
    }

    @Override
    public void OnEnterCoaster()
    {
        existCoaster = true;
        controller.NotifyChange();
        controller.SyncMiscData();
    }

    @Override
    public void OnLeaveCoaster()
    {
        existCoaster = false;
        controller.NotifyChange();
        controller.SyncMiscData();
    }

    @Override
    public void OnDeleteCoaster()
    {
        OnLeaveCoaster();
    }

    public void WriteOptionToBytes(ByteBuf buffer)
    {
        buffer.writeBoolean(existCoaster);
    }

    public void ReadOptionFromBytes(ByteBuf buffer)
    {
        existCoaster = buffer.readBoolean();
    }
}
