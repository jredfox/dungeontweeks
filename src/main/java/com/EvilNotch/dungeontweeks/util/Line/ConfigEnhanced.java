package com.EvilNotch.dungeontweeks.util.Line;

import java.io.File;
import java.util.ArrayList;

public class ConfigEnhanced extends ConfigBase{
	
	public ConfigEnhanced(File file)
    {
        this(file,new ArrayList<Comment>() );
    }
    public ConfigEnhanced(File file, ArrayList<Comment> initComments)
    {
        this(file,initComments,"");
    }
    public ConfigEnhanced(File file, ArrayList<Comment> initComments, String header)
    {
        this(file,initComments,header,'#');
    }
    public ConfigEnhanced(File file, ArrayList<Comment> initComments, String header,char commentStart)
    {
        this(file,initComments,header,commentStart,'<','>','/',true,':','"');
    }

	public ConfigEnhanced(File file, ArrayList<Comment> initComments, String header, char commentStart, char lhwrap,char rhwrap, char hslash, boolean comments, char lsep, char lquote) {
		super(file, initComments, header, commentStart, lhwrap, rhwrap, hslash, comments, lsep, lquote);
	}
	
	@Override
	protected ILine getLine(String str){
		str = this.removeComments(str);
		return new LineDynamicLogic(str,this.lineSeperator,this.lineQuote,this.commentStart);
	}

}
