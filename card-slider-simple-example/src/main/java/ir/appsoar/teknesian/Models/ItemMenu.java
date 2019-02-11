package ir.appsoar.teknesian.Models;

public class ItemMenu {

    private Long MenuId;
    private String MenuImage;
    private String MenuName;
    private String MenuPrice;


    public Long getMenuId() {
        return MenuId;
    }

    public void setMenuId(Long menuId) {
        MenuId = menuId;
    }

    public String getMenuImage() {
        return MenuImage;
    }

    public void setMenuImage(String menuImage) {
        MenuImage = menuImage;
    }

    public String getMenuName() {
        return MenuName;
    }

    public void setMenuName(String menuName) {
        MenuName = menuName;
    }

    public String getMenuPrice() {
        return MenuPrice;
    }

    public void setMenuPrice(String menuPrice) {
        MenuPrice = menuPrice;
    }

}
