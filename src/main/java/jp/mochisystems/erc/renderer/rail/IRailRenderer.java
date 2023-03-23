package jp.mochisystems.erc.renderer.rail;


import jp.mochisystems.erc.rail.Rail;

public interface IRailRenderer{
    void DeleteBuffer();
    void SetDirty();
    void SetRail(Rail rail);
	void Render();
}
