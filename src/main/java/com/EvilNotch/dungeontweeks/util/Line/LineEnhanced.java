package com.EvilNotch.dungeontweeks.util.Line;

public class LineEnhanced extends LineBase{
	public int head;
	
	public LineEnhanced(String s) 
	{
		super(s);
		this.head = parseHead(s);
	}
	
	/**
	 * Returns the head of the line
	 * @param s
	 * @return
	 */
	private int parseHead(String s) 
	{
		String[] parts = LineBase.getParts(LineBase.toWhiteSpaced(this.strLineBase), "=");
		return Integer.parseInt(parts[1]);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof LineBase) || !(obj instanceof LineDynamic) && !(obj instanceof LineEnhanced))
			return false;

		if(obj instanceof LineEnhanced)
		{
			LineEnhanced line = (LineEnhanced)obj;
			if(this.head != line.head)
				return false;//Enhanced Line and dynamic line can equal each other only if they have one head value and the same with LineBase Values
		}
		else{
			LineDynamic line = (LineDynamic)obj;
			if(!line.hasvalues || this.head != line.values[0] || line.values.length > 1)
				return false;//Enhanced Line and dynamic line can equal each other only if they have one head value and the same with LineBase Values
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString()
	{
		String s = super.toString();
		return s + " = " + this.head;
	}

}
