import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//FuncDef需跟VarDecl区分
class FuncDef extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        int index = 0;
        int tmpindex=0;
        List rankList = new ArrayList<>();
        One_word one_word;
        one_word = arrayList.get(index);
        if (one_word.getType() == Word.INTTK) {
            index++;
            one_word.print();
            Printer.print("<FuncType>");
        } else if(one_word.getType()==Word.VOIDTK){
            index++;
            one_word.print();
            Printer.print("<FuncType>");
        } else {
            return -1;
        }
        rankList.add(Word.IDENFR);
        rankList.add(Word.LPARENT);
        tmpindex = hasRank(rankList,arrayList.subList(index, arrayList.size()));
        if(tmpindex<0)return -1;
        Printer.intoMid(one_word.c+" "+arrayList.get(index).c+"()");
        index+=tmpindex;
        one_word = arrayList.get(index);
        if(one_word.getType()!=Word.RPARENT){
            Element funcFParam = new FuncFParam();
            tmpindex=funcFParam.checkSimilar(arrayList.subList(index, arrayList.size()));
            if(tmpindex<0){
                return -1;
            }
            index+=tmpindex;
            one_word = arrayList.get(index);
            while(one_word.getType()!=Word.RPARENT){
                if(one_word.getType()== Word.COMMA){
                    one_word.print();
                    index++;
                }else {
                    return -1;
                }
                tmpindex=funcFParam.checkSimilar(arrayList.subList(index, arrayList.size()));
                if(tmpindex<0){
                    return -1;
                }
                index+=tmpindex;
                one_word = arrayList.get(index);
            }
            Printer.print("<FuncFParams>");
        }
        one_word.print();
        index++;
        Element block = new Block();
        tmpindex = block.checkSimilar(arrayList.subList(index, arrayList.size()));
        if(tmpindex<0){
            return -1;
        } else {
            Printer.print("<FuncDef>");
            return index+tmpindex;
        }
    }

}
class MainFuncDef extends Element{
    //int main 无二义性，main作为关键词不能被使用
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        int index = 0;
        One_word one_word;
        List rankList = new ArrayList<>();
        Printer.intoMid("int main()");
        rankList.add(Word.INTTK);
        rankList.add(Word.MAINTK);
        rankList.add(Word.LPARENT);
        rankList.add(Word.RPARENT);
        rankList.add(new Block());
        index=hasRank(rankList,arrayList);
        if(index<0)return -1;
        Printer.print("<MainFuncDef>");
        return index;
    }
}
class FuncFParam extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        int index = 0;
        int tmpindex=0;
        One_word one_word;
        List rankList = new ArrayList<>();
        rankList.add(Word.INTTK);
        rankList.add(Word.IDENFR);
        index=hasRank(rankList,arrayList);
        String iden = arrayList.get(index-1).c;
        if(index<0)return -1;
        int level=0;
        String level2="";
        one_word = arrayList.get(index);
        if(one_word.getType()==Word.LBRACK){
            one_word.print();
            index++;
            level++;
            one_word = arrayList.get(index);
            if(one_word.getType()==Word.RBRACK){
                one_word.print();
                index++;
                one_word = arrayList.get(index);
                while(one_word.getType()==Word.LBRACK){
                    level++;
                    one_word.print();
                    index++;
                    Element constExp = new ConstExp();
                    tmpindex = constExp.checkSimilar(arrayList.subList(index, arrayList.size()));
                    if(index<0){
                        return -1;
                    }
                    level2 = constExp.topObject;
                    index+=tmpindex;
                    one_word = arrayList.get(index);
                    if(one_word.getType()==Word.RBRACK){
                        one_word.print();
                        index++;
                        one_word = arrayList.get(index);
                    }else {
                        return -1;
                    }
                }
            }else {
                return -1;
            }
        }
        if(level==0)
            Printer.intoMid("para "+"int "+iden);
        else if(level==1)
            Printer.intoMid("para "+"arr "+iden+"[]");
        else if(level==2)
            Printer.intoMid("para "+"arr "+iden+"[]["+level2+"]");
        Printer.print("<FuncFParam>");
        return index;
    }
}

class FuncRParams extends Element {
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        return check(arrayList,false);
    }

    public int check(List<One_word> arrayList, boolean ifConst) {
        int index = 0;
        int tmpindex =0;
        Element exp = new Exp();
        List<String> params = new LinkedList<String>();
        tmpindex= exp.check((List<One_word>) arrayList.subList(index, arrayList.size()),ifConst);
        params.add(exp.topObject);
        //Printer.intoMid("push "+exp.topObject);
        if(tmpindex<0){
            return -1;
        }
        index+=tmpindex;
        One_word one_word;
        one_word = arrayList.get(index);
        if (one_word.getType() == Word.COMMA) {
            while (one_word.getType() == Word.COMMA) {
                one_word.print();
                index++;
                tmpindex= exp.check((List<One_word>) arrayList.subList(index, arrayList.size()),ifConst);
                params.add(exp.topObject);
                //Printer.intoMid("push "+exp.topObject);
                if(tmpindex<0){
                    return -1;
                }
                index+=tmpindex;
                one_word = arrayList.get(index);
            }
        }
        for(int paramsIndex=0;paramsIndex<params.size();paramsIndex++)
            Printer.intoMid("push "+params.get(paramsIndex));
        Printer.print("<FuncRParams>");
        return index;
    }
}