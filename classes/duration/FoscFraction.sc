/* ------------------------------------------------------------------------------------------------------------
• FoscFraction

FoscFraction(3).cs;
FoscFraction(3.14159).cs;
FoscFraction(#[3,2]).cs;
FoscFraction(6,2).cs;
FoscNonreducedFraction(6,2).cs;
FoscFraction().cs;
------------------------------------------------------------------------------------------------------------ */
FoscFraction : AbstractFunction {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <methodNames;
    var <numerator, <denominator, <pair;
    *initClass {
        methodNames = this.methods.collect { |each| each.name };
    }
    *new { |numerator, denominator|
        var pair;
        pair = if (denominator.isNil) {
            case
            { numerator.isKindOf(FoscFraction) } { numerator.pair }
            { numerator.isSequenceableCollection } { numerator }
            { numerator === inf } { [inf, 0] }
            { numerator === -inf } { [-inf, 0] }
            { numerator.isFloat } { numerator.asFraction }
            { numerator.isInteger } { [numerator, 1] }
            { numerator.respondsTo('duration') } { numerator.pair }
            { numerator.isNil } { #[0,1] };    
        } {
            case
            { numerator.isKindOf(FoscFraction) && { denominator.isKindOf(FoscFraction) } } {
                (numerator / denominator).pair;
            }
            {
                [numerator, denominator];
            };
        };
        
        ^super.new.init(pair);
    }
    init { |argPair|
        pair = argPair.reduceFraction;
        # numerator, denominator = pair;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • +

    a = FoscFraction(3,2) + FoscFraction(1,2);
    a.str;

    a = FoscFraction(3,2) + 0.5;
    a.str;

    a = 1.5 + FoscFraction(1,2);
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    + { |expr|
        var num, denom;
        expr = this.species.new(expr);
        num = (numerator * expr.denominator) + (expr.numerator * denominator);
        denom = denominator * expr.denominator;
        ^this.species.new(num, denom);
    }
    /* --------------------------------------------------------------------------------------------------------
    • -

    a = FoscFraction(3,2) - FoscFraction(1,2);
    a.str;

    a = FoscFraction(3,2) - 0.5;
    a.str;

    a = 1.5 - FoscFraction(1,2);
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    - { |expr|
        expr = this.species.new(expr);
        ^(this + expr.neg);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *
    
    a = FoscFraction(3,2) * FoscFraction(1,2);
    a.str;

    a = FoscFraction(3,2) * 0.5;
    a.str;

    a = 0.5 * FoscFraction(3,2);
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    * { |expr|
        expr = this.species.new(expr);
        ^this.species.new(numerator * expr.numerator, denominator * expr.denominator);
    }
    /* --------------------------------------------------------------------------------------------------------
    • /
    
    a = FoscFraction(3,2) / FoscFraction(1,2);
    a.str;

    a = FoscFraction(3,2) / 0.5;
    a.str;

    a = 1.5 / FoscFraction(1,2);
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    / { |expr|
        expr = this.species.new(expr);
        ^this.species.new(numerator * expr.denominator, denominator * expr.numerator);
    }
    /* --------------------------------------------------------------------------------------------------------
    • ==
    
    a = FoscFraction(3,2);
    b = FoscFraction(6,4);
    c = FoscFraction(2,1);
    d = 2;

    a == a;     // true
    a == b;     // true
    a == c;     // false
    c == d;     // true
    d == c;     // true
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        if (expr.isNumber) { expr = FoscFraction(expr) };
        ^(numerator == expr.numerator && { denominator == expr.denominator });
    }
    /* --------------------------------------------------------------------------------------------------------
    • ===

    a === a;     // true
    a === b;     // true  !!!TODO: override for FoscNonreducedFraction
    a === c;     // false
    c === d;     // true
    d === c;     // true
    -------------------------------------------------------------------------------------------------------- */
    === { |expr|
        ^(this == expr);
    }
    /* --------------------------------------------------------------------------------------------------------
    • !=

    a = FoscFraction(3,2);
    b = FoscFraction(6,4);
    c = FoscFraction(2,1);
    d = 2;

    a != a;     // false
    a != b;     // false
    a != c;     // true
    c != d;     // false
    d != c;     // false
    -------------------------------------------------------------------------------------------------------- */
    != { |expr|
        ^(this == expr).not;
    }   
    /* --------------------------------------------------------------------------------------------------------
    • <
    
    a = FoscFraction(3,2);
    b = FoscFraction(6,4);
    c = FoscFraction(2,1);
    d = 2;

    a < a;     // false
    a < b;     // false
    a < c;     // true
    c < d;     // false
    d < c;     // false
    -------------------------------------------------------------------------------------------------------- */
    < { |expr|
        ^(this.asFloat < expr.asFloat);
    }
    /* --------------------------------------------------------------------------------------------------------
    • >
    
    a = FoscFraction(3,2);
    b = FoscFraction(6,4);
    c = FoscFraction(2,1);
    d = 2;

    a > a;     // false
    a > b;     // false
    a > c;     // true
    c > b;     // true
    b > c;     // false
    -------------------------------------------------------------------------------------------------------- */
    > { |expr|
        ^(this.asFloat > expr.asFloat);
    }
    /* --------------------------------------------------------------------------------------------------------
    • <=
    
    a = FoscFraction(3,2);
    b = FoscFraction(6,4);
    c = FoscFraction(2,1);
    d = 2;

    a <= a;     // true
    a <= b;     // true
    a <= c;     // true
    c <= d;     // true
    d <= c;     // true
    -------------------------------------------------------------------------------------------------------- */
    <= { |expr|
        ^(this.asFloat <= expr.asFloat);
    }
    /* --------------------------------------------------------------------------------------------------------
    • >=
    
    a = FoscFraction(3,2);
    b = FoscFraction(6,4);
    c = FoscFraction(2,1);
    d = 2;

    a >= a;     // true
    a >= b;     // true
    a >= c;     // false
    c >= d;     // true
    d >= c;     // true
    -------------------------------------------------------------------------------------------------------- */
    >= { |expr|
        ^(this.asFloat >= expr.asFloat);
    }
    /* --------------------------------------------------------------------------------------------------------
    • abs
    
    a = FoscFraction(-3,2).abs;
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    abs {
        ^this.species.new(numerator.abs, denominator);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    a = FoscFraction(1,4);
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^"%(%, %)".format(this.species, this.numerator, this.denominator);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asFloat

    a = FoscFraction(3,2);
    a.asFloat;
    -------------------------------------------------------------------------------------------------------- */
    asFloat {
        ^(numerator / denominator);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asInteger

    a = FoscFraction(5,2);
    a.asInteger;
    -------------------------------------------------------------------------------------------------------- */
    asInteger {
        ^this.asFloat.asInteger;
    }
    /* --------------------------------------------------------------------------------------------------------
    • copy
    -------------------------------------------------------------------------------------------------------- */
    copy {
        ^this; // immutable
    }
    /* --------------------------------------------------------------------------------------------------------
    • deepCopy
    -------------------------------------------------------------------------------------------------------- */
    deepCopy {
        ^this; // immutable
    }
    /* --------------------------------------------------------------------------------------------------------
    • div

    a = FoscFraction(3,2).div(FoscFraction(1,2));
    a.str;

    a = FoscFraction(3,2).div(0.5);
    a.str;

    a = 1.5.div(FoscFraction(1,2));
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    div { |expr|
        var div, result;
        expr = this.species.new(expr);
        div = this / expr;
        result = this.species.new(div.numerator.div(div.denominator));
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash

    a = FoscFraction([1,4]);
    b = FoscFraction([1,4]);
    a.hash;
    a.hash == b.hash;
    -------------------------------------------------------------------------------------------------------- */
    hash {
        ^(this.asFloat).hash;
    }
    /* --------------------------------------------------------------------------------------------------------
    • mod
    
    a = FoscFraction(3,2) % FoscFraction(1,2);
    a.str;

    a = FoscFraction(3,2) % 0.5;
    a.str;

    a = 1.5 % FoscFraction(1,2);
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    mod { |expr|
        var div, result;
        expr = this.species.new(expr);
        //div = this.div(expr);
        //result = (this - expr) * div;
        result = this.asFloat % expr.asFloat;
        result = this.species.new(result);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • neg
    
    a = FoscFraction(3,2).neg;
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    neg {
        ^this.species.new(numerator.neg, denominator);
    }
    /* --------------------------------------------------------------------------------------------------------
    • pow
    
    a = FoscFraction(3,2) ** FoscFraction(2,1);
    a.str;

    a = FoscFraction(3,2) ** 2;
    a.str;

    a = 1.5 ** FoscFraction(2,1);
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    pow { |expr|
        ^this.species.new(this.asFloat ** expr.asFloat)
    }
    /* --------------------------------------------------------------------------------------------------------
    • reciprocal

    a = FoscFraction(3,2);
    a.reciprocal.str;
    -------------------------------------------------------------------------------------------------------- */
    reciprocal {
        ^this.species.new(denominator, numerator);  
    }
    /* --------------------------------------------------------------------------------------------------------
    • sign

    a = FoscFraction(3,4);
    a.sign;

    a = FoscFraction(-3,4);
    a.sign;
    -------------------------------------------------------------------------------------------------------- */
    sign {
        ^numerator.sign;
    }
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[numerator, denominator];
    }
    /* --------------------------------------------------------------------------------------------------------
    • str

    a = FoscFraction(3,2);
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^"%/%".format(numerator, denominator);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • withDenominator

    a = FoscNonreducedFraction(3,2);
    a = a.withDenominator(4);
    a.pair;
    -------------------------------------------------------------------------------------------------------- */
    withDenominator { |denominator|
        var currentNumerator, currentDenominator, multiplier, newNumerator, newDenominator, pair;
        # currentNumerator, currentDenominator = this.pair;
        multiplier = FoscMultiplier(denominator, currentDenominator);
        newNumerator = multiplier * currentNumerator;
        newDenominator = multiplier * currentDenominator;
        if (newNumerator.denominator == 1 && (newDenominator.denominator == 1)) {
            pair = [newNumerator.numerator, newDenominator.numerator];
        } {
            pair = [currentNumerator, currentDenominator];
        };
        ^FoscNonreducedFraction(*pair);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • performBinaryOpOnSimpleNumber
    -------------------------------------------------------------------------------------------------------- */
    performBinaryOpOnSimpleNumber { |selector, number|
        if (FoscFraction.methodNames.includes(selector).not) {
            ^throw("% does not implement: %.".format(this.species, selector));
        };
        ^this.species.new(number).perform(selector, this);
    }
}
