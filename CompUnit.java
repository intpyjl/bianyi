import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

enum BlockType {
    LOOP,IF
}

class LabelControl {
    static private int ifNumber=0;
    static public int whileNumber=0;
    BlockType blockType;
    static LabelControl nowLabel=null;
    LabelControl father;
    LabelControl rightLabel=null;
    String startLabel="";//对于条件语句，start是条件判断的label开始，对于if/while语句，start是block开始
    String endLabel="";//对于条件语句，end是不满足的下一句;对于if/wihle语句，end是block结束
    String nextLabel="";//对于if/while语句，next是block内部（Cond满足）;对于条件语句,next是满足的下一句;
    int number;
    static int conditionNumber=0;
    public void setDefault(Operation op){
        if(op==Operation.OR){
            Printer.intoMid(this.endLabel+":");
            Printer.intoMid("j "+father.endLabel);
        }else {
            Printer.intoMid(this.nextLabel+":");
            Printer.intoMid("j "+father.nextLabel);
        }
    }
    public LabelControl(){
        super();
    }
    public void setNext(Operation op){
        if(op==Operation.OR){
            Printer.intoMid(this.endLabel+":");
            Printer.intoMid("j "+"MID_COND"+(conditionNumber+1));
        }else {
            Printer.intoMid(this.nextLabel+":");
            Printer.intoMid("j "+"MID_COND"+(conditionNumber+1));
        }
    }
    public static void release(){
        if(nowLabel.father!=null)
            nowLabel=nowLabel.father;
        else nowLabel=null;
    }
    public LabelControl(Operation op) {
        super();
        father = nowLabel;
        conditionNumber++;
        this.number = conditionNumber;
        if(op==Operation.OR) {
            this.nextLabel = father.nextLabel;
            this.endLabel="MID_ENDCOND"+conditionNumber;
        }
        else if(op==Operation.AND){
            this.endLabel=father.endLabel;
            this.nextLabel="MID_ENDCOND"+conditionNumber;
        }
        this.startLabel="MID_COND"+conditionNumber;
        Printer.intoMid(this.startLabel+":");
        nowLabel = this;
    }
    public LabelControl(BlockType blockType){
        super();
        this.father = null;
        if(blockType == BlockType.IF){
            ifNumber++;
            this.blockType = BlockType.IF;
            this.startLabel = "MID_STARTIF"+ifNumber;
            this.nextLabel = "MID_IF"+ifNumber;
            this.endLabel = "MID_ENDIF"+ifNumber;
        }else {
            whileNumber++;
            this.blockType = BlockType.LOOP;
            this.startLabel = "MID_STARTWHILE"+whileNumber;
            this.nextLabel = "MID_WHILE"+whileNumber;
            this.endLabel = "MID_ENDWHILE"+whileNumber;
        }
        Printer.intoMid(this.startLabel+":");
        if(nowLabel!=null)father = nowLabel;
        nowLabel=this;
    }
}


public class CompUnit{
    public CompUnit(){

    }
    public boolean check(List<One_word> arrayList) {
        int first_index=0, second_index=0, third_index=0;
        while(arrayList.size() > 0){
            Element decl = new Decl();
            if(first_index<0&&second_index<0&&third_index<0){
                return false;
            }
            if(arrayList.get(1).getType()==Word.MAINTK){
                Element mainFuncDef = new MainFuncDef();
                third_index = mainFuncDef.checkSimilar(arrayList);
                if(third_index>0){
                    arrayList = arrayList.subList(third_index,arrayList.size());
                    continue;//这里好像必须是最后
                }else System.exit(3);

            }else if(arrayList.get(2).getType()==Word.LPARENT){
                Element funcDef = new FuncDef();
                second_index = funcDef.checkSimilar(arrayList);
                if(second_index>0){
                    arrayList = arrayList.subList(second_index,arrayList.size());
                    continue;
                }else System.exit(2);
            } else{
                first_index =decl.checkSimilar(arrayList);
                //三种遍历不应有重复部分
                if(first_index>0){
                    arrayList = arrayList.subList(first_index,arrayList.size());
                    continue;
                }else System.exit(1);
            }
        }
        Printer.print("<CompUnit>");
        return true;
    }

}
class Block extends Element{
    int constIndex;
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        Printer.intoMid("BlockBegin ");
        constIndex = ConstTable.charStack.size()-1;//不包含
        int index = 0;
        int tmpindex=0;
        One_word one_word;
        one_word = arrayList.get(index);
        if(one_word.getType()==Word.LBRACE){
            one_word.print();
            index++;
        }else {
            return -1;
        }
        one_word = arrayList.get(index);
        Element blockItem = new BlockItem();
        blockItem.loopNumber = super.loopNumber;
        while(one_word.getType()!=Word.RBRACE){
            tmpindex = blockItem.checkSimilar(arrayList.subList(index,arrayList.size()));
            if(tmpindex<0){
                return -1;
            }
            index+=tmpindex;
            one_word = arrayList.get(index);
        }
        one_word.print();
        Printer.print("<Block>");
        Printer.intoMid("BlockEnd ");
        ConstTable.free(constIndex);
        return index+1;
    }
}
class BlockItem extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        int index = 0;
        int tmpindex=0;
        One_word one_word;
        one_word = arrayList.get(index);
        if(one_word.getType()==Word.CONSTTK||one_word.getType()==Word.INTTK){
            Element decl = new Decl();
            tmpindex = decl.checkSimilar(arrayList.subList(index,arrayList.size()));
            if(tmpindex<0){
                return -1;
            }
            index+=tmpindex;
        }else {
            Element stmt = new Stmt();
            stmt.loopNumber = super.loopNumber;
            tmpindex = stmt.checkSimilar(arrayList.subList(index,arrayList.size()));
            if(tmpindex<0){
                return -1;
            }
            index+=tmpindex;
        }
        return index;
    }
}
class Stmt extends Element{
    public Stmt(){
        super();
    }
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        int index = 0;
        int tmpindex=0;
        List rankList = new ArrayList<>();
        One_word one_word;
        one_word = arrayList.get(index);
        //条件式，跳转
        if(one_word.getType()==Word.IFTK){
            LabelControl labelControl = new LabelControl(BlockType.IF);//新增一个if

            rankList.add(Word.IFTK);
            rankList.add(Word.LPARENT);
            Element element = new Cond();
            rankList.add(new Cond());
            rankList.add(Word.RPARENT);
            index = hasRank(rankList,arrayList);
            Printer.intoMid(labelControl.nextLabel+":");
            Element stmt = new Stmt();
            stmt.loopNumber = super.loopNumber;
            tmpindex=stmt.checkSimilar(arrayList.subList(index,arrayList.size()));
            Printer.intoMid("j MID_ENDIFELSE"+labelControl.startLabel.substring(11));
            if(tmpindex<0)return -1;
            else index+=tmpindex;
            one_word = arrayList.get(index);
            Printer.intoMid(labelControl.endLabel+":");
            while(one_word.getType()==Word.ELSETK){
                one_word.print();
                index++;
                tmpindex = stmt.checkSimilar(arrayList.subList(index,arrayList.size()));
                if(tmpindex<0)return -1;
                index+=tmpindex;
                one_word = arrayList.get(index);
            }
            Printer.intoMid("MID_ENDIFELSE"+labelControl.startLabel.substring(11)+":");
            Printer.print("<Stmt>");
            return index;
        }
        else if(one_word.getType()==Word.WHILETK){
            LabelControl labelControl = new LabelControl(BlockType.LOOP);
            super.loopNumber = LabelControl.whileNumber;
            rankList = new ArrayList<>();
            rankList.add(Word.WHILETK);
            rankList.add(Word.LPARENT);
            rankList.add(new Cond());
            rankList.add(Word.RPARENT);
            index = hasRank(rankList,arrayList);if(index<0)return -1;
            Printer.intoMid(labelControl.nextLabel+":");
            Element stmt = new Stmt();
            stmt.loopNumber = LabelControl.whileNumber;
            tmpindex=stmt.checkSimilar(arrayList.subList(index,arrayList.size()));
            Printer.intoMid("j "+labelControl.startLabel);
            if(tmpindex<0)return -1;
            else index+=tmpindex;
            Printer.intoMid(labelControl.endLabel+":");
            Printer.print("<Stmt>");
            return index;
        }
        else if(one_word.getType()==Word.BREAKTK){
            one_word.print();
            index++;
            one_word = arrayList.get(index);
            if(one_word.getType()==Word.SEMICN){
                one_word.print();
                index++;
                Printer.print("<Stmt>");
                Printer.intoMid("j MID_ENDWHILE"+super.loopNumber);
                return index;
            }else {
                return -1;
            }
        }
        else if(one_word.getType()==Word.CONTINUETK){
            one_word.print();
            index++;
            one_word = arrayList.get(index);
            if(one_word.getType()==Word.SEMICN){
                one_word.print();
                index++;
                Printer.print("<Stmt>");
                Printer.intoMid("j "+"MID_STARTWHILE"+super.loopNumber);
                return index;
            }else {
                return -1;
            }
        }
        else if(one_word.getType()==Word.PRINTFTK){
            rankList = new ArrayList<>();
            rankList.add(Word.PRINTFTK);
            rankList.add(Word.LPARENT);
            rankList.add(Word.STRCON);
            index =hasRank(rankList,arrayList);
            if(index<0)return -1;
            //获得输出整数的多少
            String string = arrayList.get(index-1).c;
            string = string.substring(1,string.length()-1);
            List<String> outputList = new LinkedList<String>();
            int i=string.length()-string.replace("%d", "").length();
            int number = i/2;//这里是待给出的数据
            int printIndex=0;
            boolean ifStart = (string.length()>0&&string.charAt(0)=='%');
            String[] strings = string.split("%d");
            one_word = arrayList.get(index);
            if(!ifStart) {
                outputList.add("print "+"\""+strings[0]+"\"");
                ConstStr.addConStr("\""+strings[0]+"\"");
            }
            printIndex++;
            while(one_word.getType()==Word.COMMA){
                one_word.print();
                index++;
                Element exp = new Exp();
                tmpindex=exp.checkSimilar(arrayList.subList(index,arrayList.size()));
                outputList.add("print "+exp.topObject);
                if(printIndex<strings.length){
                    ConstStr.addConStr("\""+strings[printIndex]+"\"");
                    outputList.add("print "+"\""+strings[printIndex++]+"\"");
                }
                if(tmpindex<0)return -1;
                index+=tmpindex;
                one_word = arrayList.get(index);
            }
            if(printIndex<strings.length){
                ConstStr.addConStr("\""+strings[printIndex]+"\"");
                outputList.add("print "+"\""+strings[printIndex++]+"\"");
            }
            rankList = new ArrayList<>();
            rankList.add(Word.RPARENT);
            rankList.add(Word.SEMICN);
            tmpindex =hasRank(rankList,arrayList.subList(index,arrayList.size()));
            if(tmpindex<0)return -1;
            index+=tmpindex;
            for(tmpindex=0;tmpindex<outputList.size();tmpindex++)Printer.intoMid(outputList.get(tmpindex));
            Printer.print("<Stmt>");
            return index;
        }
        else if(one_word.getType()==Word.RETURNTK){
            index++;
            one_word.print();
            one_word = arrayList.get(index);
            if(one_word.getType()!=Word.SEMICN){
                Element exp = new Exp();
                tmpindex=exp.checkSimilar(arrayList.subList(index,arrayList.size()));
                if(tmpindex<0)return -1;
                index+=tmpindex;
                one_word = arrayList.get(index);
                if(one_word.getType()!=Word.SEMICN){
                    return -1;
                }
                Printer.intoMid("ret "+exp.topObject);
            }else Printer.intoMid("ret");
            one_word.print();
            index++;
            Printer.print("<Stmt>");
            return index;
        }
        else if(one_word.getType()==Word.LBRACE){
            Element block = new Block();
            block.loopNumber = super.loopNumber;
            tmpindex=block.checkSimilar(arrayList.subList(index,arrayList.size()));
            if(tmpindex<0)return -1;
            index+=tmpindex;
            Printer.print("<Stmt>");
            return index;
        }
        else{
            int j=0;
            index = 0;
            for(;j<arrayList.size();j++){
                if(arrayList.get(j).getType()==Word.ASSIGN){
                    break;
                }
                if(arrayList.get(j).getType()==Word.SEMICN)
                    break;
                if(j==arrayList.size()-1)return -1;
            }
            if(arrayList.get(j).getType()==Word.ASSIGN&&arrayList.get(j+1).getType()!=Word.GETINTTK){
                Element Lval = new LVal();
                tmpindex=Lval.checkSimilar(arrayList.subList(index,arrayList.size()));
                if(tmpindex<0)return -1;
                index+=tmpindex;
                one_word = arrayList.get(index);
                if(one_word.getType()!=Word.ASSIGN){
                    return -1;
                }else {
                    index++;
                    one_word.print();
                }
                Element exp = new Exp();
                tmpindex=exp.checkSimilar(arrayList.subList(index,arrayList.size()));
                if(tmpindex<0)return -1;
                index+=tmpindex;
                one_word = arrayList.get(index);
                TempResult.handleTempResult(Lval.topObject,exp.topObject);
                if(one_word.getType()!=Word.SEMICN){
                    return -1;
                }else {
                    one_word.print();
                    index++;
                    Printer.print("<Stmt>");
                    return index;
                }

            }
            else if(arrayList.get(j).getType()==Word.ASSIGN&&arrayList.get(j+1).getType()==Word.GETINTTK){
                Element Lval = new LVal();
                tmpindex=Lval.checkSimilar(arrayList.subList(index,arrayList.size()));
                if(tmpindex<0)return -1;
                index+=tmpindex;
                one_word = arrayList.get(index);
                if(one_word.getType()!=Word.ASSIGN){
                    return -1;
                }else {
                    index++;
                    one_word.print();
                }
                one_word = arrayList.get(index);
                if(one_word.getType()==Word.GETINTTK){
                    one_word.print();
                    index++;
                }else {
                    return -1;
                }
                one_word = arrayList.get(index);
                if(one_word.getType()==Word.LPARENT){
                    one_word.print();
                    index++;
                }else {
                    return -1;
                }
                one_word = arrayList.get(index);
                if(one_word.getType()==Word.RPARENT){
                    one_word.print();
                    index++;
                }else {
                    return -1;
                }
                one_word = arrayList.get(index);
                if(one_word.getType()==Word.SEMICN){
                    one_word.print();
                    index++;
                }else {
                    return -1;
                }
                Printer.intoMid("read "+Lval.topObject);
                Printer.print("<Stmt>");
                return index;
            }
            else {
                one_word = arrayList.get(index);
                if(one_word.getType()==Word.SEMICN){
                    one_word.print();
                    Printer.print("<Stmt>");
                    return index+1;
                }
                Element exp = new Exp();
                tmpindex=exp.checkSimilar(arrayList.subList(index,arrayList.size()));
                if(tmpindex<0)return -1;
                index+=tmpindex;
                one_word = arrayList.get(index);
                if(one_word.getType()!=Word.SEMICN){
                    return -1;
                }else {
                    one_word.print();
                    index++;
                    Printer.print("<Stmt>");
                    return index;
                }
            }
        }
    }
}
