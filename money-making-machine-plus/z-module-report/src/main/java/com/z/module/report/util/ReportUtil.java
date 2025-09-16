package com.z.module.report.util;

public class ReportUtil {

    public static int columnToNumber(String column) {
        int result = 0;
        for (int i = 0; i < column.length(); i++) {
            result = result * 26 + (column.charAt(i) - 'A' + 1);
        }
        return result;
    }

    /**
     * @data: 2023/7/21-下午4:47
     * @User: zhao
     * @method: numberToColumn
     * @param number :
     * @return: java.lang.String
     * @Description: 将数字转换为A,Ｂ表示, 如:1为A, 27为AA
     */
    public static String numberToColumn(int number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (number - 1) % 26;
            result.insert(0, (char) ('A' + remainder));
            number = (number - 1) / 26;
        }
        return result.toString();
    }

}
