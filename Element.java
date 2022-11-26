import java.util.ArrayList;
import java.util.List;

public class Element {
    //or关系式的成功返回地址及and关系式的失败返回地址
    String topObject;
    int loopNumber = 0;
    //返回参数匹配到的下一位
    public int checkSimilar(List<One_word> arrayList) {
        return -1;}
    public Element() {
        super();
    }
    public int check(List<One_word> arrayList, boolean ifConst){return -1;}
    public int hasRank(List rankList,List<One_word> wordList,boolean ifConst){
        int index =0;
        int tmpindex=0;
        One_word one_word;
        for(int i=0;i<rankList.size();i++){
            if(rankList.get(i) instanceof Word){
                one_word = wordList.get(index);
                if(one_word.getType()==rankList.get(i)){
                    one_word.print();
                    index++;
                }else {
                    return -1;
                }
            }else {
                tmpindex = ((Element)rankList.get(i)).check(wordList.subList(index,wordList.size()),ifConst);
                if(tmpindex<0)return -1;
                index+=tmpindex;
            }
        }
        return index;
    }
    public int hasRank(List rankList,List<One_word> wordList){
        int index =0;
        int tmpindex=0;
        One_word one_word;
        for(int i=0;i<rankList.size();i++){
            if(rankList.get(i) instanceof Word){
                one_word = wordList.get(index);
                if(one_word.getType()==rankList.get(i)){
                    one_word.print();
                    index++;
                }else {
                    return -1;
                }
            }else {
                tmpindex = ((Element)rankList.get(i)).checkSimilar(wordList.subList(index,wordList.size()));
                if(tmpindex<0)return -1;
                index+=tmpindex;
            }
        }
        return index;
    }
    static Operation getOp(One_word word) {
        if(word.getType()==Word.MINU)
            return Operation.MINUS;
        else if(word.getType()==Word.PLUS)
            return Operation.ADD;
        else if(word.getType()==Word.MULT)
            return Operation.MUL;
        else if(word.getType()==Word.DIV)
            return Operation.DIV;
        else if(word.getType()==Word.MOD)
            return Operation.MOD;
        else if(word.getType()==Word.LEQ)//<=
            return Operation.BLE;
        else if(word.getType()== Word.LSS)//<
            return Operation.BLT;
        else if(word.getType()==Word.GEQ)//>=
            return Operation.BGE;
        else if(word.getType()==Word.GRE)//>
            return Operation.BGT;
        else if(word.getType()==Word.EQL)
            return Operation.BEQ;
        else if(word.getType()==Word.NEQ)
            return Operation.BNE;
        else if(word.getType()==Word.OR)
            return Operation.OR;
        else if(word.getType()==Word.AND)
            return Operation.AND;
        else return null;
    }
    public int innerCircle(Element e, List<Word> words, List<One_word> wordList,String name){
        int index = 0;
        Operation op=null;
        LabelControl labelControl=null;
        if(name.equals("LOrExp")) op=Operation.OR;
        else if(name.equals("LAndExp"))op=Operation.AND;
        if(op==Operation.OR||op== Operation.AND) {
            labelControl= new LabelControl(op);
        }
        int tmpindex = e.checkSimilar(wordList.subList(index, wordList.size()));
        if(tmpindex<0){
            return -1;
        }
        if(labelControl!=null){
            Printer.intoMid("cmp "+e.topObject+",0");
            if(op==Operation.OR){
                Printer.intoMid("bne "+labelControl.nextLabel);
                //Printer.intoMid("j "+labelControl.endLabel);
            }

            else{
                Printer.intoMid("beq "+labelControl.endLabel);
                //Printer.intoMid("j "+labelControl.nextLabel);
            }

            this.topObject="1";
        }

        index = index + tmpindex;
        Printer.print("<"+name+">");
        One_word one_word;
        one_word = wordList.get(index);
        String object1 = e.topObject;
        while(words.contains(one_word.getType())){
            op=getOp(one_word);
            one_word.print();
            index++;
            if(op==Operation.OR||op==Operation.AND)
            {
                LabelControl.release();
                LabelControl oldLabel = labelControl;
                oldLabel.setNext(op);
                LabelControl.nowLabel=new LabelControl(op);
                labelControl = LabelControl.nowLabel;
            }
            tmpindex= e.checkSimilar(wordList.subList(index, wordList.size()));
            if(tmpindex<0){
                return -1;
            }
            String object2 = e.topObject;
            if(op!=Operation.OR&&op!=Operation.AND)
                object1 = TempResult.handleTempResult(object1,object2,op);
            if(labelControl!=null){
                Printer.intoMid("cmp "+e.topObject+",0");
                if(op==Operation.OR){
                    Printer.intoMid("bne "+labelControl.nextLabel);
                    //Printer.intoMid("j "+labelControl.endLabel);
                }

                else if(op==Operation.AND){
                    Printer.intoMid("beq "+labelControl.endLabel);
                    //Printer.intoMid("j "+labelControl.nextLabel);
                }

            }
            index = index + tmpindex;
            one_word = wordList.get(index);
            Printer.print("<"+name+">");
        }
        if(labelControl!=null){
            labelControl.setDefault(op);
            LabelControl.release();
        }
        else this.topObject = object1;
        return index;
    }
    private int CountConst(String o1,String o2,Operation op){
        int a=Integer.parseInt (o1);
        int b=Integer.parseInt (o2);
        if(op==Operation.MINUS)return a-b;
        if(op==Operation.ADD)return a+b;
        if(op==Operation.MUL)return a*b;
        if(op==Operation.MOD)return a%b;
        if(op==Operation.DIV)return a/b;
        if(op==Operation.OR){
            if(a>0||b>0)return 1;
            else return 0;
        }
        if(op==Operation.AND){
            if(a>0&&b>0)return 1;
            else return 0;
        }
        if(op==Operation.BLE){
            if(a<=b)return 1;
            else return 0;
        }
        if(op==Operation.BLT)
        {
            if(a<b)return 1;
            else return 0;
        }
        if(op==Operation.BGE)
        {
            if(a>=b)return 1;
            else return 0;
        }
        if(op==Operation.BGT)
        {
            if(a>b)return 1;
            else return 0;
        }
        if(op==Operation.BEQ)
        {
            if(a==b)return 1;
            else return 0;
        }
        if(op==Operation.BNE){
            if(a!=b)return 1;
            else return 0;
        }
        return 0;
    }
    public int innerCircle(Element e, List<Word> words, List<One_word> wordList,String name,boolean ifConst){
        int index = 0;
        int tmpindex = e.check(wordList.subList(index, wordList.size()),ifConst);
        if(tmpindex<0){
            return -1;
        }
        index = index + tmpindex;
        Printer.print("<"+name+">");
        One_word one_word;
        one_word = wordList.get(index);
        String object1 = e.topObject;
        while(words.contains(one_word.getType())){
            Operation op = getOp(one_word);
            one_word.print();
            index++;
            tmpindex= e.check(wordList.subList(index, wordList.size()),ifConst);
            if(tmpindex<0){
                return -1;
            }
            String object2 = e.topObject;
            if(!ifConst)
            object1 = TempResult.handleTempResult(object1,object2,op);
            else object1=CountConst(object1,object2,op)+"";
            index = index + tmpindex;
            one_word = wordList.get(index);
            Printer.print("<"+name+">");
        }
        topObject = object1;
        return index;
    }
}
