package common.constants;

public class AssetData {
    
    private final int width;
    private final int height;
    private final String url;

    public AssetData(final int width, final int height, final String url) {
        this.width = width;
        this.height = height;
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }
}
