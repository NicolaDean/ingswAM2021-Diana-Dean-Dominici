package it.polimi.ingsw.view.utils;

public class Logger {

    public CliColors out;

    public static String istructionText = CliColors.CYAN_TEXT;
    public String istructionBackground  = CliColors.BLACK_TEXT;

    public String warningColor          = CliColors.YELLOW_TEXT;
    public String goodMessage           = CliColors.GREEN_TEXT;

    public Logger()
    {
        out = new CliColors(System.out);
    }

    /**
     * Print a colored message corresponding to "command request"
     * @param content message
     */
    public void printRequest(String content)
    {
        out.printColored(content,this.istructionText,this.istructionBackground);
    }

    public void printError(String content)
    {
        out.printColored(content,CliColors.RED_TEXT,this.istructionBackground);
    }
    /**
     * Print a colored message corresponding to "warnings"
     * @param content message
     */

    public void printWarning(String content)
    {
        out.printColored(content,this.warningColor,this.istructionBackground);
    }

    /**
     * Print a colored message corresponding to "operation completed with no errors"
     * @param content message
     */
    public void printGoodMessages(String content)
    {
        out.printColored(content,this.goodMessage,this.istructionBackground);
    }

    public void Welcome()
    {
        out.printColored("WELCOMEE TO LORENZO IL MAGNIFICO", CliColors.RED_TEXT,CliColors.WHITE_BACKGROUND);
    }

    /**
     * Print game Logo with asii art
     */
    public void printLogo()
    {
        String logo =
                                "  _                                                          \n" +
                                " | |                                                         \n" +
                                " | |     ___  _ __ ___ _ __  _______                         \n" +
                                " | |    / _ \\| '__/ _ \\ '_ \\|_  / _ \\                        \n" +
                                " | |___| (_) | | |  __/ | | |/ / (_) |                       \n" +
                                " |______\\___/|_|  \\___|_| |_/___\\___/      _  __ _           \n" +
                                " |_   _| |       |  \\/  |                 (_)/ _(_)          \n" +
                                "   | | | |       | \\  / | __ _  __ _ _ __  _| |_ _  ___ ___  \n" +
                                "   | | | |       | |\\/| |/ _` |/ _` | '_ \\| |  _| |/ __/ _ \\ \n" +
                                "  _| |_| |____   | |  | | (_| | (_| | | | | | | | | (_| (_) |\n" +
                                " |_____|______|  |_|  |_|\\__,_|\\__, |_| |_|_|_| |_|\\___\\___/ \n" +
                                "                                __/ |                        \n" +
                                "                               |___/    ";

        String logo2   =
            "╭╮╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╭╮╱╭━╮╭━╮╱╱╱╱╱╱╱╱╱╱╭━╮\n" +
            "┃┃╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱┃┃╱┃┃╰╯┃┃╱╱╱╱╱╱╱╱╱╱┃╭╯\n" +
            "┃┃╱╱╭━━┳━┳━━┳━╮╭━━━┳━━╮╭┫┃╱┃╭╮╭╮┣━━┳━━┳━╮╭┳╯╰┳┳━━┳━━╮\n" +
            "┃┃╱╭┫╭╮┃╭┫┃━┫╭╮╋━━┃┃╭╮┃┣┫┃╱┃┃┃┃┃┃╭╮┃╭╮┃╭╮╋╋╮╭╋┫╭━┫╭╮┃\n" +
            "┃╰━╯┃╰╯┃┃┃┃━┫┃┃┃┃━━┫╰╯┃┃┃╰╮┃┃┃┃┃┃╭╮┃╰╯┃┃┃┃┃┃┃┃┃╰━┫╰╯┃\n" +
            "╰━━━┻━━┻╯╰━━┻╯╰┻━━━┻━━╯╰┻━╯╰╯╰╯╰┻╯╰┻━╮┣╯╰┻╯╰╯╰┻━━┻━━╯\n" +
            "╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╭━╯┃";

        this.out.clear();
        this.out.setBold();

       //this.out.printColored(logo,CliColors.RED_TEXT,CliColors.BLACK_BACKGROUND);
       this.out.reset();
       this.out.printColored(logo2,CliColors.RED_TEXT,CliColors.BLACK_BACKGROUND);
       this.out.reset();
    }

    /**
     * if user write help this menu will be shown
     */
    public void printHelp()
    {
        this.out.println("------------------------------------------");
        this.printWarning(" This list of commands is usable only during others turn");
        this.printWarning(" Whenever you need you can type h or help tho show this print again");
        this.out.println("------------------------------------------");
        this.out.println(" default = cancel");
        this.out.println(" q-quit/disconnect");
        this.out.println(" 0-cancel");
        this.out.println(" 1-startGame");
        this.out.println(" 2-showDashboard");
        this.out.println(" 3-swapDeposit");
        this.out.println(" 4-spyPlayer");
        this.out.println("------------------------------------------");
    }

}