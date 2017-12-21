import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTest {

    public static void main(String[] args) {


        long time = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
//            String regex = "#\\([\\D]+?\\)";
//            String regex = "#\\([\\S]+?\\)";
            String regex = "#\\(\\S+?\\)";
//            String regex = "#\\([\\w]+?\\)";
//            String regex = "#\\(\\w+?\\)";
            String input = "asdjla#(1xxx)a#(bb1b)#(ccc1)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(input);
            ArrayList al = new ArrayList();
            while (m.find()) {
                al.add(m.group(0));
            }
//        for (int i = 0; i < al.size(); i++) {
//            System.out.println(al.get(i).toString().substring(2, al.get(i).toString().length() - 1));
//        }
        }

        System.out.println(System.currentTimeMillis() - time);


    }


}


