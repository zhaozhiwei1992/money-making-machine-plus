package com.z.module.report.service.checkrule.trans;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MaxFormulaTransformer implements CheckFormulaTransformer{
    @Override
    public String transform(String expression, Map<String, String> formatMapABC) {
        if (!expression.contains("MAX")) {
            return expression;
        }
        StringBuilder result = new StringBuilder();

        Pattern sumPattern = Pattern.compile("MAX（(.*?)）");
        Matcher matcher = sumPattern.matcher(expression);
        // 如果找到匹配项
        while (matcher.find()) {
            String cellRange = matcher.group(1);
            final List<String> expandedCells = expandCellRanges(cellRange, formatMapABC);
            matcher.appendReplacement(result, "#max（" + String.join(",", expandedCells) + "）");
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public List<String> expandCellRanges(String cellRanges, Map<String, String> cellMap) {
        List<String> expandedCells = new ArrayList<>();

        // 分割不连续范围，使用逗号","作为分隔符
        String[] rangeArray = cellRanges.split(",");
        for (String range : rangeArray) {
            expandedCells.addAll(expandCellRange(range.trim(), cellMap));
        }

        return expandedCells;
    }

    /**
     * @data: 2023/7/18-下午5:31
     * @User: zhao
     * @method: expandCellRange
     * @param cellRange :
     * @return: java.util.List<java.lang.String>
     * @Description: 公式展开
     * SUM(E7:E15,E22) ==> SUM(E7,E8,E9,E10,E11,E12,E13,E14,E15,E22)
     */
    public List<String> expandCellRange(String cellRange, Map<String, String> cellMap) {
        List<String> expandedCells = new ArrayList<>();

        // 分割参数，使用冒号":"作为分隔符
        String[] rangeParts = cellRange.split(":");
        if (rangeParts.length == 1) {
            // 单个单元格
            String cellRef = rangeParts[0];
            String mappedCell = cellMap.getOrDefault(cellRef, cellRef);
            expandedCells.add(mappedCell);
        }else if (rangeParts.length == 2) {
            String startCell = rangeParts[0];
            String endCell = rangeParts[1];

            // 提取起始单元格的列和行信息
            String startColumn = startCell.replaceAll("\\d", "");
            int startRow = Integer.parseInt(startCell.replaceAll("\\D", ""));

            // 提取结束单元格的列和行信息
            String endColumn = endCell.replaceAll("\\d", "");
            int endRow = Integer.parseInt(endCell.replaceAll("\\D", ""));

            // 展开单元格范围并添加到列表中
            for (int row = startRow; row <= endRow; row++) {
                for (int col = columnToNumber(startColumn); col <= columnToNumber(endColumn); col++) {
                    String cellRef = numberToColumn(col) + row;
                    if(!Objects.isNull(cellMap)){
                        String mappedCell = cellMap.getOrDefault(cellRef, cellRef);
                        expandedCells.add(mappedCell);
                    }else{
                        expandedCells.add(cellRef);
                    }
                }
            }
        }else{
            throw new IllegalArgumentException("Invalid cell range format: " + cellRange);
        }

        return expandedCells;
    }

    public int columnToNumber(String column) {
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
    public String numberToColumn(int number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (number - 1) % 26;
            result.insert(0, (char) ('A' + remainder));
            number = (number - 1) / 26;
        }
        return result.toString();
    }
}
