package cet.globalMultiMiceManager.occlusion;

public interface ICETOcclusionListener {
	public void addOcclusionHandler(ICETOcclusionHandler handler);
	public void removeOcclusionHandler(ICETOcclusionHandler handler);
	public void processOcclusion(CETOcclusionEvent event);
	public void postOcclusion(CETOcclusionEvent event);
}
