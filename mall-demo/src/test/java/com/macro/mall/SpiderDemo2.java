package cn.hncu.net.spider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class SpiderDemo2 {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            URL mulu=new URL("http://www.biqudu.com/43_43821/");
            BufferedReader br=new BufferedReader(new InputStreamReader(mulu.openStream()));
            BufferedWriter bw=new BufferedWriter(new FileWriter("haoshuai.txt"));

            String mainContextReg="<dt>《文章》正文</dt>";
            //<dd> <a href="/43_43821/2520338.html">第一章 沙漠中的彼岸花</a></dd>
            String urlReg="<a[\\s\\S]*>";

            Pattern mainContextPattern=Pattern.compile(mainContextReg);
            Pattern urlPattern=Pattern.compile(urlReg);

            String str=null;
            boolean boo=false;	//是否是正文
            while((str=br.readLine())!=null){
                if(!boo){
                    Matcher mainContextMatcher=mainContextPattern.matcher(str);
                    if(mainContextMatcher.find()){
                        boo=true;
                        System.out.println(str.substring(str.indexOf("<dt>")+4, str.lastIndexOf("</dt>")));
                    }
                }else{
                    Matcher urlmatcher=urlPattern.matcher(str);
                    if(urlmatcher.find()){
//						System.out.println(urlmatcher.group());

                        //<a href="/43_43821/2520338.html"></a></dd>
                        String str1=urlmatcher.group();
                        String url="http://www.biqudu.com"+str1.substring(str1.indexOf("<a")+9,str1.lastIndexOf("\">"));
//						System.out.println(str1);
//						System.out.println(url);
                        zj(url,bw);
                    }
                }

            }
            br.close();
            bw.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zj(String url,BufferedWriter bw){
        try {
            URL mainUrl=new URL(url);
            BufferedReader br=new BufferedReader(new InputStreamReader(mainUrl.openStream()));

            String titleReg="<h1>[\\s\\S]*</h1>";
            String contextReg="<div id=\"content\">[\\s\\S]*</div>";
            String newLine="<br/>";
            String spac=" ";

            Pattern titlePattern=Pattern.compile(titleReg);
            Pattern contextPattern=Pattern.compile(contextReg);
            Pattern newLinePattern=Pattern.compile(newLine);
            Pattern spacPattern=Pattern.compile(spac);

            String str=null;
            String title=null;
            String context=null;
            while((str=br.readLine())!=null){
                Matcher m = titlePattern.matcher(str);
                if(m.find()){
                    title=m.group();
                    bw.write(title.substring(4, title.lastIndexOf("<"))+"\r\n");
                    bw.flush();
                    System.out.println(title.substring(4, title.lastIndexOf("<")));		//标题
                }

                m=contextPattern.matcher(str);
                if(m.find()){
                    context=m.group();			//正文--因为这里的正文是一行,所以需要继续在拆
                    char cs[]=context.toCharArray();
                    for(int i=0;i<cs.length;i++){
                        boolean boo=false;
                        if(cs[i]=='<'){
                            if(i+5<cs.length){
                                String str1=new String(cs,i,5);
                                Matcher m1=newLinePattern.matcher(str1);
                                if(m1.find()){
                                    System.out.println();
                                    bw.write("\r\n");
                                    i+=4;
                                    boo=true;
                                }
                            }
                        }
                        if(cs[i]=='&'){
                            if(i+6<cs.length){
                                String str1=new String(cs, i, 6);
                                Matcher m1=spacPattern.matcher(str1);
                                if(m1.find()){
                                    System.out.print(" ");
                                    bw.write(" ");
                                    i+=5;
                                    boo=true;
                                }
                            }
                        }
                        String s=cs[i]+"";
                        if(s.getBytes().length==1){
                            continue;
                        }
                        if(cs[i]==' '){
                            continue;
                        }
                        if(!boo){
                            System.out.print(cs[i]);
                            bw.write(cs[i]);
                            bw.flush();
                        }
                    }
                }
            }
//			System.out.println("---------------");
//			System.out.println(title);
//			System.out.println(context);
            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}