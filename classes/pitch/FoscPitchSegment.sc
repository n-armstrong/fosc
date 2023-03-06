/* ------------------------------------------------------------------------------------------------------------
• FoscPitchSegment

!!! TODO: lilypond chord notation
!!! TODO: file.layoutBlock.items.add(FoscLilyPondLiteral("\\accidentalStyle dodecaphonic"));
!!! TODO: 'clumps' (or 'partitionBySizes(sizes, annotate: true)')
!!! TODO: 'prAnnotatePartitions('horizontalBracket'/'slur'/'beam')'
!!! TODO: basic combinatorial methods: permutations, combinations, compositions, partitions, cartesianProduct
!!! TODO: add utility and transformation methods from Abjad


• Example 1 - initialize with midinotes

m = FoscPitchSegment(#[60,62,64]);
m.cs;
m.show;


• Example 2  - initialize with list of lilypond names

m = FoscPitchSegment(#["cqs'", "cs'", "bqf'"]);
m.cs;
m.show;


• Example 3  - initialize with a string of lilypond pitch names

m = FoscPitchSegment("cqs' cs' bqf'");
m.cs;
m.show;


• Example 4  - use a FoscTuning

FoscTuning.current = FoscTuning.et72;
m = FoscPitchSegment("cqs' cs' bqf' crf' etrs'");
m.cs;
m.show;


• Example 5  - initialize from a FoscSelection

m = FoscLeafMaker().((60, 61 .. 72), [1/4]);
m = FoscPitchSegment(m);
m.show;


• Example 6  - initialize from a FoscContainer

m = FoscStaff((60, 61 .. 72).collect { |each| FoscNote(each, 1/4) });
m = FoscPitchSegment(m);
m.show;


FoscPitchManager
------------------------------------------------------------------------------------------------------------ */
FoscPitchSegment : FoscTypedList {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	*new { |items|
        //items = FoscPitchParser(items);
        //!!!TODO: check 'items' is valid
        //!!!TODO: replace next line with FoscPitchManager:pitchStringToPitches
        //if (items.isString) { items = items.splitWhiteSpace };
        if (items.isString) { items = FoscPitchManager.pitchStringToPitches(items) };
        if (items.isKindOf(FoscSelection)) { ^this.newFromSelection(items) };
        if (items.isKindOf(FoscContainer)) { ^this.newFromSelection(items.select) };
        items = items.collect { |each| FoscPitch(each) };
		^super.new(items, FoscPitch);
	}
	/* --------------------------------------------------------------------------------------------------------
	• newFromSelection

    Makes pitch segment from 'selection'.

    Returns pitch segment.
    
        
    • Example 1

    a = FoscLeafMaker().((60..67).mirror, [1/4]);
    b = FoscPitchSegment.newFromSelection(a);
    b.show;
    -------------------------------------------------------------------------------------------------------- */
	*newFromSelection { |selection|
		var pitches;
        
        selection.doLogicalTies({ |logicalTie|
        	//!!! TODO: handle chords (writtenPitches)
            pitches = pitches.add(logicalTie[0].writtenPitch);
        }, pitched: true);

        ^this.new(pitches);
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • asCompileString
    
    m = FoscPitchSegment(#[60,62,64]);
	m.cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • ==
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        var exprItems;
        if (expr.isKindOf(this.species).not) { ^false };
        if (this.size != expr.size) { ^false };
        exprItems = expr.items;
        this.items.do { |each, i| if (each != exprItems[i]) { ^false } };
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • !=
    -------------------------------------------------------------------------------------------------------- */
    != { |expr|
        ^(this == expr).not
    }
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[this.pitches]
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• illustrate

	!!!TODO: compatability with FoscChord
	!!!TODO: if all pitches are in bass or treble registers, use one staff rather than piano score

	m = FoscPitchSegment(#[48,55,58,63,69].mirror);
	m.show;
	-------------------------------------------------------------------------------------------------------- */
	illustrate { |paperSize, staffSize, includes|
		var staff, selections, tweaks, score, includesPath, lilypondFile;

        staff = FoscStaff(FoscLeafMaker().(this.pitches, [1/8]));
        score = FoscScore.makePianoScore(staff);
        includesPath = Fosc.stylesheetDirectory;
        includes = includes ?? { ["%/noteheads.ily".format(includesPath)] };
        lilypondFile = score.illustrate(paperSize, staffSize, includes);
        
        ^lilypondFile;
	}
    /* --------------------------------------------------------------------------------------------------------
    • invert
	
	Inverts pitch segment about `axis`.

	Returns new pitch segment.


	a = FoscPitchSegment(#[60,61,62,63]);
	b = a.invert;
	b.midinotes;

	a = FoscPitchSegment(#[60,61,62,63]);
	b = a.invert(66);
	b.midinotes;
	-------------------------------------------------------------------------------------------------------- */
	invert { |axis|
		axis = axis ?? { this.items[0] };
		^this.species.new(this.items.collect { |each| each.invert(axis) });
	}
	/* --------------------------------------------------------------------------------------------------------
    • reverse
	
	Retrograde of pitch segment.

	Returns new pitch segment.

 	a = FoscPitchSegment(#[60,61,62]);
	b = a.reverse;
	b.midinotes;
	-------------------------------------------------------------------------------------------------------- */
	reverse {
		^this.species.new(this.items.reverse);
	}
	/* --------------------------------------------------------------------------------------------------------
    • rotate
	
	Rotates pitch segment.

	Returns new pitch segment.

	a = FoscPitchSegment(#[60,61,62]);
	b = a.rotate(-1);
	b.midinotes;
	-------------------------------------------------------------------------------------------------------- */
	rotate { |n, transpose=false|
		^this.species.new(this.items.rotate(n));
	}
	/* --------------------------------------------------------------------------------------------------------
    • transpose
	
	Transposes pitch segment by `expr`.

	Returns new pitch segment.

	a = FoscPitchSegment(#[60,61,62]);
	b = a.transpose(1);
	b.midinotes;

    a = FoscPitchSegment(#[60,61,62]);
    b = a.transpose(#[1,2,3]);
    b.midinotes;
	-------------------------------------------------------------------------------------------------------- */
	transpose { |expr|
        if (expr.isSequenceableCollection) {
            ^this.species.new(this.items.collect { |each, i| each.transpose(expr.wrapAt(i)) });
        } {
            ^this.species.new(this.items.collect { |each| each.transpose(expr) });
        };
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE PROPERTIES
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • contour

    a = FoscPitchSegment(#[60,61,62,61,60,59]);
    a.contour;
    -------------------------------------------------------------------------------------------------------- */
    contour {
    	var vals;
        vals = this.midinotes.asSet.as(Array).sort;
        ^this.midinotes.collect { |midinote| vals.indexOf(midinote) };
    }
    /* --------------------------------------------------------------------------------------------------------
    • cps
	
	a = FoscPitchSegment(#[60,61,62]);
	a.cps;
	-------------------------------------------------------------------------------------------------------- */
	cps {
		^this.items.collect { |each| each.cps };
	}
	/* --------------------------------------------------------------------------------------------------------
	• hasDuplicates
	
	True if pitch segment has duplicate items. Otherwise false.
	
	Returns true or false.

 	a = FoscPitchSegment(#[60,61,62]);
 	a.hasDuplicates;

 	a = FoscPitchSegment(#[60,60,61,62]);
 	a.hasDuplicates;
	-------------------------------------------------------------------------------------------------------- */
	hasDuplicates {
		^(FoscPitchSet(this.items).size < this.size);
	}
    /* --------------------------------------------------------------------------------------------------------
    • inflectionPointCount

    Inflection point count of pitch segment.

	Returns nonnegative integer.
	
	a = FoscPitchSegment(#[60,61,62,63,64,65,66,68,81,65,59,63,84,60]);
  	a.inflectionPointCount.postln;
	-------------------------------------------------------------------------------------------------------- */
	inflectionPointCount {
		^(this.localMinima.size + this.localMaxima.size);
	}
	/* --------------------------------------------------------------------------------------------------------
    • isEquivalentUnderTransposition

	True if pitch set is equivalent to `expr` under transposition. Otherwise false.

	Returns true or false.

	a = FoscPitchSegment(#[60,61,62]);
    b = FoscPitchSegment(#[61,62,63]);
    c = FoscPitchSegment(#[60,64,67]);

    a.isEquivalentUnderTransposition(b);
    a.isEquivalentUnderTransposition(c);
    a.isEquivalentUnderTransposition(a + 3);
    a.isEquivalentUnderTransposition(a.reverse);
	-------------------------------------------------------------------------------------------------------- */
    isEquivalentUnderTransposition { |expr|
    	if (expr.isKindOf(this.species).not) { ^false };
    	if (this.size != expr.size) { ^false };
    	^((this.midinotes - expr.midinotes).asSet.size == 1);
    }
    /* --------------------------------------------------------------------------------------------------------
    • localMaxima
    
    Local maxima of pitch segment.

	Returns array.

  	a = FoscPitchSegment(#[60,61,62,63,64,65,66,68,81,65,59,63,84,60]);
  	a.localMaxima.collect { |each| each.midinote }.postln;
	-------------------------------------------------------------------------------------------------------- */
	localMaxima {
		var result, left, middle, right;
		result = [];
		if (this.size > 3) {
			(this.size - 2).do { |i|
				# left, middle, right = this.atAll((i .. (i + 2)));
				if (left < middle && { right < middle }) { result = result.add(middle) };
			};
		};
		^result;
	}
    /* --------------------------------------------------------------------------------------------------------
    • localMinima
    
    Local minima of pitch segment.

 	Returns array;
	
	a = FoscPitchSegment(#[60,61,62,63,64,65,66,68,81,65,59,63,84,60]);
  	a.localMinima.collect { |each| each.midinote }.postln;
	-------------------------------------------------------------------------------------------------------- */
	localMinima {
		var result, left, middle, right;
		result = [];
		if (this.size > 3) {
			(this.size - 2).do { |i|
				# left, middle, right = this.atAll((i .. (i + 2)));
				if (middle < left && { middle < right }) { result = result.add(middle) };
			};
		};
		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
    • maxItem

	a = FoscPitchSegment(#[60,61,62,63,64,65,66,68,81,65,59,63,84,60]);
  	a.maxItem.midinote.postln;
	-------------------------------------------------------------------------------------------------------- */
	maxItem { |func|
		^this.items.maxItem(func);
	}
	/* --------------------------------------------------------------------------------------------------------
	• midinotes

	m = FoscPitchSegment(#["cqs'", "cs'", "bqf'"]);
    m.midinotes;
	-------------------------------------------------------------------------------------------------------- */
	midinotes {
		^this.items.collect { |each| each.midinote };
	}
	/* --------------------------------------------------------------------------------------------------------
    • minItem

	a = FoscPitchSegment(#[60,61,62,63,64,65,66,68,81,65,59,63,84,60]);
  	a.minItem.midinote.postln;
	-------------------------------------------------------------------------------------------------------- */
	minItem { |func|
		^this.items.minItem(func);
	}
	/* --------------------------------------------------------------------------------------------------------
	• names

	m = FoscPitchSegment(#["cqs'", "cs'", "bqf'"]);
    m.names;
	-------------------------------------------------------------------------------------------------------- */
	names {
		^this.items.collect { |each| each.str };
	}
	/* --------------------------------------------------------------------------------------------------------
    • pitches

    m = FoscPitchSegment(#["cqs'", "cs'", "bqf'"]);
    m.pitches.do { |each| each.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    pitches {
        ^this.items;
    }
}
