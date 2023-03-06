/* ------------------------------------------------------------------------------------------------------------
• FoscPitchManager


!!!TODO
- should a FoscTuning be passed as an argument at initialisation?
- default to FoscTuning:current when no argument is provided?
- if 'tuning' == FoscTuning:current, switch on receiver for broadcasts from FoscTuning (when current updated)

!!!TODO
- a 'simplifyPitchName' method - see: https://abjad.github.io/_modules/abjad/pitch/pitches.html#NamedPitch.simplify

!!!TODO
- when 'tuning' is nil, default to et24 accidental spellings
- see: https://abjad.github.io/_mothballed/pitch-conventions.html#default-accidental-spelling

FoscPitchManager.tuning.name
------------------------------------------------------------------------------------------------------------ */
FoscPitchManager : Fosc {
	classvar <tuning;
    classvar accidentalRegex, diatonicPitchClassRegex, octaveRegex, pitchClassRegex, pitchRegex;
    classvar diatonicPitchClassNameToDiatonicPitchClassNumber;
    classvar diatonicPitchClassNumberToDiatonicPitchClassName;
    classvar respellWithFlat;
    classvar respellWithSharp;
    classvar respellWithDefault;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	*initClass {
        diatonicPitchClassNameToDiatonicPitchClassNumber =
            ('c': 0, 'd': 2, 'e': 4, 'f': 5, 'g': 7, 'a': 9, 'b': 11);

        diatonicPitchClassNumberToDiatonicPitchClassName =
            (0: 'c', 2: 'd', 4: 'e', 5: 'f', 7: 'g', 9: 'a', 11: 'b');

        respellWithDefault = ('ds': 'ef', 'as': 'bf');
        respellWithSharp = ();
        respellWithFlat = ();

        StartUp.add {
            if (tuning.isNil) { this.tuning_(FoscTuning.et24) };
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *tuning

    Get 'tuning'.

    FoscPitchManager.tuning.name;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • *tuning_

    Called when FoscTuning:current is set.

    FoscPitchManager.tuning_('et72');
    FoscPitchManager.tuning.name;
    -------------------------------------------------------------------------------------------------------- */
    *tuning_ { |ltuning|
        case 
        { ltuning.isString } {
            ltuning = ltuning.asSymbol;
        }
        { ltuning.isKindOf(Symbol) } {
            ltuning = FoscTuning.perform(ltuning);
        }
        { ltuning.isKindOf(FoscTuning) } {
            // pass
        }
        { ltuning.isNil } {
            ltuning = FoscTuning.et24;
        }
        {
            ^throw("Bad argument for %:%: %.".format(this.species, thisMethod.name, tuning));
        };

        tuning = ltuning;

        this.prUpdateRegexes;
        this.prUpdateRespellLibrary;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC CLASS METHODS: TESTING
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • *isAccidentalName


    • Example 1
    
    FoscPitchManager.isAccidentalName("s");
    FoscPitchManager.isAccidentalName("f");
    FoscPitchManager.isAccidentalName("ss");
    FoscPitchManager.isAccidentalName("qs");
    FoscPitchManager.isAccidentalName("tqf");
    FoscPitchManager.isAccidentalName("");


    • Example 2 - non-default tuning

    Fosc.tuning.name;
	FoscPitchManager.isAccidentalName("xs"); // fails when FoscTuning:current is 'et24'

	FoscTuning.et72.accidentalNames;
    FoscTuning.current = FoscTuning.et72;
    FoscPitchManager.isAccidentalName("xs"); // succeeds when FoscTuning:current is 'et72'
    -------------------------------------------------------------------------------------------------------- */
    *isAccidentalName { |name|
        name = name.asString;
        if (name.isEmpty) { ^true };
        ^"^(%)$".format(accidentalRegex).matchRegexp(name);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *isDiatonicPitchClassName


    • Example 1

    FoscPitchManager.isDiatonicPitchClassName("c");
    FoscPitchManager.isDiatonicPitchClassName("a");
    FoscPitchManager.isDiatonicPitchClassName("cs");
    -------------------------------------------------------------------------------------------------------- */
    *isDiatonicPitchClassName { |name|
        ^"^(%)$".format(diatonicPitchClassRegex).matchRegexp(name.asString);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *isOctaveName


    • Example 1

    FoscPitchManager.isOctaveName("'");
    FoscPitchManager.isOctaveName(",,,");
    FoscPitchManager.isOctaveName("");
    -------------------------------------------------------------------------------------------------------- */
    *isOctaveName { |name|
        name = name.asString;
        if (name.isEmpty) { ^true };
        ^"^(%)$".format(octaveRegex).matchRegexp(name);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *isPitchClassName


	• Example 1

    FoscTuning.current = FoscTuning.et24;
    FoscPitchManager.isPitchClassName("c");
    FoscPitchManager.isPitchClassName("cs");
    FoscPitchManager.isPitchClassName("css");
    FoscPitchManager.isPitchClassName("dtqf");
    FoscPitchManager.isPitchClassName("cs'");   // fails when pitchName
    -------------------------------------------------------------------------------------------------------- */
    *isPitchClassName { |name|
        ^"^(%)$".format(pitchClassRegex).matchRegexp(name.asString);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *isPitchName


    • Example 1

    FoscTuning.current = FoscTuning.et24;
    FoscPitchManager.isPitchName("c");
    FoscPitchManager.isPitchName("ctqf,,,");
    FoscPitchManager.isPitchName("dss''");
    -------------------------------------------------------------------------------------------------------- */
    *isPitchName { |name|
        ^"^(%)$".format(pitchRegex).matchRegexp(name.asString);
    }
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC CLASS METHODS: CONVERSION
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• *accidentalNameToSemitones


	• Example 1

	FoscTuning.current = FoscTuning.et24;
    FoscPitchManager.accidentalNameToSemitones("s");
    FoscPitchManager.accidentalNameToSemitones("ff");
    FoscPitchManager.accidentalNameToSemitones("qs");
    FoscPitchManager.accidentalNameToSemitones("tqf");


    FoscPitchManager.accidentalNameToSemitones("foo");
	-------------------------------------------------------------------------------------------------------- */
    *accidentalNameToSemitones { |name|
        var index, result;

        if (this.isAccidentalName(name).not) {
            ^throw("%:%: accidental name is not in FoscTuning: '%'."
                .format(tuning.species, thisMethod.name, name));
        };

        index = tuning.accidentalNames.collect { |each| each.asSymbol };
        index = index.indexOf(name.asSymbol);
        result = tuning.alterations[index];

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    *diatonicPitchClassNameToDiatonicPitchClassNumber


    • Example 1

    FoscPitchManager.diatonicPitchClassNameToDiatonicPitchClassNumber("d");
    FoscPitchManager.diatonicPitchClassNameToDiatonicPitchClassNumber("ds");
    FoscPitchManager.diatonicPitchClassNameToDiatonicPitchClassNumber("ef'");
    -------------------------------------------------------------------------------------------------------- */
    *diatonicPitchClassNameToDiatonicPitchClassNumber { |name|
        ^diatonicPitchClassNameToDiatonicPitchClassNumber[name.asString[0].asSymbol];
    }
    /* --------------------------------------------------------------------------------------------------------
    • *diatonicPitchClassNumberToDiatonicPitchClassName


    • Example 1

    FoscPitchManager.diatonicPitchClassNumberToDiatonicPitchClassName(0);
    FoscPitchManager.diatonicPitchClassNumberToDiatonicPitchClassName(5);
    FoscPitchManager.diatonicPitchClassNumberToDiatonicPitchClassName(1);
    -------------------------------------------------------------------------------------------------------- */
    *diatonicPitchClassNumberToDiatonicPitchClassName { |number|
        ^diatonicPitchClassNumberToDiatonicPitchClassName[number];
    }
    /* --------------------------------------------------------------------------------------------------------
    *midinoteToPitchName


    • Example 1

    FoscTuning.current = FoscTuning.et24;
    FoscPitchManager.midinoteToPitchName(67);
    FoscPitchManager.midinoteToPitchName(48);
    FoscPitchManager.midinoteToPitchName(61, 'flat');
    -------------------------------------------------------------------------------------------------------- */
    *midinoteToPitchName { |midinote, accidental|
        var pitchClassName, octaveNumber, octaveName, result;

        pitchClassName = this.pitchClassNumberToPitchClassName(midinote);
        octaveNumber = (midinote / 12).floor.asInteger - 1;
        octaveName = FoscPitchManager.octaveNumberToOctaveName(octaveNumber);
        result = pitchClassName ++ octaveName;
        result = this.respellPitchName(result, accidental);

        ^result;
    }
	/* --------------------------------------------------------------------------------------------------------
    • *octaveNameToOctaveNumber


    • Example 1

    FoscPitchManager.octaveNameToOctaveNumber("''");
    FoscPitchManager.octaveNameToOctaveNumber(",,,,");
    FoscPitchManager.octaveNameToOctaveNumber("");
    -------------------------------------------------------------------------------------------------------- */
    *octaveNameToOctaveNumber { |name|
        ^FoscOctave(name).number;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *octaveNumberToOctaveName


    • Example 1

    FoscPitchManager.octaveNumberToOctaveName(4);
    FoscPitchManager.octaveNumberToOctaveName(-2);
    FoscPitchManager.octaveNumberToOctaveName(3);
    -------------------------------------------------------------------------------------------------------- */
    *octaveNumberToOctaveName { |number|
    	^FoscOctave(number).name;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *pitchClassNameToPitchClassNumber


    • Example 1

    FoscPitchManager.pitchClassNameToPitchClassNumber("c");
    FoscPitchManager.pitchClassNameToPitchClassNumber("cqf");
    FoscPitchManager.pitchClassNameToPitchClassNumber("cs");

    
    • Example 2 - with a non-default tuning

    FoscTuning.current = FoscTuning.et72;
    FoscPitchManager.pitchClassNameToPitchClassNumber("cts");
    FoscPitchManager.pitchClassNameToPitchClassNumber("cxs");
    FoscPitchManager.pitchClassNameToPitchClassNumber("ctrf");
    FoscPitchManager.pitchClassNameToPitchClassNumber("cfts");
    FoscPitchManager.pitchClassNameToPitchClassNumber("cs");
    -------------------------------------------------------------------------------------------------------- */
    *pitchClassNameToPitchClassNumber { |name|
        var diatonicPCName, accidentalName, diatonicPCNumber, alteration;

        # diatonicPCName, accidentalName = this.prSplitLilypondPitchName(name);
        diatonicPCNumber = this.diatonicPitchClassNameToDiatonicPitchClassNumber(diatonicPCName);
        alteration = this.accidentalNameToSemitones(accidentalName) % 12;

        ^diatonicPCNumber + alteration;
    }
	/* --------------------------------------------------------------------------------------------------------
	*pitchClassNumberToPitchClassName

    !!!TODO: make a library at init: pitchClassNumberToPitchClassName = ();


	• Example 1

	FoscTuning.current = FoscTuning.et24;
	FoscPitchManager.pitchClassNumberToPitchClassName(7);
	FoscPitchManager.pitchClassNumberToPitchClassName(60);
    FoscPitchManager.pitchClassNumberToPitchClassName(61);
    FoscPitchManager.pitchClassNumberToPitchClassName(61, 'flat');
	FoscPitchManager.pitchClassNumberToPitchClassName(2.5);
    FoscPitchManager.pitchClassNumberToPitchClassName(2.5, 'flat');
	-------------------------------------------------------------------------------------------------------- */
    *pitchClassNumberToPitchClassName { |number, accidental|
        var integer, frac, alterations, index, accidentalName, diatonicPitchClassName, result;

        integer = number.floor.asInteger % 12;
        frac = number.frac;
        
        //!!!TODO: this is defaulting to sharps, but it could be determined by default accidental mapping
        // see abjad default spellings at top of this file
        if ([1,3,6,8,10].includes(integer)) {
            integer = integer - 1;
            frac = frac + 1;
        };
        
        alterations = tuning.alterations;
        frac = frac.nearestInList(alterations); // nearest approximation
        index = alterations.indexOf(frac);
        accidentalName = tuning.accidentalNames[index];
        diatonicPitchClassName = FoscPitchManager.diatonicPitchClassNumberToDiatonicPitchClassName(integer);
        result = diatonicPitchClassName ++ accidentalName;
        result = this.respellPitchClassName(result, accidental);

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *pitchNameToMidinote


    • Example 1

    FoscPitchManager.pitchNameToMidinote("c'");
    FoscPitchManager.pitchNameToMidinote("cqf'");
    FoscPitchManager.pitchNameToMidinote("cs'");

    
    • Example 2 - with a non-default tuning

    FoscTuning.current = FoscTuning.et72;
    FoscPitchManager.pitchNameToMidinote("cts'");
    FoscPitchManager.pitchNameToMidinote("cxs'");
    FoscPitchManager.pitchNameToMidinote("ctrf'");
    FoscPitchManager.pitchNameToMidinote("cfts'''");
    FoscPitchManager.pitchNameToMidinote("cs'");
    -------------------------------------------------------------------------------------------------------- */
    *pitchNameToMidinote { |name|
        var diatonicPCName, accidentalName, octaveName, pitchClassNumber, octaveNumber, alteration, result;

        # diatonicPCName, accidentalName, octaveName = this.prSplitLilypondPitchName(name);
        pitchClassNumber = FoscPitchManager.diatonicPitchClassNameToDiatonicPitchClassNumber(diatonicPCName);
        octaveNumber = FoscPitchManager.octaveNameToOctaveNumber(octaveName);
        alteration = this.accidentalNameToSemitones(accidentalName);
        result = 12 + (octaveNumber * 12) + pitchClassNumber + alteration;

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *pitchStringToPitches

    Parse lilypond pitch string by notes and chords.

    m = "c' <cs' ds'> ef' g <c' e' g'>";
    p = FoscPitchManager.pitchStringToPitches(m);
    p.do { |each| each.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    *pitchStringToPitches { |string|
        var result, names, pitches;

        result = [];
        names = this.prSplitLilypondPitchString(string);
        
        names.do { |each|
            if (each[0] == $<) {
                pitches = this.pitchStringToPitches(each[1..(each.lastIndex - 1)]);
                result = result.add(pitches);
            } {
                result = result.add(FoscPitch(each));
            };
        };

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *respellPitchClassName

    !!!TODO: specify rules for when and when not to respell -- see exceptions below


    • Example 1

    FoscTuning.current = FoscTuning.et24;

    FoscPitchManager.respellPitchClassName("df'", 'sharp');      // YES
    FoscPitchManager.respellPitchClassName("ds'", 'flat');       // YES
    
    FoscPitchManager.respellPitchClassName("bs", 'flat');        // NO
    FoscPitchManager.respellPitchClassName("dss", 'flat');       // NO
    FoscPitchManager.respellPitchClassName("cf", 'sharp');       // NO

    FoscPitchManager.respellPitchClassName("cqs", 'flat');       // YES 
    FoscPitchManager.respellPitchClassName("ctqf", 'no');        // YES
    
    FoscPitchManager.respellPitchClassName("c", 'sharp');        // YES
    FoscPitchManager.respellPitchClassName("c", 'flat');         // YES
    -------------------------------------------------------------------------------------------------------- */
    *respellPitchClassName { |name, accidental|
        var pitchClass, newName;

        if (this.isPitchName(name).not) {
            ^throw("%:%: pitch class name not found in FoscTuning: current: '%'.".
                format(this.species, thisMethod.name, name));
        };

        pitchClass = FoscPitchClass(name);
        newName = pitchClass.name.asSymbol;

        case 
        { accidental == 'sharp' } {
            newName = respellWithSharp[newName] ?? { newName };
        }
        { accidental == 'flat' } {
            newName = respellWithFlat[newName] ?? { newName };
        }
        { accidental.isNil } {
            newName = respellWithDefault[newName] ?? { newName };
        };

        ^newName.asString;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *respellPitchName

    !!!TODO: specify rules for when and when not to respell -- see exceptions below


    • Example 1

    FoscTuning.current = FoscTuning.et24;

    FoscPitchManager.respellPitchName("df'", 'sharp');      // YES
    FoscPitchManager.respellPitchName("ds'", 'flat');       // YES
    
    FoscPitchManager.respellPitchName("bs", 'flat');        // NO
    FoscPitchManager.respellPitchName("dss", 'flat');       // NO
    FoscPitchManager.respellPitchName("cf", 'sharp');       // NO

    FoscPitchManager.respellPitchName("cqs", 'flat');       // YES 
    FoscPitchManager.respellPitchName("ctqf", 'no');        // YES
    
    FoscPitchManager.respellPitchName("c", 'sharp');        // YES
    FoscPitchManager.respellPitchName("c", 'flat');         // YES
    -------------------------------------------------------------------------------------------------------- */
    *respellPitchName { |name, accidental='sharp'|
        var pitchClassName, octave;

        pitchClassName = this.respellPitchClassName(name, accidental);
        octave = FoscPitch(name).octave;
        
        if (name[0] == $c && { pitchClassName[0] == $b }) { octave = octave - 1 };
        if (name[0] == $b && { pitchClassName[0] == $c }) { octave = octave + 1 };

        ^pitchClassName ++ octave.str;
    }
    /* --------------------------------------------------------------------------------------------------------
	• *semitonesToAccidentalName

    !!!TODO: rename 'alterationToAccidentalName' for consistent naming (incl. with FoscTuning) ???
    !!!TODO: create a library at instantiation:

    tuning.tuples.do { |each|
        # alteration, accidentalName = each;
        accidentalName = accidentalName.asSymbol;
        semitonesToAccidentalName[alteration] = accidentalName;
    };

	
	• Example 1

	FoscPitchManager.semitonesToAccidentalName(1);
    FoscPitchManager.semitonesToAccidentalName(0);
    FoscPitchManager.semitonesToAccidentalName(0.5);
    FoscPitchManager.semitonesToAccidentalName(-1.5);
    FoscPitchManager.semitonesToAccidentalName(0.9);    // gets nearest match


    • Example 2 - with a non-default tuning

    FoscTuning.current = FoscTuning.et72;
    FoscPitchManager.semitonesToAccidentalName(1/3);
    FoscPitchManager.semitonesToAccidentalName(-1/3);
	-------------------------------------------------------------------------------------------------------- */
    *semitonesToAccidentalName { |semitones, nearestMatch=true|
        var nearest, alterations, index, accidentalNames;

        alterations = tuning.alterations;
        if (nearestMatch) { semitones = semitones.nearestInList(alterations) };
        index = alterations.indexOf(semitones);
        if (index.isNil) { ^nil };
        accidentalNames = tuning.accidentalNames;
        
        ^accidentalNames[index];
    }
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prSplitLilypondPitchString

    Splits lilypond pitch string by notes and chords. Returns an Array of lilypond pitch name strings.

    !!!TODO: check that pitch names are valid

    m = "c' <cs' ds'> ef' g <c' e' g'>";
    FoscPitchManager.prSplitLilypondPitchString(m);
    -------------------------------------------------------------------------------------------------------- */
    *prSplitLilypondPitchString { |string|
        var result, matches, chordIndices, noteIndices, allIndices;

        result = [];
        matches = string.findRegexp("<.*?>");
        chordIndices = matches.collect { |pair| (pair[0]..(pair[0] + pair[1].size)) };
        noteIndices = (0..string.lastIndex).symmetricDifference(chordIndices.flat);
        noteIndices = noteIndices.separate { |a, b| (b - a) != 1 || { string[a] == Char.space } };
        allIndices = (chordIndices ++ noteIndices).sort { |a, b| a[0] < b[0] };
        allIndices.do { |indices| result = result.add(string.copyRange(indices.first, indices.last)) };
        result = result.collect { |each| each.strip($ ) };
        result = result.reject { |each| each.isEmpty };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prUpdateRegexes
    -------------------------------------------------------------------------------------------------------- */
    *prUpdateRegexes {
        accidentalRegex = tuning.accidentalNames.select { |str| str.notEmpty };
        accidentalRegex = "(%)".format(accidentalRegex.join("|"));
        diatonicPitchClassRegex = "[a-g]";
        octaveRegex = "(,+|'+)";
        pitchClassRegex = "%%?".format(diatonicPitchClassRegex, accidentalRegex);
        pitchRegex = "%%?%?".format(diatonicPitchClassRegex, accidentalRegex, octaveRegex);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prUpdateRespellLibrary
    -------------------------------------------------------------------------------------------------------- */
    *prUpdateRespellLibrary {
        var alterations, accidentalNames, diatonicPitchClassNames, flatIndices, sharpIndices, m, n, j;
        var index, current, prev, next, flat, sharp;

        alterations = tuning.alterations;
        accidentalNames = tuning.accidentalNames;
        diatonicPitchClassNames = #['a','b','c','d','e','f','g'];
        flatIndices = [];
        sharpIndices = [];

        alterations.do { |ratio, i|
            if (ratio <= 0) { flatIndices = flatIndices.add(i) };
            if (ratio >= 0) { sharpIndices = sharpIndices.add(i) };
        };

        m = (flatIndices.size / 2).asInteger;
        n = m * 2;

        diatonicPitchClassNames.do { |pitchClassName|
            index = diatonicPitchClassNames.indexOf(pitchClassName);
            current = pitchClassName;
            prev = diatonicPitchClassNames.wrapAt(index - 1);
            next = diatonicPitchClassNames.wrapAt(index + 1);

            // naturals, flats -> sharps
            flatIndices.do { |index, i|
                j = if (#['c','f'].includes(pitchClassName)) { i + m } { i + n };
   
                if (alterations[j] > 0 ) {
                    flat = (current ++ accidentalNames[index]).asSymbol;
                    sharp = prev ++ accidentalNames[j];
                    respellWithSharp[flat] = sharp;
                };
            };

            // naturals, sharps -> flats
            sharpIndices.do { |index, i|
                j = if (#['b','e'].includes(pitchClassName)) { i + m } { i };

                if (alterations[j] < 0) {
                    sharp = (current ++ accidentalNames[index]).asSymbol;
                    flat = next ++ accidentalNames[j];
                    respellWithFlat[sharp] = flat;
                };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prSplitLilypondPitchName

    !!!TODO: retrieve regex strings from class variables

    FoscPitchManager.prSplitLilypondPitchName("cxf'''");
    FoscPitchManager.prSplitLilypondPitchName("cxf");
    FoscPitchManager.prSplitLilypondPitchName("c,,");
    FoscPitchManager.prSplitLilypondPitchName("c");
    -------------------------------------------------------------------------------------------------------- */
    *prSplitLilypondPitchName { |name|
        var diatonicPitchClassName, accidentalName, octaveName;
        
        name = name.asString;    
        diatonicPitchClassName = name[0];
        octaveName = name[1..].findRegexp("(,+|'+)");
        octaveName = if (octaveName.notEmpty) { octaveName[0][1] } { "" };
        accidentalName = name[1..].findRegexp("[a-z]+");
        accidentalName = if (accidentalName.notEmpty) { accidentalName[0][1] } { "" };

        ^[diatonicPitchClassName, accidentalName, octaveName];
    }
}
