package utils;

public class Debugger {

    private boolean bool;

    public Debugger(boolean bool) {
        this.bool = bool;
    }

    public void log(String message) {
        if(bool)
            System.out.println("Log : " + message);
    }

    public void log(String message, String identifier) {
        if(bool)
            System.out.println("Log[" + identifier + "]" + message);
    }
}
