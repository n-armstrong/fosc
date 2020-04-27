/* ------------------------------------------------------------------------------------------------------------
• FoscPitchSet

x = FoscPitchSet([60, 61, 62]);
x.items;
x.collection;
x.size;
x.pitchNumbers;
x.pitchNames;

### NB: abjad.PitchSet subclasses from Set not TypedSet

FoscTypedSet
------------------------------------------------------------------------------------------------------------ */
FoscPitchSet : FoscTypedSet {
	// INIT ///////////////////////////////////////////////////////////////////////////////////////////////////
	*new { |items|
        //### some of the below is redundant -- it's handled in the superclasses
		if ([FoscPitchSet, FoscPitchSegment].any { |type| items.isKindOf(type) }) {
            items = items.items;
        } {
           if (items.isSequenceableCollection.not) { items = [items] };
           items = items.collect { |each| FoscPitch(each) };
           items = items.sort { |a, b| a.pitchNumber < b.pitchNumber };
        };
        //### assert(all items isKindOf(FoscPitch))
        ^super.new(items, FoscPitch);
	}
	/* --------------------------------------------------------------------------------------------------------
	• newFromSelection
	def from_selection(
        class_,
        selection,
        item_class=None,
        ):
        r'''Makes pitch set from `selection`.

        ::

            >>> staff_1 = Staff("c'4 <d' fs' a'>4 b2")
            >>> staff_2 = Staff("c4. r8 g2")
            >>> selection = select((staff_1, staff_2))
            >>> pitchtools.PitchSet.from_selection(selection)
            PitchSet(['c', 'g', 'b', "c'", "d'", "fs'", "a'"])

        Returns pitch set.
        '''
        from abjad.tools import pitchtools
        pitch_segment = pitchtools.PitchSegment.from_selection(selection)
        return class_(
            items=pitch_segment,
            item_class=item_class,
            )
    -------------------------------------------------------------------------------------------------------- */
	*newFromSelection {
		^this.notYetImplemented;
	}

	// PUBLIC /////////////////////////////////////////////////////////////////////////////////////////////////
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
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • difference

    Set-theoretic difference of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchSet([61, 62, 63]);
    b = FoscPitchSet([62, 63, 64]);
    a.difference(b).do { |each| each.pitchNumber.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • sect

    Set-theoretic intersection of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchSet([61, 62, 63]);
    b = FoscPitchSet([62, 63, 64]);
    a.sect(b).do { |each| each.pitchNumber.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isDisjoint

    Is true when typed receiver shares no elements with expr. Otherwise false.

    Returns boolean.

    a = FoscPitchSet([61, 62, 63], Number);
    b = FoscPitchSet([4], Number);
    c = FoscPitchSet([3, 4], Number);
    isDisjoint(a, b);
    isDisjoint(a, c);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isEmpty

    Is true when pitch class set is empty.

    Returns boolean.

    a = FoscPitchSet([61, 62, 63], Number);
    a.isEmpty;

    a = FoscPitchSet([], Number);
    a.isEmpty;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isSubsetOf

    Is true when receiver is a subset of expr. Otherwise false.

    Returns boolean.

    a = FoscPitchSet([61, 62, 63]);
    b = FoscPitchSet([62, 63]);
    a.isSubsetOf(b);
    b.isSubsetOf(a);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isSupersetOf

    Is true when receiver is a superset of expr. Otherwise false.

    Returns boolean.

    a = FoscPitchSet([61, 62, 63]);
    b = FoscPitchSet([62, 63]);
    a.isSupersetOf(b);
    b.isSupersetOf(a);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • notEmpty

    Is true when set is not empty.

    Returns boolean.

    a = FoscPitchSet([61, 62, 63], Number);
    a.notEmpty;

    a = FoscPitchSet([], Number);
    a.notEmpty;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • symmetricDifference

    Symmetric difference of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchSet([61, 62, 63]);
    b = FoscPitchSet([62, 63, 64]);
    a.symmetricDifference(b).do { |each| each.pitchNumber.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • union

    Union of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchSet([61, 62, 63]);
    b = FoscPitchSet([62, 63, 64]);
    a.union(b).do { |each| each.pitchNumber.postln };
    -------------------------------------------------------------------------------------------------------- */
	/* --------------------------------------------------------------------------------------------------------
	• invert
	a = FoscPitchSet([61, 62, 63]);
	b = a.invert(60);
	b.pitchNumbers;
	a.pitchNumbers;
    -------------------------------------------------------------------------------------------------------- */
    invert { |axis|
    	^this.species.new(this.items.collect { |each| each.invert(axis) });
    }
    /* --------------------------------------------------------------------------------------------------------
    • isEquivalentUnderTransposition
    a = FoscPitchSet([60, 61, 62]);
    b = FoscPitchSet([61, 62, 63]);
    c = FoscPitchSet([60, 61, 63]);
    isEquivalentUnderTransposition(a, b);
    isEquivalentUnderTransposition(a, c);
    isEquivalentUnderTransposition(a, a);


    def is_equivalent_under_transposition(self, expr):
        r'''True if pitch set is equivalent to `expr` under transposition.
        Otherwise false.

        Returns true or false.
        '''
        from abjad.tools import pitchtools
        if not isinstance(expr, type(self)):
            return False
        if not len(self) == len(expr):
            return False
        difference = -(pitchtools.NamedPitch(expr[0], 4) -
            pitchtools.NamedPitch(self[0], 4))
        new_pitches = (x + difference for x in self)
        new_pitches = new(self, items=new_pitch)
        return expr == new_pitches
	-------------------------------------------------------------------------------------------------------- */
    isEquivalentUnderTransposition { |expr|
    	if (expr.isKindOf(this.species).not) { ^false };
    	if (this.size != expr.size) { ^false };
    	^((this.pitchNumbers - expr.pitchNumbers).asSet.size == 1);
    }
    /* --------------------------------------------------------------------------------------------------------
    def register(self, pitch_classes):
        '''Registers `pitch_classes` by pitch set.

        
• Example ---

            ::

                >>> pitch_set = pitchtools.PitchSet(
                ...     items=[10, 19, 20, 23, 24, 26, 27, 29, 30, 33, 37, 40],
                ...     item_class=pitchtools.NumberedPitch,
                ...     )
                >>> pitch_classes = [10, 0, 2, 6, 8, 7, 5, 3, 1, 9, 4, 11]
                >>> pitches = pitch_set.register(pitch_classes)
                >>> for pitch in pitches:
                ...     pitch
                NumberedPitch(10)
                NumberedPitch(24)
                NumberedPitch(26)
                NumberedPitch(30)
                NumberedPitch(20)
                NumberedPitch(19)
                NumberedPitch(29)
                NumberedPitch(27)
                NumberedPitch(37)
                NumberedPitch(33)
                NumberedPitch(40)
                NumberedPitch(23)

        Returns list of zero or more numbered pitches.
        '''
        if isinstance(pitch_classes, list):
            result = [
                [_ for _ in self if _.pitch_number % 12 == pc]
                for pc in [x % 12 for x in pitch_classes]
                ]
            result = sequencetools.flatten_sequence(result)
        elif isinstance(pitch_classes, int):
            result = [p for p in pitch_classes if p % 12 == pitch_classes][0]
        else:
            message = 'must be pitch-class or list of pitch-classes.'
            raise TypeError(message)
        return result
	-------------------------------------------------------------------------------------------------------- */
    register {

    }
    /* --------------------------------------------------------------------------------------------------------
    • transpose
    a = FoscPitchSet([61, 62, 63]);
	a.transpose(3).pitchNumbers;

    def transpose(self, expr):
        r'''Transposes all pitches in pitch set by `expr`.

        Returns new pitch set.
        '''
        items = (pitch.transpose(expr) for pitch in self)
        return new(self, items=items)
	-------------------------------------------------------------------------------------------------------- */
	transpose { |semitones|
    	^this.species.new(this.items.collect { |each| each.transpose(semitones) });
    }

	// DISPLAY
	/* --------------------------------------------------------------------------------------------------------
    • illustrate
	-------------------------------------------------------------------------------------------------------- */
	illustrate {
        ^FoscLilypondFile.pitch([this.items]);
	}
	/* --------------------------------------------------------------------------------------------------------
    • play
	-------------------------------------------------------------------------------------------------------- */
	play {
	}
	/* --------------------------------------------------------------------------------------------------------
    • inspect
	-------------------------------------------------------------------------------------------------------- */
	inspect {
	}
	
	// PRIVATE ////////////////////////////////////////////////////////////////////////////////////////////////
}
