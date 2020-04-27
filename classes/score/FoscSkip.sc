/* ---------------------------------------------------------------------------------------------------------
• FoscSkip


• Example 1

a = FoscSkip(3/16);
a.format;


• Example 2

Implicit conversion of type when another leaf is passed as initialization argument. Indicators are preserved.

n = FoscNote(60, 3/16);
n.attach(FoscArticulation('>'));
a = FoscSkip(n);
a.format;


• Example 3

With a multiplier;

a = FoscSkip(1/8, multiplier: 5/8);
a.format;
--------------------------------------------------------------------------------------------------------- */
FoscSkip : FoscLeaf {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *new { |writtenDuration, multiplier, tag|
        var originalArgument, new;
        originalArgument = writtenDuration;
        if (originalArgument.isKindOf(FoscLeaf)) {
            writtenDuration = originalArgument.writtenDuration;
            multiplier = originalArgument.multiplier;
        };
        writtenDuration = writtenDuration ?? { FoscDuration(1, 4) };
        new = super.new(writtenDuration, multiplier, tag);
        if (originalArgument.isKindOf(FoscLeaf)) {
            new.prCopyOverrideAndSetFromLeaf(originalArgument);
        };
        ^new;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetBody

    
    FoscSkip(4/8).prGetBody;
    
    FoscSkip(5/8).prGetBody;    // unsassignable error

    a = FoscSkip(1/8, multiplier: 5/8);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    prGetBody {
        var result;
        result = [];
        result = result.add("s%".format(this.prGetFormattedDuration));
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetCompactRepresentation

    FoscSkip(4/8).prGetCompactRepresentation;
    -------------------------------------------------------------------------------------------------------- */
    prGetCompactRepresentation {
        ^"s%".format(this.prGetFormattedDuration);
    }
}
