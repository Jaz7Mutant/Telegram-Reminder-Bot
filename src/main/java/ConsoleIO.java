//import java.util.Scanner;
//
//public class ConsoleIO implements UserIO {
//
//    @Override
//    public void showMessage(String message, long chatId){
//        System.out.println(message);
//    }
//
//    @Override
//    public String getUserText(String prompt, long chatId) {
//        if (prompt != null) {
//            System.out.println(prompt);
//        }
//        Scanner in = new Scanner(System.in);
//        return in.nextLine();
//    }
//
//    @Override
//    public int getOnClickButton(String[] buttons, long chatId) {
//        for (int i = 0; i < buttons.length; i++){
//            System.out.println((i+1) + ". " + buttons[i]);
//        }
//        Scanner in = new Scanner(System.in);
//        int a = -1;
//        while (a >= buttons.length + 1 || a < 1){
//            a = in.nextInt();
//        }
//        return a - 1;
//    }
//
//    @Override
//    public void showList(String prompt, String[] elements, long chatId) {
//        System.out.println(prompt);
//        for (int i = 0; i < elements.length; i++) {
//            String str = elements[i];
//            System.out.println((i + 1) + ". " + str);
//        }
//    }
//
////    @Override
////    public String getChatId() {
////        throw new UnsupportedOperationException(); //TODO;
////    }
//}
