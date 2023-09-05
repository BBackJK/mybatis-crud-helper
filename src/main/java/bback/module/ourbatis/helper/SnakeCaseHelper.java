package bback.module.ourbatis.helper;

public final class SnakeCaseHelper {

    public static final String CLASS_TYPE_WARNING = "getClassType() 메소드를 오버라이드 하여 default 로 'T' 타입을 return 해주세요.\n" + "public interface XDao extends OurbatisCrudHelper<X, Long> {\n" +
            "\t@Override\n" +
            "\tdefault Class<X> getClassType() {\n" +
            "\t\treturn X.class;\n" +
            "\t}\n" +
            "}";

    private SnakeCaseHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String translate(String input)
    {
        if (input == null) return input; // garbage in, garbage out
        int length = input.length();
        StringBuilder result = new StringBuilder(length * 2);
        int resultLength = 0;
        boolean wasPrevTranslated = false;
        for (int i = 0; i < length; i++)
        {
            char c = input.charAt(i);
            if (i > 0 || c != '_') // skip first starting underscore
            {
                if (Character.isUpperCase(c))
                {
                    if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_')
                    {
                        result.append('_');
                        resultLength++;
                    }
                    c = Character.toLowerCase(c);
                    wasPrevTranslated = true;
                }
                else
                {
                    wasPrevTranslated = false;
                }
                result.append(c);
                resultLength++;
            }
        }
        return resultLength > 0 ? result.toString() : input;
    }


}
