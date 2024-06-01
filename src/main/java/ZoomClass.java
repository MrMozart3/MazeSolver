public class ZoomClass {
    private int zoomLevel;
    private int zoomScale;
    private int maxZoomLevel;
    private int[] zoomArray = {0, 1, 2, 3, 5};
    ZoomClass (int zoomScale, int zoomLevel, int maxZoomLevel){
        this.zoomLevel = zoomLevel;
        this.zoomScale = zoomScale;
        this.maxZoomLevel = maxZoomLevel;
    }
    public void zoomIn(){
        if(zoomLevel < zoomArray.length - 1 && zoomLevel < maxZoomLevel) {
            zoomLevel++;
        }
    }
    public void zoomOut(){
        if(zoomLevel > 0){
            zoomLevel--;
        }
    }
    public boolean isZoomableIn(){
        return zoomLevel < zoomArray.length - 1 && zoomLevel < maxZoomLevel;
    }
    public boolean isZoomableOut(){
        return zoomLevel > 0;
    }
    public int getWallSize(){
        return zoomArray[zoomLevel];
    }
    public int getPathSize(){
        return zoomArray[zoomLevel] * zoomScale == 0 ? 1 : zoomArray[zoomLevel] * zoomScale;
    }
}
