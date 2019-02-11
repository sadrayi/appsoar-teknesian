package ir.appsoar.teknesian.Helper;

import java.text.DecimalFormat;

public class MoneyFormater {
    public static String  Mooneyformatter(String cost){
        int costint;
        String money="";
        DecimalFormat formatter = new DecimalFormat("###,###,##0");
        String formattedNumber=cost;
        try{
            costint=Integer.parseInt(cost);
            formattedNumber=formatter.format(costint);
            money=" تومان";
        }catch (Exception ex){

        }
 /*       if(!cost.equals("توافقی"))
        {
        }*/
        return formattedNumber+money;
    }

}
