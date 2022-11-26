import java.io.FileWriter;
import java.util.*;

import java.io.File;
import java.io.IOException;

class One_word{
    Word type;
    String c;
    ValueType valueType;
    public One_word(){
        super();
    }
    public One_word(Word type, String c){
        this.c=c;
        this.type=type;
    }
    public void setC(String c) {
        this.c = c;
    }
    public void setValueType(ValueType v){
        this.valueType = v;
    }
    public Word getType() {
        return type;
    }
    public ValueType getValueType(){
        return this.valueType;
    }
    public void print(){
        Printer.print(this.type.name()+ " "+c);
    }
}
class TempResult {
    static int index = 0;
    static public String handleTempResult(String num) {//数组元素
        String topString = getNewTemp();
        Printer.intoMid(topString+"="+num);
        return topString;
    }
    static String getNum(String num) {
        try {
            Integer.parseInt(num);
            return Integer.parseInt(num)+"";
        } catch(Exception e) {
            return num;
        }
    }
    static int getRealNum(String num) {
        return Integer.parseInt(num);
    }
    static boolean isNum(String num) {
            try {
                Integer.parseInt(num);
                return true;
            } catch(Exception e){
                return false;
            }
    }
    static public void handleDef(Word word,String num1,String num2) {
            if(word==Word.CONSTTK) Printer.intoMid("const int "+num1+"="+num2);
            else Printer.intoMid("var int "+num1+"="+num2);
    }
    static public void handleDef(String num) {
        Printer.intoMid("var int "+num);
    }
    static public void handleArray1Def(String ide,String level1,String num) {
        num = num.replace("{","");
        num = num.replace("}","");
        num = num.replace(" ","");
        String[] strings = num.split(",");
        int l1 = Integer.parseInt(level1);
        Printer.intoMid("var arr int "+ide+"["+level1+"]");
        for(int i=0;i<l1;i++){
            Printer.intoMid(ide+"["+i+"]="+strings[i]);
        }
    }
    static public void handleArray1Def(String ide,String level1) {
        Printer.intoMid("var arr int "+ide+"["+level1+"]");
    }
    static public void handleArray2Def(String ide, String level1,String level2,String num) {
        Printer.intoMid("var arr int "+ide+"["+level1+"]["+level2+"]");
        num = num.replace("{","");
        num = num.replace("}","");
        num = num.replace(" ","");
        String[] strings = num.split(",");
        int l1 = Integer.parseInt(level1);
        int l2 = Integer.parseInt(level2);
        for(int i=0;i<l1;i++){
            for(int j=0;j<l2;j++){
                Printer.intoMid(ide+"["+i+"]"+"["+j+"]"+"="+strings[i*l2+j]);
            }
        }
    }
    static public void handleArray2Def(String ide, String level1,String level2) {
        Printer.intoMid("var arr int "+ide+"["+level1+"]["+level2+"]");
    }
    static public void handleTempResult(String num1,String num2) {
        num2 = getNum(num2);
        Printer.intoMid(num1+"="+num2);
    }
    //ConstExp直接计算出结果
    static public String handleTempResult(String num1,String num2,Operation op) {
        if(isNum(num1)&&isNum(num2)) {
            if(op == Operation.MUL)
                return (getRealNum(num1)*getRealNum(num2))+"";
            else if(op==Operation.DIV)
                return (getRealNum(num1)/getRealNum(num2))+"";
            else if(op==Operation.ADD)
                return (getRealNum(num1)+getRealNum(num2))+"";
            else if(op==Operation.MINUS)
                return (getRealNum(num1)-getRealNum(num2))+"";
            else if(op==Operation.MOD)
                return (getRealNum(num1)%getRealNum(num2))+"";
            else if(op==Operation.BEQ){
                if(getRealNum(num1)==getRealNum(num2))return "1";
                else return "0";
            }
            else if(op==Operation.BGE){
                if(getRealNum(num1)>=getRealNum(num2))return "1";
                else return "0";
            }
            else if(op==Operation.BGT){
                if(getRealNum(num1)>getRealNum(num2))return "1";
                else return "0";
            }
            else if(op==Operation.BLE){
                if(getRealNum(num1)<=getRealNum(num2))return "1";
                else return "0";
            }
            else if(op==Operation.BLT){
                if(getRealNum(num1)<getRealNum(num2))return "1";
                else return "0";
            }
            else if(op==Operation.BNE){
                if(getRealNum(num1)!=getRealNum(num2))return "1";
                else return "0";
            }
            return "0";
        }else {
            String topString = getNewTemp();
            if(op == Operation.MUL)
                Printer.intoMid(topString +"="+num1+"*"+num2);
            else if(op==Operation.DIV)
                Printer.intoMid(topString +"="+num1+"/"+num2);
            else if(op==Operation.ADD)
                Printer.intoMid(topString +"="+num1+"+"+num2);
            else if(op==Operation.MINUS)
                Printer.intoMid(topString +"="+num1+"-"+num2);
            else if(op==Operation.MOD)
                Printer.intoMid(topString +"="+num1+"%"+num2);
            else if(op==Operation.BEQ)
                Printer.intoMid(topString +"="+num1+"=="+num2);
            else if(op==Operation.BGE)
                Printer.intoMid(topString+"="+num1+">="+num2);
            else if(op==Operation.BGT)
                Printer.intoMid(topString+"="+num1+">"+num2);
            else if(op==Operation.BLE)
                Printer.intoMid(topString+"="+num1+"<="+num2);
            else if(op==Operation.BLT)
                Printer.intoMid(topString+"="+num1+"<"+num2);
            else if(op==Operation.BNE)
                Printer.intoMid(topString+"="+num1+"!="+num2);
            return topString;
        }
    }

    static public String getNewTemp() {
        index++;
        return "MID_TEMP"+index;
    }
}
class Printer{
    static File file = new File("output.txt");
    static boolean firstTime = true;
    static boolean firstMips = true;
    static boolean firstMid = true;
    static File Mips = new File("mips.txt");
    static File midCode = new File("20373901_余瑾璐_优化前中间代码.txt");
    public static void intoMid(String s) {
        try {
            FileWriter writer;
            if(firstMid){
                writer = new FileWriter(midCode);
                firstMid = false;
            }
            else {
                writer = new FileWriter(midCode, true);
            }
            // 有一个tab在
            writer.write(s+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void compile(String s) {
        try {
            FileWriter writer;
            if(firstMips){
                writer = new FileWriter(Mips);
                firstMips = false;
            }
            else {
                writer = new FileWriter(Mips, true);
            }
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer.write(s+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print(String s){
        try {
            FileWriter writer;
            if(firstTime){
                writer = new FileWriter(file);
                firstTime = false;
            }
            else {
                writer = new FileWriter(file, true);
            }
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer.write(s+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
public class WordHanlder {
    private  WordHanlder(){}
    private static WordHanlder wordHanlder = new WordHanlder();
    static Map<String, Word> charMap = new LinkedHashMap<>();
    static Map<String, Word> stringMap = new  LinkedHashMap<>();


    static {
        charMap.put("!=", Word.NEQ);
        charMap.put("!", Word.NOT);
        charMap.put("&&", Word.AND);
        charMap.put("||", Word.OR);
        charMap.put("+", Word.PLUS);
        charMap.put("-", Word.MINU);
        charMap.put("*", Word.MULT);
        charMap.put("/", Word.DIV);
        charMap.put("%", Word.MOD);
        charMap.put("<=", Word.LEQ);
        charMap.put("<", Word.LSS);
        charMap.put(">=", Word.GEQ);
        charMap.put(">", Word.GRE);
        charMap.put("==", Word.EQL);
        charMap.put(",", Word.COMMA);
        charMap.put("(", Word.LPARENT);
        charMap.put(")", Word.RPARENT);
        charMap.put("[",Word.LBRACK);
        charMap.put("]", Word.RBRACK);
        charMap.put("{",Word.LBRACE);
        charMap.put("}", Word.RBRACE);
        charMap.put("=", Word.ASSIGN);
        charMap.put(";", Word.SEMICN);

        stringMap.put("main", Word.MAINTK);
        stringMap.put("const", Word.CONSTTK);
        stringMap.put("int", Word.INTTK);
        stringMap.put("break", Word.BREAKTK);
        stringMap.put("continue", Word.CONTINUETK);
        stringMap.put("if", Word.IFTK);
        stringMap.put("else", Word.ELSETK);
        stringMap.put("void", Word.VOIDTK);
        stringMap.put("while", Word.WHILETK);
        stringMap.put("getint", Word.GETINTTK);
        stringMap.put("printf",Word.PRINTFTK);
        stringMap.put("return", Word.RETURNTK);
    }
    public static WordHanlder getInstance(){
        return wordHanlder;
    }
    public static void getTypes(String words) {
        while(words.length()>0){
            words = getType(words);
        }
    }
    public static String getType(String words) {
        boolean flag = false;
        char c = words.charAt(0);
        if(c=='\"'){
            //print(words,Word.STRCON);
            SentenceHandler.getInstance().addWord(new One_word(Word.STRCON,words));
            return "";
        }
        if(isNumber(c)){
            int j;
            for(j=0;j<words.length();j++){
                if(!isNumber(words.charAt(j)))
                    break;
            }
            //print(words.substring(0,j), Word.INTCON);
            SentenceHandler.getInstance().addWord(new One_word(Word.INTCON, words.substring(0,j)));
            return words.substring(j);
        }
        for(String key:charMap.keySet()){
            Word type = charMap.get(key);
            for (int index = 0; index < key.length(); index++) {
                if(index >= words.length()){
                    break;
                }
                if (words.charAt(index) != key.charAt(index)) {
                    break;
                }
                if (index == key.length() - 1) {
                    if(index != words.length()-1){
                        //print(words.substring(0,index+1),type);
                        SentenceHandler.getInstance().addWord(new One_word(type,words.substring(0,index+1)));
                        return words.substring(index+1);
                    } else {
                        //print(words,type);
                        SentenceHandler.getInstance().addWord(new One_word(type,words));
                        return "";
                    }
                }
            }
        }
        Iterator iter = stringMap.entrySet().iterator();
        while (iter.hasNext() && !flag) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            Word type = (Word) entry.getValue();
            for (int index = 0; index < key.length(); index++) {
                if(index >= words.length()){
                    break;
                }
                if (words.charAt(index) != key.charAt(index)) {
                    break;
                }
                if (index == key.length() - 1) {
                    //这里后面应该要改，还要判断非法字符
                    if(index != words.length()-1 && isAlpha(words.charAt(index+1))){
                        flag = true;
                        break;
                    } else {
                        if(index != words.length()-1){
                            //print(words.substring(0,index+1),type);
                            SentenceHandler.getInstance().addWord(new One_word(type,words.substring(0,index+1)));
                            return words.substring(index+1);
                        } else {
                            //print(words,type);
                            SentenceHandler.getInstance().addWord(new One_word(type,words));
                            return "";
                        }
                    }
                }
            }
        }
        if(isAlpha(c)){
            int j;
            for(j=0;j<words.length();j++){
                if(!isAlpha(words.charAt(j))){
                    break;
                }
            }
            //print(words.substring(0,j), Word.IDENFR);
            SentenceHandler.getInstance().addWord(new One_word(Word.IDENFR,words.substring(0,j)));
            return words.substring(j);
        }
        else {
            System.out.println(words);
            System.out.println("something went wrong");
            return "";
        }
    }
    public static boolean isNumber(char c) {
        if(c>='0'&&c<='9'){
            return true;
        }else {
            return false;
        }
    }

    public static boolean isAlpha(char c) {
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        }
        else if(c>='0'&&c<='9'){
            return true;
        }
        else if (c=='_'){
            return true;
        }
        else {
            return false;
        }
    }

}
