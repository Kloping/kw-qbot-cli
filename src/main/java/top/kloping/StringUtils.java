package top.kloping;

public class StringUtils {
    /**
     * 将中文字符串补充至4位，不足部分用全角空格填充在右侧
     *
     * @param input 原始字符串
     * @return 补充后的字符串（长度4）
     */
    public static String padChineseString(String input) {
        return padChineseString(input, 4);
    }

    public static String padChineseString(String input, int l0) {
        if (input == null) return null;

        int length = input.length();
        if (length >= l0) return input;

        StringBuilder sb = new StringBuilder(input);
        for (int i = 0; i < l0 - length; i++) {
            sb.append('　'); // 追加全角空格（Unicode \u3000）
        }
        return sb.toString();
    }

    private static final int l0 = 4;

    public static String padNumberString(Integer n) {
        String input = String.valueOf(n);
        if (input == null) return null;
        int length = input.length();
        if (length >= l0) return input;

        return " ".repeat(l0 - length) + input;
    }
}