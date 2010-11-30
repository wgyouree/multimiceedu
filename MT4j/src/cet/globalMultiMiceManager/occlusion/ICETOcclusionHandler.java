package cet.globalMultiMiceManager.occlusion;

public interface ICETOcclusionHandler {
	public void handleOcclusion(CETOcclusionEvent event);
	public void postOcclusion(CETOcclusionEvent event);
}
