/* ------------------------------------------------------------------------------------------------------------
• FoscPitchClass

inspect(FoscPitchClass("B"));
inspect(FoscPitchClass(11.5));
inspect(FoscPitchClass("D~"));
inspect(FoscPitchClass("C#4"));
inspect(FoscPitchClass("A#4"));
inspect(FoscPitchClass(FoscPitch("Db5")));
inspect(FoscPitchClass("cs")); // NOT YET IMPLEMENTED FOR lilypond INPUT
inspect(FoscPitchClass("cs,,,")); // NOT YET IMPLEMENTED FOR lilypond INPUT
inspect(FoscPitchClass(7.67)); // rounded to nearest quarter-tone

FoscPitchClass("x"); //!!! SHOULD BREAK GRACEFULLY WITH ERROR MESSAGE
"x".isPitchClassName;
"x".isPitchName;
------------------------------------------------------------------------------------------------------------ */
FoscPitchClass : FoscObject {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <pitchClassName;
	classvar manager;
	*new { |val|
		var pitchClassName;

		manager = FoscPitchNameManager;

		if (val.isKindOf(Symbol)) { val = val.asString };

		pitchClassName = case
		{ val.isNumber } { manager.pitchClassNumberToPitchClassName(val % 12) }
		{ val.isString } {
			case
			{ val.isPitchClassName } { val }
			{ val.isPitchName } { val.pitchClassName }
			//{ val.isLilyPondPitchClassName } { FoscLilyPondToName(val) } //!!! waiting for Converter
			//{ val.isLilyPondPitchName } { FoscLilyPondToName(val).pitchClassName }
		}
		{ val.isKindOf(FoscPitch) } { val.pitchClassName }
		{ val.isKindOf(FoscPitchClass) } { val.pitchClassName }
		{ Error("Can not initialize % from value: %".format(this.name, val)).throw };

		^super.new.init(pitchClassName);
	}
	init { |argPitchClassName|
		pitchClassName = argPitchClassName;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// SPECIAL
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• 
	def __format__(self, format_specification=''):
        r'''Formats component.

        Set `format_specification` to `''`, `'lilypond'` or `'storage'`.

        Returns string.
        '''
        from abjad.tools import systemtools
        if format_specification in ('', 'lilypond'):
            return self._lilypond_format
        elif format_specification == 'storage':
            return systemtools.StorageFormatAgent(self).get_storage_format()
        return str(self)
	-------------------------------------------------------------------------------------------------------- */
  	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// NB: all of the following SPECIAL methods are defined in abjad:Pitch but not in abjad:PitchClass
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
  	/* --------------------------------------------------------------------------------------------------------
	• add (abjad: __add__)
	'''Adds `arg` to pitch class.

	Returns new pitch class.
	'''

	x = FoscPitchClass('C#4');
	x = x + 2;
	x.pitchClassName;
	-------------------------------------------------------------------------------------------------------- */
    add { |expr|
    	var pitch;
		pitch = FoscPitch(this).add(expr);
		^this.species.new(pitch);
    }
	/* --------------------------------------------------------------------------------------------------------
    • asFloat (abjad: __float__)
	'''Changes pitchClassNumber to float.

	Returns float.
	'''

	x = FoscPitchClass('C4');
    x.pitchClassNumber.class;
	-------------------------------------------------------------------------------------------------------- */
    asFloat {
    	^this.pitchClassNumber.asFloat;
    }
	/* --------------------------------------------------------------------------------------------------------
    • asInteger (abjad: __int__)

	'''Changes numbered pitch to integer.
	
	Returns integer.
	'''
 	
	x = FoscPitchClass('C+4');
    x.asInt;
	-------------------------------------------------------------------------------------------------------- */
    asInteger {
    	^this.pitchClassNumber.asInteger;
    }
    /* --------------------------------------------------------------------------------------------------------
    • == (abjad: __eq__)
    a = FoscPitchClass('C#');
    b = FoscPitchClass('Db');
    c = FoscPitchClass('B');
  	a == a;
    a == b; // Enharmonic equivalents are treated as equal
    a == c;
	-------------------------------------------------------------------------------------------------------- */
    == { |expr|
    	^(this.pitchClassNumber == FoscPitchClass(expr).pitchClassNumber);
    }
    /* --------------------------------------------------------------------------------------------------------
    • !=
    a = FoscPitchClass('C#');
    b = FoscPitchClass('Db');
    c = FoscPitchClass('B');
  	a != a;
    a != b; // Enharmonic equivalents are treated as equal
    a != c;
	-------------------------------------------------------------------------------------------------------- */
    != { |expr|
    	^(this == expr).not;
    }
	/* --------------------------------------------------------------------------------------------------------
    • <
	'''Is true when `arg` can be coerced to a numbered pitch and when this numbered pitch is less than `arg`. Otherwise false.

	Returns true or false.
	'''

    a = FoscPitchClass('C#4');
    b = FoscPitchClass('D#4');
    c = FoscPitchClass('B3');
    a < b;
    a < c;
	-------------------------------------------------------------------------------------------------------- */
    < { |expr|
    	^(this.pitchClassNumber < FoscPitchClass(expr).pitchClassNumber);
    }
    /* --------------------------------------------------------------------------------------------------------
    • <=
    a = FoscPitchClass('C#4');
    b = FoscPitchClass('D#4');
    c = FoscPitchClass('B3');
    a <= b;
    a <= c;
	-------------------------------------------------------------------------------------------------------- */
    <= { |expr|
    	^(this.pitchClassNumber <= FoscPitchClass(expr).pitchClassNumber);
    }
	/* --------------------------------------------------------------------------------------------------------
    • neg

	'''Negates pitchClassNumber. Equivalent to inversion around 0.

	Returns new PitchClass.
	'''
	
	a = FoscPitchClass('D');
	b = a.neg;
	b.pitchClassNumber;
	-------------------------------------------------------------------------------------------------------- */
	neg {
		^this.species.new(this.pitchClassNumber.neg);
	}
	/* --------------------------------------------------------------------------------------------------------
    • str (abjad: __str__)
    
    a = FoscPitchClass('C#');
    a.str;
	-------------------------------------------------------------------------------------------------------- */
    str {
    	^this.lilypondPitchClassName;
    }
	/* --------------------------------------------------------------------------------------------------------
    • sub
    //••• !!! TODO: update to use NumberedInterval
    def __sub__(self, arg):
        r'''Subtracts `arg` from numbered pitch.

        Returns numbered interval.
        '''
        from abjad.tools import pitchtools
        if isinstance(arg, type(self)):
            return pitchtools.NumberedInterval.from_pitch_carriers(
                self, arg)
        else:
            interval = arg
            return pitchtools.transpose_pitch_carrier_by_interval(
                self, -interval)
    -------------------------------------------------------------------------------------------------------- */
    // sub { |expr|
    // 	^(this.pitchNumber - expr);
    // } 

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • accidental
	-------------------------------------------------------------------------------------------------------- */
	accidental {
		^FoscAccidental(this.accidentalName);
	}
	/* --------------------------------------------------------------------------------------------------------
    • accidentalName
	-------------------------------------------------------------------------------------------------------- */
	accidentalName {
		^pitchClassName.asString.accidentalName;
	}
	/* --------------------------------------------------------------------------------------------------------
    • alterationInSemitones
	-------------------------------------------------------------------------------------------------------- */
	alterationInSemitones {
		^this.accidental.semitones;
	}
   	/* --------------------------------------------------------------------------------------------------------
    • applyAccidental
	-------------------------------------------------------------------------------------------------------- */
	applyAccidental { |accidental|
		^this.notYetImplemented(thisMethod);
	}
	/* --------------------------------------------------------------------------------------------------------
    • diatonicPitchClassName
	-------------------------------------------------------------------------------------------------------- */
	diatonicPitchClassName {
		^pitchClassName.asString.diatonicPitchClassName;
	}
	/* --------------------------------------------------------------------------------------------------------
    • diatonicPitchClassNumber
	-------------------------------------------------------------------------------------------------------- */
	diatonicPitchClassNumber {
		^manager.pitchClassNameToDiatonicPitchClassNumber(pitchClassName);
	}
	/* --------------------------------------------------------------------------------------------------------
    • lilypondPitchClassName

    a = FoscPitchClass(10);
    a.lilypondPitchClassName;
	-------------------------------------------------------------------------------------------------------- */
	lilypondPitchClassName {
		^manager.pitchClassNameToLilyPondPitchClassName(pitchClassName);
	}
	/* --------------------------------------------------------------------------------------------------------
    • invert
    
    Inverts pitch-class about `axis`.

	Returns new pitch-class.


	a = FoscPitchClass(11);
	b = a.invert(10);
	b.pitchClassNumber;
	-------------------------------------------------------------------------------------------------------- */
	invert { |axis|
		var pitch;
		pitch = FoscPitch(this).invert(axis);
		^this.species.new(pitch);
	}
  	/* --------------------------------------------------------------------------------------------------------
    • multiply
    
    Multiplies pitch-class by `n`.

	Returns new pitch-class.

	a = FoscPitchClass(7);
	b = a.multiply(2);
	b.pitchClassNumber;
	-------------------------------------------------------------------------------------------------------- */
	multiply { |n|
		var pitch;
		pitch = FoscPitch(this).multiply(n);
		^this.species.new(pitch);
	}
	/* --------------------------------------------------------------------------------------------------------
    • pitchClassNumber
	-------------------------------------------------------------------------------------------------------- */
	pitchClassNumber {
		^this.diatonicPitchClassNumber + this.alterationInSemitones;
	}
	/* --------------------------------------------------------------------------------------------------------
    • transpose
    
    Transposes pitch-class by `n`'.

	Returns new pitch-class.

	a = FoscPitchClass(11);
	b = a.transpose(9);
	b.pitchClassNumber;
	b.pitchClassName;
	-------------------------------------------------------------------------------------------------------- */
	transpose { |expr|
		var pitch;
		pitch = FoscPitch(this).transpose(expr);
		^this.species.new(pitch);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// DISPLAY
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • inspect
	-------------------------------------------------------------------------------------------------------- */
	inspect {
		super.inspect(#[
			accidental, accidentalName, alterationInSemitones, diatonicPitchClassName, diatonicPitchClassNumber,
			pitchClassNumber
		]);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • 
    def _get_format_specification(self):
        if type(self).__name__.startswith('Named'):
            values = [str(self)]
        else:
            values = [
                mathtools.integer_equivalent_number_to_integer(float(self))
                ]
        return systemtools.FormatSpecification(
            client=self,
            coerce_for_equality=True,
            storage_format_is_indented=False,
            storage_format_args_values=values,
            template_names=['pitch_class_name'],
            )
	-------------------------------------------------------------------------------------------------------- */
}
/* ---------------------------------------------------------------------------------------------------------------
• FoscNamedPitchClass
//!!!TODO
--------------------------------------------------------------------------------------------------------------- */
FoscNamedPitchClass : FoscPitchClass {
}
/* ---------------------------------------------------------------------------------------------------------------
• FoscNumberedPitchClass
//!!!TODO
--------------------------------------------------------------------------------------------------------------- */
FoscNumberedPitchClass : FoscPitchClass {
}
