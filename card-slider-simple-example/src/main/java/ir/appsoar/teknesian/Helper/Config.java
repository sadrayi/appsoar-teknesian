package ir.appsoar.teknesian.Helper;

public class Config {

    //your admin panel url
    //public static String ADMIN_PANEL_URL = "http://136.243.245.243:3100/teknesian/";
    public static String ADMIN_PANEL_URL = "http://snapplift.com:3100/teknesian/";
    public static String SocketUrl = "http://snapplift.com:3100/";
    //public static String ADMIN_PANEL_URL = "http://192.168.64.49:3100/teknesian/";
    //public static String Pic_Url = "http://136.243.245.243:3000/images/";
    public static String Pic_Url = "http://snapplift.com/images/";
    //public static String Pic_Url = "http://192.168.64.49:3000/images/";

    //your applicationId or package name
    private static String PACKAGE_NAME = "ir.appsoar.teknesian";

    //set true if you want to enable RTL (Right To Left) mode, e.g : Arabic Language
    public static final boolean ENABLE_RTL_MODE = true;

    //*============================================================================*//
    //don't make changes anything
    //database path configuration
    private static String DB_PATH_START = "/data/data/";
    private static String DB_PATH_END = "/databases/";
    public static String DB_PATH = DB_PATH_START + PACKAGE_NAME + DB_PATH_END;

}
