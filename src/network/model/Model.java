package network.model;

public interface Model {
    
    double getX();
    
    double getY();
    
    double getWidth();
    
    double getHeight();
    
    default boolean hits(Model model) {
        if (null == model) return false;
        return     getX() < model.getX() + model.getWidth()
                && getX() + getWidth() > model.getX()
                && getY() <= model.getY()
                && getY() + getHeight() > model.getY(); // simplify y
    }
    
    default boolean overlapsOnTheLeft(Model model) {
        if (null == model) return false;
        return getX() + getWidth() >= model.getX();
    }
    
    default boolean overlapsOnTheRight(Model model) {
        if (null == model) return false;
        return getX() <= model.getX() + model.getWidth();
    }

}
