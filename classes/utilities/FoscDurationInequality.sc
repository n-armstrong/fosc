/* ------------------------------------------------------------------------------------------------------------
• FoscDurationInequality

a = FoscDurationInequality('<', [3, 4]);
a.([1, 2]);
a.(FoscNote(60, 1/4));
a.(FoscLeafMaker().([60,62],[1/1,1/1]))
------------------------------------------------------------------------------------------------------------ */
FoscDurationInequality : FoscInequality {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <duration, <preprolated;
    *new { |operatorSymbol, duration, preprolated=false|
        if (duration.isNil) { duration = inf };
        if ([inf, -inf].includes(duration).not) {
            duration = FoscDuration(duration);
            assert(0 <= duration, receiver: this, method: thisMethod);
        };
        ^super.new(operatorSymbol).initFoscDurationInequality(duration, preprolated);
    }
    initFoscDurationInequality { |argDuration, argPreprolated|
        duration = argDuration;
        preprolated = argPreprolated;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value

    Calls inequality on 'object'.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    value { |object|
        var localDuration;
        case
        { object.isKindOf(FoscComponent) } {
            if (preprolated) {
                localDuration  = object.prGetPreprolatedDuration;
            } {
                localDuration = object.prGetDuration;
            };
        }
        { object.isKindOf(FoscSelection) } {
            if (preprolated) {
                localDuration  = object.prGetPreprolatedDuration;
            } {
                localDuration = object.duration;
            };  
        }
        {
            localDuration = FoscDuration(object);
        };
        ^localDuration.perform(operatorSymbol, duration);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • duration

    Gets duration.

    Returns a duration.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • preprolated

    Is true when inequality evaluates preprolated duration.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
}
