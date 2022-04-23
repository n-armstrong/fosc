/* ------------------------------------------------------------------------------------------------------------
• FoscLilyPondContextSetting (abjad 3.0)

LilyPond context setting.


• Example 1

a = FoscLilyPondContextSetting('Score', 'autoBeaming', value: true);
a.formatPieces.join("\n");



a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
a = FoscScore([a]);
m = FoscRehearsalMark(number: 1);
a[0][0].attach(m);
z = set(a).markFormatter = FoscScheme('format-mark-box-alphabet');
a.show;

z.prAttributeTuples;
------------------------------------------------------------------------------------------------------------ */
FoscLilyPondContextSetting : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <lilypondType, <contextProperty, <isUnset, <value;
    var <formatLeafChildren=false;
    *new { |lilypondType, contextProperty='autoBeaming', isUnset=false, value=false|
        ^super.new.init(lilypondType, contextProperty, isUnset, value);
    }
    init { |argLilypondType, argContextProperty, argIsUnset, argValue|
        if (argLilypondType.notNil) { argLilypondType = argLilypondType.asSymbol };
        lilypondType = argLilypondType;
        contextProperty = argContextProperty.asSymbol;
        isUnset = argIsUnset;
        value = argValue;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • lilypondType

    Gets LilyPond type.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • name

    Optional LilyPond context name.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • contextProperty

    Gets LilyPond context property name.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • formatPieces

    Gets LilyPond context setting '\set' or '\unset' format pieces.
    -------------------------------------------------------------------------------------------------------- */
    formatPieces {
        var result, string, valuePieces;
        result = [];
        if (isUnset.not) {
            result = result.add("\\set");
        } {
            result = result.add("\\unset");
        };
        if (lilypondType.notNil) {
            string = "%.%".format(lilypondType, contextProperty);
            result = result.add(string);
        } {
            result = result.add(contextProperty);
        };
        result = result.add("=");
        valuePieces = FoscScheme.formatEmbeddedSchemeValue(value);
        valuePieces = valuePieces.split("\n");
        result = result.add(valuePieces[0]);
        result = [result.join(" ")];
        result = result.addAll(valuePieces[1..]);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isUnset

    Is true if context setting unsets its value. Otherwise false.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • value

    Value of LilyPond context setting.
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • == !!!TODO

    Is true when 'argument' is a LilyPond context setting with equivalent keyword values.
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle {
        var bundle, string;
        bundle = FoscLilyPondFormatBundle();
        string = this.formatPieces.join("\n");
        bundle.contextSettings.add(string);
        ^bundle;
    }
}
