import java.util.ArrayList;
import java.util.List;
enum Operation {
    ADD,MUL,MINUS,DIV,MOD,
    BNE,BEQ,BGT,BLT,BLE,BGE,
    OR,AND
}
public class Exp extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList){
        return check(arrayList, false);
    }
    @Override
    public int check(List<One_word> arrayList, boolean ifConst) {
        int index = 0;
        Element addExp = new AddExp();
        index = addExp.check(arrayList,ifConst);
        topObject=addExp.topObject;
        if(index<0){
            return -1;
        }else {
            Printer.print("<Exp>");
            return index;
        }
    }
}
class ConstExp extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList){
        Element addExp = new AddExp();
        int index = addExp.check(arrayList,true);
        super.topObject= addExp.topObject;
        if(index<0){
            return -1;
        }
        Printer.print("<ConstExp>");
        return index;
    }
}
class ConstInitVal extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList){
        int index = 0;
        int tmpindex=0;
        One_word one_word;
        one_word = arrayList.get(index);
        if(one_word.getType()==Word.LBRACE){
            super.topObject = "{";
            one_word.print();
            index++;
            one_word = arrayList.get(index);
            if(one_word.getType()!=Word.RBRACE){
                Element element = new ConstInitVal();
                tmpindex = element.checkSimilar(arrayList.subList(index, arrayList.size()));
                if(tmpindex<0){
                    return -1;
                }
                super.topObject = super.topObject+element.topObject;
                index+=tmpindex;
                one_word = arrayList.get(index);
                while(one_word.getType()==Word.COMMA){
                    super.topObject = super.topObject+",";
                    one_word.print();
                    index++;
                    tmpindex = element.checkSimilar(arrayList.subList(index, arrayList.size()));
                    if(tmpindex<0){
                        return -1;
                    }
                    super.topObject = super.topObject+element.topObject;
                    index+=tmpindex;
                    one_word = arrayList.get(index);
                }
            }
            super.topObject = super.topObject+"}";
            if(one_word.getType()!= Word.RBRACE){
                return -1;
            }else {
                one_word.print();
                index++;
                Printer.print("<ConstInitVal>");
                return index;
            }
        }
        else {
            Element constExp = new ConstExp();
            tmpindex = constExp.checkSimilar(arrayList.subList(index, arrayList.size()));
            super.topObject = constExp.topObject;
            if(tmpindex<0){
                return -1;
            }
            index+=tmpindex;
            Printer.print("<ConstInitVal>");
            return index;
        }
    }
}
class InitVal extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList){
        int index = 0;
        int tmpindex=0;
        One_word one_word;
        one_word = arrayList.get(index);
        if(one_word.getType()==Word.LBRACE){
            super.topObject="{";
            one_word.print();
            index++;
            one_word = arrayList.get(index);
            if(one_word.getType()!=Word.RBRACE){
                Element element = new InitVal();
                tmpindex = element.checkSimilar(arrayList.subList(index, arrayList.size()));
                if(tmpindex<0){
                    return -1;
                }
                index+=tmpindex;
                super.topObject = super.topObject+element.topObject;
                one_word = arrayList.get(index);
                while(one_word.getType()==Word.COMMA){
                    super.topObject=super.topObject+",";
                    one_word.print();
                    index++;
                    tmpindex = element.checkSimilar(arrayList.subList(index, arrayList.size()));
                    if(tmpindex<0){
                        return -1;
                    }
                    super.topObject=super.topObject+element.topObject;
                    index+=tmpindex;
                    one_word = arrayList.get(index);
                }
            }
            if(one_word.getType()!= Word.RBRACE){
                return -1;
            }else {
                super.topObject=super.topObject+"}";
                one_word.print();
                index++;
                Printer.print("<InitVal>");
                return index;
            }
        }
        else {
            Element exp = new Exp();
            tmpindex = exp.checkSimilar(arrayList.subList(index, arrayList.size()));
            topObject = exp.topObject;
            if(tmpindex<0){
                return -1;
            }
            index+=tmpindex;
            Printer.print("<InitVal>");
            return index;
        }
    }
}
class LVal extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList){
        return check(arrayList,false);
    }
    public int check(List<One_word> arrayList, boolean ifConst){
        int index = 0;
        int tmpindex=0;
        List rankList;
        One_word one_word;
        WordValue wordValue = null;
        one_word = arrayList.get(index);
        int level1= -1;
        int level2 = -1;
        if(one_word.getType()==Word.IDENFR){
            if(ifConst&&One_word.words.get(one_word.c).getValueType()!=ValueType.Const){
                Printer.print("Not const");
                return -1;
            }else if(ifConst)wordValue = ConstTable.getValue(one_word.c);
            one_word.print();
            index++;
        }else {
            return -1;
        }
        super.topObject = one_word.c;
        while(arrayList.get(index).getType()==Word.LBRACK){
            rankList = new ArrayList<>();
            rankList.add(Word.LBRACK);
            Element element = new Exp();
            rankList.add(element);
            rankList.add(Word.RBRACK);
            tmpindex=hasRank(rankList,arrayList.subList(index, arrayList.size()),ifConst);
            if(tmpindex<0)return -1;
            if(!ifConst){
                if(element.topObject.contains("[")){
                    element.topObject = TempResult.handleTempResult(element.topObject);
                }
                super.topObject = super.topObject+"["+element.topObject+"]";
            }
            else {
                if(level1<0)level1=Integer.parseInt(element.topObject);
                else level2=Integer.parseInt(element.topObject);
            }
            index+=tmpindex;
        }if(ifConst){
            if(level1<0)topObject=wordValue.intValue+"";
            else if(level2>=0)topObject=wordValue.arrayValues[wordValue.level2*level1+level2]+"";
            else topObject = wordValue.arrayValues[level1]+"";
        }
        Printer.print("<LVal>");
        return index;
    }
}
class PrimaryExp extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList){
        return check(arrayList,false);
    }
    public int check(List<One_word> arrayList, boolean ifConst){
        int index = 0;
        int tmpindex=0;
        List rankList = new ArrayList<>();
        One_word one_word;
        one_word = arrayList.get(index);
        if(one_word.getType()== Word.INTCON){
            one_word.print();
            Printer.print("<Number>");
            Printer.print("<PrimaryExp>");
            super.topObject = one_word.c;
            return index+1;
        }
        else if(arrayList.get(0).getType()==Word.LPARENT){
            rankList.add(Word.LPARENT);
            Element exp = new Exp();
            rankList.add(exp);
            rankList.add(Word.RPARENT);
            index=hasRank(rankList,arrayList,ifConst);
            if(index<0) return -1;
            Printer.print("<PrimaryExp>");
            super.topObject = exp.topObject;
            return index;
        }
        else {
            Element lVal = new LVal();
            tmpindex =  lVal.check(arrayList.subList(index, arrayList.size()),ifConst);
            super.topObject = lVal.topObject;
            if(tmpindex<0){
                return -1;
            }else{
                Printer.print("<PrimaryExp>");
                index+=tmpindex;
                return index;
            }
        }
    }
}
class UnaryExp extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        return check(arrayList,false);
    }
    public int check(List<One_word> arrayList, boolean ifConst){
        int index = 0;
        int tmpindex=0;
        One_word one_word;
        one_word = arrayList.get(index);
        if(one_word.getType()==Word.IDENFR&&arrayList.get(index+1).getType()==Word.LPARENT){
            One_word function=one_word;
            one_word.print();
            index++;
            one_word = arrayList.get(index);
            one_word.print();
            index++;
            one_word = arrayList.get(index);
            if(one_word.getType()!=Word.RPARENT){
                FuncRParams funcRParams = new FuncRParams();
                tmpindex = funcRParams.check(arrayList.subList(index, arrayList.size()),ifConst);
                if(tmpindex<0){
                    return -1;
                }
                index+=tmpindex;
                one_word = arrayList.get(index);
                if(one_word.getType()!=Word.RPARENT){
                    return -1;
                }
            }
            Printer.intoMid("call "+function.c);
            super.topObject=TempResult.handleTempResult("RET");
            one_word.print();
            Printer.print("<UnaryExp>");
            return index+1;
        }
        else if(one_word.getType()==Word.PLUS||one_word.getType()== Word.MINU||one_word.getType()==Word.NOT){
            One_word op = one_word;
            one_word.print();
            index++;
            Printer.print("<UnaryOp>");
            Element unaryExp = new UnaryExp();
            tmpindex = unaryExp.check(arrayList.subList(index, arrayList.size()),ifConst);
            //tmpindex =unaryExp.checkSimilar(arrayList.subList(index, arrayList.size()));
            if(tmpindex<0){
                return -1;
            }
            super.topObject=unaryExp.topObject;
            if(op.getType()==Word.MINU) {
                if (!ifConst) super.topObject = TempResult.handleTempResult("0", unaryExp.topObject, Operation.MINUS);
                else if(unaryExp.topObject.charAt(0)=='-')super.topObject = unaryExp.topObject.substring(1);
                else super.topObject = "-" + unaryExp.topObject;
            }
            if(op.getType()==Word.NOT)
                super.topObject=TempResult.handleTempResult(unaryExp.topObject,"0",Operation.BEQ);
            index+=tmpindex;
            Printer.print("<UnaryExp>");
            return index;
        }
        else {
            Element primaryExp = new PrimaryExp();
            tmpindex = primaryExp.check(arrayList.subList(index, arrayList.size()),ifConst);
            topObject = primaryExp.topObject;
            if(tmpindex<0){
                return -1;
            }else {
                Printer.print("<UnaryExp>");
                index+=tmpindex;
                return index;
            }
        }
    }
}

class MulExp extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        return check(arrayList,false);
    }
    public int check(List<One_word> arrayList, boolean ifConst){
        List list =new ArrayList<Word>();
        list.add(Word.MULT);list.add(Word.DIV);
        list.add(Word.MOD);
        return innerCircle(new UnaryExp(),list,arrayList,"MulExp",ifConst);
    }
}
class AddExp extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        return check(arrayList, false);
    }
    @Override
    public int check(List<One_word> arrayList, boolean ifConst){
        List list =new ArrayList<Word>();
        list.add(Word.MINU);list.add(Word.PLUS);
        Element element = new MulExp();
        return innerCircle(element,list,arrayList,"AddExp",ifConst);
    }
}
class RelExp extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        List list =new ArrayList<Word>();
        list.add(Word.LEQ);list.add(Word.LSS);
        list.add(Word.GEQ);list.add(Word.GRE);
        return innerCircle(new AddExp(),list,arrayList,"RelExp");
    }
}
class EqExp extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        List list =new ArrayList<Word>();
        list.add(Word.EQL);list.add(Word.NEQ);
        return innerCircle(new RelExp(),list,arrayList,"EqExp");
    }
}
class LAndExp extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        List list =new ArrayList<Word>();
        list.add(Word.AND);
        return innerCircle(new EqExp(),list,arrayList,"LAndExp");
    }
}
class LOrExp extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        List list =new ArrayList<Word>();
        list.add(Word.OR);
        return innerCircle(new LAndExp(),list,arrayList,"LOrExp");
    }
}
class Cond extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        Element lorExp = new LOrExp();
        int index = lorExp.checkSimilar(arrayList);
        if(index<0)return -1;
        Printer.print("<Cond>");
        return index;
    }
}

