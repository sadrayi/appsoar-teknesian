package ir.appsoar.teknesian.Models;

public class ItemSafety {
	
	private Long SId;
	private String STitle;
	private String SDegree;
	private String SContent;
	private String Createddate;
	private String Type;

	public Long getSId() {
		return SId;
	}

	public void setSId(Long cid) {
		this.SId = cid;
	}

	public String getSTitle() {
		return STitle;
	}

	public void setSTitle(String sTitle) {
		this.STitle = sTitle;
	}

	public String getSDegree() {
		return SDegree;
	}

	public void setSDegree(String SDegree) {
		this.SDegree = SDegree;
	}

	public String getSContent() {
		return SContent;
	}

	public void setSContent(String SContent) {
		this.SContent = SContent;
	}


	public String getType() {
		return Type;
	}

	public void setType(String Type) {
		this.Type = Type;
	}



	public String getCreateddate() {
		return Createddate;
	}

	public void setCreateddate(String Createddate) {
		this.Createddate = Createddate;
	}




}
