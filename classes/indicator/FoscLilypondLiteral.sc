/* ------------------------------------------------------------------------------------------------------------
• FoscLilyPondLiteral (abjad 3.0)

LilyPond literal.


• Example 1

Dotted slur.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
m = FoscSlur();
a[0..].attach(m);
l = FoscLilyPondLiteral("\\slurDotted");
a[0].attach(l);
a.show;


• Example 2

Use the absolute before and absolute after format slots like this.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
l = FoscLilyPondLiteral("% before all formatting", formatSlot: 'absoluteBefore');
a[0].attach(l);
l = FoscLilyPondLiteral("% after all formatting", formatSlot: 'absoluteAfter');
a[3].attach(l);
a.format;


• Example 3 //!!!TODO: tags not yet implemented

Lilypond literas can be tagged.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
m = FoscSlur();
a[0..].attach(m);
l = FoscLilyPondLiteral("\\slurDotted");
a[0].attach(l);
a.format;


• Example 4

Multi-line input is allowed.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
l = FoscLilyPondLiteral(#[
    "\\stopStaff",
    "\\startStaff",
    "\\once \\override Staff.StaffSymbol.transparent = ##t"
]);
a[2].attach(l);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscLilyPondLiteral : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <allowableFormatSlots;
    var <canAttachToContainers=true, <formatLeafChildren=false;
    var <string, <directed, <formatSlot, <tweaks;
    *initClass {
        allowableFormatSlots = #[
            'absoluteAfter',
            'absoluteBefore',
            'after',
            'before',
            'closing',
            'opening',
            'right'
        ];
    }
    *new { |string, formatSlot='opening', directed, tweaks|
        assert(
            [Symbol, String, SequenceableCollection].any { |type| string.isKindOf(type) },
            "FoscLilyPondLiteral:new: string is not valid: %.".format(string);
        );
        assert(
            allowableFormatSlots.includes(formatSlot),
            "FoscLilyPondLiteral:new: format slot is not valid: %.".format(formatSlot);
        );
        assert(
            [FoscLilyPondTweakManager, SequenceableCollection, Nil].any { |type| tweaks.isKindOf(type) },
            "FoscLilyPondLiteral:new: tweaks must be a FoscLilyPondTweakManager or Array: %.".format(tweaks);
        );
        ^super.new.init(string, formatSlot, directed, tweaks);
    }
    init { |argObject, argFormatSlot, argDirected, argTweaks|
        string = argObject;
        formatSlot = argFormatSlot;
        directed = argDirected;
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *allowableFormatSlots

    Lists allowable format slots.
    
    FoscLilyPondLiteral.allowableFormatSlots;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • directed

    Is true when literal is directed.


    • Example 1

    Directed literal.

    l = FoscLilyPondLiteral("\\f", 'after', directed: true);
    l.directed;


    • Example 2

    Nondirected literal.

    l = FoscLilyPondLiteral("\\breathe", 'after', directed: false);
    l.directed;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • formatSlot
    
    Gets format slot of LilyPond literal.

    Returns string.

    
    • Example 1
    
    a = FoscLilyPondLiteral(\slurDotted);
    a.formatSlot;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • string

    Gets LilyPond literal string.


    • Example 1

    l = FoscLilyPondLiteral("\\slurDotted");
    l.string;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.
    
    
    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    l = FoscLilyPondLiteral("\\f", 'after', directed: true, tweaks: #[['color', 'red']]);
    a[0].attach(l);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats Lilypond literal.
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^string.asString;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatPieces
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatPieces {
        if ([Symbol, String].any { |type| string.isKindOf(type) }) { ^[string] };
        assert(string.isSequenceableCollection);
        ^string;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle {
        var bundle, formatSlot, localTweaks, pieces;
        bundle = FoscLilyPondFormatBundle();
        formatSlot = bundle.perform(this.formatSlot);
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions(directed: directed);
            formatSlot.commands.addAll(localTweaks);
        };
        pieces = this.prGetFormatPieces;
        formatSlot.commands.addAll(pieces);
        ^bundle;  
    }
}
