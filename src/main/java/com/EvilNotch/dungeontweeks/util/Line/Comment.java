package com.EvilNotch.dungeontweeks.util.Line;

public class Comment {
    public int lineIndex = -1;
    public String comment = null;
    public LineBase nearestLine = null;
    public boolean isAttactched = false;
    public char start = '#';
    
    public Comment(String comment)
    {
        this(-1,comment);
    }
    public Comment(String comment,char c)
    {
        this(-1,comment,false,c);
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
        this.start = c;
        this.lineIndex = index;
        this.comment = comment.substring(comment.indexOf(c)+1, comment.length() );
        this.isAttactched = attatched;
    }
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Comment))
            return false;
        Comment c = (Comment)obj;
        boolean line = false;
        if(this.nearestLine != null)
            line = this.nearestLine.equals(c.nearestLine);
        else
            line = c.nearestLine == null;
        return this.lineIndex == c.lineIndex && this.comment.equals(c.comment) && line && this.start == c.start && this.isAttactched == c.isAttactched;
    }
    
    @Override
    public String toString()
    {
        return "" + this.start + this.comment + ":" + this.lineIndex;
    }

}
