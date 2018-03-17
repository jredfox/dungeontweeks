package com.EvilNotch.dungeontweeks.util.Line;

public class LineItemStack extends LineItemStackBase{

	public int head = -1;
	public float fhead = -1.0F;
	public double dhead = -1.0D;
	public boolean bhead = false;
	public byte bytehead = -1;
	public short shorthead = -1;
	public long lhead = -1;
	public String strhead = null;
	
	public boolean hashead = false;
	public boolean hasfhead = false;
	public boolean hasdhead = false;
	public boolean hasbhead = false;
	public boolean hasStringHead = false;
	public boolean hasbytehead = false;
	public boolean hasshorthead = false;
	public boolean haslhead = false;
	public boolean strLegacy = false;
	
	public LineItemStack(String s,char sep, char q,char... invalid)
	{
		super(s,sep,q,invalid);
		if(s.contains("="))
			parseWeight(s);
	}
	
	public LineItemStack(String s) 
	{
		super(s);
		if(s.contains("="))
			parseWeight(s);
	}
	/**
	 * Returns the head of the line supports [int,boolean,byte,short,long,float,double,String]
	 */
	protected void parseWeight(String s) 
	{
		try
		{
		String[] parts = LineBase.getParts(LineBase.toWhiteSpaced(s), "=");
		String str = parts[1];
		if(str == null || str.equals("null"))
			return;
		boolean isnum = LineBase.isStringNum(str);
		boolean isboolean = str.toLowerCase().contains("true") || str.toLowerCase().contains("false");
		if(isboolean)
		{
			if(str.toLowerCase().indexOf("true") == 0)
				str = "true";
			else if(str.toLowerCase().indexOf("false") == 0)
				str = "false";
			this.bhead = Boolean.parseBoolean(str);
			this.hasbhead = true;
			return;
		}
		else if(!isnum)
		{
			String numbers = "1234567890";
			String charCheck = str.substring(0,1);
			String parsedNum = "";
			if(numbers.contains(charCheck))
				parsedNum = parseNum(str);
			isnum = LineBase.isStringNum(parsedNum) && !str.contains("" + this.seperator) && !str.contains("" + this.quote);
			if(isnum)
				str = parsedNum;
		}
		if(!isnum && !isboolean)
		{
			String[] parts_unspaced = LineBase.getParts(s, "=");
			String stringlinehead = parts_unspaced[1];
			if(str.contains("" + this.quote))
				this.strhead =  LineBase.parseQuotes(stringlinehead,stringlinehead.indexOf("" + this.quote),"" + this.quote);//if all other possibilities are canceled it must simply be a string
			else{
				this.strhead = stringlinehead.trim();
				this.strLegacy = true;
			}
			this.hasStringHead = true;
			return;
		}
		if(str.contains(".") && str.toLowerCase().endsWith("d") || str.toLowerCase().endsWith("d"))
		{
			this.dhead = Double.parseDouble(str);
			this.hasdhead = true;
			return;
		}
		if(str.contains(".") && str.toLowerCase().endsWith("f")|| str.toLowerCase().endsWith("f"))
		{
			this.fhead = Float.parseFloat(str);
			this.hasfhead = true;
			return;
		}
		if(str.contains(".") && !this.hasfhead && !this.hasdhead)
		{
			this.dhead = Double.parseDouble(str);
			this.fhead = Float.parseFloat(str);
			this.hasdhead = true;
			this.hasfhead = true;
			return;
		}
		if(str.toLowerCase().endsWith("b"))
		{
			this.bytehead = Byte.parseByte(str.substring(0, str.length()-1));
			this.hasbytehead = true;
			return;
		}
		if(str.toLowerCase().endsWith("s"))
		{
			this.shorthead = Short.parseShort(str.substring(0, str.length()-1));
			this.hasshorthead = true;
			return;
		}
		if(str.toLowerCase().endsWith("l"))
		{
			this.lhead = Long.parseLong(str.substring(0, str.length()-1));
			this.haslhead = true;
			return;
		}
		if(str.endsWith("i"))
			str = str.substring(0, str.length()-1);
		int value = (int)Long.parseLong(str);
		this.head = value;
		this.hashead = true;
		//Fix for if I were to use byte,short,or long ever
		this.bytehead = (byte)value;
		this.shorthead = (short)value;
		this.lhead = Long.parseLong(str);
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * no checking just do it exceptions may occur to a check before parsing this
	 */
	public String parseNum(String str) {
		String valid = "1234567890.";
		String valid_endings = "bslfdi";//byte,short,long,float,double,int
		String num = "";
		for(int i=0;i<str.length();i++)
		{
			String char1 = str.substring(i, i+1).toLowerCase();
			boolean isEnding = valid_endings.contains(char1);
			if(valid.contains(char1) || isEnding)
				num += char1;
			else
				break;
			
			if(isEnding)
				break;
		}
		if(num.endsWith("."))
			num = num.substring(0, num.length()-1);
		return num;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + " = " + getDisplayHead();
	}
	@Override
	public String getString()
	{
		String str = super.getString();
		if(this.getHead() != null)
			str += " = " + this.getDisplayHead();
		return str;
	}
	/**
	 * Returns one of the following[int,boolean,byte,short,long,float,double,String]
	 * @return
	 */
	@Override
	public Object getHead() 
	{
		if(this.hashead)
			return this.head;
		if(this.hasdhead)
			return this.dhead;
		if(this.hasfhead)
			return this.fhead;
		if(this.hasStringHead)
			return this.strhead;
		if(this.hasbhead)
			return this.bhead;
		if(this.hasbytehead)
			return this.bytehead;
		if(this.hasshorthead)
			return this.shorthead;
		if(this.haslhead)
			return this.lhead;
		return null;
	}
	public String getDisplayHead()
	{
		String str = String.valueOf(this.getHead());
		if(this.hasdhead)
			return str + "D";
		if(this.hasfhead)
			return str + "F";
		if(this.hasStringHead)
		{
			String q = this.strLegacy ? "" : "" + this.quote;
			return q + this.strhead + q;
		}
		if(this.hasbytehead)
			return str + "B";
		if(this.hasshorthead)
			return str + "S";
		if(this.haslhead)
			return str + "L";
		
		return str;
	}
	

}
