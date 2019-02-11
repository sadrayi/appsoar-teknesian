package ir.appsoar.teknesian.Activity.divar.Models;

public class ListViewModel {
    private String title;
    private String ostan;
    private String city;
    private String price;
    private String image;
    private String id;
    private String verify;
    private String phone;
    private String detail;
    private String createdate;
    private String vaziat;
    private String kind;

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public void setVaziat(String vaziat) {
        this.vaziat = vaziat;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getVaziat() {
        return vaziat;
    }

    public String getCreatedate() {
        return createdate;
    }

    public String getKind() {
        return kind;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public String getDetail() {
        return detail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setOstan(String ostan) {
        this.ostan = ostan;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getOstan() {
        return ostan;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
