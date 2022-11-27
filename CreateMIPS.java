import java.util.*;

public class CreateMIPS {
    private boolean global=true;
    private Function nowf;
    private boolean ifCall=false;
    private BlockFlag blockFlag = new BlockFlag();
    private FuncParam funcParam;
    private static List<String> labels = new LinkedList<String>();
    private String compare;
    private CreateMIPS(){
    }
    static private CreateMIPS creater =null;
    static CreateMIPS getInstance(){
        if(creater==null)creater=new CreateMIPS();
        return creater;
    }
    public void handleLast(){
        for(int i=0;i<labels.size();i++){
            Printer.compile(labels.get(i));
        }
    }

    public void handleCal(String[] formula) {
        Operation op=null;
        String cal[];
        String output="";
        if(formula.length>1&&formula[1].equals("RET")) {
            Printer.compile("sw $v1, "+WordTable.getPosition(formula[0]));
            return;
        }
        if(formula.length>1)//意味着等式右边有内容
        {
            while(true) {
                cal = formula[1].split("\\+");
                if (cal.length > 1) {
                    op = Operation.ADD;
                    output += "add ";
                    break;
                }
                cal = formula[1].split("\\*");
                if (cal.length > 1) {
                    op = Operation.MUL;
                    output += "mul ";
                    break;
                }
                cal = formula[1].split("/");
                if (cal.length > 1) {
                    op = Operation.DIV;
                    output += "div ";
                    break;
                }
                cal = formula[1].split("%");
                if (cal.length > 1) {
                    op = Operation.MOD;
                    output += "div ";
                    break;
                }
                cal = formula[1].split(">");
                if (cal.length > 1) {
                    op = Operation.BGT;
                    output += "slt ";
                    break;
                }
                cal = formula[1].split("<");
                if (cal.length > 1) {
                    op = Operation.BLT;
                    output += "slt ";
                    break;
                }
                cal = formula[1].split("<=");
                if (cal.length > 1) {
                    op = Operation.BLE;
                    output += "slt ";
                    break;
                }
                cal = formula[1].split(">=");
                if (cal.length > 1) {
                    op = Operation.BGE;
                    output += "slt ";
                    break;
                }
                cal = formula[1].split("==");
                if (cal.length > 1) {
                    op = Operation.BEQ;
                    output += "beq ";
                    break;
                }
                cal = formula[1].split("!=");
                if (cal.length > 1) {
                    op = Operation.BNE;
                    output += "bne ";
                    break;
                }
                cal = formula[1].split("-");
                if (cal.length > 1) {
                    if(cal[0].length() == 0) {
                        String number = cal[1];
                        cal = new String[1];
                        cal[0] = number;
                    }
                    op = Operation.MINUS;
                    output += "sub ";
                    break;
                }
                break;
            }

        }else return ;
        int oc[] = {Register.getTempReg(),Register.getTempReg(),Register.getTempReg()};

        String oReg="$"+oc[0];
        String sReg1="$"+oc[1];
        String sReg2="$"+oc[2];
        if(Register.LoadImme(cal[0],sReg1)){
        } else {
            String p=WordTable.getPosition(cal[0]);
            Printer.compile("lw "+sReg1+", "+p);
        }
        if(cal.length==1&&op==Operation.MINUS) {
            Printer.compile("sub "+oReg+", $0, "+sReg1);
        } else if(op==null) {
            oReg = sReg1;
        }else {
            if(Register.LoadImme(cal[1],sReg2)){
            } else {
                String p=WordTable.getPosition(cal[1]);
                Printer.compile("lw "+sReg2+", "+p);
            }
            if(op==Operation.DIV) {
                Printer.compile("div "+sReg1+", "+sReg2);
                Printer.compile("mflo "+oReg);
            }else if(op==Operation.MOD) {
                Printer.compile("div "+sReg1+", "+sReg2);
                Printer.compile("mfhi "+oReg);
            }else if(op==Operation.BGT){
                Printer.compile(output+oReg+", "+sReg2+", "+sReg1);
            }else if(op==Operation.BGE){
                Printer.compile("addi "+sReg1+", "+sReg1+", "+1);
                Printer.compile(output+oReg+", "+sReg2+", "+sReg1);
            }else if(op==Operation.BLE){
                Printer.compile("addi "+sReg2+", "+sReg2+", "+1);
                Printer.compile(output+oReg+", "+sReg1+", "+sReg2);
            }else if(op==Operation.BEQ||op==Operation.BNE){
                Printer.compile("li "+oReg+", 0");
                Printer.compile(output+sReg1+", "+sReg2+", MIPS_LABEL_"+ labels.size());
                Printer.compile("MIPS_ENDLABEL"+labels.size()+":");
                String newLabel = "MIPS_LABEL_"+ labels.size()+":\n";
                newLabel = newLabel + "li "+oReg+", 1\n";
                newLabel = newLabel+"j MIPS_ENDLABEL"+labels.size()+"\n";
                labels.add(newLabel);
            }
            else {
                Printer.compile(output+oReg+", "+sReg1+", "+sReg2);
            }
        }
        String finalP = WordTable.getPosition(formula[0]);
        Printer.compile("sw "+oReg+", "+finalP);
        for(int index=0;index<3;index++)
            Register.free(oc[index]);
    }
    public void handleAssign(String[] formula) {
        Operation op=null;
        String cal[];
        String output="";
        if(formula.length>1&&formula[1].equals("RET")) {
            Printer.compile("sw $v1, "+WordTable.getPosition(formula[0]));
            return;
        }
        if(formula.length>1)//意味着等式右边有内容
        {
            while(true) {
                cal = formula[1].split("\\+");
                if (cal.length > 1) {
                    op = Operation.ADD;
                    output += "add ";
                    break;
                }
                cal = formula[1].split("\\*");
                if (cal.length > 1) {
                    op = Operation.MUL;
                    output += "mul ";
                    break;
                }
                cal = formula[1].split("/");
                if (cal.length > 1) {
                    op = Operation.DIV;
                    output += "div ";
                    break;
                }
                cal = formula[1].split("%");
                if (cal.length > 1) {
                    op = Operation.MOD;
                    output += "div ";
                    break;
                }
                cal = formula[1].split("<=");
                if (cal.length > 1) {
                    op = Operation.BLE;
                    output += "slt ";
                    break;
                }
                cal = formula[1].split(">=");
                if (cal.length > 1) {
                    op = Operation.BGE;
                    output += "slt ";
                    break;
                }
                cal = formula[1].split(">");
                if (cal.length > 1) {
                    op = Operation.BGT;
                    output += "slt ";
                    break;
                }
                cal = formula[1].split("<");
                if (cal.length > 1) {
                    op = Operation.BLT;
                    output += "slt ";
                    break;
                }
                cal = formula[1].split("==");
                if (cal.length > 1) {
                    op = Operation.BEQ;
                    output += "beq ";
                    break;
                }
                cal = formula[1].split("!=");
                if (cal.length > 1) {
                    op = Operation.BNE;
                    output += "bne ";
                    break;
                }
                cal = formula[1].split("-");
                if (cal.length > 1) {
                    if(cal[0].length() == 0) {
                        String number = cal[1];
                        cal = new String[1];
                        cal[0] = number;
                    }
                    op = Operation.MINUS;
                    output += "sub ";
                    break;
                }
                break;
            }

        }else return ;
        int oc[] = {Register.getTempReg(),Register.getTempReg(),Register.getTempReg()};
        String oReg="$"+oc[0];
        String sReg1="$"+oc[1];
        String sReg2="$"+oc[2];
        if(Register.LoadImme(cal[0],sReg1)){
        } else {
            String p=WordTable.getPrePosition(cal[0]);
            Printer.compile("lw "+sReg1+", "+p);
        }
        if(cal.length==1&&op==Operation.MINUS) {
            Printer.compile("sub "+oReg+", $0, "+sReg1);
        } else if(op==null) {
            oReg = sReg1;
        }else {
            if(Register.LoadImme(cal[1],sReg2)){

            } else {
                String p=WordTable.getPrePosition(cal[1]);
                Printer.compile("lw "+sReg2+", "+p);
            }
            if(op==Operation.DIV) {
                Printer.compile("div "+sReg1+", "+sReg2);
                Printer.compile("mflo "+oReg);
            }else if(op==Operation.MOD) {
                Printer.compile("div "+sReg1+", "+sReg2);
                Printer.compile("mfhi "+oReg);
            }else if(op==Operation.BGT){
                Printer.compile(output+oReg+", "+sReg2+", "+sReg1);
            }else if(op==Operation.BGE){
                Printer.compile("addi "+sReg1+", "+sReg1+", "+1);
                Printer.compile(output+oReg+", "+sReg2+", "+sReg1);
            }else if(op==Operation.BLE){
                Printer.compile("addi "+sReg2+", "+sReg2+", "+1);
                Printer.compile(output+oReg+", "+sReg1+", "+sReg2);
            }else if(op==Operation.BEQ||op==Operation.BNE){
                Printer.compile("li "+oReg+", 0");
                Printer.compile(output+sReg1+", "+sReg2+", MIPS_LABEL_"+ labels.size());
                Printer.compile("MIPS_ENDLABEL"+labels.size()+":");
                String newLabel = "MIPS_LABEL_"+ labels.size()+":\n";
                newLabel = newLabel + "li "+oReg+", 1\n";
                newLabel = newLabel+"j MIPS_ENDLABEL"+labels.size()+"\n";
                labels.add(newLabel);
            }
            else {
                Printer.compile(output+oReg+", "+sReg1+", "+sReg2);
            }
        }
        String finalP = WordTable.getPosition(formula[0]);
        Printer.compile("sw "+oReg+", "+finalP);
        for(int index=0;index<3;index++)
            Register.free(oc[index]);
    }
    public void handle(String line) {
        String[] strings = line.split(" ");
        //常量声明
        if(strings[0].equals("const"))
        {
            if(strings[1].equals("int")){
                int t=strings[2].indexOf("=");
                String[] keys= new String[2];
                if(t>=0){
                    keys[0]=strings[2].substring(0,t);
                    keys[1]=strings[2].substring(t+1);
                }
                if(global)
                    WordTable.addGlobalConstInt(keys[0]);
                else WordTable.addConstInt(keys[0]);
                handleAssign(keys);
            }else if(strings[1].equals("arr")){
                //strings[2]为'int'
                boolean p = false;
                if(strings[3].indexOf("[")==strings[3].lastIndexOf("["))p=true;
                strings[3] = strings[3].replace("["," ");
                strings[3] = strings[3].replace("]","");
                String[] values = strings[3].split(" ");
                if(p){
                   if(global)
                       WordTable.addGlobalConstArray1(values[0],Integer.parseInt(values[1]));
                   else WordTable.addConstArray1(values[0],Integer.parseInt(values[1]));
                }else {
                    if(global)
                        WordTable.addGlobalConstArray2(values[0],Integer.parseInt(values[1]),Integer.parseInt(values[2]));
                    else WordTable.addConstArray2(values[0],Integer.parseInt(values[1]),Integer.parseInt(values[2]));
                }
            }
        }
        else if(strings[0].equals("BlockBegin")){
            blockFlag = new BlockFlag(blockFlag);

        }else if(strings[0].equals("BlockEnd")){
            blockFlag = blockFlag.free();
        }
        //变量声明
        else if(strings[0].equals("var")) {
            if(strings[1].equals("int")){
                int t=strings[2].indexOf("=");
                String[] keys;
                if(t>=0){
                    keys= new String[2];
                    keys[0]=strings[2].substring(0,t);
                    keys[1]=strings[2].substring(t+1);
                }else {
                    keys=new String[1];
                    keys[0]=strings[2];
                }
                if(global)
                    WordTable.addGlobalVarInt(keys[0]);
                else
                    WordTable.addVartInt(keys[0]);
                handleAssign(keys);
            }else if(strings[1].equals("arr")){
                //strings[2]为'int'
                boolean p = false;
                if(strings[3].indexOf("[")==strings[3].lastIndexOf("["))p=true;
                strings[3] = strings[3].replace("["," ");
                strings[3] = strings[3].replace("]","");
                String[] values = strings[3].split(" ");
                if(p){
                    if(global)
                        WordTable.addGlobalVarArray1(values[0],Integer.parseInt(values[1]));
                    else WordTable.addVarArray1(values[0],Integer.parseInt(values[1]));
                }else {
                    if(global)
                        WordTable.addGlobalVarArray2(values[0],Integer.parseInt(values[1]),Integer.parseInt(values[2]));
                    else WordTable.addVarArray2(values[0],Integer.parseInt(values[1]),Integer.parseInt(values[2]));
                }
            }
        }
        //进入函数语句
        //需要加一个函数处理状态
        //函数记录临时变量的开始位置 √
        else if(strings[0].equals("ret")) {
            if(nowf!=null&&nowf.name.equals("main") ){
                Printer.compile("li $v0,10\n" +
                        "syscall");
                return ;
            }
            if(strings.length>1) {
                if(Register.LoadImme(strings[1],"$v1")){
                }else {
                    String p = WordTable.getPosition(strings[1]);
                    Printer.compile("lw " + "$v1," + p);
                }
            }
            Printer.compile("jr $ra");
        }
        else if(strings[0].equals("int") || strings[0].equals("void")){
            if(global){
                global=false;
                Printer.compile("j main");
            }
            if(nowf!=null) {
                Printer.compile("jr $ra");
                blockFlag = blockFlag.free();
                nowf = null;
            }
            String name=(strings[1].split("\\("))[0];
            Function function = new Function(name);
            if(strings[0].equals("int"))function.setReturnType(Word.INTTK);
            else function.setReturnType(Word.VOIDTK);
            nowf = function;
            blockFlag = new BlockFlag(blockFlag);
            Printer.compile(name+":");
        }
        else if(strings[0].equals("para")) {
            if(strings[1].equals("int")) {
                WordTable.addVartInt(strings[2]);

                int index = Register.getTempReg();
                //从栈中读取传参，并且作为局部变量保存
                Printer.compile("lw $"+index+", "+"0($sp)");
                Printer.compile("addi $sp,$sp,4");
                String p = WordTable.getPosition(strings[2]);
                Printer.compile("sw $"+index+", "+p);
                Register.free(index);
            }else if(strings[1].equals("arr")) {
                String[] arrs = strings[2].split("\\[");
                if(arrs.length==2)
                    WordTable.addArrAdd(arrs[0]);
                else if(arrs.length==3){
                    int Lindex = strings[2].lastIndexOf("[");
                    int Rindex = strings[2].lastIndexOf("]");
                    int array2 = Integer.parseInt(strings[2].substring(Lindex+1,Rindex));
                    WordTable.addArrAdd(arrs[0],array2);
                }

                int index = Register.getTempReg();
                //从栈中读取传参，并且作为局部变量保存
                Printer.compile("lw $"+index+", "+"0($sp)");
                Printer.compile("addi $sp,$sp,4");
                int Windex =WordTable.charStack.lastIndexOf(arrs[0]);
                String p = WordTable.wordTable.get(Windex).Pp;
                Printer.compile("sw $"+index+", "+p);//将地址存进去地址存放区
                Register.free(index);
            }
        }
        else if(strings[0].equals("push")) {
            if(!ifCall) {
                ifCall = true;
                funcParam = new FuncParam();
            }
            funcParam.addParams(strings[1]);
        }
        else if(strings[0].equals("call")) {
            if(!ifCall){
                ifCall = true;
                funcParam = new FuncParam();
            }
            boolean flag=nowf.addCall(strings[1]);
            if(flag) {
                funcParam.callFunction(nowf);
                Printer.compile("jal "+strings[1]);
                funcParam.reset(nowf);
            }else {
                funcParam.savecallFunction(nowf);
                Printer.compile("jal "+strings[1]);
                funcParam.savereset(nowf);
            }
            ifCall = false;
            funcParam = null;
        }
        //读
        else if(strings[0].equals("read")){
            if(WordTable.exist(strings[1])){
                String p = WordTable.getPosition(strings[1]);
                Printer.compile("li $v0,5");
                Printer.compile("syscall");
                Printer.compile("sw $v0 "+p);
            }
        }
        //写
        else if(strings[0].equals("print")) {
            String content = line.substring(6);
            if(content.charAt(0)=='\"')
            {
                Printer.compile("li $v0,4");
                Printer.compile("la $a0,"+ConstStr.getName(content));
                Printer.compile("syscall");
            }
            else if(Register.LoadImme(content,"$a0")){
                Printer.compile("li $v0,1");
                Printer.compile("syscall");
            }else {
                String p = WordTable.getPosition(content);
                Printer.compile("li $v0,1");
                Printer.compile("lw $a0,"+p);
                Printer.compile("syscall");
            }
        }
        //标签
        else if(strings[0].charAt(strings[0].length()-1)==':'){
            Printer.compile(line);
        }
        else if(strings[0].equals("cmp")){
            String[] number = strings[1].split(",");
            int o=Register.getTempReg();
            if(Register.LoadImme(number[0],"$"+o)){
            }else {
                String p=WordTable.getPosition(number[0]);
                Printer.compile("lw $"+o+", "+p);
            }
            compare = " $"+o+", ";
            Register.free(o);
        }
        else if(strings[0].equals("bne")||strings[0].equals("beq")){
           compare = strings[0]+compare+"$0, "+strings[1];
            Printer.compile(compare);
        }
        else if(strings[0].equals("j")){
            Printer.compile(line);
        }
        //非特殊字符，在最后，是赋值语句
        else {
            int t=strings[0].indexOf("=");
            String[] keys= new String[2];
            if(t>=0){
                keys[0]=strings[0].substring(0,t);
                keys[1]=strings[0].substring(t+1);
            }else {
                keys = new String[1];
                keys[0]=strings[0];
            }
            boolean p = WordTable.exist(keys[0]);
            if(!p){
                WordTable.addTempInt(keys[0]);
                handleAssign(keys);
            }else {
                handleCal(keys);
            }
        }
    }
}
class WordValue{
    int intValue;
    int level1;
    int level2;
    int arrayValues[];
    public WordValue(int value){
        super();
        this.intValue=value;
        this.level1 = 0;
        this.level2 = 0;
        this.arrayValues=null;
    }
    public WordValue(int level1,String value){
        super();
        value = value.substring(1,value.length()-1);
        value = value.replace(" ","");
        String[] strings = value.split(",");
        arrayValues = new int[level1];
        this.level1=level1;
        for(int i=0;i<level1&&i<strings.length;i++){
            arrayValues[i]=Integer.parseInt(strings[i]);
        }
    }
    public WordValue(int level1,int level2,String value){
        super();
        value = value.replace("{","");
        value = value.replace("}","");
        value = value.replace(" ","");
        String[] strings = value.split(",");
        arrayValues = new int[level1*level2];
        this.level1=level1;
        this.level2=level2;
        for(int i=0;i<level1*level2&&i<strings.length;i++){
            arrayValues[i]=Integer.parseInt(strings[i]);
        }
    }
}

class MIPSWord {
    String name;
    ValueType valueType;
    DataStructure dataStructure;
    WordValue wordValue;
    String position="";
    int startP;
    String Pp;
    Register reg;
    int array1=0;
    int array2=0;
    public MIPSWord(){}
    public MIPSWord(ValueType valueType,String name){
        super();
        this.name=name;
        this.dataStructure=DataStructure.Int;
        this.valueType=valueType;
        this.array1=1;
        this.array2=1;
    }
    public MIPSWord(ValueType valueType,String name,int size1){
        super();
        this.name=name;
        this.dataStructure=DataStructure.Array1;
        this.valueType=valueType;
        this.array1 = size1;
        this.array2 = 1;
    }
    public MIPSWord(ValueType valueType,String name,int size1,int size2){
        super();
        this.name=name;
        this.dataStructure=DataStructure.Array2;
        this.valueType=valueType;
        this.array1 = size1;
        this.array2 = size2;
    }
    public void setP(int startP,String reg){
        this.startP = startP;
        this.position = reg;
    }
}
class Register{
    //TODO:寄存器分配优化
    static Register regs[];
    static {
        regs = new Register[32];
        for (int index=0;index<32;index++)
            regs[index] = new Register();
    }
    boolean occupied=false;
    public static boolean LoadImme(String num,String reg) {
            try {
                Integer.parseInt(num);
                Printer.compile("li "+reg+", "+num);
                return true;
            } catch(Exception e){
                return false;
            }
    }
    public static boolean LoadImme(String num) {
        try {
            Integer.parseInt(num);
            return true;
        } catch(Exception e){
            return false;
        }
    }
    public static int getTempReg() {
        for(int index=8;index<=15;index++){
            if(!regs[index].occupied){
                regs[index].occupied=true;
                return index;
            }
        }
        if(regs[24].occupied==false){
            regs[24].occupied = true;
            return 24;
        }
        if(regs[25].occupied==false){
            regs[25].occupied = true;
            return 25;
        }
        return -1;
    }
    public static void free(int index){
        regs[index].occupied = false;
    }
}
class BlockFlag{
    int tableStart=0;
    private BlockFlag father;
    public BlockFlag(){
        father = null;
        this.tableStart = 0;
    }
    public BlockFlag(BlockFlag b){
        this.father = b;
        if(Modifier.times>=0)
            this.tableStart = Modifier.charStack.size();
        else
            this.tableStart = WordTable.wordTable.size();

    }
    public BlockFlag free(){
        if(Modifier.times>=0){
            Modifier.wordStack = Modifier.wordStack.subList(0,tableStart);
            Modifier.charStack = Modifier.charStack.subList(0,tableStart);
        }else {
            WordTable.wordTable = WordTable.wordTable.subList(0,tableStart);
            WordTable.charStack = WordTable.charStack.subList(0,tableStart);
        }

        return this.father;
    }
}
class WordTable{
    static List<String> charStack = new LinkedList<String>();
    static List<MIPSWord> wordTable = new LinkedList<MIPSWord>();
    static List<MIPSWord> tempTable = new LinkedList<MIPSWord>();
    static int global=0;
    static int temp=0;
    static public boolean exist(String name) {
        name = name.replace("["," ");
        name = name.replace("]","");
        String[] names = name.split(" ");
        int index = charStack.lastIndexOf(names[0]);
        if(index<0) return false;
        else return true;
    }
    static public String getPosition(String name) {
        //保存的地址是正确的时候，获取到某一个值的存储地址，不能用于获取指针->用指针的值进行了计算
        name = name.replace("["," ");
        name = name.replace("]","");
        String[] names = name.split(" ");
        int index = charStack.lastIndexOf(names[0]);
        if(index<0) return null;
        int sReg = Register.getTempReg();
        MIPSWord mipsWord = wordTable.get(index);
        if(names.length==1 && mipsWord.dataStructure==DataStructure.Int){
            Register.free(sReg);
            return mipsWord.startP+mipsWord.position;
        }else if(mipsWord.position.length()>0) {
            Printer.compile("addi $"+sReg+", "+(mipsWord.position).substring(1,mipsWord.position.length()-1)+", "+mipsWord.startP);
        }else Printer.compile("lw $"+sReg+", "+mipsWord.Pp);
        if(names.length==1){
            Register.free(sReg);
            return "0($"+sReg+")";
        }
        else if(names.length==3){
            if(Register.LoadImme(names[1]))
                Printer.compile("addi $"+sReg+", $"+sReg+", "+(Integer.parseInt(names[1])*mipsWord.array2*4));
            else {
                int oReg = Register.getTempReg();
                int lReg = Register.getTempReg();
                String p=getPosition(names[1]);//偏移量
                //!!!getPosition后面不能跟分配寄存器，因为该方法结束后就恢复了寄存器，但不可被污染
                Printer.compile("lw $"+oReg+", "+p);
                Printer.compile("li $"+lReg+", "+(mipsWord.array2));//偏移值
                Printer.compile("mul $"+oReg+", $"+oReg+", $"+lReg);//*array2
                Printer.compile("li $"+lReg+", 2");
                Printer.compile("sll $"+oReg+", $"+oReg+", 2");//*4
                Printer.compile("add $"+sReg+", $"+sReg+", $"+oReg);//+
                Register.free(oReg);
                Register.free(lReg);
            }
            if(Register.LoadImme(names[2]))
                Printer.compile("addi $"+sReg+", $"+sReg+", "+(Integer.parseInt(names[2])*4));
            else {
                int oReg = Register.getTempReg();
                int lReg = Register.getTempReg();
                String p=getPosition(names[2]);//偏移量
                Printer.compile("lw $"+oReg+", "+p);
                Printer.compile("sll $"+oReg+", $"+oReg+", 2");//*4
                Printer.compile("add $"+sReg+", $"+sReg+", $"+oReg);//+
                Register.free(oReg);
                Register.free(lReg);
            }
            Register.free(sReg);
            return "0($"+sReg+")";
        }else {
            if(Register.LoadImme(names[1]))
                Printer.compile("addi $"+sReg+", $"+sReg+", "+(Integer.parseInt(names[1])*mipsWord.array2*4));
            else {
                int oReg = Register.getTempReg();
                int lReg = Register.getTempReg();
                String p=getPosition(names[1]);//偏移量
                //!!!getPosition后面不能跟分配寄存器，因为该方法结束后就恢复了寄存器，但不可被污染
                Printer.compile("lw $"+oReg+", "+p);
                Printer.compile("li $"+lReg+", "+(mipsWord.array2));//偏移值
                Printer.compile("mul $"+oReg+", $"+oReg+", $"+lReg);//*array2
                Printer.compile("sll $"+oReg+", $"+oReg+", 2");//*4
                Printer.compile("add $"+sReg+", $"+sReg+", $"+oReg);//+
                Register.free(oReg);
                Register.free(lReg);
            }
            Register.free(sReg); //sReg里面是地址
            return "0($"+sReg+")";
        }

    }
    static public String getPrePosition(String name) {
        name = name.replace("["," ");
        name = name.replace("]","");
        String[] names = name.split(" ");
        int index = charStack.subList(0,charStack.size()-1).lastIndexOf(names[0]);
        if(index<0) return null;
        MIPSWord mipsWord = wordTable.get(index);
        int sReg = Register.getTempReg();
        if(mipsWord.valueType==ValueType.Temp)
            System.out.println("这块随机变量的内存被释放了");
        if(names.length==1 && mipsWord.dataStructure==DataStructure.Int){
            Register.free(sReg);
            return mipsWord.startP+mipsWord.position;
        }else if(mipsWord.position.length()>0) {
            Printer.compile("addi $"+sReg+", "+(mipsWord.position).substring(1,mipsWord.position.length()-1)+", "+mipsWord.startP);
        }else Printer.compile("lw $"+sReg+", "+mipsWord.Pp);
        if(names.length==1){
            Register.free(sReg);
            return "0($"+sReg+")";
        }
        else if(names.length==3){
            if(Register.LoadImme(names[1]))
                Printer.compile("addi $"+sReg+", $"+sReg+", "+(Integer.parseInt(names[1])*mipsWord.array2*4));
            else {
                int oReg = Register.getTempReg();
                int lReg = Register.getTempReg();
                String p=getPosition(names[1]);//偏移量
                //!!!getPosition后面不能跟分配寄存器，因为该方法结束后就恢复了寄存器，但不可被污染
                Printer.compile("lw $"+oReg+", "+p);
                Printer.compile("li $"+lReg+", "+(mipsWord.array2));//偏移值
                Printer.compile("mul $"+oReg+", $"+oReg+", $"+lReg);//*array2
                Printer.compile("sll $"+oReg+", $"+oReg+", 2");//*4
                Printer.compile("add $"+sReg+", $"+sReg+", $"+oReg);//+
                Register.free(oReg);
                Register.free(lReg);
            }
            if(Register.LoadImme(names[2]))
                Printer.compile("addi $"+sReg+", $"+sReg+", "+(Integer.parseInt(names[2])*4));
            else {
                int oReg = Register.getTempReg();
                int lReg = Register.getTempReg();
                String p=getPosition(names[2]);//偏移量
                Printer.compile("lw $"+oReg+", "+p);
                Printer.compile("li $"+lReg+", 2");
                Printer.compile("sll $"+oReg+", $"+oReg+", 2");//*4
                Printer.compile("add $"+sReg+", $"+sReg+", $"+oReg);//+
                Register.free(oReg);
                Register.free(lReg);
            }
            Register.free(sReg);
            return "0($"+sReg+")";
        }else {
            if(Register.LoadImme(names[1]))
                Printer.compile("addi $"+sReg+", $"+sReg+", "+(Integer.parseInt(names[1])*mipsWord.array2*4));
            else {
                int oReg = Register.getTempReg();
                int lReg = Register.getTempReg();
                String p=getPosition(names[1]);//偏移量
                //!!!getPosition后面不能跟分配寄存器，因为该方法结束后就恢复了寄存器，但不可被污染
                Printer.compile("lw $"+oReg+", "+p);
                Printer.compile("li $"+lReg+", "+(mipsWord.array2));//偏移值
                Printer.compile("mul $"+oReg+", $"+oReg+", $"+lReg);//*array2
                Printer.compile("li $"+lReg+", 2");
                Printer.compile("sll $"+oReg+", $"+oReg+", 2");//*4
                Printer.compile("add $"+sReg+", $"+sReg+", $"+oReg);//+
                Register.free(oReg);
                Register.free(lReg);
            }
            Register.free(sReg); //sReg里面是地址
            return "0($"+sReg+")";
        }
    }
    static public void addTempInt(String name){
        MIPSWord mipsWord = new MIPSWord(ValueType.Temp,name);
        charStack.add(name);
        wordTable.add(mipsWord);
        temp++;
        mipsWord.setP(-4*temp,"($fp)");
    }
    static public void addGlobalConstInt(String name){
        MIPSWord mipsWord = new MIPSWord(ValueType.Const,name);
        charStack.add(name);
        wordTable.add(mipsWord);
        mipsWord.setP(4*global,"($gp)");
        global++;
    }
    static public void addGlobalConstArray1(String name,int size1) {
        MIPSWord mipsWord = new MIPSWord(ValueType.Const,name,size1);
        charStack.add(name);
        wordTable.add(mipsWord);
        mipsWord.setP(4*global,"($gp)");
        global+=size1;
    }
    static public void addGlobalConstArray2(String name,int size1,int size2) {
        MIPSWord mipsWord = new MIPSWord(ValueType.Const,name,size1,size2);
        charStack.add(name);
        wordTable.add(mipsWord);
        mipsWord.setP(4*global,"($gp)");
        global+=size1*size2;
    }
    static public void addConstInt(String name){
        MIPSWord mipsWord = new MIPSWord(ValueType.Const,name);
        charStack.add(name);
        wordTable.add(mipsWord);
        temp++;
        mipsWord.setP(-4*temp,"($fp)");
    }
    static public void addConstArray1(String name,int size1){
        MIPSWord mipsWord = new MIPSWord(ValueType.Const,name,size1);
        charStack.add(name);
        wordTable.add(mipsWord);
        temp+=size1;//永远从低地址开始
        mipsWord.setP(-4*temp,"($fp)");
    }
    static public void addConstArray2(String name,int size1,int size2){
        MIPSWord mipsWord = new MIPSWord(ValueType.Const,name,size1,size2);
        charStack.add(name);
        wordTable.add(mipsWord);
        temp+=size1*size2;//永远从低地址开始
        mipsWord.setP(-4*temp,"($fp)");
    }
    static public void addVartInt(String name){
        MIPSWord mipsWord = new MIPSWord(ValueType.Var,name);
        charStack.add(name);
        wordTable.add(mipsWord);
        temp++;
        mipsWord.setP(-4*temp,"($fp)");
    }
    static public void addVarArray1(String name,int size1){
        MIPSWord mipsWord = new MIPSWord(ValueType.Var,name,size1);
        charStack.add(name);
        wordTable.add(mipsWord);
        temp+=size1;//永远从低地址开始
        mipsWord.setP(-4*temp,"($fp)");
    }
    static public void addArrAdd(String name) {
        MIPSWord mipsWord = new MIPSWord(ValueType.Var,name,0);
        charStack.add(name);
        wordTable.add(mipsWord);//
        temp+=1;//永远从低地址开始
        //这个里面存放的是地址，$fp无关
        mipsWord.Pp=(-4*temp)+"($fp)";
    }
    static public void addArrAdd(String name, int size2) {
        MIPSWord mipsWord = new MIPSWord(ValueType.Var,name,0,size2);
        charStack.add(name);
        wordTable.add(mipsWord);
        temp++;
        mipsWord.Pp=(-4*temp)+"($fp)";
    }
    static public void addVarArray2(String name,int size1,int size2){
        MIPSWord mipsWord = new MIPSWord(ValueType.Var,name,size1,size2);
        charStack.add(name);
        wordTable.add(mipsWord);
        temp+=size1*size2;//永远从低地址开始
        mipsWord.setP(-4*temp,"($fp)");
    }
    static public void addGlobalVarInt(String name){
        MIPSWord mipsWord = new MIPSWord(ValueType.Var,name);
        charStack.add(name);
        wordTable.add(mipsWord);
        mipsWord.setP(4*global,"($gp)");
        global++;
    }
    static public void addGlobalVarArray1(String name,int size1) {
        MIPSWord mipsWord = new MIPSWord(ValueType.Var,name,size1);
        charStack.add(name);
        wordTable.add(mipsWord);
        mipsWord.setP(4*global,"($gp)");
        global+=size1;
    }
    static public void addGlobalVarArray2(String name,int size1,int size2) {
        MIPSWord mipsWord = new MIPSWord(ValueType.Var,name,size1,size2);
        charStack.add(name);
        wordTable.add(mipsWord);
        mipsWord.setP(4*global,"($gp)");
        global+=size1*size2;
    }
}
class FuncParam {
    //List<MIPSWord> params;
    List<Integer> intParams;
    List<String> params;
    List<Boolean> ifReal;
     public FuncParam() {
        super();
        //params  = new LinkedList<MIPSWord>();
        intParams=new LinkedList<Integer>();
        params = new LinkedList<String>();
        ifReal = new LinkedList<Boolean>();
    }
    public void addParams(String name) {
        if(Register.LoadImme(name)) {
            intParams.add(Integer.parseInt(name));
            //params.add(null);
            ifReal.add(true);
            params.add("");
        }else {
            String arr[]=name.split("\\[");
            int index = WordTable.charStack.lastIndexOf(arr[0]);
            MIPSWord word = WordTable.wordTable.get(index);
            String p;
            if(word.dataStructure==DataStructure.Array1&&arr.length==2){
                ifReal.add(true);
            }
            else if(word.dataStructure==DataStructure.Array2&&arr.length==3){
                ifReal.add(true);
            }else if(word.dataStructure==DataStructure.Int){
                ifReal.add(true);
            }
            else {
                ifReal.add(false);
            }
            params.add(name);
            intParams.add(null);
        }
    }
    public void savereset(Function function){
        Printer.compile("lw $ra,0($sp)\n" +
                "addi $sp,$sp,4");
    }
    public void reset(Function function){
        for(int index = WordTable.wordTable.size()-1;index>=function.startIndex;index--){
            MIPSWord word = WordTable.wordTable.get(index);
            int regIndex = Register.getTempReg();

            if(word.dataStructure==DataStructure.Int){
                Printer.compile("lw $"+regIndex+","+"0($sp)");
                Printer.compile("sw $"+regIndex+","+word.startP+word.position);

                Printer.compile(
                        "addi $sp,$sp,4");
            }
            else if(word.dataStructure==DataStructure.Array1&&word.array1==0){
                Printer.compile("lw $"+regIndex+","+"0($sp)");
                Printer.compile("sw $"+regIndex+","+word.Pp);
                Printer.compile("addi $sp,$sp,4");
            }
            //TODO：getposition *4
            else if(word.dataStructure==DataStructure.Array1){
                for(int i=word.array1-1;i>=0;i--){
                    Printer.compile("lw $"+regIndex+","+"0($sp)");
                    Printer.compile("lw $"+regIndex+","+(word.startP+i*4)+word.position);
                    Printer.compile("addi $sp,$sp,4");
                }
            }
            else if(word.dataStructure==DataStructure.Array2&&word.array1==0){
                Printer.compile("lw $"+regIndex+","+"0($sp)");
                Printer.compile("sw $"+regIndex+","+word.Pp);
                Printer.compile("addi $sp,$sp,4");
            }
            else if(word.dataStructure==DataStructure.Array2){
                for(int i=word.array1* word.array2-1;i>=0 ;i--){
                    Printer.compile("lw $"+regIndex+","+"0($sp)");
                    Printer.compile("lw $"+regIndex+","+(word.startP+i*4)+word.position);
                    Printer.compile("addi $sp,$sp,4");
                }
            }
            Register.free(regIndex);
        }
        Printer.compile("lw $ra,0($sp)\n" +
                "addi $sp,$sp,4");
    }
    public void callFunction(Function function) {
        int sum=4;
        for(int index=0;index<params.size();index++) {
            sum+=4;
        }
        for(int index = function.startIndex;index<WordTable.wordTable.size();index++){
            MIPSWord word = WordTable.wordTable.get(index);
            if(word.dataStructure==DataStructure.Int)sum+=4;
            else if(word.dataStructure==DataStructure.Array1&&word.array1==0)sum+=4;
            else if(word.dataStructure==DataStructure.Array1)sum+=4*word.array1;
            else if(word.dataStructure==DataStructure.Array2&&word.array1==0)sum+=4;
            else if(word.dataStructure==DataStructure.Array2)sum+=4*word.array1*word.array2;
        }
        Printer.compile("addi $sp,$sp,-"+sum);
        Printer.compile("sw $ra,"+(sum-4)+"($sp)");
        sum =sum-8;
        for(int index = function.startIndex;index<WordTable.wordTable.size();index++){
            MIPSWord word = WordTable.wordTable.get(index);
            int regIndex = Register.getTempReg();

            if(word.dataStructure==DataStructure.Int){
                Printer.compile("lw $"+regIndex+","+word.startP+word.position);
                Printer.compile("sw $"+regIndex+","+sum+"($sp)");
                sum-=4;
            }
            else if(word.dataStructure==DataStructure.Array1&&word.array1==0){
                Printer.compile("lw $"+regIndex+","+word.Pp);
                Printer.compile("sw $"+regIndex+","+sum+"($sp)");
                sum-=4;
            }
            //TODO：getposition *4
            else if(word.dataStructure==DataStructure.Array1){
                for(int i=0;i< word.array1;i++){
                    Printer.compile("lw $"+regIndex+","+(word.startP+i*4)+word.position);
                    Printer.compile("sw $"+regIndex+","+sum+"($sp)");
                    sum-=4;
                }
            }
            else if(word.dataStructure==DataStructure.Array2&&word.array1==0){
                Printer.compile("lw $"+regIndex+","+word.Pp);
                Printer.compile("sw $"+regIndex+","+sum+"($sp)");
                sum-=4;
            }
            else if(word.dataStructure==DataStructure.Array2){
                for(int i=0;i< word.array1* word.array2;i++){
                    Printer.compile("lw $"+regIndex+","+(word.startP+i*4)+word.position);
                    Printer.compile("sw $"+regIndex+","+sum+"($sp)");
                    sum-=4;
                }
            }
            Register.free(regIndex);
        }
        for(int index = params.size()-1;index>=0;index--) {
            String name = params.get(index);
            int regIndex = Register.getTempReg();
            if(name.length()==0) {
                Printer.compile("li $"+regIndex+","+intParams.get(index));
                Printer.compile("sw $"+regIndex+", "+sum+"($sp)");;
            }
            else {
                String p = WordTable.getPosition(name);//p里储存的int真值或数组头的值
                if(ifReal.get(index)){
                    Printer.compile("lw $"+regIndex+","+p);
                    Printer.compile("sw $"+regIndex+", "+sum+"($sp)");
                }else {
                    p = p.substring(2,p.length()-1);
                    Printer.compile("sw "+p+", "+sum+"($sp)");
                }

            }
            sum-=4;
            Register.free(regIndex);
        }
    }
    public void savecallFunction(Function function) {
        int sum=4;
        for(int index=0;index<params.size();index++) {
            sum+=4;
        }
        Printer.compile("addi $sp,$sp,-"+sum);
        Printer.compile("sw $ra,"+(sum-4)+"($sp)");
        sum =sum-8;
        for(int index = params.size()-1;index>=0;index--) {
            String name = params.get(index);
            int regIndex = Register.getTempReg();
            if(name.length()==0) {
                Printer.compile("li $"+regIndex+","+intParams.get(index));
                Printer.compile("sw $"+regIndex+", "+sum+"($sp)");;
            }
            else {
                String p = WordTable.getPosition(name);//p里储存的int真值或数组头的值
                if(ifReal.get(index)){
                    Printer.compile("lw $"+regIndex+","+p);
                    Printer.compile("sw $"+regIndex+", "+sum+"($sp)");
                }else {
                    p = p.substring(2,p.length()-1);
                    Printer.compile("sw "+p+", "+sum+"($sp)");
                }

            }
            sum-=4;
            Register.free(regIndex);
        }
    }
}
class Function {
    String name;
    Word returnType;
    int startIndex;
    static List <Function> allFs= new LinkedList<Function>();
    List<Function> callFs = new LinkedList<Function>();//除自身以外的直接调用函数
    public boolean addCall(String name){
        if(name.equals(this.name))return true;
        for(int index=0;index<allFs.size();index++) {
            if(allFs.get(index).name.equals(name)) {
                Function son = allFs.get(index);
                callFs.add(son);//直接调用函数
                return son.checkCircle(this.name);//子有没有调用我
            }
        }System.out.println("未找到！");
        return false;
    }
    public boolean checkCircle(String name){
        for(int index=0;index<callFs.size();index++){
            Function function = callFs.get(index);
            if(function.checkCircle(name))return true;
        }
        return false;
    }
    public Function(String name) {
        super();
        allFs.add(this);
        this.name = name;
        if(Modifier.times>=0)
            startIndex = Modifier.charStack.size();
        else
        startIndex = WordTable.wordTable.size();
    }
    public void setReturnType(Word returnType) {
        this.returnType = returnType;
    }
}
class ConstStr{
    static List<ConstStr> conststrings = new LinkedList<ConstStr>();
    static int index=0;
    String content;
    String name;
    public ConstStr(String s){
        super();
        index++;
        this.content=s;
        this.name="MIP_STR"+index;
    }
    static void addConStr(String s){
        for(int index =0;index<conststrings.size();index++) {
            if(conststrings.get(index).content.equals(s))
                return;
        }
        conststrings.add(new ConstStr(s));
    }
    static String getName(String s) {
        for(int index =0;index<conststrings.size();index++) {
            if(conststrings.get(index).content.equals(s))
                return conststrings.get(index).name;
        }
        return null;
    }
    static void printAll(){
        for(int index =0;index<conststrings.size();index++) {
            ConstStr str = conststrings.get(index);
            Printer.compile(str.name+": .asciiz "+str.content);
        }
        Printer.compile(".text ");
    }
}
