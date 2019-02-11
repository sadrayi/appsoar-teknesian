package ir.appsoar.teknesian.Models;

public class ItemFactor {

    private String itemcost;
    private String itemname;
    private String itemcount;
    private String itemid;


    public ItemFactor(String itemcost, String itemcount, String itemid, String itemname) {

        this.itemcost = itemcost;
        this.itemcount = itemcount;
        this.itemid = itemid;
        this.itemname = itemname;
    }

    public ItemFactor() {
    }

    public String getItemcost() {
        return itemcost;
    }
    public void setItemcost(String itemcost) {
        this.itemcost = itemcost;
    }

    public String getItemname() {
        return itemname;
    }
    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getItemcount() {
        return itemcount;
    }
    public void setItemcount(String itemcount) {
        this.itemcount = itemcount;
    }

    public String getItemid() {
        return itemid;
    }
    public void setItemid(String itemid) {
        this.itemid = itemid;
    }
}
