package ir.appsoar.teknesian.Helper;

public class MohasebeA {
    public static int ACalculate(Double input){
        int result=0;
        if(input<0.37){
            result=1;
        } else if (input<0.58){
            result=2;
        }else if (input<0.70){
            result=3;
        }else if (input<0.90){
            result=4;
        }else if (input<1.17){
            result=5;
        }else if (input<1.31){
            result=6;
        }else if (input<1.45){
            result=7;
        }else if (input<1.59){
            result=8;
        }else if (input<1.73){
            result=9;
        }else if (input<1.87){
            result=10;
        }else if (input<2.01){
            result=11;
        }else if (input<2.15){
            result=12;
        }else if (input<2.29){
            result=13;
        }else if (input<2.43){
            result=14;
        }else if (input<2.57){
            result=15;
        }else if (input<2.71){
            result=16;
        }else if (input<2.85){
            result=17;
        }else if (input<2.99){
            result=18;
        }else if (input<3.13){
            result=19;
        }else if (input<3.40){
            result=20;
        }
        return result;
    }
}
