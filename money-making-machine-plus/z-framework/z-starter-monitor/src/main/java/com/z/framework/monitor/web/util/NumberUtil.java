package com.z.framework.monitor.web.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class NumberUtil {
    private static final int DEFAULT_DIV_SCALE = 10;
    private static final long[] FACTORIALS = new long[]{1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L};

    public NumberUtil() {
    }

    public static double add(float v1, float v2) {
        return add(Float.toString(v1), Float.toString(v2)).doubleValue();
    }

    public static double add(float v1, double v2) {
        return add(Float.toString(v1), Double.toString(v2)).doubleValue();
    }

    public static double add(double v1, float v2) {
        return add(Double.toString(v1), Float.toString(v2)).doubleValue();
    }

    public static double add(double v1, double v2) {
        return add(Double.toString(v1), Double.toString(v2)).doubleValue();
    }

    public static double add(Double v1, Double v2) {
        return add(v1, (Number)v2).doubleValue();
    }

    public static BigDecimal add(Number v1, Number v2) {
        return toBigDecimal(v1).add(toBigDecimal(v2));
    }

    public static BigDecimal add(Number... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        } else {
            Number value = values[0];
            BigDecimal result = null == value ? BigDecimal.ZERO : new BigDecimal(value.toString());

            for(int i = 1; i < values.length; ++i) {
                value = values[i];
                if (null != value) {
                    result = result.add(new BigDecimal(value.toString()));
                }
            }

            return result;
        }
    }

    public static BigDecimal add(String... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        } else {
            String value = values[0];
            BigDecimal result = null == value ? BigDecimal.ZERO : new BigDecimal(value);

            for(int i = 1; i < values.length; ++i) {
                value = values[i];
                if (null != value) {
                    result = result.add(new BigDecimal(value));
                }
            }

            return result;
        }
    }

    public static BigDecimal add(BigDecimal... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        } else {
            BigDecimal value = values[0];
            BigDecimal result = null == value ? BigDecimal.ZERO : value;

            for(int i = 1; i < values.length; ++i) {
                value = values[i];
                if (null != value) {
                    result = result.add(value);
                }
            }

            return result;
        }
    }

    public static double sub(float v1, float v2) {
        return sub(Float.toString(v1), Float.toString(v2)).doubleValue();
    }

    public static double sub(float v1, double v2) {
        return sub(Float.toString(v1), Double.toString(v2)).doubleValue();
    }

    public static double sub(double v1, float v2) {
        return sub(Double.toString(v1), Float.toString(v2)).doubleValue();
    }

    public static double sub(double v1, double v2) {
        return sub(Double.toString(v1), Double.toString(v2)).doubleValue();
    }

    public static double sub(Double v1, Double v2) {
        return sub(v1, (Number)v2).doubleValue();
    }

    public static BigDecimal sub(Number v1, Number v2) {
//        return sub(v1, v2);
        return toBigDecimal(v1).subtract(toBigDecimal(v2));
    }

    public static BigDecimal sub(Number... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        } else {
            Number value = values[0];
            BigDecimal result = null == value ? BigDecimal.ZERO : new BigDecimal(value.toString());

            for(int i = 1; i < values.length; ++i) {
                value = values[i];
                if (null != value) {
                    result = result.subtract(new BigDecimal(value.toString()));
                }
            }

            return result;
        }
    }

    public static BigDecimal sub(String... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        } else {
            String value = values[0];
            BigDecimal result = null == value ? BigDecimal.ZERO : new BigDecimal(value);

            for(int i = 1; i < values.length; ++i) {
                value = values[i];
                if (null != value) {
                    result = result.subtract(new BigDecimal(value));
                }
            }

            return result;
        }
    }

    public static BigDecimal sub(BigDecimal... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        } else {
            BigDecimal value = values[0];
            BigDecimal result = null == value ? BigDecimal.ZERO : value;

            for(int i = 1; i < values.length; ++i) {
                value = values[i];
                if (null != value) {
                    result = result.subtract(value);
                }
            }

            return result;
        }
    }

    public static double mul(float v1, float v2) {
        return mul(Float.toString(v1), Float.toString(v2)).doubleValue();
    }

    public static double mul(float v1, double v2) {
        return mul(Float.toString(v1), Double.toString(v2)).doubleValue();
    }

    public static double mul(double v1, float v2) {
        return mul(Double.toString(v1), Float.toString(v2)).doubleValue();
    }

    public static double mul(double v1, double v2) {
        return mul(Double.toString(v1), Double.toString(v2)).doubleValue();
    }

    public static double mul(Double v1, Double v2) {
        return mul(v1, (Number)v2).doubleValue();
    }

    public static BigDecimal mul(Number v1, Number v2) {
        return mul(new Number[]{v1, v2});
    }

    public static BigDecimal mul(Number... values) {
        if (!ArrayUtil.isEmpty(values) && !ArrayUtil.hasNull(values)) {
            Number value = values[0];
            BigDecimal result = new BigDecimal(value.toString());

            for(int i = 1; i < values.length; ++i) {
                value = values[i];
                result = result.multiply(new BigDecimal(value.toString()));
            }

            return result;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal mul(String v1, String v2) {
        return mul(new BigDecimal(v1), new BigDecimal(v2));
    }

    public static BigDecimal mul(String... values) {
        if (!ArrayUtil.isEmpty(values) && !ArrayUtil.hasNull(values)) {
            BigDecimal result = new BigDecimal(values[0]);

            for(int i = 1; i < values.length; ++i) {
                result = result.multiply(new BigDecimal(values[i]));
            }

            return result;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal mul(BigDecimal... values) {
        if (!ArrayUtil.isEmpty(values) && !ArrayUtil.hasNull(values)) {
            BigDecimal result = values[0];

            for(int i = 1; i < values.length; ++i) {
                result = result.multiply(values[i]);
            }

            return result;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static double div(float v1, float v2) {
        return div(v1, v2, 10);
    }

    public static double div(float v1, double v2) {
        return div(v1, v2, 10);
    }

    public static double div(double v1, float v2) {
        return div(v1, v2, 10);
    }

    public static double div(double v1, double v2) {
        return div(v1, v2, 10);
    }

    public static double div(Double v1, Double v2) {
        return div(v1, v2, 10);
    }

    public static BigDecimal div(Number v1, Number v2) {
        return div(v1, v2, 10);
    }

    public static BigDecimal div(String v1, String v2) {
        return div(v1, v2, 10);
    }

    public static double div(float v1, float v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    public static double div(float v1, double v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    public static double div(double v1, float v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    public static double div(double v1, double v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    public static double div(Double v1, Double v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal div(Number v1, Number v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal div(String v1, String v2, int scale) {
        return div(v1, v2, scale, RoundingMode.HALF_UP);
    }

    public static double div(float v1, float v2, int scale, RoundingMode roundingMode) {
        return div(Float.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
    }

    public static double div(float v1, double v2, int scale, RoundingMode roundingMode) {
        return div(Float.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
    }

    public static double div(double v1, float v2, int scale, RoundingMode roundingMode) {
        return div(Double.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
    }

    public static double div(double v1, double v2, int scale, RoundingMode roundingMode) {
        return div(Double.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
    }

    public static double div(Double v1, Double v2, int scale, RoundingMode roundingMode) {
        return div(v1, (Number)v2, scale, roundingMode).doubleValue();
    }

    public static BigDecimal div(Number v1, Number v2, int scale, RoundingMode roundingMode) {
        return div(v1.toString(), v2.toString(), scale, roundingMode);
    }

    public static BigDecimal div(String v1, String v2, int scale, RoundingMode roundingMode) {
        return div(new BigDecimal(v1), new BigDecimal(v2), scale, roundingMode);
    }

    public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale, RoundingMode roundingMode) {
        
        if (null == v1) {
            return BigDecimal.ZERO;
        } else {
            if (scale < 0) {
                scale = -scale;
            }

            return v1.divide(v2, scale, roundingMode);
        }
    }

    public static int ceilDiv(int v1, int v2) {
        return (int)Math.ceil((double)v1 / (double)v2);
    }

    public static BigDecimal round(double v, int scale) {
        return round(v, scale, RoundingMode.HALF_UP);
    }

    public static String roundStr(double v, int scale) {
        return round(v, scale).toString();
    }

    public static BigDecimal round(String numberStr, int scale) {
        return round(numberStr, scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal round(BigDecimal number, int scale) {
        return round(number, scale, RoundingMode.HALF_UP);
    }

    public static String roundStr(String numberStr, int scale) {
        return round(numberStr, scale).toString();
    }

    public static BigDecimal round(double v, int scale, RoundingMode roundingMode) {
        return round(Double.toString(v), scale, roundingMode);
    }

    public static String roundStr(double v, int scale, RoundingMode roundingMode) {
        return round(v, scale, roundingMode).toString();
    }

    public static BigDecimal round(String numberStr, int scale, RoundingMode roundingMode) {
        
        if (scale < 0) {
            scale = 0;
        }

        return round(toBigDecimal(numberStr), scale, roundingMode);
    }

    public static BigDecimal round(BigDecimal number, int scale, RoundingMode roundingMode) {
        if (null == number) {
            number = BigDecimal.ZERO;
        }

        if (scale < 0) {
            scale = 0;
        }

        if (null == roundingMode) {
            roundingMode = RoundingMode.HALF_UP;
        }

        return number.setScale(scale, roundingMode);
    }

    public static String roundStr(String numberStr, int scale, RoundingMode roundingMode) {
        return round(numberStr, scale, roundingMode).toString();
    }

    public static BigDecimal roundHalfEven(Number number, int scale) {
        return roundHalfEven(toBigDecimal(number), scale);
    }

    public static BigDecimal roundHalfEven(BigDecimal value, int scale) {
        return round(value, scale, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal roundDown(Number number, int scale) {
        return roundDown(toBigDecimal(number), scale);
    }

    public static BigDecimal roundDown(BigDecimal value, int scale) {
        return round(value, scale, RoundingMode.DOWN);
    }

    public static String decimalFormat(String pattern, double value) {
        return (new DecimalFormat(pattern)).format(value);
    }

    public static String decimalFormat(String pattern, long value) {
        return (new DecimalFormat(pattern)).format(value);
    }

    public static String decimalFormat(String pattern, Object value) {
        return (new DecimalFormat(pattern)).format(value);
    }

    public static String decimalFormatMoney(double value) {
        return decimalFormat(",##0.00", value);
    }

    public static String formatPercent(double number, int scale) {
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(scale);
        return format.format(number);
    }

    public static boolean isNumber(CharSequence str) {
        if (StrUtil.isBlank(str)) {
            return false;
        } else {
            char[] chars = str.toString().toCharArray();
            int sz = chars.length;
            boolean hasExp = false;
            boolean hasDecPoint = false;
            boolean allowSigns = false;
            boolean foundDigit = false;
            int start = chars[0] != '-' && chars[0] != '+' ? 0 : 1;
            int i;
            if (sz > start + 1 && chars[start] == '0' && (chars[start + 1] == 'x' || chars[start + 1] == 'X')) {
                i = start + 2;
                if (i == sz) {
                    return false;
                } else {
                    while(i < chars.length) {
                        if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
                            return false;
                        }

                        ++i;
                    }

                    return true;
                }
            } else {
                --sz;

                for(i = start; i < sz || i < sz + 1 && allowSigns && !foundDigit; ++i) {
                    if (chars[i] >= '0' && chars[i] <= '9') {
                        foundDigit = true;
                        allowSigns = false;
                    } else if (chars[i] == '.') {
                        if (hasDecPoint || hasExp) {
                            return false;
                        }

                        hasDecPoint = true;
                    } else if (chars[i] != 'e' && chars[i] != 'E') {
                        if (chars[i] != '+' && chars[i] != '-') {
                            return false;
                        }

                        if (!allowSigns) {
                            return false;
                        }

                        allowSigns = false;
                        foundDigit = false;
                    } else {
                        if (hasExp) {
                            return false;
                        }

                        if (!foundDigit) {
                            return false;
                        }

                        hasExp = true;
                        allowSigns = true;
                    }
                }

                if (i < chars.length) {
                    if (chars[i] >= '0' && chars[i] <= '9') {
                        return true;
                    } else if (chars[i] != 'e' && chars[i] != 'E') {
                        if (chars[i] == '.') {
                            return !hasDecPoint && !hasExp && foundDigit;
                        } else if (allowSigns || chars[i] != 'd' && chars[i] != 'D' && chars[i] != 'f' && chars[i] != 'F') {
                            if (chars[i] != 'l' && chars[i] != 'L') {
                                return false;
                            } else {
                                return foundDigit && !hasExp;
                            }
                        } else {
                            return foundDigit;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return !allowSigns && foundDigit;
                }
            }
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return s.contains(".");
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static boolean isPrimes(int n) {
        

        for(int i = 2; (double)i <= Math.sqrt(n); ++i) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }
    public static Integer[] generateBySet(int begin, int end, int size) {
        if (begin > end) {
            int temp = begin;
            begin = end;
            end = temp;
        }

        if (end - begin < size) {
            throw new RuntimeException("Size is larger than range between begin and end!");
        } else {
            Random ran = new Random();
            Set<Integer> set = new HashSet<>();

            while(set.size() < size) {
                set.add(begin + ran.nextInt(end - begin));
            }

            return set.toArray(new Integer[size]);
        }
    }

    public static int[] range(int stop) {
        return range(0, stop);
    }

    public static int[] range(int start, int stop) {
        return range(start, stop, 1);
    }

    public static int[] range(int start, int stop, int step) {
        if (start < stop) {
            step = Math.abs(step);
        } else {
            if (start <= stop) {
                return new int[]{start};
            }

            step = -Math.abs(step);
        }

        int size = Math.abs((stop - start) / step) + 1;
        int[] values = new int[size];
        int index = 0;
        int i = start;

        while(true) {
            if (step > 0) {
                if (i > stop) {
                    break;
                }
            } else if (i < stop) {
                break;
            }

            values[index] = i;
            ++index;
            i += step;
        }

        return values;
    }

    public static Collection<Integer> appendRange(int start, int stop, Collection<Integer> values) {
        return appendRange(start, stop, 1, values);
    }

    public static Collection<Integer> appendRange(int start, int stop, int step, Collection<Integer> values) {
        if (start < stop) {
            step = Math.abs(step);
        } else {
            if (start <= stop) {
                values.add(start);
                return values;
            }

            step = -Math.abs(step);
        }

        int i = start;

        while(true) {
            if (step > 0) {
                if (i > stop) {
                    break;
                }
            } else if (i < stop) {
                break;
            }

            values.add(i);
            i += step;
        }

        return values;
    }
    public static int processMultiple(int selectNum, int minNum) {
        return mathSubNode(selectNum, minNum) / mathNode(selectNum - minNum);
    }

    public static int divisor(int m, int n) {
        while(m % n != 0) {
            int temp = m % n;
            m = n;
            n = temp;
        }

        return n;
    }

    public static int multiple(int m, int n) {
        return m * n / divisor(m, n);
    }

    public static String getBinaryStr(Number number) {
        if (number instanceof Long) {
            return Long.toBinaryString((Long)number);
        } else {
            return number instanceof Integer ? Integer.toBinaryString((Integer)number) : Long.toBinaryString(number.longValue());
        }
    }

    public static int binaryToInt(String binaryStr) {
        return Integer.parseInt(binaryStr, 2);
    }

    public static long binaryToLong(String binaryStr) {
        return Long.parseLong(binaryStr, 2);
    }

    public static int compare(char x, char y) {
        return x - y;
    }

    public static int compare(double x, double y) {
        return Double.compare(x, y);
    }

    public static int compare(int x, int y) {
        return Integer.compare(x, y);
    }

    public static int compare(long x, long y) {
        return Long.compare(x, y);
    }

    public static int compare(short x, short y) {
        return Short.compare(x, y);
    }

    public static int compare(byte x, byte y) {
        return Byte.compare(x, y);
    }

    public static boolean isGreater(BigDecimal bigNum1, BigDecimal bigNum2) {
        
        
        return bigNum1.compareTo(bigNum2) > 0;
    }

    public static boolean isGreaterOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
        
        
        return bigNum1.compareTo(bigNum2) >= 0;
    }

    public static boolean isLess(BigDecimal bigNum1, BigDecimal bigNum2) {
        
        
        return bigNum1.compareTo(bigNum2) < 0;
    }

    public static boolean isLessOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
        
        
        return bigNum1.compareTo(bigNum2) <= 0;
    }

    public static boolean equals(double num1, double num2) {
        return Double.doubleToLongBits(num1) == Double.doubleToLongBits(num2);
    }

    public static boolean equals(float num1, float num2) {
        return Float.floatToIntBits(num1) == Float.floatToIntBits(num2);
    }

    public static boolean equals(BigDecimal bigNum1, BigDecimal bigNum2) {
        if (bigNum1.equals(bigNum2)) {
            return true;
        } else if (bigNum2 != null) {
            return 0 == bigNum1.compareTo(bigNum2);
        } else {
            return false;
        }
    }

    public static String toStr(Number number, String defaultValue) {
        return null == number ? defaultValue : toStr(number);
    }

    public static String toStr(Number number) {
        
        if (number instanceof BigDecimal) {
            return toStr((BigDecimal)number);
        } else {
            
            String string = number.toString();
            if (string.indexOf(46) > 0 && string.indexOf(101) < 0 && string.indexOf(69) < 0) {
                while(string.endsWith("0")) {
                    string = string.substring(0, string.length() - 1);
                }

                if (string.endsWith(".")) {
                    string = string.substring(0, string.length() - 1);
                }
            }

            return string;
        }
    }

    public static String toStr(BigDecimal bigDecimal) {
        
        return bigDecimal.stripTrailingZeros().toPlainString();
    }

    public static BigDecimal toBigDecimal(Number number) {
        if (null == number) {
            return BigDecimal.ZERO;
        } else if (number instanceof BigDecimal) {
            return (BigDecimal)number;
        } else if (number instanceof Long) {
            return new BigDecimal((Long)number);
        } else if (number instanceof Integer) {
            return new BigDecimal((Integer)number);
        } else {
            return number instanceof BigInteger ? new BigDecimal((BigInteger)number) : toBigDecimal(number.toString());
        }
    }

    public static BigDecimal toBigDecimal(String number) {
        try {
            number = parseNumber(number).toString();
        } catch (Exception ignored) {
        }

        return StrUtil.isBlank(number) ? BigDecimal.ZERO : new BigDecimal(number);
    }

    public static BigInteger toBigInteger(Number number) {
        if (null == number) {
            return BigInteger.ZERO;
        } else if (number instanceof BigInteger) {
            return (BigInteger)number;
        } else {
            return number instanceof Long ? BigInteger.valueOf((Long)number) : toBigInteger(number.longValue());
        }
    }

    public static BigInteger toBigInteger(String number) {
        return StrUtil.isBlank(number) ? BigInteger.ZERO : new BigInteger(number);
    }

    /** @deprecated */
    @Deprecated
    public static boolean isBlankChar(char c) {
        return isBlankChar((int)c);
    }

    /** @deprecated */
    @Deprecated
    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == 65279 || c == 8234;
    }

    public static int count(int total, int part) {
        return total % part == 0 ? total / part : total / part + 1;
    }

    public static BigDecimal null2Zero(BigDecimal decimal) {
        return decimal == null ? BigDecimal.ZERO : decimal;
    }

    public static int zero2One(int value) {
        return 0 == value ? 1 : value;
    }

    public static BigInteger newBigInteger(String str) {
        str = StrUtil.trimToNull(str);
        if (null == str) {
            return null;
        } else {
            int pos = 0;
            int radix = 10;
            boolean negate = false;
            if (str.startsWith("-")) {
                negate = true;
                pos = 1;
            }

            if (!str.startsWith("0x", pos) && !str.startsWith("0X", pos)) {
                if (str.startsWith("#", pos)) {
                    radix = 16;
                    ++pos;
                } else if (str.startsWith("0", pos) && str.length() > pos + 1) {
                    radix = 8;
                    ++pos;
                }
            } else {
                radix = 16;
                pos += 2;
            }

            if (pos > 0) {
                str = str.substring(pos);
            }

            BigInteger value = new BigInteger(str, radix);
            return negate ? value.negate() : value;
        }
    }

    public static boolean isBeside(long number1, long number2) {
        return Math.abs(number1 - number2) == 1L;
    }

    public static boolean isBeside(int number1, int number2) {
        return Math.abs(number1 - number2) == 1;
    }

    public static int partValue(int total, int partCount) {
        return partValue(total, partCount, true);
    }

    public static int partValue(int total, int partCount, boolean isPlusOneWhenHasRem) {
        int partValue = total / partCount;
        if (isPlusOneWhenHasRem && total % partCount > 0) {
            ++partValue;
        }

        return partValue;
    }

    public static BigDecimal pow(Number number, int n) {
        return pow(toBigDecimal(number), n);
    }

    public static BigDecimal pow(BigDecimal number, int n) {
        return number.pow(n);
    }

    public static boolean isPowerOfTwo(long n) {
        return n > 0L && (n & n - 1L) == 0L;
    }

    public static long parseLong(String number) {
        if (StrUtil.isBlank(number)) {
            return 0L;
        } else if (number.startsWith("0x")) {
            return Long.parseLong(number.substring(2), 16);
        } else {
            try {
                return Long.parseLong(number);
            } catch (NumberFormatException var2) {
                return parseNumber(number).longValue();
            }
        }
    }

    public static float parseFloat(String number) {
        if (StrUtil.isBlank(number)) {
            return 0.0F;
        } else {
            try {
                return Float.parseFloat(number);
            } catch (NumberFormatException var2) {
                return parseNumber(number).floatValue();
            }
        }
    }

    public static double parseDouble(String number) {
        if (StrUtil.isBlank(number)) {
            return 0.0;
        } else {
            try {
                return Double.parseDouble(number);
            } catch (NumberFormatException var2) {
                return parseNumber(number).doubleValue();
            }
        }
    }

    public static Number parseNumber(String numberStr) throws NumberFormatException {
        try {
            return NumberFormat.getInstance().parse(numberStr);
        } catch (ParseException var3) {
            NumberFormatException nfe = new NumberFormatException(var3.getMessage());
            nfe.initCause(var3);
            throw nfe;
        }
    }

    public static byte[] toBytes(int value) {
        return new byte[]{(byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)value};
    }

    public static int toInt(byte[] bytes) {
        return (bytes[0] & 255) << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8 | bytes[3] & 255;
    }

    public static byte[] toUnsignedByteArray(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes[0] == 0) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            return tmp;
        } else {
            return bytes;
        }
    }

    public static byte[] toUnsignedByteArray(int length, BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length == length) {
            return bytes;
        } else {
            int start = bytes[0] == 0 ? 1 : 0;
            int count = bytes.length - start;
            if (count > length) {
                throw new IllegalArgumentException("standard length exceeded for value");
            } else {
                byte[] tmp = new byte[length];
                System.arraycopy(bytes, start, tmp, tmp.length - count, count);
                return tmp;
            }
        }
    }

    public static BigInteger fromUnsignedByteArray(byte[] buf) {
        return new BigInteger(1, buf);
    }

    public static BigInteger fromUnsignedByteArray(byte[] buf, int off, int length) {
        byte[] mag = buf;
        if (off != 0 || length != buf.length) {
            mag = new byte[length];
            System.arraycopy(buf, off, mag, 0, length);
        }

        return new BigInteger(1, mag);
    }

    public static boolean isValidNumber(Number number) {
        if (number instanceof Double) {
            return !((Double)number).isInfinite() && !((Double)number).isNaN();
        } else if (!(number instanceof Float)) {
            return true;
        } else {
            return !((Float)number).isInfinite() && !((Float)number).isNaN();
        }
    }

    private static int mathSubNode(int selectNum, int minNum) {
        return selectNum == minNum ? 1 : selectNum * mathSubNode(selectNum - 1, minNum);
    }

    private static int mathNode(int selectNum) {
        return selectNum == 0 ? 1 : selectNum * mathNode(selectNum - 1);
    }
}
