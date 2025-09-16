package com.z.framework.monitor.web.util;

public class StrUtil {
    public static boolean isBlank(CharSequence str) {
        int length;
        if (str != null && (length = str.length()) != 0) {
            for (int i = 0; i < length; ++i) {
                if (!isBlankChar(str.charAt(i))) {
                    return false;
                }
            }

        }
        return true;
    }

    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == 65279 || c == 8234;
    }

    public static String trimToNull(CharSequence str) {
        String trimStr = trim(str);
        return "".equals(trimStr) ? null : trimStr;
    }

    public static String trim(CharSequence str) {
        return null == str ? null : trim(str, 0);
    }

    public static String trim(CharSequence str, int mode) {
        String result;
        if (str == null) {
            result = null;
        } else {
            int length = str.length();
            int start = 0;
            int end = length;
            if (mode <= 0) {
                while (start < end && isBlankChar(str.charAt(start))) {
                    ++start;
                }
            }

            if (mode >= 0) {
                while (start < end && isBlankChar(str.charAt(end - 1))) {
                    --end;
                }
            }

            if (start <= 0 && end >= length) {
                result = str.toString();
            } else {
                result = str.toString().substring(start, end);
            }
        }

        return result;
    }
}
