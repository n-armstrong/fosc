/* ------------------------------------------------------------------------------------------------------------
• FoscPitchSet

x = FoscPitchSet(#[60,61,62]);
x.cs;
x.size;
x.pitchNumbers;
x.pitchNames;

• NB: abjad.PitchSet subclasses from Set not TypedSet
------------------------------------------------------------------------------------------------------------ */
FoscPitchSet : FoscTypedSet {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	*new { |items|
        // • TODO: some of the below is redundant -- it's handled in the superclasses
		if ([FoscPitchSet, FoscPitchSegment].any { |type| items.isKindOf(type) }) {
            items = items.items;
        } {
           if (items.isSequenceableCollection.not) { items = [items] };
           items = items.collect { |each| FoscPitch(each) };
           items = items.sort { |a, b| a.pitchNumber < b.pitchNumber };
        };
        // • TODO: assert(all items isKindOf(FoscPitch))
        ^super.new(items, FoscPitch);
	}
	/* --------------------------------------------------------------------------------------------------------
	• newFromSelection

	def from_selection(
        class_,
        selection,
        item_class=None,
        ):
        Makes pitch set from 'selection'.

        ::

            >>> staff_1 = Staff("c'4 <d' fs' a'>4 b2")
            >>> staff_2 = Staff("c4. r8 g2")
            >>> selection = select((staff_1, staff_2))
            >>> pitchtools.PitchSet.from_selection(selection)
            PitchSet(['c', 'g', 'b', "c'", "d'", "fs'", "a'"])

        Returns pitch set.
        
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
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
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
    • difference

    Set-theoretic difference of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchSet(#[61,62,63]);
    b = FoscPitchSet(#[62,63,64]);
    a.difference(b).do { |each| each.pitchNumber.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • sect

    Set-theoretic intersection of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchSet(#[61,62,63]);
    b = FoscPitchSet(#[62,63,64]);
    a.sect(b).do { |each| each.pitchNumber.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isDisjoint

    Is true when typed receiver shares no elements with expr. Otherwise false.

    Returns boolean.

    a = FoscPitchSet(#[60,61]);
    b = FoscPitchSet(#[61,62,63]);
    c = FoscPitchSet(#[64,65]);

    a.isDisjoint(a);
    a.isDisjoint(b);
    a.isDisjoint(c);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isEmpty

    Is true when pitch class set is empty.

    Returns boolean.

    a = FoscPitchSet(#[61,62,63]);
    a.isEmpty;

    a = FoscPitchSet([]);
    a.isEmpty;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isSubsetOf

    Is true when receiver is a subset of expr. Otherwise false.

    Returns boolean.

    a = FoscPitchSet(#[61,62,63]);
    b = FoscPitchSet(#[62,63]);
    
    a.isSubsetOf(a);
    a.isSubsetOf(b);
    b.isSubsetOf(a);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isSupersetOf

    Is true when receiver is a superset of expr. Otherwise false.

    Returns boolean.

    a = FoscPitchSet(#[61,62,63]);
    b = FoscPitchSet(#[62,63]);
    
    a.isSupersetOf(a);
    a.isSupersetOf(b);
    b.isSupersetOf(a);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • notEmpty

    Is true when set is not empty.

    Returns boolean.

    a = FoscPitchSet(#[61,62,63]);
    a.notEmpty;

    a = FoscPitchSet([]);
    a.notEmpty;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • symmetricDifference

    Symmetric difference of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchSet(#[61,62,63]);
    b = FoscPitchSet(#[62,63,64]);
    a.symmetricDifference(b).cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • union

    Union of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchSet(#[61,62,63]);
    b = FoscPitchSet(#[62,63,64]);
    a.union(b).cs;
    -------------------------------------------------------------------------------------------------------- */
	/* --------------------------------------------------------------------------------------------------------
	• invert
	
    a = FoscPitchSet(#[61,62,63]);
	b = a.invert(60);
    b.cs;
    -------------------------------------------------------------------------------------------------------- */
    invert { |axis|
    	^this.species.new(this.items.collect { |each| each.invert(axis) });
    }
    /* --------------------------------------------------------------------------------------------------------
    • isEquivalentUnderTransposition

    True if receiver is equivalent to 'pitchSet' under transposition.
    
    Otherwise false.
    
    a = FoscPitchSet(#[60,61,62]);
    b = FoscPitchSet(#[61,62,63]);
    c = FoscPitchSet(#[60,61,63]);

    a.isEquivalentUnderTransposition(a);
    a.isEquivalentUnderTransposition(b);
    a.isEquivalentUnderTransposition(c);
	-------------------------------------------------------------------------------------------------------- */
    isEquivalentUnderTransposition { |pitchSet|
    	if (pitchSet.isKindOf(this.species).not) { ^false };
    	if (this.size != pitchSet.size) { ^false };
    	^((this.pitchNumbers - pitchSet.pitchNumbers).asSet.size == 1);
    }
    /* --------------------------------------------------------------------------------------------------------
    • register
    
    Registers 'pitchClasses' by pitch set.

    Returns list of zero or more numbered pitches.
	-------------------------------------------------------------------------------------------------------- */
    register { |pitchClasses|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • transpose

    Transposes all pitches in pitch set by 'semitones'.

    Returns new pitch set.


    a = FoscPitchSet(#[61,62,63]);
	b = a.transpose(3);
    b.cs;

    def transpose(self, expr):
        Transposes all pitches in pitch set by 'expr'.

        Returns new pitch set.
        
        items = (pitch.transpose(expr) for pitch in self)
        return new(self, items=items)
	-------------------------------------------------------------------------------------------------------- */
	transpose { |semitones|
    	^this.species.new(this.items.collect { |each| each.transpose(semitones) });
    }
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
        ^this.notYetImplemented(thisMethod);
	}
}
