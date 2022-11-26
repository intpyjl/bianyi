import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//按总体出现一次来识别，识别完毕即返回
class Decl extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        //两种遍历无重复部分
        Element constDecl = new ConstDecl();
        int first_index = constDecl.checkSimilar(arrayList);
        if(first_index>0){
            return first_index;
        }
        Element varDecl = new VarDecl();
        int second_index = varDecl.checkSimilar(arrayList);
        if(second_index>0){
            return second_index;
        }
        return -1;
    }
}
//不需跟任何区分,可以直接打印
class ConstDecl extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        int index = 0;
        int tmpindex=0;

        List rankList=new ArrayList<>();
        rankList.add(Word.CONSTTK);
        rankList.add(Word.INTTK);
        Element constDef = new ConstDef();
        rankList.add(constDef);
        index=this.hasRank(rankList,arrayList);
        if(index<0)return -1;
        while(Word.COMMA ==arrayList.get(index).getType()){
            rankList = new ArrayList<>();
            rankList.add(Word.COMMA);
            rankList.add(constDef);
            tmpindex = hasRank(rankList,arrayList.subList(index, arrayList.size()));
            if(tmpindex<0){
                return -1;
            }
            index+=tmpindex;
        }
        rankList = new ArrayList<>();
        rankList.add(Word.SEMICN);
        tmpindex = hasRank(rankList,arrayList.subList(index, arrayList.size()));
        if(tmpindex<0){
            Printer.print("something is wrong");
            return -1;
        }
        Printer.print("<ConstDecl>");
        return index + 1;
    }
}
//需跟mainfunc区分
class VarDecl extends Element{
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        int index = 0;
        int tmpindex=0;
        List rankList=new ArrayList<>();
        rankList.add(Word.INTTK);
        Element varDef = new VarDef();
        rankList.add(varDef);
        index = hasRank(rankList,arrayList);
        if(index<0){
            return -1;
        }

        while(arrayList.get(index).getType() == Word.COMMA){
            rankList = new ArrayList<>();
            rankList.add(Word.COMMA);
            rankList.add(varDef);
            tmpindex = hasRank(rankList,arrayList.subList(index, arrayList.size()));
            if(tmpindex<0){
                return -1;
            }
            index+=tmpindex;
        }
        rankList = new ArrayList<>();
        rankList.add(Word.SEMICN);
        tmpindex = hasRank(rankList,arrayList.subList(index, arrayList.size()));
        if(tmpindex<0){
            Printer.print("something is wrong");
            return -1;
        }
            Printer.print("<VarDecl>");
            return index + 1;

    }
}
//目前无需和任何区分
class ConstDef extends Element {
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        int index = 0;
        int tmpindex =0;
        One_word one_word;
        List rankList = new ArrayList<>();
        one_word = arrayList.get(index);
        String name = one_word.c;
        if(Word.IDENFR == one_word.getType()){
            index++;
            one_word.print();
            ExeStack.wordStack.add(one_word);
            one_word.setValueType(ValueType.Const);
            ExeStack.charStack.add(one_word.c);
        }else{
            return -1;
        }
        one_word = arrayList.get(index);
        List<String> levels = new LinkedList<String>();
        while(Word.LBRACK == one_word.getType()){
            Element constExp = new ConstExp();
            rankList=new ArrayList();
            rankList.add(Word.LBRACK);
            rankList.add(constExp);
            rankList.add(Word.RBRACK);
            tmpindex=hasRank(rankList,arrayList.subList(index, arrayList.size()));
            if(tmpindex<0){
                return -1;
            }
            levels.add(constExp.topObject);
            index +=tmpindex;
            one_word = arrayList.get(index);
        }
        rankList=new ArrayList<>();
        rankList.add(Word.ASSIGN);
        Element constInitial = new ConstInitVal();
        rankList.add(constInitial);
        tmpindex = hasRank(rankList,arrayList.subList(index, arrayList.size()));
        if(levels.size()==0)
            ConstTable.addConstInt(name,constInitial.topObject);//TempResult.handleDef(Word.CONSTTK,name,constInitial.topObject);
        else if(levels.size()==1)
            ConstTable.addConstArray1(name,levels.get(0),constInitial.topObject);//TempResult.handleArray1Def(Word.CONSTTK,name,levels.get(0),constInitial.topObject);
        else if(levels.size()==2)
            ConstTable.addConstArray2(name,levels.get(0),levels.get(1),constInitial.topObject);//TempResult.handleArray2Def(Word.CONSTTK,name,levels.get(0),levels.get(1),constInitial.topObject);
        if(tmpindex<0)return -1;
        Printer.print("<ConstDef>");
        index+=tmpindex;
        return index;
    }
}

class VarDef extends Element {
    @Override
    public int checkSimilar(List<One_word> arrayList) {
        int index = 0;
        int tmpindex =0;
        List rankList = new ArrayList<>();
        One_word one_word;
        one_word = arrayList.get(index);
        String name = one_word.c;
        if(Word.IDENFR == one_word.getType()){
            index++;
            one_word.print();
            one_word.setValueType(ValueType.Var);
            ExeStack.charStack.add(one_word.c);
            ExeStack.wordStack.add(one_word);
        }else{
            return -1;
        }
        one_word = arrayList.get(index);
        List<String> levels = new LinkedList<String>();
        while(Word.LBRACK == one_word.getType()){
            Element constExp = new ConstExp();
            rankList=new ArrayList<>();
            rankList.add(Word.LBRACK);
            rankList.add(constExp);
            rankList.add(Word.RBRACK);
            tmpindex=hasRank(rankList,arrayList.subList(index, arrayList.size()));
            if(tmpindex<0){
                return -1;
            }
            levels.add(constExp.topObject);
            index +=tmpindex;
            one_word = arrayList.get(index);
        }
        if(Word.ASSIGN!=one_word.getType()){
            Printer.print("<VarDef>");
            if(levels.size()==0)
                TempResult.handleDef(name);
            else if(levels.size()==1)
                TempResult.handleArray1Def(name,levels.get(0));
            else if(levels.size()==2)
                TempResult.handleArray2Def(name,levels.get(0),levels.get(1));

            return index;
        }else {
            one_word.print();
            index++;
        }
        Element initVal = new InitVal();
        tmpindex = initVal.checkSimilar(arrayList.subList(index, arrayList.size()));
        if(levels.size()==0)
            TempResult.handleDef(Word.INTTK,name,initVal.topObject);
        else if(levels.size()==1)
            TempResult.handleArray1Def(name,levels.get(0),initVal.topObject);
        else if(levels.size()==2)
            TempResult.handleArray2Def(name,levels.get(0),levels.get(1),initVal.topObject);

        if(tmpindex<0){
            return -1;
        }
        else {
            Printer.print("<VarDef>");
            index+=tmpindex;
            return index;
        }
    }
}