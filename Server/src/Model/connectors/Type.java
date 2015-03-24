package Model.connectors;

public enum Type {

	CCS_COMBO ("CCS Combo","CSS"),
	BS_1363 ("Domestic plug/socket type G (BS 1363)","BS1363"),
	IEC_60309 ("IEC60309 3P+N+E,6h,32A","IEC60309"),
	IEC_TYPE_1 ("IEC 62196-2 type 1 (SAE J1772)","type1"),
	IEC_TYPE_2 ("IEC 62196-2 type 2","type2"),
	IEC_TYPE_3 ("IEC 62196-2 type 3","type3"),
	JEVS_G_105 ("JEVS G 105 (CHAdeMO)","CHAdeMO");
	
	private final String description;
	private final String shortDesc;
	
	Type(String description, String shortDesc){
		this.description = description;
		this.shortDesc = shortDesc;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getShortDescription(){
		return shortDesc;
	}
	
}
