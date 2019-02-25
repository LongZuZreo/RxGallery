package cn.finalteam.rxgalleryfinal.rxbus.event;

import cn.finalteam.rxgalleryfinal.rxbus.RxMessage;

public class ImageCropResultEvent extends RxMessage {

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
