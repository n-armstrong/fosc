/* ---------------------------------------------------------------------------------------------------------
• FoscMultimeasureRest

A multimeasure rest.


• Example 1

a = FoscMultimeasureRest(3/4);
a.format;


• Example 2

a = FoscMultimeasureRest(4/4, multiplier: 4);
a.show;


• Example 3

Use a FoscLilyPondLiteral to compress full-bar rests;

a = FoscStaff([FoscMultimeasureRest(4/4, multiplier: 4)]);
a.leafAt(0).attach(FoscLilyPondLiteral("\\compressFullBarRests"));
a.show;
--------------------------------------------------------------------------------------------------------- */
FoscMultimeasureRest : FoscLeaf {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *new { |writtenDuration, multiplier, tag|
        writtenDuration = writtenDuration ?? { FoscDuration(1, 4) };
        ^super.new(writtenDuration, multiplier, tag);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetBody

    Gets list of string representation of body of rest. Picked up as format contribution at format-time.
    -------------------------------------------------------------------------------------------------------- */
    prGetBody {
        ^[this.prGetCompactRepresentation];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetCompactRepresentation
    -------------------------------------------------------------------------------------------------------- */
    prGetCompactRepresentation {
        ^"R%".format(this.prGetFormattedDuration);
    }
}
