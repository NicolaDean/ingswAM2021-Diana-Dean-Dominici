package it.polimi.ingsw.utils;

import it.polimi.ingsw.view.utils.Logger;

public  class DebugMessages {

    public static boolean infiniteResources = true;
    public static boolean enableError = true;
    public static boolean enableWarning = true;
    public static boolean enableNetwork= false;
    public static boolean enableGeneric = true;
    public static boolean windowsDetection = false;
    public static Logger log = new Logger();

    public static void printError(String msg)
    {
        if(enableError)
        {
            log.printError(msg);
        }
    }

    public static void printWarning(String msg)
    {
        if(enableWarning)
        {
            log.printError(msg);
        }
    }

    public static void printNetwork(String msg)
    {
        if(enableNetwork)
        {
            System.out.println(msg);
        }
    }
    public static void printGeneric(String msg)
    {
        if(enableGeneric)
        {
            System.out.println(msg);
        }
    }
}
