package common.constants;

import network.model.enums.CarType;

import static common.constants.Constants.ASSETS;
import static common.constants.Constants.FROG_SIZE;

public enum Assets {
    
    CARS("car"),
    FROG("frog")
    ;
    
    private final String name;
    Assets(final String _name) {
        name = _name;
    }
    
    public AssetData getFrogData(boolean first) {
        checkAccess(FROG);
        return new AssetData(FROG_SIZE, FROG_SIZE, getUrl(first ? 1 : 2));
    }    
    
    public AssetData getDeadFrogData() {
        checkAccess(FROG);
        return new AssetData(FROG_SIZE, FROG_SIZE, getUrl(3));
    }

    public AssetData getCarsData(final CarType type, boolean leftToRight) {
        checkAccess(CARS);
        int width = 102;
        int height = 51;
        int assetIndex;
        switch (type) {
            case WAGON:
                assetIndex = 1;
                break;
            case SEDAN: 
                assetIndex = 2;
                break;
            default: throw new IllegalArgumentException("Unknown car type " + type);
        }
        String url = getUrl(leftToRight, assetIndex);
        return new AssetData(width, height, url);
    }

    private void checkAccess(Assets required) {
        if (this != required) throw new IllegalArgumentException(
                String.format("This assets are designed for %s, not %s", required, this)
        );
    }

    private String getUrl(final boolean leftToRight, final int assetIndex) {
        return String.format("%s/%s%d-%s.png", ASSETS, name, assetIndex, leftToRight ? 'r' : 'l');
    }

    private String getUrl(final int assetIndex) {
        return String.format("%s/%s%d.png", ASSETS, name, assetIndex);
    }
}
