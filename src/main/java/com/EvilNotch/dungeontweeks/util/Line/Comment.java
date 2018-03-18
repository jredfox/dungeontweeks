package com.EvilNotch.dungeontweeks.util.Line;

import com.EvilNotch.dungeontweeks.util.ICopy;

public class Comment implements ICopy{
    public int lineIndex = -1;
    public String comment = null;
    public ILine nearestLine = null;
    public boolean isAttactched = false;
    public char start = '#';
    
    public Comment(String comment)
    {
        this(0,comment);
    }
    public Comment(String comment,char c)
    {
        this(0,comment,false,c);
    }
    public Comment(int index, String comment)
    {
        this(index,comment,false);
    }
    public Comment(int index, String comment,boolean attatched)
    {
        this(index,comment,attatched,'#');
    }
    public Comment(int index, String comment,boolean attatched,char c)
    {
    	this(index,comment,attatched,c,null);
    }
    public Comment(int index, String comment,boolean attatched,char c,ILine nearLine)
    {
        this.start = c;
        this.lineIndex = index;
        this.comment = comment.substring(comment.indexOf(c)+1, comment.length() );
        this.isAttactched = attatched;
        if(nearLine != null)
        	this.nearestLine = nearLine;//in case line changes keep memory location also more optimized
    }
    
    public boolean equals(Object obj,boolean compareIndex){
    	 if(!(obj instanceof Comment))
             return false;
         Comment c = (Comment)obj;
         boolean index = true;
         if(compareIndex)
        	 index = this.lineIndex == c.lineIndex;
         return index && this.comment.equals(c.comment) && this.start == c.start && this.isAttactched == c.isAttactched;
    }
    @Override
    public boolean equals(Object obj){
       return this.equals(obj, true);
    }
    
    @Override
    public String toString()
    {
        return "" + this.start + this.comment + ":" + this.lineIndex;
    }
	@Override
	public ICopy copy() {
		return new Comment(this.lineIndex,this.comment,this.isAttactched,this.start,this.nearestLine);
	}

}
