
package ir.appsoar.teknesian.Models;

public class BuildingHistoryModel {


    private String id;
    private String date;
    private String kind;
    private String teknesian;
    public BuildingHistoryModel(String id, String date, String kind, String teknesian){
        this.id=id;
        this.date=date;
        this.kind=kind;
        this.teknesian=teknesian;
    }
    public String getId(){
        return id;
    }
    public String getDate(){
        return date;
    }
    public String getKind(){
        return kind;
    }
    public String getTeknesian(){
        return teknesian;
    }
}