/* ------------------------------------------------------------------------------------------------------------
• FoscTimeSignature (abjad 3.0)

Time signature.


• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64], [1/8]));
m = FoscTimeSignature(#[3,8]);
a[0].attach(m);
a.show;


• Example 2 !!!TODO: not yet working

Create a score-contexted time signature. Score-contexted time signatures format behind comments when no Abjad score container is found.

a = FoscStaff(FoscLeafMaker().(#[60,62,64], [1/8]));
m = FoscTimeSignature(#[3,8]);
a[0].attach(m, context: 'Score');
a.format;
a.show;

Score-contexted time signatures format normally when an Abjad score container is found.

a = FoscScore([a]);
a.show;


• Example 3 !!!TODO: tags not yet implemented

Time signatures can be tagged.

a = FoscStaff(FoscLeafMaker().(#[60,62,64], [1/8]));
m = FoscTimeSignature(#[3,8]);
a[0].attach(m);
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscTimeSignature : Fosc {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <denominator, <hasNonPowerOfTwoDenominator, multiplier, <numerator, <partial, partialReprString, <hide;
    var <context="Staff", <formatSlot='opening', <parameter=true;
	*new { |pair, partial, hide=false|
        pair = case
        { pair.isKindOf(FoscFraction) } { pair.pair}
        { pair.isNumber } { FoscDuration(pair).pair }
        { pair.isKindOf(FoscMeter) } { pair.pair }
        { pair.isKindOf(FoscTimeSignature) } { pair.pair }
        { pair };
		^super.new.init(pair, partial, hide);
	}
	init { |argPair, argPartial, argHide|
		var pair, result;
        assert(argPair.isSequenceableCollection);
        assert(argPair.size == 2);
        assert(argPair.every { |each| each.isInteger });
		pair = argPair;
        # numerator, denominator = pair;
        if (argPartial.notNil) { 
            partial = FoscDuration(argPartial);
            partialReprString = ", partial=%".format(partial);
        } {
            partialReprString = "";
        };
        hide = argHide;
        multiplier = this.impliedProlation;
        hasNonPowerOfTwoDenominator = denominator.isPowerOfTwo.not;
	}
     ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context of time signature.


    • Example 1

    a = FoscTimeSignature(#[3,4]);
    a.context;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • denominator

    Gets denominator of time signature:
    
    
    • Example 1
    
    a = FoscTimeSignature(#[3,4]);
    a.denominator;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • duration
    
    Gets duration of time signature.
    

    • Example 1
    
    a = FoscTimeSignature(#[3,4]);
    a.duration.cs;
    -------------------------------------------------------------------------------------------------------- */
    duration {
        ^FoscDuration(numerator, denominator);
    }
    /* --------------------------------------------------------------------------------------------------------
    • hasNonPowerOfTwoDenominator

    Is true when time signature has non-power-of-two denominator.

    
    • Example 1
    
    a = FoscTimeSignature(#[7,12]);
    a.hasNonPowerOfTwoDenominator;

    
    • Example 2
    
    a = FoscTimeSignature(#[3,4]);
    a.hasNonPowerOfTwoDenominator;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • hide

    Is true when time signature should not appear in output (but should still determine effective time signature).


    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    m = FoscTimeSignature(#[4,4]);
    a[0].attach(m);
    m = FoscTimeSignature(#[2,4], hide: true);
    a[2].attach(m);
    a.show;

    a.format;

    !!!TODO: bug: result shuold be [[4,4],[4,4],[2,4],[2,4]]
    iterate(a).leaves.do { |leaf|
        FoscInspection(leaf).effectiveIndicator(FoscTimeSignature).pair.postln;
    };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • impliedProlation

    Gets implied prolation of time signature.
   
    
    • Example 1

    Implied prolation of time signature with power-of-two denominator.

    a = FoscTimeSignature(#[3,8]);
    a.impliedProlation.cs;


    • Example 2

    Implied prolation of time signature with non-power-of-two denominator.

    a = FoscTimeSignature(#[7,12]);
    a.impliedProlation.cs;
    -------------------------------------------------------------------------------------------------------- */
    impliedProlation {
        ^FoscDuration(1, denominator).impliedProlation;
    }
    /* --------------------------------------------------------------------------------------------------------
    • numerator

    Gets numerator of time signature.
    
    
    • Example 1

    a = FoscTimeSignature(#[3,8]);
    a.numerator;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • pair

    Gets numerator / denominator pair corresponding to time signature.
    
    
    • Example 1

    a = FoscTimeSignature(#[3,8]);
    a.pair;
    -------------------------------------------------------------------------------------------------------- */
    pair {
        ^[numerator, denominator];
    }
     /* --------------------------------------------------------------------------------------------------------
    • parameter

    Is true.


    • Example 1

    a = FoscTimeSignature(#[3,8]);
    a.parameter;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • partial

    Gets duration of pick-up to time signature.
    

    • Example 1

    a = FoscTimeSignature(#[3,8]);
    a.partial;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Tweaks are not implemented on time signature.

    The LilyPond '\time' command refuses tweaks.

    Override the LilyPond 'TimeSignature' grob instead.
    -------------------------------------------------------------------------------------------------------- */
    tweaks {
        // pass
    }
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS: SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • add !!!TODO: incomplete

    Adds time signature to 'timeSignature'.
    
    Returns new time signature.
    
	a = FoscTimeSignature([3, 4]);
	b = FoscTimeSignature([7, 8]);
	c = a + b;
	c.pair;
    -------------------------------------------------------------------------------------------------------- */
    add { |timeSignature|
        assert(
            timeSignature.isKindOf(this.species),
            "%:%: argument must be a %: %.".format(this.species, thisMethod.name, this.species, timeSignature);
        );
    	^this.species.new((this.duration + timeSignature.duration).pair);
    }
    /* --------------------------------------------------------------------------------------------------------
    • copy

    Copies time signature.
    -------------------------------------------------------------------------------------------------------- */
    copy {
    	^this.species.new([numerator, denominator], partial);
    }
    /* --------------------------------------------------------------------------------------------------------
    • ==

    Is true when arg is a time signature with numerator and denominator equal to this time signature. Also true when arg is a tuple with first and second elements equal to numerator and denominator of this time signature. Otherwise false.
    
    Returns true or false.

    a = FoscTimeSignature([3, 4]);
    b = FoscTimeSignature([3, 4]);
    c = FoscTimeSignature([4, 4]);
    d = FoscDuration([3, 4]);
    a == b;
    a == c;
    a == d;
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
    	if (expr.isKindOf(this.species)) {
    		^(numerator == expr.numerator && (denominator == expr.denominator));
    	};
    	if (expr.isKindOf(SequenceableCollection)) {
    		if (expr.size != 2) { ^false };
    		^(numerator == expr[0] && (denominator == expr[1]));
    	};
    	^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats time signature.

    a = FoscTimeSignature([3, 8]);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    format {
    	^this.prGetLilypondFormat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • >=

    Is true when duration of time signature is greater than or equal to duration of arg. Otherwise false.

    Returns true or false.
    
	a = FoscTimeSignature([3, 4]);
    b = FoscTimeSignature([2, 4]);
    a >= b;
    b >= a;
    -------------------------------------------------------------------------------------------------------- */
    >= { |expr|
    	^(this.duration >= expr.duration);
    }
    /* --------------------------------------------------------------------------------------------------------
    • >
    
    Is true when duration of time signature is greater than duration of arg. Otherwise false.
    
    Returns true or false.
    
    a = FoscTimeSignature([3, 4]);
    b = FoscTimeSignature([2, 4]);
    a > b;
    b > a;
    -------------------------------------------------------------------------------------------------------- */
    > { |expr|
    	^(this.duration > expr.duration);
    }
    /* --------------------------------------------------------------------------------------------------------
    • <=

    Is true when duration of time signature is less than or equal to duration of arg. Otherwise false.
    
    Returns true or false.
    
    a = FoscTimeSignature([3, 4]);
    b = FoscTimeSignature([2, 4]);
    a <= b;
    b <= a;

    a <= 3;
    -------------------------------------------------------------------------------------------------------- */
    <= { |expr|
        ^(this.duration <= expr.duration);
    }
    /* --------------------------------------------------------------------------------------------------------
    • <

    Is true when duration of time signature is less than duration of arg. Otherwise false.

    Returns booelan.

    a = FoscTimeSignature([3, 4]);
    b = FoscTimeSignature([2, 4]);
    a < b;
    b < a;
    -------------------------------------------------------------------------------------------------------- */
    < { |expr|
    	^(this.duration < expr.duration);
    }
    /* --------------------------------------------------------------------------------------------------------
    • (abjad: __radd__)

    !!!TODO: see FoscFraction:performBinaryOpOnSimpleNumber
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • str

    Gets string representation of time signature.
    
    a = FoscTimeSignature([3, 4]);
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    str {
    	^"%/%".format(numerator, denominator);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • withPowerOfTwoDenominator
    
    Makes new time signature equivalent to current time signature with power-of-two denominator.

    Returns new time signature.
    -------------------------------------------------------------------------------------------------------- */
    withPowerOfTwoDenominator {
    	^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    
    a = FoscTimeSignature([3, 16]);
    a.format;

    a = FoscTimeSignature([3, 16], suppress: true);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        var result, durationString, partialDirective, string;
    	if (hide) { ^[] };
        if (partial.isNil) {
            ^"\\time %/%".format(numerator.asInteger, denominator.asInteger);
        } {
            result = [];
            durationString = partial.lilypondDurationString;
            partialDirective = "\\partial %".format(durationString);
            result = result.add(partialDirective);
            string = "\\time %/%".format(numerator.asInteger, denominator.asInteger);
            result = result.add(string);
            ^result;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle

    !!!TODO:
    '_get_lilypond_format_bundle' not impleneted in equavalent abjad class -- why is it needed here?
    NB: it's also not inherited from any superclass in abjad
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle {
        var bundle;
        bundle = FoscLilyPondFormatBundle();
        if (hide.not) {
            bundle.opening.commands.add(this.prGetLilypondFormat);
        };
        ^bundle;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: DISPLAY
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • inspect

    FoscTimeSignature([7, 16]).inspect;
    -------------------------------------------------------------------------------------------------------- */
    inspect {
        Post << this << nl;
        Post.tab << "pair: " << this.pair << nl;
    }
}
