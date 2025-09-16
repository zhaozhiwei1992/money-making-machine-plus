package com.z.framework.common.util.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {

    public String[] getMatcherValues(String value, String patternStr) {

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(value);

        List<String> matches = new ArrayList<>(5);
        while (matcher.find()) {
            String group = matcher.group();
            matches.add(group);
        }

        return matches.toArray(new String[0]);
    }

}
