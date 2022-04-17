/* ------------------------------------------------------------------------------------------------------------
• FoscPitchSet

!!!TODO: add utility and transformation methods from Abjad

x = FoscPitchSet(#[60,61,62]);
x.cs;
x.size;
x.midinotes;
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
           items = items.sort { |a, b| a.midinote < b.midinote };
        };
        // • TODO: assert(all items isKindOf(FoscPitch))
        ^super.new(items, FoscPitch);
	}
	/* --------------------------------------------------------------------------------------------------------
	• newFromSelection

    Makes pitch set from 'selection'.

    Returns pitch set.
    

    • Example 1

    a = FoscLeafMaker().((60..67).mirror, [1/4]);
    b = FoscPitchSet.newFromSelection(a);
    b.show;
    -------------------------------------------------------------------------------------------------------- */
	*newFromSelection { |selection|
		var segment;    
        segment = FoscPitchSegment.newFromSelection(selection);
        ^this.new(segment);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• midinotes
	-------------------------------------------------------------------------------------------------------- */
	midinotes {
		^this.items.collect { |each| each.midinote };
	}
    /* --------------------------------------------------------------------------------------------------------
    • pitches
    -------------------------------------------------------------------------------------------------------- */
    pitches {
        ^this.items;
    }
	/* --------------------------------------------------------------------------------------------------------
	• pitchNames
	-------------------------------------------------------------------------------------------------------- */
	pitchNames {
		^this.items.collect { |each| each.name };
	}
    /* --------------------------------------------------------------------------------------------------------
    • difference

    Set-theoretic difference of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchSet(#[61,62,63]);
    b = FoscPitchSet(#[62,63,64]);
    a.difference(b).do { |each| each.midinote.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • sect

    Set-theoretic intersection of receiver and expr.

    Returns new pitch class set.

    a = FoscPitchSet(#[61,62,63]);
    b = FoscPitchSet(#[62,63,64]);
    a.sect(b).do { |each| each.midinote.postln };
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
    	^((this.midinotes - pitchSet.midinotes).asSet.size == 1);
    }
    /* --------------------------------------------------------------------------------------------------------
    • register
    
    Registers 'pitchClasses' by pitch set.

    Returns list of zero or more numbered pitches.
	-------------------------------------------------------------------------------------------------------- */
    // register { |pitchClasses|
    //     ^this.notYetImplemented(thisMethod);
    // }
    /* --------------------------------------------------------------------------------------------------------
    • transpose

    Transposes all pitches in pitch set by 'semitones'.

    Returns new pitch set.


    a = FoscPitchSet(#[61,62,63]);
	b = a.transpose(3);
    b.cs;
	-------------------------------------------------------------------------------------------------------- */
	transpose { |semitones|
    	^this.species.new(this.items.collect { |each| each.transpose(semitones) });
    }
	/* --------------------------------------------------------------------------------------------------------
    • illustrate

    a = FoscPitchSet(#[61,62,63]);
    a.show;
	-------------------------------------------------------------------------------------------------------- */
	illustrate {
        ^FoscPitchSegment(this.pitches).illustrate;
	}
}
