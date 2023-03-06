/* ------------------------------------------------------------------------------------------------------------
• FoscOctave


• Example 1

a = FoscOctave(4);
a.number;
a.name;


• Example 2 - Initialize from a lilypond octave name

a = FoscOctave("''");
a.number;
a.name;


• Example 3 - Initialize from a lilypond string

a = FoscOctave("cs'");
a.number;
a.name;


• Example 4 - Initialize from a pitch

a = FoscOctave(FoscPitch("cs'"));
a.number;
a.name;


a = FoscOctave("cs'");
a.number;
a.name;

FoscPitchManager.isOctaveName(",,");
------------------------------------------------------------------------------------------------------------ */
FoscOctave : Fosc {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <number;
	*new { |number|
		^super.new.init(number);
	}
	init { |argNumber|
        
        number = argNumber;

        case
        { number.isNumber } {
            // pass
        }
        { number.isString && { FoscPitchManager.isOctaveName(number) } } {
            ^FoscOctave.newFromOctaveName(number);
        }
        { number.isString && { FoscPitchManager.isPitchName(number) } } {
            ^FoscOctave.newFromPitch(FoscPitch(number));
        }
        { number.isKindOf(Symbol) } {
            ^FoscOctave(number.asString);
        }
        { number.isKindOf(FoscPitch) } {
            ^FoscOctave.newFromPitch(number);
        }
        { number.isKindOf(FoscOctave) } {
            ^number;
        }
        {
            ^throw("Can't initialize % from value: %".format(this.species, number));
        };

		number = number.asInteger;
	}
	/* --------------------------------------------------------------------------------------------------------
	• *newFromPitch

	a = FoscOctave.newFromPitch(FoscPitch("cs'"));
    a.number;

    a = FoscOctave.newFromPitch(FoscPitch("cs"));    
    a.number;

    a = FoscOctave.newFromPitch(FoscPitch("cs,,,,,"));    
    a.number;
	-------------------------------------------------------------------------------------------------------- */
	*newFromPitch { |pitch|
		//!!!TODO: check is valid FoscPitch
		var octaveName;
		octaveName = FoscPitchManager.prSplitLilypondPitchName(pitch.name)[2];
		^FoscOctave.newFromOctaveName(octaveName);
	}
	/* --------------------------------------------------------------------------------------------------------
	• *newFromOctaveName

	a = FoscOctave.newFromOctaveName(",,,,,");
    a.number;

    a = FoscOctave.newFromOctaveName("");
    a.number;

    a = FoscOctave.newFromOctaveName("''");
    a.number;
	-------------------------------------------------------------------------------------------------------- */
	*newFromOctaveName { |octaveName|
		var number;

		octaveName = octaveName.asString;

		if (FoscPitchManager.isOctaveName(octaveName).not) {
			^throw("Bad argument type for %:%: '%'".format(this.species, thisMethod.name, octaveName));
		};

        number = case
        { octaveName.includes($,) } { 3 - octaveName.size }
        { octaveName.includes($') } { 3 + octaveName.size }
        { octaveName.isEmpty } { 3 };

        ^FoscOctave(number);
	}
	/* --------------------------------------------------------------------------------------------------------
	• *isOctaveName

    //!!! DEPRECATED -- moved into FoscPitchManager
	-------------------------------------------------------------------------------------------------------- */
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • ==

    a = FoscOctave(3);
    b = FoscOctave(4);

    a == a;
    a == b;
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        ^(this.number == FoscOctave(expr).number);
    }
    /* --------------------------------------------------------------------------------------------------------
    • !=

    a = FoscOctave(3);
    b = FoscOctave(4);

    a != a;
    a != b;
    -------------------------------------------------------------------------------------------------------- */
    != { |expr|
        ^(this == expr).not;
    }
    /* --------------------------------------------------------------------------------------------------------
    • <

    a = FoscOctave(3);
    b = FoscOctave(4);

    a < b;
    b < a;
    -------------------------------------------------------------------------------------------------------- */
    < { |expr|
        ^(this.number < FoscOctave(expr).number);
    }
    /* --------------------------------------------------------------------------------------------------------
    • >

    a = FoscOctave(3);
    b = FoscOctave(4);

    a > b;
    b > a;
    -------------------------------------------------------------------------------------------------------- */
    > { |expr|
        ^(this < expr).not;
    }
    /* --------------------------------------------------------------------------------------------------------
    • <=

    a = FoscOctave(3);
    b = FoscOctave(4);

    a <= a;
    a <= b;
    b <= a;
    -------------------------------------------------------------------------------------------------------- */
    <= { |expr|
        ^(this.number <= FoscOctave(expr).number);
    }
    /* --------------------------------------------------------------------------------------------------------
    • >=

    a = FoscOctave(3);
    b = FoscOctave(4);

    a >= a;
    a >= b;
    b >= a;
    -------------------------------------------------------------------------------------------------------- */
	>= { |expr|
        ^(this.number >= FoscOctave(expr).number);
    }
    /* --------------------------------------------------------------------------------------------------------
    • add

    a = FoscOctave(3);
    a = a + FoscOctave(4);
    a.cs;

    a = FoscOctave(3);
    a = a + 4;
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    add { |expr|
    	if (expr.isKindOf(FoscOctave)) { expr = expr.number };
    	^this.species.new(this.number + expr);
    }
    /* --------------------------------------------------------------------------------------------------------
    • sub

    a = FoscOctave(3);
    a = a - FoscOctave(4);
    a.cs;

    a = FoscOctave(3);
    a = a - 4;
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    sub { |expr|
    	if (expr.isKindOf(FoscOctave)) { expr = expr.number };
    	^this.species.new(this.number - expr);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString
    
    a = FoscOctave(4);
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^"FoscOctave(%)".format(this.number);
    }
    /* --------------------------------------------------------------------------------------------------------
    • format
    
    a = FoscOctave(4);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • str

    a = FoscOctave(4);
	a.str;

	a = FoscOctave(3);
	a.str;

	a = FoscOctave(-2);
	a.str;
    -------------------------------------------------------------------------------------------------------- */
	str {
		^this.name;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE PROPERTIES
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • midinote

    Gets midinote of first note in octave.

    a = FoscOctave(4);
    a.midinote;
    -------------------------------------------------------------------------------------------------------- */
    midinote {
        ^(this.number + 1) * 12;
    }
    /* --------------------------------------------------------------------------------------------------------
	• name
	
    a = FoscOctave(4);
	a.name;

	a = FoscOctave(3);
	a.name;

	a = FoscOctave(-2);
	a.name;
	-------------------------------------------------------------------------------------------------------- */
	name {
		if (this.number < 3) { ^",".wrapExtend((3 - number).abs) };
    	if (this.number == 3) { ^"" };
    	if (this.number > 3) { ^"'".wrapExtend(number - 3) };
		^nil;
	}
    /* --------------------------------------------------------------------------------------------------------
    • number

    a = FoscOctave(4);
    a.number;
    -------------------------------------------------------------------------------------------------------- */
}
