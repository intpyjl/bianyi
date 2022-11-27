import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.lang.*;
public class Compiler {

    public static void main(String[] args)
    {
        String fileName = "testfile.txt";
        String midName = "20373901_余瑾璐_优化前中间代码.txt";
        String midName2 = "20373901_余瑾璐_优化后中间代码.txt";
        File file = new File(fileName);

        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                //处理注释
                SentenceHandler.getInstance().prehandle(line);
            }
            SentenceHandler.getInstance().checkGrammar();
        }catch (Exception e){
            System.out.println(e);
        }
        while (Modifier.isChanged){
            //优化扫描
            try{
                FileReader fr = new FileReader(midName);
                BufferedReader br = new BufferedReader(fr);
                String line;
                Modifier.startNewRound();
                while((line = br.readLine()) != null){
                    //处理注释
                    Modifier.handle(line);
                }
                Function.allFs = new LinkedList<Function>();
            }catch (Exception e){
                System.out.println(Modifier.lineNumber);
                System.out.println(e);
            }
        }
        try{
            FileReader fr = new FileReader(midName);
            BufferedReader br = new BufferedReader(fr);
            String line;
            Modifier.startNewRound();
            while((line = br.readLine()) != null){
                //处理注释
                Modifier.deleteTrash(line);
            }
            Function.allFs = new LinkedList<Function>();
        }catch (Exception e){
            System.out.println(Modifier.lineNumber);
            System.out.println(e);
        }
        Modifier.times=-1;
        try{
            FileReader fr = new FileReader(midName2);
            BufferedReader br = new BufferedReader(fr);
            String line;
            Printer.compile(".data");
            ConstStr.printAll();
            while((line = br.readLine()) != null){
                //处理注释
                CreateMIPS.getInstance().handle(line);
            }
            CreateMIPS.getInstance().handleLast();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
enum Word {
    IDENFR, INTCON, STRCON, MAINTK, CONSTTK, INTTK, BREAKTK,
    CONTINUETK, IFTK, ELSETK, NOT, AND, OR, WHILETK, GETINTTK, PRINTFTK,
    RETURNTK, PLUS, MINU, VOIDTK, MULT, DIV, MOD, LSS, LEQ, GRE, GEQ, EQL, NEQ,
    COMMA, LPARENT, RPARENT, LBRACK, RBRACK, LBRACE, RBRACE, ASSIGN, SEMICN
}
class ExeStack {
    static int number=-1;
    static List<One_word> wordStack = new LinkedList<One_word>();
    static List<String> charStack = new LinkedList<String>();
    public static ValueType getWord(String c) {
        int index = charStack.lastIndexOf(c);
        if(index<0)return null;
        else return wordStack.get(index).getValueType();
    }
    public static void free(int index){
        charStack  = charStack.subList(0,index+1);
        wordStack = wordStack.subList(0,index+1);
    }
}
class ConstTable{
    static List<String> charStack = new LinkedList<String>();
    static List<WordValue> valueList = new LinkedList<WordValue>();
    public static void free(int index){
        charStack  = charStack.subList(0,index+1);
        valueList = valueList.subList(0,index+1);
    }
    public static void addConstInt(String name,String value){
        charStack.add(name);
        valueList.add(new WordValue(Integer.parseInt(value)));
        Printer.intoMid("const int "+name+"="+value);
    }

    public static WordValue getValue(String name){
        int index = charStack.lastIndexOf(name);
        if(index<0) return null;
        return valueList.get(index);
    }
    public static void  addConstArray1(String name,String level,String value){
        charStack.add(name);
        int level1=Integer.parseInt(level);
        WordValue wordValue=new WordValue(level1,value);
        valueList.add(wordValue);
        Printer.intoMid("const arr int "+name+"["+level1+"]");
        for(int i=0;i<level1;i++){
            Printer.intoMid(name+"["+i+"]="+wordValue.arrayValues[i]);
        }
    }
    public static void  addConstArray2(String name,String level1,String level2,String value){
        charStack.add(name);
        int l1=Integer.parseInt(level1);
        int l2 = Integer.parseInt(level2);
        WordValue wordValue=new WordValue(l1,l2,value);
        valueList.add(wordValue);
        Printer.intoMid("const arr int "+name+"["+level1+"]"+"["+level2+"]");
        for(int i=0;i<l1;i++){
            for(int j=0;j<l2;j++){
                Printer.intoMid(name+"["+i+"]["+j+"]="+wordValue.arrayValues[i*l2+j]);
            }
        }
    }
}
enum ValueType{
    Const, Var,Temp
}
enum DataStructure {
    Array1,Array2,Int
}