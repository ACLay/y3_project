package Model.connectors;

public enum Type {

	CCS_COMBO ("CCS Combo"),
	BS_1363 ("Domestic plug/socket type G (BS 1363)"),
	IEC_60309 ("IEC60309 3P+N+E,6h,32A"),
	IEC_TYPE_1 ("IEC 62196-2 type 1 (SAE J1772)"),
	IEC_TYPE_2 ("IEC 62196-2 type 2"),
	IEC_TYPE_3 ("IEC 62196-2 type 3"),
	JEVS_G_105 ("JEVS G 105 (CHAdeMO)");
	
	private final String description;
	
	Type(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
	
}
