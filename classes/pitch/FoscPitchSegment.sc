/* ------------------------------------------------------------------------------------------------------------
• FoscPitchSegment

• test 1
n = [60, 62, 64];
FoscPitchSegment(n).cs;

• test 2
n = "Bb4 F#5 C4 Cb4 E+4 G4 D+5";
FoscPitchSegment(n).cs;

• test 3: FoscPitchSegment does not wrap FoscPitchSegments
n = "Bb4 F#5 <C4 Cb4> E+4 G4 D+5";
FoscPitchSegment(n).cs;
------------------------------------------------------------------------------------------------------------ */
FoscPitchSegment : FoscSegment {
    var <player;
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	*new { |items|
        items = FoscPitchParser(items);
		^super.new(items, FoscPitch);
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • asCompileString
    
    FoscPitchSegment([60, 62, 64]).cs;
    -------------------------------------------------------------------------------------------------------- */
    // asCompileString {
    //     var pitchesStr;
    //     pitchesStr = this.pitches.collect { |each| each.cs }.join(",\n\t");
    //     ^"%([\n\t%\n])".format(this.species, pitchesStr);
    // }
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[this.pitches]
    }
    /* --------------------------------------------------------------------------------------------------------
    • str

    FoscPitchSegment([60, 62, 64]).str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^"<%>".format("".scatList(this.pitches.collect { |each| each.str })[1..]);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • pitches
    -------------------------------------------------------------------------------------------------------- */
    pitches {
        ^this.items;
    }
    /* --------------------------------------------------------------------------------------------------------
	• pitchNumbers
	-------------------------------------------------------------------------------------------------------- */
	pitchNumbers {
		^this.items.collect { |each| each.pitchNumber };
	}
	/* --------------------------------------------------------------------------------------------------------
	• pitchNames
	-------------------------------------------------------------------------------------------------------- */
	pitchNames {
		^this.items.collect { |each| each.pitchName };
	}
    /* --------------------------------------------------------------------------------------------------------
    • pitchString
    -------------------------------------------------------------------------------------------------------- */
	pitchString {
        var pitchNames;
        pitchNames = this.pitchNames.collect { |each| each.asString };  
        ^pitchNames.join(" ").asCompileString;
    }
    /* --------------------------------------------------------------------------------------------------------
    • invert
	'''Inverts pitch segment about `axis`.

	Returns new pitch segment.
	'''

	a = FoscPitchSegment([60, 61, 62, 63]);
	b = a.invert(66);
	b.pitchNumbers;

	a = FoscPitchSegment([60, 61, 62, 63]);
	b = a.invert;
	b.pitchNumbers;
	-------------------------------------------------------------------------------------------------------- */
	invert { |axis|
		axis = axis ?? { this.items[0] };
		^this.species.new(this.items.collect { |each| each.invert(axis) });
	}
	/* --------------------------------------------------------------------------------------------------------
    • isEquivalentUnderTransposition

	'''True if pitch set is equivalent to `expr` under transposition.
	Otherwise false.

	Returns true or false.
	'''

	a = FoscPitchSegment([60, 61, 62]);
    b = FoscPitchSegment([61, 62, 63]);
    c = FoscPitchSegment([60, 61, 63]);
    isEquivalentUnderTransposition(a, b);
    isEquivalentUnderTransposition(a, c);
    isEquivalentUnderTransposition(a, a);
	-------------------------------------------------------------------------------------------------------- */
    isEquivalentUnderTransposition { |expr|
    	if (expr.isKindOf(this.species).not) { ^false };
    	if (this.size != expr.size) { ^false };
    	^((this.pitchNumbers - expr.pitchNumbers).asSet.size == 1);
    }
	/* --------------------------------------------------------------------------------------------------------
    • makeNotes
    def make_notes(self, n=None, written_duration=None):
        r'''Makes first `n` notes in pitch segment.

        Set `n` equal to `n` or length of segment.

        Set `written_duration` equal to `written_duration` or ``1/8``:

        ::

            >>> notes = named_pitch_segment.make_notes()
            >>> staff = Staff(notes)
            >>> show(staff) # doctest: +SKIP

        ..  doctest::

            >>> f(staff)
            \new Staff {
                bf,8
                aqs8
                fs'8
                g'8
                bqf8
                g'8
            }

        Allows nonassignable `written_duration`:

        ::

            >>> notes = named_pitch_segment.make_notes(4, Duration(5, 16))
            >>> staff = Staff(notes)
            >>> time_signature = TimeSignature((5, 4))
            >>> attach(time_signature, staff)
            >>> show(staff) # doctest: +SKIP

        ..  doctest::

            >>> f(staff)
            \new Staff {
                \time 5/4
                bf,4 ~
                bf,16
                aqs4 ~
                aqs16
                fs'4 ~
                fs'16
                g'4 ~
                g'16
            }

        Returns list of notes.
        '''
        from abjad.tools import durationtools
        from abjad.tools import scoretools
        n = n or len(self)
        written_duration = written_duration or durationtools.Duration(1, 8)
        result = scoretools.make_notes([0] * n, [written_duration])
        for i, logical_tie in enumerate(iterate(result).by_logical_tie()):
            pitch = self[i % len(self)]
            for note in logical_tie:
                note.written_pitch = pitch
        return result
	-------------------------------------------------------------------------------------------------------- */
	makeNotes {
		^this.notYetImplemented;
	}
	/* --------------------------------------------------------------------------------------------------------
     • multiply
	''Multiplies pitch segment.
	
	Returns new pitch segment.
   	
   	a = FoscPitchSegment([60, 61, 62]);
	b = a.multiply(3);
	b.pitchNumbers;
	-------------------------------------------------------------------------------------------------------- */
	multiply { |n|
		^this.species.new(this.items.collect { |each| each.multiply(n) });
	}
	/* --------------------------------------------------------------------------------------------------------
    • reverse
	'''Retrograde of pitch segment.

	Returns new pitch segment.
  	'''

 	a = FoscPitchSegment([60, 61, 62]);
	b = a.retrograde;
	b.pitchNumbers;
	-------------------------------------------------------------------------------------------------------- */
	reverse {
		^this.species.new(this.items.reverse);
	}
	/* --------------------------------------------------------------------------------------------------------
    • rotate
	'''Rotates pitch segment.

	Returns new pitch segment.
	'''

	a = FoscPitchSegment([60, 61, 62]);
	b = a.rotate(-1);
	b.pitchNumbers;

	a = FoscPitchSegment([60, 61, 62]);
	b = a.rotate(-1, transpose: true);
	b.pitchNumbers;
	-------------------------------------------------------------------------------------------------------- */
	rotate { |n, transpose=false|
		var rotatedPitches, newSegment, interval;
		rotatedPitches = this.items.rotate(n);
		newSegment = this.species.new(rotatedPitches);
		if (transpose) {
			//!!! TODO interval = newSegment[0] - this[0];
			interval = this[0].pitchNumber - newSegment[0].pitchNumber;
			if (this[0] != newSegment[0]) { newSegment = newSegment.transpose(interval) };
		};
		^newSegment;
	}
	/* --------------------------------------------------------------------------------------------------------
    • transpose
	'''Transposes pitch segment by `expr`.

	Returns new pitch segment.
	'''

	a = FoscPitchSegment([60, 61, 62]);
	b = a.transpose(1);
	b.pitchNumbers;

    a = FoscPitchSegment([60, 61, 62]);
    b = a.transpose([12, 14, 16]);
    b.pitchNumbers;
	-------------------------------------------------------------------------------------------------------- */
	transpose { |expr|
        if (expr.isSequenceableCollection) {
            ^this.species.new(this.items.collect { |each, i| each.transpose(expr.wrapAt(i)) });
        } {
            ^this.species.new(this.items.collect { |each| each.transpose(expr) });
        };
	}
	/* --------------------------------------------------------------------------------------------------------
	• hasDuplicates
	'''True if pitch segment has duplicate items. Otherwise false.
	
	Returns true or false.
	'''

 	a = FoscPitchSegment([60, 61, 62]);
 	a.hasDuplicates;

 	a = FoscPitchSegment([60, 60, 61, 62]);
 	a.hasDuplicates;
	-------------------------------------------------------------------------------------------------------- */
	hasDuplicates {
		^(FoscPitchSet(this.items).size < this.size);
	}
    /* --------------------------------------------------------------------------------------------------------
    • midicps (abjad: hertz)
	'''Gets hertz of pitches in pitch segment.
	
	Returns tuple.
	'''
	
	a = FoscPitchSegment([60, 61, 62]);
	a.midicps;
	-------------------------------------------------------------------------------------------------------- */
	midicps {
		^this.items.collect { |each| each.midicps };
	}
    /* --------------------------------------------------------------------------------------------------------
    • inflectionPointCount

    Inflection point count of pitch segment.

	Returns nonnegative integer.
	
	a = FoscPitchSegment([60, 61, 62, 63, 64, 65, 66, 68, 81, 65, 59, 63, 84, 60]);
  	a.inflectionPointCount.postln;
	-------------------------------------------------------------------------------------------------------- */
	inflectionPointCount {
		^(this.localMinima.size + this.localMaxima.size);
	}
    /* --------------------------------------------------------------------------------------------------------
    • localMaxima
    
    Local maxima of pitch segment.

	Returns array.

  	a = FoscPitchSegment([60, 61, 62, 63, 64, 65, 66, 68, 81, 65, 59, 63, 84, 60]);
  	a.localMaxima.collect { |each| each.pitchNumber }.postln;
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
	
	a = FoscPitchSegment([60, 61, 62, 63, 64, 65, 66, 68, 81, 65, 59, 63, 84, 60]);
  	a.localMinima.collect { |each| each.pitchNumber }.postln;
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

	a = FoscPitchSegment([60, 61, 62, 63, 64, 65, 66, 68, 81, 65, 59, 63, 84, 60]);
  	a.maxItem.pitchNumber.postln;
	-------------------------------------------------------------------------------------------------------- */
	maxItem { |func|
		^this.items.maxItem(func);
	}
	/* --------------------------------------------------------------------------------------------------------
    • minItem

	a = FoscPitchSegment([60, 61, 62, 63, 64, 65, 66, 68, 81, 65, 59, 63, 84, 60]);
  	a.minItem.pitchNumber.postln;
	-------------------------------------------------------------------------------------------------------- */
	minItem { |func|
		^this.items.minItem(func);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS: DISPLAY
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• illustrate

    a = FoscPitchSegment([60, 61, 62, 63, 64, 65, 66, 68, 81, 65, 59, 63, 84, 60]);
    a.illustrate.format;
    a.show;
	-------------------------------------------------------------------------------------------------------- */
 	illustrate {
        ^FoscLilypondFile.pitch([this]);
    }
	/* --------------------------------------------------------------------------------------------------------
    • inspect
	-------------------------------------------------------------------------------------------------------- */
	// inspect {
	// 	//^this.notYetImplemented(thisMethod);
	// 	collection.do { |each| each.inspect };
	// }
	/* --------------------------------------------------------------------------------------------------------
    • play
	-------------------------------------------------------------------------------------------------------- */
	play {
		var selection;
        selection = FoscLeafMaker().(this, [1/4]);
        player = selection.play;
	}
	/* --------------------------------------------------------------------------------------------------------
    • show

    a = FoscPitchSegment([60, 61, 62, 63, 64, 65, 66, 68, 81, 65, 59, 63, 84, 60]);
    a.show;
	-------------------------------------------------------------------------------------------------------- */
	// show {
	// 	^this.illustrate.show;
	// }
}
