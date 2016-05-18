package youmo.slook;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tanch on 2016/1/18.
 */
public class StringHelper {

    @Nullable
    public static String MidString(String source, String start, String end)
    {
        if (start.isEmpty()||end.isEmpty()||source.isEmpty())
            return null;
        int x=source.indexOf(start)+start.length();
        if (x==-1)
            return null;
        int y=source.indexOf(end,x);
        if (y==-1)
            return null;
        return source.substring(x,y);
    }

    @Nullable
    public static String MidString(String source, String Regex)
    {
        if (source.isEmpty()||Regex.isEmpty())
            return null;
        Pattern p = Pattern.compile(Regex);
        Matcher m = p.matcher(source);
        if (m.find())
            return m.group(1);
        return null;
    }

    @Nullable
    public static List<String> MidListString(String source, String Regex)
    {
        return MidListString(source,Regex,1);
    }

    @Nullable
    public static ArrayList<String> MidListString(String source, String Regex,int i)
    {
        ArrayList<String> ret=new ArrayList<String>();
        Pattern p = Pattern.compile(Regex);
        Matcher m = p.matcher(source);
        while (m.find()) {
            ret.add(m.group(i));
        }
        return ret;
    }

    public static ArrayList<String> MidListString(String source, String start,String end)
    {
        if (start.isEmpty()||end.isEmpty()||source.isEmpty())
            return null;
        ArrayList<String> ret=new ArrayList<String>();
        boolean b=true;
        int x,y;
        while (b)
        {
            x=source.indexOf(start);
            y=source.indexOf(end,x+start.length());
            if (x<0||y<0)
            {
                b=false;
                continue;
            }
            ret.add(source.substring(x,y));
            source=source.substring(y+1,source.length());
        }
        return ret;
    }
}
