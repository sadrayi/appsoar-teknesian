package ir.appsoar.teknesian.Models;

public class PriceModel {
    private String sherkatid;
    private String sherkatname;
    private String sherkataddress;
    private String sherkatphone;
    public PriceModel(String sherkatid,String sherkatname,String sherkataddress,String sherkatphone)
    {
        this.sherkatid=sherkatid;
        this.sherkatphone=sherkatphone;
        this.sherkataddress=sherkataddress;
        this.sherkatname=sherkatname;
    }

    public String getSherkatid() {
        return sherkatid;
    }

    public String getSherkatname() {
        return sherkatname;
    }
    public String getSherkataddress() {
        return sherkataddress;
    }
    public String getSherkatphone() {
        return sherkatphone;
    }


    public void setSherkatid(String sherkatid) {
        this.sherkatid = sherkatid;
    }

    public void setSherkatname(String sherkatname) {
        this.sherkatname = sherkatname;
    }

    public void setSherkataddress(String sherkataddress) {
        this.sherkataddress = sherkataddress;
    }

    public void setSherkatphone(String sherkatphone) {
        this.sherkatphone = sherkatphone;
    }

}
