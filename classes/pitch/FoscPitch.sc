/* ------------------------------------------------------------------------------------------------------------
• FoscPitch


### TODO: abjad public methods
accidental_spelling	// Accidental spelling of Abjad session (retrieves from abjad_configuration)
apply_accidental

### TODO: make set operations work for collections of pitches, eg:
[60, 61, 62].collect { |each| FoscPitch(each) }.sect([60, 62].collect { |each| FoscPitch(each) });


• Example 1

Quantize to 1/8th tones

a = FoscPitch(60.25);
a.pitchNumber;
a.str;

a = FoscPitch(60.75);
a.pitchNumber;
a.str;


• Example 2

a = FoscPitch("C4", arrow: 'up');
a.pitchNumber;
a.str;

a = FoscPitch("C#4", arrow: 'down');
a.pitchNumber;
a.str;
------------------------------------------------------------------------------------------------------------ */
FoscPitch : FoscObject {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <pitchClass, <octave, <accidental, <arrow;
	var manager;
	*new { |val, arrow|
		^super.new.init(val, arrow);
	}
	init { |val, argArrow|
		var initializer, arrowState;
		manager = FoscPitchNameManager;
		
        initializer = case
		{ val.isNumber } {
            FoscNumberedPitch(val);
        }
		{ val.isKindOf(FoscPitch) } {
            FoscNamedPitch(val.pitchName, val.arrow);
        }
		{ val.asString.isPitchName } {
            FoscNamedPitch(val, argArrow);
        }
		{ val.asString.isLilyPondPitchName } {
            FoscNamedPitch(manager.lilypondPitchNameToPitchName(val), argArrow);
        }
		{ val.isKindOf(FoscPitchClass) } {
            FoscPitch(val.pitchClassName ++ "4", val.arrow);
        }
		{ 
            throw("Can't initialize % from value: %".format(this.species, val));
        };

		pitchClass = initializer.pitchClass;
		octave = initializer.octave;
		accidental = initializer.accidental;
        arrow = initializer.arrow;
        if (arrow.notNil && { #['up', 'down'].includes(arrow) }) {
            accidental.arrow_(arrow);
        };
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS: SPECIAL
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • == (abjad: __eq__)

    a = FoscPitch('C#4');
    b = FoscPitch('Db4');
    c = FoscPitch('B3');
    a == a;
    a == b; // Enharmonic equivalents are treated as equal
    a == c;
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        ^(this.pitchNumber == FoscPitch(expr).pitchNumber);
    }
    /* --------------------------------------------------------------------------------------------------------
    • !=

    a = FoscPitch('C#4');
    b = FoscPitch('Db4');
    c = FoscPitch('B3');
    a != b; // Enharmonic equivalents are treated as equal
    a != c;
    a != a;
    -------------------------------------------------------------------------------------------------------- */
    != { |expr|
        ^(this == expr).not;
    }
    /* --------------------------------------------------------------------------------------------------------
    • >
    
    Is true when arg can be coerced to a pitch and when this pitch is greater than arg. Otherwise false.

    Returns true or false.
    

    a = FoscPitch('C#4');
    b = FoscPitch('D#4');
    c = FoscPitch('B3');
    a > b;
    a > c;
    a > a;
    -------------------------------------------------------------------------------------------------------- */
    > { |expr|
        ^(this.pitchNumber > FoscPitch(expr).pitchNumber);
    }
    /* --------------------------------------------------------------------------------------------------------
    • >=

    Is true when arg can be coerced to a pitch and when this pitch is greater than or equal to arg. Otherwise false.

    Returns true or false.

    a = FoscPitch('C#4');
    b = FoscPitch('D#4');
    c = FoscPitch('B3');
    a >= a;
    a >= b;
    a >= c;
    -------------------------------------------------------------------------------------------------------- */
    >= { |expr|
        ^(this.pitchNumber >= FoscPitch(expr).pitchNumber);
    }
    /* --------------------------------------------------------------------------------------------------------
    • <
    
    Is true when arg can be coerced to a pitch and when this pitch is less than arg. Otherwise false.

    Returns true or false.

    a = FoscPitch('C#4');
    b = FoscPitch('D#4');
    c = FoscPitch('B3');
    a < b;
    a < c;
    a < a;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • <=

    Is true when arg can be coerced to a pitch and when this pitch is less than or equal to arg. Otherwise false.

    Returns true or false.

    a = FoscPitch('C#4');
    b = FoscPitch('D#4');
    c = FoscPitch('B3');
    a <= a;
    a <= b;
    a <= c;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
	• add (abjad: __add__)

	Adds arg to numberd pitch.

	Returns new numbered pitch.
	

	x = FoscPitch('C#4');
	x = x + 2;
	x.pitchName;

    x = FoscPitch('C#4') + FoscInterval(2);
    x.pitchName;
	-------------------------------------------------------------------------------------------------------- */
    add { |expr|
        if (expr.isKindOf(FoscInterval)) { expr = expr.number };
    	^FoscPitch(this.pitchNumber + FoscPitch(expr).pitchNumber);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^"FoscPitch('%')".format(this.pitchName);
    }
	/* --------------------------------------------------------------------------------------------------------
    • asFloat (abjad: __float__)

	Changes numbered pitch to float.

	Returns float.
	

	x = FoscPitch('C+4');
    x.asFloat;
	-------------------------------------------------------------------------------------------------------- */
    asFloat {
    	^this.pitchNumber.asFloat;
    }
	/* --------------------------------------------------------------------------------------------------------
    • asInteger (abjad: __int__)

	Changes numbered pitch to integer.
	
	Returns integer.
	
 	
	x = FoscPitch('C+4');
    x.asInt;
	-------------------------------------------------------------------------------------------------------- */
    asInteger {
    	^this.pitchNumber.asInteger;
    }
    /* --------------------------------------------------------------------------------------------------------
    • neg

	Negates numbered pitch.

	Returns new numbered pitch.
	
	
	a = FoscPitch('C#4');
	b = a.neg;
	b.pitchNumber;
	-------------------------------------------------------------------------------------------------------- */
	neg {
		^FoscPitch(this.pitchNumber.neg);
	}
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[this.pitchName];
    }
    /* --------------------------------------------------------------------------------------------------------
    • str (abjad: __str__)
    
    a = FoscPitch('C#4');
    a.str;

    •••••••••••••••••••• TODO
    a = FoscPitch('C#4', arrow: 'up');
    a.str;
	-------------------------------------------------------------------------------------------------------- */
    str {
    	^this.lilypondPitchName;
    }
    /* --------------------------------------------------------------------------------------------------------
    • pitchString
    
    a = FoscPitch('C#4');
    a.pitchString;
    a.ps;
    -------------------------------------------------------------------------------------------------------- */
    pitchString {
        ^"\"%\"".format(this.pitchName);
    }
	/* --------------------------------------------------------------------------------------------------------
    • sub
    -------------------------------------------------------------------------------------------------------- */
    sub { |expr|
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC PROPERTIES
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • accidentalName

    FoscPitch('Db5').accidentalName;

    •••••••••••••••••••• DONE
    FoscPitch('Db5', arrow: 'up').accidentalName;
	-------------------------------------------------------------------------------------------------------- */
	accidentalName {
		^accidental.name;
	}
	/* --------------------------------------------------------------------------------------------------------
    • alterationInSemitones

    FoscPitch('Db5').alterationInSemitones;

    •••••••••••••••••••• DONE
    FoscPitch('Db5', arrow: 'down').alterationInSemitones;
	-------------------------------------------------------------------------------------------------------- */
	alterationInSemitones {
		^accidental.semitones;
	}
	/* --------------------------------------------------------------------------------------------------------
    • diatonicPitchClassName

    FoscPitch('Db5').diatonicPitchClassName;
	-------------------------------------------------------------------------------------------------------- */
	diatonicPitchClassName {
		^pitchClass.diatonicPitchClassName;
	}
	/* --------------------------------------------------------------------------------------------------------
    • diatonicPitchClassNumber

    FoscPitch('Db5').diatonicPitchClassNumber;
	-------------------------------------------------------------------------------------------------------- */
	diatonicPitchClassNumber {
		^pitchClass.diatonicPitchClassNumber;
	}
	/* --------------------------------------------------------------------------------------------------------
    • diatonicPitchName

    FoscPitch('Db5').diatonicPitchName;
	-------------------------------------------------------------------------------------------------------- */
	diatonicPitchName {
		^(pitchClass.diatonicPitchClassName ++ octave.octaveName);
	}
	/* --------------------------------------------------------------------------------------------------------
    • diatonicPitchNumber

    FoscPitch('Db5').diatonicPitchNumber;
	-------------------------------------------------------------------------------------------------------- */
	diatonicPitchNumber {
		^((12 * (octave.octaveNumber + 1)) + pitchClass.diatonicPitchClassNumber);
	}
	/* --------------------------------------------------------------------------------------------------------
    • isDiatonic

    FoscPitch('Db5').isDiatonic;
	-------------------------------------------------------------------------------------------------------- */
	isDiatonic {
		^(this.pitchClassName == this.diatonicPitchClassName);
	}
	/* --------------------------------------------------------------------------------------------------------
    • isFlattened

    FoscPitch('Db5').isFlattened;
	-------------------------------------------------------------------------------------------------------- */
	isFlattened {
		^#['b', 'bb', '~', 'b~'].includes(this.accidentalName.asSymbol);
	}
	/* --------------------------------------------------------------------------------------------------------
    • isSharpened

    FoscPitch('Db5').isSharpened;
	-------------------------------------------------------------------------------------------------------- */
	isSharpened {
		^#['#', '##', '+', '#+'].includes(this.accidentalName.asSymbol);
	}
	/* --------------------------------------------------------------------------------------------------------
    • lilypondPitchName

    FoscPitch('Db5').lilypondPitchName;

    •••••••••••••••••••• DONE
    FoscPitch('Db5', arrow: 'up').lilypondPitchName;
	-------------------------------------------------------------------------------------------------------- */
	lilypondPitchName {
		^manager.pitchNameToLilypondPitchName(this.pitchName, arrow: arrow);
	}
	/* --------------------------------------------------------------------------------------------------------
    • midicps

    FoscPitch('A4').midicps;
	-------------------------------------------------------------------------------------------------------- */
	midicps {
		^this.pitchNumber.midicps;
	}
	/* --------------------------------------------------------------------------------------------------------
    • octaveName

    FoscPitch('Db5').octaveName;
	-------------------------------------------------------------------------------------------------------- */
	octaveName {
		^octave.octaveName;
	}
	/* --------------------------------------------------------------------------------------------------------
    • octaveNumber

    FoscPitch('Db5').octaveNumber;
	-------------------------------------------------------------------------------------------------------- */
	octaveNumber {
		^octave.octaveNumber;
	}
	/* --------------------------------------------------------------------------------------------------------
    • pitchClassName

    FoscPitch('Db5').pitchClassName;
	-------------------------------------------------------------------------------------------------------- */
	pitchClassName {
		^pitchClass.pitchClassName;
	}
	/* --------------------------------------------------------------------------------------------------------
    • pitchClassNumber

    FoscPitch('Db5').pitchClassNumber;

    •••••••••••••••••••• TODO
    FoscPitch('Db5', arrow: 'up').pitchClassNumber;
	-------------------------------------------------------------------------------------------------------- */
	pitchClassNumber {
		^pitchClass.pitchClassNumber;
	}
	/* --------------------------------------------------------------------------------------------------------
    • pitchName

    FoscPitch('Db5').pitchName;
	-------------------------------------------------------------------------------------------------------- */
	pitchName {
		^(pitchClass.pitchClassName ++ octave.octaveName);
	}
	/* --------------------------------------------------------------------------------------------------------
    • pitchNumber
    
    FoscPitch('Db~5').pitchNumber;

    FoscPitch(61.5).pitchNumber;

    FoscPitch('C4', arrow: 'up').pitchNumber;
	-------------------------------------------------------------------------------------------------------- */
	pitchNumber {
        var result;
		result = (pitchClass.pitchClassNumber + 12 + (octave.octaveNumber * 12));
        if (arrow.notNil) {
            switch(arrow,
                'up', { result = result + 0.25 },
                'down', { result = result - 0.25 },
            );
        };
        ^result;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS: TRANSFORMATIONS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • applyAccidental
	-------------------------------------------------------------------------------------------------------- */
	applyAccidental { |accidental|
		^this.notYetImplemented(thisMethod);
	}
	/* --------------------------------------------------------------------------------------------------------
    • multiply
	
    Multiplies pitch-class of numbered pitch by n and maintains octave.
	
	Returns new numbered pitch.
	

    a = FoscPitch(62);
    a = a.multiply(3);
    a.pitchNumber;
	-------------------------------------------------------------------------------------------------------- */
	multiply { |n=1|
		var pitchClassNumber, octaveFloor;
		pitchClassNumber = (this.pitchClassNumber * n) % 12;
		octaveFloor = (this.octaveNumber + 1) * 12;
		^this.species.new(pitchClassNumber + octaveFloor);
	}
	/* --------------------------------------------------------------------------------------------------------
    • invert

	x = FoscPitch('Eb4');
	x = x.invert(axis: 'D4');
	x.pitchName;
	-------------------------------------------------------------------------------------------------------- */
	invert { |axis|
		axis = FoscPitch(axis);
		axis = axis.transpose(axis.pitchNumber - this.pitchNumber);
		^this.species.new(axis);
	}
	/* --------------------------------------------------------------------------------------------------------
    • respellWithFlats

    x = FoscPitch('C#4');
	x.pitchName;
	x = x.respellWithFlats;
	x.pitchName;
	-------------------------------------------------------------------------------------------------------- */
	respellWithFlats {
		^this.species.new(manager.pitchClassNumberToPitchClassNameWithFlats(pitchClass.pitchClassNumber) ++ octave.octaveName);
	}
	/* --------------------------------------------------------------------------------------------------------
    • respellWithSharps

    x = FoscPitch('Db4');
	x.pitchName;
	x = x.respellWithSharps;
	x.pitchName;
	-------------------------------------------------------------------------------------------------------- */
	respellWithSharps {
		^this.species.new(manager.pitchClassNumberToPitchClassNameWithSharps(pitchClass.pitchClassNumber) ++ octave.octaveName);
	}
    /* --------------------------------------------------------------------------------------------------------
    • toStaffPosition

    Changes named pitch to staff position.
    -------------------------------------------------------------------------------------------------------- */
	toStaffPosition { |clef|
        var staffPositionNumber, staffPosition;
        staffPositionNumber = this.diatonicPitchNumber;
        if (clef.notNil) {
            clef = FoscClef(clef);
            staffPositionNumber = staffPositionNumber + clef.middleCPositionNumber;
        };
        staffPosition = FoscStaffPosition(staffPositionNumber);
        ^staffPosition;
    }
    /* --------------------------------------------------------------------------------------------------------
    • transpose

    x = FoscPitch('A4');
	x = x.transpose(semitones: 6);
	x.pitchName;
	-------------------------------------------------------------------------------------------------------- */
	transpose { |semitones|
		var respell, result;
		respell = case
		{ this.isSharpened } { \respellWithSharps }
		{ this.isFlattened } { \respellWithFlats };
		result = this.species.new(this.pitchNumber + semitones);
		if (respell.notNil && { result.isDiatonic.not }) { result = result.perform(respell) };
		^result;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS: DISPLAY
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • show
	-------------------------------------------------------------------------------------------------------- */
	show {
		^this.notYetImplemented;
	}
	/* --------------------------------------------------------------------------------------------------------
    • play
	-------------------------------------------------------------------------------------------------------- */
	play {
		^this.notYetImplemented;
	}
	/* --------------------------------------------------------------------------------------------------------
    • inspect

    FoscPitch("C#5").inspect;
	-------------------------------------------------------------------------------------------------------- */
	inspect {
		super.inspect(#['pitchName', 'pitchNumber']);
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatSpecification

    - abjad 2.21
    def _get_format_specification(self):
    import abjad
    return abjad.FormatSpecification(
        self,
        coerce_for_equality=True,
        repr_is_indented=False,
        storage_format_args_values=[self.name],
        storage_format_is_indented=False,
        storage_format_kwargs_names=['arrow'],
        )
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatSpecification {
        ^FoscFormatSpecification(
            coerceForEquality: true,
            reprIsIndented: false,
            storageFormatArgsValues: [this.name],
            storageFormatIsIndented: false,
            storageFormatKwargsNames: #['arrow']
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prListFormatContributions
    
    - abjad 2.2.1
    def _list_format_contributions(self):
        contributions = []
        if self.arrow is None:
            return contributions
        override_string = r'\once \override Accidental.stencil ='
        override_string += ' #ly:text-interface::print'
        contributions.append(override_string)
        string = 'accidentals.{}.arrow{}'
        string = string.format(self.accidental.name, str(self.arrow).lower())
        override_string = r'\once \override Accidental.text ='
        override_string += r' \markup {{ \musicglyph #"{}" }}'
        override_string = override_string.format(string)
        contributions.append(override_string)
        return contributions
    
    a = FoscStaff([FoscNote(FoscPitch("C4", arrow: 'down'), [1, 4])]);
    a.show;

    a = FoscStaff([FoscNote(FoscPitch("C#4", arrow: 'up'), [1, 4])]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prListFormatContributions {
        var contributions, overrideString, string;
        contributions = [];
        if (this.arrow.isNil) { ^contributions };
        overrideString = "\\once \\override Accidental.stencil =";
        overrideString = overrideString + "#ly:text-interface::print";
        contributions = contributions.add(overrideString);
        string = "accidentals.%.arrow%";
        // to be changed to: this.accidental.name (also change in FoscAccidental, FoscNoteHead)
        string = string.format(this.accidental.unabbreviatedName, this.arrow.asString.toLower);
        overrideString = "\\once \\override Accidental.text =";
        overrideString = overrideString + "\\markup { \\musicglyph #\"%\" }";
        overrideString = overrideString.format(string);
        contributions = contributions.add(overrideString);
        ^contributions;
    }
}
/* ------------------------------------------------------------------------------------------------------------
• FoscPitchInitializer
------------------------------------------------------------------------------------------------------------ */
FoscPitchInitializer {
	var <pitchClass, <accidental, <octave, <arrow;
}
/* ------------------------------------------------------------------------------------------------------------
• FoscNamedPitch

a = FoscPitch("C4", arrow: 'up');
a.pitchNumber;
a.str;
------------------------------------------------------------------------------------------------------------ */
FoscNamedPitch : FoscPitchInitializer {
	var <pitchName;
	*new { |pitchName, arrow|
		^if (pitchName.asString.isPitchName ){
            super.new.init(pitchName.asString, arrow);
        } {
            throw("Can not initialize % from value: %".format(this.species, pitchName));
        };
	}
	init { |argPitchName, argArrow|
		pitchName = argPitchName;
		pitchClass = FoscPitchClass(pitchName);
		accidental = pitchClass.accidental;
		octave = FoscOctave(pitchName);
        arrow = argArrow;
	}
}
/* ------------------------------------------------------------------------------------------------------------
• FoscNumberedPitch


a = FoscPitch(60.25);
a.str;
a.pitchNumber;

a = FoscPitch(60.75);
a.str;
a.pitchNumber;
------------------------------------------------------------------------------------------------------------ */
FoscNumberedPitch : FoscPitchInitializer {
    var <pitchNumber;
	*new { |pitchNumber|
		^if (pitchNumber.isNumber) {
			super.new.init(pitchNumber);
		} {
			throw("Can not initialize % from value: %".format(this.species, pitchNumber));
		};
	}
	init { |argPitchNumber|
        var list, index;

		pitchNumber = argPitchNumber;
		pitchClass = FoscPitchClass(pitchNumber);
		accidental = pitchClass.accidental;
		octave = FoscOctave(((pitchNumber / 12) - 1).floor);

        list = #[0, 0.25, 0.5, 0.75];
        index = list.indexOf(pitchNumber.frac.nearestInList(list));
        arrow = switch(index, 1, 'up', 3, 'down', nil);
	}
}
