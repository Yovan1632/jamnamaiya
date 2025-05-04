package com.example.receipt.controller;

import java.text.DecimalFormat;

public class NumberToWordsConverter {
    private static final String[] tensNames = {
        "", " ten", " twenty", " thirty", " forty", " fifty", " sixty", " seventy", " eighty", " ninety"
    };
    private static final String[] numNames = {
        "", " one", " two", " three", " four", " five", " six", " seven", " eight", " nine",
        " ten", " eleven", " twelve", " thirteen", " fourteen", " fifteen", " sixteen",
        " seventeen", " eighteen", " nineteen"
    };

    private static String convertLessThanOneThousand(int number) {
        String soFar;
        if (number % 100 < 20){
            soFar = numNames[number % 100];
            number /= 100;
        }
        else {
            soFar = numNames[number % 10];
            number /= 10;
            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) return soFar;
        return numNames[number] + " hundred" + soFar;
    }

    public static String convert(long number) {
        if (number == 0) { return "zero"; }
        String snumber = Long.toString(number);

        // pad with "0"
        String mask = "000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        snumber = df.format(number);

        // XXX crore XX lakh XX thousand XXX
        int billions = Integer.parseInt(snumber.substring(0,3));
        int millions  = Integer.parseInt(snumber.substring(3,5));
        int hundredThousands = Integer.parseInt(snumber.substring(5,7));
        int thousands = Integer.parseInt(snumber.substring(7,9));
        int hundreds = Integer.parseInt(snumber.substring(9,12));

        String tradBillions;
        switch (billions) {
            case 0:
                tradBillions = "";
                break;
            case 1 :
                tradBillions = convertLessThanOneThousand(billions) + " billion ";
                break;
            default :
                tradBillions = convertLessThanOneThousand(billions) + " billion ";
        }
        String result =  tradBillions;

        String tradMillions;
        switch (millions) {
            case 0:
                tradMillions = "";
                break;
            case 1 :
                tradMillions = convertLessThanOneThousand(millions) + " million ";
                break;
            default :
                tradMillions = convertLessThanOneThousand(millions) + " million ";
        }
        result =  result + tradMillions;

        String tradHundredThousands;
        switch (hundredThousands) {
            case 0:
                tradHundredThousands = "";
                break;
            case 1 :
                tradHundredThousands = "one thousand ";
                break;
            default :
                tradHundredThousands = convertLessThanOneThousand(hundredThousands) + " thousand ";
        }
        result =  result + tradHundredThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result =  result + tradThousand;

        String tradHundreds;
        tradHundreds = convertLessThanOneThousand(hundreds);
        result =  result + tradHundreds;

        // remove extra spaces
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }
} 