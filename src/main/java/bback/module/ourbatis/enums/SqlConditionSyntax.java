package bback.module.ourbatis.enums;
public enum SqlConditionSyntax {
    EQ("= {0}")
    , NOT("<> {0}")
    , ALL_LIKE("like concat(''%'', {0}, ''%'')")
    , R_LIKE("like concat({0}, ''%'')")
    , L_LIKE("like concat(''%'', {0})")
    , IN("in {0}")
    , BIGGER(">= {0}")
    , SMALLER("<= {0}")
    , MORE("> {0}")
    , LESS("< {0}")
    ;

    private final String syntax;

    SqlConditionSyntax(String syntax) {
        this.syntax = syntax;
    }

    public String get() {
        return this.syntax;
    }
}
