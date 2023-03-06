/* ------------------------------------------------------------------------------------------------------------
• FoscPitch

!!!TODO
- Do not round 'midinote' when initialised with a number
- Get nearest matching pitch name, but only round when a FoscTuning is active

a = FoscPitch(60.1);
a.midinote;


!!!TODO:
- Establish consistent use of 'midinote' and 'pitchNumber' in: FoscPitch, FoscPitchSegment, FoscPitchSet, FoscPitchClassSet, FoscPitchManager, FoscTuning, FoscOctave
- Perhaps create a utitilty class for bridging between SC and Fosc idioms, e.g. 'midinote', 'note', 'degree', 'octave', etc. to 'pitchNumber', etc.


!!! add pitchNumber method (FoscPitch("cs'").pitchNumber == 1)
!!! possibly call pitchNumber methods 'note' for SC pattern compatability (but confusion with FoscNote!!)



• Example 1 - Initialize with a lilypond string and get some properties.

a = FoscPitch("cs'");
a.name;
a.str;
a.format;
a.accidental.cs;
a.octave.cs;
a.pitchClass.cs;
a.cps;
a.isFlattened;
a.isSharpened;
a.midinote;



//!!! TODO: a.respell(accidental: 'sharp'); 

//!!! TODO: BROKEN
a.respellWithSharps;
a.respellWithFlats;
a.invert(FoscPitch("c'"));
a.transpose(5).cs;

a.illustrate.format;
a.show;


• Example 2 - Initialize with a pitch number

a = FoscPitch(0.5);
a.str;
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscPitch : Fosc {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <name;
	*new { |name|
        ^super.new.init(name);
	}
	init { |argName|
        name = argName;

        case
        { name.isString && { FoscPitchManager.isPitchName(name) } } {
            // pass
        }
        { name.isKindOf(Symbol) } {
            ^FoscPitch(name.asString);
        }
        { name.isNumber } {
            name = FoscPitchManager.midinoteToPitchName(name);
            ^FoscPitch(name);
        }
        { name.isKindOf(FoscPitchClass) } {
            ^FoscPitch(name.name);
        }
        { name.isKindOf(FoscPitch) } {
            ^name;
        }
        { 
            ^throw("Can't initialize % from value: %".format(this.species, name));
        };
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS: SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • ==

    a = FoscPitch("cs'");
    b = FoscPitch("df'");
    c = FoscPitch("b");
    a == a;
    a == b;     // Enharmonic equivalents are treated as equal
    a == c;

    m = a.midinote;
    n = b.midinote;
    m == n;

    Fosc
    -------------------------------------------------------------------------------------------------------- */
    // == { |expr|
    //     ^this.midinote == FoscPitch(expr).midinote;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • !=

    a = FoscPitch("cs'");
    b = FoscPitch("df'");
    c = FoscPitch("b");
    a != b;     // Enharmonic equivalents are treated as equal
    a != c;
    a != a;
    -------------------------------------------------------------------------------------------------------- */
    != { |expr|
        //^this != expr;
        ^(this.midinote != FoscPitch(expr).midinote);
    }
    /* --------------------------------------------------------------------------------------------------------
    • >
    
    Is true when arg can be coerced to a pitch and when this pitch is greater than arg. Otherwise false.

    Returns true or false.
    

    a = FoscPitch("cs'");
    b = FoscPitch("ds'");
    c = FoscPitch("b");
    a > b;
    a > c;
    a > a;
    -------------------------------------------------------------------------------------------------------- */
    > { |expr|
        ^this.midinote > FoscPitch(expr).midinote;
    }
    /* --------------------------------------------------------------------------------------------------------
    • >=

    Is true when arg can be coerced to a pitch and when this pitch is greater than or equal to arg. Otherwise false.

    Returns true or false.

    a = FoscPitch("cs'");
    b = FoscPitch("ds'");
    c = FoscPitch("b");
    a >= a;
    a >= b;
    a >= c;
    -------------------------------------------------------------------------------------------------------- */
    >= { |expr|
        ^(this.midinote >= FoscPitch(expr).midinote);
    }
    /* --------------------------------------------------------------------------------------------------------
    • <
    
    Is true when arg can be coerced to a pitch and when this pitch is less than arg. Otherwise false.

    Returns true or false.

    a = FoscPitch("cs'");
    b = FoscPitch("ds'");
    c = FoscPitch("b");
    a < b;
    a < c;
    a < a;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • <=

    Is true when arg can be coerced to a pitch and when this pitch is less than or equal to arg. Otherwise false.

    Returns true or false.

    a = FoscPitch("cs'");
    b = FoscPitch("ds'");
    c = FoscPitch("b");
    a <= a;
    a <= b;
    a <= c;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • add

    • Example 1

    x = FoscPitch("cs");
    x = x + 2;
    x.str;


    • Example 2

    x = FoscPitch("cs");
    x = x + FoscPitch("d");
    x.str;
    -------------------------------------------------------------------------------------------------------- */
    add { |expr|
        var semitones, respell, result;
        
        semitones = case
        { expr.isKindOf(FoscPitch) } { expr.midinote }
        { expr.isNumber } { expr }
        {
            ^throw("Bad argument type for %:%: '%'".format(this.species, thisMethod.name, expr));
        };

        // respell = case
        // { this.isSharpened } { 'respellWithSharp' }
        // { this.isFlattened } { 'respellWithFlat' };
        
        result = this.species.new(this.midinote + semitones);
        //if (respell.notNil && { result.isDiatonic.not }) { result = result.perform(respell) };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • format
    
    a = FoscPitch("cs'");
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • neg

    Negates 'midinote'. Equivalent to inversion around 0.

    Returns new PitchClass.
    
    a = FoscPitch("c");
    b = a.neg;
    b.midinote;
    -------------------------------------------------------------------------------------------------------- */
    neg {
        ^this.species.new(this.midinote.neg);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    a = FoscPitch("cs'");
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^"FoscPitch(%)".format(this.str.cs);
    }
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs

    a = FoscPitch("cs'");
    a.storeArgs;
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[this.str];
    }
    /* --------------------------------------------------------------------------------------------------------
    • str
    
	a = FoscPitch("cs'");
    a.str;

    a = FoscPitch('C#4');
    a.str;

    a = FoscPitch('C#+4');
    a.str;
	-------------------------------------------------------------------------------------------------------- */
    str {
    	^this.name;
    }
    /* --------------------------------------------------------------------------------------------------------
    • sub

    • Example 1

    x = FoscPitchClass("cs");
    x = x - 2;
    x.str;


    • Example 2

    x = FoscPitchClass("cs");
    x = x - FoscPitchClass("d");
    x.str;
    -------------------------------------------------------------------------------------------------------- */
    sub { |expr|
        ^(this + expr.neg);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE PROPERTIES
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • accidental

    a = FoscPitch("cs'");
    a.accidental;
    a.accidental.name;
    a.accidental.semitones;
    -------------------------------------------------------------------------------------------------------- */
    accidental {
        ^this.pitchClass.accidental;
    }
    /* --------------------------------------------------------------------------------------------------------
    • cps

    a = FoscPitch("a'");
    a.cps;
    -------------------------------------------------------------------------------------------------------- */
    cps {
        ^this.midinote.midicps;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isFlattened

    a = FoscPitch("cs'");
    a.isFlattened;
    -------------------------------------------------------------------------------------------------------- */
    isFlattened {
        ^this.pitchClass.isFlattened;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isSharpened

    a = FoscPitch("cs'");
    a.isSharpened;
    -------------------------------------------------------------------------------------------------------- */
    isSharpened {
        ^this.pitchClass.isSharpened;
    }
    /* --------------------------------------------------------------------------------------------------------
    • midinote

    a = FoscPitch("cs'");
    a.midinote;

    a = FoscPitch("ctqf'");
    a.midinote;

    a = FoscPitch("c,");
    a.midinote;
    -------------------------------------------------------------------------------------------------------- */
    midinote {
        ^this.prDiatonicMidinote + this.prAlteration;
    }
    /* --------------------------------------------------------------------------------------------------------
    • octave

    a = FoscPitch("cs'");
    a.octave;
    a.octave.name;
    a.octave.number;
    -------------------------------------------------------------------------------------------------------- */
    octave {
        ^FoscOctave(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • pitchClass

    a = FoscPitch("cs'");
    a.pitchClass;
    a.pitchClass.name;
    a.pitchClass.number;
    -------------------------------------------------------------------------------------------------------- */
    pitchClass {
        ^FoscPitchClass(this);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • illustrate

    a = FoscPitch("cs''");
    a.illustrate.format;

    btf


    Fosc.tuning_('et72');
    a = FoscPitch("cfts'");
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |paperSize, staffSize, includes|
        var note, lincludes, lilypondFile;

        note = FoscNote(this, 1/4);
        if (this < 55) { note.attach(FoscClef('bass')) };
        lincludes = ["%/noteheads.ily".format(Fosc.stylesheetDirectory)];
        if (Fosc.tuning.notNil) { lincludes = lincludes ++ [Fosc.tuning.stylesheetPath] };
        if (includes.notNil) { lincludes = lincludes ++ includes };
        
        lilypondFile = FoscLilyPondFile(
            [note],
            paperSize: paperSize,
            includes: lincludes,
            staffSize: staffSize
        );
        
        ^lilypondFile;
    }
    /* --------------------------------------------------------------------------------------------------------
    • invert
    
    Inverts pitch about 'axis'.

    Returns new pitch.


    • Example 1

    a = FoscPitch(59);
    b = a.invert(60);
    b.midinote;
    b.str;


    • Example 2

    a = FoscPitch("b");
    b = a.invert(FoscPitch("c'"));
    b.midinote;
    b.str;
    -------------------------------------------------------------------------------------------------------- */
    invert { |axis|
        axis = this.species.new(axis);
        ^(axis + (axis - this));
    }
    /* --------------------------------------------------------------------------------------------------------
    • respell

    def respell(self, accidental="sharps"):
        """
        Respells named pitch with ``accidental``.

        ..  container:: example

            >>> abjad.NamedPitch("cs").respell(accidental="flats")
            NamedPitch('df')

            >>> abjad.NamedPitch("df").respell(accidental="sharps")
            NamedPitch('cs')

        """
        if accidental == "sharps":
            dictionary = _pitch_class_number_to_pitch_class_name_with_sharps
        else:
            assert accidental == "flats"
            dictionary = _pitch_class_number_to_pitch_class_name_with_flats
        name = dictionary[self.pitch_class.number]
        candidate = type(self)((name, self.octave.number))
        if candidate.number == self.number - 12:
            candidate = type(self)(candidate, octave=candidate.octave.number + 1)
        elif candidate.number == self.number + 12:
            candidate = type(self)(candidate, octave=candidate.octave.number - 1)
        assert candidate.number == self.number
        return candidate
    -------------------------------------------------------------------------------------------------------- */
    respell {
        ^this.notYetImplemented;
    }
    /* --------------------------------------------------------------------------------------------------------
    • simplify

    !!!TODO: should this return the smallest possible accidental alteration ??

    Reduce alteration to between -2 and 2 while maintaining identical pitch number.

    Returns named pitch.


    >>> abjad.NamedPitch("cssqs'").simplify()
    NamedPitch("dqs'")

    >>> abjad.NamedPitch("cfffqf'").simplify()
    NamedPitch('aqf')

    >>> float(abjad.NamedPitch("cfffqf'").simplify()) == float(abjad.NamedPitch('aqf'))
    True

    note:: LilyPond by default only supports accidentals from double-flat to double-sharp.

     def simplify(self):
        alteration = self._get_alteration()
        if abs(alteration) <= 2:
            return self
        diatonic_pc_number = self._get_diatonic_pc_number()
        octave = int(self.octave)
        while alteration > 2:
            step_size = 2
            if diatonic_pc_number == 2:  # e to f
                step_size = 1
            elif diatonic_pc_number == 6:  # b to c
                step_size = 1
                octave += 1
            diatonic_pc_number = (diatonic_pc_number + 1) % 7
            alteration -= step_size
        while alteration < -2:
            step_size = 2
            if diatonic_pc_number == 3:  # f to e
                step_size = 1
            elif diatonic_pc_number == 0:  # c to b
                step_size = 1
                octave -= 1
            diatonic_pc_number = (diatonic_pc_number - 1) % 7
            alteration += step_size
        diatonic_pc_name = _lib._diatonic_pc_number_to_diatonic_pc_name[
            diatonic_pc_number
        ]
        accidental = Accidental(alteration)
        octave = Octave(octave)
        pitch_name = f"{diatonic_pc_name}{accidental!s}{octave!s}"
        return type(self)(pitch_name, arrow=self.arrow)
    -------------------------------------------------------------------------------------------------------- */
    simplify {
        ^this.notYetImplemented;
    }
    /* --------------------------------------------------------------------------------------------------------
    • transpose

    Transpose pitch by 'semitones'.


    • Example 1

	x = FoscPitch(67);
	x = x.transpose(semitones: 6);
	x.midinote;


    • Example 2 - spells the resulting pitch with the same accidental as the original pitch

    x = FoscPitch("gs'");
    x = x.transpose(semitones: 7);
    x.str;
	-------------------------------------------------------------------------------------------------------- */
	transpose { |semitones|
        ^(this + semitones);
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAlteration

    a = FoscPitch("cs'");
    a.prAlteration;
    -------------------------------------------------------------------------------------------------------- */
    prAlteration {
        ^this.accidental.semitones;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prApplyAccidental

    a = FoscPitch("cs'");
    a.prApplyAccidental("f").str;

    a = FoscPitch("bf'");
    a.prApplyAccidental(FoscAccidental("f")).str;
    -------------------------------------------------------------------------------------------------------- */
    prApplyAccidental { |accidental|
        var diatonicPitchClassName; 
        diatonicPitchClassName = this.prDiatonicPitchClassName;
        accidental = this.accidental + FoscAccidental(accidental);
        ^this.species.new("%%%".format(diatonicPitchClassName, accidental.str, this.octave.str));
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDiatonicMidinote
    
    a = FoscPitch("cs'");
    a.prDiatonicMidinote;

    a = FoscPitch("fs'");
    a.prDiatonicMidinote;

    a = FoscPitch("fs");
    a.prDiatonicMidinote;
    -------------------------------------------------------------------------------------------------------- */
    prDiatonicMidinote {
        ^this.prDiatonicPitchClassNumber + this.octave.midinote;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDiatonicPitchClassName

    a = FoscPitch("cs'");
    a.prDiatonicPitchClassName;
    -------------------------------------------------------------------------------------------------------- */
    prDiatonicPitchClassName {
        ^this.pitchClass.prDiatonicPitchClassName;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDiatonicPitchClassNumber

    a = FoscPitch("cs'");
    a.prDiatonicPitchClassNumber;
    -------------------------------------------------------------------------------------------------------- */
    prDiatonicPitchClassNumber {
        ^this.pitchClass.prDiatonicPitchClassNumber;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDiatonicPitchName

     a = FoscPitch("cs'");
    a.prDiatonicPitchName;
    -------------------------------------------------------------------------------------------------------- */
    prDiatonicPitchName {
        ^this.prDiatonicPitchClassName ++ this.octave.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prPitchClassName

    a = FoscPitch("cs'");
    a.prPitchClassName;
    -------------------------------------------------------------------------------------------------------- */
    prPitchClassName {
        ^this.prDiatonicPitchClassName ++ this.accidental.str;   
    }
    /* --------------------------------------------------------------------------------------------------------
    • prPitchClassNumber

    a = FoscPitch("df'");
    a.prPitchClassNumber;
    -------------------------------------------------------------------------------------------------------- */
    prPitchClassNumber {
        ^this.prDiatonicMidinote % 12;   
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRespell    

    FoscPitch("df'").prRespell('sharp').str;
    FoscPitch("df'").prRespell('flat').str;
    FoscPitch("c'").prRespell('flat').str;
    FoscPitch("c'").prRespell('sharp').str;
    -------------------------------------------------------------------------------------------------------- */
    prRespell { |accidental='sharp'|
        var name;
        name = FoscPitchManager.respellPitchName(this.name, accidental);
        ^this.species.new(name);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^this.str;
    }
}
