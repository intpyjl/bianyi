import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Modifier {
    static List<FlowWord>  useFulList = new LinkedList<FlowWord>();
    static List<String> charStack = new LinkedList<String>();
    static List<FlowWord> wordStack = new LinkedList<FlowWord>();
    static List<FlowWord> wordTable = new LinkedList<FlowWord>();
    static BlockFlag blockFlag = new BlockFlag();
    private static Function nowf=null;
    static boolean isChanged = true;
    static boolean justLabel = false;
    static int times=-1;
    static int lineNumber=0;
    static List<String> noChange = new LinkedList<String>();
    //TODO：用总顺序值唯一标识变量
    //TODO：复制传播，当终点从栈中弹出后，不可能再传播；当中间节点被赋值时，不可能再传播
    static {
        noChange.add("const");
        noChange.add("var");
        noChange.add("BlockBegin");
        noChange.add("BlockEnd");
        noChange.add("ret");
        noChange.add("int");
        noChange.add("void");
        noChange.add("push");
        noChange.add("call");
        noChange.add("para");
        noChange.add("read");
        noChange.add("print");
        noChange.add("bne");
        noChange.add("beq");
        noChange.add("cmp");
        noChange.add("j");
    }
    //调用前无需处理
    public static void addUseFul(String name){
        if(name.equals("RET"))return;
        try {
            int i = Integer.parseInt(name);
            return;
        }catch (Exception e){
            //
        }
        if(name.contains("[")){
            name = name.replace("["," ");
            name = name.replace("]","");
            String[] values = name.split(" ");
            for(int i=0;i<values.length;i++)addUseFul(values[i]);
        }else {
            FlowWord flowWord=getWord(name);
            if(!useFulList.contains(flowWord)){
                isChanged=true;
                useFulList.add(flowWord);
            }
        }
    }
    public static  void startNewRound(){
        nowf=null;
        times++;
        lineNumber=0;
        isChanged = false;
        charStack = new LinkedList<String>();
        wordStack = new LinkedList<FlowWord>();
        blockFlag = new BlockFlag();
        FlowWord.allIndex = -1;
    }
    //调用前需处理
    public static void addWord(String name) {
        if(name.equals("RET"))return;
        //System.out.println(FlowWord.allIndex+": "+name);
        if(name==null){
            System.out.println(lineNumber);
        }
        FlowWord flowWord;
        if(times==0){
            flowWord = new FlowWord();
            wordTable.add(flowWord);
        }else {
            flowWord = FlowWord.countNew();
        }
        charStack.add(name);
        wordStack.add(flowWord);
    }
    public static boolean isUseful(){
        for(int i=0;i<useFulList.size();i++){
            if(useFulList.get(i).upComingIndex==FlowWord.allIndex+1)
                return true;
        }return  false;
    }
    public static boolean isUseful(String name){
        try {
            int i = Integer.parseInt(name);
            return false;
        }catch (Exception e){
            //
        }
        String[] values =new String[0];
        if(name.contains("[")){
            name = name.replace("["," ");
            name = name.replace("]","");
            values = name.split(" ");
            name = values[0];
        }
        FlowWord flowWord = getWord(name);
        if(useFulList.contains(flowWord)){
            for(int i=0;i<values.length;i++)addUseFul(values[i]);
            return true;
        }
        return false;
    }
    public static String getIden(String name){
        String[] values;
        if(name.contains("[")){
            name = name.replace("["," ");
            name = name.replace("]","");
            values = name.split(" ");
            name = values[0];
        }
        return  name;
    }
    public static FlowWord getWord(String name) {
        try{
            int index = charStack.lastIndexOf(name);
            return wordStack.get(index);
        }catch (Exception e){
            System.out.println(name);
            System.out.println(lineNumber);
            System.out.println("未定义的量！");
            System.exit(0);
            return null;
        }
    }

    public static void handleAssign(String[] formula) {
        String cal[]=new String[0];
        String[] ops=new String[]{"\\+","\\*","/","%","<=",">=",">","<","==","!=","-"};
        int number=0;
        int brack = 0;
        if(formula.length>1)//意味着等式右边有内容
        {
            int index;
            for(index =0;index<ops.length;index++){
                cal = formula[1].split(ops[index]);
                if (cal.length > 1 && index==ops.length-1&&cal[0].length() == 0) return;
                if (cal.length > 1) {
                    break;
                }
            }
            if(index == ops.length)addUseFul(formula[1]);
            else {
                addUseFul(cal[0]);
                if(cal.length>1)
                    addUseFul(cal[1]);
            }
        }
    }
    public static void deleteTrash(String line) {
        lineNumber++;
        String[] strings = line.split(" ");

        if(strings[0].equals("const")||strings[0].equals("var"))
        {
            String[] keys= new String[2];
            int t;
            if(strings[1].equals("int")){
                t=strings[2].indexOf("=");
                if(t>=0){
                    keys[0]=strings[2].substring(0,t);
                    keys[1]=strings[2].substring(t+1);
                }else keys[0]=strings[2];
            }else if(strings[1].equals("arr")){
                //strings[2]为'int'
                strings[3] = strings[3].replace("["," ");
                strings[3] = strings[3].replace("]","");
                String[] values = strings[3].split(" ");
                keys[0]=values[0];
            }
            if(isUseful())
               Printer.intoMid2(line);
            addWord(keys[0]);
            return;
        }else if(strings[0].equals("para")){
            String name = strings[2];
            if(strings[2].contains("[")){
                String[] names= strings[2].split("\\[");
                name = names[0];
            }
            addWord(name);

        }else if(strings[0].equals("BlockBegin")){
            blockFlag = new BlockFlag(blockFlag);
        }else if(strings[0].equals("BlockEnd")){
            blockFlag = blockFlag.free();
        }else if(strings[0].equals("int") || strings[0].equals("void")){
            if(nowf!=null) {
                blockFlag = blockFlag.free();
                nowf = null;
            }
            String name=(strings[1].split("\\("))[0];
            Function function = new Function(name);
            nowf = function;
            blockFlag = new BlockFlag(blockFlag);
        }
        for(int i=0;i<noChange.size();i++){
            if(strings[0].equals(noChange.get(i))){
                Printer.intoMid2(line);
                return;
            }
        }
        if(strings[0].charAt(strings[0].length()-1)==':'){
            Printer.intoMid2(line);
        }
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
            if(!charStack.contains(getIden(keys[0])))addWord(getIden(keys[0]));
            if(isUseful(keys[0]))
                Printer.intoMid2(line);
        }
    }
    public static void handle(String line) {
        lineNumber++;
        String[] strings = line.split(" ");
        //常量声明
        if(strings[0].equals("const")||strings[0].equals("var"))
        {
            String[] keys= new String[2];
            int t=-1;
            if(strings[1].equals("int")){
                t=strings[2].indexOf("=");
                if(t>=0){
                    keys[0]=strings[2].substring(0,t);
                    keys[1]=strings[2].substring(t+1);
                }else keys[0]=strings[2];
                if(t>=0&&isUseful())//这个时候加入的useful
                    handleAssign(keys);
            }else if(strings[1].equals("arr")){
                strings[3] = strings[3].replace("["," ");
                strings[3] = strings[3].replace("]","");
                String[] values = strings[3].split(" ");
                keys[0]=values[0];
                if(isUseful())
                {
                    for(int index=1;index<values.length;index++)
                        addUseFul(values[index]);
                }
            }
            addWord(keys[0]);
        }
        else if(strings[0].equals("BlockBegin")){
            blockFlag = new BlockFlag(blockFlag);
        }else if(strings[0].equals("BlockEnd")){
            blockFlag = blockFlag.free();
        }else if(strings[0].equals("ret")) {
            if(strings.length>1)
                addUseFul(strings[1]);
        }else if(strings[0].equals("int") || strings[0].equals("void")){
            if(nowf!=null) {
                blockFlag = blockFlag.free();
                nowf = null;
            }
            String name=(strings[1].split("\\("))[0];
            Function function = new Function(name);
            nowf = function;
            blockFlag = new BlockFlag(blockFlag);
        } else if(strings[0].equals("para")) {
                String name = strings[2];
            boolean isArray=false;
                if(strings[2].contains("[")){
                    String[] names= strings[2].split("\\[");
                    name = names[0];
                    isArray=true;
                }
                addWord(name);
            if(isArray)
                addUseFul(name);
        } else if(strings[0].equals("push")) {
            addUseFul(strings[1]);
        } else if(strings[0].equals("call")) {
            //do nothing
        }
        //读
        else if(strings[0].equals("read")){
            String content = strings[1];
            if(content.contains("[")){
                content = content.replace("["," ");
                content = content.replace("]","");
                String[] values = content.split(" ");
                if(isUseful(values[0]))
                for(int i=1;i<values.length;i++)addUseFul(values[i]);
            }
            //do nothing
        }
        else if(strings[0].equals("print")) {
            String content = line.substring(6);
            if(content.charAt(0)!='\"')
                addUseFul(content);
        }
        //标签
        else if(strings[0].charAt(strings[0].length()-1)==':'){
            String label = strings[0].substring(0,strings[0].length()-1);
            //TODO:label的并查集，可以小幅度减小branch
        }
        else if(strings[0].equals("cmp")){
            String[] number = strings[1].split(",");
            addUseFul(number[0]);
        }
        else if(strings[0].equals("bne")||strings[0].equals("beq")){
        }
        else if(strings[0].equals("j")){
            // do nothing
        }
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
            if(!charStack.contains(getIden(keys[0])))addWord(getIden(keys[0]));
            if(isUseful(keys[0]))
                handleAssign(keys);
        }
    }
}
class FlowWord{
    int upComingIndex;
    static int allIndex = -1;
    //不能被删除的 b语句，形参和实参，返回值，压栈用到的？
    //每一步赋值语句，从下至上扫描upFlows，判断该赋值语句是否有意义；
    public FlowWord(){
        super();
        allIndex++;
        this.upComingIndex = allIndex;
    }
    public static FlowWord countNew(){
        allIndex++;
        return Modifier.wordTable.get(allIndex);
    }
}
class LabelGroup{
    String label;
    LabelGroup father;
    List<LabelGroup> labels = new LinkedList<LabelGroup>();
    static List<LabelGroup> allLabels = new LinkedList<LabelGroup>();
    static List<String> allChars = new LinkedList<String>();
    static List<LabelGroup> fatherLabels = new LinkedList<LabelGroup>();
}
