/* ------------------------------------------------------------------------------------------------------------
• FoscPitchClass


• Example 1 - Initialize with a lilypond string and get some properties.

a = FoscPitchClass("cs");
a.str;
a.format;
a.cs;
a.name;
a.accidental;
a.number;


• Example 2 - Initialize with a pitch class number.

a = FoscPitchClass(3.5);
a.cs;

Get nearest approximation in FoscTuning:current

a = FoscPitchClass(1/3);
a.number;
a.str;



a = FoscPitchClass("cs");
a.number;
a.name_("ef");
a.number;
a.name;
------------------------------------------------------------------------------------------------------------ */
FoscPitchClass : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <name, <accidental;
    *new { |name|
        ^super.new.init(name);
    }
    name_ { |lname|
        this.init(lname);
    }
    init { |argName|
        name = argName;

        case
        { name.isString && { FoscPitchManager.isPitchClassName(name) } } {
            // pass
        }
        { name.isString && { FoscPitchManager.isPitchName(name) } } {
            name = FoscPitchManager.prSplitLilypondPitchName(name)[..1].join;
            ^FoscPitchClass(name);
        }
        { name.isKindOf(Symbol) } {
            ^FoscPitchClass(name.asString);
        }
        { name.isNumber } {
            name = FoscPitchManager.midinoteToPitchName(name);
            ^FoscPitchClass(name);
        }
        { name.isKindOf(FoscPitch) } {
            ^FoscPitchClass(name.name);
        }
        { name.isKindOf(FoscPitchClass) } {
            ^name;
        }
        { 
            ^throw("Can't initialize % from value: %".format(this.species, name));
        };

        accidental = FoscAccidental(FoscPitchManager.prSplitLilypondPitchName(name)[1]);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    a = FoscPitchClass("cs");
    b = FoscPitchClass("ds");
    c = FoscPitchClass("b");
    a == a;
    a == b; // Enharmonic equivalents are treated as equal
    a == c;
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        ^(this.number == FoscPitchClass(expr).number);
    }
    /* --------------------------------------------------------------------------------------------------------
    • !=

    a = FoscPitchClass("cs");
    b = FoscPitchClass("ds");
    c = FoscPitchClass("b");
    a != a;
    a != b; // Enharmonic equivalents are treated as equal
    a != c;
    -------------------------------------------------------------------------------------------------------- */
    != { |expr|
        ^(this == expr).not;
    }
    /* --------------------------------------------------------------------------------------------------------
    • <

    Is true when 'expr' can be coerced to a numbered pitch and when this numbered pitch is less than 'expr'. Otherwise false.

    Returns true or false.
    

    a = FoscPitchClass("cs");
    b = FoscPitchClass("ds");
    c = FoscPitchClass("b");
    a < b;
    a < c;
    -------------------------------------------------------------------------------------------------------- */
    < { |expr|
        ^(this.number < FoscPitchClass(expr).number);
    }
    /* --------------------------------------------------------------------------------------------------------
    • <=

    a = FoscPitchClass("cs");
    b = FoscPitchClass("ds");
    c = FoscPitchClass("b");
    a <= b;
    a <= c;
    -------------------------------------------------------------------------------------------------------- */
    <= { |expr|
        ^(this.number <= FoscPitchClass(expr).number);
    }
    /* --------------------------------------------------------------------------------------------------------
    • add


    !!!TODO: respelling


    • Example 1

    x = FoscPitchClass("cs");
    x = x + 2;
    x.str;


    • Example 2

    x = FoscPitchClass("cs");
    x = x + FoscPitchClass("d");
    x.str;
    -------------------------------------------------------------------------------------------------------- */
    add { |expr|
        var semitones, respell, result;
        
        semitones = case
        { expr.isKindOf(FoscPitchClass) } { expr.number }
        { expr.isNumber } { expr }
        {
            ^throw("Bad argument type for %:%: '%'".format(this.species, thisMethod.name, expr));
        };

        respell = case
        { this.isSharpened } { 'respellWithSharp' }
        { this.isFlattened } { 'respellWithFlat' };
        
        result = this.species.new(this.number + semitones % 12);
        if (respell.notNil && { result.isDiatonic.not }) { result = result.perform(respell) };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString
    
    a = FoscPitchClass("cs");
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^"FoscPitchClass(%)".format(this.str.cs);
    }
    /* --------------------------------------------------------------------------------------------------------
    • format
    
    a = FoscPitchClass("cs");
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • neg

    Negates number. Equivalent to inversion around 0.

    Returns new PitchClass.
    
    a = FoscPitchClass('d');
    b = a.neg;
    b.number;
    -------------------------------------------------------------------------------------------------------- */
    neg {
        ^this.species.new(this.number.neg);
    }
    /* --------------------------------------------------------------------------------------------------------
    • str

    a = FoscPitchClass('cs');
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
    • isFlattened

    a = FoscPitchClass("cs");
    a.isFlattened;

    a = FoscPitchClass("ctqf");
    a.isFlattened;
    -------------------------------------------------------------------------------------------------------- */
    isFlattened {
        ^(this.accidental.semitones < 0);
    }
    /* --------------------------------------------------------------------------------------------------------
    • isSharpened

    a = FoscPitchClass("cs");
    a.isSharpened;

    a = FoscPitchClass("ctqf");
    a.isSharpened;
    -------------------------------------------------------------------------------------------------------- */
    isSharpened {
        ^(this.accidental.semitones > 0);
    }
    /* --------------------------------------------------------------------------------------------------------
    • name

    a = FoscPitchClass("cs");
    a.name;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • number

    a = FoscPitchClass("cs");
    a.number;
    -------------------------------------------------------------------------------------------------------- */
    number {
        var result; 

        result = FoscPitchManager.diatonicPitchClassNameToDiatonicPitchClassNumber(this.name);
        result = result + this.accidental.semitones;
        if (result.frac == 0) { result = result.asInteger };
        
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • invert
    
    Inverts pitch-class about 'axis'.

    Returns new pitch-class.


    • Example 1

    a = FoscPitchClass(11);
    b = a.invert(0);
    b.number;
    b.str;


    • Example 2

    a = FoscPitchClass("b");
    b = a.invert(FoscPitchClass("c"));
    b.number;
    b.str;
    -------------------------------------------------------------------------------------------------------- */
    invert { |axis|
        axis = this.species.new(axis);
        ^(axis - (this + axis));
    }
    /* --------------------------------------------------------------------------------------------------------
    • respell

    FoscPitchManager.respellPitchName("df'", 'sharp');      // YES
    FoscPitchManager.respellPitchName("ds'", 'flat');       // YES
    
    FoscPitchManager.respellPitchName("bs", 'flat');        // NO
    FoscPitchManager.respellPitchName("dss", 'flat');       // NO
    FoscPitchManager.respellPitchName("cf", 'sharp');       // NO

    FoscPitchManager.respellPitchName("cqs", 'flat');       // YES 
    FoscPitchManager.respellPitchName("ctqf", 'no');        // YES
    
    FoscPitchManager.respellPitchName("c", 'sharp');        // YES
    FoscPitchManager.respellPitchName("c", 'flat');         // YES


    m = FoscPitchClass("cs");
    m = m.respell('flat');
    m.cs;

    m = FoscPitchClass("df");
    m = m.respell('sharp');
    m.cs;

    m = FoscPitchClass("cqs");
    m = m.respell('flat');
    m.cs;

    m = FoscPitchClass("ctqs");
    m = m.respell('flat');
    m.cs;

    m = FoscPitchClass("dqf");
    m = m.respell('sharp');
    m.cs;

    m = FoscPitchClass("dtqf");
    m = m.respell('sharp');
    m.cs;



    Fosc.tuning = 'et72';

    m = FoscPitchClass("csts");
    m.cs;
    m.respell('flat').cs;
    m.respell('sharp').cs;

    m = FoscPitchClass("dftf");
    m.cs;
    m.respell('sharp').cs;

    m = FoscPitchClass("c");
    m.cs;
    m.respell('sharp').cs;


    //!!! NOT WORKING
    m = FoscPitchClass("bs");
    m.cs;
    m.respell('flat').cs;


    m = FoscPitchClass("cqs");
    m.accidental.semitones;
    -------------------------------------------------------------------------------------------------------- */
    // respell {
    //     var accidental, pitchClassName;

    //     if (this.accidental.semitones > 0) {
    //         accidental = 'flat';
    //     } {
    //         accidental = 'sharp';
    //     };

    //     pitchClassName = FoscPitchManager.respellPitchClassName(this.name, accidental);
        
    //     ^this.species.new(pitchClassName);
    // }
    respell { |accidental='sharp'|
        var pitchClassName;

        pitchClassName = FoscPitchManager.respellPitchClassName(this.name, accidental);
        
        ^this.species.new(pitchClassName);
    }
    /* --------------------------------------------------------------------------------------------------------
    • respellWithFlat

    !!! DEPRECATE

    a = FoscPitchClass("cs");
    b = a.respellWithFlat;
    b.str;
    -------------------------------------------------------------------------------------------------------- */
    // respellWithFlat {
    //     //!!!TODO: update to use FoscTuning
    //     var pitchClassName;
    //     pitchClassName = FoscPitchManager.pitchClassNumberToPitchClassNameWithFlats(this.number);
    //     ^this.species.new(pitchClassName);
    // }
    /* --------------------------------------------------------------------------------------------------------
    • respellWithSharp

    !!! DEPRECATE

    a = FoscPitchClass("df");
    b = a.respellWithSharp;
    b.str;
    -------------------------------------------------------------------------------------------------------- */
    // respellWithSharp {
    //     //!!!TODO: update to use FoscTuning
    //     var pitchClassName;
    //     pitchClassName = FoscPitchManager.pitchClassNumberToPitchClassNameWithSharps(this.number);
    //     ^this.species.new(pitchClassName);
    // }
    /* --------------------------------------------------------------------------------------------------------
    • transpose

    Transposes pitch-class by 'n''.

    Returns new pitch-class.

    a = FoscPitchClass(11);
    b = a.transpose(9);
    b.number;
    b.str;
    -------------------------------------------------------------------------------------------------------- */
    transpose { |expr|
        ^(this + expr);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAlteration

    a = FoscPitchClass("cs");
    a.prAlteration;
    -------------------------------------------------------------------------------------------------------- */
    prAlteration {
        ^this.accidental.semitones;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prApplyAccidental
    
    a = FoscPitchClass("c");
    a.prApplyAccidental("s").cs;

    a = FoscPitchClass("cs");
    a.prApplyAccidental("s").cs;
    -------------------------------------------------------------------------------------------------------- */
    prApplyAccidental { |accidental|
        accidental = this.accidental + FoscAccidental(accidental);
        ^this.species.new(this.prDiatonicPitchClassName ++ accidental.str);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDiatonicPitchClassName

    a = FoscPitchClass("c");
    a.prDiatonicPitchClassName;

    a = FoscPitchClass("cs");
    a.prDiatonicPitchClassName;
    -------------------------------------------------------------------------------------------------------- */
    prDiatonicPitchClassName {
        ^FoscPitchManager.diatonicPitchClassNumberToDiatonicPitchClassName(this.prDiatonicPitchClassNumber);
    }
    /* --------------------------------------------------------------------------------------------------------
    •  prDiatonicPitchClassNumber

    a = FoscPitchClass("c");
    a.prDiatonicPitchClassNumber;

    a = FoscPitchClass("cs");
    a.prDiatonicPitchClassNumber;
    -------------------------------------------------------------------------------------------------------- */
    prDiatonicPitchClassNumber {
        ^FoscPitchManager.diatonicPitchClassNameToDiatonicPitchClassNumber(this.name);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prLilypondFormat
    
    a = FoscPitchClass("cs");
    a.prLilypondFormat;
    -------------------------------------------------------------------------------------------------------- */
    prLilypondFormat {
        ^"%%".format(this.prDiatonicPitchClassName, this.accidental.str);
    }
}
