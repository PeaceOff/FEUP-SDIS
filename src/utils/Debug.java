package utils;

public class Debug {

    private static boolean bool = true;
    
    private static String separator = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
    
    public static void log(String message) {
        if(bool)
            System.out.println("Log : " + message);
    }

    public static void log(String identifier, String message) {
        if(bool)
            System.out.println(">" + identifier + " : " + message);
    }
    
    public static void log(int n , String identifier, String message) {
        if(bool)
            System.out.println(">"+ separator.substring(0, n) + identifier + " : " + message);
    }
}
