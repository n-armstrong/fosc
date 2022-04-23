/* ------------------------------------------------------------------------------------------------------------
• FoscLilyPondComment (abjad 3.0)

LilyPond comment.


• Example 1

a = FoscNote(60, 1/4);
m = FoscLilyPondComment("a comment");
a.attach(m);
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscLilyPondComment : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <allowableFormatSlots;
    var <canAttachToContainers=true, <formatLeafChildren=false;
    var <string, <formatSlot;
    *initClass {
        allowableFormatSlots = #['after', 'before', 'closing', 'opening'];
    }
    *new { |string, formatSlot='before'|
        if (string.isKindOf(FoscLilyPondComment)) {
            string = string.contentsString;
            formatSlot = string.contentsString;
        } {
            string = string.asString;
        };
        assert(string.isString);
        assert(
            allowableFormatSlots.includes(formatSlot.asSymbol),
            thisMethod,
            "'formatSlot' must be one of: %."
                .format(formatSlot, "".ccatList(allowableFormatSlots)[1..]);
        );
        ^super.new.init(string, formatSlot);
    }
    init { |argString, argFormatSlot|
        string = argString;
        formatSlot = argFormatSlot;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • formatSlot

    Format slot of LilyPond comment.


    • Example 1

    m = FoscLilyPondComment("a comment");
    m.formatSlot;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • string

    Gets string.


    • Example 1

    m = FoscLilyPondComment("a comment");
    m.string;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •  str

    Gets string representation of LilyPond comment.


    • Example 1

    m = FoscLilyPondComment("a comment");
    m.str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^("% " ++ string);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, formatSlot;
        bundle = FoscLilyPondFormatBundle();
        formatSlot = bundle.perform(this.formatSlot);
        formatSlot.comments.add(this.prGetLilypondFormat);
        ^bundle;  
    }
}
