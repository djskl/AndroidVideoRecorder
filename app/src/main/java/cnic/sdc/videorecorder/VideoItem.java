package cnic.sdc.videorecorder;

public class VideoItem {
    ItemType item_type;
    String path;

    public VideoItem(ItemType t, String path){
        this.item_type = t;
        this.path = path;
    }
}

enum ItemType {
    BUTTON, VIDEO
}