package com.EvilNotch.dungeontweeks.util.Line;

import java.util.List;

import com.EvilNotch.dungeontweeks.util.JavaUtil;

public class LineDynamic extends LineBase{

	public int[] values = new int[0];
	public boolean[] bvalues = new boolean[0];
	public byte[] bytevalues = new byte[0];
	public short[] shortvalues = new short[0];
	public long[] lvalues = new long[0];
	public float[] fvalues = new float[0];
	public double[] dvalues = new double[0];
	public String[] stringValues = new String[0];
	
	public boolean hasvalues = false;
	public boolean hasbvalues = false;
	public boolean hasbytevalues = false;
	public boolean hasshortvalues = false;
	public boolean haslvalues = false;
	public boolean hasfvalues = false;
	public boolean hasdvalues = false;
	public boolean hasStringValues;
	
	public LineDynamic(String s) 
	{
		super(s);
		getHeadEnchantments(); //Gets Dynamic Lines old format modid:block = 1,1,3.....
	}
	
	public void getHeadEnchantments() 
	{
		try
		{
		String[] parts = LineBase.getParts(this.strLineBase, "=");
		String[] strench = LineBase.getParts(parts[1], ",");
		String str = strench[0];
		boolean isnum = LineBase.isStringNum(LineBase.toWhiteSpaced(str));
		boolean isboolean = LineBase.toWhiteSpaced(str.toLowerCase()).equals("true") || LineBase.toWhiteSpaced(str.toLowerCase()).equals("false");
		
		if(!isnum && !isboolean)
		{
			this.stringValues = new String[strench.length];//Create length to current amount of ints
			for(int i=0;i<strench.length;i++)
			{
				String s = strench[i];
				this.stringValues[i] = LineBase.parseQuotes(s,s.indexOf("\""));
			}
			this.hasStringValues = true;
			return;
		}
		if(isboolean)
		{
			this.bvalues = new boolean[strench.length];//Create length to current amount of ints
			for(int i=0;i<strench.length;i++)
				this.bvalues[i] = Boolean.parseBoolean(LineBase.toWhiteSpaced(strench[i]));
			this.hasbvalues = true;
			return;
		}
		if(str.contains(".") && str.toLowerCase().endsWith("d")|| str.toLowerCase().endsWith("d"))
		{
			this.dvalues = new double[strench.length];//Create length to current amount of ints
			for(int i=0;i<strench.length;i++)
				this.dvalues[i] = Double.parseDouble(LineBase.toWhiteSpaced(strench[i]));
			this.hasdvalues = true;
			return;
		}
		if(str.contains(".") &&  str.toLowerCase().endsWith("f") || str.toLowerCase().endsWith("f"))
		{
			this.fvalues = new float[strench.length];//Create length to current amount of ints
			for(int i=0;i<strench.length;i++)
				this.fvalues[i] = Float.parseFloat(LineBase.toWhiteSpaced(strench[i]));
			this.hasfvalues = true;
			return;
		}
		if(str.contains(".") || containsDoubleOrFloat(strench))
		{			
			this.fvalues = new float[strench.length];//Create length to current amount of ints
			this.dvalues = new double[strench.length];//Create length to current amount of ints
			for(int i=0;i<strench.length;i++)
			{
				String s = LineBase.toWhiteSpaced(strench[i].toLowerCase());
				if(s.endsWith("d") || s.endsWith("f"))
					s = s.substring(0, s.length()-1);//gets rid of them from bad configurations
				this.fvalues[i] = Float.parseFloat(s);
				this.dvalues[i] = Double.parseDouble(s);
			}
			this.hasfvalues = true;
			this.hasdvalues = true;
			return;
		}
		if(str.toLowerCase().endsWith("b"))
		{
			this.bytevalues = new byte[strench.length];//Create length to current amount of ints
			for(int i=0;i<strench.length;i++)
			{
				String s = LineBase.toWhiteSpaced(strench[i].toLowerCase());
				if(s.endsWith("b"))
					s = s.substring(0, s.length()-1);
				
				this.bytevalues[i] = Byte.parseByte(s);
			}
			this.hasbytevalues = true;
			return;
		}
		if(str.toLowerCase().endsWith("s"))
		{
			this.shortvalues = new short[strench.length];//Create length to current amount of ints
			for(int i=0;i<strench.length;i++)
			{
				String s = LineBase.toWhiteSpaced(strench[i].toLowerCase());
				if(s.endsWith("s"))
					s = s.substring(0, s.length()-1);
				
				this.shortvalues[i] = Short.parseShort(s);
			}
			this.hasshortvalues = true;
			return;
		}
		if(str.toLowerCase().endsWith("l"))
		{
			this.lvalues = new long[strench.length];//Create length to current amount of ints
			for(int i=0;i<strench.length;i++)
			{
				String s = LineBase.toWhiteSpaced(strench[i].toLowerCase());
				if(s.endsWith("l"))
					s = s.substring(0, s.length()-1);
				
				this.lvalues[i] = Long.parseLong(s);
			}
			this.haslvalues = true;
			return;
		}
		if(!isnum)
			return;
		this.values = new int[strench.length];
		if(!str.endsWith("i"))
		{
			this.bytevalues = new byte[strench.length];
			this.shortvalues = new short[strench.length];
			this.lvalues = new long[strench.length];
		}
		
		for(int i=0;i<strench.length;i++)
		{
			String s = LineBase.toWhiteSpaced(strench[i].toLowerCase());
			if(s.endsWith("b") || s.endsWith("l") || s.endsWith("s") || s.endsWith("i"))
				s = s.substring(0, s.length()-1);
			
			int value = (int)Long.parseLong(s);
			this.values[i] = value;
			if(!str.endsWith("i"))
			{
				this.bytevalues[i] = (byte)value;
				this.shortvalues[i] = (short)value;
				this.lvalues[i] = Long.parseLong(s);
			}
			this.hasvalues = true;
		 }
	  }catch(Exception e){e.printStackTrace();}
	}
	
	public boolean containsDoubleOrFloat(String[] strench) 
	{
		for(int i=0;i<strench.length;i++)
		{
			String s = LineBase.toWhiteSpaced(strench[i].toLowerCase());
			if(s.endsWith("d") || s.endsWith("f") || s.contains("."))
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof LineBase) || !(obj instanceof LineDynamic) && !(obj instanceof LineItemStack))
			return false;

		if(obj instanceof LineItemStack)
		{
			LineItemStack line = (LineItemStack)obj;
			List li = this.getValues();
			if(li == null)
				return false;
			if(!this.hasvalues || line.getHead() != li.get(0) || li.size() > 1)
				return false;//Enhanced Line and dynamic line can equal each other only if they have one head value and the same with LineBase Values
		}
		else{
			List list = this.getValues();
			List objlist = ((LineDynamic)obj).getValues();
			if(list == null || objlist == null)
				return super.equals(obj) && list == null && objlist == null;
			if(list.size() != objlist.size())
				return false;
			for(int i=0;i<list.size();i++)
			{
				Object object = list.get(i);
				Object object2 = objlist.get(i);
				if(object == null || object2 == null)
					return false;
				if(!object.equals(object2))
					return false;
			}
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString()
	{
		return super.toString() + " = " + getDisplayValues();
	}
	
	public List getValues()
	{
		if(this.hasvalues)
			return JavaUtil.staticToArray(this.values);
		if(this.hasdvalues)
			return JavaUtil.staticToArray(this.dvalues);
		if(this.hasfvalues)
			return JavaUtil.staticToArray(this.fvalues);
		if(this.hasStringValues)
			return JavaUtil.staticToArray(this.stringValues);
		if(this.hasbvalues)
			return JavaUtil.staticToArray(this.bvalues);
		if(this.hasbytevalues)
			return JavaUtil.staticToArray(this.bytevalues);
		if(this.hasshortvalues)
			return JavaUtil.staticToArray(this.shortvalues);
		if(this.haslvalues)
			return JavaUtil.staticToArray(this.lvalues);
		return null;
	}

	public String getDisplayValues() 
	{
		if(this.hasvalues)
			return JavaUtil.getStaticArrayStringWithLiteral(this.values);
		if(this.hasdvalues)
			return JavaUtil.getStaticArrayStringWithLiteral(this.dvalues);
		if(this.hasfvalues)
			return JavaUtil.getStaticArrayStringWithLiteral(this.fvalues);
		if(this.hasStringValues)
			return JavaUtil.getStaticArrayStringWithLiteral(this.stringValues);
		if(this.hasbvalues)
			return JavaUtil.getStaticArrayString(this.bvalues);
		if(this.hasbytevalues)
			return JavaUtil.getStaticArrayStringWithLiteral(this.bytevalues);
		if(this.hasshortvalues)
			return JavaUtil.getStaticArrayStringWithLiteral(this.shortvalues);
		if(this.haslvalues)
			return JavaUtil.getStaticArrayStringWithLiteral(this.lvalues);
		return null;
	}

}
