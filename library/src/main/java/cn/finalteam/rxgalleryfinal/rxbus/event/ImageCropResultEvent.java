package cn.finalteam.rxgalleryfinal.rxbus.event;

public class ImageCropResultEvent implements BaseResultEvent {

    public ImageCropResultEvent(String cropPath) {
        this.cropPath = cropPath;
    }

    private String cropPath;

    public String getCropPath() {
        return cropPath;
    }

    public void setCropPath(String cropPath) {
        this.cropPath = cropPath;
    }
}
