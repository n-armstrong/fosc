/* ------------------------------------------------------------------------------------------------------------
• FoscStopTrillSpan (abjad 3.0)
------------------------------------------------------------------------------------------------------------ */
FoscStopTrillSpan : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <leak, <rightBroken;
    var <context='Voice', <parameter='TRILL', <persistent=true, <publishStorageFormat=true;
    var <timeOrientation='right';
    *new { |leak=false, rightBroken=false|
        // assert(leak.isKindOf(Boolean));
        // assert(rightBroken.isKindOf(Boolean));
        ^super.new.init(leak, rightBroken);
    }
    init { |argLeak, argRightBroken|
        leak = argLeak;
        rightBroken = argRightBroken;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context. Returns 'Voice'.
    

    • Example 1

    a = FoscStopTrillSpan();
    a.context;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • leak

    Is true when stop beam leaks LilyPond '<>'' empty chord


    • Example 1

    a = FoscStopTrillSpan();
    a.leak;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • parameter

    Gets parameter. Returns 'TRILL'.


    • Example 1

    a = FoscStopTrillSpan();
    a.parameter;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • persistent

    Is true.


    • Example 1

    a = FoscStopTrillSpan();
    a.persistent;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rightBroken

    Is true when stop trill spanner is right-broken.


    • Example 1

    a = FoscStopTrillSpan();
    a.rightBroken;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStop

    Is true.


    • Example 1

    a = FoscStopTrillSpan();
    a.spannerStop;
    -------------------------------------------------------------------------------------------------------- */
    spannerStop {
        ^true;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    !!!TODO

    Gets interpreter representation.

    def __repr__(self) -> str:
        return StorageFormatManager(self).get_repr_format()
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle

    bundle = LilyPondFormatBundle()
    string = r'\stopTrillSpan'
    if self.right_broken:
        string = self._tag_hide([string])[0]
    if self.leak:
        string = f'<> {string}'
        bundle.after.leaks.append(string)
    else:
        bundle.after.spanner_stops.append(string)
    return bundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, localTweaks, string;
        bundle = FoscLilyPondFormatBundle();
        string = "\\stopTrillSpan";
        // if (rightBroken) {
        //     string = this.prTagHide([string])[0];
        // };
        if (leak) {
            string = "<> %".format(string);
            bundle.after.leaks.add(string);
        } {
            bundle.after.spannerStarts.add(string); 
        };
        ^bundle;
    }
}
