/* ------------------------------------------------------------------------------------------------------------
• FoscMultiplier

FoscMultiplier.


• Example 1

Initializes from integer numerator.

FoscMultiplier(3)


• Example 2

Initializes from integer numerator and denominator.

FoscMultiplier(3, 16)


• Example 3

Initializes from integer-equivalent numeric numerator.

FoscMultiplier(3.0)


• Example 4

Initializes from integer-equivalent numeric numerator and denominator.

FoscMultiplier(3.0, 16)


• Example 5

Initializes from integer-equivalent singleton.

FoscMultiplier(3)


• Example 6

Initializes from integer-equivalent pair.

FoscMultiplier([3, 16])


• Example 7

Initializes from other duration.

FoscMultiplier(FoscDuration(3, 16))


• Example 8

Intializes from fraction.

FoscMultiplier(FoscFraction(3, 16))


• Example 9

Initializes from nonreduced fraction.


m = FoscMultiplier(FoscNonreducedFraction(6, 32))
m.pair; //!!!! BROKEN


• Example 10

FoscMultipliers inherit from built-in fraction.

FoscMultiplier(3, 16).isKindOf(FoscFraction);       // true

• Example 11

FoscMultipliers are numbers.

FoscMultiplier(3, 16).isKindOf(Number); //!!! BROKEN


• Example 12

Attaching a multiplier to a score component multiplies that component's duration.

n = FoscNote(60, 1);
n.attach(FoscMultiplier(5, 8));
n.format;
------------------------------------------------------------------------------------------------------------ */
FoscMultiplier : FoscDuration {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *

    Multiplier times duration returns duration.

    Returns duration.
    
    def __mul__(self, *arguments):
    if len(arguments) == 1 and type(arguments[0]) is Duration:
        return Duration(Duration.__mul__(self, *arguments))
    else:
        return Duration.__mul__(self, *arguments)
    
    a = FoscMultiplier(1, 1);
    b = FoscMultiplier(1, 3);
    c = FoscDuration(1, 3);

    (a * b).inspect;        // returns a FoscMultiplier
    (a * c).inspect;        // returns a FoscDuration
    -------------------------------------------------------------------------------------------------------- */
    * { |expr|
        if (expr.species == FoscDuration) {
            ^(FoscDuration(this) * expr);
        } {
            ^this.species.new(FoscDuration(this) * expr);
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *fromDotCount

    Makes multiplier from 'dotCount'.


    • Example 1

    FoscMultiplier.fromDotCount(0).str;
    FoscMultiplier.fromDotCount(1).str;
    FoscMultiplier.fromDotCount(2).str;
    FoscMultiplier.fromDotCount(3).str;
    FoscMultiplier.fromDotCount(4).str;
    -------------------------------------------------------------------------------------------------------- */
    *fromDotCount { |dotCount|
        var denominator, numerator;
        assert(dotCount.isInteger);
        assert(0 <= dotCount);
        denominator = (2 ** dotCount).asInteger;
        numerator = (2 ** (dotCount + 1) - 1).asInteger;
        ^FoscMultiplier(numerator, denominator);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • isNormalized

    Is true when mutliplier is greater than 1/2 and less than 2. Otherwise false:
    
    Returns true or false.


    • Example 1

    FoscMultiplier(3, 2).isNormalized;
    FoscMultiplier(7, 2).isNormalized;
    -------------------------------------------------------------------------------------------------------- */
    isNormalized {
        ^((this.species.new(1, 2) < this) && (this < this.species.new(2)));
    }
}
