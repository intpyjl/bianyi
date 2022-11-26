import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SentenceHandler {
    static boolean bigOpen = false;
    static boolean quoteOpen = false;
    private static SentenceHandler sentenceHandler;
    ArrayList<One_word> wordList = new ArrayList<>();

    private SentenceHandler() {
        super();
    }

    public static SentenceHandler getInstance(){
        if(sentenceHandler==null){
            sentenceHandler = new SentenceHandler();
        }
        return sentenceHandler;
    }

    public void prehandle(String line){
        while(line.length()>0){
            if(bigOpen){
                int endIndex=line.indexOf("*/");
                if(endIndex>=0){
                    bigOpen=false;
                    if(endIndex+2<line.length())
                        line=line.substring(endIndex+2);
                    else break;
                }else {
                    break;
                }
            }
            if(quoteOpen){
                quoteOpen=false;
                int startIndex=line.indexOf("\"");
                if(startIndex>=0){
                    getWords(new String[] { "\""+line.substring(0,startIndex)+"\""});
                    if(startIndex+1<line.length()) {
                        line = line.substring(startIndex + 1);
                    }else
                        break;
                }else System.out.println("there is something wrong");
            }
            int bigIndex = line.indexOf("/*");
            int smallIndex = line.indexOf("//");
            int quoteIndex = line.indexOf("\"");
            if((bigIndex<smallIndex||smallIndex==-1)&&(bigIndex<quoteIndex||quoteIndex==-1)&&bigIndex!=-1){
                bigOpen=true;
                getWords(line.substring(0,bigIndex).split("\\s+"));
                if(bigIndex+2<line.length()){
                    line= line.substring(bigIndex+2);
                }else break;
            }else if((quoteIndex<smallIndex||smallIndex==-1)&&(quoteIndex<bigIndex||bigIndex==-1)&&quoteIndex!=-1){
                quoteOpen=true;
                getWords(line.substring(0,quoteIndex).split("\\s+"));
                if(quoteIndex+1<line.length()){
                    line= line.substring(quoteIndex+1);
                }else break;
            }else if((smallIndex<bigIndex||bigIndex==-1)&&(smallIndex<quoteIndex||quoteIndex==-1)&&smallIndex!=-1){
                getWords(line.substring(0,smallIndex).split("\\s+"));
                break;
            }else {
                getWords(line.split("\\s+"));
                break;
            }
        }

    }

    public void getWords(String[] words){
        WordHanlder wordHanlder = WordHanlder.getInstance();
        for (int j = 0; j < words.length; j++){
            if(words[j].length()>0){
                wordHanlder.getTypes(words[j]);
            }
        }
    }
    public void addWord(One_word one_word) {
        this.wordList.add(one_word);

    }
    public void checkGrammar(){
        CompUnit compUnit = new CompUnit();
        boolean correct=compUnit.check(this.wordList);
        if(!correct){
            System.out.println("Grammar is Wrong!");
        }
    }
}
