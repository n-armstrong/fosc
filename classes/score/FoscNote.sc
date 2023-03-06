/* ------------------------------------------------------------------------------------------------------------
• FoscNote

FoscNote().pitch.str;
FoscNote().writtenDuration.str;
FoscNote().str;
FoscNote("g'", 1/4).str;
FoscNote("Gb5", 1/4).str;
FoscNote(60, 1/4).str;

a = FoscNote(60, 1/4);
a.show;


• implicit conversion of type when another leaf is passed as initialization argument; indicators are preserved

m = FoscNote(60, 1/4);
m.attach(FoscArticulation('>'));
m.attach(FoscDynamic('p'));
a = FoscNote(m);
a.show;


• implicit conversion of type when another leaf is passed as initialization argument; indicators are preserved

m = FoscChord(#[60,64,67], 1/4);
m.attach(FoscArticulation('>'));
m.attach(FoscDynamic('p'));
a = FoscNote(m);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscNote : FoscLeaf {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <noteHead;
	*new { |writtenPitch, writtenDuration, multiplier, tag|
        var leaf, isCautionary=false, isForced=false, isParenthesized=false;
        if (writtenPitch.isKindOf(FoscLeaf)) {
            leaf = writtenPitch;

            case 
            { leaf.isKindOf(FoscChord) } {
                writtenDuration = leaf.writtenDuration;
                multiplier = leaf.multiplier;
                writtenPitch = leaf.writtenPitches[0];
                isCautionary = leaf.noteHeads[0].isCautionary ? false;
                isForced = leaf.noteHeads[0].isForced ? false;
                isParenthesized = leaf.noteHeads[0].isParenthesized ? false;
                
                ^super.new(writtenDuration, multiplier, tag).prCopyOverrideAndSetFromLeaf(leaf)
                    .initFoscNote(writtenPitch, isCautionary, isForced, isParenthesized);
            }
            { leaf.isKindOf(FoscNote) } {
                writtenDuration = leaf.writtenDuration;
                multiplier = leaf.multiplier;
                writtenPitch = leaf.writtenPitch;
                isCautionary = leaf.noteHead.isCautionary ? false;
                isForced = leaf.noteHead.isForced ? false;
                isParenthesized = leaf.noteHead.isParenthesized ? false;
                
                ^super.new(writtenDuration, multiplier, tag).prCopyOverrideAndSetFromLeaf(leaf)
                    .initFoscNote(writtenPitch, isCautionary, isForced, isParenthesized);
            };
        };

        writtenPitch = writtenPitch ? 60;
        writtenDuration = writtenDuration ?? { FoscDuration(1, 4) };
		
        ^super.new(writtenDuration, multiplier, tag)
            .initFoscNote(writtenPitch, isCautionary, isForced, isParenthesized);
	}
	initFoscNote { |writtenPitch, isCautionary, isForced, isParenthesized|
        //!!!TODO: noteHead = if (writtenPitch.notNil) { FoscNoteHead(writtenPitch, this) } { FoscDrumNoteHead('snare', this) };
        noteHead = FoscNoteHead(writtenPitch, this, isCautionary, isForced, isParenthesized);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS: SPECIAL
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • asCompileString

    FoscNote(60, 1/4).cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        var pitch, duration;
        pitch = this.writtenPitch.name;
        duration = this.writtenDuration.str;
        ^"FoscNote(\"%\", %)".format(pitch, duration);
    }
    /* --------------------------------------------------------------------------------------------------------
	• storeArgs

    Gets new arguments.

    Returns array.
	
    FoscNote(60, 1/4).storeArgs;
    -------------------------------------------------------------------------------------------------------- */
	storeArgs {
        ^[this.writtenPitch, this.writtenDuration];
    }
    /* --------------------------------------------------------------------------------------------------------
	• str

	FoscNote(60, 1/4).str;
	-------------------------------------------------------------------------------------------------------- */
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE INSTANCE METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetBody
    
    a = FoscNote(60, 1/4);
    a.prGetBody;

    a.noteHead.isForced = true;
    a.noteHead.isCautionary = true;
    a.prGetBody;
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prGetBody {
        var formattedDuration, string;
        formattedDuration = this.prGetFormattedDuration;
        if (noteHead.notNil) {
            string = noteHead.prGetLilypondFormat(formattedDuration: formattedDuration);
        } {
            string = formattedDuration;
        };
        ^[string];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetCompactRepresentation
    
    a = FoscNote(60, 1/4);
    a.prGetCompactRepresentation;
    -------------------------------------------------------------------------------------------------------- */
    prGetCompactRepresentation {
        ^this.prGetBody[0];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetCompactRepresentationWithTie

    a = FoscStaff(FoscLeafMaker().(60 ! 4, [1/4]));
    a[0..].tie;
    a.leafAt(0).prGetCompactRepresentationWithTie;
    -------------------------------------------------------------------------------------------------------- */
    prGetCompactRepresentationWithTie {
        var logicalTie;
        logicalTie = this.prGetLogicalTie;
        if (logicalTie.size > 1 && { this != logicalTie.last }) {
            ^"% ~".format(this.prGetBody[0]);
        } {
            ^this.prGetBody[0];
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDivide
    
    a = FoscNote(60, FoscDuration(1, 4));
    b = a.prDivide;
    b[0].show;
    b[1].show;

    a = FoscNote(60, FoscDuration(1, 4));
    b = a.prDivide(62);
    b;
    -------------------------------------------------------------------------------------------------------- */
    // !!!TODO: DEPRECATED ??
    // prDivide { |pitch|
    //     //!!! INCOMPLETE
    //     var treble, bass;
    //     pitch = pitch ?? { FoscPitch(59) };
    //     pitch = FoscPitch(pitch);
    //     treble = this.copy;
    //     bass = this.copy;
    //     // detach(markuptools.Markup, treble)
    //     // detach(markuptools.Markup, bass)
    //     if (treble.writtenPitch < pitch) { treble = FoscRest(treble) };
    //     if (pitch <= bass.writtenPitch) { bass = FoscRest(bass) };
    //     // up_markup = self._get_markup(direction=Up)
    //     // up_markup = [copy.copy(markup) for markup in up_markup]
    //     // down_markup = self._get_markup(direction=Down)
    //     // down_markup = [copy.copy(markup) for markup in down_markup]
    //     // for markup in up_markup:
    //     //     markup(treble)
    //     // for markup in down_markup:
    //     //     markup(bass)
    //     ^[treble, bass];
    // }
    /* --------------------------------------------------------------------------------------------------------
    • prGetSoundingPitch
    -------------------------------------------------------------------------------------------------------- */
    prGetSoundingPitch {
        ^this.notYetImplemented(thisMethod);
        // if 'sounding pitch' in inspect(self).indicators(str):
        //     return self.written_pitch
        // else:
        //     instrument = self._get_effective(instruments.Instrument)
        //     if instrument:
        //         sounding_pitch = instrument.middle_c_sounding_pitch
        //     else:
        //         sounding_pitch = NamedPitch('C4')
        //     interval = NamedPitch('C4') - sounding_pitch
        //     sounding_pitch = interval.transpose(self.written_pitch)
        //     return sounding_pitch
    }
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE PROPERTIES
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• isPitched
	-------------------------------------------------------------------------------------------------------- */
	isPitched {
		^true;
	}
	/* --------------------------------------------------------------------------------------------------------
	• noteHead

    Gets note-head of note.

    Returns note-head.

    
    • Example 1

    a = FoscNote(60, 1/4);
    a.noteHead.str;
	-------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • noteHead_

    Sets note-head of note.

    
    • Example 2

    a = FoscNote(60, 1/4);
    a.noteHead_(61);
    a.noteHead.str;
    -------------------------------------------------------------------------------------------------------- */
    noteHead_ { |object|
        case
        { object.isNil } { noteHead = nil }
        { object.isKindOf(FoscNoteHead) } { noteHead = object }
        { this.noteHead_(FoscNoteHead(object, this)) };
    }
    /* --------------------------------------------------------------------------------------------------------
    • writtenDuration

    Gets and sets written duration of note.

    Returns duration


    • Example 1

    a = FoscNote(60, 1/4);
    a.writtenDuration.str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • writtenDuration_

    Sets written duration of note.


    • Example 1

    a = FoscNote(60, 1/4);
    a.writtenDuration.str;
    
    a.writtenDuration_(3/4);
    a.writtenDuration.str;

    a.writtenDuration_(5/4); // throws non-assignable duration error
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • writtenPitch


    • Example 1

    a = FoscNote(60, 1/4);
    a.writtenPitch.str;
    -------------------------------------------------------------------------------------------------------- */
    writtenPitch {
        if (noteHead.notNil) {
            if (noteHead.writtenPitch.notNil) { ^noteHead.writtenPitch };
        };    
    }
    /* --------------------------------------------------------------------------------------------------------
    • writtenPitch_

    Sets written pitch of note.

    a = FoscNote(60, 1/4);
    a.writtenPitch.str;

    a = FoscNote(60, 1/4);
    a.writtenPitch_("F#4");
    a.writtenPitch.str;
    a.noteHead.writtenPitch.str;
    -------------------------------------------------------------------------------------------------------- */
    writtenPitch_ { |pitch|
        if (pitch.isNil) {
            if (noteHead.notNil) { noteHead.writtenPitch_(nil) };
        } {
            if (noteHead.notNil) {
                this.noteHead_(FoscNoteHead(FoscPitch(pitch), this));
            } {
                noteHead.writtenPitch_(FoscPitch(pitch));
            };
        };
    }
}
