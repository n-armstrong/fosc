/* ------------------------------------------------------------------------------------------------------------
• FoscLilypondComment (abjad 3.0)

LilyPond comment.


• Example 1

a = FoscNote(60, 1/4);
m = FoscLilypondComment("a comment");
a.attach(m);
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscLilypondComment : FoscObject {
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
        if (string.isKindOf(FoscLilypondComment)) {
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

    m = FoscLilypondComment("a comment");
    m.formatSlot;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • string

    Gets string.


    • Example 1

    m = FoscLilypondComment("a comment");
    m.string;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •  str

    Gets string representation of LilyPond comment.


    • Example 1

    m = FoscLilypondComment("a comment");
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
        bundle = FoscLilypondFormatBundle();
        formatSlot = bundle.perform(this.formatSlot);
        formatSlot.comments.add(this.prGetLilypondFormat);
        ^bundle;  
    }
}
