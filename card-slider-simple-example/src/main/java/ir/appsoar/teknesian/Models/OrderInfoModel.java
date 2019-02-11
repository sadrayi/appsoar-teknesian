package ir.appsoar.teknesian.Models;

public class OrderInfoModel {

    private String catName;
    private String requestStatus;
    private String requestAddressPosti;
    private String requestComment;
    private String teknesianId;

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestAddressPosti() {
        return requestAddressPosti;
    }

    public void setRequestAddressPosti(String requestAddressPosti) {
        this.requestAddressPosti = requestAddressPosti;
    }

    public String getRequestComment() {
        return requestComment;
    }

    public void setRequestComment(String requestComment) {
        this.requestComment = requestComment;
    }

    public String getTeknesianId() {
        return teknesianId;
    }

    public void setTeknesianId(String teknesianId) {
        this.teknesianId = teknesianId;
    }

}